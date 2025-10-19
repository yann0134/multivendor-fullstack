/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.service.impl;

import com.camoutech.multivendor.model.OrderItem;
import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.repository.OrderItemRepository;
import com.camoutech.multivendor.service.CommissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommissionServiceImpl implements CommissionService {

    private final OrderItemRepository orderItemRepository;

    @Override
    public double calculateSupplierCommission(Product product, int quantitySold) {
        double supplierPrice = product.getSupplierPrice();
        double sellingPrice = product.getSellingPrice();
        double margin = sellingPrice - supplierPrice;
        return margin * quantitySold;
    }

    @Override
    public double calculateSupplierPayment(Long supplierId, LocalDateTime from, LocalDateTime to) {
        List<OrderItem> soldItems = getSoldItemsBySupplier(supplierId, from, to);
        return soldItems.stream()
                .mapToDouble(item -> item.getProduct().getSupplierPrice() * item.getQuantity())
                .sum();
    }

    @Override
    public double calculateSupermarketMargin(Product product, int quantitySold) {
        return calculateSupplierCommission(product, quantitySold);
    }

    @Override
    public List<OrderItem> getSoldItemsBySupplier(Long supplierId, LocalDateTime from, LocalDateTime to) {
        return orderItemRepository.findByProductSupplierAndOrderDateBetween(supplierId, from, to);
    }

    @Override
    public double getTotalCommissionEarned(LocalDateTime from, LocalDateTime to) {
        // Calculate total commission earned by supermarket
        List<OrderItem> allSoldItems = orderItemRepository.findByOrderDateBetween(from, to);
        return allSoldItems.stream()
                .mapToDouble(item -> calculateSupermarketMargin(item.getProduct(), item.getQuantity()))
                .sum();
    }

    @Override
    public double getSupplierEarnings(Long supplierId, LocalDateTime from, LocalDateTime to) {
        return calculateSupplierPayment(supplierId, from, to);
    }
}
