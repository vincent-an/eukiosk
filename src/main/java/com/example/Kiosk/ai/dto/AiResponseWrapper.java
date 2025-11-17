package com.example.Kiosk.ai.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Getter
@RequiredArgsConstructor // (final 필드를 위한 생성자)
public class AiResponseWrapper {

    private final String textAnswer; // 1. AI가 생성한 텍스트
    private final Flux<byte[]> audioFlux; // 2. AI가 생성한 음성 스트림

}
