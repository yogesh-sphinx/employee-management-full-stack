package com.employee_management.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.employee_management.dtos.LoginUserDto;
import com.employee_management.dtos.RegisterUserDto;
import com.employee_management.entities.User;
import com.employee_management.repository.UserRepository;

@Service
public class AuthenticationService {
        private final UserRepository userRepository;

        private final PasswordEncoder passwordEncoder;

        private final AuthenticationManager authenticationManager;

        public AuthenticationService(
                        UserRepository userRepository,
                        AuthenticationManager authenticationManager,
                        PasswordEncoder passwordEncoder) {
                this.authenticationManager = authenticationManager;
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
        }

        public User signup(RegisterUserDto registerUserDto) {
                if (registerUserDto.getFullName() == null || registerUserDto.getFullName().trim().length() < 3) {
                        throw new IllegalArgumentException("Name must be at least 3 characters long");
                }

                String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
                if (registerUserDto.getEmail() == null || !registerUserDto.getEmail().matches(emailRegex)) {
                        throw new IllegalArgumentException("Invalid email format (e.g., example@gmail.com)");
                }
                
                User user = new User();
                user.setFullName(registerUserDto.getFullName());
                user.setEmail(registerUserDto.getEmail());
                user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
                return userRepository.save(user);
        }

        public User authenticate(LoginUserDto input) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                input.getEmail(),
                                                input.getPassword()));
                return userRepository.findByEmail(input.getEmail())
                                .orElseThrow();
        }
}