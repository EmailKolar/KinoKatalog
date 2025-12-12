package com.example.kinokatalog.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CatFactService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String CATFACT_URL = "https://catfact.ninja/fact";

    public Map<String, Object> getCatFact() {
        return restTemplate.getForObject(CATFACT_URL, Map.class);
    }
}

