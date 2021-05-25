package com.bitirme.bitirmeapi.trip;


import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TripQueryDslRepository {
    Page<TripDto> findTripDtos(BooleanExpression predicate, Pageable pageable);
}
