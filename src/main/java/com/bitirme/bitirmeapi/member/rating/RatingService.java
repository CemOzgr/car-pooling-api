package com.bitirme.bitirmeapi.member.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;

    @Autowired
    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Transactional
    public void saveRating(Rating rating) {
        if(!ratingRepository.isSubmitterPassenger(rating.getMember().getId(), rating.getSubmitterId())) {
            throw new IllegalStateException("Only passengers can rate driver if the trip is over");
        }
        if(rating.getRating() > 5 || rating.getRating() < 0) {
            throw new IllegalStateException("Rating should be between 0 and 5");
        }

        ratingRepository.save(rating);

    }

    @PreAuthorize("#memberId != authentication.principal.id")
    public void deleteRating(int memberId, int submitterId) {
        Rating rating = ratingRepository.findByMemberIdAndSubmitterId(memberId, submitterId)
                .orElseThrow(() -> new IllegalStateException("rating not found"));
        ratingRepository.delete(rating);
    }

    public Rating loadByMemberAndSubmitterId(int memberId, int submitterId) {
        return ratingRepository.findByMemberIdAndSubmitterId(memberId, submitterId)
                .orElse(new Rating());
    }

    public Double loadAverageRatingOfMember(int memberId) {
        return ratingRepository.findAverageRatingByMember(memberId);
    }

    public List<RatingDto> loadMemberRatings(int memberId) {
        return ratingRepository.findRatingsByMemberId(memberId);
    }



}
