package com.cedie.api.smartparking.dao;

import com.cedie.api.smartparking.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleDAO extends JpaRepository<Vehicle, Long> {

    @Query("SELECT CASE WHEN(COUNT(v) > 0) THEN TRUE ELSE FALSE END FROM Vehicle v WHERE v.licensePlate = :plate")
    public Boolean isVehicleExistByLicensePlate(@Param(value="plate") String plate);

}
