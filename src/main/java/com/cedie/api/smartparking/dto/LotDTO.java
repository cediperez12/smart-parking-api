package com.cedie.api.smartparking.dto;

import com.cedie.api.smartparking.entity.Lot;

import java.util.Set;

public class LotDTO {

    public record SimpleLot(Long id, String location, Long capacity) {}
    public static SimpleLot mapEntityToSimpleLot(Lot entity) {
        return new SimpleLot(entity.getId(), entity.getLocation(), entity.getCapacity());
    }

    public record LotOccupancyAndAvailability(Long lotId, String location, Long capacity, Long availableSlots, boolean isSlotAvailable) {}
    public record SimpleLotWithParkedVehicle(SimpleLot lot, Set<VehicleDTO.SimpleVehicle> parkedVehicles) {}

    public record CreateLotDTO(String location, Long capacity) {}
    public static Lot mapCreateLotDTOToEntity(CreateLotDTO dto) {
        return new Lot(dto.location(), dto.capacity());
    }


}
