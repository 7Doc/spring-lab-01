package kz.iitu.spring_lab_01.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    public static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 1;

    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lockUntil = new ConcurrentHashMap<>();

    public void loginFailed(String username) {
        if (username == null || isBlocked(username)) {
            return;
        }
        int count = attempts.getOrDefault(username, 0) + 1;
        attempts.put(username, count);

        if (count >= MAX_ATTEMPTS) {
            lockUntil.put(username, LocalDateTime.now().plusMinutes(LOCK_MINUTES));
        }
    }

    public void loginSucceeded(String username) {
        attempts.remove(username);
        lockUntil.remove(username);
    }

    public boolean isBlocked(String username) {
        LocalDateTime time = lockUntil.get(username);
        if (time == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(time)) {
            loginSucceeded(username);
            return false;
        }
        return true;
    }

    public int getRemainingAttempts(String username) {
        return Math.max(0, MAX_ATTEMPTS - attempts.getOrDefault(username, 0));
    }

    public long getSecondsLeft(String username) {
        LocalDateTime time = lockUntil.get(username);
        if (time == null) return 0;
        return Math.max(0, Duration.between(LocalDateTime.now(), time).getSeconds());
    }
}
