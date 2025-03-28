package uz.gym.training.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import uz.gym.training.security.JwtUtil;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

  private JwtUtil jwtUtil;
  private String secretKeyString;
  private String testToken;

  @BeforeEach
  void setUp() {
    SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    secretKeyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());

    jwtUtil = new JwtUtil(secretKeyString);

    ReflectionTestUtils.setField(jwtUtil, "expirationTime", 10000L); // 10 seconds expiry

    testToken =
        Jwts.builder()
            .setSubject("testuser")
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 10000))
            .signWith(secretKey)
            .compact();
  }

  @Test
  void testExtractUsername() {
    String username = jwtUtil.extractUsername(testToken);
    assertEquals("testuser", username, "Username should be extracted correctly.");
  }

  @Test
  void testIsTokenExpired_NotExpired() {
    assertFalse(jwtUtil.isTokenExpired(testToken), "Token should not be expired yet.");
  }
}
