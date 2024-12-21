package proxy;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFile {
    private static final String LOG_FILE_PATH = "log/proxy_requests.log";

    public static void logRequest(String clientIp, String request, String statusCode) {
        try (FileWriter fw = new FileWriter(LOG_FILE_PATH, true); BufferedWriter bw = new BufferedWriter(fw)) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            bw.write("[" + timestamp + "] IP: " + clientIp + " Request: " + request + " Status: " + statusCode);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showLogs() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE_PATH));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier de journaux : " + e.getMessage());
        }
    }
}
