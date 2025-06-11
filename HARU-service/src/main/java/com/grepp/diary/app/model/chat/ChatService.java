package com.grepp.diary.app.model.chat;

import com.grepp.diary.app.model.chat.entity.Chat;
import com.grepp.diary.app.model.chat.repository.ChatRepository;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.app.model.member.repository.MemberRepository;
import com.grepp.diary.app.model.reply.ReplyRepository;
import com.grepp.diary.app.model.reply.entity.Reply;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;
    private final ReplyRepository replyRepository;

    @Transactional
    public void registCount(String userId, Integer replyId, int plusCount) {
        Optional<Chat> result = chatRepository.findByReply_ReplyId(replyId);
        if (result.isEmpty()) {
            Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
            Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found"));

            Chat chat = new Chat();
            chat.setMember(member);
            chat.setReply(reply);
            chat.setCount(plusCount);

            chatRepository.save(chat);
            log.info("replyId : {}", replyId);
            log.info("Regist new chat count : {}", plusCount);
        } else {
            Chat chat = result.get();
            chat.setCount(chat.getCount() + plusCount); // dirty checking 자동 저장
            log.info("replyId : {}", replyId);
            log.info("Update chat count : {}", chat.getCount());
        }
    }

    public Integer getMonthChatCount() {
        LocalDateTime startDateTime = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endDateTime = startDateTime.plusMonths(1);

        return chatRepository.getMonthChatCount(startDateTime, endDateTime);
    }
}
