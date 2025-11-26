package com.metalix.metalixbackend.shared.interfaces.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class HomeController {
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "Metalix Backend API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("swagger", "/swagger-ui/index.html");
        response.put("apiDocs", "/v3/api-docs");
        return ResponseEntity.ok(response);
    }
}

