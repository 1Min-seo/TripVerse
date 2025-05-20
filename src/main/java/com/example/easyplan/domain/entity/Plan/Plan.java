package com.example.easyplan.domain.entity.Plan;

import com.example.easyplan.domain.entity.User.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@RequiredArgsConstructor
@Getter
public class Plan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanDay> days = new ArrayList<>();

    private Plan(User user, String title, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Plan createPlan(String title, LocalDate startDate, LocalDate endDate, User user) {
        validateTitle(title);
        validateStartDate(startDate);
        validateEndDate(endDate);

        return new Plan(user, title, startDate, endDate);
    }

    public void update(String title, LocalDate startDate, LocalDate endDate) {
        validateTitle(title);
        validateStartDate(startDate);
        validateEndDate(endDate);

        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void addDay(PlanDay day) {
        this.days.add(day);
        day.setPlan(this);
    }

    public void removeDay(PlanDay day) {
        this.days.remove(day);
        day.setPlan(null);
    }

    private static void validateTitle(String title){
        if(title == null || title.isEmpty()){
            throw new IllegalArgumentException("Please enter the title");
        }
    }

    private static void validateStartDate(LocalDate startDate){
        if(startDate == null){
            throw new IllegalArgumentException("Please enter the start date");
        }
    }

    private static void validateEndDate(LocalDate endDate){
        if(endDate == null){
            throw new IllegalArgumentException("Please enter the end date");
        }
    }
}
