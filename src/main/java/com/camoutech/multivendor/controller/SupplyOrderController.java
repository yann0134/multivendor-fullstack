/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.domain.SupplyOrderStatus;
import com.camoutech.multivendor.model.SupplyOrder;
import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.request.CreateSupplyOrderRequest;
import com.camoutech.multivendor.request.UpdateStatusRequest;
import com.camoutech.multivendor.request.UpdateShipmentStatusRequest;
import com.camoutech.multivendor.repository.SupplierRepository;
import com.camoutech.multivendor.repository.WarehouseRepository;
import com.camoutech.multivendor.service.SupplyOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.camoutech.multivendor.model.Supplier;
import com.camoutech.multivendor.model.Warehouse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/supply-orders")
public class SupplyOrderController {

    private final SupplyOrderService supplyOrderService;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;

    @PostMapping("/create")
    public ResponseEntity<SupplyOrder> createSupplyOrder(@RequestBody CreateSupplyOrderRequest request) throws Exception {
        SupplyOrder supplyOrder = supplyOrderService.createSupplyOrder(request);
        return new ResponseEntity<>(supplyOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{supplyOrderId}")
    public ResponseEntity<SupplyOrder> getSupplyOrderById(@PathVariable Long supplyOrderId) throws Exception {
        SupplyOrder supplyOrder = supplyOrderService.getSupplyOrderById(supplyOrderId);
        return new ResponseEntity<>(supplyOrder, HttpStatus.OK);
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<SupplyOrder>> getSupplyOrdersBySupplier(@PathVariable Long supplierId) {
        List<SupplyOrder> supplyOrders = supplyOrderService.getSupplyOrdersBySupplier(supplierId);
        return new ResponseEntity<>(supplyOrders, HttpStatus.OK);
    }



    @GetMapping("/status/{status}")
    public ResponseEntity<List<SupplyOrder>> getSupplyOrdersByStatus(@PathVariable SupplyOrderStatus status) {
        List<SupplyOrder> supplyOrders = supplyOrderService.getSupplyOrdersByStatus(status);
        return new ResponseEntity<>(supplyOrders, HttpStatus.OK);
    }

    @PutMapping("/{supplyOrderId}/status")
    public ResponseEntity<SupplyOrder> updateSupplyOrderStatus(@PathVariable Long supplyOrderId, 
                                                              @RequestBody UpdateStatusRequest request) throws Exception {
        SupplyOrder supplyOrder = supplyOrderService.updateSupplyOrderStatus(supplyOrderId, request.getStatus());
        return new ResponseEntity<>(supplyOrder, HttpStatus.OK);
    }

    @PutMapping("/{supplyOrderId}/shipment-status")
    public ResponseEntity<SupplyOrder> updateShipmentStatus(@PathVariable Long supplyOrderId, 
                                                           @RequestBody UpdateShipmentStatusRequest request) throws Exception {
        SupplyOrder supplyOrder = supplyOrderService.updateShipmentStatus(supplyOrderId, request.getStatus());
        return new ResponseEntity<>(supplyOrder, HttpStatus.OK);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<SupplyOrder>> getSupplyOrdersByDateRange(@RequestParam LocalDateTime from, 
                                                                        @RequestParam LocalDateTime to) {
        List<SupplyOrder> supplyOrders = supplyOrderService.getSupplyOrdersByDateRange(from, to);
        return new ResponseEntity<>(supplyOrders, HttpStatus.OK);
    }

    @PutMapping("/{supplyOrderId}/confirm-delivery-to-warehouse")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
    public ResponseEntity<SupplyOrder> confirmDeliveryToWarehouse(@PathVariable Long supplyOrderId) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Supplier supplier = supplierRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé pour l'email: " + email));
        SupplyOrder supplyOrder = supplyOrderService.confirmDeliveryToWarehouse(supplyOrderId, supplier.getId());
        return new ResponseEntity<>(supplyOrder, HttpStatus.OK);
    }

    @PutMapping("/{supplyOrderId}/complete-delivery")
    @PreAuthorize("hasAuthority('ROLE_WAREHOUSE')")
    public ResponseEntity<SupplyOrder> completeDelivery(
            @PathVariable Long supplyOrderId,
            @RequestParam Long warehouseId) throws Exception {

        SupplyOrder supplyOrder = supplyOrderService.completeSupplyOrderDelivery(supplyOrderId, warehouseId);
        return new ResponseEntity<>(supplyOrder, HttpStatus.OK);
    }

    @GetMapping("/supplier/by-product-status")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
    public ResponseEntity<List<SupplyOrder>> getSupplyOrdersByCurrentSupplierAndProductStatus(
            @RequestParam Product.ProductStatus productStatus) {
        List<SupplyOrder> supplyOrders = supplyOrderService.getSupplyOrdersByCurrentSupplierAndProductStatus(productStatus);
        return new ResponseEntity<>(supplyOrders, HttpStatus.OK);
    }



    @GetMapping("/supplier")
    public ResponseEntity<List<Product>> getSupplyOrdersByCurrentSupplier() {
        List<Product> supplyOrders = supplyOrderService.getSupplyOrdersByCurrentSupplier();
        return new ResponseEntity<>(supplyOrders, HttpStatus.OK);
    }



    @GetMapping("/supplier/expedier")
    public ResponseEntity<List<Product>> getSupplyOrdersByCurrentSupplierExpedier() {
        List<Product> supplyOrders = supplyOrderService.getSupplyOrdersByCurrentSupplierExpedier();
        return new ResponseEntity<>(supplyOrders, HttpStatus.OK);
    }








}
