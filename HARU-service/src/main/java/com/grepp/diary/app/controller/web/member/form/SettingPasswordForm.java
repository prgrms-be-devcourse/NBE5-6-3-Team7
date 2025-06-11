package com.grepp.diary.app.controller.web.member.form;

import lombok.Data;

@Data
public class SettingPasswordForm {
    private String currentPassword;
    private String newPassword;
    private String checkPassword;
}
