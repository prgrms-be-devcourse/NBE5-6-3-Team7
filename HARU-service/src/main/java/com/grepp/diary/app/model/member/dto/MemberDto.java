package com.grepp.diary.app.model.member.dto;

import com.grepp.diary.app.model.auth.code.Role;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.member.entity.Member;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class MemberDto {

    private String userId;
    private String email;
    private String name;
    private String password;
    private Role role;
    private boolean enabled;
    private Custom custom;
    private List<Diary> diaries = new ArrayList<>();

    public Member toEntity() {
        Member member = new Member();
        member.setUserId(this.userId);
        member.setEmail(this.email);
        member.setName(this.name);
        member.setPassword(this.password);
        member.setRole(this.role);
        member.setEnabled(this.enabled);
        member.setCustom(this.custom);
        member.setDiaries(this.diaries);
        return member;
    }
}
