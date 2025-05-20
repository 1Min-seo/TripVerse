package com.example.easyplan.controller;

import com.example.easyplan.service.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class S3UploadController {
    private final S3UploadService s3UploadService;

    @PostMapping("/images/new")
    public ResponseEntity<List<String>> uploadImages(@RequestParam("files") List<MultipartFile> files){
        try{
            List<String> uploadFiles = s3UploadService.saveFile(files);
            return ResponseEntity.ok(uploadFiles);
        } catch(IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @DeleteMapping("/images")
    public ResponseEntity<String> deleteImages(@RequestParam("files") List<String> filenames){
        try{
            for(String filename : filenames){
                s3UploadService.deleteFile(filename);
            }
            return ResponseEntity.ok("Image deleted successfully");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed to delete images");
        }
    }
}
