package com.grepp.diary.app.model.custom.entity;

import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter @Setter
public class Custom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="custom_id")
    private Integer customId;

    @OneToOne
    @JoinColumn(name="user_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "ai_id", nullable = false)
    private Ai ai;

    @Column(name = "is_formal")
    private boolean isFormal;

    @Column(name = "is_long")
    private boolean isLong;
}

