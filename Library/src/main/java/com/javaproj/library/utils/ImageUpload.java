package com.javaproj.library.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class ImageUpload {
    private final String UPLOAD_FOLDER = "C:\\Users\\ckwJa\\Desktop\\GITJAVAZAJECIA\\javaprojFinal\\javaprojFinal\\Admin\\src\\main\\resources\\static\\image-product";
    public boolean uploadFile(MultipartFile file) {
        boolean isUpload = false;
        try {
            Files.copy(file.getInputStream(), Paths.get(UPLOAD_FOLDER + File.separator + file.getOriginalFilename()) , StandardCopyOption.REPLACE_EXISTING);
            isUpload = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isUpload;
    }

    public boolean checkExist(MultipartFile multipartFile){
        boolean isExist = false;
        try {
            File file = new File(UPLOAD_FOLDER +"\\" + multipartFile.getOriginalFilename());
            isExist = file.exists();
        }catch (Exception e){
            e.printStackTrace();
        }
        return isExist;
    }
    public boolean uploadFileFromBytes(byte[] fileBytes, String uniqueFilename) {
        boolean isUpload = false;
        try {
            Files.write(Paths.get(UPLOAD_FOLDER + File.separator + uniqueFilename), fileBytes);
            isUpload = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isUpload;
    }
}
