package com.example.demo.rag.service;

import com.example.demo.chat.service.ChatService;
import com.example.demo.embedding.converter.EmbeddingConverter;
import com.example.demo.embedding.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagServiceImpl implements RagService {

    private final EmbeddingService embeddingService;
    private final EmbeddingConverter embeddingConverter;
    private final ChatService chatService;

    @Override
    public String uploadKnowledge(MultipartFile file) {
        try {
            List<String> chunks = extractTextFromPDFAsChuncks(file);
            int n = chunks.size();
            for (int i = 0; i < n; i++) {
                embeddingService.buildAndStoreEmbedding(file.getOriginalFilename() + i, chunks.get(i));
            }
            return "Knowledge uploaded and embeddings generated for " + n + " chunks.";
        } catch (IOException e) {
            return "Error processing PDF file: " + e.getMessage();
        }
    }

    @Override
    public String generateText(String question) {
        List<String> closestEmbeddings = embeddingConverter.convertToListOfClosestEmbeddings(embeddingService.searchClosestEmbeddings(question));
        chatService.reset();
        chatService.sendSystemPrompt("Make summary for the paragraphs given in prompt. Have in mind that the first one is the most relevant, the others are auxiliary.");
        return chatService.getChatResponse(String.join("\n", closestEmbeddings)).aiResponse();
    }

    private List<String> extractTextFromPDFAsChuncks(MultipartFile file) throws IOException {
        PDDocument document = Loader.loadPDF(file.getBytes());
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();
        return splitStringIntoParts(text, document.getNumberOfPages() * 3);
    }

    private List<String> splitStringIntoParts(String str, int parts) {
        int length = str.length();
        int partLength = length / parts;
        int remainder = length % parts;
        List<String> result = new ArrayList<>();
        int startIndex = 0;
        for (int i = 0; i < parts; i++) {
            int endIndex = startIndex + partLength + (i < remainder ? 1 : 0);
            result.add(str.substring(startIndex, endIndex));
            startIndex = endIndex;
        }
        return result;
    }

}
