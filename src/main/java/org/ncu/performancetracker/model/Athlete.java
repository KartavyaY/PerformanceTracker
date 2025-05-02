package org.ncu.performancetracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Athlete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private String sport;
    private String position;

    @OneToMany(mappedBy = "athlete", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformanceRecord> performanceRecords = new ArrayList<>();

    public void addPerformanceRecord(PerformanceRecord record) {
        performanceRecords.add(record);
        record.setAthlete(this);
    }

    public void removePerformanceRecord(PerformanceRecord record) {
        performanceRecords.remove(record);
        record.setAthlete(null);
    }
}