//package com.kihyaa.Eiplanner.scheduler;
//
//import com.kihyaa.Eiplanner.service.TaskService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public  class  ScheduledTask {
//
//    final private TaskService taskService;
//
//    @Scheduled(cron = "0 0 0/1 * * ?") // 매일 00시를 시작으로 1시간마다 실행
////    @Scheduled(fixedDelay = 100000)
//    public void PerformTask () {
//
//        try {
//            log.info( "{}에 스케줄러 실행" , LocalDateTime.now());
//            taskService.scheduleTaskTypeRotation(LocalDateTime.now());
//
//        } catch (Exception e) {
//            log.error( "Error while task running" , e);
//        } }
//}