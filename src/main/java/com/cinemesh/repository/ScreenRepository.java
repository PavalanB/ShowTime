package com.cinemesh.repository;

import com.cinemesh.model.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {
    List<Screen> findByTenantId(String tenantId);
    Optional<Screen> findByTenantIdAndScreenNumber(String tenantId, int screenNumber);
}
