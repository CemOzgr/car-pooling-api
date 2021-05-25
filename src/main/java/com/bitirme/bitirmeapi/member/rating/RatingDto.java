package com.bitirme.bitirmeapi.member.rating;

import com.bitirme.bitirmeapi.member.MemberDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RatingDto {

    private Integer memberId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int submitterId;

    private double rating;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer count;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime submissionDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties(value = {"email", "contactNo", "createdAt", "vehicle", "preferences"})
    private MemberDto submitter;

    public RatingDto(int memberId, int submitterId, double rating) {
        this.memberId = memberId;
        this.submitterId = submitterId;
        this.rating = rating;
    }

    public RatingDto(double rating, LocalDateTime submissionDate,
                     MemberDto submitter) {
        this.rating = rating;
        this.submissionDate = submissionDate;
        this.submitter = submitter;
    }
}
