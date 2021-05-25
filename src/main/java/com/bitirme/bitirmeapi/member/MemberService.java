package com.bitirme.bitirmeapi.member;

import com.bitirme.bitirmeapi.member.preferences.PreferenceService;
import com.bitirme.bitirmeapi.member.preferences.PreferencesDto;
import com.bitirme.bitirmeapi.member.rating.Rating;
import com.bitirme.bitirmeapi.member.rating.RatingDto;
import com.bitirme.bitirmeapi.member.rating.RatingService;
import com.bitirme.bitirmeapi.member.vehicle.Vehicle;
import com.bitirme.bitirmeapi.member.vehicle.VehicleDto;
import com.bitirme.bitirmeapi.member.vehicle.VehicleService;
import com.bitirme.bitirmeapi.registration.token.ConfirmationToken;
import com.bitirme.bitirmeapi.registration.token.ConfirmationTokenService;
import com.bitirme.bitirmeapi.security.MemberDetails;
import com.bitirme.bitirmeapi.trip.TripDto;
import com.bitirme.bitirmeapi.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ConfirmationTokenService tokenService;
    private final RatingService ratingService;
    private final PreferenceService preferenceService;
    private final VehicleService vehicleService;
    private final FileService fileService;

    @Autowired
    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder passwordEncoder,
                         ConfirmationTokenService tokenService, RatingService ratingService,
                         PreferenceService preferenceService, VehicleService vehicleService,
                         @Qualifier("image") FileService fileService) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.ratingService = ratingService;
        this.preferenceService = preferenceService;
        this.vehicleService = vehicleService;
        this.fileService = fileService;
    }

    public MemberDto loadMemberDetailed(int memberId) {
        MemberDto member = memberRepository.findMemberWithPrefAndVehicle(memberId);
        member.setAverageRating(ratingService.loadAverageRatingOfMember(memberId));
        return member;
    }

    public Member loadMember(int memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("Unable to find resource"));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
        return new MemberDetails(member);
    }

    public List<TripDto> loadMemberPassengerTrips(int memberId) {
        return memberRepository.findMemberPassengerTrips(memberId);
    }

    public List<TripDto> loadMemberDriverTrips(int memberId) {
        return memberRepository.findMemberDriverTrips(memberId);
    }

    @Transactional
    public String signUpUserAndGetToken(Member member) {
        Optional<Member> optionalMember = memberRepository.findByEmail(member.getEmail());

        if(optionalMember.isPresent()) {
            if(!optionalMember.get().getEnabled()) {
                ConfirmationToken confirmationToken = tokenService.generateRandomToken(member);
                tokenService.saveConfirmationToken(confirmationToken);
                return confirmationToken.getToken();
            }
            throw new IllegalStateException("email already taken");
        }

        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encodedPassword);

        memberRepository.save(member);

        ConfirmationToken confirmationToken = tokenService.generateRandomToken(member);
        tokenService.saveConfirmationToken(confirmationToken);

        return confirmationToken.getToken();
    }

    public void enableMember(int memberId) {
        memberRepository.enableMember(memberId);
    }

    @PreAuthorize("#memberId == authentication.principal.id")
    @Transactional
    public void savePreference(int memberId, PreferencesDto preferencesDto) {
        Member member = this.loadMember(memberId);
        preferenceService.savePreference(member, preferencesDto);
    }

    @Transactional
    public void updatePreference(int memberId, PreferencesDto preferencesDto) {
        preferenceService.updatePreference(memberId, preferencesDto);
    }

    @Transactional
    public void saveVehicle(int memberId, VehicleDto vehicleDto) {
        Member member = this.loadMember(memberId);
        vehicleService.saveVehicle(member, vehicleDto);
    }

    @Transactional
    public void updateVehicle(int memberId, VehicleDto vehicleDto) {
        vehicleService.updateVehicle(memberId, vehicleDto);
    }

    @Transactional
    public void deleteVehicle(int memberId) {
        vehicleService.deleteVehicle(memberId);
    }

    @Transactional
    @PreAuthorize("#ratingDto.memberId != authentication.principal.id")
    public void saveRating(RatingDto ratingDto) {
        Rating rating = ratingService.loadByMemberAndSubmitterId(ratingDto.getMemberId(), ratingDto.getSubmitterId());

        if(rating.getId() == 0) {
            Member memberReference = memberRepository.getOne(ratingDto.getMemberId());
            rating.setMember(memberReference);
            rating.setSubmitterId(ratingDto.getSubmitterId());
        }
        rating.setRating(ratingDto.getRating());

        ratingService.saveRating(rating);
    }

    @PreAuthorize("#memberId == authentication.principal.id")
    public void deleteRating(int memberId, int submitterId) {
        ratingService.deleteRating(memberId, submitterId);
    }

    public List<RatingDto> loadMemberRatings(int memberId) {
        return ratingService.loadMemberRatings(memberId);
    }

    public boolean membersDriverPassengerOfSameActiveTrip(int member1Id, int member2Id) {
        return memberRepository.existsActiveByTripDriverIdAndTripPassengerId(member1Id, member2Id);
    }

    @Transactional
    public void uploadProfilePicture(int memberId, MultipartFile image) {
        String fileName = String.format("%d_profile.jpeg",memberId);

        fileService.upload(fileName, image);
        memberRepository.setProfilePictureName(memberId, fileName);
    }

    @Transactional
    public void deleteProfilePicture(int memberId) {
        Member member = loadMember(memberId);
        String fileName = member.getProfilePictureName();

        memberRepository.setProfilePictureName(memberId, null);
        fileService.delete(fileName);
    }

    @Transactional
    public void uploadVehiclePicture(int memberId, MultipartFile image) {
        if(!vehicleService.existsByMemberId(memberId)) {
            throw new IllegalStateException("vehicle does not exist");
        }
        String fileName = String.format("%d_vehicle.jpeg", memberId);
        fileService.upload(fileName, image);
        vehicleService.setPictureFileName(memberId, fileName);
    }

    @Transactional
    public void deleteVehiclePicture(int memberId) {
        Vehicle vehicle = vehicleService.loadVehicle(memberId);
        String fileName = vehicle.getPictureFileName();

        vehicleService.setPictureFileName(memberId, null);
        fileService.delete(fileName);
    }

}
