package org.ncu.performancetracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ncu.performancetracker.model.Athlete;
import org.ncu.performancetracker.repository.AthleteRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AthleteServiceTest {

    @Mock
    private AthleteRepository athleteRepository;

    @InjectMocks
    private AthleteService athleteService;

    private Athlete athlete1;
    private Athlete athlete2;
    private List<Athlete> athletes;

    @BeforeEach
    public void setup() {
        // Create test athletes
        athlete1 = new Athlete();
        athlete1.setId(1L);
        athlete1.setName("John Smith");
        athlete1.setSport("Basketball");
        athlete1.setPosition("Forward");

        athlete2 = new Athlete();
        athlete2.setId(2L);
        athlete2.setName("Jane Doe");
        athlete2.setSport("Swimming");
        athlete2.setPosition("Freestyle");

        athletes = Arrays.asList(athlete1, athlete2);

    }

    @Test
    public void testFindAllAthletes() {
        // Given
        when(athleteRepository.findAll()).thenReturn(athletes);

        // When
        List<Athlete> result = athleteService.findAllAthletes();

        // Then
        assertEquals(2, result.size());
        assertEquals("John Smith", result.get(0).getName());
        assertEquals("Jane Doe", result.get(1).getName());
        verify(athleteRepository, times(1)).findAll();
    }

    @Test
    public void testFindAthleteById() {
        // Given
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(athlete1));

        // When
        Optional<Athlete> result = athleteService.findAthleteById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("John Smith", result.get().getName());
        verify(athleteRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindAthleteById_NotFound() {
        // Given
        when(athleteRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<Athlete> result = athleteService.findAthleteById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(athleteRepository, times(1)).findById(999L);
    }

    @Test
    public void testFindAthletesByName() {
        // Given
        when(athleteRepository.findAthletesByName("John Smith")).thenReturn(List.of(athlete1));

        // When
        List<Athlete> result = athleteService.findAthletesByName("John Smith");

        // Then
        assertEquals(1, result.size());
        assertEquals("John Smith", result.getFirst().getName());
        verify(athleteRepository, times(1)).findAthletesByName("John Smith");
    }

    @Test
    public void testSaveAthlete() {
        // Given
        Athlete newAthlete = new Athlete();
        newAthlete.setName("New Athlete");
        newAthlete.setSport("Tennis");
        newAthlete.setPosition("Singles");

        when(athleteRepository.save(any(Athlete.class))).thenReturn(newAthlete);

        // When
        Athlete result = athleteService.saveAthlete(newAthlete);

        // Then
        assertNotNull(result);
        assertEquals("New Athlete", result.getName());
        assertEquals("Tennis", result.getSport());
        assertEquals("Singles", result.getPosition());
        verify(athleteRepository, times(1)).save(newAthlete);
    }

    @Test
    public void testSaveAthletes() {
        // Given
        when(athleteRepository.saveAll(anyList())).thenReturn(athletes);

        // When
        List<Athlete> result = athleteService.saveAthletes(athletes);

        // Then
        assertEquals(2, result.size());
        verify(athleteRepository, times(1)).saveAll(athletes);
    }

    @Test
    public void testDeleteAthlete() {
        // Given
        doNothing().when(athleteRepository).deleteById(anyLong());

        // When
        athleteService.deleteAthlete(1L);

        // Then
        verify(athleteRepository, times(1)).deleteById(1L);
    }
}