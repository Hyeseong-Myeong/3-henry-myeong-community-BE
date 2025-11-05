package com.ktb.ktb_community.controller;

import com.ktb.ktb_community.common.advice.ApiResponse;
import com.ktb.ktb_community.dto.S3RequestDto;
import com.ktb.ktb_community.dto.S3ResponseDto;
import com.ktb.ktb_community.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping
    public ResponseEntity<ApiResponse<S3ResponseDto>> getUploadUrl(@RequestBody S3RequestDto s3RequestDto){

        String uniqueFileName = s3Service.generateRandomFileName(s3RequestDto.getFilename());

        String preSignedUrl = s3Service.generateUploadPreSignedUrl(uniqueFileName);

        ApiResponse<S3ResponseDto> response = ApiResponse.success(
                "upload_url_generate_success",
                new S3ResponseDto(preSignedUrl, uniqueFileName)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<S3ResponseDto>> getDownloadUrl(@RequestParam("filename") String filename){

        String preSignedUrl = s3Service.generateDownloadPresignedUrl(filename);

        ApiResponse<S3ResponseDto> response = ApiResponse.success(
                "download_url_generate_success",
                new S3ResponseDto(preSignedUrl, filename)
        );

        return ResponseEntity.ok(response);
    }

}
