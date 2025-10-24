package com.camoutech.multivendor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modèle pour les statistiques du tableau de bord du fournisseur
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDashboardStats {
    
    // Statistiques générales
    private long totalProducts;
    private long approvedProducts;
    private long pendingProducts;
    private long rejectedProducts;
    private long shippedProducts;
    private long deliveredProducts;
    
    // Statistiques de revenus
    private BigDecimal totalRevenue;           // Revenus totaux (prix de vente)
    private BigDecimal receivedAmount;         // Montant déjà reçu
    private BigDecimal pendingAmount;          // Montant en attente
    private BigDecimal thisMonthRevenue;      // Revenus du mois en cours
    private BigDecimal lastMonthRevenue;      // Revenus du mois dernier
    
    // Statistiques de performance
    private double averageProcessingTime;     // Temps moyen de traitement (en heures)
    private long totalOrders;                 // Nombre total de commandes
    private long completedOrders;             // Commandes complétées
    private double completionRate;            // Taux de complétion (%)
    
    // Statistiques temporelles
    private LocalDateTime lastActivity;       // Dernière activité
    private LocalDateTime accountCreated;    // Date de création du compte
    
    // Top produits
    private String topSellingProduct;        // Produit le plus vendu
    private long topSellingQuantity;         // Quantité vendue du top produit
    
    // Constructeur pour les statistiques de base
    public SupplierDashboardStats(long totalProducts, long approvedProducts, 
                                long pendingProducts, long rejectedProducts,
                                BigDecimal totalRevenue, BigDecimal receivedAmount) {
        this.totalProducts = totalProducts;
        this.approvedProducts = approvedProducts;
        this.pendingProducts = pendingProducts;
        this.rejectedProducts = rejectedProducts;
        this.totalRevenue = totalRevenue;
        this.receivedAmount = receivedAmount;
        this.pendingAmount = totalRevenue.subtract(receivedAmount);
    }
}
