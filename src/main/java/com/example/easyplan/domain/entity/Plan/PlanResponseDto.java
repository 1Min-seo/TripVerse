package com.example.easyplan.domain.entity.Plan;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
public class PlanResponseDto {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<PlanDayResponseDto> days;

    @Builder
    public PlanResponseDto(String title, LocalDate startDate, LocalDate endDate, List<PlanDayResponseDto> days) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.days = days;
    }

    @Getter
    public static class PlanDayResponseDto {
        private LocalDate date;
        private List<PlanPlaceResponseDto> places;

        @Builder
        public PlanDayResponseDto(LocalDate date, List<PlanPlaceResponseDto> places) {
            this.date = date;
            this.places = places;
        }
    }

    @Getter
    public static class PlanPlaceResponseDto {
        private String name;
        private LocalTime time;
        private String memo;
        private double latitude;
        private double longitude;
        private int placeOrder;

        @Builder
        public PlanPlaceResponseDto(String name, LocalTime time, String memo, double latitude, double longitude, int placeOrder) {
            this.name = name;
            this.time = time;
            this.memo = memo;
            this.latitude = latitude;
            this.longitude = longitude;
            this.placeOrder = placeOrder;
        }
    }

}
