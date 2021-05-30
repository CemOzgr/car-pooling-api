package com.bitirme.bitirmeapi.trip.request;

import com.bitirme.bitirmeapi.member.Member;
import com.bitirme.bitirmeapi.member.MemberDto;
import com.bitirme.bitirmeapi.notification.*;
import com.bitirme.bitirmeapi.trip.Trip;
import com.bitirme.bitirmeapi.trip.TripDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class TripRequestService {
    private final TripRequestRepository requestRepository;
    private final NotificationService notificationService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public TripRequestService(TripRequestRepository requestRepository, NotificationService notificationService) {
        this.requestRepository = requestRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public void createTripRequest(Trip trip, Member submitter) {
        if(requestRepository.hasSubmitterGotActiveRequestForTrip(trip.getId(), submitter.getId())) {
            throw new IllegalStateException("You have active request for this trip");
        }

        TripRequest request = new TripRequest(trip, submitter);
        request.setStatus(Status.SUBMITTED);
        requestRepository.save(request);

        Member recipient = entityManager.getReference(Member.class, trip.getDriverId());

        Notification requestNotification = new TripRequestNotification(
                "new trip request",
                recipient,
                submitter,
                request
        );

        notificationService.saveNotification(requestNotification);

        notificationService.sendNotification(
                trip.getDriverId(),
                NotificationDtoConverter.convertToDto(requestNotification)
        );

    }

    public TripRequest loadTripRequest(int requestId) {
        return requestRepository.findDetailedById(requestId)
                .orElseThrow(() -> new IllegalStateException("request not found"));
    }

    @PreAuthorize("@tripRequestRepository.isMemberOwnerOfRequestOrDriver(#requestId, #memberId)")
    public TripRequestDto loadTripRequestDto(int requestId, int memberId) {
        TripRequest request = loadTripRequest(requestId);
        Trip trip = request.getTrip();
        Member submitter = request.getSubmitter();
        return new TripRequestDto(request, new TripDto(trip), new MemberDto(submitter));
    }

    @Transactional
    public void deleteTripRequest(TripRequest request) {
        if(request.getUpdatedAt() != null) {
            throw new IllegalStateException("Request already answered");
        }
        notificationService.deleteNotificationsForTripRequest(request.getId());
        requestRepository.delete(request);
    }

    public List<TripRequest> loadRequestsOfMember(int memberId) {
        return requestRepository.findBySubmitter(memberId);
    }

    public List<TripRequest> loadRequestsForTripByStatus(int tripId, Status status) {
        return requestRepository.findNotExpiredByTripAndStatus(tripId, status);
    }

    @Transactional
    public void updateRequestStatus(TripRequest request, Status status) {
        if(request.getUpdatedAt() != null || request.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Request expired or previously updated");
        }

        requestRepository.updateStatus(request.getId(), status);

        Member sender = entityManager.getReference(Member.class, request.getTrip().getDriverId());

        Notification requestNotification = new TripRequestNotification(
                "trip request " + status.name().toLowerCase(Locale.ROOT),
                request.getSubmitter(),
                sender,
                request
        );

        notificationService.saveNotification(requestNotification);
        notificationService.sendNotification(
                request.getSubmitter().getId(),
                NotificationDtoConverter.convertToDto(requestNotification)
        );

    }

}
