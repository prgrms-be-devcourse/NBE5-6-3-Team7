package com.grepp.diary.app.controller.web.diary.payload;

import com.grepp.diary.app.model.diary.code.Emotion;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class DiaryRequest {

    private String content;
    private Emotion emotion;
    private List<String> keywords = new ArrayList<>();
    private LocalDate date;
    // Multipart 파일 업로드용
    @Size(max = 1)
    private List<MultipartFile> images;
}
