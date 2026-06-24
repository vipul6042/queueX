package com.queueX.backend.repository;

import com.queueX.backend.entity.Job;
import com.queueX.backend.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findByJobStatus(JobStatus jobStatus);
}
