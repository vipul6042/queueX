package com.queueX.backend.services.job;

import com.queueX.backend.dto.CreateJobRequest;
import com.queueX.backend.dto.GetJobResponse;
import com.queueX.backend.dto.UpdateJobRequest;
import com.queueX.backend.entity.Job;
import com.queueX.backend.enums.JobStatus;
import com.queueX.backend.repository.JobRepository;
import com.queueX.backend.services.queues.QueueService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;
    private final QueueService queueService;

    public Job createJobRequest(@NonNull CreateJobRequest req){
        Job job=Job.builder()
                .jobType(req.jobType())
                .payload(req.payload())
                .jobStatus(JobStatus.QUEUED)
                .build();

        Job savedJob= jobRepository.save(job);
        queueService.push(savedJob.getId().toString());
        return savedJob;
    }

    public GetJobResponse getJob(UUID id) {
        Job job=jobRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("Job not found"));
        return new GetJobResponse(
                job.getId(),
                job.getJobType(),
                job.getPayload(),
                job.getJobStatus(),
                job.getRetryCount(),
                job.getErrorMessage()
        );
    }

    public List<GetJobResponse> getAllJob(){
        return jobRepository.findAll()
                .stream()
                .map(job -> new GetJobResponse(
                        job.getId(),
                        job.getJobType(),
                        job.getPayload(),
                        job.getJobStatus(),
                        job.getRetryCount(),
                        job.getErrorMessage()
                ))
                .toList();
    }

    public GetJobResponse updateJob(UUID id,UpdateJobRequest req){
        Job job=jobRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("Job not found"));
        job.setJobStatus(req.status());
        jobRepository.save(job);
        return new GetJobResponse(
                job.getId(),
                job.getJobType(),
                job.getPayload(),
                job.getJobStatus(),
                job.getRetryCount(),
                job.getErrorMessage()
        );
    }

    public void deleteJob(UUID id){
        Job job=jobRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("Job not found"));
        jobRepository.delete(job);
    }

    public List<GetJobResponse>failedJobs(){
        return jobRepository.findByJobStatus(JobStatus.FAILED)
                .stream()
                .map(job->new GetJobResponse(
                        job.getId(),
                        job.getJobType(),
                        job.getPayload(),
                        job.getJobStatus(),
                        job.getRetryCount(),
                        job.getErrorMessage()
                ))
                .toList();
    }

    public Job retry(UUID id){
        Job job=jobRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("Job not found"));

        if (job.getJobStatus() != JobStatus.FAILED) {
            throw new IllegalStateException("Only failed jobs can be retried");
        }

        job.setRetryCount(0);
        job.setErrorMessage(null);
        job.setJobStatus(JobStatus.QUEUED);
        Job savedJob=jobRepository.save(job);
        queueService.push(savedJob.getId().toString());
        return savedJob;
    }
}
