package uz.gym.crm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.gym.crm.service.BlackListService;
import uz.gym.crm.service.TraineeServiceImpl;
import uz.gym.crm.util.JwtUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);
    @Autowired
    private BlackListService blackListService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (isExcluded(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            if (blackListService.isBlacklisted(token)) {
                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token is invalidated");
                return;
            }
            try {
                // Validate the token to ensure it is valid and not expired
                JwtUtil.validateToken(token);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null, null);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (Exception e) {
                LOGGER.error("Token validation failed: {}", e.getMessage(), e);
                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }
        } else {
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isExcluded(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        if ("POST".equalsIgnoreCase(requestMethod)) {
            return pathMatcher.match("/api/trainees", requestURI) || pathMatcher.match("/api/trainers", requestURI) || pathMatcher.match("/api/users/login", requestURI);
        }

        return pathMatcher.match("/api/users/login", requestURI) || pathMatcher.match("/swagger-ui.html", requestURI) || pathMatcher.match("/v2/api-docs", requestURI) || pathMatcher.match("/swagger-ui/index.html", requestURI) || pathMatcher.match("/actuator/**", requestURI);
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", HttpServletResponse.SC_UNAUTHORIZED == status ? "Unauthorized" : "Error");
        errorResponse.put("message", message);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}
