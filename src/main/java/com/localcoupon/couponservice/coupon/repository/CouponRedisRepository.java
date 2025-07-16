package com.localcoupon.couponservice.coupon.repository;

import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.common.infra.RedisProperties;
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

@Repository
@RequiredArgsConstructor
public class CouponRedisRepository {

    private final RedissonClient redissonClient;
    private final RedisProperties redisProperties;

    public <T> T executeWithLock(
            String lockKey,
            long waitSeconds, // 락을 얻기 위해 최대 몇초 대기
            long ttl, // 락을 잡은 뒤 자동으로 몇 초 뒤에 풀릴지
            Supplier<T> supplier
    ) {
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean locked = lock.tryLock(waitSeconds, ttl, TimeUnit.MILLISECONDS);
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

    public <T> Result saveData(String key, T value, Duration ttl) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        bucket.set(value, ttl);
        return Result.SUCCESS;
    }

    public <T> Optional<T> getValue(String key, Class<T> clazz) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return Optional.ofNullable(clazz.cast(bucket.get()));
    }

    public <T> boolean deleteData(String key) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.delete();
    }

    public Iterable<String> getAllOpenCouponKeys() {
        String pattern = redisProperties.couponOpenPrefix() + "*";
        return redissonClient.getKeys().getKeysByPattern(pattern);
    }



    public boolean exists(String key) {
        return redissonClient.getBucket(key).isExists();
    }

    public Optional<Integer> decreaseCouponStock(String key) {
        RBucket<String> bucket = redissonClient.getBucket(key);

        return Optional.of(bucket.get())
                .map(Integer::parseInt)
                .filter(CouponUtils::isStockCountPositive)
                .map(stock -> {
                    bucket.set(String.valueOf(stock -1));
                    return stock-1;
                });

    }
}
