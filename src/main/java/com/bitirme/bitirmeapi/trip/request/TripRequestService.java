package com.bitirme.bitirmeapi.trip.request;

import com.bitirme.bitirmeapi.member.Member;
import com.bitirme.bitirmeapi.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TripRequestService {
    private final TripRequestRepository requestRepository;

    @Autowired
    public TripRequestService(TripRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Transactional
    public void createTripRequest(Trip trip, Member submitter) {
        if(requestRepository.hasSubmitterGotActiveRequestForTrip(trip.getId(), submitter.getId())) {
            throw new IllegalStateException("Request is still active");
        }

        TripRequest request = new TripRequest(trip, submitter);
        request.setStatus(Status.SUBMITTED);

        requestRepository.save(request);
    }

    public TripRequest loadTripRequest(int requestId) {
        return requestRepository.findDetailedById(requestId)
                .orElseThrow(() -> new IllegalStateException("request not found"));
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
    }

}
