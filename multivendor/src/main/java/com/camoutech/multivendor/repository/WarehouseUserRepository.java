/**
 * Created by camoutech
 * Date :19/10/2024
 * Time :15:35
 * Project Name :multivendor
 */

package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.model.WarehouseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseUserRepository extends JpaRepository<WarehouseUser, Long> {
    
    Optional<WarehouseUser> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<WarehouseUser> findByWarehouseCode(String warehouseCode);
    
    Optional<WarehouseUser> findByEmployeeId(String employeeId);
}
