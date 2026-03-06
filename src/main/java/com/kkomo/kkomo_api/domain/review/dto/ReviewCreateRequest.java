package com.kkomo.kkomo_api.domain.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCreateRequest {

    private Long reservationId;
    private String content;
    private int rating;
}
