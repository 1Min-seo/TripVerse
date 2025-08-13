package com.example.easyplan.controller;

import com.example.easyplan.domain.entity.Plan.Plan;
import com.example.easyplan.domain.entity.Plan.PlanRequestDto;
import com.example.easyplan.domain.entity.User.User;
import com.example.easyplan.security.CustomOAuth2User;
import com.example.easyplan.service.PlanService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plans")
public class PlanRestController {
    private final PlanService planService;

    @PostMapping
    public ResponseEntity<?> createPlan(
            @RequestBody @Valid PlanRequestDto planRequestDto,
            HttpServletRequest request) {

        System.out.println("=== Plan 생성 요청 시작 ===");
        System.out.println("Request Body: " + planRequestDto.getTitle());
        System.out.println("Days 수: " + planRequestDto.getDays().size());

        Long userId = getUserId();
        System.out.println("User ID: " + userId);

        if (userId == null) {
            System.out.println("인증 실패: userId가 null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "인증이 필요합니다."));
        }

        try {
            Long planId = planService.createPlan(userId, planRequestDto);
            System.out.println("Plan 생성 완료. ID: " + planId);

            Map<String, Object> response = Map.of(
                    "id", planId,
                    "title", planRequestDto.getTitle(),
                    "startDate", planRequestDto.getStartDate(),
                    "endDate", planRequestDto.getEndDate(),
                    "message", "여행 계획이 성공적으로 생성되었습니다."
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Plan 생성 중 오류: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "저장 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/{planId}")
    public ResponseEntity<Void> updatePlan(@Valid @PathVariable Long planId,
                                           @RequestBody PlanRequestDto planRequestDto) {
        Long userId = getUserId();
        planService.updatePlan(planId, userId, planRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long planId) {
        Long userId = getUserId();
        planService.deletePlan(planId, userId);
        return ResponseEntity.ok().build();
    }

    private Long getUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        return user.getUser().getId();
    }

}
