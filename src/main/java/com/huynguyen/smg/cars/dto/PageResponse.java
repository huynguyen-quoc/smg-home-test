package com.huynguyen.smg.cars.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Builder
@Getter
@Value
public class PageResponse<T> {
    List<T> content;
    int page;
    int totalPages;
    long totalElements;
}
