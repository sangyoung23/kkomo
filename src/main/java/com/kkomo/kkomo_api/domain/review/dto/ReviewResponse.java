package com.kkomo.kkomo_api.domain.review.dto;

import com.kkomo.kkomo_api.domain.review.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder
@AllArgsConstructor
public class ReviewResponse {

    private Long reviewId;
    private String userName;
    private String petName;
    private int rating;
    private String content;
    private LocalDateTime createdAt;

    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getId())
                .userName(review.getReservation().getUser().getName())
                .petName(review.getReservation().getPet().getName())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
