package com.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private Boolean hasNext;
    private Boolean hasPrevious;

    public static <T> PageResponse<T> of(List<T> content, int currentPage, int pageSize, long totalElements, int totalPages) {
        return PageResponse.<T>builder()
            .content(content)
            .currentPage(currentPage)
            .pageSize(pageSize)
            .totalElements(totalElements)
            .totalPages(totalPages)
            .hasNext(currentPage < totalPages - 1)
            .hasPrevious(currentPage > 0)
            .build();
    }
}
