package com.kkomo.kkomo_api.domain.review;

import com.kkomo.kkomo_api.domain.reservation.Reservation;
import com.kkomo.kkomo_api.domain.reservation.ReservationRepository;
import com.kkomo.kkomo_api.domain.review.dto.ReviewCreateRequest;
import com.kkomo.kkomo_api.domain.review.dto.ReviewListResponse;
import com.kkomo.kkomo_api.domain.review.dto.ReviewResponse;
import com.kkomo.kkomo_api.domain.shop.ShopRepository;
import com.kkomo.kkomo_api.global.exception.BusinessException;
import com.kkomo.kkomo_api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;
    private final ShopRepository shopRepository;

    // 리뷰 조회
    public ReviewListResponse getReviews(Long shopId) {

        List<Review> reviews = reviewRepository.findByReservationShopId(shopId);

        double avgRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0);

        List<ReviewResponse> reviewResponses = reviews.stream()
                .map(ReviewResponse::from)
                .toList();

        return ReviewListResponse.builder()
                .averageRating(avgRating)
                .reviewCount(reviews.size())
                .reviews(reviewResponses)
                .build();
    }

    // 리뷰 작성
    @Transactional
    public void createReview(ReviewCreateRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        Review review = Review.create(request.getContent(), request.getRating(), reservation);

        reviewRepository.save(review);
    }
}
