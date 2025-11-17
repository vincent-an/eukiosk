package com.example.Kiosk.ai.controller;

import com.example.Kiosk.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ai")
@Slf4j
@RequiredArgsConstructor
public class AiController {

    private  final AiService aiService;

    @PostMapping(
            value = "/chat-voice",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity<byte[]> chatVoiceOneModel(
            @RequestParam("question") MultipartFile question
    ) throws Exception {

        // 여기서 aiService.chatVoiceOneModel()을 호출
        byte[] bytes = aiService.chatVoiceOneModel(
                question.getBytes(),
                question.getContentType()
        );

        // 응답: mp3 파일 형태로 반환
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"response.mp3\"")
                .body(bytes);
    }
}
