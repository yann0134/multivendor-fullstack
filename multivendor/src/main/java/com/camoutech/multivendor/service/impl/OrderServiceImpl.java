/**
 * Implémentation du service de gestion des commandes
 * Created by camoutech
 * Date :21/10/2024
 * Time :14:05
 * Project Name :multivendor
 */

package com.camoutech.multivendor.service.impl;

import com.camoutech.multivendor.model.*;
import com.camoutech.multivendor.domain.OrderStatus;
import com.camoutech.multivendor.domain.USER_ROLE;
import com.camoutech.multivendor.repository.OrderRepository;
import com.camoutech.multivendor.repository.OrderItemRepository;
import com.camoutech.multivendor.repository.PaymentOrderRepository;
import com.camoutech.multivendor.repository.ProductRepository;
import com.camoutech.multivendor.repository.DeliveryPersonRepository;
import com.camoutech.multivendor.repository.UserRepository;
import com.camoutech.multivendor.service.OrderService;
import com.camoutech.multivendor.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentOrderRepository paymentOrderRepository;
    private final WarehouseService warehouseService;
    private final ProductRepository productRepository;
    private final DeliveryPersonRepository deliveryPersonRepository;
    private final UserRepository userRepository;

    @Override
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrder(Long orderId, Order order) {
        order.setId(orderId);
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + orderId));
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Page<Order> getOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public List<Order> getOrdersBySeller(Long sellerId) {
        // Filtrer les commandes par ID du vendeur
        return orderRepository.findBySellerId(sellerId);
    }

    @Override
    public List<Order> getOrdersByCustomer(Long customerId) {
        // Filtrer les commandes par ID du client connecté
        List<Order> orders = orderRepository.findByUserId(customerId);
        
        // Charger les informations du livreur pour chaque commande
        for (Order order : orders) {
            loadDeliveryPersonInfo(order);
        }
        
        return orders;
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByOrderStatus(status);
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> searchOrders(String query) {
        return orderRepository.searchOrders(query);
    }

    @Override
    public List<Order> getOrdersByDateRange(LocalDateTime from, LocalDateTime to) {
        // Méthode simplifiée - retourner toutes les commandes pour l'instant
        return orderRepository.findAll();
    }

    @Override
    public long countOrders() {
        return orderRepository.count();
    }

    @Override
    public long countOrdersByStatus(OrderStatus status) {
        return orderRepository.countByOrderStatus(status);
    }

    @Override
    public long countOrdersBySeller(Long sellerId) {
        // Méthode simplifiée
        return orderRepository.count();
    }

    @Override
    public Set<Order> createOrder(User user, Address shippingAddress, Cart cart) {
        // Implémentation pour créer une commande complète avec tous les produits du panier
        Set<Order> orders = new HashSet<>();
        Order order = new Order();
        
        // Définir l'utilisateur (customer_id)
        order.setUser(user);
        
        // Définir l'adresse de livraison
        order.setShippingAddress(shippingAddress);
        
        // Définir le statut initial
        order.setOrderStatus(OrderStatus.PENDING);
        
        // Calculer les totaux basés sur le panier et créer les OrderItems
        if (cart != null && cart.getCartItems() != null && !cart.getCartItems().isEmpty()) {
            double totalMrp = 0.0;
            int totalSelling = 0;
            int totalItems = 0;
            
            // Créer les OrderItems pour chaque produit du panier
            for (CartItem cartItem : cart.getCartItems()) {
                // Créer un OrderItem pour chaque produit
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setSize(cartItem.getSize());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setMrpPrice(cartItem.getMrpPrice());
                orderItem.setSellingPrice(cartItem.getSellingPrice());
                orderItem.setUserId(user.getId());
                
                // Ajouter l'OrderItem à la commande
                order.getOrderItems().add(orderItem);
                
                // Calculer les totaux
                totalMrp += (cartItem.getMrpPrice() != null ? cartItem.getMrpPrice() : 0) * cartItem.getQuantity();
                totalSelling += (cartItem.getSellingPrice() != null ? cartItem.getSellingPrice() : 0) * cartItem.getQuantity();
                totalItems += cartItem.getQuantity();
            }
            
            order.setTotalMrpPrice(totalMrp);
            order.setTotalSellingPrice(totalSelling);
            order.setTotalItem(totalItems);
            order.setDiscount((int)(totalMrp - totalSelling));
            
            // Définir le sellerId basé sur le premier produit (ou utiliser une logique plus complexe)
            if (!cart.getCartItems().isEmpty()) {
                Product firstProduct = cart.getCartItems().iterator().next().getProduct();
                if (firstProduct.getSeller() != null) {
                    order.setSellerId(firstProduct.getSeller().getId());
                } else {
                    order.setSellerId(1L); // Valeur par défaut si pas de vendeur
                }
            }
        } else {
            // Valeurs par défaut si pas de panier
            order.setTotalMrpPrice(0.0);
            order.setTotalSellingPrice(0);
            order.setTotalItem(0);
            order.setDiscount(0);
            order.setSellerId(1L); // Valeur par défaut
        }
        
        // Sauvegarder la commande (les OrderItems seront sauvegardés automatiquement grâce à CascadeType.PERSIST)
        Order savedOrder = orderRepository.save(order);
        orders.add(savedOrder);
        
        return orders;
    }

    @Override
    public List<Order> usersOrderHistory(Long userId) {
        return getOrdersByCustomer(userId);
    }

    @Override
    public Order findOrderById(Long orderId) {
        return getOrderById(orderId);
    }

    @Override
    public OrderItem getOrderItemById(Long orderItemId) {
        // Méthode simplifiée - créer un OrderItem vide
        OrderItem orderItem = new OrderItem();
        return orderItem;
    }

    @Override
    public Order cancelOrder(Long orderId, User user) {
        Order order = getOrderById(orderId);
        // Annulation simplifiée
        return orderRepository.save(order);
    }

    // Méthodes pour l'entrepôt
    @Override
    public Page<Order> getAllOrdersForWarehouse(String status, String search, Pageable pageable) {
        // Récupérer les commandes avec pagination
        Page<Order> ordersPage = orderRepository.findAll(pageable);
        
        // Charger les informations du livreur pour chaque commande
        for (Order order : ordersPage.getContent()) {
            loadDeliveryPersonInfo(order);
        }
        
        return ordersPage;
    }
    
    /**
     * Charger les informations du livreur pour une commande
     */
    private void loadDeliveryPersonInfo(Order order) {
        if (order.getDeliveryUserId() == null) {
            return;
        }
        
        Long userId = order.getDeliveryUserId();
        
        // Chercher d'abord dans DeliveryPerson
        Optional<DeliveryPerson> deliveryPerson = deliveryPersonRepository.findById(userId);
        if (deliveryPerson.isPresent()) {
            order.setDeliveryPerson(deliveryPerson.get());
        } else {
            // Si non trouvé dans DeliveryPerson, chercher dans User
            User user = userRepository.findById(userId).orElse(null);
            if (user != null && user.getRole() == USER_ROLE.ROLE_DELIVERY) {
                // Créer un objet temporaire pour l'affichage
                DeliveryPerson tempDeliveryPerson = new DeliveryPerson();
                tempDeliveryPerson.setId(user.getId());
                tempDeliveryPerson.setFullName(user.getFullName());
                tempDeliveryPerson.setEmail(user.getEmail());
                tempDeliveryPerson.setMobile(user.getMobile());
                order.setDeliveryPerson(tempDeliveryPerson);
            }
        }
    }

    @Override
    public Map<String, Object> getOrderStatsForWarehouse() {
        Map<String, Object> stats = new HashMap<>();
        
        // Compter les commandes par statut
        stats.put("pending", orderRepository.countByOrderStatus(OrderStatus.PENDING));
        stats.put("confirmed", orderRepository.countByOrderStatus(OrderStatus.CONFIRMED));
        stats.put("shipped", orderRepository.countByOrderStatus(OrderStatus.SHIPPED));
        stats.put("delivered", orderRepository.countByOrderStatus(OrderStatus.DELIVERED));
        stats.put("cancelled", orderRepository.countByOrderStatus(OrderStatus.CANCELLED));
        
        return stats;
    }

    @Override
    public Order markOrderAsReady(Long orderId) {
        Order order = getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("Commande non trouvée avec l'ID: " + orderId);
        }
        
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Seules les commandes en attente peuvent être marquées comme prêtes");
        }
        
        log.info("📦 Marquage de la commande {} comme prête - Réservation du stock", orderId);
        
        try {
            // 1. Réserver le stock pour tous les produits de la commande
            if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                for (OrderItem orderItem : order.getOrderItems()) {
                    Product product = orderItem.getProduct();
                    int quantity = orderItem.getQuantity();
                    
                    log.info("🔒 Réservation de {} unités du produit {} (ID: {})", 
                        quantity, product.getTitle(), product.getId());
                    
                    // Réserver le stock dans l'entrepôt
                    warehouseService.reserveStock(product, quantity);
                    
                    // Mettre à jour le stock réservé du produit
                    int currentReserved = product.getReservedQuantity();
                    product.setReservedQuantity(currentReserved + quantity);
                    
                    // IMPORTANT: Déduire le stock d'entrepôt lors de la confirmation
                    int currentWarehouse = product.getWarehouseQuantity();
                    if (currentWarehouse >= quantity) {
                        product.setWarehouseQuantity(currentWarehouse - quantity);
                        log.info("📦 Stock d'entrepôt débité: {} - {} unités = {} unités restantes", 
                            currentWarehouse, quantity, product.getWarehouseQuantity());
                    } else {
                        log.warn("⚠️ Stock insuffisant pour le produit {}: Stock actuel = {}, Demandé = {}", 
                            product.getTitle(), currentWarehouse, quantity);
                    }
                    
                    // Sauvegarder les modifications du produit
                    productRepository.save(product);
                }
            }
            
            // 2. Changer le statut de la commande
            order.setOrderStatus(OrderStatus.CONFIRMED);
            
            log.info("✅ Commande {} marquée comme prête avec réservation de stock", orderId);
            return orderRepository.save(order);
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la réservation du stock pour la commande {}: {}", orderId, e.getMessage());
            throw new RuntimeException("Erreur lors de la réservation du stock: " + e.getMessage());
        }
    }

    @Override
    public Order assignDeliveryPerson(Long orderId, Long deliveryPersonId, String deliveryNotes) {
        Order order = getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("Commande non trouvée avec l'ID: " + orderId);
        }
        
        // Stocker directement l'ID de l'utilisateur dans le champ deliveryUserId
        // Cela évite les problèmes de contrainte de clé étrangère
        order.setDeliveryUserId(deliveryPersonId);
        order.setDeliveryNotes(deliveryNotes);
        
        Order savedOrder = orderRepository.save(order);
        
        // Charger les informations du livreur pour l'affichage
        // Chercher d'abord dans DeliveryPerson
        Optional<DeliveryPerson> deliveryPerson = deliveryPersonRepository.findById(deliveryPersonId);
        if (deliveryPerson.isPresent()) {
            savedOrder.setDeliveryPerson(deliveryPerson.get());
        } else {
            // Si non trouvé dans DeliveryPerson, chercher dans User
            User user = userRepository.findById(deliveryPersonId).orElse(null);
            if (user != null && user.getRole() == USER_ROLE.ROLE_DELIVERY) {
                // Créer un objet temporaire pour l'affichage
                DeliveryPerson tempDeliveryPerson = new DeliveryPerson();
                tempDeliveryPerson.setId(user.getId());
                tempDeliveryPerson.setFullName(user.getFullName());
                tempDeliveryPerson.setEmail(user.getEmail());
                tempDeliveryPerson.setMobile(user.getMobile());
                savedOrder.setDeliveryPerson(tempDeliveryPerson);
            }
        }
        
        return savedOrder;
    }

    @Override
    public Order updateDeliveryStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("Commande non trouvée avec l'ID: " + orderId);
        }
        
        log.info("📦 Mise à jour du statut de livraison de la commande {}: {}", orderId, status);
        
        try {
            // Si la commande est livrée, libérer le stock réservé et débiter le stock réel
            if ("DELIVERED".equals(status) && order.getOrderStatus() == OrderStatus.CONFIRMED) {
                if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                    for (OrderItem orderItem : order.getOrderItems()) {
                        Product product = orderItem.getProduct();
                        int quantity = orderItem.getQuantity();
                        
                        log.info("📤 Livraison de {} unités du produit {} (ID: {})", 
                            quantity, product.getTitle(), product.getId());
                        
                        // Libérer le stock réservé
                        warehouseService.unreserveStock(product, quantity);
                        
                        // Débiter le stock réel de l'entrepôt
                        warehouseService.updateStock(product, quantity, com.camoutech.multivendor.domain.StockOperation.OUTGOING);
                        
                        // Mettre à jour les quantités du produit
                        Integer reservedQty = product.getReservedQuantity();
                        Integer warehouseQty = product.getWarehouseQuantity();
                        
                        int currentReserved = reservedQty != null ? reservedQty : 0;
                        int currentWarehouse = warehouseQty != null ? warehouseQty : 0;
                        
                        product.setReservedQuantity(Math.max(0, currentReserved - quantity));
                        product.setWarehouseQuantity(Math.max(0, currentWarehouse - quantity));
                    }
                }
                
                // Changer le statut de la commande
                order.setOrderStatus(OrderStatus.DELIVERED);
            }
            
            log.info("✅ Statut de livraison de la commande {} mis à jour", orderId);
            return orderRepository.save(order);
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la mise à jour du statut de livraison de la commande {}: {}", orderId, e.getMessage());
            throw new RuntimeException("Erreur lors de la mise à jour du statut: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteOrder(Long orderId, Long userId) {
        try {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                return false;
            }
            
            // Vérifier que la commande appartient à l'utilisateur
            if (!order.getUser().getId().equals(userId)) {
                return false;
            }
            
            // 1. D'abord, supprimer les PaymentOrder qui référencent cette commande
            List<PaymentOrder> paymentOrders = paymentOrderRepository.findAll().stream()
                .filter(po -> po.getOrders().contains(order))
                .toList();
            
            for (PaymentOrder paymentOrder : paymentOrders) {
                paymentOrder.getOrders().remove(order);
                if (paymentOrder.getOrders().isEmpty()) {
                    paymentOrderRepository.delete(paymentOrder);
                } else {
                    paymentOrderRepository.save(paymentOrder);
                }
            }
            
            // 2. Ensuite, supprimer la commande (les OrderItems seront supprimés automatiquement grâce à orphanRemoval)
            orderRepository.delete(order);
            
            log.info("Commande {} supprimée avec succès", orderId);
            return true;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de la commande {}: {}", orderId, e.getMessage());
            return false;
        }
    }

    @Override
    public int deleteAllOrdersByUser(Long userId) {
        try {
            List<Order> userOrders = orderRepository.findByUserId(userId);
            int deletedCount = userOrders.size();
            
            // 1. D'abord, supprimer les PaymentOrder qui référencent ces commandes
            List<PaymentOrder> allPaymentOrders = paymentOrderRepository.findAll();
            for (PaymentOrder paymentOrder : allPaymentOrders) {
                boolean hasUserOrders = paymentOrder.getOrders().stream()
                    .anyMatch(order -> order.getUser().getId().equals(userId));
                
                if (hasUserOrders) {
                    // Retirer les commandes de cet utilisateur
                    paymentOrder.getOrders().removeIf(order -> order.getUser().getId().equals(userId));
                    
                    if (paymentOrder.getOrders().isEmpty()) {
                        paymentOrderRepository.delete(paymentOrder);
                    } else {
                        paymentOrderRepository.save(paymentOrder);
                    }
                }
            }
            
            // 2. Ensuite, supprimer toutes les commandes de l'utilisateur
            orderRepository.deleteAll(userOrders);
            
            log.info("Suppression de {} commande(s) pour l'utilisateur {}", deletedCount, userId);
            return deletedCount;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression des commandes pour l'utilisateur {}: {}", userId, e.getMessage());
            return 0;
        }
    }

    @Override
    public int deleteEmptyOrders() {
        try {
            // Récupérer toutes les commandes
            List<Order> allOrders = orderRepository.findAll();
            List<Order> emptyOrders = new ArrayList<>();
            
            // Identifier les commandes vides (sans orderItems)
            for (Order order : allOrders) {
                if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
                    emptyOrders.add(order);
                }
            }
            
            int deletedCount = emptyOrders.size();
            
            if (deletedCount > 0) {
                // Supprimer les PaymentOrder qui référencent ces commandes vides
                List<PaymentOrder> allPaymentOrders = paymentOrderRepository.findAll();
                for (PaymentOrder paymentOrder : allPaymentOrders) {
                    boolean hasEmptyOrders = paymentOrder.getOrders().stream()
                        .anyMatch(order -> order.getOrderItems() == null || order.getOrderItems().isEmpty());
                    
                    if (hasEmptyOrders) {
                        // Retirer les commandes vides
                        paymentOrder.getOrders().removeIf(order -> 
                            order.getOrderItems() == null || order.getOrderItems().isEmpty());
                        
                        if (paymentOrder.getOrders().isEmpty()) {
                            paymentOrderRepository.delete(paymentOrder);
                        } else {
                            paymentOrderRepository.save(paymentOrder);
                        }
                    }
                }
                
                // Supprimer les commandes vides
                orderRepository.deleteAll(emptyOrders);
                
                log.info("🧹 Suppression de {} commande(s) vide(s)", deletedCount);
            }
            
            return deletedCount;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression des commandes vides: {}", e.getMessage());
            return 0;
        }
    }
}