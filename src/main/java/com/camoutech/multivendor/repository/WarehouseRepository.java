/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.domain.WarehouseStatus;
import com.camoutech.multivendor.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    
    List<Warehouse> findByStatus(WarehouseStatus status);
    
    List<Warehouse> findByWarehouseNameContainingIgnoreCase(String name);
}
