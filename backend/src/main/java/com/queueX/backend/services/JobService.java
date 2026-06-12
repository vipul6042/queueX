package com.queueX.backend.services;

import com.queueX.backend.dto.CreateJobRequest;
import com.queueX.backend.entity.Job;
import com.queueX.backend.enums.JobStatus;
import com.queueX.backend.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;

    public Job jobRequest(@NonNull CreateJobRequest req){
        Job job=Job.builder()
                .jobType(req.jobType())
                .payload(req.payload())
                .jobStatus(JobStatus.QUEUED)
                .build();

        return jobRepository.save(job);
    }
}
