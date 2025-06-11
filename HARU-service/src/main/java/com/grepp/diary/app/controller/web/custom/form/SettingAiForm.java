package com.grepp.diary.app.controller.web.custom.form;

import lombok.Data;

@Data
public class SettingAiForm {

    private Integer selectedAiId;
    private Boolean isFormal;
    private Boolean isLong;
}
