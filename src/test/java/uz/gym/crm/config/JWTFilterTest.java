package uz.gym.crm.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import uz.gym.crm.util.JwtUtil;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JWTFilterTest {

    private JwtFilter jwtFilter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtFilter = new JwtFilter();
        filterChain = mock(FilterChain.class);
    }

    @Test
    void testDoFilterInternalWithValidToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer validToken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.validateToken("validToken")).thenAnswer(invocation -> null);

            jwtFilter.doFilterInternal(request, response, filterChain);

            assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            verify(filterChain, times(1)).doFilter(request, response);
        }
    }

    @Test
    void testDoFilterInternalWithInvalidToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalidToken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        try (MockedStatic<JwtUtil> mockedJwtUtil = mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.validateToken("invalidToken")).thenThrow(new RuntimeException("Invalid token"));

            jwtFilter.doFilterInternal(request, response, filterChain);

            assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
            assertEquals("Invalid or expired token", response.getContentAsString());
            verify(filterChain, never()).doFilter(request, response);
        }
    }

    @Test
    void testDoFilterInternalWithMissingToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("Missing or invalid Authorization header", response.getContentAsString());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalExcludedPaths() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/api/users/login");
        request.setMethod("GET");

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalExcludedPostPaths() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/api/trainees");
        request.setMethod("POST");

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
