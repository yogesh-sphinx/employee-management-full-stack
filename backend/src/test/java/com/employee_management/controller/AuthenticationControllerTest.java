package com.employee_management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.employee_management.dtos.LoginUserDto;
import com.employee_management.dtos.RegisterUserDto;
import com.employee_management.entities.User;
import com.employee_management.responses.LoginResponse;
import com.employee_management.services.AuthenticationService;
import com.employee_management.services.JwtService;

class AuthenticationControllerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ShouldReturnSuccessMessage() {
        // Arrange
        RegisterUserDto registerUserDto = new RegisterUserDto();
        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(any(User.class));

        // Act
        ResponseEntity<String> response = authenticationController.register(registerUserDto);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Account Created successfully", response.getBody());

        verify(authenticationService, times(1)).signup(registerUserDto);
    }

    @Test
    void authenticate_ShouldReturnValidToken() {
        // Arrange
        LoginUserDto loginUserDto = new LoginUserDto();
        User mockUser = new User();
        String mockToken = "mock-jwt-token";
        long mockExpiration = 3600000L; // 1 hour in milliseconds

        when(authenticationService.authenticate(loginUserDto)).thenReturn(mockUser);
        when(jwtService.generateToken(mockUser)).thenReturn(mockToken);
        when(jwtService.getExpirationTime()).thenReturn(mockExpiration);

        // Act
        ResponseEntity<LoginResponse> response = authenticationController.authenticate(loginUserDto);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(mockToken, response.getBody().getToken());
        assertEquals(mockExpiration, response.getBody().getExpiresIn());

        verify(authenticationService, times(1)).authenticate(loginUserDto);
        verify(jwtService, times(1)).generateToken(mockUser);
        verify(jwtService, times(1)).getExpirationTime();
    }
}
