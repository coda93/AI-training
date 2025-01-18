package com.example.demo.rag.service;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface RagService {

    String uploadKnowledge(@RequestParam("file") MultipartFile file);

    String generateText(@RequestBody String question);
}
