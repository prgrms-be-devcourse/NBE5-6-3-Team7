package com.grepp.diary.app.model.diary.entity;

import com.grepp.diary.app.model.common.entity.BaseEntity;
import com.grepp.diary.app.model.diary.code.Emotion;
import com.grepp.diary.app.model.keyword.entity.DiaryKeyword;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.app.model.reply.entity.Reply;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
public class Diary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer diaryId;
    @Enumerated(EnumType.STRING)
    private Emotion emotion;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDate date;
    private Boolean isUse = true;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<DiaryImg> images = new ArrayList<>();

    @OneToMany(mappedBy = "diaryId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryKeyword> keywords;

    @OneToOne(mappedBy = "diary", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Reply reply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;
}
