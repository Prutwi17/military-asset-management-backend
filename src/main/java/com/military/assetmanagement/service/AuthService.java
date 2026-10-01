package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.AuthResponse;
import com.military.assetmanagement.dto.LoginRequest;
import com.military.assetmanagement.dto.UserSummaryDto;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.UserRepository;
import com.military.assetmanagement.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuditService auditService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtService jwtService,
                       AuditService auditService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest loginRequest) {
        // Find user by username or email first
        User user = userRepository.findByUsernameOrEmail(loginRequest.getUsername(), loginRequest.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!user.isActive()) {
            throw new BadCredentialsException("Account is deactivated. Contact administrator.");
        }

        // Authenticate with user's actual username
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // NOTICE: We do NOT trust loginRequest.getRole().
        // The authoritative role comes directly from the user record in database.
        logger.info("User '{}' authenticated successfully with authoritative role '{}'", 
                user.getUsername(), user.getRole());

        auditService.recordAudit(
                user,
                "USER_LOGIN",
                "User",
                user.getId(),
                "User authenticated successfully with authoritative role: " + user.getRole()
        );

        String jwtToken = jwtService.generateToken(user);
        long expiresInMs = jwtService.getExpirationMs();

        return new AuthResponse(jwtToken, expiresInMs, UserSummaryDto.fromEntity(user));
    }

    @Transactional(readOnly = true)
    public UserSummaryDto getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return UserSummaryDto.fromEntity(user);
    }
}
