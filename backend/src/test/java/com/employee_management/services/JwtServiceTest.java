package com.employee_management.services;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;
    private final String testSecretKey = "MySuperSecretKeyForJWTMySuperSecretKeyForJWT";  // At least 32 bytes
    private final long testExpirationTime = 1000 * 60 * 60 * 10; // 10 hours

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Set private fields using reflection
        setPrivateField(jwtService, "secretKey", testSecretKey);
        setPrivateField(jwtService, "jwtExpiration", testExpirationTime);

        userDetails = User.withUsername("testUser")
                .password("password")
                .roles("USER")
                .build();
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void generateToken_ShouldGenerateValidToken() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String token = jwtService.generateToken(userDetails);
        assertEquals(userDetails.getUsername(), jwtService.extractUsername(token));
    }

    @Test
    void isTokenValid_ShouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails)); // Should return true for a valid token
    }

    @Test
    void isTokenValid_ShouldReturnFalseForExpiredToken() throws Exception {
        setPrivateField(jwtService, "jwtExpiration", 1L);  // 1ms expiration
        String token = jwtService.generateToken(userDetails);

        Thread.sleep(5);  // Wait for expiration
        assertFalse(jwtService.isTokenValid(token, userDetails)); // Token should be expired
    }

    @Test
    void isTokenValid_ShouldReturnFalseForInvalidUsername() {
        String token = jwtService.generateToken(userDetails);
        UserDetails anotherUser = new User("anotherUser", "password", new ArrayList<>());

        assertFalse(jwtService.isTokenValid(token, anotherUser)); // Should return false due to username mismatch
    }

    @Test
    void getExpirationTime_ShouldReturnConfiguredExpiration() {
        assertEquals(testExpirationTime, jwtService.getExpirationTime());
    }
}
