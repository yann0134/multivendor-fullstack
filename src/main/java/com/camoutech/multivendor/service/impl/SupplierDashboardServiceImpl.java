package com.camoutech.multivendor.service.impl;

import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.Supplier;
import com.camoutech.multivendor.model.SupplierDashboardStats;
import com.camoutech.multivendor.repository.ProductRepository;
import com.camoutech.multivendor.repository.SupplierRepository;
import com.camoutech.multivendor.service.SupplierDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implémentation du service pour les statistiques du tableau de bord du fournisseur
 */
@Service
@RequiredArgsConstructor
public class SupplierDashboardServiceImpl implements SupplierDashboardService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    @Override
    public SupplierDashboardStats getSupplierDashboardStats(Long supplierId) {
        System.out.println("📊 Calcul des statistiques du tableau de bord pour le fournisseur ID: " + supplierId);
        
        // Récupérer tous les produits du fournisseur
        List<Product> allProducts = productRepository.findBySupplierId(supplierId);
        
        // Statistiques générales
        long totalProducts = allProducts.size();
        long approvedProducts = allProducts.stream()
            .filter(p -> p.getStatus() == Product.ProductStatus.APPROVED)
            .count();
        long pendingProducts = allProducts.stream()
            .filter(p -> p.getStatus() == Product.ProductStatus.PENDING_APPROVAL)
            .count();
        long rejectedProducts = allProducts.stream()
            .filter(p -> p.getStatus() == Product.ProductStatus.REJECTED)
            .count();
        long shippedProducts = allProducts.stream()
            .filter(p -> p.getShipmentStatus() == Product.ShipmentStatus.SHIPPED)
            .count();
        long deliveredProducts = allProducts.stream()
            .filter(p -> p.getShipmentStatus() == Product.ShipmentStatus.DELIVERED)
            .count();
        
        // Calcul des revenus (basés sur les prix de vente)
        BigDecimal totalRevenue = getTotalRevenue(supplierId);
        BigDecimal receivedAmount = getReceivedAmount(supplierId);
        BigDecimal pendingAmount = totalRevenue.subtract(receivedAmount);
        BigDecimal thisMonthRevenue = getThisMonthRevenue(supplierId);
        BigDecimal lastMonthRevenue = getLastMonthRevenue(supplierId);
        
        // Statistiques de performance
        double averageProcessingTime = calculateAverageProcessingTime(allProducts);
        long totalOrders = totalProducts;
        long completedOrders = deliveredProducts;
        double completionRate = totalOrders > 0 ? (double) completedOrders / totalOrders * 100 : 0;
        
        // Statistiques temporelles
        LocalDateTime lastActivity = allProducts.stream()
            .map(Product::getUpdatedAt)
            .filter(date -> date != null)
            .max(LocalDateTime::compareTo)
            .orElse(null);
        
        Supplier supplier = supplierRepository.findById(supplierId).orElse(null);
        LocalDateTime accountCreated = null; // Le modèle Supplier n'a pas de champ createdAt
        
        // Top produit
        String topSellingProduct = getTopSellingProduct(supplierId);
        long topSellingQuantity = getTopSellingQuantity(supplierId, topSellingProduct);
        
        SupplierDashboardStats stats = new SupplierDashboardStats();
        stats.setTotalProducts(totalProducts);
        stats.setApprovedProducts(approvedProducts);
        stats.setPendingProducts(pendingProducts);
        stats.setRejectedProducts(rejectedProducts);
        stats.setShippedProducts(shippedProducts);
        stats.setDeliveredProducts(deliveredProducts);
        stats.setTotalRevenue(totalRevenue);
        stats.setReceivedAmount(receivedAmount);
        stats.setPendingAmount(pendingAmount);
        stats.setThisMonthRevenue(thisMonthRevenue);
        stats.setLastMonthRevenue(lastMonthRevenue);
        stats.setAverageProcessingTime(averageProcessingTime);
        stats.setTotalOrders(totalOrders);
        stats.setCompletedOrders(completedOrders);
        stats.setCompletionRate(completionRate);
        stats.setLastActivity(lastActivity);
        stats.setAccountCreated(accountCreated);
        stats.setTopSellingProduct(topSellingProduct);
        stats.setTopSellingQuantity(topSellingQuantity);
        
        System.out.println("📊 Statistiques calculées:");
        System.out.println("  - Total produits: " + totalProducts);
        System.out.println("  - Produits approuvés: " + approvedProducts);
        System.out.println("  - Revenus totaux: " + totalRevenue);
        System.out.println("  - Montant reçu: " + receivedAmount);
        System.out.println("  - Montant en attente: " + pendingAmount);
        
        return stats;
    }

    @Override
    public BigDecimal getTotalRevenue(Long supplierId) {
        List<Product> products = productRepository.findBySupplierId(supplierId);
        
        // Calculer les revenus basés sur les prix de vente (sellingPrice)
        BigDecimal totalRevenue = products.stream()
            .filter(p -> p.getStatus() == Product.ProductStatus.APPROVED)
            .map(p -> BigDecimal.valueOf(p.getSellingPrice()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        System.out.println("💰 Revenus totaux calculés pour le fournisseur " + supplierId + ": " + totalRevenue);
        return totalRevenue;
    }

    @Override
    public BigDecimal getReceivedAmount(Long supplierId) {
        List<Product> products = productRepository.findBySupplierId(supplierId);
        
        // Calculer le montant reçu (produits livrés et reçus)
        BigDecimal receivedAmount = products.stream()
            .filter(p -> p.getStatus() == Product.ProductStatus.APPROVED)
            .filter(p -> p.getShipmentStatus() == Product.ShipmentStatus.DELIVERED)
            .filter(p -> p.getReceptionStatus() == Product.ReceptionStatus.RECEIVED)
            .map(p -> BigDecimal.valueOf(p.getSellingPrice()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        System.out.println("💰 Montant reçu calculé pour le fournisseur " + supplierId + ": " + receivedAmount);
        return receivedAmount;
    }

    @Override
    public BigDecimal getPendingAmount(Long supplierId) {
        BigDecimal totalRevenue = getTotalRevenue(supplierId);
        BigDecimal receivedAmount = getReceivedAmount(supplierId);
        return totalRevenue.subtract(receivedAmount);
    }

    @Override
    public BigDecimal getThisMonthRevenue(Long supplierId) {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now();
        
        List<Product> products = productRepository.findBySupplierId(supplierId);
        
        BigDecimal thisMonthRevenue = products.stream()
            .filter(p -> p.getStatus() == Product.ProductStatus.APPROVED)
            .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isAfter(startOfMonth) && p.getCreatedAt().isBefore(endOfMonth))
            .map(p -> BigDecimal.valueOf(p.getSellingPrice()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        System.out.println("💰 Revenus du mois en cours pour le fournisseur " + supplierId + ": " + thisMonthRevenue);
        return thisMonthRevenue;
    }

    @Override
    public BigDecimal getLastMonthRevenue(Long supplierId) {
        LocalDateTime startOfLastMonth = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfLastMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        
        List<Product> products = productRepository.findBySupplierId(supplierId);
        
        BigDecimal lastMonthRevenue = products.stream()
            .filter(p -> p.getStatus() == Product.ProductStatus.APPROVED)
            .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isAfter(startOfLastMonth) && p.getCreatedAt().isBefore(endOfLastMonth))
            .map(p -> BigDecimal.valueOf(p.getSellingPrice()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        System.out.println("💰 Revenus du mois dernier pour le fournisseur " + supplierId + ": " + lastMonthRevenue);
        return lastMonthRevenue;
    }

    @Override
    public String getTopSellingProduct(Long supplierId) {
        List<Product> products = productRepository.findBySupplierId(supplierId);
        
        // Grouper par titre de produit et compter les quantités
        Map<String, Long> productCounts = products.stream()
            .filter(p -> p.getStatus() == Product.ProductStatus.APPROVED)
            .collect(Collectors.groupingBy(
                Product::getTitle,
                Collectors.counting()
            ));
        
        return productCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Aucun produit");
    }

    @Override
    public double getCompletionRate(Long supplierId) {
        List<Product> products = productRepository.findBySupplierId(supplierId);
        
        long totalProducts = products.size();
        long completedProducts = products.stream()
            .filter(p -> p.getStatus() == Product.ProductStatus.APPROVED)
            .filter(p -> p.getShipmentStatus() == Product.ShipmentStatus.DELIVERED)
            .filter(p -> p.getReceptionStatus() == Product.ReceptionStatus.RECEIVED)
            .count();
        
        return totalProducts > 0 ? (double) completedProducts / totalProducts * 100 : 0;
    }
    
    private double calculateAverageProcessingTime(List<Product> products) {
        return products.stream()
            .filter(p -> p.getDeliveryDate() != null && p.getReceivedAt() != null)
            .mapToDouble(p -> ChronoUnit.HOURS.between(p.getDeliveryDate(), p.getReceivedAt()))
            .average()
            .orElse(0.0);
    }
    
    private long getTopSellingQuantity(Long supplierId, String productTitle) {
        if (productTitle == null || productTitle.equals("Aucun produit")) {
            return 0;
        }
        
        List<Product> products = productRepository.findBySupplierId(supplierId);
        return products.stream()
            .filter(p -> p.getTitle().equals(productTitle))
            .filter(p -> p.getStatus() == Product.ProductStatus.APPROVED)
            .count();
    }
}
