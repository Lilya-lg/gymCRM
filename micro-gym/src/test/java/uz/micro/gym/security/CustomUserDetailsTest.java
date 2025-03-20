package uz.micro.gym.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.micro.gym.domain.User;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    private CustomUserDetails customUserDetails;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");

        customUserDetails = new CustomUserDetails(user);
    }

    @Test
    void testGetUsername() {
        assertEquals("testuser", customUserDetails.getUsername());
    }

    @Test
    void testGetPassword() {
        assertEquals("testpassword", customUserDetails.getPassword());
    }

    @Test
    void testGetAuthorities() {
        assertNull(customUserDetails.getAuthorities(), "Authorities should be null");
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(customUserDetails.isAccountNonExpired(), "Account should not be expired");
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(customUserDetails.isAccountNonLocked(), "Account should not be locked");
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(customUserDetails.isCredentialsNonExpired(), "Credentials should not be expired");
    }

    @Test
    void testIsEnabled() {
        assertTrue(customUserDetails.isEnabled(), "Account should be enabled");
    }
}
