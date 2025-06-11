package com.grepp.diary.app.controller.web.auth.form;

import com.grepp.diary.app.model.auth.code.Role;
import com.grepp.diary.app.model.member.dto.MemberDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupForm {

    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    private String userId;

    @Size(max = 20)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$?%^&*()_+=-]).{8,}$"
        ,message = "비밀번호는 8자리 이상의 영문자, 숫자, 특수문자 조합으로 이루어져야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력 항목입니다.")
    private String repassword;

    @NotBlank
    @Email
    private String email;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(max = 20)
    private String name;

    public MemberDto toDto() {
        MemberDto dto = new MemberDto();
        dto.setEmail(email);
        dto.setName(name);
        dto.setPassword(password);
        dto.setUserId(userId);
        dto.setRole(Role.ROLE_USER);
        dto.setEnabled(true); // 아이디 활성화 default
        return dto;
    }
}
