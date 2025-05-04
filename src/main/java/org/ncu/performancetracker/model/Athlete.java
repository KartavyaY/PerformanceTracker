package org.ncu.performancetracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Athlete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "sport is required")
    private String sport;
    @NotBlank(message = "position is required")
    private String position;

    @OneToMany(mappedBy = "athlete", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformanceRecord> performanceRecords = new ArrayList<>();

    public Athlete(String name, String sport, String position) {
        this.name = name;
        this.sport = sport;
        this.position = position;
    }

    public void addPerformanceRecord(PerformanceRecord record) {
        performanceRecords.add(record);
        record.setAthlete(this);
    }

    public void removePerformanceRecord(PerformanceRecord record) {
        performanceRecords.remove(record);
        record.setAthlete(null);
    }
}