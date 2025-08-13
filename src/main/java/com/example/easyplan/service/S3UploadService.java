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

    /**
     * 단일 파일을 S3에 업로드하고 업로드된 파일의 URL을 반환합니다.
     */
    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new IllegalArgumentException("File cannot be empty or null.");
        }

        String originalFilename = file.getOriginalFilename();
        // S3에 저장할 고유한 파일명 생성 (UUID + 원본 파일명)
        String uuidFilename = UUID.randomUUID() + "_" + originalFilename;

        // 파일 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        // S3 업로드
        try {
            // PutObjectRequest 객체 생성
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, uuidFilename, file.getInputStream(), metadata);

            // !! ACL 설정 부분을 제거합니다. (The bucket does not allow ACLs 오류 해결)
            // .withCannedAcl(CannedAccessControlList.PublicRead) 옵션 삭제

            amazonS3.putObject(putObjectRequest);

            // 업로드된 파일의 URL 반환
            return amazonS3.getUrl(bucket, uuidFilename).toString();
        } catch (Exception e) {
            log.error("S3 파일 업로드 실패: {}", e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to upload file to S3", e);
        }
    }

    // ... (saveFile, deleteFile, deleteFileFromUrls, extractFileNameFromUrl 메서드는 이전과 동일합니다.)

    public List<String> saveFile(List<MultipartFile> multipartFiles) throws IOException {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            fileUrls.add(uploadFile(file));
        }
        return fileUrls;
    }

    public void  deleteFile(String filename) {
        amazonS3.deleteObject(bucket, filename);
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