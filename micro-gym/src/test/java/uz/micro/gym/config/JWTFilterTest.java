package uz.micro.gym.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import uz.micro.gym.service.BlackListService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class JWTFilterTest {

  @Autowired private JwtFilter jwtFilter;

  @MockBean private BlackListService blackListService;

  private FilterChain filterChain;

  @BeforeEach
  void setUp() {
    filterChain = mock(FilterChain.class);
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
