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
import java.util.Optional;

@RestController
@RequestMapping("/api/performance-records")
public class PerformanceRecordController {

    private final PerformanceRecordService recordService;

    @Autowired
    public PerformanceRecordController(PerformanceRecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping("/{athleteId}")
    public ResponseEntity<PerformanceRecord> addRecord(
            @PathVariable Long athleteId,
            @Valid @RequestBody PerformanceRecord record) {
        PerformanceRecord savedRecord = recordService.addRecordToAthlete(athleteId, record);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRecord);
    }

    @GetMapping("/{athleteId}")
    public ResponseEntity<List<PerformanceRecord>> getAthleteRecords(@PathVariable Long athleteId) {
        List<PerformanceRecord> records = recordService.findRecordsByAthleteId(athleteId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{athleteId}/metric/{metricName}")
    public ResponseEntity<List<PerformanceRecord>> getAthleteRecordsByMetric(
            @PathVariable Long athleteId,
            @PathVariable String metricName) {
        List<PerformanceRecord> records = recordService.findRecordsByAthleteIdAndMetric(athleteId, metricName);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{athleteId}/date-range")
    public ResponseEntity<List<PerformanceRecord>> getAthleteRecordsByDateRange(
            @PathVariable Long athleteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<PerformanceRecord> records = recordService.findRecordsByAthleteIdAndDateRange(athleteId, start, end);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{athleteId}/personal-bests")
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

    @PutMapping("/{athleteId}")
    public ResponseEntity<PerformanceRecord> updateRecord(
            @PathVariable Long athleteId,
            @Valid @RequestBody PerformanceRecord record) {
        PerformanceRecord updatedRecord = recordService.updateRecord(athleteId, record);
        return new ResponseEntity<>(updatedRecord, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRecord(@RequestParam Long athleteId, @RequestParam Long metricId) {
        Optional<PerformanceRecord> optionalRecord = recordService.findRecordById(metricId);

        if (optionalRecord.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PerformanceRecord record = optionalRecord.get();

        if (!record.getAthlete().getId().equals(athleteId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Record does not belong to the specified athlete");
        }

        recordService.deleteRecord(metricId);
        return ResponseEntity.ok("Record with id " + metricId + " was deleted");
    }

}