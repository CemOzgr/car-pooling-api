package com.bitirme.bitirmeapi.util.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Getter
public class PageDto<T> implements Serializable {
    private final List<T> content;
    private final PaginationDto pagination;

}
