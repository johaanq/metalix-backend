package com.metalix.metalixbackend.iam.application.services;

import com.metalix.metalixbackend.iam.domain.model.aggregates.User;
import com.metalix.metalixbackend.iam.domain.model.valueobjects.Role;
import com.metalix.metalixbackend.iam.domain.repository.UserRepository;
import com.metalix.metalixbackend.iam.interfaces.rest.dto.UserActivityResponse;
import com.metalix.metalixbackend.iam.interfaces.rest.dto.UserProfileResponse;
import com.metalix.metalixbackend.iam.interfaces.rest.dto.UserStatsResponse;
import com.metalix.metalixbackend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }
    
    public Page<User> getUsersByRole(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }
    
    public List<User> getUsersByMunicipality(Long municipalityId) {
        return userRepository.findByMunicipalityId(municipalityId);
    }
    
    public Integer getUserPoints(Long userId) {
        User user = getUserById(userId);
        return user.getTotalPoints();
    }
    
    @Transactional
    public User updateUser(Long id, User updatedUser) {
        User user = getUserById(id);
        
        if (updatedUser.getFirstName() != null) user.setFirstName(updatedUser.getFirstName());
        if (updatedUser.getLastName() != null) user.setLastName(updatedUser.getLastName());
        if (updatedUser.getPhone() != null) user.setPhone(updatedUser.getPhone());
        if (updatedUser.getAddress() != null) user.setAddress(updatedUser.getAddress());
        if (updatedUser.getCity() != null) user.setCity(updatedUser.getCity());
        if (updatedUser.getZipCode() != null) user.setZipCode(updatedUser.getZipCode());
        if (updatedUser.getMunicipalityId() != null) user.setMunicipalityId(updatedUser.getMunicipalityId());
        
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
    
    @Transactional
    public void deactivateUser(Long id) {
        User user = getUserById(id);
        user.setIsActive(false);
        userRepository.save(user);
    }
    
    public User getUserByRfidCard(String rfidCard) {
        return userRepository.findByRfidCard(rfidCard)
                .orElseThrow(() -> new ResourceNotFoundException("User with RFID card " + rfidCard + " not found"));
    }
    
    public UserProfileResponse getUserProfile(Long userId) {
        User user = getUserById(userId);
        
        // Get total collections
        Integer totalCollections = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM waste_collections WHERE user_id = ?",
            Integer.class, userId
        );
        
        // Get total weight
        Double totalWeight = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(weight), 0) FROM waste_collections WHERE user_id = ?",
            Double.class, userId
        );
        
        // Get last activity
        LocalDateTime lastActivity = jdbcTemplate.query(
            "SELECT MAX(timestamp) as last_activity FROM waste_collections WHERE user_id = ?",
            rs -> rs.next() ? rs.getTimestamp("last_activity").toLocalDateTime() : null,
            userId
        );
        
        return UserProfileResponse.fromEntity(user, totalCollections, totalWeight, lastActivity);
    }
    
    public UserStatsResponse getUserStats(Long userId) {
        User user = getUserById(userId);
        
        // Get total collections
        Integer totalCollections = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM waste_collections WHERE user_id = ?",
            Integer.class, userId
        );
        
        // Get total weight
        Double totalWeight = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(weight), 0) FROM waste_collections WHERE user_id = ?",
            Double.class, userId
        );
        
        // Calculate average weight
        Double averageWeight = totalCollections > 0 ? totalWeight / totalCollections : 0.0;
        
        // Get collections this month
        Integer collectionsThisMonth = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM waste_collections " +
            "WHERE user_id = ? AND MONTH(timestamp) = MONTH(CURRENT_DATE) AND YEAR(timestamp) = YEAR(CURRENT_DATE)",
            Integer.class, userId
        );
        
        // Get points this month
        Integer pointsThisMonth = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(points), 0) FROM waste_collections " +
            "WHERE user_id = ? AND MONTH(timestamp) = MONTH(CURRENT_DATE) AND YEAR(timestamp) = YEAR(CURRENT_DATE)",
            Integer.class, userId
        );
        
        // Calculate user rank (based on total points)
        Integer rank = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) + 1 FROM users WHERE total_points > ? AND municipality_id = ?",
            Integer.class, user.getTotalPoints(), user.getMunicipalityId()
        );
        
        return new UserStatsResponse(
            userId,
            totalCollections,
            totalWeight,
            user.getTotalPoints(),
            averageWeight,
            collectionsThisMonth,
            pointsThisMonth,
            rank,
            LocalDateTime.now()
        );
    }
    
    public List<UserActivityResponse> getUserActivity(Long userId, Integer limit) {
        getUserById(userId); // Validate user exists
        
        return jdbcTemplate.query(
            "SELECT wc.id, 'COLLECTION' as type, " +
            "CONCAT('Collected ', wc.weight, ' kg of ', wc.recyclable_type) as description, " +
            "wc.points, wc.weight, wc.timestamp " +
            "FROM waste_collections wc " +
            "WHERE wc.user_id = ? " +
            "UNION ALL " +
            "SELECT rt.id, 'REWARD_REDEMPTION' as type, " +
            "CONCAT('Redeemed reward') as description, " +
            "rt.points, NULL as weight, rt.timestamp " +
            "FROM reward_transactions rt " +
            "WHERE rt.user_id = ? AND rt.transaction_type = 'REDEEMED' " +
            "ORDER BY timestamp DESC " +
            "LIMIT ?",
            (rs, rowNum) -> new UserActivityResponse(
                rs.getLong("id"),
                rs.getString("type"),
                rs.getString("description"),
                rs.getInt("points"),
                rs.getObject("weight", Double.class),
                rs.getTimestamp("timestamp").toLocalDateTime()
            ),
            userId, userId, limit
        );
    }
}

