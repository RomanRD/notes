package com.service.notes.controller;

import com.service.notes.domain.dto.AuthenticationRequestDTO;
import com.service.notes.domain.exception.BruteForceException;
import com.service.notes.persistence.document.User;
import com.service.notes.security.JwtUtil;
import com.service.notes.security.BruteForceDefender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final BruteForceDefender bruteForceDefender;

    @PostMapping("/authenticate")
    public String authenticate(@RequestBody AuthenticationRequestDTO request) {
        try{
            bruteForceDefender.checkCurrentClientIPBlocked();

            final Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            final User user = (User) authenticate.getPrincipal();

            return jwtUtil.generateToken(user);
        }catch (AuthenticationException e){
            bruteForceDefender.loginFailed();
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } catch (BruteForceException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You have exceeded the number of attempts, you are blocked for " +
                            bruteForceDefender.BLOCKING_HOURS + " hours");
        }
    }

}