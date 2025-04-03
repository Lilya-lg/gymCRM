package uz.micro.gym.config;


import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
/*
public class FeignConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeignConfig.class);

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String transactionId = MDC.get("transactionId");
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String token = request.getHeader("Authorization");

                if (token != null && token.startsWith("Bearer ")) {
                    requestTemplate.header("Authorization", token);
                } else {
                    LOGGER.warn("No JWT Token Found in Feign Client");
                }
            }
            if (transactionId != null) {
                requestTemplate.header("transactionId", transactionId);
            }
        };
    }

    private String extractToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            LOGGER.warn("SecurityContext is NULL in Feign Client");
            return null;
        }

        LOGGER.debug("SecurityContext Authentication: {}", authentication);

        if (authentication.getCredentials() instanceof String token) {
            LOGGER.debug("Extracted JWT Token: {}", token);
            return token;
        }

        LOGGER.warn("Could not extract JWT Token from Authentication");
        return null;
    }

    private String extractTokenFromRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            LOGGER.warn("No Request Context Available");
            return null;
        }

        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            LOGGER.debug("Extracted JWT Token from Request: {}", authHeader.substring(7));
            return authHeader.substring(7);
        } else {
            LOGGER.warn("Authorization Header Not Found or Invalid in HTTP Request");
            return null;
        }
    }
}


 */