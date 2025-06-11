package com.grepp.diary.app.model.reply;

import com.grepp.diary.app.model.reply.entity.Reply;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Integer> {

    @Modifying
    @Query("UPDATE Reply d SET d.isUse = false WHERE d.diary.diaryId = :diaryId")
    void deactivateReplyByDiaryId(@Param("diaryId") Integer diaryId);
}
