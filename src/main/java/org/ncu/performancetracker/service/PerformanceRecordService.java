package org.ncu.performancetracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.ncu.performancetracker.exception.ResourceNotFoundException;
import org.ncu.performancetracker.model.Athlete;
import org.ncu.performancetracker.model.PerformanceRecord;
import org.ncu.performancetracker.repository.AthleteRepository;
import org.ncu.performancetracker.repository.PerformanceRecordRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PerformanceRecordService {

    private final PerformanceRecordRepository recordRepository;
    private final AthleteRepository athleteRepository;

    @Autowired
    public PerformanceRecordService(
            PerformanceRecordRepository recordRepository,
            AthleteRepository athleteRepository) {
        this.recordRepository = recordRepository;
        this.athleteRepository = athleteRepository;
    }

    @Transactional
    public PerformanceRecord addRecordToAthlete(Long athleteId, PerformanceRecord record) {
        Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found with id: " + athleteId));

        record.setAthlete(athlete);
        return recordRepository.save(record);
    }

    public List<PerformanceRecord> findRecordsByAthleteId(Long athleteId) {
        // Verify athlete exists
        if (!athleteRepository.existsById(athleteId)) {
            throw new ResourceNotFoundException("Athlete not found with id: " + athleteId);
        }

        return recordRepository.findByAthleteId(athleteId);
    }

    public List<PerformanceRecord> findRecordsByAthleteIdAndMetric(Long athleteId, String metricName) {
        // Verify athlete exists
        if (!athleteRepository.existsById(athleteId)) {
            throw new ResourceNotFoundException("Athlete not found with id: " + athleteId);
        }

        return recordRepository.findByAthleteIdAndMetricName(athleteId, metricName);
    }

    public List<PerformanceRecord> findRecordsByAthleteIdAndDateRange(
            Long athleteId, LocalDate startDate, LocalDate endDate) {
        // Verify athlete exists
        if (!athleteRepository.existsById(athleteId)) {
            throw new ResourceNotFoundException("Athlete not found with id: " + athleteId);
        }

        return recordRepository.findByAthleteIdAndDateBetween(athleteId, startDate, endDate);
    }

    public Map<String, Double> findPersonalBestsByAthleteId(Long athleteId) {
        // Verify athlete exists
        if (!athleteRepository.existsById(athleteId)) {
            throw new ResourceNotFoundException("Athlete not found with id: " + athleteId);
        }

        List<Object[]> results = recordRepository.findPersonalBestsByAthleteId(athleteId);

        Map<String, Double> personalBests = new HashMap<>();
        for (Object[] result : results) {
            String metricName = (String) result[0];
            Double maxValue = (Double) result[1];
            personalBests.put(metricName, maxValue);
        }

        return personalBests;
    }

    public Map<String, Double> compareAthletes(Long athlete1Id, Long athlete2Id, String metricName) {
        // Verify athletes exist
        Athlete athlete1 = athleteRepository.findById(athlete1Id)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found with id: " + athlete1Id));

        Athlete athlete2 = athleteRepository.findById(athlete2Id)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found with id: " + athlete2Id));

        // Get the best performance for each athlete on the specific metric
        List<PerformanceRecord> athlete1Records = recordRepository.findByAthleteIdAndMetricName(athlete1Id, metricName);
        List<PerformanceRecord> athlete2Records = recordRepository.findByAthleteIdAndMetricName(athlete2Id, metricName);

        Double athlete1Best = athlete1Records.stream()
                .mapToDouble(PerformanceRecord::getValue)
                .max()
                .orElse(0.0);

        Double athlete2Best = athlete2Records.stream()
                .mapToDouble(PerformanceRecord::getValue)
                .max()
                .orElse(0.0);

        Map<String, Double> comparison = new HashMap<>();
        comparison.put(athlete1.getName(), athlete1Best);
        comparison.put(athlete2.getName(), athlete2Best);

        return comparison;
    }

    @Transactional
    public PerformanceRecord updateRecord(Long id, PerformanceRecord updatedRecord) {
        PerformanceRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Performance record not found with id: " + id));

        record.setMetricName(updatedRecord.getMetricName());
        record.setValue(updatedRecord.getValue());
        record.setDate(updatedRecord.getDate());
        record.setRemarks(updatedRecord.getRemarks());

        return recordRepository.save(record);
    }

    @Transactional
    public void deleteRecord(Long id) {
        if (!recordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Performance record not found with id: " + id);
        }

        recordRepository.deleteById(id);
    }
}
