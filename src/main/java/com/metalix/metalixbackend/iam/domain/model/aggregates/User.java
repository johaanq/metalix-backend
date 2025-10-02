package com.metalix.metalixbackend.iam.domain.model.aggregates;

import com.metalix.metalixbackend.iam.domain.model.valueobjects.Role;
import com.metalix.metalixbackend.shared.domain.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    
    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank
    @Size(min = 6)
    @Column(nullable = false)
    private String password;
    
    @NotBlank
    @Column(nullable = false)
    private String firstName;
    
    @NotBlank
    @Column(nullable = false)
    private String lastName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.CITIZEN;
    
    private Long municipalityId;
    
    private String phone;
    
    private String address;
    
    private String city;
    
    private String zipCode;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    private String rfidCard;
    
    @Column(nullable = false)
    private Integer totalPoints = 0;
    
    public void addPoints(Integer points) {
        this.totalPoints += points;
    }
    
    public void deductPoints(Integer points) {
        if (this.totalPoints >= points) {
            this.totalPoints -= points;
        } else {
            throw new IllegalArgumentException("Insufficient points");
        }
    }
}

