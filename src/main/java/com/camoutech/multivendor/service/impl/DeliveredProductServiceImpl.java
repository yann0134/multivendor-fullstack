package com.camoutech.multivendor.service.impl;

import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.Supplier;
import com.camoutech.multivendor.model.DeliveredProductStats;
import com.camoutech.multivendor.repository.ProductRepository;
import com.camoutech.multivendor.repository.SupplierRepository;
import com.camoutech.multivendor.service.DeliveredProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service pour la gestion des produits livrés et reçus
 */
@Service
@RequiredArgsConstructor
public class DeliveredProductServiceImpl implements DeliveredProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    @Override
    public List<Product> getDeliveredAndReceivedProductsBySupplier(Long supplierId) {
        System.out.println("🔍 Récupération des produits livrés et reçus pour le fournisseur ID: " + supplierId);
        
        List<Product> products = productRepository.findApprovedAndPendingProductsBySupplierRecu(supplierId);
        
        System.out.println("📦 Produits trouvés: " + products.size());
        for (Product product : products) {
            System.out.println("  📦 " + product.getTitle() + 
                " - Statut: " + product.getStatus() + 
                " - Envoi: " + product.getShipmentStatus() + 
                " - Réception: " + product.getReceptionStatus());
        }
        
        return products;
    }

    @Override
    public List<Product> getAllDeliveredAndReceivedProducts() {
        System.out.println("🔍 Récupération de tous les produits livrés et reçus");
        
        List<Product> allProducts = productRepository.findAll();
        List<Product> deliveredAndReceivedProducts = allProducts.stream()
                .filter(product -> 
                    product.getStatus() == Product.ProductStatus.APPROVED &&
                    product.getShipmentStatus() == Product.ShipmentStatus.SHIPPED &&
                    product.getReceptionStatus() == Product.ReceptionStatus.RECEIVED
                )
                .collect(Collectors.toList());
        
        System.out.println("📦 Total des produits livrés et reçus: " + deliveredAndReceivedProducts.size());
        
        return deliveredAndReceivedProducts;
    }

    @Override
    public DeliveredProductStats getDeliveredProductStats(Long supplierId) {
        System.out.println("📊 Calcul des statistiques des produits livrés et reçus");
        
        List<Product> products;
        if (supplierId != null) {
            products = getDeliveredAndReceivedProductsBySupplier(supplierId);
        } else {
            products = getAllDeliveredAndReceivedProducts();
        }
        
        long totalDeliveredProducts = products.size();
        long totalValue = products.stream()
                .mapToLong(product -> (long) product.getSupplierPrice())
                .sum();
        long totalQuantity = products.stream()
                .mapToLong(product -> {
                    if (product.getAdminRequestedQuantity() > 0) {
                        return product.getAdminRequestedQuantity();
                    } else if (product.getSupplierAvailableQuantity() > 0) {
                        return product.getSupplierAvailableQuantity();
                    }
                    return 0;
                })
                .sum();
        
        // Récupérer les top fournisseurs
        List<Supplier> topSuppliers = getTopSuppliers();
        
        DeliveredProductStats stats = new DeliveredProductStats(totalDeliveredProducts, totalValue, totalQuantity);
        stats.setTopSuppliers(topSuppliers);
        
        System.out.println("📊 Statistiques calculées:");
        System.out.println("  - Total produits livrés: " + totalDeliveredProducts);
        System.out.println("  - Valeur totale: " + totalValue);
        System.out.println("  - Quantité totale: " + totalQuantity);
        System.out.println("  - Top fournisseurs: " + topSuppliers.size());
        
        return stats;
    }
    
    /**
     * Récupérer les top fournisseurs basés sur le nombre de produits livrés
     */
    private List<Supplier> getTopSuppliers() {
        return supplierRepository.findAll().stream()
                .filter(supplier -> {
                    List<Product> supplierProducts = getDeliveredAndReceivedProductsBySupplier(supplier.getId());
                    return !supplierProducts.isEmpty();
                })
                .sorted((s1, s2) -> {
                    List<Product> products1 = getDeliveredAndReceivedProductsBySupplier(s1.getId());
                    List<Product> products2 = getDeliveredAndReceivedProductsBySupplier(s2.getId());
                    return Integer.compare(products2.size(), products1.size());
                })
                .limit(5)
                .collect(Collectors.toList());
    }
}
