/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.domain.SupplyOrderStatus;
import com.camoutech.multivendor.model.SupplyOrder;
import com.camoutech.multivendor.request.CreateSupplyOrderRequest;
import com.camoutech.multivendor.service.SupplyOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/supply-orders")
public class SupplyOrderController {

    private final SupplyOrderService supplyOrderService;

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
                                                              @RequestParam SupplyOrderStatus status) throws Exception {
        SupplyOrder supplyOrder = supplyOrderService.updateSupplyOrderStatus(supplyOrderId, status);
        return new ResponseEntity<>(supplyOrder, HttpStatus.OK);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<SupplyOrder>> getSupplyOrdersByDateRange(@RequestParam LocalDateTime from, 
                                                                        @RequestParam LocalDateTime to) {
        List<SupplyOrder> supplyOrders = supplyOrderService.getSupplyOrdersByDateRange(from, to);
        return new ResponseEntity<>(supplyOrders, HttpStatus.OK);
    }
}
