package proxy;

import java.io.*;
import java.net.*;
import java.util.*;

public class ProxyHandler implements Runnable {

    private final Socket clientSocket;
    private static Map<String, CacheEntry> cache = ProxyCache.cache;
    private static ConfigReader conf = ProxyCache.conf;

    public ProxyHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try (BufferedReader clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String clientIp = clientSocket.getInetAddress().getHostAddress();
            if (ProxyCache.clientDenied.contains(clientIp)) {
                String forbiddenResponse = "HTTP/1.1 403 Forbidden\r\n" +
                        "Content-Type: text/html; charset=UTF-8\r\n" +
                        "Connection: close\r\n\r\n" +
                        "<html><body><h1>Access Forbidden</h1><p>Your IP is denied from accessing this proxy server.</p></body></html>";
                clientOut.println(forbiddenResponse);
                LogFile.logRequest(clientIp, "", "403 Forbidden");
                clientSocket.close();
                return;
            }

            String requestLine = clientIn.readLine();
            if (requestLine == null || requestLine.contains("GET /favicon.ico")) {
                return;
            }

            System.out.println("Client request from " + clientSocket.toString() + " : " + requestLine);

            CacheEntry cachedResponse = cache.get(requestLine);

            if (cachedResponse != null && !cachedResponse.isExpired()) {
                System.out.println("Response retrieved from cache.");
                clientOut.println(cachedResponse.getValue());
                LogFile.logRequest(clientIp, requestLine, "200 OK");
            } else {
                String response = getResponse(requestLine);
                if (response != null) {
                    cache.put(requestLine, new CacheEntry(response, conf.getDefaultExpiration()));
                    clientOut.print(response);
                    LogFile.logRequest(clientIp, requestLine, "200 OK");
                } else {
                    String errorResponse = "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Type: text/html; charset=UTF-8\r\n" +
                            "Connection: close\r\n\r\n" +
                            "<html><body><h1>404 Not Found</h1><p>The page you are looking for could not be found.</p></body></html>";
                    System.out.println("No response found");
                    clientOut.print(errorResponse);
                    LogFile.logRequest(clientIp, requestLine, "404 Not Found");
                }
                clientOut.flush();
            }

            System.out.println();
            Start.displayTerminal();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getResponse(String requestLine) {
        Vector<String[]> listServeur = conf.getServeurApache();
        String valiny = null;
        for (String[] strings : listServeur) {
            if (isServerAccessible(strings[0], Integer.parseInt(strings[1]))) {
                valiny = getResponseFromServer(strings[0], Integer.parseInt(strings[1]), requestLine);
                if (valiny != null) {
                    System.out.println("Response found on server " + strings[0] + " on port " + strings[1]);
                    break;
                }
            }
        }
        return valiny;
    }

    private boolean isServerAccessible(String serverIp, int serverPort) {
        try (Socket testSocket = new Socket()) {
            testSocket.connect(new InetSocketAddress(serverIp, serverPort), 3000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private String getResponseFromServer(String serverIp, int serverPort, String requestLine) {
        try (Socket serverSocket = new Socket(serverIp, serverPort);
                BufferedReader serverIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
                PrintWriter serverOut = new PrintWriter(serverSocket.getOutputStream(), true)) {

            StringBuilder requestBuilder = new StringBuilder();
            requestBuilder.append(requestLine).append("\r\n");
            requestBuilder.append("Host: ").append(serverIp).append("\r\n");
            requestBuilder.append("Connection: close\r\n");
            requestBuilder.append("\r\n");

            serverOut.print(requestBuilder.toString());
            serverOut.flush();

            String serverResponse;
            StringBuilder responseBuilder = new StringBuilder();
            boolean is404 = false;

            while ((serverResponse = serverIn.readLine()) != null) {
                responseBuilder.append(serverResponse).append("\n");
                if (serverResponse.contains("404 Not Found")) {
                    is404 = true;
                }
            }

            if (is404) {
                return null;
            }

            return responseBuilder.toString();
        } catch (IOException e) {
            System.err.println("Error connecting to server " + serverIp + ":" + serverPort);
            return null;
        }
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public static Map<String, CacheEntry> getCache() {
        return cache;
    }
}
