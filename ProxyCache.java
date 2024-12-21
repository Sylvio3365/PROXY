package proxy;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Date;

public class ProxyCache {
    private int port;
    private String host;
    public static ConfigReader conf;
    public static ConcurrentHashMap<String, CacheEntry> cache;
    public static Vector<String> clientDenied;
    private static final ScheduledExecutorService cleaner = Executors.newScheduledThreadPool(1);

    public ProxyCache() throws Exception {
        try {
            conf = new ConfigReader();
            this.port = conf.getPort();
            this.host = conf.getHost();
            cache = new ConcurrentHashMap<>();
            clientDenied = new Vector<String>();
        } catch (Exception e) {
            throw new Exception("Unable to read the configuration file.", e);
        }
    }

    public void addDeniedIp(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            throw new IllegalArgumentException("The IP address cannot be null or empty.");
        }
        String ipPattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        if (!ip.matches(ipPattern)) {
            throw new IllegalArgumentException("Invalid IP address format: " + ip);
        }
        if (!clientDenied.contains(ip)) {
            clientDenied.add(ip);
            System.out.println("The IP address " + ip + " has been added to the denied clients list.");
        } else {
            System.out.println("The IP address " + ip + " is already in the denied clients list.");
        }
    }

    public void showDeniedIPs() {
        if (ProxyCache.clientDenied.isEmpty()) {
            System.out.println("No denied IPs.");
        } else {
            System.out.println("Denied IPs:");
            for (String ip : ProxyCache.clientDenied) {
                System.out.println("-" + ip);
            }
        }
    }

    public void allowIp(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            throw new IllegalArgumentException("The IP address cannot be null or empty.");
        }

        if (clientDenied.contains(ip)) {
            clientDenied.remove(ip);
            System.out.println("The IP address " + ip + " has been removed from the denied clients list.");
        } else {
            System.out.println("The IP address " + ip + " is not in the denied clients list.");
        }
    }

    public void effacerCache(int i) {
        int index = 1;
        for (var entry : cache.entrySet()) {
            if (index == i) {
                String key = entry.getKey();
                String cleanedKey = key.replace("GET ", "").replace(" HTTP/1.1", "");
                cache.remove(key);
                System.out.println(cleanedKey + " has been cleared");
                break;
            }
            index++;
        }
    }

    public void viderCache() {
        cache.clear();
        System.out.println("Cache has been cleared.");
    }

    public void cleanExpiredEntries() {
        cache.entrySet().removeIf(entry -> {
            boolean expired = entry.getValue().isExpired();
            return expired;
        });
    }

    public void ajouter(String key, String value, int expirationSeconds) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty.");
        }
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty.");
        }
        cache.put(key, new CacheEntry(value, expirationSeconds));
        System.out.println("Added entry: " + key + " with expiration: " + expirationSeconds + " seconds.");
    }

    public String getFromCache(String key) {
        CacheEntry entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            System.out.println("Cache hit for key: " + key);
            return entry.getValue();
        }
        if (entry != null && entry.isExpired()) {
            System.out.println("Removing expired cache entry for key: " + key);
            cache.remove(key);
        }
        System.out.println("Cache miss for key: " + key);
        return null;
    }

    public void displayCache() {
        if (cache.isEmpty()) {
            System.out.println("Cache is empty.");
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Indian/Antananarivo"));
            int index = 1;
            for (var entry : cache.entrySet()) {
                CacheEntry cacheEntry = entry.getValue();
                String cleanedKey = entry.getKey().replace(" HTTP/1.1", "");
                String formattedDate = dateFormat.format(new Date(cacheEntry.getAddedTime()));
                System.out.println(index + "/ " + cleanedKey + " (Added Time: " + formattedDate + ")");
                System.out.println("----------------------------------------------------");
                index++;
            }
        }
    }

    public void displayMyConfig() {
        System.out.println("Server IP: " + this.getHost());
        System.out.println("Server Port: " + this.getPort());
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public ConfigReader getConf() {
        return conf;
    }

    public static void setConf(ConfigReader conf) {
        ProxyCache.conf = conf;
    }

    public static ConcurrentHashMap<String, CacheEntry> getCache() {
        return cache;
    }

    public static void setCache(ConcurrentHashMap<String, CacheEntry> cache) {
        ProxyCache.cache = cache;
    }

    public static ScheduledExecutorService getCleaner() {
        return cleaner;
    }

}
