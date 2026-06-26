//package com.queueX.backend.scheduler;
//
//import com.queueX.backend.services.WorkerService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//
//public class JobScheduler {
//    private final WorkerService workerService;
//
//    @Scheduled(fixedDelay = 1000)
//    public void processJobs() {
//        workerService.processNextJob();
//    }
//
//
//}
