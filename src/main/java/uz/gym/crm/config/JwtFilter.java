package uz.gym.crm.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.gym.crm.service.BlackListService;
import uz.gym.crm.util.JwtUtil;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private BlackListService blackListService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (isExcluded(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            if (blackListService.isBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token is invalidated");
                return;
            }
            try {
                // Validate the token to ensure it is valid and not expired
                JwtUtil.validateToken(token);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(null, null, null);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                return;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isExcluded(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        if ("POST".equalsIgnoreCase(requestMethod)) {
            return pathMatcher.match("/api/trainees", requestURI) ||
                    pathMatcher.match("/api/trainers", requestURI) ||
                    pathMatcher.match("/api/users/login", requestURI);
        }

        return pathMatcher.match("/api/users/login", requestURI) ||
                pathMatcher.match("/swagger-ui.html", requestURI) ||
                pathMatcher.match("/v2/api-docs", requestURI) ||
                pathMatcher.match("/swagger-ui/index.html", requestURI) ||
                pathMatcher.match("/actuator/**", requestURI);
    }


}
