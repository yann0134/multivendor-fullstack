/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.Warehouse;
import com.camoutech.multivendor.model.WarehouseStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, Long> {
    
    Optional<WarehouseStock> findByWarehouseAndProduct(Warehouse warehouse, Product product);
    
    List<WarehouseStock> findByWarehouse(Warehouse warehouse);
    
    List<WarehouseStock> findByProduct(Product product);
    
    List<WarehouseStock> findByAvailableQuantityGreaterThan(int quantity);
}
