package com.hwansol.moviego.image.dto;

import com.hwansol.moviego.image.model.Image;
import com.hwansol.moviego.image.model.PosterType;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ImageGetDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response implements Serializable {

        private Long id;
        private String contentType;      // ex) "image/webp"
        private byte[] imageData;        // 변환된 이미지 바이트 배열
        private PosterType posterType;
        private String imageName;
        private String extension;
        private long size;

        public Response(Long id, String contentType, byte[] imageData, PosterType posterType, String imageName, String extension, long size) {
            boolean isValidateDataFail = id == null || id <= 0 || contentType == null || contentType.isBlank() || imageData == null || imageData.length == 0 || posterType == null || imageName == null || imageName.isBlank() || extension == null || extension.isBlank() || size <= 0;

            if (isValidateDataFail) {
                throw new IllegalArgumentException("ImageGetDto.Response 생성 실패");
            }

            this.id = id;
            this.contentType = contentType;
            this.imageData = imageData;
            this.posterType = posterType;
            this.imageName = imageName;
            this.extension = extension;
            this.size = size;
        }

        public static ImageGetDto.Response from(Image image, String contentType, byte[] imageData) {
            return Response.builder()
                    .id(image.getId())
                    .contentType(contentType)
                    .imageData(imageData)
                    .extension(image.getExtension())
                    .imageName(image.getOriginImageName())
                    .posterType(image.getPosterType())
                    .size(image.getSize())
                    .build();
        }
    }
}
