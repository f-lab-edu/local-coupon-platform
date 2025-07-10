package com.localcoupon.couponservice.coupon.repository;

import com.localcoupon.couponservice.common.util.CouponUtils;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.localcoupon.couponservice.common.infra.RedisConstants.COUPON_OPEN_PREFIX;

@Repository
@RequiredArgsConstructor
public class CouponRedisRepository {

    private final RedissonClient redissonClient;

    public <T> T executeWithLock(
            String lockKey,
            long waitSeconds, // 락을 얻기 위해 최대 몇초 대기
            long ttl, // 락을 잡은 뒤 자동으로 몇 초 뒤에 풀릴지
            Supplier<T> supplier
    ) {
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean locked = lock.tryLock(waitSeconds, ttl, TimeUnit.SECONDS);
            if (!locked) {
                throw new UserCouponException(UserCouponErrorCode.COUPON_LOCK_FAILED);
            }

            return supplier.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UserCouponException(UserCouponErrorCode.COUPON_LOCK_FAILED);
        } finally {
            if (lock.isHeldByCurrentThread()) { //락이 현재 쓰레드가 주인인지 확인한다.
                lock.unlock();
            }
        }
    }

    public <T> void saveData(String key, T value, Duration ttl) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        bucket.set(value, ttl);
    }

    public <T> Optional<T> getValue(String key) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return Optional.ofNullable(bucket.get());
    }

    public <T> void deleteData(String key) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        bucket.delete();
    }

    public Iterable<String> getAllOpenCouponKeys() {
        String pattern = COUPON_OPEN_PREFIX + "*";
        return redissonClient.getKeys().getKeysByPattern(pattern);
    }



    public boolean exists(String key) {
        return redissonClient.getBucket(key).isExists();
    }

    public Optional<Integer> decreaseCouponStock(String key) {
        RBucket<Integer> bucket = redissonClient.getBucket(key);

        return Optional.ofNullable(bucket.get())
                .filter(CouponUtils::isStockCountPositive)
                .map(stock -> {
                    int updatedValue = stock - 1;
                    bucket.set(updatedValue);
                    return updatedValue;
                });
    }
}
