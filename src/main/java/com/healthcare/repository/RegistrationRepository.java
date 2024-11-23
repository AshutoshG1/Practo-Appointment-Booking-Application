package com.healthcare.repository;

import com.healthcare.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    // Custom method to find registration by mobile number
    Optional<Registration> findByMobile(String mobile);
}