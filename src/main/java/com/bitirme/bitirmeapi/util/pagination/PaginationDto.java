package com.bitirme.bitirmeapi.util.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class PaginationDto implements Serializable {
    private final Integer page;
    private final Integer pageSize;
    private final Integer totalPages;
    private final Long totalElements;
}
