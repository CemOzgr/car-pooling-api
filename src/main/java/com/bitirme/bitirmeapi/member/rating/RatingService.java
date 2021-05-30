package com.bitirme.bitirmeapi.member.rating;

import com.bitirme.bitirmeapi.member.Member;
import com.bitirme.bitirmeapi.notification.Notification;
import com.bitirme.bitirmeapi.notification.NotificationDtoConverter;
import com.bitirme.bitirmeapi.notification.NotificationService;
import com.bitirme.bitirmeapi.notification.RatingNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final NotificationService notificationService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public RatingService(RatingRepository ratingRepository, NotificationService notificationService) {
        this.ratingRepository = ratingRepository;
        this.notificationService = notificationService;
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

        Member sender = entityManager.getReference(Member.class, rating.getSubmitterId());

        Notification ratingNotification = new RatingNotification(
                "New Rating",
                rating.getMember(),
                sender,
                rating
        );
        notificationService.saveNotification(ratingNotification);
        notificationService.sendNotification(
                rating.getMember().getId(),
                NotificationDtoConverter.convertToDto(ratingNotification));
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
