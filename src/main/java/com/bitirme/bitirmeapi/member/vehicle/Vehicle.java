package com.bitirme.bitirmeapi.member.vehicle;

import com.bitirme.bitirmeapi.member.Member;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="vehicles",schema = "v1")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Vehicle {

    @Id
    @Column(name="member_id")
    private int memberId;

    private String plate;
    private String make;
    private String model;

    @Column(name = "model_year")
    private int modelYear;

    @Column(name = "vehicle_type")
    @Enumerated(EnumType.ORDINAL)
    private Type type;

    @Column(name = "vehicle_picture_name")
    private String pictureFileName;

    @OneToOne
    @MapsId
    @JoinColumn(name="member_id")
    private Member member;

    public Vehicle(String plate, String make, String model,
                   int modelYear, Type type, Member member) {
        this.plate = plate;
        this.make = make;
        this.model = model;
        this.modelYear = modelYear;
        this.type = type;
        this.member = member;
    }
}
