package com.bitirme.bitirmeapi.member;

import com.bitirme.bitirmeapi.member.preferences.PreferencesDto;
import com.bitirme.bitirmeapi.member.vehicle.VehicleDto;
import com.bitirme.bitirmeapi.util.jackson.View;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MemberDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonView(View.External.class)
    private int id;

    @JsonView(View.External.class)
    private String fullName;

    @JsonView(View.Internal.class)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonView(View.Internal.class)
    private String contactNo;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonView(View.External.class)
    private String profilePictureLink;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonView(View.External.class)
    private Double averageRating;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yy hh:mm:ss")
    @JsonView(View.External.class)
    private Date createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonView(View.External.class)
    private PreferencesDto preferences;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonView(View.External.class)
    private VehicleDto vehicle;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.fullName = String.format("%s %s", member.getFirstName(), member.getLastName());
        this.email = member.getEmail();
        this.contactNo = member.getContactNo();
        this.createdAt = member.getCreatedAt();
        if(member.getProfilePictureName() != null) {
            this.profilePictureLink = "/api/images/" + member.getProfilePictureName();
        }
    }

    public MemberDto(Member member, PreferencesDto preferencesDto, VehicleDto vehicleDto) {
        this(member);
        this.preferences = preferencesDto;
        this.vehicle = vehicleDto;
    }
}
