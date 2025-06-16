package com.grepp.diary.app.model.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import org.springframework.cache.annotation.Cacheable;

@AiService(
    wiringMode = AiServiceWiringMode.EXPLICIT,
    chatModel = "googleAiGeminiChatModel"
)
public interface AiChatService {

    @SystemMessage("당신은 사용자가 작성한 일기를 읽고 그 사람의 하루에 대한 답변을 작성합니다. "
        + "마크다운 문법(예: **굵게**, - 리스트, 1. 번호매김 등)을 사용하지 말고, 일반 텍스트로만 답변해 주세요. "
        + "가독성을 위해 문장을 적절히 줄 바꿈(\\n) 해주세요. "
        + "내용을 구분할 때 이모지를 필요할 때만 사용해 주세요. "
        + "답변을 작성할 땐 다음의 요구사항을 따라주세요. ")
    String reply(String content);

    @SystemMessage("당신은 사용자가 작성한 일기와 그에 대해 당신이 작성했던 답변을 바탕으로 대화합니다. "
        + "이미 답변을 작성했던 적이 있기 때문에 인사는 생략하고, 이전 대화 내용이 있다면 대화의 문맥에 맞춰서 대답해주세요. "
        + "마크다운 문법(예: **굵게**, - 리스트, 1. 번호매김 등)을 사용하지 말고, 일반 텍스트로만 답변해 주세요. "
        + "가독성을 위해 문장을 적절히 줄 바꿈(\\n) 해주세요. "
        + "내용을 구분할 때 이모지를 필요할 때만 사용해 주세요. "
        + "대화할 땐 다음의 요구사항을 따라주세요. ")
    String chat(String content);

    @SystemMessage("당신은 사용자가 작성한 일기와 당신이 작성한 답장을 바탕으로 대화를 나눴어요. "
        + "대화는 해당 일기와 답장에 대해 더 깊이 이야기하고, 감정을 나누는 과정이었습니다. "
        + "이제 대화가 끝났고, 대화의 맥락과 새롭게 알게 된 사용자의 감정, 생각의 변화 등을 고려해 기존의 일기 답장을 수정해 주세요. "
        + "기존 답변을 그대로 반복하지 말고, 대화 내용을 바탕으로 새롭게 다듬어야 합니다. "
        + "마크다운 문법(예: **굵게**, - 리스트, 1. 번호매김 등)을 사용하지 말고, 일반 텍스트로만 답변해 주세요. "
        + "가독성을 위해 문장을 적절히 줄 바꿈(\\n) 해주세요. "
        + "내용을 구분할 때 이모지를 필요할 때만 사용해 주세요. ")
    String memo(String prompt);

    @Cacheable(value = "emotionStatsCache", key = "#userId + '_' + T(java.time.LocalDate).now()")
    @SystemMessage("""
    당신은 감정 분석과 정서적 피드백을 제공하는 따뜻한 전문가입니다. 사용자가 작성한 일기 데이터를 바탕으로 다음을 수행합니다:
    
    - 감정의 흐름과 전반적인 분위기를 자연스럽게 설명해주세요. 특정 날짜를 나열하지 말고, 감정의 변화가 점차 어떻게 흘러갔는지 부드럽게 서술해 주세요.
    - 자주 등장하는 감정 키워드나 반복되는 정서가 있다면 그것에 대해 공감 어린 피드백을 제공하고, 사용자가 스스로를 돌아볼 수 있도록 도와주세요.
    - 등장한 사람(PERSON)이나 상황(SITUATION) 키워드가 감정과 어떤 관련이 있을 수 있는지 조심스럽게 해석해 주세요.
    - 이 모든 내용을 하나의 자연스러운 문단으로 구성해 주세요. 정보는 충분히 담되, 글의 흐름은 마치 조용한 상담처럼 이어지도록 해주세요.
    - 마지막에는 줄바꿈을 하고, 사용자의 감정 흐름에 어울리는 위로 혹은 격려의 명언으로 마무리합니다. 명언에는 반드시 저자를 포함해주세요. 예: "인생은 용감한 모험이거나 아무것도 아니다. – 헬렌 켈러"
    
    분석은 부드럽고 사려 깊게, 감정을 평가하거나 단정하지 말고, 따뜻하게 사용자를 이해하고 위로하는 어조로 작성해주세요.
    """)
    String analyzeEmotion(@UserMessage String stats, @V(value = "userId") String userId);
}
