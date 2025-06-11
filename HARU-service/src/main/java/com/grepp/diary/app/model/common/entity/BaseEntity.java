package com.grepp.diary.app.model.common.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString
public class BaseEntity {
    @CreatedDate
    protected LocalDateTime createdAt = LocalDateTime.now();
    @LastModifiedDate
    protected LocalDateTime modifiedAt = LocalDateTime.now();
}
