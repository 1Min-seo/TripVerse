package com.example.easyplan.controller;

import com.example.easyplan.domain.entity.Preference.TravelPreferenceRequestDto;
import com.example.easyplan.domain.entity.Preference.TravelPreferenceResponseDto;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class TravelPreferenceController {
    private final TravelPreferenceService travelPreferenceService;
    private final GeminiService geminiService;

    @PostMapping("/travel-preferences")
    public ResponseEntity<TravelPreferenceResponseDto> saveTravelPreference(@RequestBody TravelPreferenceRequestDto travelPreferenceRequestDto) {

        Long userId = getUserId();
        TravelPreferenceResponseDto dto = travelPreferenceService.save(userId, travelPreferenceRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/travel-preferences/recommendation")
    public ResponseEntity<String> getTravelRecommendation(@RequestBody TravelPreferenceRequestDto requestDto) {
        try {
            String recommendation = geminiService.getTravelRecommendation(requestDto);
            return ResponseEntity.ok(recommendation);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("여행 추천 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    private Long getUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        return user.getUser().getId();
    }
}
