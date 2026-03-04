package com.kkomo.kkomo_api.domain.payment;

import com.kkomo.kkomo_api.domain.payment.dto.PaymentRequest;
import com.kkomo.kkomo_api.domain.payment.dto.PaymentResponse;
import com.kkomo.kkomo_api.domain.payment.dto.RefundRequest;
import com.kkomo.kkomo_api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 요청
    @PostMapping
    public ApiResponse<PaymentResponse> requestPayment(@RequestBody PaymentRequest request) {
        Payment payment = paymentService.requestPayment(request.getReservationId(), request.getAmount());
        return ApiResponse.success(PaymentResponse.from(payment));
    }

//    // 결제 성공 (Mock PG 시뮬레이션용)
//    @PostMapping("/{paymentId}/success")
//    public ApiResponse<Void> completePayment(@PathVariable Long paymentId) {
//        paymentService.completePayment(paymentId);
//        return ApiResponse.success(null);
//    }
//
//    // 결제 실패
//    @PostMapping("/{paymentId}/fail")
//    public ApiResponse<Void> failPayment(@PathVariable Long paymentId) {
//        paymentService.failPayment(paymentId);
//        return ApiResponse.success(null);
//    }

    // 환불 처리
    @PostMapping("/{paymentId}/refund")
    public ApiResponse<Void> refundPayment(@PathVariable Long paymentId, @RequestBody RefundRequest request) {
        paymentService.refundPayment(paymentId, request.getAmount());
        return ApiResponse.success(null);
    }
}