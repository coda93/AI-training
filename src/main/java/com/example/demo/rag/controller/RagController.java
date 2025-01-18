package com.example.demo.rag.controller;

import com.example.demo.rag.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RagController {

    private final RagService ragService;

    @PostMapping("/upload-knowledge")
    public ResponseEntity<String> uploadKnowledge(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ragService.uploadKnowledge(file));
    }

    @PostMapping("/generate-text")
    public ResponseEntity<String> generateText(@RequestBody String question) {
        return ResponseEntity.ok(ragService.generateText(question));
    }
}
