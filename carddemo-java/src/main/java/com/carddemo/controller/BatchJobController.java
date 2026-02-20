package com.carddemo.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/batch")
public class BatchJobController {

    private final JobLauncher jobLauncher;
    private final Job dailyTransactionPostingJob;
    private final Job interestCalculationJob;

    public BatchJobController(JobLauncher jobLauncher,
                              @Qualifier("dailyTransactionPostingJob") Job dailyTransactionPostingJob,
                              @Qualifier("interestCalculationJob") Job interestCalculationJob) {
        this.jobLauncher = jobLauncher;
        this.dailyTransactionPostingJob = dailyTransactionPostingJob;
        this.interestCalculationJob = interestCalculationJob;
    }

    @PostMapping("/daily-transaction-posting")
    public ResponseEntity<Map<String, String>> runDailyTransactionPosting() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("runDate", LocalDate.now()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE))
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(dailyTransactionPostingJob, params);
            return ResponseEntity.ok(Map.of(
                    "status", "STARTED",
                    "message", "Daily transaction posting job has been started"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "FAILED",
                    "message", e.getMessage()));
        }
    }

    @PostMapping("/interest-calculation")
    public ResponseEntity<Map<String, String>> runInterestCalculation(
            @RequestParam(required = false) String parmDate) {
        try {
            String date = parmDate != null ? parmDate :
                    LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            JobParameters params = new JobParametersBuilder()
                    .addString("parmDate", date)
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(interestCalculationJob, params);
            return ResponseEntity.ok(Map.of(
                    "status", "STARTED",
                    "message", "Interest calculation job has been started"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "FAILED",
                    "message", e.getMessage()));
        }
    }
}
