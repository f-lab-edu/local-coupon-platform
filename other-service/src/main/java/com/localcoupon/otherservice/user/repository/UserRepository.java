package com.localcoupon.otherservice.user.repository;

import com.localcoupon.otherservice.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<Long> findIdByEmail(String email);
    boolean existsByEmail(String email);

}
