package com.grepp.diary.app.model.keyword.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeywordDto {
    private String name;
    private Integer count;
}
