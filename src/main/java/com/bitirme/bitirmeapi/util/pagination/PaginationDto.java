package com.bitirme.bitirmeapi.util.pagination;

import com.bitirme.bitirmeapi.util.jackson.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@JsonView(View.External.class)
public class PaginationDto implements Serializable {
    private final Integer page;
    private final Integer pageSize;
    private final Integer totalPages;
    private final Long totalElements;
}
