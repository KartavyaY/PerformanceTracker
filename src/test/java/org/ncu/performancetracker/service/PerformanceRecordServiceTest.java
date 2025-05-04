package org.ncu.performancetracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ncu.performancetracker.exception.ResourceNotFoundException;
import org.ncu.performancetracker.model.Athlete;
import org.ncu.performancetracker.model.PerformanceRecord;
import org.ncu.performancetracker.repository.AthleteRepository;
import org.ncu.performancetracker.repository.PerformanceRecordRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Use Mockito extension instead of SpringBootTest
public class PerformanceRecordServiceTest {

    @Mock
    private PerformanceRecordRepository recordRepository;

    @Mock
    private AthleteRepository athleteRepository;

    @InjectMocks
    private PerformanceRecordService performanceRecordService;

    private Athlete athlete;
    private PerformanceRecord record;

    @BeforeEach
    public void setUp() {
        // No need to call MockitoAnnotations.openMocks() when using @ExtendWith(MockitoExtension.class)

        // Create sample data
        athlete = new Athlete();
        athlete.setId(1L);
        athlete.setName("John Doe");

        record = new PerformanceRecord();
        record.setId(1L);
        record.setMetricName("Speed");
        record.setValue(10.5);
        record.setDate(LocalDate.now());
        record.setAthlete(athlete);
    }

    @Test
    public void testAddRecordToAthlete() {
        // Make sure to set stubbing before calling the method
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(athlete));
        when(recordRepository.save(any(PerformanceRecord.class))).thenReturn(record);

        PerformanceRecord savedRecord = performanceRecordService.addRecordToAthlete(1L, record);

        assertNotNull(savedRecord);
        assertEquals("Speed", savedRecord.getMetricName());
        verify(athleteRepository, times(1)).findById(1L);
        verify(recordRepository, times(1)).save(record);
    }

    @Test
    public void testAddRecordToAthlete_AthleteNotFound() {
        when(athleteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            performanceRecordService.addRecordToAthlete(1L, record);
        });
    }

    @Test
    public void testFindRecordsByAthleteId() {
        when(athleteRepository.existsById(1L)).thenReturn(true);
        when(recordRepository.findByAthleteId(1L)).thenReturn(List.of(record));

        assertFalse(performanceRecordService.findRecordsByAthleteId(1L).isEmpty());
        verify(recordRepository, times(1)).findByAthleteId(1L);
    }

    @Test
    public void testFindRecordsByAthleteId_AthleteNotFound() {
        when(athleteRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            performanceRecordService.findRecordsByAthleteId(1L);
        });
    }

    @Test
    public void testFindRecordsByAthleteIdAndMetric() {
        when(athleteRepository.existsById(1L)).thenReturn(true);
        when(recordRepository.findByAthleteIdAndMetricName(1L, "Speed")).thenReturn(List.of(record));

        assertFalse(performanceRecordService.findRecordsByAthleteIdAndMetric(1L, "Speed").isEmpty());
        verify(recordRepository, times(1)).findByAthleteIdAndMetricName(1L, "Speed");
    }

    @Test
    public void testFindRecordsByAthleteIdAndMetric_AthleteNotFound() {
        when(athleteRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            performanceRecordService.findRecordsByAthleteIdAndMetric(1L, "Speed");
        });
    }

    @Test
    public void testFindPersonalBestsByAthleteId() {
        when(athleteRepository.existsById(1L)).thenReturn(true);

        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[] {"Speed", 12.5});

        when(recordRepository.findPersonalBestsByAthleteId(1L))
                .thenReturn(mockResults);

        var personalBests = performanceRecordService.findPersonalBestsByAthleteId(1L);
        assertNotNull(personalBests);
        assertTrue(personalBests.containsKey("Speed"));
        assertEquals(12.5, personalBests.get("Speed"));
        verify(recordRepository, times(1)).findPersonalBestsByAthleteId(1L);
    }

    @Test
    public void testUpdateRecord() {
        PerformanceRecord updatedRecord = new PerformanceRecord();
        updatedRecord.setMetricName("New Speed");
        updatedRecord.setValue(12.0);
        updatedRecord.setDate(LocalDate.now());
        updatedRecord.setRemarks("Updated record");

        when(recordRepository.findById(1L)).thenReturn(Optional.of(record));
        when(recordRepository.save(any(PerformanceRecord.class))).thenReturn(updatedRecord);

        PerformanceRecord result = performanceRecordService.updateRecord(1L, updatedRecord);

        assertNotNull(result);
        assertEquals("New Speed", result.getMetricName());
        assertEquals(12.0, result.getValue());
        verify(recordRepository, times(1)).findById(1L);
        verify(recordRepository, times(1)).save(any(PerformanceRecord.class));
    }

    @Test
    public void testDeleteRecord() {
        when(recordRepository.existsById(1L)).thenReturn(true);
        doNothing().when(recordRepository).deleteById(1L);

        performanceRecordService.deleteRecord(1L);

        verify(recordRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteRecord_RecordNotFound() {
        when(recordRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            performanceRecordService.deleteRecord(1L);
        });
    }

    @Test
    public void testCompareAthletes() {
        Athlete athlete2 = new Athlete();
        athlete2.setId(2L);
        athlete2.setName("Jane Doe");

        PerformanceRecord record2 = new PerformanceRecord();
        record2.setAthlete(athlete2);
        record2.setMetricName("Speed");
        record2.setValue(13.5);
        record2.setDate(LocalDate.now());

        when(athleteRepository.findById(1L)).thenReturn(Optional.of(athlete));
        when(athleteRepository.findById(2L)).thenReturn(Optional.of(athlete2));
        when(recordRepository.findByAthleteIdAndMetricName(1L, "Speed")).thenReturn(List.of(record));
        when(recordRepository.findByAthleteIdAndMetricName(2L, "Speed")).thenReturn(List.of(record2));

        var comparison = performanceRecordService.compareAthletes(1L, 2L, "Speed");

        assertEquals(10.5, comparison.get("John Doe"));
        assertEquals(13.5, comparison.get("Jane Doe"));
    }
}