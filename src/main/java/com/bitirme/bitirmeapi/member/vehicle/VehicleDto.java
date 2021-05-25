package com.bitirme.bitirmeapi.member.vehicle;

import com.bitirme.bitirmeapi.member.View;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonView(View.External.class)
public class VehicleDto {

    @Pattern(regexp = "^(\\d{2})([A-VY-Z]{1,3})(\\d{2,4})$")
    private String plate;

    @Pattern(regexp = "^[a-zA-Z]{3,15}$")
    private String make;

    @Pattern(regexp = "^[a-zA-Z0-9]{3,15}$")
    private String model;

    private int modelYear;

    @Pattern(regexp = "^[A-Z]+?$")
    private String type;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String pictureLink;

    public VehicleDto(Vehicle vehicle) {
        this.plate = vehicle.getPlate();
        this.make = vehicle.getMake();
        this.model = vehicle.getModel();
        this.modelYear = vehicle.getModelYear();
        this.type = vehicle.getType().name();
        if(vehicle.getPictureFileName() != null) {
            this.pictureLink = "/api/images/" + vehicle.getPictureFileName();
        }
    }

}
