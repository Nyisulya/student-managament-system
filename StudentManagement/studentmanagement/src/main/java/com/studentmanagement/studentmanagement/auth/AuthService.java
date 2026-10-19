package com.studentmanagement.studentmanagement.auth;

import com.studentmanagement.studentmanagement.security.JwtService;
import com.studentmanagement.studentmanagement.user.Role;
import com.studentmanagement.studentmanagement.user.User;
import com.studentmanagement.studentmanagement.user.UserRepository;
import com.studentmanagement.studentmanagement.user.UserResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // Hash the password before saving it
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // Public registration creates STUDENT accounts
        user.setRole(Role.STUDENT);

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        String accessToken =
                jwtService.generateAccessToken(user);

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        return new LoginResponse(
                accessToken,
                refreshToken.getToken()
        );
    }

    public LoginResponse refresh(RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenService.validateRefreshToken(
                        request.getRefreshToken()
                );

        User user = refreshToken.getUser();

        String accessToken =
                jwtService.generateAccessToken(user);

        return new LoginResponse(
                accessToken,
                refreshToken.getToken()
        );
    }

    public void logout(RefreshTokenRequest request) {

        refreshTokenService.revokeRefreshToken(
                request.getRefreshToken()
        );
    }
}