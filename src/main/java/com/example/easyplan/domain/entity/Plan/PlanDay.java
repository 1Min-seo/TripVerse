package com.example.easyplan.domain.entity.Plan;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor  // 추가!
@Getter
@Setter
public class PlanDay {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @OneToMany(mappedBy = "planDay", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanPlace> places = new ArrayList<>();

    public static PlanDay createPlanDay(LocalDate date) {
        validateDate(date);

        PlanDay planDay = new PlanDay();
        planDay.setDate(date);
        return planDay;
    }

    public void addPlace(PlanPlace place) {
        this.places.add(place);
        place.setPlanDay(this);
    }

    public void removePlace(PlanPlace place) {
        this.places.remove(place);
        place.setPlanDay(null);
    }

    private static void validateDate(LocalDate date) {
        if(date == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
    }
}