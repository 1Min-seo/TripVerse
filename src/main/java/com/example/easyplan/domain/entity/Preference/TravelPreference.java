package com.example.easyplan.domain.entity.Preference;

import com.example.easyplan.domain.entity.BaseEntity;
import com.example.easyplan.domain.entity.User.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "travel_preferences")
@NoArgsConstructor
@Getter
public class TravelPreference extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String travelStyle;

    private String travelDuration;

    private String transportation;

    private String budget;

    private String destinationType;

    private String companion;

    private String priority;

    private String dailyPace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String aiRecommendation;

    private Boolean isBookmarked = false;

    private TravelPreference(User user, String travelStyle, String travelDuration, String transportation,
                             String budget, String destinationType, String companion,
                             String priority, String dailyPace) {

        this.user = user;
        this.travelStyle = travelStyle;
        this.travelDuration = travelDuration;
        this.transportation = transportation;
        this.budget = budget;
        this.destinationType = destinationType;
        this.companion = companion;
        this.priority = priority;
        this.dailyPace = dailyPace;
    }

    public static TravelPreference createPreference(User user, String travelStyle, String travelDuration, String transportation, String budget, String destinationType,
                                                    String companion, String priority, String dailyPace) {
        validatePreferenceFields(travelStyle, travelDuration, transportation, budget, destinationType, companion, priority, dailyPace);

        return new TravelPreference(user, travelStyle, travelDuration, transportation, budget, destinationType, companion,
                priority, dailyPace);

    }

    public void saveRecommendation(String recommendation) {
        this.aiRecommendation = recommendation;
    }

    public boolean hasRecommendation() {
        return this.aiRecommendation != null;
    }

    public void toggleBookmarked() {
        this.isBookmarked = !this.isBookmarked;
    }

    public String generatePreferenceHash() {
        return String.valueOf((travelStyle + travelDuration + transportation + budget +
                destinationType + companion + priority + dailyPace).hashCode());
    }

    private static void validatePreferenceFields(String travelStyle, String travelDuration, String transportation,
                                                 String budget, String destinationType, String companion,
                                                 String priority, String dailyPace) {
        if (travelStyle == null || travelStyle.trim().isEmpty()) {
            throw new IllegalArgumentException("여행 스타일은 필수입니다.");
        }
        if (travelDuration == null || travelDuration.trim().isEmpty()) {
            throw new IllegalArgumentException("여행 기간은 필수입니다.");
        }
        if (transportation == null || transportation.trim().isEmpty()) {
            throw new IllegalArgumentException("이동 수단은 필수입니다.");
        }
        if (budget == null || budget.trim().isEmpty()) {
            throw new IllegalArgumentException("예상 경비는 필수입니다.");
        }
        if (destinationType == null || destinationType.trim().isEmpty()) {
            throw new IllegalArgumentException("여행지 유형은 필수입니다.");
        }
        if (companion == null || companion.trim().isEmpty()) {
            throw new IllegalArgumentException("여행 동반자는 필수입니다.");
        }
        if (priority == null || priority.trim().isEmpty()) {
            throw new IllegalArgumentException("여행 중요도는 필수입니다.");
        }
        if (dailyPace == null || dailyPace.trim().isEmpty()) {
            throw new IllegalArgumentException("하루 일정을 선택해주세요.");
        }
    }
}
