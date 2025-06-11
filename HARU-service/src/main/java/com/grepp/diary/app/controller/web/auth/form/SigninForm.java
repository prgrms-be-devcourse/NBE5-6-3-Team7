package com.grepp.diary.app.controller.web.auth.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SigninForm {

    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;
}
