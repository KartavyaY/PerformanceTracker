// src/main/java/com/sportstracker/app/repository/PerformanceRecordRepository.java
package org.ncu.performancetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.ncu.performancetracker.model.PerformanceRecord;

import java.time.LocalDate;
import java.util.List;

public interface PerformanceRecordRepository extends JpaRepository<PerformanceRecord, Long> {

    List<PerformanceRecord> findByAthleteId(Long athleteId);

    List<PerformanceRecord> findByAthleteIdAndMetricName(Long athleteId, String metricName);

    List<PerformanceRecord> findByAthleteIdAndDateBetween(Long athleteId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT p.metricName, MAX(p.value) FROM PerformanceRecord p WHERE p.athlete.id = :athleteId GROUP BY p.metricName")
    List<Object[]> findPersonalBestsByAthleteId(@Param("athleteId") Long athleteId);
}