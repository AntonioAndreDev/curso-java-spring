package com.mballem.demo_park_api.web.controller;

import com.mballem.demo_park_api.jwt.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class JwtController {

    @PostMapping("/jwt-verify/{token}")
    public ResponseEntity<Map<String, Boolean>> verifyJwt(@PathVariable String token) {
        Boolean isValid = JwtUtils.isTokenValid(token);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isValid", isValid);

        HttpStatus status = isValid ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response);
    }
}
