package uz.micro.gym.util;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import uz.micro.gym.service.TrainingServiceImpl;

import java.io.IOException;
import java.util.UUID;
/*
@Component
public class TransactionLoggingFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionLoggingFilter.class);
    private static final String TRANSACTION_ID = "transactionId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Generate a unique transaction ID
        String transactionId = UUID.randomUUID().toString();

        // Store it in MDC (Mapped Diagnostic Context) for logging
        MDC.put(TRANSACTION_ID, transactionId);

        // Log the request
        logRequest(httpRequest, transactionId);

        try {
            chain.doFilter(request, response);
        } finally {
            // Log the response
            logResponse(httpResponse, transactionId);
            MDC.clear();
        }
    }

    private void logRequest(HttpServletRequest request, String transactionId) {
        LOGGER.info("Transaction ID: " + transactionId +
                " | Request: " + request.getMethod() + " " + request.getRequestURI() +
                " | Headers: " + request.getHeaderNames());
    }

    private void logResponse(HttpServletResponse response, String transactionId) {
        LOGGER.info("Transaction ID: " + transactionId +
                " | Response Status: " + response.getStatus());
    }
}


 */