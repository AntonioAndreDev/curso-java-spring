package com.mballem.demo_park_api.web.controller;

import com.mballem.demo_park_api.jwt.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class JwtController {

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Boolean>> verifyJwt(@RequestBody Map<String, String> tokenMap) {
        String token = tokenMap.get("token");
        Boolean isValid = JwtUtils.isTokenValid(token);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isValid", isValid);

        HttpStatus status = isValid ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response);
    }
}
