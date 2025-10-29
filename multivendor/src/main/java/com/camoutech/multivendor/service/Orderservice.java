/**
 * Service pour la gestion des commandes
 * Created by camoutech
 * Date :21/10/2024
 * Time :14:00
 * Project Name :multivendor
 */

package com.camoutech.multivendor.service;

import com.camoutech.multivendor.model.*;
import com.camoutech.multivendor.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;
import java.util.Map;

public interface OrderService {
    
    // Gestion des commandes
    Order createOrder(Order order);
    Order updateOrder(Long orderId, Order order);
    void deleteOrder(Long orderId);
    Order getOrderById(Long orderId);
    List<Order> getAllOrders();
    Page<Order> getOrders(Pageable pageable);
    
    // Gestion des commandes par vendeur
    List<Order> getOrdersBySeller(Long sellerId);
    List<Order> getOrdersByCustomer(Long customerId);
    List<Order> getOrdersByStatus(OrderStatus status);
    
    // Mise à jour du statut
    Order updateOrderStatus(Long orderId, OrderStatus status);
    
    // Recherche et filtres
    List<Order> searchOrders(String query);
    List<Order> getOrdersByDateRange(java.time.LocalDateTime from, java.time.LocalDateTime to);
    
    // Statistiques
    long countOrders();
    long countOrdersByStatus(OrderStatus status);
    long countOrdersBySeller(Long sellerId);
    
    // Méthodes spécifiques
    Set<Order> createOrder(User user, Address shippingAddress, Cart cart);
    List<Order> usersOrderHistory(Long userId);
    Order findOrderById(Long orderId);
    OrderItem getOrderItemById(Long orderItemId);
    Order cancelOrder(Long orderId, User user);
    
    // Méthodes pour l'entrepôt
    Page<Order> getAllOrdersForWarehouse(String status, String search, Pageable pageable);
    Map<String, Object> getOrderStatsForWarehouse();
    Order markOrderAsReady(Long orderId);
    Order assignDeliveryPerson(Long orderId, Long deliveryPersonId, String deliveryNotes);
    Order updateDeliveryStatus(Long orderId, String status);
    
    // Méthodes de suppression
    boolean deleteOrder(Long orderId, Long userId);
    int deleteAllOrdersByUser(Long userId);
    
    // Méthode pour supprimer les commandes vides (sans orderItems)
    int deleteEmptyOrders();
}
