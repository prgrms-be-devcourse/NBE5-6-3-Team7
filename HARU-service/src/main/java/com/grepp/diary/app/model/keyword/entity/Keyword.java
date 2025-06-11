package com.grepp.diary.app.model.keyword.entity;

import com.grepp.diary.app.model.keyword.code.KeywordType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Keyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer keywordId;
    private String name;
    private Boolean isUse;

    @Enumerated(EnumType.STRING)
    private KeywordType type;
}
