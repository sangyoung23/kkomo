package com.kkomo.kkomo_api.domain.integration;

import com.kkomo.kkomo_api.PetFixture;
import com.kkomo.kkomo_api.ShopFixture;
import com.kkomo.kkomo_api.UserFixture;
import com.kkomo.kkomo_api.domain.payment.Payment;
import com.kkomo.kkomo_api.domain.pet.Pet;
import com.kkomo.kkomo_api.domain.pet.PetRepository;
import com.kkomo.kkomo_api.domain.payment.PaymentRepository;
import com.kkomo.kkomo_api.domain.payment.PaymentService;
import com.kkomo.kkomo_api.domain.payment.PaymentStatus;
import com.kkomo.kkomo_api.domain.reservation.Reservation;
import com.kkomo.kkomo_api.domain.reservation.ReservationRepository;
import com.kkomo.kkomo_api.domain.reservation.ReservationService;
import com.kkomo.kkomo_api.domain.reservation.ReservationStatus;
import com.kkomo.kkomo_api.domain.reservation.dto.ReservationCreateRequest;
import com.kkomo.kkomo_api.domain.shop.Shop;
import com.kkomo.kkomo_api.domain.shop.ShopRepository;
import com.kkomo.kkomo_api.domain.timeslot.TimeSlot;
import com.kkomo.kkomo_api.domain.timeslot.TimeSlotRepository;
import com.kkomo.kkomo_api.domain.user.User;
import com.kkomo.kkomo_api.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class ReservationPaymentIntegrationTest {

    @Autowired ReservationService reservationService;
    @Autowired PaymentService paymentService;
    @Autowired ReservationRepository reservationRepository;
    @Autowired PaymentRepository paymentRepository;
    @Autowired UserRepository userRepository;
    @Autowired PetRepository petRepository;
    @Autowired ShopRepository shopRepository;
    @Autowired TimeSlotRepository timeSlotRepository;

    @Test
    @DisplayName("예약 생성 후 결제 성공 시 예약 상태가 CONFIRMED로 변경된다")
    void createReservationAndCompletePayment_shouldConfirmReservation() {
        // given
        User owner = userRepository.save(UserFixture.owner());
        Shop shop = shopRepository.save(ShopFixture.shop(owner));

        User customer = userRepository.save(UserFixture.customer());
        Pet pet = petRepository.save(PetFixture.pet(customer));

        LocalDateTime start = LocalDateTime.now().plusDays(3);

        TimeSlot timeSlot = timeSlotRepository.save(
                TimeSlot.create(shop, start, start.plusHours(1))
        );

        ReservationCreateRequest reservationRequest = new ReservationCreateRequest(
                customer.getId(),
                pet.getId(),
                shop.getId(),
                timeSlot.getId()
        );

        Long reservationId = reservationService.createReservation(reservationRequest);

        // when
        Payment payment = paymentService.requestPayment(reservationId);

        paymentService.completePayment(payment.getId());

        // then
        Reservation updatedReservation = reservationRepository.findById(reservationId).orElseThrow();
        Payment updatedPayment = paymentRepository.findById(payment.getId()).orElseThrow();

        assertThat(updatedReservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    @DisplayName("결제 실패 시 예약 상태가 CANCELLED로 변경된다")
    void failPayment_shouldCancelReservation() {
        // given
        User owner = userRepository.save(UserFixture.owner());
        Shop shop = shopRepository.save(ShopFixture.shop(owner));

        User customer = userRepository.save(UserFixture.customer());
        Pet pet = petRepository.save(PetFixture.pet(customer));

        LocalDateTime start = LocalDateTime.now().plusDays(3);

        TimeSlot timeSlot = timeSlotRepository.save(
                TimeSlot.create(shop, start, start.plusHours(1))
        );

        ReservationCreateRequest reservationRequest = new ReservationCreateRequest(
                customer.getId(),
                pet.getId(),
                shop.getId(),
                timeSlot.getId()
        );

        Long reservationId = reservationService.createReservation(reservationRequest);

        Payment payment = paymentService.requestPayment(reservationId);

        // when
        paymentService.failPayment(payment.getId());

        // then
        Reservation updatedReservation = reservationRepository.findById(reservationId).orElseThrow();
        Payment updatedPayment = paymentRepository.findById(payment.getId()).orElseThrow();

        assertThat(updatedReservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    @DisplayName("동시에 결제 요청이 들어와도 하나만 성공한다")
    void concurrentPayments_shouldOnlyAllowOneSuccess() throws InterruptedException {
        // given
        User owner = userRepository.save(UserFixture.owner());
        Shop shop = shopRepository.save(ShopFixture.shop(owner));

        User customer = userRepository.save(UserFixture.customer());
        Pet pet = petRepository.save(PetFixture.pet(customer));

        LocalDateTime start = LocalDateTime.now().plusDays(3);

        TimeSlot timeSlot = timeSlotRepository.save(
                TimeSlot.create(shop, start, start.plusHours(1))
        );

        ReservationCreateRequest reservationRequest = new ReservationCreateRequest(
                customer.getId(),
                pet.getId(),
                shop.getId(),
                timeSlot.getId()
        );

        Long reservationId = reservationService.createReservation(reservationRequest);

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    Payment payment = paymentService.requestPayment(reservationId);
                    paymentService.completePayment(payment.getId());
                    successCount.incrementAndGet();
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Reservation updatedReservation = reservationRepository.findById(reservationId).orElseThrow();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(updatedReservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }
}