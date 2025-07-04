package com.grepp.diary.app.controller.api.diary;

import com.grepp.diary.app.controller.api.diary.payload.DiaryCalendarResponse;
import com.grepp.diary.app.controller.api.diary.payload.DiaryCardResponse;
import com.grepp.diary.app.controller.api.diary.payload.DiaryEditRequest;
import com.grepp.diary.app.model.diary.DiaryService;
import com.grepp.diary.infra.util.date.DateUtil;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary")
public class DiaryApiController {

    private final DiaryService diaryService;
    private final DateUtil dateUtil;

    @GetMapping("/calendar")
    public DiaryCalendarResponse getEmotionsForCalendar(
        Authentication authentication,
        @RequestParam int year,
        @RequestParam int month
    ) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        String userId = authentication.getName();

        return DiaryCalendarResponse.fromEntityList(
            diaryService.getDiariesDateBetween(userId, start, end)
        );
    }

    @GetMapping("/cards")
    public DiaryCardResponse getDiaryCards( // 기본적으로는 최근 작성된 14개의 일기를 가져옵니다.
        Authentication authentication,
        @RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue = "14") int size,
        @RequestParam(defaultValue = "all") String filter
    ) {
        String userId = authentication.getName();

        return DiaryCardResponse.fromEntityList(
            diaryService.getDiariesWithImages(userId, page, size, filter)
        );
    }

    // 특정 날에 대한 일기 유무 API
    @GetMapping("/check")
    public boolean checkDiaryOfDay(
        Authentication authentication,
        @RequestParam LocalDate date
    ) {
//        LocalDate nextDate = date.plusDays(1);
        String userId = authentication.getName();

        return !diaryService.getDiaryAtDate(userId, date).isEmpty();
    }

    @PatchMapping
    public ResponseEntity<?> editDiary(
        @RequestPart("request") DiaryEditRequest request,
        @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages,
        @AuthenticationPrincipal UserDetails user
    ) {
        String username = user.getUsername();

        try {
            diaryService.updateDiary(username, request, newImages);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiary(@PathVariable Integer id,
        @AuthenticationPrincipal UserDetails user
    )
    {
        String username = user.getUsername();

        try {
            diaryService.deleteDiary(id, username);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
