package com.grepp.diary.app.model.ai.repository;

import com.grepp.diary.app.model.ai.entity.AiImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiImgRepository extends JpaRepository<AiImg, Integer>, AiImgRepositoryCustom {

}
