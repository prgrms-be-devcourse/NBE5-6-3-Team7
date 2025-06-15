package com.grepp.diary.app.model.ai;

import com.grepp.diary.app.controller.api.admin.payload.AdminAiWriteRequest;
import com.grepp.diary.app.model.ai.dto.AiImgDto;
import com.grepp.diary.app.model.ai.dto.AiInfoDto;
import com.grepp.diary.app.model.ai.dto.AiDto;
import com.grepp.diary.app.model.ai.dto.AiWithPathDto;
import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.ai.entity.AiImg;
import com.grepp.diary.app.model.ai.repository.AiImgRepository;
import com.grepp.diary.app.model.ai.repository.AiRepository;
import com.grepp.diary.app.model.common.code.ImgType;
import com.grepp.diary.infra.error.exceptions.CommonException;
import com.grepp.diary.infra.response.ResponseCode;
import com.grepp.diary.infra.util.file.FileDto;
import com.grepp.diary.infra.util.file.FileUtil;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AiService {

    private final AiRepository aiRepository;
    private final AiImgRepository aiImgRepository;
    private final FileUtil fileUtil;

    public AiDto getSingleAi(Integer id) {
        Ai ai = aiRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("AI not found"));

        return AiDto.fromEntity(ai);
    }

    public List<AiDto> getAllAi() {
        List<Ai> aiList = aiRepository.findAll();

        return aiList.stream()
            .map(AiDto::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional
    public List<Ai> modifyAiActivate(List<Integer> aiIds) {
        List<Ai> aiList = new ArrayList<>();

        for (Integer id : aiIds) {
            Ai ai = aiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AI not found with id: " + id));
            ai.setIsUse(true);
            aiList.add(ai);
        }

        return aiRepository.saveAll(aiList);
    }

    @Transactional
    public List<Ai> modifyAiNonActivate(List<Integer> aiIds) {
        List<Ai> aiList = new ArrayList<>();

        for (Integer id : aiIds) {
            Ai ai = aiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AI not found with id: " + id));
            ai.setIsUse(false);
            aiList.add(ai);
        }

        return aiRepository.saveAll(aiList);
    }

    @Transactional
    public Ai modifyAi(List<MultipartFile> images, AdminAiWriteRequest request) {
        try {

            Optional<Ai> optionalAi = aiRepository.findById(request.getId());

            if (optionalAi.isEmpty()) {
                throw new RuntimeException("Ai not found");
            }

            Ai ai = optionalAi.get();
            ai.setName(request.getName());
            ai.setMbti(request.getMbti());
            ai.setInfo(request.getInfo());
            ai.setPrompt(request.getPrompt());

            aiRepository.save(ai);

            if (images != null && !images.isEmpty()) {
                Integer aiId = ai.getAiId();
                aiImgRepository.makeRestImgFalse(aiId);

                List<FileDto> imageList = fileUtil.upload(images, "ai", ai.getAiId());
                AiImg aiImg = new AiImg(ImgType.THUMBNAIL, imageList.getFirst());

                aiImg.setAi(ai);
                aiImg.setIsUse(true);

                aiImgRepository.save(aiImg);
            }

            return ai;
        } catch (IOException e) {
            throw new CommonException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public Ai createAi(List<MultipartFile> images, AdminAiWriteRequest request) {
        try {
            Ai ai = new Ai();

            ai.setName(request.getName());
            ai.setMbti(request.getMbti());
            ai.setInfo(request.getInfo());
            ai.setPrompt(request.getPrompt());
            ai.setIsUse(true);

            aiRepository.save(ai);

            if (images.size() > 0) {
                List<FileDto> imageList = fileUtil.upload(images, "ai", ai.getAiId());
                AiImg aiImg = new AiImg(ImgType.THUMBNAIL, imageList.getFirst());

                aiImg.setAi(ai);
                aiImg.setIsUse(true);

                aiImgRepository.save(aiImg);
            }

            return ai;
        } catch (IOException e) {
            throw new CommonException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    public List<AiInfoDto> getAIList() {
        return aiRepository.getAIListDto();
    }

    public Optional<Ai> findById(Integer id) {
        return aiRepository.findById(id);
    }

    @Transactional
    public List<Ai> deleteAi(List<Integer> requests) {
        List<Ai> results = new ArrayList<>();

        for (Integer request : requests) {

            Optional<Ai> optionalAi = aiRepository.findById(request);

            if (optionalAi.isEmpty()) {
                throw new RuntimeException("Ai not found");
            }

            Ai ai = optionalAi.get();
            ai.setDeletedAt(LocalDateTime.now());
            ai.setIsUse(false);

            results.add(ai);
        }

        aiRepository.saveAllAndFlush(results);

        return results;
    }

    public List<AiImgDto> getAiImgList() {
        return aiImgRepository.findAll().stream().map(AiImgDto::fromEntity).toList();
    }

    public List<AiWithPathDto> getImgPathList() {
        return aiImgRepository.getAiImgInfoActivated();
    }
}
