package com.employee_management.configs;

import com.employee_management.repository.UserRepository;
import com.employee_management.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigurationTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationConfiguration applicationConfiguration;

    @BeforeEach
    void setUp() {
        applicationConfiguration = new ApplicationConfiguration(userRepository);
    }

    @Test
    void userDetailsService_ShouldReturnUserDetails_WhenUserExists() {
        // Arrange
        String email = "test@example.com";
        User mockUser = new User();
        mockUser.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        // Act
        UserDetailsService userDetailsService = applicationConfiguration.userDetailsService();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
    }

    @Test
    void userDetailsService_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        UserDetailsService userDetailsService = applicationConfiguration.userDetailsService();

        // Assert
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));
    }
}
