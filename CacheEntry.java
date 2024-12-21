package proxy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CacheEntry {
    private final String value;
    private final int expirationTime; 
    private final long addedTime; 

    public CacheEntry(String value, int expirationTime) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty.");
        }
        if (expirationTime <= 0) {
            throw new IllegalArgumentException("Expiration time must be positive.");
        }
        this.value = value;
        this.expirationTime = expirationTime;
        this.addedTime = System.currentTimeMillis(); 
    }

    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        long elapsedMillis = currentTime - addedTime;
        boolean expired = elapsedMillis >= expirationTime * 60 * 1000;
        return expired;
    }

    public long getRemainingTime() {
        long currentTime = System.currentTimeMillis();
        long remainingMillis = (expirationTime * 60 * 1000) - (currentTime - addedTime);
        return Math.max(remainingMillis, 0); // Retourne 0 si déjà expiré
    }

    public String getValue() {
        return value;
    }

    public int getExpirationTime() {
        return expirationTime;
    }

    public long getAddedTime() {
        return addedTime;
    }

}
