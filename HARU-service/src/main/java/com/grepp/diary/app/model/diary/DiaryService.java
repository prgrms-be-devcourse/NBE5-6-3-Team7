package com.grepp.diary.app.model.diary;

import com.grepp.diary.app.controller.api.diary.payload.DiaryEditRequest;
import com.grepp.diary.app.controller.web.diary.payload.DiaryRequest;
import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.common.code.ImgType;
import com.grepp.diary.app.model.diary.code.Emotion;
import com.grepp.diary.app.model.diary.dto.DiaryDto;
import com.grepp.diary.app.model.diary.dto.DiaryEmotionAvgDto;
import com.grepp.diary.app.model.diary.dto.DiaryEmotionCountDto;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.diary.entity.DiaryImg;
import com.grepp.diary.app.model.diary.repository.DiaryImgRepository;
import com.grepp.diary.app.model.diary.repository.DiaryKeywordRepository;
import com.grepp.diary.app.model.diary.repository.DiaryRepository;
import com.grepp.diary.app.model.keyword.entity.DiaryKeyword;
import com.grepp.diary.app.model.keyword.entity.Keyword;
import com.grepp.diary.app.model.keyword.repository.KeywordRepository;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.app.model.member.repository.MemberRepository;
import com.grepp.diary.app.model.reply.ReplyRepository;
import com.grepp.diary.app.model.reply.entity.Reply;
import com.grepp.diary.infra.error.exceptions.CommonException;
import com.grepp.diary.infra.response.ResponseCode;
import com.grepp.diary.infra.util.file.FileDto;
import com.grepp.diary.infra.util.file.FileUtil;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;
    private final KeywordRepository keywordRepository;
    private final DiaryImgRepository diaryImgRepository;
    private final DiaryKeywordRepository diaryKeywordRepository;

    private final FileUtil fileUtil;
    private final ReplyRepository replyRepository;

    /** 시작일과 끝을 기준으로 해당 날짜 사이에 존재하는 일기들을 반환합니다. */
    public List<Diary> getDiariesDateBetween(String userId, LocalDate start, LocalDate end) {
        return diaryRepository.findByMemberUserIdAndDateBetweenAndIsUseTrueOrderByDateAsc(userId, start, end);

    }

    public List<Diary> getDiaryAtDate(String userId, LocalDate date){
        return diaryRepository.findByMemberUserIdAndDateAndIsUseTrue(userId, date);
    }

    public Integer getMonthDiariesCount() {
        LocalDateTime startDateTime = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endDateTime = startDateTime.plusMonths(1);

        return diaryRepository.countByCreatedAtBetweenAndIsUseTrue(startDateTime, endDateTime);
    }

    /** 시작일과 끝을 기준으로 해당 날짜 사이에 존재하는 일기들의 개수를 반환합니다. */
    public Integer getUserDiaryCount(String userId, LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();
        return diaryRepository.countByMemberUserIdAndCreatedAtBetweenAndIsUseTrue(userId, startDateTime, endDateTime);
    }

    /**
     * 작성된 일기들을 size 만큼 가져옵니다.
     * 일기들은 포함된 이미지와 함께 반환됩니다.
     * 일기들을 하나의 페이지에 표시하기 위해 사용됩니다.
     * */
    public List<Diary> getDiariesWithImages(String userId, int page, int size) {
        Pageable limit = PageRequest.of(page, size);
        return diaryRepository.findRecentDiariesWithImages(userId, limit);
    }

    public Diary getDiaryById(int diaryId) {
        Optional<Diary> result = diaryRepository.findById(diaryId);
        if (result.isEmpty()) {
            throw new RuntimeException("일기가 존재하지 않습니다.");
        }
        return result.get();
    }

    public DiaryDto getDto(int diaryId) {
        Optional<Diary> result = diaryRepository.findById(diaryId);
        if (result.isEmpty()) {
            throw new RuntimeException("일기가 존재하지 않습니다.");
        }
        return DiaryDto.fromEntity(result.get());
    }

    public List<DiaryDto> getNoReplyDtos() {
        List<Diary> diaries = diaryRepository.findAll();
        List<Diary> NoReplyDiaries = diaries.stream().filter(e -> e.getReply() == null).toList();
        return NoReplyDiaries.stream().map(DiaryDto::fromEntity).toList();
    }

    @Transactional
    public void registReply(int diaryId, String replyContent) {
        Optional<Diary> result = diaryRepository.findById(diaryId);
        if (result.isEmpty()) {
            throw new RuntimeException("일기가 존재하지 않습니다.");
        }

        Diary diary = result.get();
        Reply reply = diary.getReply();
        Member member = diary.getMember();
        Custom custom = member.getCustom();
        Ai ai = custom.getAi();

        if (reply == null) { // 등록된 reply X
            reply = new Reply();
            reply.setContent(replyContent);
            reply.setAi(ai);
            reply.setDiary(diary);
            diary.setReply(reply);
        } else { // 등록된 reply O
            reply.setContent(replyContent);
            reply.setAi(ai);
        }
        diaryRepository.save(diary);
    }

    @Transactional
    public Diary saveDiary(DiaryRequest form, String userId) {
        try {

            Member member = memberRepository
                .findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다: " + userId));

            LocalDate targetDate = form.getDate();
            boolean exists = diaryRepository.existsByMember_UserIdAndDateAndIsUse(userId, targetDate, true);
            if (exists) {
                throw new CommonException(ResponseCode.DIARY_ALREADY_EXISTS);
            }

            Diary diary = new Diary();
            diary.setEmotion(form.getEmotion());
            diary.setContent(form.getContent());
            diary.setCreatedAt(LocalDateTime.now());
            diary.setModifiedAt(LocalDateTime.now());
            diary.setDate(form.getDate());
            diary.setIsUse(true);
            diary.setMember(member);
            Diary savedDiary = diaryRepository.save(diary);
            diaryRepository.flush();

            // 키워드를 선택했을 경우 키워드 저장
            if (form.getKeywords() != null && !form.getKeywords().isEmpty()) {
                List<DiaryKeyword> keywordList = form
                    .getKeywords()
                    .stream()
                    .distinct()
                    .map(name -> {
                        Keyword keywordEntity = keywordRepository
                            .findByName(name)
                            .orElseThrow(() -> new IllegalArgumentException("키워드 없음: " + name));
                        DiaryKeyword dk = new DiaryKeyword();
                        dk.setDiaryId(diary);
                        dk.setKeywordId(keywordEntity);
                        return dk;
                    })
                    .collect(Collectors.toList());

                diaryKeywordRepository.saveAll(keywordList);
            }


            // 사진을 업로드 했을 경우 사진 저장
            if (form.getImages() != null && !form.getImages().isEmpty()) {
                List<FileDto> imageList = fileUtil.upload(form.getImages(), "diary", savedDiary.getDiaryId());

                List<DiaryImg> diaryImgs = imageList.stream()
                                                    .map(fileDto -> {
                                                        DiaryImg diaryImg = new DiaryImg(ImgType.THUMBNAIL, fileDto);
                                                        diaryImg.setDiary(savedDiary);
                                                        return diaryImg;
                                                    })
                                                    .collect(Collectors.toList());
                diaryImgRepository.saveAll(diaryImgs);
            }
            return diary;
        } catch (IOException e) {
            throw new CommonException(ResponseCode.INTERNAL_SERVER_ERROR,e.getMessage());
        }

    }

    public Optional<Diary> findDiaryByUserIdAndDate(String userId, LocalDate targetDate) {
        return diaryRepository.findActiveDiaryByDateWithAllRelations(userId, targetDate);
    }

    @Transactional
    public void deleteDiary(Integer id, String username) throws AccessDeniedException {
        Diary diary = diaryRepository.findById(id)
                                     .orElseThrow(() -> new EntityNotFoundException("Diary not found"));

        if (!diary.getMember().getUserId().equals(username)) {
            throw new AccessDeniedException("해당 일기를 삭제할 권한이 없습니다.");
        }

        diaryImgRepository.deactivateDiaryImgByDiaryId(id);
        diaryRepository.deactivateDiaryByDiaryId(id);
        replyRepository.deactivateReplyByDiaryId(id);
    }

    @Transactional
    public void updateDiary(String username, DiaryEditRequest request, List<MultipartFile> newImages)
        throws AccessDeniedException {
        Diary diary = diaryRepository.findById(request.getDiaryId())
                                     .orElseThrow(() -> new EntityNotFoundException("Diary not found"));
        if (!diary.getMember().getUserId().equals(username)) {
            throw new AccessDeniedException("해당 일기를 삭제할 권한이 없습니다.");
        }

        diary.setEmotion(Emotion.valueOf(request.getEmotion()));
        diary.setContent(request.getContent());
        diary.setDate(request.getDate());

        diaryKeywordRepository.deleteByDiaryId(diary);
        // 키워드를 선택했을 경우 키워드 저장
        if (request.getKeywords() != null && !request.getKeywords().isEmpty()) {
            List<DiaryKeyword> keywordList = request
                .getKeywords()
                .stream()
                .distinct()
                .map(name -> {
                    Keyword keywordEntity = keywordRepository
                        .findByName(name)
                        .orElseThrow(() -> new IllegalArgumentException("키워드 없음: " + name));
                    DiaryKeyword dk = new DiaryKeyword();
                    dk.setDiaryId(diary);
                    dk.setKeywordId(keywordEntity);
                    return dk;
                })
                .collect(Collectors.toList());

            diaryKeywordRepository.saveAll(keywordList);
        }

        for (Integer deletedImageId : request.getDeletedImageIds()) {
            diaryImgRepository.deactivateDiaryImgByDiaryId(deletedImageId);
        }

        Diary updateDiary = diaryRepository.findById(request.getDiaryId())
                                     .orElseThrow(() -> new EntityNotFoundException("Diary not found"));


        if (newImages != null && !newImages.isEmpty()) {
            List<FileDto> imageList = null;
            try {
                imageList = fileUtil.upload(newImages, "diary", request.getDiaryId());
            } catch (IOException e) {
                throw new CommonException(ResponseCode.INTERNAL_SERVER_ERROR,e.getMessage());
            }

            List<DiaryImg> diaryImgs = imageList.stream()
                                                .map(fileDto -> {
                                                    DiaryImg diaryImg = new DiaryImg(ImgType.THUMBNAIL, fileDto);
                                                    diaryImg.setDiary(updateDiary);
                                                    return diaryImg;
                                                })
                                                .collect(Collectors.toList());
            diaryImgRepository.saveAll(diaryImgs);
        }
    }

    /** 특정 년도에 작성된 일기들을 기준으로 월별 평균 기분점수를 반환합니다. */
    public List<DiaryEmotionAvgDto> getMonthlyEmotionAvgOfYear(String userId, int year){
        List<Object []> emotionsByDate = diaryRepository.findDateAndEmotionByUserIdAndYear(userId, year);

        Map<Emotion, Integer> emotionScore = Map.of(
            Emotion.VERY_GOOD, 5,
            Emotion.GOOD, 4,
            Emotion.COMMON, 3,
            Emotion.BAD, 2,
            Emotion.VERY_BAD, 1
        );

        // 월별 감정 점수 모으기
        Map<Integer, List<Integer>> monthToScores = new HashMap<>();
        for (Object [] row : emotionsByDate) {
            LocalDate date = (LocalDate) row[0];
            Emotion emotion = (Emotion) row[1];
            int month = date.getMonthValue();

            int score = emotionScore.getOrDefault(emotion, 0);
            monthToScores.computeIfAbsent(month, k -> new ArrayList<>()).add(score);
        }

        // 평균 계산
        List<DiaryEmotionAvgDto> result = new ArrayList<>();
        for(int month = 1; month <= 12; month++) {
            List<Integer> scores = monthToScores.get(month);
            if(scores == null || scores.isEmpty()) {
                continue;   // 일기가 없는 달은 생략 하도록 합니다.
            }

            double avg = scores.stream().mapToInt(i -> i).average().orElse(0.0);
            result.add(new DiaryEmotionAvgDto(month, avg));
        }

        return result;
    }

    /**
     * 기준 달 또는 연도에 작성된 일기들의 감정별 갯수를 반환합니다.
     * 사용되지 않은 감정의 경우 0으로 처리됩니다.
     * */
    public List<DiaryEmotionCountDto> getEmotionsCount(String userId, String period, int value){
        Map<Emotion, Integer> countMap = Arrays.stream(Emotion.values())
            .collect(Collectors.toMap(e -> e, e -> 0, (a, b) -> a, () -> new EnumMap<>(Emotion.class)));

        if("monthly".equals(period)){
            return diaryRepository.findEmotionCountByUserIdAndMonth(userId, value)
                .stream()
                .map(row -> new DiaryEmotionCountDto((Emotion) row[0],
                    Math.toIntExact((Long) row[1])))
                .toList();
        } else if("yearly".equals(period)){
            return diaryRepository.findEmotionCountByUserIdAndYear(userId, value)
                .stream()
                .map(row -> new DiaryEmotionCountDto((Emotion) row[0],
                    Math.toIntExact((Long) row[1])))
                .toList();
        } else {
            throw new IllegalArgumentException("Unsupported period: " + period);
        }
    }

    public Optional<Diary> findDiaryByUserIdAndDiaryId(String userId, Integer diaryId) {
        return diaryRepository.findActiveDiaryByDiaryIdWithAllRelations(userId, diaryId);
    }
}

