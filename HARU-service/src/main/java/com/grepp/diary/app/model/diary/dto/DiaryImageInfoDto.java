package com.grepp.diary.app.model.diary.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DiaryImageInfoDto {
    private Integer imgId;
    private String path;
    private String originName;

    public DiaryImageInfoDto(Integer imgId, String path, String originName) {
        this.imgId = imgId;
        this.path = path;
        this.originName = originName;
    }
}
