package com.example.fluxeip.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    private final Path rootLocation = Paths.get("uploads/attachment");

    public String saveFile(MultipartFile file) {
        try {
            // 確保 upload 資料夾存在
            Files.createDirectories(rootLocation);

            // 生成文件名稱
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // 儲存文件
            Path targetLocation = rootLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 返回文件的路徑
            return targetLocation.toString();
        } catch (IOException e) {
            throw new RuntimeException("檔案上傳失敗", e);
        }
    }
}
