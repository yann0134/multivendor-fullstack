package com.camoutech.multivendor.service;

import com.camoutech.multivendor.domain.SupplyOrderStatus;
import com.camoutech.multivendor.model.SupplyOrder;
import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.request.CreateSupplyOrderRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface SupplyOrderService {

    SupplyOrder createSupplyOrder(CreateSupplyOrderRequest request) throws Exception;
    SupplyOrder getSupplyOrderById(Long supplyOrderId) throws Exception;
    List<SupplyOrder> getSupplyOrdersBySupplier(Long supplierId);
    List<Product> getSupplyOrdersByCurrentSupplier();
    List<SupplyOrder> getSupplyOrdersByStatus(SupplyOrderStatus status);
    SupplyOrder updateSupplyOrderStatus(Long supplyOrderId, SupplyOrderStatus status) throws Exception;
    SupplyOrder updateShipmentStatus(Long supplyOrderId, com.camoutech.multivendor.model.Product.ShipmentStatus status) throws Exception;
    List<SupplyOrder> getSupplyOrdersByDateRange(LocalDateTime from, LocalDateTime to);
    SupplyOrder confirmDeliveryToWarehouse(Long supplyOrderId, Long supplierId) throws Exception;
    List<SupplyOrder> getSupplyOrdersByCurrentSupplierAndProductStatus(Product.ProductStatus productStatus);
    SupplyOrder completeSupplyOrderDelivery(Long supplyOrderId, Long warehouseId) throws Exception;



}
