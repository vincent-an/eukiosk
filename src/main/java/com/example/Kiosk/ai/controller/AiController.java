package com.example.Kiosk.ai.controller;

import com.example.Kiosk.ai.service.AiService;
import jakarta.servlet.http.HttpServletResponse;
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
import reactor.core.publisher.Flux;

import java.io.OutputStream;

@RestController
@RequestMapping("/api/ai")
@Slf4j
@RequiredArgsConstructor
public class AiController {

    private  final AiService aiService;

    @PostMapping(
            value = "/chat-voice", // (API 주소 /chat-voice로 단순화)
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public void chatVoice( // (메서드 이름 단순화)
                           @RequestParam("question") MultipartFile question,
                           HttpServletResponse response
    ) throws Exception {

        // 1. 서비스 호출
        Flux<byte[]> flux = aiService.chatVoiceSttLlmTts(question.getBytes());

        // 2. 응답 헤더 설정 (스트리밍 MP3)
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        // 3. 음성 데이터를 응답 본문으로 스트림 출력 (프론트엔드가 받는 즉시 재생)
        OutputStream outputStream = response.getOutputStream();
        for (byte[] chunk : flux.toIterable()) {
            outputStream.write(chunk);
            outputStream.flush();
        }
    }

//    @PostMapping(
//            value = "/chat-voice",
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
//            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
//    )
//    public ResponseEntity<byte[]> chatVoiceOneModel(
//            @RequestParam("question") MultipartFile question
//    ) throws Exception {
//
//        // 여기서 aiService.chatVoiceOneModel()을 호출
//        byte[] bytes = aiService.chatVoiceOneModel(
//                question.getBytes(),
//                question.getContentType()
//        );
//
//        // 응답: mp3 파일 형태로 반환
//        return ResponseEntity
//                .ok()
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"response.mp3\"")
//                .body(bytes);
//    }
}
