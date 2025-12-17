package com.gooddaytaxi.dispatch.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Dispatch 단위의 동시성 제어를 담당하는 분산 락 관리자.
 *
 * 여러 기사가 동시에 동일한 배차를 수락(Accept)하려는 상황에서
 * 단 하나의 기사만 배차를 확정할 수 있도록 Redisson 기반 분산 락을 제공한다.
 *
 * 수락(Accept)은 Dispatch의 상태 전이가 동시에 이루어질 위험이 있기에 락이 필요하지만,
 * 거절(Reject)은 개별 기사 응답 처리이므로 수락 시에만 락이 사용된다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DispatchLockManager {

    private final RedissonClient redissonClient;
    private final RedisTemplate<String, String> redisTemplate;

    private static final long WAIT_TIME = 1;
    private static final long LEASE_TIME = 3; // 자동 해제

    /**
     * 특정 배차(dispatchId)에 대한 분산 락 획득을 시도
     *
     * @param dispatchId 락 대상이 되는 배차 식별자
     * @param driverId 락 획득을 시도하는 기사 식별자
     * @return 락 획득 성공 시 true, 실패 또는 인터럽트 발생 시 false
     */
    public boolean tryLock(String dispatchId, UUID driverId) {

        RLock lock = getLock(dispatchId);

        log.debug("[LOCK] 락 시도 - dispatchId={}, driverId={}", dispatchId, driverId);

        final boolean acquired;
        try {
            acquired = lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[LOCK] 인터럽트 발생 - dispatchId={}, driverId={}", dispatchId, driverId);
            return false;
        }

        if (acquired) {
            log.info("[LOCK] 락 획득 성공 - dispatchId={}, driverId={}", dispatchId, driverId);

            redisTemplate.opsForValue().set(
                    "dispatch:lock:monitor:" + dispatchId,
                    "ACQUIRED_BY:" + driverId,
                    Duration.ofSeconds(LEASE_TIME)
            );

        } else {
            log.warn("[LOCK] 락 획득 실패 - dispatchId={}, driverId={}", dispatchId, driverId);

            redisTemplate.opsForValue().set(
                    "dispatch:lock:monitor:" + dispatchId,
                    "FAILED_TO_ACQUIRE:" + driverId,
                    Duration.ofSeconds(2)
            );
        }

        return acquired;
    }

    /**
     * 현재 처리 중인 요청이 보유한 배차 락을 해제한다.
     * 락은 락을 획득한 요청에서만 해제할 수 있으며,
     * 다른 요청에서 호출될 경우 아무 동작도 하지 않는다.
     *
     * @param dispatchId 락 해제할 배차 식별자
     */
    public void unlock(String dispatchId) {
        RLock lock = getLock(dispatchId);

        if (lock.isHeldByCurrentThread()) {
            lock.unlock();

            log.info("[LOCK] 락 해제 완료 - dispatchId={}", dispatchId);

            redisTemplate.opsForValue().set(
                    "dispatch:lock:monitor:" + dispatchId,
                    "RELEASED",
                    Duration.ofSeconds(2)
            );
        } else {
            log.debug("[LOCK] 현재 스레드가 락을 보유하고 있지 않음 - dispatchId={}", dispatchId);
        }
    }

    /**
     * 동일한 배차(dispatchId)에 대해 어떤 서버, 어떤 요청이 오더라도
     * 락의 상태를 모두 동일하게 유지하기 위해 사용된다.
     *
     * 특정 배차가 처리 중일 경우 다른 요청들은
     * "이미 처리 중"(잠겨 있음)으로 인식하고 접근하지 못하도록 한다.
     *
     * @param dispatchId 락을 구분하기 위한 배차 식별자
     * @return 해당 배차(dispatchId)에 대한 분산 락을 조작할 수 있는 RLock 객체
     */
    private RLock getLock(String dispatchId) {
        return redissonClient.getLock("dispatch:lock:" + dispatchId);
    }
}
