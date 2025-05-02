package org.ncu.performancetracker.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.ncu.performancetracker.model.PerformanceRecord;
import org.ncu.performancetracker.service.PerformanceRecordService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PerformanceRecordController {

    private final PerformanceRecordService recordService;

    @Autowired
    public PerformanceRecordController(PerformanceRecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping("/athletes/{athleteId}/records")
    public ResponseEntity<PerformanceRecord> addRecord(
            @PathVariable Long athleteId,
            @Valid @RequestBody PerformanceRecord record) {
        PerformanceRecord savedRecord = recordService.addRecordToAthlete(athleteId, record);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRecord);
    }

    @GetMapping("/athletes/{athleteId}/records")
    public ResponseEntity<List<PerformanceRecord>> getAthleteRecords(@PathVariable Long athleteId) {
        List<PerformanceRecord> records = recordService.findRecordsByAthleteId(athleteId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/athletes/{athleteId}/records/metric/{metricName}")
    public ResponseEntity<List<PerformanceRecord>> getAthleteRecordsByMetric(
            @PathVariable Long athleteId,
            @PathVariable String metricName) {
        List<PerformanceRecord> records = recordService.findRecordsByAthleteIdAndMetric(athleteId, metricName);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/athletes/{athleteId}/records/daterange")
    public ResponseEntity<List<PerformanceRecord>> getAthleteRecordsByDateRange(
            @PathVariable Long athleteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<PerformanceRecord> records = recordService.findRecordsByAthleteIdAndDateRange(athleteId, start, end);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/athletes/{athleteId}/personal-bests")
    public ResponseEntity<Map<String, Double>> getPersonalBests(@PathVariable Long athleteId) {
        Map<String, Double> personalBests = recordService.findPersonalBestsByAthleteId(athleteId);
        return ResponseEntity.ok(personalBests);
    }

    @GetMapping("/compare")
    public ResponseEntity<Map<String, Double>> compareAthletes(
            @RequestParam Long athlete1Id,
            @RequestParam Long athlete2Id,
            @RequestParam String metricName) {
        Map<String, Double> comparison = recordService.compareAthletes(athlete1Id, athlete2Id, metricName);
        return ResponseEntity.ok(comparison);
    }

    @PutMapping("/records/{id}")
    public ResponseEntity<PerformanceRecord> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody PerformanceRecord record) {
        PerformanceRecord updatedRecord = recordService.updateRecord(id, record);
        return ResponseEntity.ok(updatedRecord);
    }

    @DeleteMapping("/records/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}