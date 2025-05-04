package org.ncu.performancetracker;

import org.ncu.performancetracker.model.Athlete;
import org.ncu.performancetracker.model.PerformanceRecord;
import org.ncu.performancetracker.repository.AthleteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
public class PerformanceTrackerApplication implements CommandLineRunner {

    private final AthleteRepository athleteRepository;

    public PerformanceTrackerApplication(AthleteRepository athleteRepository) {
        this.athleteRepository = athleteRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(PerformanceTrackerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (athleteRepository.count() == 0) {
            Athlete john = new Athlete("John Doe", "Basketball", "Forward");
            Athlete jane = new Athlete("Jane Smith", "Soccer", "Goalkeeper");

            PerformanceRecord johnSpeed = new PerformanceRecord("Speed", 12.5, LocalDate.now(), "Good pace");
            PerformanceRecord johnAgility = new PerformanceRecord("Agility", 8.3, LocalDate.now(), "Improving");

            john.addPerformanceRecord(johnSpeed);
            john.addPerformanceRecord(johnAgility);

            PerformanceRecord janeReflex = new PerformanceRecord("Reflex", 9.1, LocalDate.now(), "Excellent reaction time");
            PerformanceRecord janeStamina = new PerformanceRecord("Stamina", 7.8, LocalDate.now(), "Needs improvement");

            jane.addPerformanceRecord(janeReflex);
            jane.addPerformanceRecord(janeStamina);

            athleteRepository.save(john);
            athleteRepository.save(jane);

            System.out.println("Sample athletes and performance records added to the database.");
        } else {
            System.out.println("Sample data already present.");
        }

        System.out.println("Performance Tracker is running!");
    }
}
