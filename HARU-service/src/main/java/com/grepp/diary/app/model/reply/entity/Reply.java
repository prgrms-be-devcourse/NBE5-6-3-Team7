package com.grepp.diary.app.model.reply.entity;

import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.common.entity.BaseEntity;
import com.grepp.diary.app.model.diary.entity.Diary;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer replyId;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;
    private Boolean isUse = true;

    @ManyToOne
    @JoinColumn(name = "ai_id")
    private Ai ai;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;
}
