/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.domain.StockOperation;
import com.camoutech.multivendor.domain.WarehouseStatus;
import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.Warehouse;
import com.camoutech.multivendor.model.WarehouseStock;
import com.camoutech.multivendor.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping("/create")
    public ResponseEntity<Warehouse> createWarehouse(@RequestBody Warehouse warehouse) {
        Warehouse createdWarehouse = warehouseService.createWarehouse(warehouse);
        return new ResponseEntity<>(createdWarehouse, HttpStatus.CREATED);
    }

    @GetMapping("/{warehouseId}")
    public ResponseEntity<Warehouse> getWarehouseById(@PathVariable Long warehouseId) throws Exception {
        Warehouse warehouse = warehouseService.getWarehouseById(warehouseId);
        return new ResponseEntity<>(warehouse, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Warehouse>> getAllWarehouses() {
        List<Warehouse> warehouses = warehouseService.getAllWarehouses();
        return new ResponseEntity<>(warehouses, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Warehouse>> getWarehousesByStatus(@PathVariable WarehouseStatus status) {
        List<Warehouse> warehouses = warehouseService.getWarehousesByStatus(status);
        return new ResponseEntity<>(warehouses, HttpStatus.OK);
    }

    @PutMapping("/{warehouseId}")
    public ResponseEntity<Warehouse> updateWarehouse(@PathVariable Long warehouseId, @RequestBody Warehouse warehouse) throws Exception {
        Warehouse updatedWarehouse = warehouseService.updateWarehouse(warehouseId, warehouse);
        return new ResponseEntity<>(updatedWarehouse, HttpStatus.OK);
    }

    @DeleteMapping("/{warehouseId}")
    public ResponseEntity<String> deleteWarehouse(@PathVariable Long warehouseId) throws Exception {
        warehouseService.deleteWarehouse(warehouseId);
        return new ResponseEntity<>("Warehouse deleted successfully", HttpStatus.OK);
    }

    @PostMapping("/stock/add")
    public ResponseEntity<WarehouseStock> addProductToWarehouse(@RequestParam Long warehouseId,
                                                               @RequestParam Long productId,
                                                               @RequestParam int quantity,
                                                               @RequestParam double costPrice,
                                                               @RequestParam double sellingPrice) throws Exception {
        Warehouse warehouse = warehouseService.getWarehouseById(warehouseId);
        Product product = new Product(); // You would get this from ProductService
        product.setId(productId);
        
        WarehouseStock stock = warehouseService.addProductToWarehouse(warehouse, product, quantity, costPrice, sellingPrice);
        return new ResponseEntity<>(stock, HttpStatus.CREATED);
    }

    @PutMapping("/stock/update")
    public ResponseEntity<String> updateStock(@RequestParam Long productId,
                                             @RequestParam int quantity,
                                             @RequestParam StockOperation operation) {
        Product product = new Product(); // You would get this from ProductService
        product.setId(productId);
        
        warehouseService.updateStock(product, quantity, operation);
        return new ResponseEntity<>("Stock updated successfully", HttpStatus.OK);
    }

    @GetMapping("/stock/{warehouseId}")
    public ResponseEntity<List<WarehouseStock>> getWarehouseStocks(@PathVariable Long warehouseId) throws Exception {
        Warehouse warehouse = warehouseService.getWarehouseById(warehouseId);
        List<WarehouseStock> stocks = warehouseService.getWarehouseStocks(warehouse);
        return new ResponseEntity<>(stocks, HttpStatus.OK);
    }

    @GetMapping("/stock/low-stock")
    public ResponseEntity<List<WarehouseStock>> getLowStockProducts(@RequestParam int threshold) {
        List<WarehouseStock> lowStockProducts = warehouseService.getLowStockProducts(threshold);
        return new ResponseEntity<>(lowStockProducts, HttpStatus.OK);
    }

    @PostMapping("/stock/reserve")
    public ResponseEntity<String> reserveStock(@RequestParam Long productId, @RequestParam int quantity) {
        Product product = new Product(); // You would get this from ProductService
        product.setId(productId);
        
        warehouseService.reserveStock(product, quantity);
        return new ResponseEntity<>("Stock reserved successfully", HttpStatus.OK);
    }

    @PostMapping("/stock/unreserve")
    public ResponseEntity<String> unreserveStock(@RequestParam Long productId, @RequestParam int quantity) {
        Product product = new Product(); // You would get this from ProductService
        product.setId(productId);
        
        warehouseService.unreserveStock(product, quantity);
        return new ResponseEntity<>("Stock unreserved successfully", HttpStatus.OK);
    }
}
