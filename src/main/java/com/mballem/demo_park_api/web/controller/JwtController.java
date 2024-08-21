package com.mballem.demo_park_api.web.controller;

import com.mballem.demo_park_api.jwt.JwtUserDetailsService;
import com.mballem.demo_park_api.jwt.JwtUtils;
import com.mballem.demo_park_api.jwt.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JwtController {

    private final JwtUserDetailsService jwtUserDetailsService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> verifyJwt(@RequestBody Map<String, String> tokenMap) {
        String token = tokenMap.get("token");
        Boolean isValid = JwtUtils.isTokenValid(token);
        Map<String, Object> response = new HashMap<>();
        response.put("isValid", isValid);

        if (isValid) {
            Date expirationDate = JwtUtils.getExpirationDateFromToken(token);
            long diffInMillies = Math.abs(expirationDate.getTime() - new Date().getTime());
            long diffInMinutes = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);

            if (diffInMinutes < 5) {
                String username = JwtUtils.getUsernameFromToken(token);
                JwtToken newToken = jwtUserDetailsService.getTokenAuthenticated(username);
                response.put("newToken", newToken.getToken());
            }
        }

        HttpStatus status = isValid ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response);
    }
}