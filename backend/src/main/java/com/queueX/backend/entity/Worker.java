package com.queueX.backend.entity;

import com.queueX.backend.enums.WorkerStatus;
import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Data
@Builder
@Setter
@Table(name = "worker")
@NoArgsConstructor
@AllArgsConstructor
public class Worker {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true,nullable = false)
    String workerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkerStatus workerStatus;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private LocalDateTime registeredAt;

    @Timestamp
    @Column(nullable = false)
    private LocalDateTime lastDbHeartbeat;

    @PrePersist
    public void pre(){
        LocalDateTime now=LocalDateTime.now();
        this.lastDbHeartbeat=now;
        this.registeredAt=now;
        this.workerStatus=WorkerStatus.ONLINE;
    }
}
