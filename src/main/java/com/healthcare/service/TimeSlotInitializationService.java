package com.healthcare.service;

import com.healthcare.entity.DoctorTimeSlot;
import com.healthcare.repository.DoctorTimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;

@Service
public class TimeSlotInitializationService {

    @Autowired
    private DoctorTimeSlotRepository timeSlotRepo;

    @PostConstruct
    public void init() {
        long doctorId = 2; // Specify the doctor ID
        String[] timeSlots = {"10:15 AM", "11:15 AM", "12:15 PM", "01:15 PM"};

        for (String timeSlot : timeSlots) {
            DoctorTimeSlot newSlot = new DoctorTimeSlot();
            newSlot.setDoctorId(doctorId);
            newSlot.setTimeSlot(timeSlot);
            newSlot.setSlotDate(new Date()); // Current date
            newSlot.setBooked(false);
            timeSlotRepo.save(newSlot);
        }
    }
}
