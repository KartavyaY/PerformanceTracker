package org.ncu.performancetracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Metric name is required")
    private String metricName;

    @NotNull(message = "Value is required")
    private Double value;

    private LocalDate date;

    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "athlete_id")
    @JsonIgnore
    private Athlete athlete;

    public PerformanceRecord(String metricName, double value, LocalDate date, String remarks) {
        this.metricName = metricName;
        this.value = value;
        this.date = date;
        this.remarks = remarks;
    }

    @PrePersist
    public void prePersist() {
        if (this.date == null) {
            this.date = LocalDate.now();
        }
    }

}