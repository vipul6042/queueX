package com.queueX.backend.repository;

import com.queueX.backend.entity.Worker;
import com.queueX.backend.enums.WorkerStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, UUID> {
    Optional<Worker>findByWorkerName(String workerName);

    @Modifying
    @Transactional
    @Query("""
                    UPDATE Worker w
                    set w.lastDbHeartbeat=:heartbeat
                    where w.id=:workerId
            """)
    void updateDbHeartbeat(UUID workerId, LocalDateTime heartbeat);

    List<Worker> findByWorkerStatus(WorkerStatus workerStatus);}
