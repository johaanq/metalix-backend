package com.metalix.metalixbackend.monitoring.application.services;

import com.metalix.metalixbackend.monitoring.domain.model.aggregates.Report;
import com.metalix.metalixbackend.monitoring.domain.model.entities.Alert;
import com.metalix.metalixbackend.monitoring.domain.model.entities.Metric;
import com.metalix.metalixbackend.monitoring.domain.repository.AlertRepository;
import com.metalix.metalixbackend.monitoring.domain.repository.MetricRepository;
import com.metalix.metalixbackend.monitoring.domain.repository.ReportRepository;
import com.metalix.metalixbackend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitoringService {
    
    private final ReportRepository reportRepository;
    private final MetricRepository metricRepository;
    private final AlertRepository alertRepository;
    
    // Reports
    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report", id));
    }
    
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }
    
    public List<Report> getReportsByMunicipality(Long municipalityId) {
        return reportRepository.findByMunicipalityId(municipalityId);
    }
    
    @Transactional
    public Report createReport(Report report) {
        return reportRepository.save(report);
    }
    
    @Transactional
    public void deleteReport(Long id) {
        Report report = getReportById(id);
        reportRepository.delete(report);
    }
    
    // Metrics
    public Metric getMetricById(Long id) {
        return metricRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Metric", id));
    }
    
    public List<Metric> getAllMetrics() {
        return metricRepository.findAll();
    }
    
    public List<Metric> getMetricsByMunicipality(Long municipalityId) {
        return metricRepository.findByMunicipalityId(municipalityId);
    }
    
    @Transactional
    public Metric createMetric(Metric metric) {
        return metricRepository.save(metric);
    }
    
    @Transactional
    public void deleteMetric(Long id) {
        Metric metric = getMetricById(id);
        metricRepository.delete(metric);
    }
    
    // Alerts
    public Alert getAlertById(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", id));
    }
    
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }
    
    public List<Alert> getAlertsByMunicipality(Long municipalityId) {
        return alertRepository.findByMunicipalityId(municipalityId);
    }
    
    public List<Alert> getUnreadAlerts() {
        return alertRepository.findByIsRead(false);
    }
    
    public List<Alert> getUnreadAlertsByMunicipality(Long municipalityId) {
        return alertRepository.findByMunicipalityIdAndIsRead(municipalityId, false);
    }
    
    @Transactional
    public Alert createAlert(Alert alert) {
        return alertRepository.save(alert);
    }
    
    @Transactional
    public Alert markAlertAsRead(Long id) {
        Alert alert = getAlertById(id);
        alert.setIsRead(true);
        return alertRepository.save(alert);
    }
    
    @Transactional
    public Alert updateAlert(Long id, java.util.Map<String, Object> updates) {
        Alert alert = getAlertById(id);
        
        if (updates.containsKey("isResolved")) {
            alert.setIsResolved((Boolean) updates.get("isResolved"));
            if ((Boolean) updates.get("isResolved")) {
                alert.setResolvedAt(LocalDateTime.now());
            }
        }
        
        if (updates.containsKey("isRead")) {
            alert.setIsRead((Boolean) updates.get("isRead"));
            if ((Boolean) updates.get("isRead")) {
                alert.setReadAt(LocalDateTime.now());
            }
        }
        
        return alertRepository.save(alert);
    }
    
    @Transactional
    public void deleteAlert(Long id) {
        Alert alert = getAlertById(id);
        alertRepository.delete(alert);
    }
}

