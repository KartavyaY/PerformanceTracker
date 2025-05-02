package org.ncu.performancetracker.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.ncu.performancetracker.model.PerformanceRecord;
import org.ncu.performancetracker.model.Athlete;
import org.ncu.performancetracker.repository.PerformanceRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class PerformanceLoggingAspect {

    private final PerformanceRecordRepository recordRepository;
    private static final Logger logger = LoggerFactory.getLogger(PerformanceLoggingAspect.class);

    public PerformanceLoggingAspect(PerformanceRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    //Logs when a new record is added successful

    @AfterReturning(
            pointcut = "execution(* org.ncu.performancetracker.service.PerformanceRecordService.addRecordToAthlete(..))",
            returning = "savedRecord"
    )
    public void logPerformanceAddition(PerformanceRecord savedRecord) {
        if (savedRecord != null && savedRecord.getAthlete() != null) {
            String athleteName = savedRecord.getAthlete().getName();
            logger.info("New performance record added for athlete '{}': Metric='{}', Value={}",
                    athleteName, savedRecord.getMetricName(), savedRecord.getValue());
        }
    }

    //Checks for personal best and logs if a new one is achieved

    @Around("execution(* org.ncu.performancetracker.service.PerformanceRecordService.addRecordToAthlete(..))")
    public Object checkForPersonalBest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Long athleteId = (Long) args[0];
        PerformanceRecord newRecord = (PerformanceRecord) args[1];
        String metric = newRecord.getMetricName();

        // Get previous best
        List<PerformanceRecord> previousRecords = recordRepository.findByAthleteIdAndMetricName(athleteId, metric);
        double previousBest = previousRecords.stream()
                .mapToDouble(PerformanceRecord::getValue)
                .max()
                .orElse(0.0);

        // Proceed with the actual method
        PerformanceRecord result = (PerformanceRecord) joinPoint.proceed();

        // If it's a new personal best
        if (result.getValue() > previousBest) {
            logger.info("🎉 New personal best for '{}': Metric='{}', New Value={}, Previous Best={}",
                    result.getAthlete().getName(), metric, result.getValue(), previousBest);
        }

        return result;
    }
}
