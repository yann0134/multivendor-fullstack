package com.camoutech.multivendor.service;

import com.camoutech.multivendor.model.*;
import com.camoutech.multivendor.request.CreateSupplyOrderRequest;

import java.time.LocalDateTime;

public interface SupermarketWorkflowService {

    // Workflow d'approvisionnement
    SupplyOrder processSupplyOrder(CreateSupplyOrderRequest request) throws Exception;
    void completeSupplyOrder(Long supplyOrderId) throws Exception;
    
    // Workflow de vente
    void processCustomerOrder(Order customerOrder) throws Exception;
    void completeCustomerDelivery(Long orderId) throws Exception;
    
    // Workflow de stock
    void updateWarehouseStock(Product product, int quantity, String operation) throws Exception;
    void reserveStockForOrder(Order order) throws Exception;
    void releaseReservedStock(Order order) throws Exception;
    
    // Workflow de commission
    void calculateAndProcessCommissions(Long supplierId, LocalDateTime from, LocalDateTime to) throws Exception;
    double getSupplierPendingPayment(Long supplierId) throws Exception;
    
    // Workflow de livraison
    DeliveryTask assignDeliveryTask(Order customerOrder) throws Exception;
    DeliveryTask assignPickupTask(SupplyOrder supplyOrder) throws Exception;
    void completeDeliveryTask(Long taskId, String notes) throws Exception;
}
