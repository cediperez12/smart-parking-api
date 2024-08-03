package com.cedie.api.smartparking.controller;

import com.cedie.api.smartparking.dto.VehicleDTO;
import com.cedie.api.smartparking.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(summary="Register a vehicle")
    @PostMapping
    public VehicleDTO.SimpleVehicle registerVehicle(@RequestBody VehicleDTO.CreateVehicleDTO dto) {
        return vehicleService.registerVehicle(dto);
    }
}
