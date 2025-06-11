package com.grepp.diary.app.model.keyword.repository;

import com.grepp.diary.app.model.keyword.entity.Keyword;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Integer>, KeywordRepositoryCustom {

    Optional<Keyword> findByName(String name);
}
