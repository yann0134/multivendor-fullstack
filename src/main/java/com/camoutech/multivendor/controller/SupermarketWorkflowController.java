/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.model.DeliveryTask;
import com.camoutech.multivendor.model.Order;
import com.camoutech.multivendor.model.SupplyOrder;
import com.camoutech.multivendor.request.CreateSupplyOrderRequest;
import com.camoutech.multivendor.service.SupermarketWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/supermarket/workflow")
public class SupermarketWorkflowController {

    private final SupermarketWorkflowService workflowService;

    @PostMapping("/supply-order/process")
    public ResponseEntity<SupplyOrder> processSupplyOrder(@RequestBody CreateSupplyOrderRequest request) throws Exception {
        SupplyOrder supplyOrder = workflowService.processSupplyOrder(request);
        return new ResponseEntity<>(supplyOrder, HttpStatus.CREATED);
    }

    @PutMapping("/supply-order/{supplyOrderId}/complete")
    public ResponseEntity<String> completeSupplyOrder(@PathVariable Long supplyOrderId) throws Exception {
        workflowService.completeSupplyOrder(supplyOrderId);
        return new ResponseEntity<>("Supply order completed successfully", HttpStatus.OK);
    }

    @PostMapping("/customer-order/process")
    public ResponseEntity<String> processCustomerOrder(@RequestBody Order customerOrder) throws Exception {
        workflowService.processCustomerOrder(customerOrder);
        return new ResponseEntity<>("Customer order processed successfully", HttpStatus.OK);
    }

    @PutMapping("/customer-order/{orderId}/delivery-complete")
    public ResponseEntity<String> completeCustomerDelivery(@PathVariable Long orderId) throws Exception {
        workflowService.completeCustomerDelivery(orderId);
        return new ResponseEntity<>("Customer delivery completed successfully", HttpStatus.OK);
    }

    @PutMapping("/stock/update")
    public ResponseEntity<String> updateWarehouseStock(@RequestParam Long productId,
                                                      @RequestParam int quantity,
                                                      @RequestParam String operation) throws Exception {
        // Vous devrez récupérer le produit depuis ProductService
        workflowService.updateWarehouseStock(null, quantity, operation);
        return new ResponseEntity<>("Stock updated successfully", HttpStatus.OK);
    }

    @PostMapping("/stock/reserve")
    public ResponseEntity<String> reserveStockForOrder(@RequestBody Order order) throws Exception {
        workflowService.reserveStockForOrder(order);
        return new ResponseEntity<>("Stock reserved successfully", HttpStatus.OK);
    }

    @PostMapping("/stock/release")
    public ResponseEntity<String> releaseReservedStock(@RequestBody Order order) throws Exception {
        workflowService.releaseReservedStock(order);
        return new ResponseEntity<>("Stock released successfully", HttpStatus.OK);
    }

    @PostMapping("/commission/calculate")
    public ResponseEntity<String> calculateAndProcessCommissions(@RequestParam Long supplierId,
                                                                 @RequestParam LocalDateTime from,
                                                                 @RequestParam LocalDateTime to) throws Exception {
        workflowService.calculateAndProcessCommissions(supplierId, from, to);
        return new ResponseEntity<>("Commissions calculated and processed successfully", HttpStatus.OK);
    }

    @GetMapping("/supplier/{supplierId}/pending-payment")
    public ResponseEntity<Map<String, Double>> getSupplierPendingPayment(@PathVariable Long supplierId) throws Exception {
        double pendingPayment = workflowService.getSupplierPendingPayment(supplierId);
        return new ResponseEntity<>(Map.of("pendingPayment", pendingPayment), HttpStatus.OK);
    }

    @PostMapping("/delivery/assign")
    public ResponseEntity<DeliveryTask> assignDeliveryTask(@RequestBody Order customerOrder) throws Exception {
        DeliveryTask task = workflowService.assignDeliveryTask(customerOrder);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @PostMapping("/pickup/assign")
    public ResponseEntity<DeliveryTask> assignPickupTask(@RequestBody SupplyOrder supplyOrder) throws Exception {
        DeliveryTask task = workflowService.assignPickupTask(supplyOrder);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @PutMapping("/delivery/{taskId}/complete")
    public ResponseEntity<String> completeDeliveryTask(@PathVariable Long taskId, 
                                                      @RequestParam(required = false) String notes) throws Exception {
        workflowService.completeDeliveryTask(taskId, notes);
        return new ResponseEntity<>("Delivery task completed successfully", HttpStatus.OK);
    }
}
