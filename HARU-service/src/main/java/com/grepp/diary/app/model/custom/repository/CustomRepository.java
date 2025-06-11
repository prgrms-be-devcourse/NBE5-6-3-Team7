package com.grepp.diary.app.model.custom.repository;

import com.grepp.diary.app.model.custom.entity.Custom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomRepository extends JpaRepository<Custom, Integer>, CustomRepositoryCustom {

    Optional<Custom> findByMemberUserId(String userId);
}
