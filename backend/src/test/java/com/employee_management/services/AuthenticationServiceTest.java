package com.employee_management.services;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.employee_management.dtos.LoginUserDto;
import com.employee_management.dtos.RegisterUserDto;
import com.employee_management.entities.User;
import com.employee_management.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterUserDto validUserDto;
    private LoginUserDto validLoginDto;

    @BeforeEach
    void setUp() {
        validUserDto = new RegisterUserDto();
        validUserDto.setFullName("John Doe");
        validUserDto.setEmail("johndoe@example.com");
        validUserDto.setPassword("password123");

        validLoginDto = new LoginUserDto();
        validLoginDto.setEmail("johndoe@example.com");
        validLoginDto.setPassword("password123");
    }

    @Test
    void signup_SuccessfulRegistration() {
        User mockUser = new User();
        mockUser.setFullName(validUserDto.getFullName());
        mockUser.setEmail(validUserDto.getEmail());
        mockUser.setPassword("hashed_password");

        when(passwordEncoder.encode(validUserDto.getPassword())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User registeredUser = authenticationService.signup(validUserDto);

        assertNotNull(registeredUser);
        assertEquals(validUserDto.getFullName(), registeredUser.getFullName());
        assertEquals(validUserDto.getEmail(), registeredUser.getEmail());
        assertEquals("hashed_password", registeredUser.getPassword());
    }

    @Test
    void signup_ThrowsException_WhenNameIsTooShort() {
        validUserDto.setFullName("Jo");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.signup(validUserDto);
        });

        assertEquals("Name must be at least 3 characters long", exception.getMessage());
    }

    @Test
    void signup_ThrowsException_WhenEmailIsInvalid() {
        validUserDto.setEmail("invalid-email");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.signup(validUserDto);
        });

        assertEquals("Invalid email format (e.g., example@gmail.com)", exception.getMessage());
    }

    @Test
    void signup_ThrowsException_WhenNameIsNull() {
        validUserDto.setFullName(null); // Set fullName to null

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.signup(validUserDto);
        });

        assertEquals("Name must be at least 3 characters long", exception.getMessage());
    }

    @Test
    void signup_ThrowsException_WhenEmailIsNull() {
        validUserDto.setEmail(null); // Set email to null

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.signup(validUserDto);
        });

        assertEquals("Invalid email format (e.g., example@gmail.com)", exception.getMessage());
    }

    @Test
    void authenticate_SuccessfulAuthentication() {
        User mockUser = new User();
        mockUser.setEmail(validLoginDto.getEmail());
        mockUser.setPassword("hashed_password");

        when(userRepository.findByEmail(validLoginDto.getEmail())).thenReturn(Optional.of(mockUser));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null); // AuthenticationManager requires a return value

        User authenticatedUser = authenticationService.authenticate(validLoginDto);

        assertNotNull(authenticatedUser);
        assertEquals(validLoginDto.getEmail(), authenticatedUser.getEmail());
    }

    @Test
    void authenticate_ThrowsException_WhenUserNotFound() {
        when(userRepository.findByEmail(validLoginDto.getEmail())).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> authenticationService.authenticate(validLoginDto));
    }
}
