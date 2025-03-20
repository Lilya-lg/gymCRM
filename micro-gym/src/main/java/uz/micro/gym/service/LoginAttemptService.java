package uz.micro.gym.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class LoginAttemptService {
    private static final int MAX_ATTEMPTS = 3;
    private static final long BLOCK_TIME_MS = 300000; // 5 minutes
    private final ConcurrentMap<String, LoginAttemptInfo> attemptsInfo = new ConcurrentHashMap<>();

    public void loginFailed(String username) {
        attemptsInfo.compute(username, (key, attemptInfo) -> {
            if (attemptInfo == null) {
                return new LoginAttemptInfo(1, null);
            }
            int newAttempts = attemptInfo.attempts + 1;
            Long blockTime = newAttempts >= MAX_ATTEMPTS ? System.currentTimeMillis() : null;
            return new LoginAttemptInfo(newAttempts, blockTime);
        });
    }

    public void loginSucceeded(String username) {
        attemptsInfo.remove(username);
    }

    public boolean isBlocked(String username) {
        LoginAttemptInfo attemptInfo = attemptsInfo.get(username);
        if (attemptInfo != null && attemptInfo.blockTime != null) {
            if (System.currentTimeMillis() - attemptInfo.blockTime < BLOCK_TIME_MS) {
                return true;
            } else {
                attemptsInfo.remove(username);
            }
        }
        return false;
    }

    private static class LoginAttemptInfo {
        private final int attempts;
        private final Long blockTime;

        public LoginAttemptInfo(int attempts, Long blockTime) {
            this.attempts = attempts;
            this.blockTime = blockTime;
        }
    }
}