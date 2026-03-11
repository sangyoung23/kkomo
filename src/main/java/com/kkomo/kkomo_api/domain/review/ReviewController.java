package com.kkomo.kkomo_api.domain.review;

import com.kkomo.kkomo_api.domain.review.dto.ReviewCreateRequest;
import com.kkomo.kkomo_api.domain.review.dto.ReviewListResponse;
import com.kkomo.kkomo_api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 조회
    @GetMapping
    public ApiResponse<ReviewListResponse> getReviews(@RequestParam Long shopId) {
        return ApiResponse.success(reviewService.getReviews(shopId));
    }

    // 리뷰 생성
    @PostMapping
    public ApiResponse<Void> createReview(@RequestBody ReviewCreateRequest request) {
        reviewService.createReview(request);

        return ApiResponse.success();
    }
}
