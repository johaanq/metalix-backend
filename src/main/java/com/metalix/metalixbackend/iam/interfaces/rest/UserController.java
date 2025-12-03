package com.metalix.metalixbackend.iam.interfaces.rest;

import com.metalix.metalixbackend.iam.application.services.UserService;
import com.metalix.metalixbackend.iam.domain.model.aggregates.User;
import com.metalix.metalixbackend.iam.domain.model.valueobjects.Role;
import com.metalix.metalixbackend.iam.interfaces.rest.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "User management endpoints")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Get all users with pagination")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "100") int size,
            @RequestParam(required = false, defaultValue = "id") String sort,
            @RequestParam(required = false, defaultValue = "asc") String direction) {
        
        // Create Pageable with proper sort handling
        org.springframework.data.domain.Sort sortObj = org.springframework.data.domain.Sort.by(
            direction.equalsIgnoreCase("desc") ? 
                org.springframework.data.domain.Sort.Direction.DESC : 
                org.springframework.data.domain.Sort.Direction.ASC, 
            sort
        );
        
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sortObj);
        Page<User> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users.map(UserResponse::fromEntity));
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Get all users without pagination")
    public ResponseEntity<List<UserResponse>> getAllUsersWithoutPagination() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users.stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList()));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }
    
    @GetMapping("/{id}/points")
    @Operation(summary = "Get user points")
    public ResponseEntity<Integer> getUserPoints(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserPoints(id));
    }
    
    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Get users by role")
    public ResponseEntity<Page<UserResponse>> getUsersByRole(
            @PathVariable Role role,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "100") int size
    ) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        Page<User> users = userService.getUsersByRole(role, pageable);
        return ResponseEntity.ok(users.map(UserResponse::fromEntity));
    }
    
    @GetMapping("/municipality/{municipalityId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Get users by municipality")
    public ResponseEntity<List<UserResponse>> getUsersByMunicipality(@PathVariable Long municipalityId) {
        List<User> users = userService.getUsersByMunicipality(municipalityId);
        return ResponseEntity.ok(users.stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList()));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody User user
    ) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(UserResponse.fromEntity(updatedUser));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Deactivate user")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/profile")
    @Operation(summary = "Get user profile with extended information")
    public ResponseEntity<com.metalix.metalixbackend.iam.interfaces.rest.dto.UserProfileResponse> getUserProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }
    
    @GetMapping("/{id}/stats")
    @Operation(summary = "Get user statistics")
    public ResponseEntity<com.metalix.metalixbackend.iam.interfaces.rest.dto.UserStatsResponse> getUserStats(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserStats(id));
    }
    
    @GetMapping("/{id}/activity")
    @Operation(summary = "Get user activity history")
    public ResponseEntity<java.util.List<com.metalix.metalixbackend.iam.interfaces.rest.dto.UserActivityResponse>> getUserActivity(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        return ResponseEntity.ok(userService.getUserActivity(id, limit));
    }
}

