/**
 * Created by camoutech
 * Date :19/10/2024
 * Time :18:34
 * Project Name :multivendor
 */

package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.domain.PaymentMethod;
import com.camoutech.multivendor.model.*;
import com.camoutech.multivendor.repository.PaymentOrderRepository;
import com.camoutech.multivendor.response.PaymentLinkResponse;
import com.camoutech.multivendor.service.*;
import com.razorpay.PaymentLink;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;
    private final SellerService sellerService;
    private final SellerReportService sellerReportService;
    private final PaymentService paymentService;
    private final PaymentOrderRepository paymentOrderRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrderHandler(
            @RequestBody Address sippingAddress,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        Cart cart = cartService.findUserCart(user);
        
        // Utiliser le nouveau workflow warehouse (sans paiement)
        Set<Order> orders = orderService.createOrder(user, sippingAddress, cart);
        
        // Retourner une réponse simple sans lien de paiement
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Commande créée avec succès");
        response.put("orders", orders);
        response.put("status", "PENDING");
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Order>> usersOrderHistoryHandler(
            @RequestHeader("Authorization")
            String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        List<Order> orders = orderService.getOrdersByCustomer(user.getId());
        return new ResponseEntity<>(orders, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable Long orderId,
            @RequestHeader("Authorization")
            String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        Order order = orderService.getOrderById(orderId);
        
        // Vérifier que la commande appartient à l'utilisateur connecté
        if (!order.getUser().getId().equals(user.getId())) {
            throw new Exception("Vous n'êtes pas autorisé à accéder à cette commande");
        }
        
        return new ResponseEntity<>(order, HttpStatus.ACCEPTED);
    }

    @GetMapping("/item/{orderItemId}")
    public ResponseEntity<OrderItem> getOrderItemById(
            @PathVariable Long orderItemId,
            @RequestHeader("Authorization")
            String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        OrderItem orderItem = orderService.getOrderItemById(orderItemId);
        
        // Vérifier que la commande de cet article appartient à l'utilisateur connecté
        if (orderItem.getOrder() != null && !orderItem.getOrder().getUser().getId().equals(user.getId())) {
            throw new Exception("Vous n'êtes pas autorisé à accéder à cet article de commande");
        }
        
        return new ResponseEntity<>(orderItem, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Order order = orderService.getOrderById(orderId);
        
        // Vérifier que la commande appartient à l'utilisateur connecté
        if (!order.getUser().getId().equals(user.getId())) {
            throw new Exception("Vous n'êtes pas autorisé à annuler cette commande");
        }
        
        Order canceledOrder = orderService.cancelOrder(orderId, user);

        Seller seller = sellerService.getSellerById(order.getSellerId());
        SellerReport report = sellerReportService.getSellerReport(seller);

        report.setCanceledOrders(report.getCanceledOrders()+1);
        report.setTotalRefunds(report.getTotalRefunds()+order.getTotalSellingPrice());
        sellerReportService.updateSellerReport(report);

        return ResponseEntity.ok(canceledOrder);
    }

    // Supprimer une commande individuelle
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> deleteOrder(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        
        // Vérifier que la commande appartient à l'utilisateur avant de la supprimer
        Order order = orderService.getOrderById(orderId);
        if (!order.getUser().getId().equals(user.getId())) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Vous n'êtes pas autorisé à supprimer cette commande");
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        // Vérifier que la commande est en statut PENDING (seules les commandes en attente peuvent être supprimées)
        if (order.getOrderStatus() != com.camoutech.multivendor.domain.OrderStatus.PENDING) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Impossible de supprimer cette commande. Seules les commandes en attente peuvent être supprimées.");
            response.put("success", false);
            response.put("status", order.getOrderStatus().toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        boolean deleted = orderService.deleteOrder(orderId, user.getId());
        
        Map<String, Object> response = new HashMap<>();
        if (deleted) {
            response.put("message", "Commande supprimée avec succès");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Commande non trouvée");
            response.put("success", false);
            return ResponseEntity.notFound().build();
        }
    }

    // Supprimer toutes les commandes d'un client
    @DeleteMapping("/user/all")
    public ResponseEntity<Map<String, Object>> deleteAllUserOrders(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        int deletedCount = orderService.deleteAllOrdersByUser(user.getId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", deletedCount + " commande(s) supprimée(s) avec succès");
        response.put("deletedCount", deletedCount);
        response.put("success", true);
        
        return ResponseEntity.ok(response);
    }
}
