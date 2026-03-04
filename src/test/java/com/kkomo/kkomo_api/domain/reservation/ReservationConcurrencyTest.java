package com.kkomo.kkomo_api.domain.reservation;

import com.kkomo.kkomo_api.PetFixture;
import com.kkomo.kkomo_api.ShopFixture;
import com.kkomo.kkomo_api.UserFixture;
import com.kkomo.kkomo_api.domain.pet.Pet;
import com.kkomo.kkomo_api.domain.pet.PetRepository;
import com.kkomo.kkomo_api.domain.reservation.dto.ReservationCreateRequest;
import com.kkomo.kkomo_api.domain.shop.Shop;
import com.kkomo.kkomo_api.domain.shop.ShopRepository;
import com.kkomo.kkomo_api.domain.timeslot.TimeSlot;
import com.kkomo.kkomo_api.domain.timeslot.TimeSlotRepository;
import com.kkomo.kkomo_api.domain.timeslot.TimeSlotStatus;
import com.kkomo.kkomo_api.domain.user.User;
import com.kkomo.kkomo_api.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class ReservationConcurrencyTest {

    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    TimeSlotRepository timeSlotRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PetRepository petRepository;

    @Autowired
    ShopRepository shopRepository;

    @Test
    @DisplayName("동일한 타임슬롯에 동시에 예약 요청이 들어와도 최종적으로 하나만 생성된다")
    void shouldCreateOnlyOneReservationUnderConcurrentRequests() throws Exception {

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

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();

        Long customerId = customer.getId();
        Long petId = pet.getId();
        Long shopId = shop.getId();
        Long timeSlotId = timeSlot.getId();

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    ReservationCreateRequest request =
                            new ReservationCreateRequest(
                                    customerId,
                                    petId,
                                    shopId,
                                    timeSlotId
                            );

                    reservationService.createReservation(request);
                    successCount.incrementAndGet();

                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // then
        assertThat(reservationRepository.count()).isEqualTo(1);
        assertThat(successCount.get()).isEqualTo(1);
        TimeSlot updated = timeSlotRepository.findById(timeSlotId).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(TimeSlotStatus.RESERVED);
    }
}