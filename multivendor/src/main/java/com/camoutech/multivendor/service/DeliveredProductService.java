package com.camoutech.multivendor.service;

import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.Supplier;
import com.camoutech.multivendor.model.DeliveredProductStats;
import java.util.List;

/**
 * Service pour la gestion des produits livrés et reçus
 */
public interface DeliveredProductService {
    
    /**
     * Récupérer les produits expédiés et reçus par un fournisseur spécifique
     * @param supplierId ID du fournisseur
     * @return Liste des produits livrés et reçus
     */
    List<Product> getDeliveredAndReceivedProductsBySupplier(Long supplierId);
    
    /**
     * Récupérer tous les produits expédiés et reçus (pour l'admin)
     * @return Liste de tous les produits livrés et reçus
     */
    List<Product> getAllDeliveredAndReceivedProducts();
    
    /**
     * Récupérer les statistiques des produits livrés et reçus
     * @param supplierId ID du fournisseur (optionnel, null pour tous)
     * @return Statistiques des produits livrés et reçus
     */
    DeliveredProductStats getDeliveredProductStats(Long supplierId);
}
