package proxy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

public class ConfigReader {
    private String filePath = "";
    private String host = null;
    private int port;
    private int defaultExpiration;
    private Vector<String[]> serveurApache;

    public ConfigReader() throws Exception {
        this.filePath = "conf/config.conf";
        this.serveurApache = new Vector<>();
        try {
            String ip = getIpLocal();
            this.setHost(ip);
            readConf();
        } catch (Exception e) {
            throw e;
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            ConfigReader r = new ConfigReader();
            System.out.println(r);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void readConf() throws Exception {
        FileReader conf = null;
        try {
            conf = new FileReader(this.getFilePath());
            if (conf == null) {
                throw new Exception("Unable to read the configuration file: " + this.getFilePath());
            }
            String portConf = null;
            String expiration = null;

            try (BufferedReader reader = new BufferedReader(conf)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("port=")) {
                        portConf = line.split("=", 2)[1].trim();
                    } else if (line.startsWith("expiration=")) {
                        expiration = line.split("=", 2)[1].trim();
                    } else if (line.startsWith("apacheServer=")) {
                        String temp = line.split("=", 2)[1].trim();
                        String[] listeServeur = temp.split(" , ");
                        for (String string : listeServeur) {
                            String[] server = string.split(":");
                            this.serveurApache.add(server);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception("Error reading the configuration file.", e);
            }

            if (portConf == null) {
                throw new Exception("Port not found");
            }
            if (expiration == null) {
                throw new Exception("Default expiration time not found");
            }
            setPort(Integer.parseInt(portConf));
            setDefaultExpiration(Integer.parseInt(expiration));
        } catch (Exception e) {
            throw new Exception("Error loading the configuration.", e);
        } finally {
            if (conf != null) {
                try {
                    conf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getIpLocal() throws Exception {
        String valiny = null;
        try {
            // Parcourir toutes les interfaces réseau
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                // Vérifier si l'interface est active (up) et si ce n'est pas une interface
                // virtuelle
                if (networkInterface.isUp() && !networkInterface.isLoopback() && !networkInterface.isVirtual()) {
                    String interfaceName = networkInterface.getName();
                    // Vérifier si le nom de l'interface correspond à "wlan0" ou similaire
                    if (interfaceName.contains("wlan")) {
                        // Parcourir les adresses associées à cette interface
                        Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                        while (inetAddresses.hasMoreElements()) {
                            InetAddress inetAddress = inetAddresses.nextElement();
                            // Ignorer les adresses IPv6 et localhost
                            if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().indexOf(":") == -1) {
                                valiny = inetAddress.getHostAddress().toString();
                            }
                        }
                    }
                }
            }
            if (valiny == null) {
                valiny = "127.0.0.1";
            }
        } catch (SocketException e) {
            throw new Exception("Erreur lors de la récupération des interfaces réseau.");
        }
        return valiny;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getDefaultExpiration() {
        return defaultExpiration;
    }

    public void setDefaultExpiration(int defaultExpiration) {
        this.defaultExpiration = defaultExpiration;
    }

    public Vector<String[]> getServeurApache() {
        return serveurApache;
    }

    public void setServeurApache(Vector<String[]> serveurApache) {
        this.serveurApache = serveurApache;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ConfigReader {");
        sb.append("\n  filePath='").append(filePath).append('\'');
        sb.append(",\n  host='").append(host).append('\'');
        sb.append(",\n  port=").append(port);
        sb.append(",\n  defaultExpiration=").append(defaultExpiration);
        sb.append(",\n  serveurApache=[");

        for (String[] server : serveurApache) {
            sb.append("\n    [");
            sb.append(String.join(", ", server)); 
            sb.append("],");
        }

        if (!serveurApache.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }

        sb.append("\n  ]");
        sb.append("\n}");
        return sb.toString();
    }

}
