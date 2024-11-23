package com.healthcare.dto;

import java.util.Date;

public class BookingDto {
    private long doctorId;
    private long patientId;
    private String bookingTime;
    private Date bookingDate;

    // Getters and Setters
    public long getDoctorId() { return doctorId; }
    public void setDoctorId(long doctorId) { this.doctorId = doctorId; }
    public long getPatientId() { return patientId; }
    public void setPatientId(long patientId) { this.patientId = patientId; }
    public String getBookingTime() { return bookingTime; }
    public void setBookingTime(String bookingTime) { this.bookingTime = bookingTime; }
    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }
}