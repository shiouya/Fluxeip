package com.example.fluxeip.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
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
            // 確保資料夾存在
            Files.createDirectories(rootLocation);

            // 生成唯一檔名
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // 儲存檔案
            Path targetLocation = rootLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // **返回相對路徑 (適用於 API URL)**
            return "uploads/attachment/" + filename;  
        } catch (IOException e) {
            throw new RuntimeException("檔案上傳失敗", e);
        }
    }
    
//    public String extractOriginalFileName(String attachmentPath) {
//        if (attachmentPath != null && !attachmentPath.isEmpty()) {
//            try {
//                // 解碼 URL，處理中文和特殊字符
//                attachmentPath = URLDecoder.decode(attachmentPath, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                // 處理編碼錯誤
//                e.printStackTrace();
//            }
//
//            // 使用平台相關的檔案分隔符
//            FileSystem fs = FileSystems.getDefault();
//            String[] pathParts = attachmentPath.split(fs.getSeparator());  // 使用平台的檔案分隔符
//            String fileNameWithUUID = pathParts[pathParts.length - 1];  // 取得檔案名稱部分
//
//            // 假設檔案名稱以 UUID 和原始檔案名稱組成
//            String[] nameParts = fileNameWithUUID.split("_");  // 假設 UUID 和檔案名稱之間用 "_" 分隔
//
//            if (nameParts.length > 1) {
//                // 返回原始檔案名稱，去除 UUID 部分
//                return nameParts[nameParts.length - 1];
//            } else {
//                // 如果沒有 "_", 直接返回檔案名稱
//                return fileNameWithUUID;
//            }
//        }
//        return "123";
//    }
//    public String extractOriginalFileName(String attachmentPath) {
//        if (attachmentPath != null && !attachmentPath.isEmpty()) {
//            // 假設檔案名稱包含 UUID 和原始檔案名稱
//            String[] parts = attachmentPath.split("_"); // 假設 UUID 是檔案名稱的一部分
//            String originalFileName = parts.length > 1 ? parts[1] : attachmentPath;  // 取得原始檔案名稱
//            return originalFileName;
//        } 
//        return null;
//    }

    public String extractOriginalFileName(String attachmentPath) {
        if (attachmentPath != null && !attachmentPath.isEmpty()) {
            try {
                // 解碼 URL 來處理空格和其他特殊字符
                attachmentPath = URLDecoder.decode(attachmentPath, "UTF-8");

                // 假設檔案名稱是以 UUID 開頭，且兩者由 "_" 分隔
                String[] parts = attachmentPath.split("_", 2);  // 限制分割為兩部分
                if (parts.length > 1) {
                    // 如果有兩部分，返回第二部分作為檔案名稱
                    return parts[1];
                } else {
                    // 如果無法分割，則返回整個路徑
                    return attachmentPath;
                }
            } catch (UnsupportedEncodingException e) {
                // 處理編碼異常
                System.out.println("解碼失敗: " + e.getMessage());
                return null;
            }
        }
        return null;
    }

}
