package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.model.Order;
import com.camoutech.multivendor.model.OrderItem;
import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.DeliveryPerson;
import com.camoutech.multivendor.model.User;
import com.camoutech.multivendor.model.Address;
import com.camoutech.multivendor.model.Cart;
import com.camoutech.multivendor.domain.OrderStatus;
import com.camoutech.multivendor.service.OrderService;
import com.camoutech.multivendor.service.DeliveryPersonService;
import com.camoutech.multivendor.service.UserService;
import com.camoutech.multivendor.service.CartService;
import com.camoutech.multivendor.repository.UserRepository;
import com.camoutech.multivendor.repository.DeliveryPersonRepository;
import com.camoutech.multivendor.domain.USER_ROLE;
import com.camoutech.multivendor.domain.DeliveryStatus;
import com.camoutech.multivendor.dto.DeliveryPersonDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la gestion des commandes par l'entrepôt
 */
@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class WarehouseController {

    private final OrderService orderService;
    private final DeliveryPersonService deliveryPersonService;
    private final UserService userService;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final DeliveryPersonRepository deliveryPersonRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Créer une commande directement dans le workflow de l'entrepôt
     * Cette commande sera automatiquement mise en statut "PENDING" (à préparer)
     */
    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> createWarehouseOrder(
            @RequestBody CreateWarehouseOrderRequest request) {
        
        log.info("📦 Création d'une nouvelle commande pour l'entrepôt - Client: {}", request.getUserId());
        
        try {
            log.info("🔍 Recherche de l'utilisateur avec l'ID: {}", request.getUserId());
            
            // Récupérer l'utilisateur
            User user = userService.findUserById(request.getUserId());
            if (user == null) {
                log.error("❌ Utilisateur non trouvé avec l'ID: {}", request.getUserId());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Utilisateur non trouvé");
                errorResponse.put("message", "L'utilisateur avec l'ID " + request.getUserId() + " n'existe pas");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            log.info("✅ Utilisateur trouvé: {} ({})", user.getFullName(), user.getEmail());

            // Récupérer le panier de l'utilisateur
            log.info("🛒 Recherche du panier pour l'utilisateur: {}", user.getId());
            Cart cart = cartService.findUserCart(user);
            if (cart == null) {
                log.error("❌ Panier non trouvé pour l'utilisateur: {}", user.getId());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Panier non trouvé");
                errorResponse.put("message", "Aucun panier trouvé pour cet utilisateur");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
                log.error("❌ Panier vide pour l'utilisateur: {}", user.getId());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Panier vide");
                errorResponse.put("message", "Le panier de l'utilisateur est vide");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            log.info("✅ Panier trouvé avec {} articles", cart.getCartItems().size());

            // Créer la commande avec le statut PENDING (à préparer)
            Set<Order> orders = orderService.createOrder(user, request.getShippingAddress(), cart);
            
            // S'assurer que toutes les commandes sont en statut PENDING
            for (Order order : orders) {
                order.setOrderStatus(OrderStatus.PENDING);
                orderService.updateOrder(order.getId(), order);
            }

            // Vider le panier après la création de la commande pour éviter les doublons
            log.info("🧹 Vidage du panier après création de la commande");
            cartService.clearCart(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Commande créée avec succès dans le workflow de l'entrepôt");
            response.put("orders", orders);
            response.put("status", "PENDING");
            response.put("workflow", "WAREHOUSE_PREPARATION");

            log.info("✅ Commande créée avec succès pour l'entrepôt - {} commande(s) créée(s)", orders.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la création de la commande pour l'entrepôt: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors de la création de la commande");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Récupérer toutes les commandes pour l'entrepôt
     */
    @GetMapping("/orders")
    public ResponseEntity<Page<Order>> getAllOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        
        log.info("📋 Récupération des commandes pour l'entrepôt - Status: {}, Search: {}", status, search);
        
        try {
            Page<Order> orders = orderService.getAllOrdersForWarehouse(status, search, pageable);
            log.info("✅ {} commandes récupérées", orders.getTotalElements());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des commandes: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer les statistiques des commandes
     */
    @GetMapping("/orders/stats")
    public ResponseEntity<Map<String, Object>> getOrderStats() {
        log.info("📊 Récupération des statistiques des commandes");
        
        try {
            Map<String, Object> stats = orderService.getOrderStatsForWarehouse();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des statistiques: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer les statistiques de ventes effectuées
     */
    @GetMapping("/sales-stats")
    public ResponseEntity<Map<String, Object>> getSalesStats() {
        log.info("📊 Récupération des statistiques de ventes");
        
        try {
            // Récupérer toutes les commandes confirmées ou livrées via le service
            List<Order> confirmedOrders = orderService.getOrdersByStatus(OrderStatus.CONFIRMED);
            List<Order> deliveredOrders = orderService.getOrdersByStatus(OrderStatus.DELIVERED);
            List<Order> shippedOrders = orderService.getOrdersByStatus(OrderStatus.SHIPPED);
            
            // Combiner toutes les commandes
            List<Order> allCompletedOrders = new ArrayList<>();
            allCompletedOrders.addAll(confirmedOrders);
            allCompletedOrders.addAll(deliveredOrders);
            allCompletedOrders.addAll(shippedOrders);
            
            double totalSalesAmount = 0;
            int totalQuantitySold = 0;
            
            for (Order order : allCompletedOrders) {
                if (order.getOrderItems() != null) {
                    for (OrderItem item : order.getOrderItems()) {
                        Product product = item.getProduct();
                        int quantity = item.getQuantity();
                        double price = product.getSellingPrice();
                        
                        totalSalesAmount += quantity * price;
                        totalQuantitySold += quantity;
                    }
                }
            }
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalSalesAmount", totalSalesAmount);
            stats.put("totalQuantitySold", totalQuantitySold);
            stats.put("totalOrders", allCompletedOrders.size());
            
            log.info("💰 Montant total des ventes: {}", totalSalesAmount);
            log.info("📦 Quantité totale vendue: {}", totalQuantitySold);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des statistiques de ventes: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Marquer une commande comme prête (statut PENDING -> CONFIRMED)
     */
    @PutMapping("/orders/{orderId}/ready")
    public ResponseEntity<Map<String, Object>> markOrderAsReady(@PathVariable Long orderId) {
        log.info("✅ Marquage de la commande {} comme prête", orderId);
        
        try {
            Order order = orderService.markOrderAsReady(orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Commande marquée comme prête");
            response.put("orderId", order.getOrderId());
            response.put("status", order.getOrderStatus());
            
            log.info("✅ Commande {} marquée comme prête", order.getOrderId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Erreur lors du marquage de la commande {}: {}", orderId, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors du marquage de la commande");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Affecter un livreur à une commande
     */
    @PutMapping("/orders/{orderId}/assign-delivery")
    public ResponseEntity<Map<String, Object>> assignDeliveryPerson(
            @PathVariable Long orderId,
            @RequestBody AssignDeliveryRequest request) {
        
        log.info("🚚 Affectation du livreur {} à la commande {}", request.getDeliveryPersonId(), orderId);
        
        try {
            // Utiliser directement l'ID de l'utilisateur (qui peut être dans users ou delivery_persons)
            Long deliveryPersonId = request.getDeliveryPersonId();
            
            log.info("✅ Utilisation de l'ID {} pour l'affectation (directement depuis users)", deliveryPersonId);
            
            Order order = orderService.assignDeliveryPerson(orderId, deliveryPersonId, request.getDeliveryNotes());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Livreur affecté avec succès");
            response.put("orderId", order.getOrderId());
            response.put("deliveryUserId", order.getDeliveryUserId());
            response.put("deliveryStatus", order.getDeliveryStatus());
            
            log.info("✅ Livreur affecté à la commande {}", order.getOrderId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'affectation du livreur à la commande {}: {}", orderId, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors de l'affectation du livreur");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Récupérer tous les livreurs disponibles
     * Inclut les livreurs de la table delivery_persons ET les utilisateurs avec le rôle ROLE_DELIVERY
     */
    @GetMapping("/delivery-persons")
    public ResponseEntity<List<DeliveryPersonDTO>> getAllDeliveryPersons() {
        log.info("🚚 Récupération des livreurs disponibles");
        
        try {
            // Récupérer les livreurs de la table delivery_persons
            List<DeliveryPerson> deliveryPersons = deliveryPersonService.getAllActiveDeliveryPersons();
            log.info("✅ {} livreurs récupérés de delivery_persons", deliveryPersons.size());
            
            // Convertir en DTO
            List<DeliveryPersonDTO> result = deliveryPersons.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // Récupérer les utilisateurs avec le rôle ROLE_DELIVERY
            List<User> usersWithDeliveryRole = userRepository.findByRole(USER_ROLE.ROLE_DELIVERY);
            log.info("✅ {} utilisateurs avec rôle ROLE_DELIVERY récupérés de users", usersWithDeliveryRole.size());
            
            // Convertir les utilisateurs en DTO et les ajouter s'ils ne sont pas déjà présents
            for (User user : usersWithDeliveryRole) {
                // Vérifier si l'utilisateur n'est pas déjà dans la liste (éviter les doublons)
                boolean alreadyExists = result.stream()
                        .anyMatch(dto -> dto.getEmail().equals(user.getEmail()));
                
                if (!alreadyExists) {
                    result.add(convertUserToDTO(user));
                }
            }
            
            log.info("✅ Total de {} livreurs disponibles", result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des livreurs: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Convertir DeliveryPerson en DTO
     */
    private DeliveryPersonDTO convertToDTO(DeliveryPerson deliveryPerson) {
        DeliveryPersonDTO dto = new DeliveryPersonDTO();
        dto.setId(deliveryPerson.getId());
        dto.setFullName(deliveryPerson.getFullName());
        dto.setEmail(deliveryPerson.getEmail());
        dto.setMobile(deliveryPerson.getMobile());
        dto.setLicenseNumber(deliveryPerson.getLicenseNumber());
        dto.setVehicleType(deliveryPerson.getVehicleType());
        return dto;
    }
    
    /**
     * Convertir User en DeliveryPersonDTO
     */
    private DeliveryPersonDTO convertUserToDTO(User user) {
        DeliveryPersonDTO dto = new DeliveryPersonDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setMobile(user.getMobile());
        dto.setLicenseNumber(null); // Pas disponible dans User
        dto.setVehicleType(null); // Pas disponible dans User
        return dto;
    }

    /**
     * Récupérer les détails d'une commande
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrderDetails(@PathVariable Long orderId) {
        log.info("👁️ Récupération des détails de la commande {}", orderId);
        
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }
            
            log.info("✅ Détails de la commande {} récupérés", order.getOrderId());
            return ResponseEntity.ok(order);
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des détails de la commande {}: {}", orderId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Changer le statut de livraison d'une commande
     */
    @PutMapping("/orders/{orderId}/delivery-status")
    public ResponseEntity<Map<String, Object>> updateDeliveryStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateDeliveryStatusRequest request) {
        
        log.info("📦 Mise à jour du statut de livraison de la commande {}: {}", orderId, request.getStatus());
        
        try {
            Order order = orderService.updateDeliveryStatus(orderId, request.getStatus());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Statut de livraison mis à jour");
            response.put("orderId", order.getOrderId());
            response.put("deliveryStatus", order.getDeliveryStatus());
            
            log.info("✅ Statut de livraison de la commande {} mis à jour", order.getOrderId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la mise à jour du statut de livraison de la commande {}: {}", orderId, e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors de la mise à jour du statut");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    // Classes de requête internes
    public static class AssignDeliveryRequest {
        private Long deliveryPersonId;
        private String deliveryNotes;

        public Long getDeliveryPersonId() { return deliveryPersonId; }
        public void setDeliveryPersonId(Long deliveryPersonId) { this.deliveryPersonId = deliveryPersonId; }

        public String getDeliveryNotes() { return deliveryNotes; }
        public void setDeliveryNotes(String deliveryNotes) { this.deliveryNotes = deliveryNotes; }
    }

    public static class UpdateDeliveryStatusRequest {
        private String status;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class CreateWarehouseOrderRequest {
        private Long userId;
        private Address shippingAddress;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Address getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(Address shippingAddress) { this.shippingAddress = shippingAddress; }
    }

    /**
     * Nettoyer les commandes vides (sans orderItems)
     */
    @DeleteMapping("/orders/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupEmptyOrders() {
        log.info("🧹 Nettoyage des commandes vides");
        
        try {
            int deletedCount = orderService.deleteEmptyOrders();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", deletedCount + " commande(s) vide(s) supprimée(s)");
            response.put("deletedCount", deletedCount);
            response.put("success", true);
            
            log.info("✅ Nettoyage terminé: {} commande(s) supprimée(s)", deletedCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Erreur lors du nettoyage des commandes vides: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors du nettoyage");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}