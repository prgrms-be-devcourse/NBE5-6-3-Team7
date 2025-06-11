package com.grepp.diary.app.model.member.entity;

import com.grepp.diary.app.model.auth.code.Role;
import com.grepp.diary.app.model.chat.entity.Chat;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.diary.entity.Diary;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Role role;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Custom custom;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Diary> diaries = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chat = new ArrayList<>();
}
