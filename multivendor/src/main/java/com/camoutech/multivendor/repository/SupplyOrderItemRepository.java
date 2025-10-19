/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.model.SupplyOrder;
import com.camoutech.multivendor.model.SupplyOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyOrderItemRepository extends JpaRepository<SupplyOrderItem, Long> {
    
    List<SupplyOrderItem> findBySupplyOrder(SupplyOrder supplyOrder);
}
