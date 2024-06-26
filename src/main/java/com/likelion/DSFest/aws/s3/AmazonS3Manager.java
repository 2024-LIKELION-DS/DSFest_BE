package com.likelion.DSFest.aws.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager{
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3Client amazonS3Client;
    public String uploadFile(MultipartFile file){
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        String storeFilename = UUID.randomUUID() + "." + extractExt(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, storeFilename, file.getInputStream(), metadata));
        }catch (IOException e){
            log.error("error at AmazonS3Manager uploadFile : {}", (Object) e.getStackTrace());
        }

        return amazonS3Client.getUrl("dsfest2024", storeFilename).toString();
    }

    public void deleteFile(String filename) {
        amazonS3Client.deleteObject(bucket, filename);
    }




    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}