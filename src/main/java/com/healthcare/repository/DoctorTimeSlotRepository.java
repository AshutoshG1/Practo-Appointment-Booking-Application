package com.healthcare.repository;

import com.healthcare.entity.DoctorTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DoctorTimeSlotRepository extends JpaRepository<DoctorTimeSlot, Long> {

    // Find time slots for a specific doctor, date, and time that are unbooked
    DoctorTimeSlot findByDoctorIdAndTimeSlotAndSlotDateAndIsBookedFalse(long doctorId, String timeSlot, Date slotDate);

    // Find all time slots for a specific date
    List<DoctorTimeSlot> findBySlotDate(Date slotDate);
}