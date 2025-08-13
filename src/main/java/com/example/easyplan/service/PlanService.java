package com.example.easyplan.service;

import com.example.easyplan.domain.entity.Plan.*;
import com.example.easyplan.domain.entity.User.User;
import com.example.easyplan.repository.UserRepository;
import com.example.easyplan.repository.plan.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanService {
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    @Transactional
    public Long createPlan(Long userId, PlanRequestDto planRequestDto) {
        User user = getUserById(userId);
        Plan plan = Plan.createPlan(planRequestDto.getTitle(), planRequestDto.getStartDate(), planRequestDto.getEndDate(), user);

        System.out.println("Plan 생성: " + plan.getTitle() + ", " + plan.getStartDate() + ", " + plan.getEndDate());

        for(PlanRequestDto.PlanDayDto dayDto: planRequestDto.getDays()) {
            PlanDay day = PlanDay.createPlanDay(dayDto.getDate());  // static 메서드 사용

            System.out.println("Day 생성: " + day.getDate() + ", Places 수: " + dayDto.getPlaces().size());

            for(PlanRequestDto.PlanPlaceDto placeDto : dayDto.getPlaces()) {
                // 빈 장소 이름 필터링
                if(placeDto.getName() == null || placeDto.getName().trim().isEmpty()) {
                    System.out.println("빈 장소 이름 건너뛰기");
                    continue;
                }

                PlanPlace place = PlanPlace.createPlanPlace(
                        placeDto.getName().trim(),
                        placeDto.getTime(),
                        placeDto.getMemo(),
                        placeDto.getPlaceOrder(),
                        placeDto.getLatitude(),
                        placeDto.getLongitude()
                );

                System.out.println("Place 생성: " + place.getName());
                day.addPlace(place);
            }

            plan.addDay(day);
        }

        Plan savedPlan = planRepository.save(plan);

        System.out.println("Plan 저장 완료. ID: " + savedPlan.getId());
        System.out.println("Plan 저장 완료. title: " + savedPlan.getTitle());

        return savedPlan.getId();
    }

    @Transactional
    public void updatePlan(Long planId, Long userId, PlanRequestDto planRequestDto) {
        Plan plan = getPlanById(planId);
        if(!plan.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Unauthorized access");
        }

        plan.update(planRequestDto.getTitle(), planRequestDto.getStartDate(), planRequestDto.getEndDate());
        plan.getDays().clear();

        for (PlanRequestDto.PlanDayDto dayDto : planRequestDto.getDays()) {
            PlanDay day = PlanDay.createPlanDay(dayDto.getDate());

            for (PlanRequestDto.PlanPlaceDto placeDto : dayDto.getPlaces()) {
                if(placeDto.getName() == null || placeDto.getName().trim().isEmpty()) {
                    continue;
                }

                PlanPlace place = PlanPlace.createPlanPlace(
                        placeDto.getName().trim(),
                        placeDto.getTime(),
                        placeDto.getMemo(),
                        placeDto.getPlaceOrder(),
                        placeDto.getLatitude(),
                        placeDto.getLongitude()
                );

                day.addPlace(place);
            }

            plan.addDay(day);
        }
    }

    @Transactional
    public void deletePlan(Long planId, Long userId) {
        Plan plan = getPlanById(planId);
        if(!plan.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Unauthorized access");
        }
        planRepository.delete(plan);
    }

    private Plan getPlanById(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}