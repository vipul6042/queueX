package com.queueX.backend.controller;

import com.queueX.backend.dto.CreateJobRequest;
import com.queueX.backend.dto.CreateJobResponse;
import com.queueX.backend.dto.GetJobResponse;
import com.queueX.backend.dto.UpdateJobRequest;
import com.queueX.backend.entity.Job;
import com.queueX.backend.enums.JobStatus;
import com.queueX.backend.services.job.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor

public class JobController {
    private final JobService jobService;

    @PostMapping("/createJob")
    public ResponseEntity<CreateJobResponse> createJob(
            @RequestBody CreateJobRequest request
    ) {
        Job job = jobService.createJobRequest(request);
        CreateJobResponse res = new CreateJobResponse(
                job.getId(),
                job.getJobStatus().name()
        );
        return ResponseEntity.ok(res);
    }

    @GetMapping("/all")
    public ResponseEntity<List<GetJobResponse>> getAllJob() {
        List<GetJobResponse> jobs = jobService.getAllJob();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetJobResponse> getJob(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(jobService.getJob(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetJobResponse> updateJob(
            @PathVariable UUID id,
            @RequestBody JobStatus status
    ) {
        return ResponseEntity.ok(jobService.updateJob(id, new UpdateJobRequest(status)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(
            @PathVariable UUID id
    ) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/failedJobs")
    public ResponseEntity<List<GetJobResponse>>failedJobs(){
        return ResponseEntity.ok(jobService.failedJobs());
    }

    @PostMapping("/retry/{id}")
    public ResponseEntity<CreateJobResponse>retry(
            @PathVariable UUID id
    ){
        Job job=jobService.retry(id);
        return ResponseEntity.ok(new CreateJobResponse(id,job.getJobStatus().name()));
    }

}
