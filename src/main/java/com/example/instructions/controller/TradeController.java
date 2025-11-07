package com.example.instructions.controller;

import com.example.instructions.model.Trade;
import com.example.instructions.model.CanonicalTrade;
import com.example.instructions.service.TradeService;
import com.example.instructions.store.InMemoryStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeService tradeService;
    private final InMemoryStore store;
    private final ObjectMapper mapper = new ObjectMapper();

    public TradeController(TradeService tradeService, InMemoryStore store) {
        this.tradeService = tradeService;
        this.store = store;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String fname = file.getOriginalFilename();
        if (fname == null || fname.isBlank()) return ResponseEntity.badRequest().body("Invalid file");
        if(!fname.endsWith(".csv") && !fname.endsWith(".json")) {
            return ResponseEntity.badRequest().body("Unsupported file type");
        }
        int processed = 0;
        try{
            processed = tradeService.processData(file);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Processing error: " + e.getMessage());
        }
        return ResponseEntity.ok(Map.of("processed", processed));
    }



}
