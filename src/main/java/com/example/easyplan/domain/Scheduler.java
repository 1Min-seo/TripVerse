//package com.example.easyplan.domain;
//
//import com.example.easyplan.service.schedule.ScheduleService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//@EnableScheduling
//@RequiredArgsConstructor
//@Log4j2
//public class Scheduler {
//
//    private final ScheduleService scheduleService;
//
//    @Scheduled(cron = "30 37 23 * * *")
//    public void sendEmailScheduler(){
//        log.info("scheduler 시작");
//        scheduleService.sendMails();
//        log.info("scheduler 완료");
//    }
//}
