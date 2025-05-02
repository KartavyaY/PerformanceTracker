package org.ncu.performancetracker.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.ncu.performancetracker.model.Athlete;
import org.ncu.performancetracker.service.AthleteService;

import java.util.List;

@RestController
@RequestMapping("/api/athletes")
public class AthleteController {

    private final AthleteService athleteService;

    @Autowired
    public AthleteController(AthleteService athleteService) {
        this.athleteService = athleteService;
    }

    @GetMapping
    public ResponseEntity<List<Athlete>> getAllAthletes() {
        List<Athlete> athletes = athleteService.findAllAthletes();
        return ResponseEntity.ok(athletes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Athlete> getAthleteById(@PathVariable Long id) {
        return athleteService.findAthleteById(id)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Athlete> createAthlete(@Valid @RequestBody Athlete athlete) {
        Athlete createdAthlete = athleteService.saveAthlete(athlete);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAthlete);
    }

    @PostMapping("/listsave")
    public ResponseEntity<List<Athlete>>  saveAthletes(@RequestBody List<Athlete> athletes) {
        List<Athlete> createdAthletes = athleteService.saveAthletes(athletes);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAthletes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Athlete> updateAthlete(@PathVariable Long id, @Valid @RequestBody Athlete athlete) {
        return athleteService.findAthleteById(id)
                .map(existingAthlete -> {
                    athlete.setId(id);
                    return ResponseEntity.ok(athleteService.saveAthlete(athlete));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAthlete(@PathVariable Long id) {
        return athleteService.findAthleteById(id)
                .map(athlete -> {
                    athleteService.deleteAthlete(id);
                    return ResponseEntity.noContent().<Void>build();
                }).orElse(ResponseEntity.notFound().build());
    }
}