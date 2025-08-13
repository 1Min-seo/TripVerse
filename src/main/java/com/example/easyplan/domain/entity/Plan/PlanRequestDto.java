package com.example.easyplan.domain.entity.Plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class PlanRequestDto {
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "시작일은 필수입니다.")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "종료일은 필수입니다.")
    private LocalDate endDate;
    private List<PlanDayDto> days;

    @Getter
    @Setter
    public static class PlanDayDto {
        private LocalDate date;
        private List<PlanPlaceDto> places;
    }

    @Getter
    @Setter
    public static class PlanPlaceDto {
        private String name;
        private LocalTime time;
        private String memo;
        private double latitude;
        private double longitude;
        private int placeOrder;
    }
}
