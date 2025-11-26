package com.metalix.metalixbackend.shared.interfaces.rest;

import lombok.RequiredArgsConstructor;
import com.metalix.metalixbackend.iam.application.services.AuthenticationService;
import com.metalix.metalixbackend.iam.interfaces.rest.dto.LoginRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class DataLoaderController {
    
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    
    @PostMapping("/load-sample-data")
    public ResponseEntity<Map<String, Object>> loadSampleData() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Read the SQL file
            ClassPathResource resource = new ClassPathResource("data-sample.sql");
            String sqlContent = resource.getContentAsString(StandardCharsets.UTF_8);
            
            // Split by semicolon and execute each statement
            String[] statements = sqlContent.split(";");
            int executedStatements = 0;
            StringBuilder errors = new StringBuilder();
            
            for (String statement : statements) {
                String trimmedStatement = statement.trim();
                if (!trimmedStatement.isEmpty() && !trimmedStatement.startsWith("--")) {
                    try {
                        jdbcTemplate.execute(trimmedStatement);
                        executedStatements++;
                        System.out.println("Executed: " + trimmedStatement.substring(0, Math.min(50, trimmedStatement.length())) + "...");
                    } catch (Exception e) {
                        // Log error but continue with other statements
                        String error = "Error executing statement: " + trimmedStatement.substring(0, Math.min(50, trimmedStatement.length())) + "... Error: " + e.getMessage();
                        System.err.println(error);
                        errors.append(error).append("\n");
                    }
                }
            }
            
            response.put("success", true);
            response.put("message", "Sample data loaded successfully");
            response.put("executedStatements", executedStatements);
            if (errors.length() > 0) {
                response.put("errors", errors.toString());
            }
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Error reading SQL file: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error loading sample data: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @DeleteMapping("/clear-all-data")
    public ResponseEntity<Map<String, Object>> clearAllData() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Disable foreign key checks
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
            
            // Clear all tables in reverse dependency order
            String[] tables = {
                "reward_transactions", "waste_collections", "sensor_data", "alerts", 
                "metrics", "reports", "rewards", "rfid_cards", "waste_collectors", 
                "zones", "users", "municipalities"
            };
            
            int clearedTables = 0;
            for (String table : tables) {
                try {
                    jdbcTemplate.execute("DELETE FROM " + table);
                    clearedTables++;
                } catch (Exception e) {
                    System.err.println("Error clearing table " + table + ": " + e.getMessage());
                }
            }
            
            // Re-enable foreign key checks
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            
            response.put("success", true);
            response.put("message", "All data cleared successfully");
            response.put("clearedTables", clearedTables);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error clearing data: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/create-basic-data")
    public ResponseEntity<Map<String, Object>> createBasicData() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Create basic municipalities
            jdbcTemplate.execute("INSERT INTO municipalities (name, code, region, population, area, contact_email, contact_phone, is_active, created_at, updated_at) VALUES " +
                "('Lima Metropolitana', 'LIM-001', 'Costa', 9674755, 2672.28, 'contacto@munilima.gob.pe', '+51-1-315-1919', true, NOW(), NOW()), " +
                "('Arequipa', 'AQP-001', 'Sur', 1008290, 9682.00, 'contacto@muniarequipa.gob.pe', '+51-54-221050', true, NOW(), NOW())");
            
            // Create basic users
            jdbcTemplate.execute("INSERT INTO users (email, password, first_name, last_name, role, municipality_id, phone, address, city, zip_code, is_active, rfid_card, total_points, created_at, updated_at) VALUES " +
                "('admin@metalix.com', '$2a$10$xZV/6x5kQO8nFzAUvwKAa.iB7ZqJWZH5E2vMaZmPvYvJzQq7XfNjO', 'Admin', 'Sistema', 'SYSTEM_ADMIN', 1, '+51-999-888-777', 'Av. Principal 123', 'Lima', '15001', true, 'ADMIN001', 0, NOW(), NOW()), " +
                "('maria.lopez@email.com', '$2a$10$xZV/6x5kQO8nFzAUvwKAa.iB7ZqJWZH5E2vMaZmPvYvJzQq7XfNjO', 'María', 'López', 'CITIZEN', 1, '+51-999-555-444', 'Jr. Las Flores 234', 'Lima', '15001', true, 'RFID001', 150, NOW(), NOW())");
            
            // Create basic zones
            jdbcTemplate.execute("INSERT INTO zones (name, municipality_id, type, coordinates, collection_schedule, description, is_active, created_at, updated_at) VALUES " +
                "('Miraflores Centro', 1, 'RESIDENTIAL', '{\"lat\": -12.1191, \"lng\": -77.0285}', 'Lunes, Miércoles, Viernes', 'Zona residencial centro de Miraflores', true, NOW(), NOW())");
            
            // Create basic waste collectors
            jdbcTemplate.execute("INSERT INTO waste_collectors (name, type, location, municipality_id, zone_id, capacity, current_fill, last_collection, next_scheduled_collection, status, sensor_id, created_at, updated_at) VALUES " +
                "('Contenedor Plástico - Miraflores 01', 'PLASTIC', 'Parque Kennedy', 1, 1, 100.0, 45.0, '2024-10-01 08:00:00', '2024-10-03 08:00:00', 'ACTIVE', 'SENSOR001', NOW(), NOW())");
            
            // Create basic rewards
            jdbcTemplate.execute("INSERT INTO rewards (name, description, points_cost, category, availability, municipality_id, image_url, expiration_date, terms_and_conditions, is_active, created_at, updated_at) VALUES " +
                "('Descuento 10% en Supermercado', 'Cupón de descuento del 10% en compras mayores a S/50', 50, 'DISCOUNT', 100, 1, 'https://example.com/discount-10.jpg', '2024-12-31', 'Válido solo en Lima. No acumulable.', true, NOW(), NOW())");
            
            response.put("success", true);
            response.put("message", "Basic data created successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating basic data: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/data-status")
    public ResponseEntity<Map<String, Object>> getDataStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if data exists in key tables
            Integer municipalityCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM municipalities", Integer.class);
            Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
            Integer collectorCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM waste_collectors", Integer.class);
            Integer rewardCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM rewards", Integer.class);
            
            // Check user details
            try {
                String adminPassword = jdbcTemplate.queryForObject("SELECT password FROM users WHERE email = 'admin@metalix.com'", String.class);
                response.put("adminPasswordHash", adminPassword);
            } catch (Exception e) {
                response.put("adminPasswordError", e.getMessage());
            }
            
            response.put("municipalities", municipalityCount);
            response.put("users", userCount);
            response.put("wasteCollectors", collectorCount);
            response.put("rewards", rewardCount);
            response.put("hasData", municipalityCount > 0 || userCount > 0);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", "Error checking data status: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/test-password")
    public ResponseEntity<Map<String, Object>> testPassword(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = request.get("email");
            String password = request.get("password");
            
            // Get stored password hash
            String storedHash = jdbcTemplate.queryForObject("SELECT password FROM users WHERE email = ?", String.class, email);
            
            // Test password match
            boolean matches = passwordEncoder.matches(password, storedHash);
            
            response.put("email", email);
            response.put("storedHash", storedHash);
            response.put("matches", matches);
            response.put("success", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error testing password: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/check-users")
    public ResponseEntity<Map<String, Object>> checkUsers() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get all users
            var users = jdbcTemplate.queryForList("SELECT id, email, first_name, last_name, role, is_active FROM users");
            
            response.put("users", users);
            response.put("count", users.size());
            response.put("success", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error checking users: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/create-test-user")
    public ResponseEntity<Map<String, Object>> createTestUser() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Create a test user with proper password encoding
            String encodedPassword = passwordEncoder.encode("password123");
            
            jdbcTemplate.execute("INSERT INTO users (email, password, first_name, last_name, role, municipality_id, phone, address, city, zip_code, is_active, rfid_card, total_points, created_at, updated_at) VALUES " +
                "('test@test.com', '" + encodedPassword + "', 'Test', 'User', 'CITIZEN', 1, '+51-999-000-000', 'Test Address', 'Lima', '15001', true, 'TEST001', 0, NOW(), NOW())");
            
            response.put("success", true);
            response.put("message", "Test user created successfully");
            response.put("email", "test@test.com");
            response.put("password", "password123");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating test user: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/test-auth-service")
    public ResponseEntity<Map<String, Object>> testAuthService(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = request.get("email");
            String password = request.get("password");
            
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail(email);
            loginRequest.setPassword(password);
            
            var authResponse = authenticationService.login(loginRequest);
            
            response.put("success", true);
            response.put("token", authResponse.getToken());
            response.put("userId", authResponse.getUserId());
            response.put("email", authResponse.getEmail());
            response.put("role", authResponse.getRole());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error in auth service: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }
}
