package com.queueX.backend.services.job;

import com.queueX.backend.entity.Job;
import com.queueX.backend.entity.WorkerContext;
import com.queueX.backend.enums.JobStatus;
import com.queueX.backend.repository.JobRepository;
import com.queueX.backend.services.queues.DeadLetterQueueService;
import com.queueX.backend.services.queues.DelayedQueueService;
import com.queueX.backend.services.queues.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j

public class JobProcessingService {
    private final QueueService queueService;
    private final DelayedQueueService delayedQueueService;
    private final DeadLetterQueueService deadLetterQueueService;
    private final JobRepository jobRepository;
    private final Random random = new Random();
    private static final int MAX_RETRIES = 2;
    private static final int failureChance = 50;

    public void processNextJob(WorkerContext workerContext){
        String jobId = queueService.pop();
        if (jobId == null) {
            return;
        }
        log.info("Worker is processing job{} ", jobId);
        Job job = jobRepository.findById(UUID.fromString(jobId))
                .orElseThrow(() -> new IllegalStateException("Job not found: " + jobId));

        try {
            job.setJobStatus(JobStatus.RUNNING);
            jobRepository.save(job);

            log.info("Job {} status changed to RUNNING", job.getId());

            Thread.sleep(5000);

            if (random.nextInt(100) < failureChance) {
                throw new RuntimeException(job.getJobType() + " service unavailable");
            }

            job.setJobStatus(JobStatus.COMPLETED);
            jobRepository.save(job);

            log.info("Job {} completed successfully", job.getId());

        } catch (Exception e) {
            if (job.getRetryCount() < MAX_RETRIES) {
                job.setRetryCount(job.getRetryCount() + 1);
                job.setJobStatus(JobStatus.QUEUED);
                jobRepository.save(job);
                long delay = (long) Math.pow(2, job.getRetryCount() - 1) * 5000;
                long retryAt = System.currentTimeMillis() + delay;
                delayedQueueService.push(jobId, retryAt);

                log.warn(
                        "Retrying job {}. Attempt {}",
                        job.getId(),
                        job.getRetryCount()
                );
            } else {
                job.setJobStatus(JobStatus.FAILED);
                job.setErrorMessage(e.getMessage());
                jobRepository.save(job);
                deadLetterQueueService.push(jobId);
                log.error(
                        "Job {} permanently failed after {} retries",
                        job.getId(),
                        MAX_RETRIES,
                        e
                );
            }
        }

    }
}
