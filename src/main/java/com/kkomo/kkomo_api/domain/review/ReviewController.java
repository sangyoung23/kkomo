package com.kkomo.kkomo_api.domain.review;

import com.kkomo.kkomo_api.domain.review.dto.ReviewCreateRequest;
import com.kkomo.kkomo_api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 생성
    @PostMapping
    public ApiResponse<Void> createReview(@RequestBody ReviewCreateRequest request) {
        reviewService.createReview(request);

        return ApiResponse.success();
    }
}
