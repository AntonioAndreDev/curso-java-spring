package com.mballem.demo_park_api.service;

import com.mballem.demo_park_api.repository.ClienteVagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteVagaService {

    private final ClienteVagaRepository repository;
}
