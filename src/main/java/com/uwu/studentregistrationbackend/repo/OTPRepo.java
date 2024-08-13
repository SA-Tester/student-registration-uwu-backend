package com.uwu.studentregistrationbackend.repo;

import com.uwu.studentregistrationbackend.entity.OTP;
import com.uwu.studentregistrationbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPRepo  extends JpaRepository<OTP, Integer> {
    Optional<OTP> findByuser(User user);
}
