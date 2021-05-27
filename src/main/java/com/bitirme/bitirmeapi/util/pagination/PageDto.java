package com.bitirme.bitirmeapi.util.pagination;

import com.bitirme.bitirmeapi.util.jackson.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Getter
@JsonView(View.External.class)
public class PageDto<T> implements Serializable {
    private final List<T> content;
    private final PaginationDto pagination;

}
