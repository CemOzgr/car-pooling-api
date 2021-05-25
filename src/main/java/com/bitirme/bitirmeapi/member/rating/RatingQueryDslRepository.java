package com.bitirme.bitirmeapi.member.rating;

import java.util.List;

public interface RatingQueryDslRepository {

    List<RatingDto> findRatingsByMemberId(int memberId);

}
