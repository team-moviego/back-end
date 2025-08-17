package com.hwansol.moviego.image.dto;

import com.hwansol.moviego.image.model.Image;
import com.hwansol.moviego.image.model.PosterType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ImageGetDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder(toBuilder = true)
    public static class Response {

        private Long id;
        private String url;
        private PosterType posterType;
        private String imageName;
        private String extension;
        private long size;

        public Response(Long id, String url, PosterType posterType, String imageName, String extension, long size) {
            boolean isValidateDataFail = id == null || id <= 0 || url == null || url.isBlank() || posterType == null || imageName == null || imageName.isBlank() || extension == null || extension.isBlank() || size <= 0;

            if (isValidateDataFail) {
                throw new IllegalArgumentException("ImageGetDto.Response 생성 실패");
            }

            this.id = id;
            this.url = url;
            this.posterType = posterType;
            this.imageName = imageName;
            this.extension = extension;
            this.size = size;
        }

        public static ImageGetDto.Response from(Image image) {
            return Response.builder()
                    .id(image.getId())
                    .extension(image.getExtension())
                    .imageName(image.getOriginImageName())
                    .posterType(image.getPosterType())
                    .size(image.getSize())
                    .url(image.getUrl())
                    .build();
        }
    }
}
