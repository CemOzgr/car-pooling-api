package com.bitirme.bitirmeapi.member.rating;

import com.bitirme.bitirmeapi.member.MemberDto;
import com.bitirme.bitirmeapi.member.QMember;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RatingQueryDslRepositoryImpl implements RatingQueryDslRepository{
    private final JPAQueryFactory queryFactory;

    @Autowired
    public RatingQueryDslRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<RatingDto> findRatingsByMemberId(int memberId) {
        QRating rating = QRating.rating1;
        QMember submitter = QMember.member;

        return queryFactory
                .selectFrom(rating)
                .join(submitter).on(submitter.id.eq(rating.submitterId))
                .where(rating.member.id.eq(memberId))
                .select(Projections.constructor(RatingDto.class, rating.rating, rating.createdAt,
                            Projections.constructor(MemberDto.class, submitter)))
                .fetch();
    }
}
