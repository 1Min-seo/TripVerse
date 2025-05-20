//package com.example.easyplan.controller;
//
//import com.example.easyplan.domain.entity.Review.Review;
//import com.example.easyplan.domain.entity.User.User;
//import com.example.easyplan.service.ReviewService;
//import com.example.easyplan.service.ScrapService;
//import com.example.easyplan.service.UserService;
//import lombok.Getter;
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
//@Getter
//@RequiredArgsConstructor
//@RequestMapping("/api")
//@Slf4j
//public class ScrapRestController {
//    private final UserService userService;
//    private final ReviewService reviewService;
//    private final ScrapService scrapService;
//
//    @PostMapping("/reviews/scrap/{reviewId}")
//    public ResponseEntity<?> addScrap(@PathVariable("reviewId") Long reviewId){
//        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
//        if (userId == null || userId.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
//        }
//
//        scrapService.addScrap(Long.valueOf(userId), reviewId);
//        return ResponseEntity.ok(Map.of("userId", userId,"reviewId",reviewId));
//    }
//
//    @GetMapping("/reviews/scrap/{reviewId}/status")
//    public ResponseEntity<?> getScrapStatus(@PathVariable("reviewId") Long reviewId){
//        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
//        if (userId == null || userId.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
//        }
//
//        Boolean isScrapped= scrapService.isScrapped(Long.valueOf(userId), reviewId);
//        return ResponseEntity.ok(Map.of("userId", userId,"reviewId",reviewId, "isScrapped",isScrapped));
//
//    }
//
//}
