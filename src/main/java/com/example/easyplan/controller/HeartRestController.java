//package com.example.easyplan.controller;
//
//import com.example.easyplan.service.HeartService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@Slf4j
//@RequestMapping("/api/reviews")
//@RequiredArgsConstructor
//public class HeartRestController {
//    private final HeartService heartService;
//
//    @PostMapping("/{reviewId}/hearts")
//    public ResponseEntity<?> toggleHeart(@PathVariable("reviewId") Long reviewId){
//        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
//        if (userId == null || userId.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
//        }
//
//        heartService.toggleHeart(Long.valueOf(userId), reviewId);
//        return ResponseEntity.ok(Map.of("userId", userId,"reviewId",reviewId));
//    }
//
//    @GetMapping("/{reviewId}/heartCnt")
//    public ResponseEntity<?> getCntHeart(@PathVariable("reviewId") Long reviewId) {
//        Long heartCount = heartService.countHeart(reviewId);
//        return ResponseEntity.ok(Map.of("reviewId", reviewId, "heartCount", heartCount));
//    }
//
//    @GetMapping("/{reviewId}/status")
//    public ResponseEntity<?> getUserHeart(@PathVariable("reviewId") Long reviewId) {
//        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
//        if (userId == null || userId.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
//        }
//
//        boolean userHasHearted = heartService.userHasHearted(Long.parseLong(userId), reviewId);
//        return ResponseEntity.ok(Map.of("reviewId", reviewId, "userHasHearted", userHasHearted));
//    }
//
//
//
//}
