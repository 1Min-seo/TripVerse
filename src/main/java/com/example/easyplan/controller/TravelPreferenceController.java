package com.example.easyplan.controller;

import com.example.easyplan.domain.entity.Preference.TravelPreferenceRequestDto;
import com.example.easyplan.domain.entity.Preference.TravelPreferenceResponseDto;
import com.example.easyplan.domain.entity.Review.ReviewResponseDto;
import com.example.easyplan.security.CustomOAuth2User;
import com.example.easyplan.service.GeminiService;
import com.example.easyplan.service.TravelPreferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class TravelPreferenceController {
    private final TravelPreferenceService travelPreferenceService;
    private final GeminiService geminiService;

    @PostMapping("/travel-preferences")
    public ResponseEntity<TravelPreferenceResponseDto> saveTravelPreference(@RequestBody TravelPreferenceRequestDto requestDto) {

        Long userId = getUserId();
        TravelPreferenceResponseDto dto = travelPreferenceService.save(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/travel-preferences/{preferenceId}/recommendation")
    public ResponseEntity<String> getTravelRecommendation(@PathVariable("preferenceId") Long preferenceId, @RequestBody TravelPreferenceRequestDto requestDto) {
        try {
            log.info("성공1");
            String aiRecommendationText = geminiService.getTravelRecommendation(requestDto);
            log.info("성공2");
            travelPreferenceService.saveAiRecommendation(preferenceId, aiRecommendationText);
            log.info("성공3");
            return ResponseEntity.ok(aiRecommendationText);
        } catch (Exception e) {
            log.error("여행 추천 생성 및 저장 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("여행 추천 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/travel-preferences/{preferenceId}/recommendation")
    public ResponseEntity<TravelPreferenceResponseDto> getTravelRecommendation(@PathVariable("preferenceId") Long preferenceId) {
        TravelPreferenceResponseDto dto = travelPreferenceService.getRecommendation(preferenceId);

        log.info("추천 불러오기 성공: " + dto.getAiRecommendation());
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @GetMapping("/mypage/travel-preference")
    public ResponseEntity<List<TravelPreferenceResponseDto>> getAllTravelPreferences(
            @RequestParam(value = "page", defaultValue = "0") int page) {

        Long userId = getUserId();
        List<TravelPreferenceResponseDto> myAiResponse = travelPreferenceService.getAllRecommendations(userId, page);

        return ResponseEntity.status(HttpStatus.OK).body(myAiResponse);
    }

//    @GetMapping("/mypage/likes")
//    public ResponseEntity<List<ReviewResponseDto>> getLikedReviews(
//            @RequestParam(value = "page", defaultValue = "0") int page) {
//
//        System.out.println("내가 좋아요 누른 리뷰 요청 들어옴");
//        Long userId = getUserId();
//        List<ReviewResponseDto> likedReviews = heartService.getLikedReviews(userId, page);
//        System.out.println("좋아요 누른 개수 controller: " + likedReviews.size());
//        for(ReviewResponseDto review : likedReviews) {
//            System.out.println(review.getTitle());
//        }
//        return ResponseEntity.ok(likedReviews);
//    }


    private Long getUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        return user.getUser().getId();
    }
}
