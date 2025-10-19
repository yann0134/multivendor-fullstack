package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    @Query("SELECT oi FROM OrderItem oi JOIN oi.product p WHERE p.supplier.id = :supplierId AND oi.order.orderDate BETWEEN :from AND :to")
    List<OrderItem> findByProductSupplierAndOrderDateBetween(@Param("supplierId") Long supplierId, 
                                                              @Param("from") LocalDateTime from, 
                                                              @Param("to") LocalDateTime to);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.orderDate BETWEEN :from AND :to")
    List<OrderItem> findByOrderDateBetween(@Param("from") LocalDateTime from, 
                                           @Param("to") LocalDateTime to);
}
