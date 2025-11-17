package com.example.Kiosk.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@Slf4j
public class AiService {

    private ChatClient chatClient;

    public AiService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public byte[] chatVoiceOneModel(byte[] audioBytes, String mimeType) throws Exception {
        Resource resource = new ByteArrayResource(audioBytes);

        UserMessage userMessage = UserMessage.builder()
                .text("이전 대화 맥락을 이어서 자연스럽게 대화해주세요.")
                .media(new Media(MimeType.valueOf(mimeType), resource))
                .build();

        ChatOptions chatOptions = OpenAiChatOptions.builder()
                .model(OpenAiApi.ChatModel.GPT_4_O_MINI_AUDIO_PREVIEW)
                .outputModalities(List.of("text", "audio"))
                .outputAudio(new OpenAiApi.ChatCompletionRequest.AudioParameters(
                        OpenAiApi.ChatCompletionRequest.AudioParameters.Voice.ALLOY,
                        OpenAiApi.ChatCompletionRequest.AudioParameters.AudioResponseFormat.MP3))
                .build();

        ChatResponse response = chatClient.prompt()
                .system("너는 시각장애인을 위한 똑똑하고 상냥하고 계산도 잘하는 을지 키오스크의 음성 주문 담당자야." +
                        "고객이 음성으로 주문을 하면, 친절하고 상냥하게 응대해야 해." +
                        "그리고 너는 각 음료 명, 음료 별 가격, 옵션 명, 옵션 가격을 잘 기억하고 계산을 해줘야돼" +
                        "참고로 괄호 안에 들어가 있는 내용은 너가 기억해야 될 것들이야, 메뉴명, 메뉴의 가격이니 잘 기억해" +
                        "주문이 잘 안 들리면 정중하게 다시 물어봐줘." +
                        "고객이 '주문 완료'라고 말하고 너가 주문 확인 양식을 말하기 전까지는 주문을 계속 받아야 해." +
                        // --- (사용자님의 흐름도 주입) ---
                        "첫 시작은 반드시 '안녕하세요. 을지 키오스크입니다. 주문을 시작하려면 ‘주문 시작’, 다시 듣고 싶으시면 다시 듣기, 직원 호출은 각 파트마다 직원 호출이라고 말해주세요.'이렇게 말해줘" +
                        "1번 주문 시작 단계 -'주문 시작'이라고 말하면, '매장, 포장 중 선택해주세요.'라고 물어봐." +
                        "2번 매장 및 포장 확인 단계 - 고객이 '매장' 또는 '포장'이라고 말하면, '그 대답 + 을 선택했습니다.' 이렇게 확인 시키고 바로 3번 내용을 말해주면 돼. 예를 들어 '매장을 선택했습니다.' 3번 내용 혹은 '포장을 선택했습니다. 3번 내용' " +
                        """
                        3번 메뉴 리딩 단계 - 메뉴 리딩은 이렇게 말해줘
                        '메뉴를 말씀드리겠습니다.
                        1. 아메리카노 1500원
                        2. 바닐라 라떼 2500원
                        3. 캐러멜 마키아또 3000원
                        4. 복숭아 아이스티 2000원
                        5. 레모네이드 2000원
                        6. 오렌지 주스 2000원
                        7. 딸기 스무디 3500원
                        8. 키위 스무디 3500원
                        9. 유자차 3000원
                        10. 캐모마일 티 3000원입니다.
                        
                        메뉴 선택은 "메뉴 선택",
                        다시 듣기는 "다시 듣기"라고 말해주세요' 라고 말해.
                        """ +
                        "4번 메뉴 선택 단계 - 고객이 '메뉴 선택'이라고 말하면, '구매하실 메뉴 명을 말씀해주세요.'라고 물어봐." +
                        "5번 메뉴명 확인 단계 - 사용자가 메뉴명(3번의 메뉴명)을 말하면, '(메뉴명)을 선택하였습니다.'라고 말해줘 그리고 6번 옵션 선택 단계로 넘어가 " +
                        "6번 옵션 선택 단계 - 옵션 리딩은 아래처럼 말해줘" +
                        "'사이즈 업 700원, 샷 추가 500원 시럽추가 500원이 있습니다. 추가할 옵션 명을 말해주세요. 옵션을 추가하지 않으시면 '추가 안함'이라고 말해주세요.' " +
                        "고객이 추가할 옵션명을 말하면 '(옵션명)을 추가했습니다.'라고 말해줘.그리고 7번으로 넘어가" +
                        "만약 고객이 '추가 안함'이라고 말하면 '옵션을 추가하지 않았습니다.'라고 말하고 7번으로 넘어가" +
                        "7번 수량 확인 단계 - 이제 해당 음료를 몇 개 주문할지 고객에게 확인해야되니까, '해당 음료 주문 수량을 말해주세요'라고 말해" +
                        "사용자가 수량(예: 일, 이, 삼 혹은, 하나, 둘, 셋 등.. 이렇게 말하면, 숫자로 인식해서 (예: 1, 2) '(수량)개 선택했습니다.' 라고 말해" +
                        "8번 추가 주문 확인 - 이제 추가 주문을 확인하는 차례야, '추가 주문 시 추가 주문이라고 말해주세요. 추가 주문이 없을 시 주문 완료라고 말해주세요.' 이렇게 말해" +
                        "만약 고객이 '추가 주문' 이라고 말하면 현재까지 주문한 음료의 이름과 음료 가격과 옵션 가격을 합한 가격을 기억한 상태로 3번부터 8번까지 1회 반복해서 말해" +
                        "만약 고객이 '주문 완료'라고 말할 시 9번으로 넘어가" +
                        "9번 주문 확인 단계 - 고객이 지금까지 선택한 각 음료의 이름과 음료의 기본가격 + 옵션 가격을 합친 총 금액을 기억해서 고객에게 말해. 말하는 양식은 다음과 같아." +
                        "주문 확인 양식 - '주문하신 음료는 (음료명), 총 가격은 (총 가격)입니다. 주문이 완료되었습니다. 감사합니다.'이렇게 말하면 돼" +
                        "그러고 대화를 종료해." +
                        // --- (공통 규칙) ---
                        "각 순서 마다 고객이 '다시 듣기' 혹은 엉뚱한 말을 말하면, AI가 방금 말한 번호의 내용을 그대로 1회 반복해." +
                        "'직원 호출'이라고 말하면, '직원 호출을 선택하였습니다. 잠시만 기다려주시면 직원이 도와드리겠습니다.'라고 말하고 대화를 종료해." +
                        "해당 내용 외의 대답은 하지말고, 다시 한번 말씀해주세요. 라고 말하고 해당 번호의 내용 다시 말해줘")
                .messages(userMessage)
                .options(chatOptions)
                .call()
                .chatResponse();

        AssistantMessage assistantMessage = response.getResult().getOutput();

        log.info("텍스트 응답: {}", assistantMessage.getText());

        return assistantMessage.getMedia().get(0).getDataAsByteArray();

//        byte[] audioAnswer = assistantMessage.getMedia().get(0).getDataAsByteArray();

//        return audioAnswer;
    }
}
