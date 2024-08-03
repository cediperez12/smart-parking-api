package com.cedie.api.smartparking;

import com.cedie.api.smartparking.dto.LotDTO;
import com.cedie.api.smartparking.dto.VehicleDTO;
import com.cedie.api.smartparking.entity.Lot;
import com.cedie.api.smartparking.enums.ParkingStatus;
import com.cedie.api.smartparking.enums.VehicleType;
import com.cedie.api.smartparking.exception.ConflictException;
import com.cedie.api.smartparking.exception.InvalidInputException;
import com.cedie.api.smartparking.exception.NotFoundException;
import com.cedie.api.smartparking.service.LotService;
import com.cedie.api.smartparking.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class LotServiceTests {

    @Autowired
    private LotService lotService;

    @Autowired
    private VehicleService vehicleService;

    @Test
    public void lotServiceShouldExist() {
        assertThat(lotService).isNotNull();
    }

    @Test
    public void whenRegistering_thenShouldSave() {
        // When
        LotDTO.CreateLotDTO dto = new LotDTO.CreateLotDTO("SM Manila, Metro Manila", 50L);

        // Then
        LotDTO.SimpleLot result = lotService.registerParkingLot(dto);
        assertThat(result).isNotNull();
    }

    @Test
    public void whenRegisteringWithNullLocation_thenShouldThrowInvalidInput() {
        // When
        final LotDTO.CreateLotDTO dto = new LotDTO.CreateLotDTO(null, 50L);
        // Then
        assertThatThrownBy(() -> {
            LotDTO.SimpleLot result = lotService.registerParkingLot(dto);
        }).isInstanceOf(InvalidInputException.class);

        // When
        final LotDTO.CreateLotDTO dto1 = new LotDTO.CreateLotDTO("", 12L);
        // Then
        assertThatThrownBy(() -> {
            lotService.registerParkingLot(dto1);
        }).isInstanceOf(InvalidInputException.class);
    }

    @Test
    public void whenRegisteringLotWith0Capacity_thenShouldThrowInvalidInput() {
        // When
        final LotDTO.CreateLotDTO dto = new LotDTO.CreateLotDTO(null, 0L);
        // Then
        assertThatThrownBy(() -> {
            LotDTO.SimpleLot result = lotService.registerParkingLot(dto);
        }).isInstanceOf(InvalidInputException.class);
    }

    @Test
    public void whenParking_thenShouldSave() {
        // When
        LotDTO.CreateLotDTO p1 = new LotDTO.CreateLotDTO("Quezon City", 5L);
        VehicleDTO.CreateVehicleDTO v1 = new VehicleDTO.CreateVehicleDTO("BIN-1888", VehicleType.CAR, "Jhoanna Robles");
        VehicleDTO.CreateVehicleDTO v2 = new VehicleDTO.CreateVehicleDTO("BIN-1881", VehicleType.MOTORCYCLE, "Mariah Queen Arceta");

        LotDTO.SimpleLot sP1 = lotService.registerParkingLot(p1);
        VehicleDTO.SimpleVehicle sV1 = vehicleService.registerVehicle(v1);
        VehicleDTO.SimpleVehicle sV2 = vehicleService.registerVehicle(v2);

        assertThat(sP1).isNotNull();
        assertThat(sV1).isNotNull();
        assertThat(sV2).isNotNull();

        // Then
        VehicleDTO.VehicleParkedAt parking1 = lotService.in(sP1.id(), sV1.id());
        assertThat(parking1.parkedAt().id()).isEqualTo(sP1.id());
        assertThat(parking1).isNotNull();
        assertThat(parking1.licensePlate()).isEqualTo("BIN-1888");
        assertThat(parking1.status()).isEqualTo(ParkingStatus.IN);

        VehicleDTO.VehicleParkedAt parking2 = lotService.in(sP1.id(), sV2.id());
        assertThat(parking1.parkedAt().id()).isEqualTo(sP1.id());
        assertThat(parking2).isNotNull();
        assertThat(parking2.licensePlate()).isEqualTo("BIN-1881");
        assertThat(parking2.status()).isEqualTo(ParkingStatus.IN);

        // Parking while still parked in other parking lot.
        assertThatThrownBy(() -> {
            LotDTO.CreateLotDTO cLot1 = new LotDTO.CreateLotDTO("Metro Manila", 5L);
            LotDTO.SimpleLot otherLot = lotService.registerParkingLot(cLot1);

            lotService.in(otherLot.id(), sV1.id());
        }).isInstanceOf(ConflictException.class);

        VehicleDTO.VehicleParkedAt parkingOut1 = lotService.out(sP1.id(), sV1.id());
        assertThat(parkingOut1.parkedAt().id()).isEqualTo(sP1.id());
        assertThat(parkingOut1).isNotNull();
        assertThat(parkingOut1.licensePlate()).isEqualTo("BIN-1888");
        assertThat(parkingOut1.status()).isEqualTo(ParkingStatus.OUT);

        // Parking while parking id does not exist
        assertThatThrownBy(() -> {
            lotService.in(9999L, sV1.id());
        }).isInstanceOf(NotFoundException.class);

        VehicleDTO.VehicleParkedAt parkingOut2 = lotService.out(sP1.id(), sV2.id());
        assertThat(parkingOut2.parkedAt().id()).isEqualTo(sP1.id());
        assertThat(parkingOut2).isNotNull();
        assertThat(parkingOut2.licensePlate()).isEqualTo("BIN-1881");
        assertThat(parkingOut2.status()).isEqualTo(ParkingStatus.OUT);

        // Parking is not even in
        assertThatThrownBy(() -> {
            lotService.out(sP1.id(), sV1.id());
        }).isInstanceOf(InvalidInputException.class);

        // Parking at a full parking lot should throw Invalid
        assertThatThrownBy(() -> {
            // Parking lot with only 2 capacity
            LotDTO.CreateLotDTO p2 = new LotDTO.CreateLotDTO("Bulacan", 2L);
            LotDTO.SimpleLot sP2 = lotService.registerParkingLot(p2);

            VehicleDTO.CreateVehicleDTO v3 = new VehicleDTO.CreateVehicleDTO("BIN-1891", VehicleType.MOTORCYCLE, "Mary Lois Yves Recalde");
            VehicleDTO.SimpleVehicle sV3 = vehicleService.registerVehicle(v3);

            lotService.in(sP2.id(), sV1.id());
            lotService.in(sP2.id(), sV2.id());
            lotService.in(sP2.id(), sV3.id());
        }).isInstanceOf(ConflictException.class);
    }

    @Test
    public void whenCheckingLotForAvailability_thenShouldReturnAvailability() {
        // When
        LotDTO.CreateLotDTO dto1 = new LotDTO.CreateLotDTO("Metro Manila", 5L);
        LotDTO.SimpleLot savedLot1 = lotService.registerParkingLot(dto1);

        // Then
        assertThat(savedLot1).isNotNull();

        LotDTO.LotOccupancyAndAvailability av1 = lotService.lotAvailability(savedLot1.id());
        assertThat(av1.lotId()).isEqualTo(savedLot1.id());
        assertThat(av1.availableSlots()).isEqualTo(5L);
        assertThat(av1.isSlotAvailable()).isTrue();

    }

    @Test
    public void whenCheckingParkedVehicles_thenShouldReturnParkedVehicles() {
        // When
        LotDTO.CreateLotDTO dto1 = new LotDTO.CreateLotDTO("Cebu City", 5L);
        LotDTO.SimpleLot savedLot1 = lotService.registerParkingLot(dto1);

        // Then
        assertThat(savedLot1).isNotNull();

        LotDTO.SimpleLotWithParkedVehicle parkedVehicle = lotService.parkedVehicles(savedLot1.id());
        assertThat(parkedVehicle).isNotNull();
        assertThat(parkedVehicle.lot().id()).isEqualTo(savedLot1.id());

        // When
        VehicleDTO.CreateVehicleDTO v1 = new VehicleDTO.CreateVehicleDTO("BIN-1828", VehicleType.CAR, "Jhoanna Robles");
        VehicleDTO.SimpleVehicle sV1 = vehicleService.registerVehicle(v1);

        lotService.in(savedLot1.id(), sV1.id());
        parkedVehicle = lotService.parkedVehicles(savedLot1.id());
        assertThat(parkedVehicle).isNotNull();
        assertThat(parkedVehicle.lot().id()).isEqualTo(savedLot1.id());
        assertThat(parkedVehicle.parkedVehicles()).isNotEmpty();
    }
}
