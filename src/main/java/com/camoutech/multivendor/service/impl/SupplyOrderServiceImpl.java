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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        supplyOrder.setDeliveryDate(supplyOrder.getOrderDate().plusDays(2)); // Date de livraison = Date de commande + 2 jours
        supplyOrder.setNotes(request.getNotes());

        SupplyOrder savedSupplyOrder = supplyOrderRepository.save(supplyOrder);

        double totalAmount = 0;
        for (var itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new Exception("Product not found"));

            // Logique de priorité de quantité de stock
            int quantityToOrder = 0;
            if (product.getAdminRequestedQuantity() > 0) {
                quantityToOrder = product.getAdminRequestedQuantity();
            } else if (product.getSupplierAvailableQuantity() > 0) {
                quantityToOrder = product.getSupplierAvailableQuantity();
            } else {
                throw new Exception("Quantité de stock non disponible pour le produit: " + product.getTitle());
            }

            SupplyOrderItem item = new SupplyOrderItem();
            item.setSupplyOrder(savedSupplyOrder);
            item.setProduct(product);
            item.setQuantity(quantityToOrder);
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setTotalPrice(quantityToOrder * itemRequest.getUnitPrice());

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
    public SupplyOrder updateShipmentStatus(Long supplyOrderId, com.camoutech.multivendor.model.Product.ShipmentStatus status) throws Exception {
        SupplyOrder supplyOrder = getSupplyOrderById(supplyOrderId);
        
        // Mettre à jour le statut d'envoi pour tous les produits de cette commande
        for (SupplyOrderItem item : supplyOrder.getSupplyOrderItems()) {
            Product product = item.getProduct();
            product.setShipmentStatus(status);
            productRepository.save(product);
        }
        
        return supplyOrderRepository.save(supplyOrder);
    }

    @Override
    public List<SupplyOrder> getSupplyOrdersByDateRange(LocalDateTime from, LocalDateTime to) {
        return supplyOrderRepository.findByOrderDateBetween(from, to);
    }

    @Override
    public SupplyOrder confirmDeliveryToWarehouse(Long supplyOrderId, Long supplierId) throws Exception {
        SupplyOrder supplyOrder = supplyOrderRepository.findById(supplyOrderId)
                .orElseThrow(() -> new Exception("Supply order not found with id: " + supplyOrderId));

        if (!supplyOrder.getSupplier().getId().equals(supplierId)) {
            throw new Exception("Supply order does not belong to the provided supplier.");
        }

        supplyOrder.setStatus(SupplyOrderStatus.DELIVERED_TO_WAREHOUSE);
        supplyOrder.setDeliveryDate(LocalDateTime.now());
        return supplyOrderRepository.save(supplyOrder);
    }

    @Override
    public List<SupplyOrder> getSupplyOrdersByCurrentSupplierAndProductStatus(Product.ProductStatus productStatus) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            System.out.println("🔍 Récupération des commandes pour le fournisseur: " + email + " avec statut de produit: " + productStatus);

            Supplier supplier = supplierRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé pour l'email: " + email));
            System.out.println("✅ Fournisseur trouvé: " + supplier.getSupplierName() + " (ID: " + supplier.getId() + ")");

            List<SupplyOrder> orders = supplyOrderRepository.findBySupplierAndProductStatus(supplier, productStatus);
            System.out.println("📦 Nombre de commandes trouvées avec statut de produit " + productStatus + ": " + orders.size());
            return orders;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des commandes du fournisseur par statut de produit: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public SupplyOrder completeSupplyOrderDelivery(Long supplyOrderId, Long warehouseId) throws Exception {
        SupplyOrder supplyOrder = supplyOrderRepository.findById(supplyOrderId)
                .orElseThrow(() -> new Exception("Supply order not found with id: " + supplyOrderId));

        if (supplyOrder.getStatus() != SupplyOrderStatus.DELIVERED_TO_WAREHOUSE) {
            throw new Exception("Supply order is not in DELIVERED_TO_WAREHOUSE status.");
        }

        if (!supplyOrder.getWarehouse().getId().equals(warehouseId)) {
            throw new Exception("Supply order does not belong to the provided warehouse.");
        }

        supplyOrder.setStatus(SupplyOrderStatus.COMPLETED);
        supplyOrderRepository.save(supplyOrder);

        // Mettre à jour le stock de chaque produit dans l'entrepôt
        for (SupplyOrderItem item : supplyOrder.getSupplyOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }
        return supplyOrder;
    }

    @Override
    public List<Product> getSupplyOrdersByCurrentSupplier() {
        try {
            // Récupérer l'utilisateur connecté
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            System.out.println("🔍 Recherche des commandes pour le fournisseur: " + email);

            // Trouver le fournisseur par email
            Supplier supplier = supplierRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé pour l'email: " + email));

            //System.out.println("✅ Fournisseur trouvé: " + supplier.getBusinessName() + " (ID: " + supplier.getId() + ")");

            // Récupérer les commandes du fournisseur
            List<Product> allOrders = productRepository.findApprovedAndPendingProductsBySupplier(supplier.getId());
            System.out.println("📦 Nombre total de commandes trouvées: " + allOrders.size());



            return allOrders;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des commandes du fournisseur: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // Retourner une liste vide en cas d'erreur
        }
    }





    @Override
    public List<Product> getSupplyOrdersByCurrentSupplierExpedier() {
        try {
            // Récupérer l'utilisateur connecté
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            System.out.println("🔍 Recherche des commandes pour le fournisseur: " + email);

            // Trouver le fournisseur par email
            Supplier supplier = supplierRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé pour l'email: " + email));

            //System.out.println("✅ Fournisseur trouvé: " + supplier.getBusinessName() + " (ID: " + supplier.getId() + ")");

            // Récupérer les commandes du fournisseur
            List<Product> allOrders = productRepository.findApprovedAndPendingProductsBySupplierExpedier(supplier.getId());
            System.out.println("📦 Nombre total de commandes trouvées: " + allOrders.size());



            return allOrders;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des commandes du fournisseur: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // Retourner une liste vide en cas d'erreur
        }
    }























}
