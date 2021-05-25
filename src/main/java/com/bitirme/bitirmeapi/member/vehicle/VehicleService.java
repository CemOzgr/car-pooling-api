package com.bitirme.bitirmeapi.member.vehicle;

import com.bitirme.bitirmeapi.member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle loadWithMember(int memberId) {
        return vehicleRepository.loadWithMemberByMemberId(memberId)
                .orElseThrow(() -> new IllegalStateException("member is not a driver"));
    }

    public Vehicle loadVehicle(int memberId) {
        return vehicleRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("Vehicle not found"));
    }

    @Transactional
    public void saveVehicle(Member member, VehicleDto vehicleDto) {
        try {
            Type type = Type.valueOf(vehicleDto.getType());
            Vehicle vehicle = new Vehicle(
                    vehicleDto.getPlate(), vehicleDto.getMake(),
                    vehicleDto.getModel(), vehicleDto.getModelYear(),
                    type, member
            );
            vehicleRepository.save(vehicle);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new IllegalStateException(String.format("%s is not a valid vehicle type", vehicleDto.getType()));
        }
    }

    @Transactional
    public void updateVehicle(int memberId, VehicleDto vehicleDto) {
        Vehicle vehicle = vehicleRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("Vehicle does not exist"));
        vehicle.setMake(vehicleDto.getMake());
        vehicle.setModel(vehicleDto.getModel());
        vehicle.setPlate(vehicleDto.getPlate());
        vehicle.setModelYear(vehicleDto.getModelYear());
        try {
            vehicle.setType(Type.valueOf(vehicleDto.getType()));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new IllegalStateException(String.format("%s is not a valid vehicle type", vehicleDto.getType()));
        }
        vehicleRepository.save(vehicle);
    }

    @Transactional
    public void deleteVehicle(int memberId) {
        vehicleRepository.deleteById(memberId);
    }

    @Transactional
    public void setPictureFileName(int memberId, String fileName) {
        vehicleRepository.setVehiclePictureName(memberId, fileName);
    }

    public boolean existsByMemberId(int memberId) {
        return vehicleRepository.existsByMemberId(memberId);
    }
}
