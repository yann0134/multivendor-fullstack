/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.domain.SupplyOrderStatus;
import com.camoutech.multivendor.model.Supplier;
import com.camoutech.multivendor.model.SupplyOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SupplyOrderRepository extends JpaRepository<SupplyOrder, Long> {
    
    List<SupplyOrder> findBySupplier(Supplier supplier);
    
    List<SupplyOrder> findByStatus(SupplyOrderStatus status);
    
    List<SupplyOrder> findBySupplierAndStatus(Supplier supplier, SupplyOrderStatus status);
    
    List<SupplyOrder> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
