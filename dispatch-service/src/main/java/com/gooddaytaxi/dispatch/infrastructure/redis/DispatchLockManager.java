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

@Slf4j
@Component
@RequiredArgsConstructor
public class DispatchLockManager {

    private final RedissonClient redissonClient;
    private final RedisTemplate<String, String> redisTemplate;

    private static final long WAIT_TIME = 1;
    private static final long LEASE_TIME = 3; // 자동 해제

    /** 락 획득 */
    public boolean tryLock(String dispatchId, UUID driverId) {

        RLock lock = getLock(dispatchId);

        log.debug("[LOCK] 락 시도 - dispatchId={}, driverId={}", dispatchId, driverId);

        boolean acquired = false;
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

    /** 락 해제 */
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

    private RLock getLock(String dispatchId) {
        return redissonClient.getLock("dispatch:lock:" + dispatchId);
    }
}
