package com.cinemesh.repository;

import com.cinemesh.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByTenantId(String tenantId);
    List<Show> findByMovieId(Long movieId);
    List<Show> findByTenantIdAndMovieId(String tenantId, Long movieId);
}
