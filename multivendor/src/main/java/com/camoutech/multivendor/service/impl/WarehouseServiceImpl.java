/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.service.impl;

import com.camoutech.multivendor.domain.StockOperation;
import com.camoutech.multivendor.domain.WarehouseStatus;
import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.Warehouse;
import com.camoutech.multivendor.model.WarehouseStock;
import com.camoutech.multivendor.repository.WarehouseRepository;
import com.camoutech.multivendor.repository.WarehouseStockRepository;
import com.camoutech.multivendor.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseStockRepository warehouseStockRepository;

    @Override
    public Warehouse createWarehouse(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    @Override
    public Warehouse getWarehouseById(Long id) throws Exception {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new Exception("Warehouse not found with id: " + id));
    }

    @Override
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    @Override
    public List<Warehouse> getWarehousesByStatus(WarehouseStatus status) {
        return warehouseRepository.findByStatus(status);
    }

    @Override
    public Warehouse updateWarehouse(Long id, Warehouse warehouse) throws Exception {
        Warehouse existingWarehouse = getWarehouseById(id);
        existingWarehouse.setWarehouseName(warehouse.getWarehouseName());
        existingWarehouse.setLocation(warehouse.getLocation());
        existingWarehouse.setStatus(warehouse.getStatus());
        existingWarehouse.setCapacity(warehouse.getCapacity());
        existingWarehouse.setDescription(warehouse.getDescription());
        return warehouseRepository.save(existingWarehouse);
    }

    @Override
    public void deleteWarehouse(Long id) throws Exception {
        Warehouse warehouse = getWarehouseById(id);
        warehouseRepository.delete(warehouse);
    }

    @Override
    public WarehouseStock addProductToWarehouse(Warehouse warehouse, Product product, int quantity, double costPrice, double sellingPrice) {
        Optional<WarehouseStock> existingStock = warehouseStockRepository.findByWarehouseAndProduct(warehouse, product);
        
        if (existingStock.isPresent()) {
            WarehouseStock stock = existingStock.get();
            stock.setQuantity(stock.getQuantity() + quantity);
            stock.setCostPrice(costPrice);
            stock.setSellingPrice(sellingPrice);
            stock.setLastUpdated(LocalDateTime.now());
            return warehouseStockRepository.save(stock);
        } else {
            WarehouseStock newStock = new WarehouseStock();
            newStock.setWarehouse(warehouse);
            newStock.setProduct(product);
            newStock.setQuantity(quantity);
            newStock.setReservedQuantity(0);
            newStock.setAvailableQuantity(quantity);
            newStock.setCostPrice(costPrice);
            newStock.setSellingPrice(sellingPrice);
            newStock.setLastUpdated(LocalDateTime.now());
            return warehouseStockRepository.save(newStock);
        }
    }

    @Override
    public void updateStock(Product product, int quantity, StockOperation operation) {
        List<WarehouseStock> stocks = warehouseStockRepository.findByProduct(product);
        
        for (WarehouseStock stock : stocks) {
            switch (operation) {
                case INCOMING:
                    stock.setQuantity(stock.getQuantity() + quantity);
                    break;
                case OUTGOING:
                    stock.setQuantity(stock.getQuantity() - quantity);
                    break;
                case RESERVE:
                    stock.setReservedQuantity(stock.getReservedQuantity() + quantity);
                    break;
                case UNRESERVE:
                    stock.setReservedQuantity(stock.getReservedQuantity() - quantity);
                    break;
                case ADJUSTMENT:
                    stock.setQuantity(quantity);
                    break;
            }
            
            stock.setAvailableQuantity(stock.getQuantity() - stock.getReservedQuantity());
            stock.setLastUpdated(LocalDateTime.now());
            warehouseStockRepository.save(stock);
        }
    }

    @Override
    public WarehouseStock getWarehouseStock(Warehouse warehouse, Product product) {
        return warehouseStockRepository.findByWarehouseAndProduct(warehouse, product)
                .orElse(null);
    }

    @Override
    public List<WarehouseStock> getWarehouseStocks(Warehouse warehouse) {
        return warehouseStockRepository.findByWarehouse(warehouse);
    }

    @Override
    public List<WarehouseStock> getLowStockProducts(int threshold) {
        return warehouseStockRepository.findByAvailableQuantityGreaterThan(threshold);
    }

    @Override
    public void reserveStock(Product product, int quantity) {
        updateStock(product, quantity, StockOperation.RESERVE);
    }

    @Override
    public void unreserveStock(Product product, int quantity) {
        updateStock(product, quantity, StockOperation.UNRESERVE);
    }
}
