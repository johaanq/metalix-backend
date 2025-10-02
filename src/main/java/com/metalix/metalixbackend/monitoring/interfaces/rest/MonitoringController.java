package com.metalix.metalixbackend.monitoring.interfaces.rest;

import com.metalix.metalixbackend.monitoring.application.services.MonitoringService;
import com.metalix.metalixbackend.monitoring.domain.model.aggregates.Report;
import com.metalix.metalixbackend.monitoring.domain.model.entities.Alert;
import com.metalix.metalixbackend.monitoring.domain.model.entities.Metric;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/monitoring")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Monitoring", description = "Monitoring and reporting endpoints")
public class MonitoringController {
    
    private final MonitoringService monitoringService;
    
    // Reports
    @GetMapping("/reports")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Get all reports")
    public ResponseEntity<List<Report>> getAllReports() {
        return ResponseEntity.ok(monitoringService.getAllReports());
    }
    
    @GetMapping("/reports/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Get report by ID")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        return ResponseEntity.ok(monitoringService.getReportById(id));
    }
    
    @GetMapping("/reports/municipality/{municipalityId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Get reports by municipality")
    public ResponseEntity<List<Report>> getReportsByMunicipality(@PathVariable Long municipalityId) {
        return ResponseEntity.ok(monitoringService.getReportsByMunicipality(municipalityId));
    }
    
    @PostMapping("/reports")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Create report")
    public ResponseEntity<Report> createReport(@Valid @RequestBody Report report) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(monitoringService.createReport(report));
    }
    
    // Metrics
    @GetMapping("/metrics")
    @Operation(summary = "Get all metrics")
    public ResponseEntity<List<Metric>> getAllMetrics() {
        return ResponseEntity.ok(monitoringService.getAllMetrics());
    }
    
    @GetMapping("/metrics/{id}")
    @Operation(summary = "Get metric by ID")
    public ResponseEntity<Metric> getMetricById(@PathVariable Long id) {
        return ResponseEntity.ok(monitoringService.getMetricById(id));
    }
    
    @GetMapping("/metrics/municipality/{municipalityId}")
    @Operation(summary = "Get metrics by municipality")
    public ResponseEntity<List<Metric>> getMetricsByMunicipality(@PathVariable Long municipalityId) {
        return ResponseEntity.ok(monitoringService.getMetricsByMunicipality(municipalityId));
    }
    
    @PostMapping("/metrics")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Create metric")
    public ResponseEntity<Metric> createMetric(@Valid @RequestBody Metric metric) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(monitoringService.createMetric(metric));
    }
    
    // Alerts
    @GetMapping("/alerts")
    @Operation(summary = "Get all alerts")
    public ResponseEntity<List<Alert>> getAllAlerts() {
        return ResponseEntity.ok(monitoringService.getAllAlerts());
    }
    
    @GetMapping("/alerts/{id}")
    @Operation(summary = "Get alert by ID")
    public ResponseEntity<Alert> getAlertById(@PathVariable Long id) {
        return ResponseEntity.ok(monitoringService.getAlertById(id));
    }
    
    @GetMapping("/alerts/municipality/{municipalityId}")
    @Operation(summary = "Get alerts by municipality")
    public ResponseEntity<List<Alert>> getAlertsByMunicipality(@PathVariable Long municipalityId) {
        return ResponseEntity.ok(monitoringService.getAlertsByMunicipality(municipalityId));
    }
    
    @GetMapping("/alerts/unread")
    @Operation(summary = "Get unread alerts")
    public ResponseEntity<List<Alert>> getUnreadAlerts() {
        return ResponseEntity.ok(monitoringService.getUnreadAlerts());
    }
    
    @PostMapping("/alerts")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Create alert")
    public ResponseEntity<Alert> createAlert(@Valid @RequestBody Alert alert) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(monitoringService.createAlert(alert));
    }
    
    @PatchMapping("/alerts/{id}/read")
    @Operation(summary = "Mark alert as read")
    public ResponseEntity<Alert> markAlertAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(monitoringService.markAlertAsRead(id));
    }
    
    @PatchMapping("/alerts/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Resolve/update alert")
    public ResponseEntity<Alert> updateAlert(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Object> updates
    ) {
        return ResponseEntity.ok(monitoringService.updateAlert(id, updates));
    }
}

