package com.camoutech.multivendor.model;

import java.util.List;

/**
 * Classe pour les statistiques des produits livrés et reçus
 */
public class DeliveredProductStats {
    private long totalDeliveredProducts;
    private long totalValue;
    private long totalQuantity;
    private List<Supplier> topSuppliers;
    
    // Constructeurs
    public DeliveredProductStats() {}
    
    public DeliveredProductStats(long totalDeliveredProducts, long totalValue, long totalQuantity) {
        this.totalDeliveredProducts = totalDeliveredProducts;
        this.totalValue = totalValue;
        this.totalQuantity = totalQuantity;
    }
    
    // Getters et setters
    public long getTotalDeliveredProducts() { 
        return totalDeliveredProducts; 
    }
    
    public void setTotalDeliveredProducts(long totalDeliveredProducts) { 
        this.totalDeliveredProducts = totalDeliveredProducts; 
    }
    
    public long getTotalValue() { 
        return totalValue; 
    }
    
    public void setTotalValue(long totalValue) { 
        this.totalValue = totalValue; 
    }
    
    public long getTotalQuantity() { 
        return totalQuantity; 
    }
    
    public void setTotalQuantity(long totalQuantity) { 
        this.totalQuantity = totalQuantity; 
    }
    
    public List<Supplier> getTopSuppliers() { 
        return topSuppliers; 
    }
    
    public void setTopSuppliers(List<Supplier> topSuppliers) { 
        this.topSuppliers = topSuppliers; 
    }
}
