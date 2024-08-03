package com.cedie.api.smartparking.service;

import com.cedie.api.smartparking.dao.VehicleDAO;
import com.cedie.api.smartparking.dto.VehicleDTO;
import com.cedie.api.smartparking.entity.Vehicle;
import com.cedie.api.smartparking.enums.VehicleType;
import com.cedie.api.smartparking.exception.InvalidInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VehicleService {

    private final VehicleDAO vehicleDAO;

    private final Logger LOG = LoggerFactory.getLogger(VehicleService.class);

    public VehicleService(VehicleDAO vehicleDAO) {
        this.vehicleDAO = vehicleDAO;
    }

    @Transactional
    public VehicleDTO.SimpleVehicle registerVehicle(VehicleDTO.CreateVehicleDTO dto) throws InvalidInputException {
        LOG.info("VehicleService.registerVehicle()");
        LOG.info("dto : {}", dto);

        final String INVALID_LICENSE_PLATE = "The license plate you entered is not valid. Valid License Plate example: ABC-1234";
        final String DUPLICATE_LICENSE_PLATE = "This license plate has already been registered.";

        if(!isLicenseValid(dto.licensePlate()))
            throw new InvalidInputException(INVALID_LICENSE_PLATE);

        if(vehicleDAO.isVehicleExistByLicensePlate(dto.licensePlate()))
            throw new InvalidInputException(DUPLICATE_LICENSE_PLATE);

        Vehicle v = VehicleDTO.mapCreateVehicleDTOToEntity(dto);
        return VehicleDTO.mapEntityToSimpleVehicle(vehicleDAO.save(v));
    }

    /**
     * Checks if the License Plate
     * presented is valid.
     * Valid license ex: ABC-1234
     *
     * @param value The license plate being
     *              validated
     * @return {@link java.lang.Boolean}
     */
    public boolean isLicenseValid(String value) {
        Pattern pattern = Pattern.compile("^[A-Z]{3}-\\d{4}$");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
