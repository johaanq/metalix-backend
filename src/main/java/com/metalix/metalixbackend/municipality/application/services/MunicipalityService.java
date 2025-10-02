package com.metalix.metalixbackend.municipality.application.services;

import com.metalix.metalixbackend.municipality.domain.model.aggregates.Municipality;
import com.metalix.metalixbackend.municipality.domain.repository.MunicipalityRepository;
import com.metalix.metalixbackend.municipality.interfaces.rest.dto.DashboardDataResponse;
import com.metalix.metalixbackend.municipality.interfaces.rest.dto.MunicipalityStatsResponse;
import com.metalix.metalixbackend.shared.exception.ResourceNotFoundException;
import com.metalix.metalixbackend.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MunicipalityService {
    
    private final MunicipalityRepository municipalityRepository;
    private final JdbcTemplate jdbcTemplate;
    
    public Municipality getMunicipalityById(Long id) {
        return municipalityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Municipality", id));
    }
    
    public Municipality getMunicipalityByCode(String code) {
        return municipalityRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Municipality with code " + code + " not found"));
    }
    
    public List<Municipality> getAllMunicipalities() {
        return municipalityRepository.findAll();
    }
    
    public List<Municipality> getActiveMunicipalities() {
        return municipalityRepository.findByIsActive(true);
    }
    
    public List<Municipality> getMunicipalitiesByRegion(String region) {
        return municipalityRepository.findByRegion(region);
    }
    
    @Transactional
    public Municipality createMunicipality(Municipality municipality) {
        if (municipalityRepository.existsByCode(municipality.getCode())) {
            throw new ValidationException("Municipality with code " + municipality.getCode() + " already exists");
        }
        return municipalityRepository.save(municipality);
    }
    
    @Transactional
    public Municipality updateMunicipality(Long id, Municipality updatedMunicipality) {
        Municipality municipality = getMunicipalityById(id);
        
        if (updatedMunicipality.getName() != null) municipality.setName(updatedMunicipality.getName());
        if (updatedMunicipality.getRegion() != null) municipality.setRegion(updatedMunicipality.getRegion());
        if (updatedMunicipality.getPopulation() != null) municipality.setPopulation(updatedMunicipality.getPopulation());
        if (updatedMunicipality.getArea() != null) municipality.setArea(updatedMunicipality.getArea());
        if (updatedMunicipality.getContactEmail() != null) municipality.setContactEmail(updatedMunicipality.getContactEmail());
        if (updatedMunicipality.getContactPhone() != null) municipality.setContactPhone(updatedMunicipality.getContactPhone());
        
        return municipalityRepository.save(municipality);
    }
    
    @Transactional
    public void deleteMunicipality(Long id) {
        Municipality municipality = getMunicipalityById(id);
        municipalityRepository.delete(municipality);
    }
    
    public MunicipalityStatsResponse getMunicipalityStats(Long municipalityId) {
        getMunicipalityById(municipalityId); // Validate exists
        
        // Get total users
        Integer totalUsers = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE municipality_id = ?", 
            Integer.class, municipalityId
        );
        
        // Get active users
        Integer activeUsers = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE municipality_id = ? AND is_active = true", 
            Integer.class, municipalityId
        );
        
        // Get total collections
        Integer totalCollections = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM waste_collections WHERE municipality_id = ?", 
            Integer.class, municipalityId
        );
        
        // Get total weight
        Double totalWeight = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(weight), 0) FROM waste_collections WHERE municipality_id = ?", 
            Double.class, municipalityId
        );
        
        // Get total points
        Integer totalPoints = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(total_points), 0) FROM users WHERE municipality_id = ?", 
            Integer.class, municipalityId
        );
        
        // Calculate average participation
        Double averageParticipation = activeUsers > 0 ? 
            (totalCollections.doubleValue() / activeUsers) : 0.0;
        
        return new MunicipalityStatsResponse(
            municipalityId,
            totalUsers,
            activeUsers,
            totalCollections,
            totalWeight,
            totalPoints,
            averageParticipation,
            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        );
    }
    
    public DashboardDataResponse getDashboardData(Long municipalityId) {
        getMunicipalityById(municipalityId); // Validate exists
        
        MunicipalityStatsResponse stats = getMunicipalityStats(municipalityId);
        
        // Calculate environmental impact (simplified formulas)
        double co2Saved = stats.getTotalWeight() * 2.5; // kg CO2 per kg waste
        double energySaved = stats.getTotalWeight() * 1.8; // kWh per kg waste
        double treesEquivalent = co2Saved / 21.77; // kg CO2 absorbed per tree per year
        
        DashboardDataResponse.EnvironmentalImpact impact = new DashboardDataResponse.EnvironmentalImpact(
            co2Saved,
            energySaved,
            treesEquivalent
        );
        
        // Get recent unresolved alerts
        List<DashboardDataResponse.AlertSummary> recentAlerts = jdbcTemplate.query(
            "SELECT id, alert_type, severity, message, is_resolved, created_at " +
            "FROM alerts WHERE municipality_id = ? AND is_resolved = false " +
            "ORDER BY created_at DESC LIMIT 5",
            (rs, rowNum) -> new DashboardDataResponse.AlertSummary(
                rs.getLong("id"),
                rs.getString("alert_type"),
                rs.getString("severity"),
                rs.getString("message"),
                rs.getBoolean("is_resolved"),
                rs.getTimestamp("created_at").toLocalDateTime()
            ),
            municipalityId
        );
        
        // Get top collectors by weight
        List<DashboardDataResponse.TopCollector> topCollectors = jdbcTemplate.query(
            "SELECT wc.id, wc.name, " +
            "COUNT(wcol.id) as collections, " +
            "COALESCE(SUM(wcol.weight), 0) as weight " +
            "FROM waste_collectors wc " +
            "LEFT JOIN waste_collections wcol ON wc.id = wcol.collector_id " +
            "WHERE wc.municipality_id = ? " +
            "GROUP BY wc.id, wc.name " +
            "ORDER BY weight DESC LIMIT 5",
            (rs, rowNum) -> new DashboardDataResponse.TopCollector(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getInt("collections"),
                rs.getDouble("weight")
            ),
            municipalityId
        );
        
        return new DashboardDataResponse(
            municipalityId,
            stats.getTotalCollections(),
            stats.getTotalWeight(),
            stats.getActiveUsers(),
            stats.getTotalPoints(),
            impact,
            recentAlerts,
            topCollectors,
            LocalDateTime.now()
        );
    }
}

