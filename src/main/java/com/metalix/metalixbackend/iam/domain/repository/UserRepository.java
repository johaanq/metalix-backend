package com.metalix.metalixbackend.iam.domain.repository;

import com.metalix.metalixbackend.iam.domain.model.aggregates.User;
import com.metalix.metalixbackend.iam.domain.model.valueobjects.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    Page<User> findByRole(Role role, Pageable pageable);
    List<User> findByMunicipalityId(Long municipalityId);
    Page<User> findByMunicipalityId(Long municipalityId, Pageable pageable);
    Optional<User> findByRfidCard(String rfidCard);
}

