package com.kkomo.kkomo_api.domain.payment;

import com.kkomo.kkomo_api.PetFixture;
import com.kkomo.kkomo_api.ShopFixture;
import com.kkomo.kkomo_api.UserFixture;
import com.kkomo.kkomo_api.domain.pet.Pet;
import com.kkomo.kkomo_api.domain.pet.PetRepository;
import com.kkomo.kkomo_api.domain.reservation.Reservation;
import com.kkomo.kkomo_api.domain.reservation.ReservationRepository;
import com.kkomo.kkomo_api.domain.reservation.ReservationStatus;
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
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class PaymentConcurrencyTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PetRepository petRepository;

    @Autowired
    ShopRepository shopRepository;

    @Autowired
    TimeSlotRepository timeSlotRepository;

    @Test
    @DisplayName("동시에 결제 성공 요청이 들어와도 하나만 성공한다")
    void shouldCompletePaymentOnlyOnceUnderConcurrentRequests() throws Exception {

        // given
        User owner = userRepository.save(UserFixture.owner());
        Shop shop = shopRepository.save(ShopFixture.shop(owner));

        User customer = userRepository.save(UserFixture.customer());
        Pet pet = petRepository.save(PetFixture.pet(customer));

        TimeSlot timeSlot = timeSlotRepository.save(
                TimeSlot.create(
                        shop,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(1)
                )
        );

        Reservation reservation = reservationRepository.save(
                Reservation.create(customer, pet, shop, timeSlot, BigDecimal.valueOf(10000))
        );

        Payment payment = paymentRepository.save(
                Payment.create(reservation, false, BigDecimal.valueOf(10000))
        );

        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        Long paymentId = payment.getId();

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    paymentService.completePayment(paymentId);
                    successCount.incrementAndGet();
                } catch (ObjectOptimisticLockingFailureException e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // then
        Payment updatedPayment = paymentRepository.findById(paymentId).orElseThrow();
        Reservation updatedReservation = reservationRepository.findById(reservation.getId()).orElseThrow();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(updatedReservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }
}
