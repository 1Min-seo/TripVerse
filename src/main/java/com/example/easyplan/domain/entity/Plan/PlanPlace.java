package com.example.easyplan.domain.entity.Plan;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@NoArgsConstructor  // 추가!
@Getter
@Setter
public class PlanPlace {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalTime time;
    private String memo;
    private int placeOrder;
    private double latitude;
    private double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="plan_day_id")
    private PlanDay planDay;

    public static PlanPlace createPlanPlace(String name, LocalTime time, String memo, int placeOrder, double latitude, double longitude) {
        validateName(name);
        PlanPlace planPlace = new PlanPlace();
        planPlace.setName(name);
        planPlace.setTime(time);
        planPlace.setMemo(memo);
        planPlace.setPlaceOrder(placeOrder);
        planPlace.setLatitude(latitude);
        planPlace.setLongitude(longitude);
        return planPlace;
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {  // trim() 추가
            throw new IllegalArgumentException("장소를 적어주세요.");
        }
    }
}