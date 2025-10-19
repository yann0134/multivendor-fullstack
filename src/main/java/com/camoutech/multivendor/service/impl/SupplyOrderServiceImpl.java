/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.service.impl;

import com.camoutech.multivendor.domain.SupplyOrderStatus;
import com.camoutech.multivendor.model.*;
import com.camoutech.multivendor.repository.*;
import com.camoutech.multivendor.request.CreateSupplyOrderRequest;
import com.camoutech.multivendor.service.SupplyOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplyOrderServiceImpl implements SupplyOrderService {

    private final SupplyOrderRepository supplyOrderRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final SupplyOrderItemRepository supplyOrderItemRepository;

    @Override
    public SupplyOrder createSupplyOrder(CreateSupplyOrderRequest request) throws Exception {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new Exception("Supplier not found"));
        
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new Exception("Warehouse not found"));

        SupplyOrder supplyOrder = new SupplyOrder();
        supplyOrder.setSupplyOrderId(UUID.randomUUID().toString());
        supplyOrder.setSupplier(supplier);
        supplyOrder.setWarehouse(warehouse);
        supplyOrder.setStatus(SupplyOrderStatus.PENDING);
        supplyOrder.setOrderDate(LocalDateTime.now());
        supplyOrder.setNotes(request.getNotes());

        SupplyOrder savedSupplyOrder = supplyOrderRepository.save(supplyOrder);

        double totalAmount = 0;
        for (var itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new Exception("Product not found"));

            SupplyOrderItem item = new SupplyOrderItem();
            item.setSupplyOrder(savedSupplyOrder);
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setTotalPrice(itemRequest.getQuantity() * itemRequest.getUnitPrice());
            item.setNotes(itemRequest.getNotes());

            supplyOrderItemRepository.save(item);
            totalAmount += item.getTotalPrice();
        }

        savedSupplyOrder.setTotalAmount(totalAmount);
        return supplyOrderRepository.save(savedSupplyOrder);
    }

    @Override
    public SupplyOrder getSupplyOrderById(Long supplyOrderId) throws Exception {
        return supplyOrderRepository.findById(supplyOrderId)
                .orElseThrow(() -> new Exception("Supply order not found with id: " + supplyOrderId));
    }

    @Override
    public List<SupplyOrder> getSupplyOrdersBySupplier(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId).orElse(null);
        return supplier != null ? supplyOrderRepository.findBySupplier(supplier) : List.of();
    }

    @Override
    public List<SupplyOrder> getSupplyOrdersByStatus(SupplyOrderStatus status) {
        return supplyOrderRepository.findByStatus(status);
    }

    @Override
    public SupplyOrder updateSupplyOrderStatus(Long supplyOrderId, SupplyOrderStatus status) throws Exception {
        SupplyOrder supplyOrder = getSupplyOrderById(supplyOrderId);
        supplyOrder.setStatus(status);
        return supplyOrderRepository.save(supplyOrder);
    }

    @Override
    public List<SupplyOrder> getSupplyOrdersByDateRange(LocalDateTime from, LocalDateTime to) {
        return supplyOrderRepository.findByOrderDateBetween(from, to);
    }
}
