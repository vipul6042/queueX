package com.queueX.backend.services.worker;

import com.queueX.backend.entity.Worker;
import com.queueX.backend.entity.WorkerContext;
import com.queueX.backend.enums.WorkerStatus;
import com.queueX.backend.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkerService {
    private final WorkerRepository workerRepository;

    public Worker registerWorker(String workerName){
        Optional<Worker> optionalWorker=workerRepository.findByWorkerName(workerName);

        Worker worker;
        if(optionalWorker.isPresent()){
            worker = optionalWorker.get();
            worker.setWorkerStatus(WorkerStatus.ONLINE);
            worker.setLastDbHeartbeat(LocalDateTime.now());
        }else{
            worker = Worker.builder()
                    .workerName(workerName)
                    .build();
        }
        return workerRepository.save(worker);
    }

    public void updateDbHeartbeat(WorkerContext workerContext){
        workerRepository.updateDbHeartbeat(workerContext.id(),LocalDateTime.now());
    }

    public void markOffline(UUID id){
        Worker worker=workerRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("Worker not found"));

        worker.setWorkerStatus(WorkerStatus.OFFLINE);
        workerRepository.save(worker);
    }

    public void markOnline(UUID id){
        Worker worker=workerRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("Worker not found"));

        worker.setWorkerStatus(WorkerStatus.ONLINE);
        workerRepository.save(worker);
    }

    public List<Worker> getAllWorker(){
        return workerRepository.findByWorkerStatus(WorkerStatus.ONLINE);
    }

}
