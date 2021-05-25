package com.bitirme.bitirmeapi.trip;

import com.bitirme.bitirmeapi.member.QMember;
import com.bitirme.bitirmeapi.member.rating.QRating;
import com.bitirme.bitirmeapi.trip.waypoint.QWaypoint;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.hibernate.QueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.querydsl.core.group.GroupBy.groupBy;

@Repository
public class TripQueryDslRepositoryImpl implements TripQueryDslRepository{

    private final JPAQueryFactory queryFactory;
    private static final Logger LOGGER = LoggerFactory.getLogger(TripQueryDslRepositoryImpl.class);

    @Autowired
    public TripQueryDslRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @Transactional
    public Page<TripDto> findTripDtos(BooleanExpression predicate, Pageable pageable) {

        QTrip trip = QTrip.trip;

        QWaypoint startLocation = new QWaypoint("startLocation");
        QWaypoint destinationLocation = new QWaypoint("destinationLocation");

        QMember driver = new QMember("driver");
        QRating rating = QRating.rating1;

        try {
            JPAQuery<Trip> query = queryFactory
                    .selectFrom(trip)
                    .join(trip.startWaypoint, startLocation)
                    .join(trip.destinationWaypoint, destinationLocation)
                    .join(trip.driver, driver)
                    .where(predicate)
                    .where(trip.numberOfAvailableSeats.gt(0))
                    .where(driver.enabled.eq(true));

            List<TripDto> content = query.clone()
                    .leftJoin(driver.ratings, rating)
                    .groupBy(trip, startLocation, destinationLocation, driver, rating.member.id)
                    .orderBy(rating.rating.avg().desc().nullsLast())
                    .orderBy(trip.startDate.asc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                    .transform(
                            groupBy(trip, startLocation, destinationLocation, driver)
                                    .list(Projections.constructor(TripDto.class, trip, rating.rating.avg())));

            long totalRowCount = query.fetchCount();

            return new PageImpl<>(content, pageable, totalRowCount);

        } catch(IllegalArgumentException | QueryException e) {
            LOGGER.info(e.getMessage());
            throw new IllegalStateException("Invalid property or value");
        }

    }



}
