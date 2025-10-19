/**
 * Created by camoutech
 * Date :13/10/2024
 * Time :03:03
 * Project Name :multivendor
 */

package com.camoutech.multivendor.service.impl;

import com.camoutech.multivendor.domain.StockOperation;
import com.camoutech.multivendor.domain.SupplyOrderStatus;
import com.camoutech.multivendor.domain.TaskStatus;
import com.camoutech.multivendor.model.*;
import com.camoutech.multivendor.repository.SupplyOrderRepository;
import com.camoutech.multivendor.request.CreateSupplyOrderRequest;
import com.camoutech.multivendor.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupermarketWorkflowServiceImpl implements SupermarketWorkflowService {

    private final SupplyOrderService supplyOrderService;
    private final WarehouseService warehouseService;
    private final DeliveryService deliveryService;
    private final CommissionService commissionService;
    private final SupplyOrderRepository supplyOrderRepository;

    @Override
    @Transactional
    public SupplyOrder processSupplyOrder(CreateSupplyOrderRequest request) throws Exception {
        // 1. Créer la commande d'approvisionnement
        SupplyOrder supplyOrder = supplyOrderService.createSupplyOrder(request);
        
        // 2. Assigner un livreur pour la récupération
        DeliveryTask pickupTask = deliveryService.assignPickupTask(supplyOrder);
        
        // 3. Mettre à jour le statut
        supplyOrder.setStatus(SupplyOrderStatus.CONFIRMED);
        supplyOrder.setDeliveryPerson(pickupTask.getDeliveryPerson());
        
        return supplyOrderRepository.save(supplyOrder);
    }

    @Override
    @Transactional
    public void completeSupplyOrder(Long supplyOrderId) throws Exception {
        SupplyOrder supplyOrder = supplyOrderService.getSupplyOrderById(supplyOrderId);
        
        // 1. Mettre à jour le stock de l'entrepôt
        for (var item : supplyOrder.getSupplyOrderItems()) {
            warehouseService.addProductToWarehouse(
                supplyOrder.getWarehouse(),
                item.getProduct(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getProduct().getSellingPrice()
            );
        }
        
        // 2. Marquer la commande comme livrée
        supplyOrder.setStatus(SupplyOrderStatus.DELIVERED_TO_WAREHOUSE);
        supplyOrderRepository.save(supplyOrder);
    }

    @Override
    @Transactional
    public void processCustomerOrder(Order customerOrder) throws Exception {
        // 1. Réserver le stock pour la commande
        reserveStockForOrder(customerOrder);
        
        // 2. Assigner un livreur pour la livraison
        DeliveryTask deliveryTask = assignDeliveryTask(customerOrder);
        
        // 3. Mettre à jour le statut de la commande
        customerOrder.setDeliveryPerson(deliveryTask.getDeliveryPerson());
        // Le statut sera mis à jour par le service Order
    }

    @Override
    @Transactional
    public void completeCustomerDelivery(Long orderId) throws Exception {
        // 1. Débiter le stock réservé
        // 2. Calculer les commissions
        // 3. Mettre à jour les paiements fournisseurs
        // Cette logique sera implémentée selon les besoins spécifiques
    }

    @Override
    public void updateWarehouseStock(Product product, int quantity, String operation) throws Exception {
        StockOperation stockOp = StockOperation.valueOf(operation.toUpperCase());
        warehouseService.updateStock(product, quantity, stockOp);
    }

    @Override
    public void reserveStockForOrder(Order order) throws Exception {
        for (var orderItem : order.getOrderItems()) {
            warehouseService.reserveStock(orderItem.getProduct(), orderItem.getQuantity());
        }
    }

    @Override
    public void releaseReservedStock(Order order) throws Exception {
        for (var orderItem : order.getOrderItems()) {
            warehouseService.unreserveStock(orderItem.getProduct(), orderItem.getQuantity());
        }
    }

    @Override
    public void calculateAndProcessCommissions(Long supplierId, LocalDateTime from, LocalDateTime to) throws Exception {
        // 1. Calculer les ventes du fournisseur
        List<OrderItem> soldItems = commissionService.getSoldItemsBySupplier(supplierId, from, to);
        
        // 2. Calculer le paiement dû
        double payment = commissionService.calculateSupplierPayment(supplierId, from, to);
        
        // 3. Traiter le paiement (à implémenter selon le système de paiement)
        // Logique de traitement des commissions basée sur les ventes
        if (!soldItems.isEmpty() && payment > 0) {
            // TODO: Implémenter le traitement du paiement
            System.out.println("Processing commission for supplier " + supplierId + ": " + payment);
        }
    }

    @Override
    public double getSupplierPendingPayment(Long supplierId) throws Exception {
        LocalDateTime from = LocalDateTime.now().minusMonths(1);
        LocalDateTime to = LocalDateTime.now();
        return commissionService.calculateSupplierPayment(supplierId, from, to);
    }

    @Override
    public DeliveryTask assignDeliveryTask(Order customerOrder) throws Exception {
        return deliveryService.assignDeliveryTask(customerOrder);
    }

    @Override
    public DeliveryTask assignPickupTask(SupplyOrder supplyOrder) throws Exception {
        return deliveryService.assignPickupTask(supplyOrder);
    }

    @Override
    public void completeDeliveryTask(Long taskId, String notes) throws Exception {
        deliveryService.completeTask(taskId, notes);
    }
}
