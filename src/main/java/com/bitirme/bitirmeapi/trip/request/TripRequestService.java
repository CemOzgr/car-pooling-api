package com.bitirme.bitirmeapi.trip.request;

import com.bitirme.bitirmeapi.member.Member;
import com.bitirme.bitirmeapi.member.MemberDto;
import com.bitirme.bitirmeapi.notification.Notification;
import com.bitirme.bitirmeapi.notification.NotificationDtoConverter;
import com.bitirme.bitirmeapi.notification.NotificationService;
import com.bitirme.bitirmeapi.notification.TripRequestNotification;
import com.bitirme.bitirmeapi.trip.Trip;
import com.bitirme.bitirmeapi.trip.TripDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class TripRequestService {
    private final TripRequestRepository requestRepository;
    private final NotificationService notificationService;

    @Autowired
    public TripRequestService(TripRequestRepository requestRepository, NotificationService notificationService) {
        this.requestRepository = requestRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public void createTripRequest(Trip trip, Member submitter) {
        if(requestRepository.hasSubmitterGotActiveRequestForTrip(trip.getId(), submitter.getId())) {
            throw new IllegalStateException("Request is still active");
        }

        TripRequest request = new TripRequest(trip, submitter);
        request.setStatus(Status.SUBMITTED);

        requestRepository.save(request);

        Notification requestNotification = new TripRequestNotification(
                "new trip request", trip.getDriver(), request);
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

    public void deleteTripRequest(TripRequest request) {
        if(request.getUpdatedAt() != null) {
            throw new IllegalStateException("Request already answered");
        }
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

        Notification requestNotification = new TripRequestNotification(
                "trip request " + status.name().toLowerCase(Locale.ROOT),
                request.getSubmitter(),
                request
        );
        notificationService.saveNotification(requestNotification);
        notificationService.sendNotification(request.getSubmitter().getId(), NotificationDtoConverter.convertToDto(requestNotification));

    }

}
