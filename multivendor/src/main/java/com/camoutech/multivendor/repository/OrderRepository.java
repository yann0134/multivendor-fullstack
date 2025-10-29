package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.model.Order;
import com.camoutech.multivendor.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);
    List<Order> findBySellerId(Long sellerId);
    List<Order> findByOrderStatus(OrderStatus status);
    List<Order> findByOrderStatusIn(List<OrderStatus> statuses);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = :status")
    long countByOrderStatus(@Param("status") OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE " +
           "LOWER(o.user.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.user.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.orderId) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Order> searchOrders(@Param("query") String query);
}
