package com.example.kinokatalog.controller;

import com.example.kinokatalog.service.CatFactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/external")
@RequiredArgsConstructor
public class ExternalApiController {

    private final CatFactService catFactService;

    @GetMapping("/catfact")
    public ResponseEntity<Map<String, Object>> getCatFact() {
        Map<String, Object> fact = catFactService.getCatFact();
        return ResponseEntity.ok(fact);
    }
}
