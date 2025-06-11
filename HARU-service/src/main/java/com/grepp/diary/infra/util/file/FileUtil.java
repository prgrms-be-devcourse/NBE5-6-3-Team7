package com.grepp.diary.infra.util.file;

import com.grepp.diary.app.model.common.code.ImgType;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.diary.entity.DiaryImg;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUtil {

    String projectRoot = System.getProperty("user.dir");  //프로젝트 폴더 경로
    String uploadDir = projectRoot + File.separator + "photo" + File.separator;
    private String filePath = uploadDir;
    
    public List<FileDto> upload(List<MultipartFile> files, String depthKind, Integer kindId) throws IOException {
        List<FileDto> fileDtos = new ArrayList<>();

        if (files.isEmpty() || files.getFirst().isEmpty()) {
            return fileDtos;
        }
        String savePath = createSavePath(depthKind, kindId);

        for (MultipartFile file : files) {
            String originFileName = file.getOriginalFilename();
            String renameFileName = generateRenameFileName(originFileName);
            FileDto fileDto = new FileDto(originFileName, renameFileName, savePath);
            fileDtos.add(fileDto);
            uploadFile(file, fileDto);
        }

        return fileDtos;
    }

    private void uploadFile(MultipartFile file, FileDto fileDto) throws IOException {
        File path = new File(filePath + fileDto.savePath());
        if (!path.exists()) {
            path.mkdirs();
        }

        File target = new File(filePath + fileDto.savePath() + fileDto.renameFileName());
        file.transferTo(target);
    }

    private String generateRenameFileName(String originFileName) {
        String ext = originFileName.substring(originFileName.lastIndexOf("."));
        return UUID.randomUUID().toString() + ext;
    }

    private String createSavePath(String depthKind, Integer kindId) {
        return depthKind + "/" + kindId + "/";

    }
}
