package com.camoutech.multivendor.service;

import com.camoutech.multivendor.domain.StockOperation;
import com.camoutech.multivendor.domain.WarehouseStatus;
import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.Warehouse;
import com.camoutech.multivendor.model.WarehouseStock;

import java.util.List;

public interface WarehouseService {

    Warehouse createWarehouse(Warehouse warehouse);
    Warehouse getWarehouseById(Long id) throws Exception;
    List<Warehouse> getAllWarehouses();
    List<Warehouse> getWarehousesByStatus(WarehouseStatus status);
    Warehouse updateWarehouse(Long id, Warehouse warehouse) throws Exception;
    void deleteWarehouse(Long id) throws Exception;
    
    // Stock management
    WarehouseStock addProductToWarehouse(Warehouse warehouse, Product product, int quantity, double costPrice, double sellingPrice);
    void updateStock(Product product, int quantity, StockOperation operation);
    WarehouseStock getWarehouseStock(Warehouse warehouse, Product product);
    List<WarehouseStock> getWarehouseStocks(Warehouse warehouse);
    List<WarehouseStock> getLowStockProducts(int threshold);
    void reserveStock(Product product, int quantity);
    void unreserveStock(Product product, int quantity);
}
