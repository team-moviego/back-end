package com.hwansol.moviego.common.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class PageListResponseDto<T> {

    private int totalPage; // 전체 페이지 수
    private long totalElements; // 전체 데이터 수
    private int curPageNumber; // 현재 페이지
    private int curPageSize; // 현재 페이지에 표시된 데이터 수
    private boolean isFirst; // 첫 페이지 여부
    private boolean isLast; // 마지막 페이지 여부
    private List<T> content; // 데이터

    public PageListResponseDto(int totalPage, long totalElements, int curPageNumber,
            int curPageSize, boolean isFirst, boolean isLast, List<T> content) {
        boolean isValidateDataFail = totalPage < 0 || totalElements < 0 || curPageNumber < 0 || curPageSize <= 0;

        if (isValidateDataFail) {
            throw new IllegalArgumentException("PageListResponseDto 생성 실패");
        }

        this.totalPage = totalPage;
        this.totalElements = totalElements;
        this.curPageNumber = curPageNumber;
        this.curPageSize = curPageSize;
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.content = content;
    }

    public static <T> PageListResponseDto<T> from(Page<T> list) {
        return PageListResponseDto.<T>builder()
                .content(list.getContent())
                .curPageNumber(list.getPageable().getPageNumber())
                .curPageSize(list.getPageable().getPageSize())
                .isFirst(list.isFirst())
                .totalPage(list.getTotalPages())
                .isLast(list.isLast())
                .totalElements(list.getTotalElements())
                .build();
    }
}
