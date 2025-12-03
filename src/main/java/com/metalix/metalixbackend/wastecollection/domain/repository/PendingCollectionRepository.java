package com.metalix.metalixbackend.wastecollection.domain.repository;

import com.metalix.metalixbackend.wastecollection.domain.model.entities.PendingCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PendingCollectionRepository extends JpaRepository<PendingCollection, Long> {
    Optional<PendingCollection> findBySessionToken(String sessionToken);
    List<PendingCollection> findByCompletedFalse();
    List<PendingCollection> findByExpiresAtBeforeAndCompletedFalse(LocalDateTime dateTime);
    void deleteByExpiresAtBeforeAndCompletedFalse(LocalDateTime dateTime);
}

