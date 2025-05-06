package uz.gym.training.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
@Component
public class TestJwtTokenProvider {
    @Value("${jwt.secret}")
    private String base64Secret;
    private Key secretKey;
    // 256 bits = minimum 32 characters
    @PostConstruct
    public void init() {
        this.secretKey = new SecretKeySpec(Base64.getDecoder().decode(base64Secret), "HmacSHA256");
    }
    public  String generateTestToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}
