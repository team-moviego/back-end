package com.hwansol.moviego.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.hwansol.moviego.common.NotFoundException;
import com.hwansol.moviego.image.dto.ImageGetDto;
import com.hwansol.moviego.image.exception.ReadImageException;
import com.hwansol.moviego.image.model.Image;
import com.hwansol.moviego.image.repository.ImageRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final ImageRepository imageRepository;
    private final AmazonS3 r2Client;

    @Value("${r2.bucketName}")
    private String bucketName;

    /**
     * 파일 조회 서비스
     *
     * @param id 조회할 파일 pk
     * @return 조회된 파일 엔티티
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "image", key = "#id")
    public ImageGetDto.Response getFile(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        S3Object object = r2Client.getObject(bucketName, image.getStoreImageName());

        byte[] imageData;
        try (S3ObjectInputStream inputStream = object.getObjectContent();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }

            imageData = baos.toByteArray();
        } catch (IOException e) {
            throw new ReadImageException();
        }

        String contentType = "image/webp";

        return ImageGetDto.Response.from(image, contentType, imageData);
    }

    /**
     * 파일 생성 서비스
     *
     * @param fileList 생성할 파일 리스트
     * @return 생성된 파일 엔티티 리스트
     */
    @Transactional
    public List<Image> createFile(List<MultipartFile> fileList) {
        return createFileEntity(fileList);
    }

    // r2에 파일을 저장하는 메소드
    private void uploadFile(String storeFileName, MultipartFile file) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize()); // 파일 크기 메타데이터 생성

        r2Client.putObject(bucketName, storeFileName, file.getInputStream(), objectMetadata);
    }

    // 파일 엔티티 생성하는 메소드
    private List<Image> createFileEntity(List<MultipartFile> fileList) {
        // 확장자
        // 중복된 파일 이름을 피하기 위해

        return fileList.stream()
                .map(f -> {
                    String originalFilename = f.getOriginalFilename();

                    String extension = ""; // 확장자
                    int dotIndex = Objects.requireNonNull(originalFilename).lastIndexOf('.');
                    if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
                        extension = originalFilename.substring(dotIndex + 1);
                    }

                    String storeFileName = UUID.randomUUID() + originalFilename; // 중복된 파일 이름을 피하기 위해

                    try {
                        uploadFile(storeFileName, f);
                    } catch (IOException e) {
                        log.error("{} 파일을 r2에 저장하는데 실패하였습니다.", originalFilename, e);
                        throw new RuntimeException(e);
                    }

                    long size = f.getSize();

                    return Image.builder()
                            .originImageName(originalFilename)
                            .storeImageName(storeFileName)
                            .extension(extension)
                            .size(size)
                            .build();
                })
                .toList();
    }
}
