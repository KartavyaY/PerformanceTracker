package org.ncu.performancetracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.ncu.performancetracker.model.Athlete;
import org.ncu.performancetracker.repository.AthleteRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AthleteService {

    private final AthleteRepository athleteRepository;

    @Autowired
    public AthleteService(AthleteRepository athleteRepository) {
        this.athleteRepository = athleteRepository;
    }

    public List<Athlete> findAllAthletes() {
        return athleteRepository.findAll();
    }

    public Optional<Athlete> findAthleteById(Long id) {
        return athleteRepository.findById(id);
    }

    @Transactional
    public Athlete saveAthlete(Athlete athlete) {
        return athleteRepository.save(athlete);
    }

    @Transactional
    public List<Athlete> saveAthletes(List<Athlete> athletes) {
        return athleteRepository.saveAll(athletes);
    }

    @Transactional
    public void deleteAthlete(Long id) {
        athleteRepository.deleteById(id);
    }
}