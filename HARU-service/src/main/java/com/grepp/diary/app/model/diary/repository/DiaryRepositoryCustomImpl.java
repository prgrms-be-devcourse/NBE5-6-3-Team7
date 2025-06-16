package com.grepp.diary.app.model.diary.repository;

import com.grepp.diary.app.model.diary.dto.DiaryEmotionStatsDto;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.diary.entity.DiaryImg;
import com.grepp.diary.app.model.diary.entity.QDiary;
import com.grepp.diary.app.model.diary.entity.QDiaryImg;
import com.grepp.diary.app.model.keyword.code.KeywordType;
import com.grepp.diary.app.model.keyword.dto.KeywordInfoDto;
import com.grepp.diary.app.model.keyword.entity.DiaryKeyword;
import com.grepp.diary.app.model.keyword.entity.QDiaryKeyword;
import com.grepp.diary.app.model.keyword.entity.QKeyword;
import com.grepp.diary.app.model.reply.dto.ReplyAdminDto;
import com.grepp.diary.app.model.reply.entity.QReply;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.Group;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DiaryRepositoryCustomImpl implements DiaryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    QDiary diary = QDiary.diary;
    QDiaryImg diaryImg = QDiaryImg.diaryImg;
    QDiaryKeyword diaryKeyword = QDiaryKeyword.diaryKeyword;
    QKeyword keyword = QKeyword.keyword;
    QReply reply = QReply.reply;

    @Override
    public List<Diary> findRecentDiariesWithImages(String userId, Pageable pageable) {
        return queryFactory
            .selectFrom(diary)
            .leftJoin(diary.images, diaryImg).fetchJoin() // Diary.images → DiaryImg에서 mappedBy되어야 함
            .where(
                diary.member.userId.eq(userId),
                diary.isUse.isTrue(),
                diaryImg.isUse.isTrue().or(diaryImg.isUse.isNull()) // 이미지 사용 중이거나 없는 경우 허용
            )
            .orderBy(diary.date.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .distinct() // 중복 Diary 제거 (fetchJoin 시 유용)
            .fetch();
    }

    @Override
    public Optional<Diary> findActiveDiaryByDateWithAllRelations(String userId, LocalDate targetDate) {
        Diary diaryWithImages = queryFactory
            .selectFrom(diary)
            .leftJoin(diary.images, diaryImg).fetchJoin()
            .leftJoin(diary.reply, reply).fetchJoin()
            .where(
                diary.member.userId.eq(userId),
                diary.date.eq(targetDate),
                diary.isUse.isTrue()
            )
            .distinct()
            .fetchOne(); // 결과가 1건이면 Diary 객체 반환, 없으면 null 반환

        // Diary가 없으면 바로 반환
        if (diaryWithImages == null) {
            return Optional.empty();
        }

        // 이미지 중 isUse == true인 것만 남기기
        if (diaryWithImages.getImages() != null) {
            List<DiaryImg> filteredImages = diaryWithImages.getImages().stream()
                                                           .filter(DiaryImg::getIsUse)
                                                           .collect(Collectors.toList());
            diaryWithImages.setImages(filteredImages);
        }

        // Diary와 keywords만 fetch join
        List<DiaryKeyword> keywordsList = queryFactory
            .selectFrom(diaryKeyword)
            .leftJoin(diaryKeyword.keywordId, keyword).fetchJoin()
            .where(diaryKeyword.diaryId.eq(diaryWithImages))
            .fetch();

        // keywords만 diaryWithImages에 병합
        if (keywordsList != null) {
            diaryWithImages.setKeywords(keywordsList);
        }

        return Optional.ofNullable(diaryWithImages);
    }

    @Override
    public Optional<Diary> findActiveDiaryByDiaryIdWithAllRelations(String userId, Integer diaryId) {
        Diary diaryWithImages = queryFactory
            .selectFrom(diary)
            .leftJoin(diary.images, diaryImg).fetchJoin()
            .leftJoin(diary.reply, reply).fetchJoin()
            .where(
                diary.member.userId.eq(userId),
                diary.diaryId.eq(diaryId),
                diary.isUse.isTrue()
            )
            .distinct()
            .fetchOne(); // 결과가 1건이면 Diary 객체 반환, 없으면 null 반환

        // Diary가 없으면 바로 반환
        if (diaryWithImages == null) {
            return Optional.empty();
        }

        // 이미지 중 isUse == true인 것만 남기기
        if (diaryWithImages.getImages() != null) {
            List<DiaryImg> filteredImages = diaryWithImages.getImages().stream()
                                                           .filter(DiaryImg::getIsUse)
                                                           .collect(Collectors.toList());
            diaryWithImages.setImages(filteredImages);
        }

        // Diary와 keywords만 fetch join
        List<DiaryKeyword> keywordsList = queryFactory
            .selectFrom(diaryKeyword)
            .leftJoin(diaryKeyword.keywordId, keyword).fetchJoin()
            .where(diaryKeyword.diaryId.eq(diaryWithImages))
            .fetch();

        // keywords만 diaryWithImages에 병합
        if (keywordsList != null) {
            diaryWithImages.setKeywords(keywordsList);
        }

        return Optional.ofNullable(diaryWithImages);
    }

    @Override
    public void deactivateDiaryByDiaryId(Integer id) {
        long updatedCount = queryFactory
            .update(diary)
            .set(diary.isUse, false)
            .where(diary.diaryId.eq(id))
            .execute();
    }

    @Override
    public List<Object[]> findDateAndEmotionByUserIdAndYear(String userId, int year) {
        return queryFactory
            .select(diary.date, diary.emotion)
            .from(diary)
            .where(
                diary.member.userId.eq(userId),
                diary.isUse.isTrue(),
                diary.date.year().eq(year)
            )
            .fetch()
            .stream()
            .map(tuple -> new Object[] {
                tuple.get(diary.date),
                tuple.get(diary.emotion)
            })
            .toList();
    }

    @Override
    public List<Object[]> findEmotionCountByUserIdAndMonth(String userId, int month) {
        return queryFactory
            .select(diary.emotion, diary.count())
            .from(diary)
            .where(
                diary.member.userId.eq(userId),
                diary.date.month().eq(month),
                diary.isUse.isTrue()
            )
            .groupBy(diary.emotion)
            .fetch()
            .stream()
            .map(tuple -> new Object[] {
                tuple.get(diary.emotion),
                tuple.get(1, Long.class)
            })
            .toList();
    }

    @Override
    public List<Object[]> findEmotionCountByUserIdAndYear(String userId, int year) {
        return queryFactory
            .select(diary.emotion, diary.count())
            .from(diary)
            .where(
                diary.member.userId.eq(userId),
                diary.date.year().eq(year),
                diary.isUse.isTrue()
            )
            .groupBy(diary.emotion)
            .fetch()
            .stream()
            .map(tuple -> new Object[] {
                tuple.get(diary.emotion),
                tuple.get(1, Long.class)
            })
            .toList();
    }

    @Override
    public List<DiaryEmotionStatsDto> findEmotionStatsByUserIdAndDate(String userId,
        LocalDate start, LocalDate end) {

        List<Tuple> result = queryFactory
            .select(
                diary.diaryId,
                diary.emotion,
                diary.date,
                keyword.name,
                keyword.type
            )
            .from(diary)
            .leftJoin(diary.keywords, diaryKeyword)
            .leftJoin(diaryKeyword.keywordId, keyword)
            .where(
                diary.member.userId.eq(userId),
                diary.date.between(start, end)
            )
            .orderBy(diary.date.asc())
            .fetch();

        Map<Integer, DiaryEmotionStatsDto> diaryMap = new LinkedHashMap<>();

        for (Tuple tuple : result) {
            Integer diaryId = tuple.get(diary.diaryId);
            DiaryEmotionStatsDto dto = diaryMap.get(diaryId);

            // 아직 데이터를 할당하지 않았을 때
            if (dto == null) {
                dto = new DiaryEmotionStatsDto();
                dto.setDiaryId(diaryId);
                dto.setEmotion(tuple.get(diary.emotion));
                dto.setDate(tuple.get(diary.date));
                dto.setKeywordInfoDtos(new ArrayList<>());
                diaryMap.put(diaryId, dto);
            }

            // 키워드 주입
            String keywordName = tuple.get(keyword.name);
            KeywordType keywordType = tuple.get(keyword.type);
            if (keywordName != null && keywordType != null) {
                dto.getKeywordInfoDtos().add(new KeywordInfoDto(keywordName, keywordType));
            }
        }

        return new ArrayList<>(diaryMap.values());
    }

    @Override
    public List<ReplyAdminDto> findByDateRangeAndStatus(LocalDateTime startDate, LocalDateTime endDate, String status) {

        BooleanExpression dateCondition = diary.createdAt.between(startDate, endDate);
        BooleanExpression statusCondition = null;

        if (status.equals("replied")) {
            statusCondition = reply.createdAt.isNotNull();
        } else if (status.equals("unreplied")) {
            statusCondition = reply.createdAt.isNull();
        }

        return queryFactory
            .select(Projections.constructor(
                ReplyAdminDto.class,
                diary.member.userId,
                diary.diaryId,
                diary.date,
                diary.createdAt,
                reply.createdAt,
                reply.createdAt.isNotNull()
            ))
            .from(diary)
            .leftJoin(reply).on(reply.diary.eq(diary))
            .where(dateCondition, statusCondition)
            .fetch();
    }

}
