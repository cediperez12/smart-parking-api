package com.cedie.api.smartparking.controller;

import com.cedie.api.smartparking.dto.LotDTO;
import com.cedie.api.smartparking.dto.VehicleDTO;
import com.cedie.api.smartparking.service.LotService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/lots")
public class LotController {

    private final LotService lotService;

    public LotController (LotService lotService) {
        this.lotService = lotService;
    }

    @Operation(summary="Register a parking lot")
    @PostMapping
    public LotDTO.SimpleLot registerLot(@RequestBody LotDTO.CreateLotDTO dto) {
        return lotService.registerParkingLot(dto);
    }

    @Operation(summary="Check for the availability of slots in a parking lot")
    @GetMapping(value="/{id}")
    public LotDTO.LotOccupancyAndAvailability lotAvailability(@PathVariable Long id) {
        return lotService.lotAvailability(id);
    }

    @Operation(summary="Check-in a vehicle to the parking lot")
    @PostMapping(value="/{lotId}/vehicles/{vehicleId}")
    public VehicleDTO.VehicleParkedAt in(@PathVariable Long lotId, @PathVariable Long vehicleId) {
        return lotService.in(lotId, vehicleId);
    }

    @Operation(summary="Check-out a vehicle to the parking lot")
    @PutMapping(value="/{lotId}/vehicles/{vehicleId}")
    public VehicleDTO.VehicleParkedAt out(@PathVariable Long lotId, @PathVariable Long vehicleId) {
        return lotService.out(lotId, vehicleId);
    }

    @Operation(summary="Check the parked vehicles in the parking lot")
    @GetMapping(value="/{lotId}/vehicles")
    public LotDTO.SimpleLotWithParkedVehicle parkedVehicles(@PathVariable Long lotId) {
        return lotService.parkedVehicles(lotId);
    }
}
