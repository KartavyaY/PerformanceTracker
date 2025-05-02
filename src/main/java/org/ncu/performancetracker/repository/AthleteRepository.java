package org.ncu.performancetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ncu.performancetracker.model.Athlete;

public interface AthleteRepository extends JpaRepository<Athlete, Long> {
}

