package com.grepp.diary.app.model.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

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

    @SystemMessage("당신은 사용자가 작성했던 일기와 그에 대해 당신이 작성했던 답장을 바탕으로 사용자와 대화를 나눴어요."
        + "이제 대화의 내용을 요약해야 합니다. 요약에서 사용자를 지칭할 때는 '당신', 그리고 당신 자신을 지칭할 때는 '저'라고 지칭해 주세요."
        + "요약이 완료되면 요약 내용과 당신이 작성했던 일기 답변의 원본을 함께 전달해 주세요."
        + "그리고 전달할 때 각 내용은 마크다운 기호는 사용하지 말고 개행문자(\\\\n)로 구분해 주세요."
        + "예를 들면 \\n\uD83D\uDCAC 대화 요약 : ~ (\\\\n) \\n\uD83D\uDC8C 일기 답변 : ~ ")
    String memoWithOriginalReply(String prompt);
}
