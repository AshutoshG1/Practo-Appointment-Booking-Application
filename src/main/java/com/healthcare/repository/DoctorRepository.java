package com.healthcare.repository;

import com.healthcare.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByNameContainingOrSpecializationsContaining(String name, String specialization);
}
