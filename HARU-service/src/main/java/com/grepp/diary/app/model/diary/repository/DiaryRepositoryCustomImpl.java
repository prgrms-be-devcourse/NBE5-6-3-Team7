package com.grepp.diary.app.model.diary.repository;

import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.diary.entity.DiaryImg;
import com.grepp.diary.app.model.diary.entity.QDiary;
import com.grepp.diary.app.model.diary.entity.QDiaryImg;
import com.grepp.diary.app.model.keyword.entity.DiaryKeyword;
import com.grepp.diary.app.model.keyword.entity.QDiaryKeyword;
import com.grepp.diary.app.model.keyword.entity.QKeyword;
import com.grepp.diary.app.model.reply.entity.QReply;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
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
}
