package com.example.Kiosk.ai.controller;

import com.example.Kiosk.ai.dto.AiResponseWrapper;
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
            value = "/chat-voice",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public void chatVoice(
            @RequestParam("question") MultipartFile question,
            HttpServletResponse response // (Spring이 response 객체를 주입해 줌)
    ) throws Exception {

        // 1. 서비스 호출 (Wrapper 객체를 받음)
        AiResponseWrapper wrapper = aiService.chatVoiceSttLlmTts(question.getBytes());

        // 2. (핵심!) AI 텍스트 응답 확인
        //    (프롬프트의 "주문이 완료되었습니다" 키워드 감지)
        if (wrapper.getTextAnswer().contains("주문 완료")) {
            log.info("주문 완료 감지. euKiosk 헤더 추가.");
            // 3. (신호 전송) 프론트엔드에 "신호" 보내기
            response.setHeader("euKiosk", "COMPLETED");
        }

        // 4. 응답 헤더 설정 (스트리밍 MP3)
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        // 5. 음성 데이터를 응답 본문으로 스트림 출력
        OutputStream outputStream = response.getOutputStream();
        for (byte[] chunk : wrapper.getAudioFlux().toIterable()) {
            outputStream.write(chunk);
            outputStream.flush();
        }
    }
}
