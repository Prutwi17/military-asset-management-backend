package com.military.assetmanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping({"/health", "/api/health"})
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Military Asset Management System API");
        health.put("version", "1.0.0");
        health.put("timestamp", LocalDateTime.now().toString());

        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            health.put("database", result != null && result == 1 ? "CONNECTED" : "DEGRADED");
        } catch (Exception e) {
            health.put("database", "DISCONNECTED");
            return ResponseEntity.status(503).body(health);
        }

        return ResponseEntity.ok(health);
    }
}
