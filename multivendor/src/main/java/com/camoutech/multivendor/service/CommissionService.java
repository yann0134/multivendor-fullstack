package com.camoutech.multivendor.service;

import com.camoutech.multivendor.model.OrderItem;
import com.camoutech.multivendor.model.Product;

import java.time.LocalDateTime;
import java.util.List;

public interface CommissionService {

    double calculateSupplierCommission(Product product, int quantitySold);
    double calculateSupplierPayment(Long supplierId, LocalDateTime from, LocalDateTime to);
    double calculateSupermarketMargin(Product product, int quantitySold);
    List<OrderItem> getSoldItemsBySupplier(Long supplierId, LocalDateTime from, LocalDateTime to);
    double getTotalCommissionEarned(LocalDateTime from, LocalDateTime to);
    double getSupplierEarnings(Long supplierId, LocalDateTime from, LocalDateTime to);
}
