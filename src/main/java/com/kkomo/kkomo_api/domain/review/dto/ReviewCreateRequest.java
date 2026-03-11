package com.kkomo.kkomo_api.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateRequest {

    private Long reservationId;
    private String content;
    private int rating;
}
