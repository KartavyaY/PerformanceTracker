package org.ncu.performancetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ncu.performancetracker.model.Athlete;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AthleteRepository extends JpaRepository<Athlete, Long> {

    @Query("SELECT a FROM Athlete a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name,'%'))")
    List<Athlete> findAthletesByName(@Param("name") String name);

}

