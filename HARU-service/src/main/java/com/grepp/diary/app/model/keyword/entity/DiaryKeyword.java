package com.grepp.diary.app.model.keyword.entity;

import com.grepp.diary.app.model.diary.entity.Diary;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class DiaryKeyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer diaryKeywordId;
    @ManyToOne
    @JoinColumn(name = "diary_id")
    private Diary diaryId;
    @ManyToOne
    @JoinColumn(name = "keyword_id")
    private Keyword keywordId;
}
