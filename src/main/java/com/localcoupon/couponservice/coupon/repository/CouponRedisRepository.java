package com.localcoupon.couponservice.coupon.repository;

import com.localcoupon.couponservice.common.enums.CommonErrorCode;
import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.common.exception.CommonException;
import com.localcoupon.couponservice.common.infra.RedisProperties;
import com.localcoupon.couponservice.common.util.CouponUtils;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        try {
            RBucket<T> bucket = redissonClient.getBucket(key);
            bucket.set(value, ttl);
            return Result.SUCCESS;
        } catch (Exception e) {
            log.error("[Redis] Failed to save key: {}, value: {}", key, value, e);
            throw new CommonException(CommonErrorCode.REDIS_OPERATION_ERROR);
        }
    }


    public <T> Optional<T> getValue(String key, Class<T> clazz) {
        try {
            RBucket<T> bucket = redissonClient.getBucket(key);
            return Optional.ofNullable(clazz.cast(bucket.get()));
        } catch (Exception e) {
            log.error("[Redis] Failed to get value for key: {}", key, e);
            throw new CommonException(CommonErrorCode.REDIS_OPERATION_ERROR);
        }
    }


    public <T> boolean deleteData(String key) {
        try {
            RBucket<T> bucket = redissonClient.getBucket(key);
            return bucket.delete();
        } catch (Exception e) {
            log.error("[Redis] Failed to delete key: {}", key, e);
            throw new CommonException(CommonErrorCode.REDIS_OPERATION_ERROR);
        }
    }


    public Iterable<String> getAllOpenCouponKeys() {
        try {
            String pattern = redisProperties.couponOpenPrefix() + "*";
            return redissonClient.getKeys().getKeysByPattern(pattern);
        } catch (Exception e) {
            log.error("[Redis] Failed to get keys by pattern", e);
            throw new CommonException(CommonErrorCode.REDIS_OPERATION_ERROR);
        }
    }



    public boolean exists(String key) {
        try {
            return redissonClient.getBucket(key).isExists();
        } catch (Exception e) {
            log.error("[Redis] exists() failed for key={}", key, e);
            throw new CommonException(CommonErrorCode.REDIS_OPERATION_ERROR);
        }
    }


    public Optional<Integer> decreaseCouponStock(String key) {
        try {
            RBucket<String> bucket = redissonClient.getBucket(key);

            return Optional.ofNullable(bucket.get())
                    .map(Integer::parseInt)
                    .filter(CouponUtils::isStockCountPositive)
                    .map(stock -> {
                        bucket.set(String.valueOf(stock - 1));
                        return stock - 1;
                    });
        } catch (Exception e) {
            log.error("[Redis] decreaseCouponStock failed. key={}", key, e);
            throw new CommonException(CommonErrorCode.REDIS_OPERATION_ERROR);
        }
    }

}
