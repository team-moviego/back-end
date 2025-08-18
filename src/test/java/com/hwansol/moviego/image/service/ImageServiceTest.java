package com.hwansol.moviego.image.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.hwansol.moviego.image.model.Image;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private AmazonS3 r2Client;

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        setPrivateVariable(imageService, "bucket");
    }

    @Test
    @DisplayName("파일 생성")
    void createFile() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("image.png", "image.png", MediaType.IMAGE_PNG_VALUE, "image.png".getBytes(StandardCharsets.UTF_8));

        List<Image> imageList = imageService.createFile(List.of(mockMultipartFile));

        assertThat(imageList.get(0).getExtension()).isEqualTo("png");

        verify(r2Client, times(1)).putObject(eq("bucket"), anyString(), any(InputStream.class), any(ObjectMetadata.class));
    }

    private void setPrivateVariable(ImageService imageService, String bucketName) throws NoSuchFieldException, IllegalAccessException {
        Field bucketNameField = imageService.getClass().getDeclaredField("bucketName");

        bucketNameField.setAccessible(true);
        bucketNameField.set(imageService, bucketName);
    }
}