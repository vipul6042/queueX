package com.queueX.backend.services.worker;

import com.queueX.backend.entity.Worker;
import com.queueX.backend.enums.WorkerStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatchdogService {
    private final WorkerService workerService;
    private final RedisHeartbeatService redisHeartbeatService;

    @Scheduled(fixedDelay = 15000,initialDelay = 15000)
    public void scanWorkers(){
        List<Worker> workerList= workerService.getAllWorker();
        for(Worker worker :workerList){
            Boolean alive=redisHeartbeatService.isAlive(worker.getId());
            log.info(
                    "Worker={} Alive={} Status={}",
                    worker.getWorkerName(),
                    alive,
                    worker.getWorkerStatus()
            );
            if (alive && worker.getWorkerStatus()== WorkerStatus.OFFLINE) {
                workerService.markOnline(worker.getId());
                log.info("Worker {} is marked ONLINE",worker.getWorkerName());
            }
            else if(!alive && worker.getWorkerStatus()==WorkerStatus.ONLINE) {
                workerService.markOffline(worker.getId());
                log.error("Worker {} is marked OFFLINE",worker.getWorkerName());
            }
        }
    }
}
