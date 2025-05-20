package com.example.easyplan.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3UploadService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<String> saveFile(List<MultipartFile> multipartFiles) throws IOException {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            String originalFilename = file.getOriginalFilename();
            String uuidFilename = UUID.randomUUID() + "_" + originalFilename;

            //파일에 대한 정보를 담는 객체
            ObjectMetadata metadata = new ObjectMetadata();

            //파일 크기 설정
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            //업로드 하는 부분
            try{
                amazonS3.putObject(bucket, uuidFilename, file.getInputStream(), metadata);
                fileUrls.add(amazonS3.getUrl(bucket, uuidFilename).toString());
            }catch (Exception e){
                log.info("업로드 실패");
                e.printStackTrace();
                throw e;
            }
        }
        return fileUrls;
    }

    public void  deleteFile(String originalFilename) {
        amazonS3.deleteObject(bucket, originalFilename);
    }


    public void deleteFileFromUrls(List<String> urls){
        for(String url : urls){
            String fileName = extractFileNameFromUrl(url);
            amazonS3.deleteObject(bucket, fileName);
        }
    }

    private String extractFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
