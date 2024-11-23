package com.healthcare.service;

import com.healthcare.entity.Doctor;
import com.healthcare.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

    public Doctor addDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public List<Doctor> searchDoctorsByNameOrSpecializations(String search) {
        // Logic to search doctors by name or specializations
        return doctorRepository.findByNameContainingOrSpecializationsContaining(search, search);
    }
}
