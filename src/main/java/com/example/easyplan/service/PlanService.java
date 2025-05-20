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
        System.out.print(plan.getTitle() + plan.getStartDate() + plan.getEndDate());
        for(PlanRequestDto.PlanDayDto dayDto: planRequestDto.getDays()) {
            PlanDay day = new PlanDay();
            day.setDate(dayDto.getDate());

            for(PlanRequestDto.PlanPlaceDto placeDto : dayDto.getPlaces()) {
                PlanPlace place = new PlanPlace();
                place.setName(placeDto.getName());
                place.setTime(placeDto.getTime());
                place.setMemo(placeDto.getMemo());
                place.setLatitude(placeDto.getLatitude());
                place.setLongitude(placeDto.getLongitude());
                place.setPlaceOrder(placeDto.getPlaceOrder());

                day.addPlace(place);
            }
            plan.addDay(day);
        }
        planRepository.save(plan);
        return plan.getId();
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
            PlanDay day = new PlanDay();
            day.setDate(dayDto.getDate());

            for (PlanRequestDto.PlanPlaceDto placeDto : dayDto.getPlaces()) {
                PlanPlace place = new PlanPlace();
                place.setName(placeDto.getName());
                place.setTime(placeDto.getTime());
                place.setMemo(placeDto.getMemo());
                place.setLatitude(placeDto.getLatitude());
                place.setLongitude(placeDto.getLongitude());
                place.setPlaceOrder(placeDto.getPlaceOrder());

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
