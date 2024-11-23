package com.healthcare.service;

import com.healthcare.dto.BookingDto;
import com.healthcare.entity.Booking;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.DoctorTimeSlot;
import com.healthcare.entity.Patient;
import com.healthcare.repository.BookingRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.DoctorTimeSlotRepository;
import com.healthcare.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorTimeSlotRepository timeSlotRepo;

    public String bookAnAppointment(BookingDto dto) {
        DoctorTimeSlot availableSlot = timeSlotRepo.findByDoctorIdAndTimeSlotAndSlotDateAndIsBookedFalse(
                dto.getDoctorId(), dto.getBookingTime(), dto.getBookingDate());

        if (availableSlot == null) {
            throw new RuntimeException("Time Slot Not Available for the selected date");
        }

        // Fetch the Doctor and Patient entities
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Set doctor, patient, and other details in the Booking entity
        Booking booking = new Booking();
        booking.setDoctor(doctor);
        booking.setPatient(patient);
        booking.setBookingTime(dto.getBookingTime());
        booking.setBookingDate(dto.getBookingDate());
        bookingRepo.save(booking);

        availableSlot.setBooked(true);
        timeSlotRepo.save(availableSlot);

        return "Booking Confirmed for " + dto.getBookingDate();
    }

    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public void resetDailyTimeSlots() {
        Date tomorrow = getTomorrowDate();
        List<DoctorTimeSlot> existingSlots = timeSlotRepo.findBySlotDate(tomorrow);

        if (existingSlots.isEmpty()) {
            List<DoctorTimeSlot> todaySlots = timeSlotRepo.findBySlotDate(new Date());
            for (DoctorTimeSlot slot : todaySlots) {
                DoctorTimeSlot newSlot = new DoctorTimeSlot();
                newSlot.setDoctorId(slot.getDoctorId());
                newSlot.setTimeSlot(slot.getTimeSlot());
                newSlot.setSlotDate(tomorrow);
                newSlot.setBooked(false);
                timeSlotRepo.save(newSlot);
            }
        }
    }

    private Date getTomorrowDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        return cal.getTime();
    }
}
