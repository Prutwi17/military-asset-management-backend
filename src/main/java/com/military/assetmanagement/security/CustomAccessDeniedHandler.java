package com.military.assetmanagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.military.assetmanagement.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        logger.error("Forbidden access error: {}", accessDeniedException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpServletResponse.SC_FORBIDDEN,
                "Forbidden",
                "Access denied: Insufficient permissions for this resource",
                request.getRequestURI()
        );

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
