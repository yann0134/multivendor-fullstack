package com.camoutech.multivendor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour surveiller la santé de l'application
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    /**
     * Vérifie la santé de la connexion à la base de données
     */
    private boolean isConnectionHealthy() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(5); // 5 secondes de timeout
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Endpoint pour vérifier la santé de la base de données
     */
    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> checkDatabaseHealth() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean isHealthy = isConnectionHealthy();
            
            response.put("status", isHealthy ? "UP" : "DOWN");
            response.put("database", "MySQL");
            response.put("timestamp", System.currentTimeMillis());
            
            if (isHealthy) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(503).body(response);
            }
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("error", e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(503).body(response);
        }
    }

    /**
     * Endpoint général de santé de l'application
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean dbHealthy = isConnectionHealthy();
            
            response.put("status", dbHealthy ? "UP" : "DOWN");
            response.put("database", dbHealthy ? "UP" : "DOWN");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("error", e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(503).body(response);
        }
    }
}
