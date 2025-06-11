package com.grepp.diary.app.model.ai.repository;

import com.grepp.diary.app.model.ai.entity.Ai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiRepository extends JpaRepository<Ai, Integer>, AIRepositoryCustom {

}
