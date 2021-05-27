package com.bitirme.bitirmeapi.member;

import com.bitirme.bitirmeapi.member.preferences.PreferencesDto;
import com.bitirme.bitirmeapi.member.rating.RatingDto;
import com.bitirme.bitirmeapi.member.vehicle.VehicleDto;
import com.bitirme.bitirmeapi.notification.NotificationDto;
import com.bitirme.bitirmeapi.notification.NotificationService;
import com.bitirme.bitirmeapi.security.MemberDetails;
import com.bitirme.bitirmeapi.trip.TripDto;
import com.bitirme.bitirmeapi.util.jackson.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/members")
public class MemberResource {

    private final MemberService memberService;
    private final NotificationService notificationService;

    @Autowired
    public MemberResource(MemberService memberService, NotificationService notificationService) {
        this.memberService = memberService;
        this.notificationService = notificationService;
    }

    @GetMapping(value = {"/{memberId}", "/me"})
    public MappingJacksonValue loadMember(@PathVariable(required = false) Integer memberId,
                                          @AuthenticationPrincipal MemberDetails principal) {
        MemberDto memberDto = memberService.loadMemberDetailed(
                memberId!=null
                        ? memberId
                        : principal.getId());

        MappingJacksonValue value = new MappingJacksonValue(memberDto);
        if(memberId == null || memberService.membersDriverPassengerOfSameActiveTrip(principal.getId(), memberId)) {
            value.setSerializationView(View.Internal.class);
        } else {
            value.setSerializationView(View.External.class);
        }

        return value;
    }

    @GetMapping("/{memberId}/ratings")
    public List<RatingDto> loadMemberRatings(@PathVariable int memberId) {
        return memberService.loadMemberRatings(memberId);
    }

    @GetMapping("/me/passenger-trips")
    public List<TripDto> loadAttendingTrips(@AuthenticationPrincipal MemberDetails principal) {
        return memberService.loadMemberPassengerTrips(principal.getId());
    }

    @GetMapping("/me/driver-trips")
    public List<TripDto> loadDriverTrips(@AuthenticationPrincipal MemberDetails principal) {
        return memberService.loadMemberDriverTrips(principal.getId());
    }

    @GetMapping("/me/notifications")
    public List<NotificationDto> loadNotifications(@AuthenticationPrincipal MemberDetails principal) {
        return notificationService.loadNotifications(principal.getId());
    }
    @GetMapping("/me/notifications/count")
    public int loadNotificationCountByRead(@AuthenticationPrincipal MemberDetails principal,
                                           @RequestParam("read") boolean read) {
        return notificationService.loadNumberOfNotificationsByRead(principal.getId(), read);
    }

    @PatchMapping("/me/notifications/{notificationId}")
    public HttpStatus updateNotificationReadStatus(@AuthenticationPrincipal MemberDetails principal,
                                                   @PathVariable int notificationId) {
        notificationService.setNotificationToRead(notificationId, principal.getId());
        return HttpStatus.OK;
    }

    @PostMapping("/me/preference")
    public HttpStatus saveMemberPreferences(@AuthenticationPrincipal MemberDetails principal,
                                            @Valid @RequestBody PreferencesDto preferencesDto) {
        memberService.savePreference(principal.getId(), preferencesDto);
        return HttpStatus.CREATED;
    }

    @PutMapping("/me/preference")
    public HttpStatus updateMemberPreferences(@AuthenticationPrincipal MemberDetails principal,
                                              @Valid @RequestBody PreferencesDto preferencesDto) {
        memberService.updatePreference(principal.getId(), preferencesDto);
        return HttpStatus.OK;
    }

    @PostMapping("/me/vehicle")
    public HttpStatus saveMemberVehicle(@AuthenticationPrincipal MemberDetails principal,
                                        @Valid @RequestBody VehicleDto vehicleDto) {
        memberService.saveVehicle(principal.getId(), vehicleDto);
        return HttpStatus.CREATED;
    }

    @PutMapping("/me/vehicle")
    public HttpStatus updateMemberVehicle(@AuthenticationPrincipal MemberDetails principal,
                                          @Valid @RequestBody VehicleDto vehicleDto) {
        memberService.updateVehicle(principal.getId(), vehicleDto);
        return HttpStatus.OK;
    }

    @DeleteMapping("/me/vehicle")
    public HttpStatus deleteMemberVehicle(@AuthenticationPrincipal MemberDetails principal) {
        memberService.deleteVehicle(principal.getId());
        return HttpStatus.OK;
    }

    @PostMapping("/{memberId}/ratings")
    public HttpStatus addRatingToMember(@AuthenticationPrincipal MemberDetails submitter,
                                        @PathVariable int memberId,
                                        @RequestBody double rating) {
        RatingDto ratingDto = new RatingDto(memberId, submitter.getId(), rating);
        memberService.saveRating(ratingDto);
        return HttpStatus.CREATED;
    }

    @DeleteMapping("/{memberId}/ratings")
    public HttpStatus deleteRatingFromMember(@AuthenticationPrincipal MemberDetails submitter,
                                             @PathVariable int memberId) {
        memberService.deleteRating(memberId, submitter.getId());
        return HttpStatus.OK;
    }

    @PostMapping(value="/me/image")
    public ResponseEntity<String> uploadProfilePicture(@AuthenticationPrincipal MemberDetails principal,
                                                       @RequestParam("image") MultipartFile file) {

        if(!Objects.requireNonNull(file.getContentType()).matches("image/.+")) {
            return new ResponseEntity<>("Invalid format: " + file.getContentType(), HttpStatus.BAD_REQUEST);
        }

        if(file.isEmpty()) {
            return new ResponseEntity<>("File can not be empty", HttpStatus.BAD_REQUEST);
        }

        memberService.uploadProfilePicture(principal.getId(), file);
        return new ResponseEntity<>("Image created", HttpStatus.OK);
    }

    @DeleteMapping("/me/image")
    public HttpStatus deleteProfilePicture(@AuthenticationPrincipal MemberDetails principal) {
        memberService.deleteProfilePicture(principal.getId());
        return HttpStatus.OK;
    }

    @PostMapping(value="/me/vehicle/image")
    public ResponseEntity<String> uploadVehiclePicture(@AuthenticationPrincipal MemberDetails principal,
                                                       @RequestParam("image") MultipartFile image) {
        if(!Objects.requireNonNull(image.getContentType()).matches("image/.+")) {
            return new ResponseEntity<>("Invalid format: " + image.getContentType(), HttpStatus.BAD_REQUEST);
        }

        if(image.isEmpty()) {
            return new ResponseEntity<>("File can not be empty", HttpStatus.BAD_REQUEST);
        }

        memberService.uploadVehiclePicture(principal.getId(), image);
        return new ResponseEntity<>("Image created", HttpStatus.OK);
    }

    @DeleteMapping("/me/vehicle/image")
    public HttpStatus deleteVehiclePicture(@AuthenticationPrincipal MemberDetails principal) {
        memberService.deleteVehiclePicture(principal.getId());
        return HttpStatus.OK;
    }
}
