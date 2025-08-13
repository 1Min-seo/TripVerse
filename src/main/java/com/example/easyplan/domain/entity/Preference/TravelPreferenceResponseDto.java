package com.example.easyplan.domain.entity.Preference;

import com.example.easyplan.domain.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TravelPreferenceResponseDto{

    private Long preferenceId;
    private String travelStyle;
    private String travelDuration;
    private String transportation;
    private String budget;
    private String destinationType;
    private String companion;
    private String priority;
    private String dailyPace;
    private String aiRecommendation;
    private Boolean isBookmarked;

    //엔티티 -> dto
    public static TravelPreferenceResponseDto from(TravelPreference travelPreference) {
        return new TravelPreferenceResponseDto(
                travelPreference.getId(),
                travelPreference.getTravelStyle(),
                travelPreference.getTravelDuration(),
                travelPreference.getTransportation(),
                travelPreference.getBudget(),
                travelPreference.getDestinationType(),
                travelPreference.getCompanion(),
                travelPreference.getPriority(),
                travelPreference.getDailyPace(),
                travelPreference.getAiRecommendation(),
                travelPreference.getIsBookmarked()
        );
    }

}
