package com.cedie.api.smartparking.service;

import com.cedie.api.smartparking.dao.LotDAO;
import com.cedie.api.smartparking.dao.VehicleDAO;
import com.cedie.api.smartparking.dto.LotDTO;
import com.cedie.api.smartparking.dto.VehicleDTO;
import com.cedie.api.smartparking.entity.Lot;
import com.cedie.api.smartparking.entity.Vehicle;
import com.cedie.api.smartparking.enums.ParkingStatus;
import com.cedie.api.smartparking.exception.ConflictException;
import com.cedie.api.smartparking.exception.InvalidInputException;
import com.cedie.api.smartparking.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LotService {

    private final LotDAO lotDAO;
    private final VehicleDAO vehicleDAO;

    private final Logger LOG = LoggerFactory.getLogger(LotService.class);

    public LotService(LotDAO lotDAO, VehicleDAO vehicleDAO) {
        this.lotDAO = lotDAO;
        this.vehicleDAO = vehicleDAO;
    }

    @Transactional
    public LotDTO.SimpleLot registerParkingLot(LotDTO.CreateLotDTO dto) throws InvalidInputException {
        LOG.info("LotService.registerParkingLot()");
        LOG.info("dto : {}", dto);

        final String NO_LOCATION = "Parking lot location is missing";
        final String INVALID_CAPACITY = "The capacity must be at least 1";

        if(!StringUtils.hasText(dto.location()))
            throw new InvalidInputException(NO_LOCATION);

        if(dto.capacity() < 1)
            throw new InvalidInputException(INVALID_CAPACITY);

        Lot newLot = lotDAO.save(LotDTO.mapCreateLotDTOToEntity(dto));
        return LotDTO.mapEntityToSimpleLot(newLot);
    }

    @Transactional
    public VehicleDTO.VehicleParkedAt in(Long lotId, Long vehicleId) throws NotFoundException, InvalidInputException, ConflictException {
        LOG.info("LotService.in()");
        LOG.info("lotId     : {}", lotId);
        LOG.info("vehicleId : {}", vehicleId);

        final String VEHICLE_IS_PARKED_SOMEWHERE_ALREADY = "The vehicle is already parked somewhere.";
        final String PARKING_LOT_IS_FULL = "The parking lot is already full.";

        Lot lot = lotDAO.findById(lotId).orElseThrow(() -> {
            return new NotFoundException(String.format("lotId:%d", lotId));
        });

        Vehicle vehicle = vehicleDAO.findById(vehicleId).orElseThrow(() -> {
            return new NotFoundException(String.format("vehicleId:%d", vehicleId));
        });

        // if the vehicle is already parked somewhere.
        if(Optional.ofNullable(vehicle.getParkedAt()).isPresent()) {
            throw new ConflictException(VEHICLE_IS_PARKED_SOMEWHERE_ALREADY);
        }

        // If the parking lot is already full
        if(lot.getOccupiedSpaces().equals(lot.getCapacity())) {
            throw new ConflictException(PARKING_LOT_IS_FULL);
        }

        // Set the vehicle to be parked at the lot
        vehicle.setParkedAt(lot);

        Set<Vehicle> vehiclesParked = lot.getOccupants();
        vehiclesParked.add(vehicle);

        lot.setOccupiedSpaces(lot.getOccupiedSpaces() + 1);

        Vehicle parked = vehicleDAO.save(vehicle);
        Lot parkingLot = lotDAO.save(lot);

        return new VehicleDTO.VehicleParkedAt(
                LotDTO.mapEntityToSimpleLot(parkingLot),
                parked.getLicensePlate(),
                LocalDateTime.now(),
                ParkingStatus.IN
        );
    }

    @Transactional
    public VehicleDTO.VehicleParkedAt out(Long lotId, Long vehicleId) throws NotFoundException, InvalidInputException {
        LOG.info("LotService.out()");
        LOG.info("lotId     : {}", lotId);
        LOG.info("vehicleId : {}", vehicleId);

        final String VEHICLE_IS_NOT_PARKED = "Vehicle is not parked.";
        final String VEHICLE_IS_NOT_PARKED_AT_THE_PARKING_LOT = "Vehicle is not parked at the said parking lot.";

        Lot lot = lotDAO.findById(lotId).orElseThrow(() -> {
            return new NotFoundException(String.format("lotId:%d", lotId));
        });

        Vehicle vehicle = vehicleDAO.findById(vehicleId).orElseThrow(() -> {
            return new NotFoundException(String.format("vehicleId:%d", vehicleId));
        });

        // Vehicle must be parked
        if(vehicle.getParkedAt() == null) {
            throw new InvalidInputException(VEHICLE_IS_NOT_PARKED);
        }

        // Vehicle must be parked at the said parking lot
        if(!vehicle.getParkedAt().equals(lot)) {
            throw new ConflictException(VEHICLE_IS_NOT_PARKED_AT_THE_PARKING_LOT);
        }

        vehicle.setParkedAt(null);

        Set<Vehicle> occupants = lot.getOccupants();
        occupants.remove(vehicle);

        lot.setOccupants(occupants);
        lot.setOccupiedSpaces(lot.getOccupiedSpaces() - 1);

        Vehicle parked = vehicleDAO.save(vehicle);
        Lot parkingLot = lotDAO.save(lot);

        return new VehicleDTO.VehicleParkedAt(
                LotDTO.mapEntityToSimpleLot(parkingLot),
                parked.getLicensePlate(),
                LocalDateTime.now(),
                ParkingStatus.OUT
        );
    }

    @Transactional
    public LotDTO.LotOccupancyAndAvailability lotAvailability(Long lotId) throws NotFoundException {
        LOG.info("LotService.lotAvailability()");
        LOG.info("lotId : {}", lotId);

        final String NO_PARKING_LOT_FOUND = "No parking lot found with the given id.";

        Lot lot = lotDAO.findById(lotId).orElseThrow(() -> {
            return new NotFoundException(NO_PARKING_LOT_FOUND);
        });

        long availableSlot = lot.getCapacity() - lot.getOccupiedSpaces();
        boolean isAnySlotAvailable = availableSlot != 0;

        return new LotDTO.LotOccupancyAndAvailability(lot.getId(), lot.getLocation(), lot.getCapacity(), availableSlot, isAnySlotAvailable);
    }

    @Transactional
    public LotDTO.SimpleLotWithParkedVehicle parkedVehicles(Long lotId) throws NotFoundException {
        LOG.info("LotService.parkedVehicles()");
        LOG.info("lotId : {}", lotId);

        final String NO_PARKING_LOT_FOUND = "No parking lot found with the given id.";

        Lot lot = lotDAO.findById(lotId).orElseThrow(() -> {
            return new NotFoundException(NO_PARKING_LOT_FOUND);
        });

        LotDTO.SimpleLot simpleLot = LotDTO.mapEntityToSimpleLot(lot);
        return new LotDTO.SimpleLotWithParkedVehicle(simpleLot, lot.getOccupants().stream().map(VehicleDTO::mapEntityToSimpleVehicle).collect(Collectors.toSet()));
    }
}
