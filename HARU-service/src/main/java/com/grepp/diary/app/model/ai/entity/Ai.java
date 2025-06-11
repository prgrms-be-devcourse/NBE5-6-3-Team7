package com.grepp.diary.app.model.ai.entity;

import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.reply.entity.Reply;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Where;

@Entity
@Getter @Setter
@ToString
public class Ai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_id")
    private Integer aiId;

    @Column(length = 50)
    private String name;

    @Column(length = 10)
    private String mbti;

    @Column(length = 500)
    private String info;

    @Column(length = 500)
    private String prompt;

    @Column(name = "is_use")
    private Boolean isUse;

    @OneToMany(mappedBy = "ai", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Custom> customs = new ArrayList<>();

    @OneToMany(mappedBy = "ai", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "ai", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "is_use = true")
    private List<AiImg> images = new ArrayList<>();
}
