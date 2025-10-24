package com.camoutech.multivendor.service;

import com.camoutech.multivendor.model.SupplierDashboardStats;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service pour les statistiques du tableau de bord du fournisseur
 */
public interface SupplierDashboardService {
    
    /**
     * Récupérer les statistiques complètes du tableau de bord pour un fournisseur
     * @param supplierId ID du fournisseur
     * @return Statistiques du tableau de bord
     */
    SupplierDashboardStats getSupplierDashboardStats(Long supplierId);
    
    /**
     * Récupérer les revenus totaux du fournisseur (basés sur les prix de vente)
     * @param supplierId ID du fournisseur
     * @return Montant total des revenus
     */
    BigDecimal getTotalRevenue(Long supplierId);
    
    /**
     * Récupérer le montant déjà reçu par le fournisseur
     * @param supplierId ID du fournisseur
     * @return Montant reçu
     */
    BigDecimal getReceivedAmount(Long supplierId);
    
    /**
     * Récupérer le montant en attente de paiement
     * @param supplierId ID du fournisseur
     * @return Montant en attente
     */
    BigDecimal getPendingAmount(Long supplierId);
    
    /**
     * Récupérer les revenus du mois en cours
     * @param supplierId ID du fournisseur
     * @return Revenus du mois en cours
     */
    BigDecimal getThisMonthRevenue(Long supplierId);
    
    /**
     * Récupérer les revenus du mois dernier
     * @param supplierId ID du fournisseur
     * @return Revenus du mois dernier
     */
    BigDecimal getLastMonthRevenue(Long supplierId);
    
    /**
     * Récupérer le produit le plus vendu
     * @param supplierId ID du fournisseur
     * @return Nom du produit le plus vendu
     */
    String getTopSellingProduct(Long supplierId);
    
    /**
     * Récupérer le taux de complétion des commandes
     * @param supplierId ID du fournisseur
     * @return Taux de complétion en pourcentage
     */
    double getCompletionRate(Long supplierId);
}
