package com.example.easyplan.controller;

import com.example.easyplan.domain.entity.Plan.Plan;
import com.example.easyplan.domain.entity.Plan.PlanRequestDto;
import com.example.easyplan.domain.entity.User.User;
import com.example.easyplan.security.CustomOAuth2User;
import com.example.easyplan.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plans")
public class PlanRestController {
    private final PlanService planService;

    @PostMapping
    public ResponseEntity<Long> createPlan(@Valid @RequestBody PlanRequestDto planRequestDto) {
        Long userId = getUserId();
        Long planId = planService.createPlan(userId, planRequestDto);
        return ResponseEntity.ok(planId);
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
