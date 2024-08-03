package com.cedie.api.smartparking.dto;

import com.cedie.api.smartparking.entity.Vehicle;
import com.cedie.api.smartparking.enums.ParkingStatus;
import com.cedie.api.smartparking.enums.VehicleType;

import java.time.LocalDateTime;

public class VehicleDTO {

    public record SimpleVehicle(Long id, String licensePlate, VehicleType type, String ownerName) {}
    public static SimpleVehicle mapEntityToSimpleVehicle(Vehicle entity) {
        return new SimpleVehicle(entity.getId(), entity.getLicensePlate(),
                VehicleType.valueOf(entity.getType()),
                entity.getOwnerName());
    }

    public record VehicleParkedAt(LotDTO.SimpleLot parkedAt, String licensePlate, LocalDateTime time, ParkingStatus status) {}

    public record CreateVehicleDTO(String licensePlate, VehicleType type, String ownerName) {}

    public static Vehicle mapCreateVehicleDTOToEntity(CreateVehicleDTO dto) {
        return new Vehicle(dto.licensePlate(), dto.type().name(), dto.ownerName());
    }
}
