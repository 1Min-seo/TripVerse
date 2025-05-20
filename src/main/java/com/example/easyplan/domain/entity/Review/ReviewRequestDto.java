package com.example.easyplan.domain.entity.Review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @Min(1)
    @Max(5)
    private int rating;

    private List<String> photos;

    @NotBlank
    private Long heartCount;
}
