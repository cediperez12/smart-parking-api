package com.cedie.api.smartparking.dao;

import com.cedie.api.smartparking.entity.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LotDAO extends JpaRepository<Lot, Long> {
}
