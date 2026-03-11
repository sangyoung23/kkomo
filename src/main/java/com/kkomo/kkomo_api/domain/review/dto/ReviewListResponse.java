package com.kkomo.kkomo_api.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
@AllArgsConstructor
public class ReviewListResponse {

    private double averageRating;
    private long reviewCount;
    private List<ReviewResponse> reviews;

}
