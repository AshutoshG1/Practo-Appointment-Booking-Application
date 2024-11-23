package com.healthcare.controller;

import com.healthcare.dto.LoginDto;
import com.healthcare.dto.TokenResponse;
import com.healthcare.entity.Registration;
import com.healthcare.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/registration")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    // Register new user
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Registration registration) {
        registrationService.registerUser(registration);
        return ResponseEntity.ok("User registered successfully. OTP sent.");
    }

    // Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String mobile, @RequestParam String otp) {
        boolean isVerified = registrationService.verifyOtp(mobile, otp);
        if (isVerified) {
            return ResponseEntity.ok("OTP verified successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP or user not found.");
        }
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        String token = registrationService.verifyLogin(loginDto);

        if (token != null) {
            TokenResponse tokenResponse = new TokenResponse();
            tokenResponse.setToken(token);
            return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
    }

    // Get current user profile
    @GetMapping("/profile")
    public Registration getCurrentUserProfile(@AuthenticationPrincipal Registration user) {
        return user;
    }

    // Logout is just a client-side action in JWT-based authentication
// Clear the token from the client side (e.g., in browser's localStorage or cookies)

    // Forgot Password - Send OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String mobile) {
        String message = registrationService.initiatePasswordReset(mobile);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String mobile,
            @RequestParam String otp,
            @RequestParam String newPassword) {
        boolean isReset = registrationService.verifyOtpAndResetPassword(mobile, otp, newPassword);
        if (isReset) {
            return ResponseEntity.ok("Password reset successful.");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP or mobile number.");
        }
    }

}