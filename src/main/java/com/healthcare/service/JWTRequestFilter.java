package com.healthcare.service;

import com.healthcare.entity.Registration;
import com.healthcare.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private JWTService jwtService;
    private RegistrationRepository registrationRepository;

    @Autowired
    public JWTRequestFilter(JWTService jwtService, RegistrationRepository registrationRepository) {
        this.jwtService = jwtService;
        this.registrationRepository = registrationRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
            String userName = jwtService.getUserName(token);

            Optional<Registration> opUser = registrationRepository.findByMobile(userName);
            if (opUser.isPresent()) {
                Registration user = opUser.get();

                // Convert the user's role to a GrantedAuthority
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getUserRole());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user, null, Collections.singletonList(authority));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}