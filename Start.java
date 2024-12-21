package proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import style.Style;

public class Start {
    private static ProxyCache proxy;

    public static void main(String[] args) {
        proxy = null;
        try {
            proxy = new ProxyCache();
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(proxy.getPort(), 50, InetAddress.getByName(proxy.getHost()));
                try {

                    Thread commandThread = new Thread(() -> {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                            while (true) {

                                Timer timer = new Timer();
                                timer.scheduleAtFixedRate(new TimerTask() {
                                    @Override
                                    public void run() {
                                        proxy.cleanExpiredEntries();
                                    }
                                }, 0, proxy.conf.getDefaultExpiration() * 60 * 1000);

                                displayTerminal();
                                String command = reader.readLine();
                                command = command.toLowerCase();
                                try {
                                    handleCommand(command);
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    commandThread.start();

                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        new Thread(new ProxyHandler(clientSocket)).start();
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void handleCommand(String command) throws Exception {
        if (command.equals("stop") || command.equals("bye") || command.equals("exit")) {
            System.out.println(Style.getGras()[0] + "Goodbye!!" + Style.getGras()[1]);
            System.exit(0);
        } else if (command.equals("edit conf")) {
            String fileName = null;
            try {
                fileName = ProxyCache.conf.getFilePath();
                openNano(fileName);
            } catch (Exception e) {
                throw e;
            }
        } else if (command.equals("myconfig")) {
            proxy.displayMyConfig();
        } else if (command.equals("ls")) {
            proxy.displayCache();
        } else if (command.startsWith("rm") || command.startsWith("del") || command.startsWith("clear")) {
            String[] temp = command.split(" ");
            if (temp.length == 2) {
                if (temp[1].equals("all")) {
                    proxy.viderCache();
                } else {
                    try {
                        int t = Integer.parseInt(temp[1]);
                        proxy.effacerCache(t);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format for cache index: " + temp[1]);
                    }
                }
            } else {
                System.out.println(command + ": invalid arguments or command not found");
            }
        } else if (command.startsWith("allow ip")) {
            String[] parts = command.split(" ");
            if (parts.length == 3) {
                String ip = parts[2];
                proxy.allowIp(ip);
            } else {
                System.out.println("Usage: allow ip <IP_ADDRESS>");
            }
        } else if (command.startsWith("deny ip")) {
            String[] parts = command.split(" ");
            if (parts.length == 3) {
                String ip = parts[2];
                proxy.addDeniedIp(ip);
            } else {
                System.out.println("Usage: deny ip <IP_ADDRESS>");
            }
        } else if (command.equals("show denied ip")) {
            proxy.showDeniedIPs();
        } else if (command.equals("show log")) {
            LogFile.showLogs();
        } else {
            System.out.println(command + ": command not found");
        }
        System.out.println();
    }

    public static void displayTerminal() {
        System.out
                .print(Style.getGreen() + Style.getGras()[0] + "[myProxy]:> " + Style.getGras()[1] + Style.getReset());
    }

    public static void openNano(String fileName) {
        try {
            ProcessBuilder pb = new ProcessBuilder("nano", fileName);
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error opening nano: " + e.getMessage());
        }
    }

}
