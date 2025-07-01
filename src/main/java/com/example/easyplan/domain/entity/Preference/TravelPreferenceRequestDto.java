package com.example.easyplan.domain.entity.Preference;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TravelPreferenceRequestDto {

    @NotBlank(message = "여행 스타일을 선택해주세요.")
    private String travelStyle;

    @NotBlank(message = "여행 기간을 선택해주세요.")
    private String travelDuration;

    @NotBlank(message = "이동 수단을 선택해주세요.")
    private String transportation;

    @NotBlank(message = "예상 경비를 선택해주세요.")
    private String budget;

    @NotBlank(message = "여행지 유형을 선택해주세요.")
    private String destinationType;

    @NotBlank(message = "여행 동반자를 선택해주세요.")
    private String companion;

    @NotBlank(message = "여행 중요도를 선택해주세요.")
    private String priority;

    @NotBlank(message = "하루 일정을 선택해주세요.")
    private String dailyPace;

}
