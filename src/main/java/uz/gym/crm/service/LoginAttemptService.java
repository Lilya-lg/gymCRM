package uz.gym.crm.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class LoginAttemptService {
    private final int MAX_ATTEMPTS = 3;
    private final long BLOCK_TIME_MS = 300000; // 5 minutes
    private ConcurrentMap<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Long> blockedUsers = new ConcurrentHashMap<>();

    public void loginFailed(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0) + 1;
        attemptsCache.put(username, attempts);
        if (attempts >= MAX_ATTEMPTS) {
            blockedUsers.put(username, System.currentTimeMillis());
        }
    }

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        blockedUsers.remove(username);
    }

    public boolean isBlocked(String username) {
        Long blockTime = blockedUsers.get(username);
        if (blockTime != null) {
            if (System.currentTimeMillis() - blockTime < BLOCK_TIME_MS) {
                return true;
            } else {
                blockedUsers.remove(username);
                attemptsCache.remove(username);
            }
        }
        return false;
    }
}