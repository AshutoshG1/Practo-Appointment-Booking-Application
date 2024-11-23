package com.healthcare.service;

import com.healthcare.dto.LoginDto;
import com.healthcare.entity.Registration;
import com.healthcare.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;
import java.util.Random;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private TwilioSmsSender twilioSmsSender;  // Assuming you're using Twilio for SMS

    @Autowired
    private JWTService jwtService;

    @Autowired
    private SendGridEmailSender sendGridEmailSender;

    // Register new user and send OTP
    @Transactional
    public void registerUser(Registration registration) {
        // Save user details
        registrationRepository.save(registration);

        // Generate OTP and send via SMS
        String otp = generateOtp();
        registration.setOtp(otp);
        registrationRepository.save(registration);

        // Send OTP via SMS
        twilioSmsSender.sendSms(registration.getMobile(), "Your OTP is: " + otp);
    }

    // Verify OTP
    public boolean verifyOtp(String mobile, String otp) {
        // Find user by mobile number
        Registration registration = registrationRepository.findByMobile(mobile).get();
        if (registration != null && registration.getOtp().equals(otp)) {
            // Mark OTP as verified
            registration.setOtpVerified(true);
            registrationRepository.save(registration);
            return true;
        }
        return false;
    }

    // Generate a 6-digit OTP
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);  // Generates a random 6-digit number
        return String.valueOf(otp);
    }

    public String verifyLogin(LoginDto loginDto) {
        Registration user = registrationRepository.findByMobile(loginDto.getMobile()).get();
        if (user != null && user.getPassword().equals(loginDto.getPassword())) {
            return jwtService.generateToken(user);  // Generate token on successful login
        }
        return null;  // Invalid credentials
    }

    public String initiatePasswordReset(String mobile) {
        Registration registration = registrationRepository.findByMobile(mobile).get();
        if (registration != null) {
            String otp = generateOtp();
            registration.setOtp(otp);
            registrationRepository.save(registration);

            // Send OTP via SMS
            twilioSmsSender.sendSms(registration.getMobile(), "Your password reset OTP is: " + otp);

            // Send OTP via Email
            try {
                sendGridEmailSender.sendEmail(
                        registration.getEmail(),
                        "Password Reset OTP",
                        "Your password reset OTP is: " + otp
                );
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "OTP sent to registered mobile and email.";
        } else {
            throw new RuntimeException("User not found");
        }
    }

    // Verify OTP and reset password
    public boolean verifyOtpAndResetPassword(String mobile, String otp, String newPassword) {
        Registration registration = registrationRepository.findByMobile(mobile).get();
        if (registration != null && registration.getOtp().equals(otp)) {
            registration.setPassword(newPassword);
            registration.setOtp(null);  // Clear the OTP once used
            registrationRepository.save(registration);
            return true;
        }
        return false;
    }
}
