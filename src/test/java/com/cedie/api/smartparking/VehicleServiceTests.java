package com.cedie.api.smartparking;

import com.cedie.api.smartparking.dto.VehicleDTO;
import com.cedie.api.smartparking.enums.VehicleType;
import com.cedie.api.smartparking.exception.InvalidInputException;
import com.cedie.api.smartparking.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class VehicleServiceTests {

    @Autowired
    public VehicleService vehicleService;

    @Test
    public void checkVehicleServiceExistence() {
        assertThat(vehicleService).isNotNull();
    }

    @Test
    public void whenVehicleRegistered_thenShouldSave() {
        // When
        VehicleDTO.CreateVehicleDTO dto = new VehicleDTO.CreateVehicleDTO("ABC-1234", VehicleType.CAR, "John Doe");
        VehicleDTO.CreateVehicleDTO dto2 = new VehicleDTO.CreateVehicleDTO("DEF-5678", VehicleType.MOTORCYCLE, "Jane Doe");

        // Then
        VehicleDTO.SimpleVehicle res = vehicleService.registerVehicle(dto);
        VehicleDTO.SimpleVehicle res2 = vehicleService.registerVehicle(dto2);

        assertThat(res).isNotNull();
        assertThat(res.licensePlate()).isEqualTo("ABC-1234");
        assertThat(res.type()).isEqualTo(VehicleType.CAR);

        assertThat(res2).isNotNull();
        assertThat(res2.licensePlate()).isEqualTo("DEF-5678");
        assertThat(res2.type()).isEqualTo(VehicleType.MOTORCYCLE);
    }

    @Test
    public void whenVehicleRegisteredAlready_thenShouldThrowInvalidInputException() {
        // When
        VehicleDTO.CreateVehicleDTO dto = new VehicleDTO.CreateVehicleDTO("GHI-1234", VehicleType.CAR, "Mariah Queen Arceta");

        // Then
        assertThatThrownBy(() -> {
            vehicleService.registerVehicle(dto);
            vehicleService.registerVehicle(dto);
        }).isInstanceOf(InvalidInputException.class);
    }

    @Test
    public void whenLicensePlateIsWrong_thenShouldReturnFalse() {
        // When
        String fakePlate = "1234-GHJ";

        // Then
        assertThat(vehicleService.isLicenseValid(fakePlate)).isFalse();
    }

}
