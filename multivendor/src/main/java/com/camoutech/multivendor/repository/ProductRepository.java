package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.Supplier;
import com.camoutech.multivendor.model.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findBySellerId(Long id);

    @Query("SELECT p FROM Product p WHERE " +
            "(:query IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR (:query IS NULL OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :query, '%'))))")
    List<Product> searchProduct(@Param("query") String query);

    // Nouvelles méthodes pour les produits agricoles
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    Page<Product> findBySubCategoryId(Long subCategoryId, Pageable pageable);
    
    Page<Product> findByCategoryType(ProductCategory.CategoryType type, Pageable pageable);
    
    Page<Product> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    Page<Product> findByOrganicTrue(Pageable pageable);
    
    Page<Product> findByLocalTrue(Pageable pageable);
    
    List<Product> findTop8ByOrderByNumRatingsDesc();
    
    List<Product> findTop8ByOrderByCreatedAtDesc();
    
    @Query("SELECT p FROM Product p WHERE p.status = 'APPROVED' AND p.receptionStatus = 'RECEIVED' AND p.shipmentStatus = 'DELIVERED' AND p.quantity > 0 AND (p.expiryDate IS NULL OR p.expiryDate > CURRENT_TIMESTAMP)")
    Page<Product> findAvailableProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.category.type = :type AND p.status = 'APPROVED' AND p.receptionStatus = 'RECEIVED' AND p.shipmentStatus = 'DELIVERED' AND p.quantity > 0 AND (p.expiryDate IS NULL OR p.expiryDate > CURRENT_TIMESTAMP)")
    Page<Product> findAvailableProductsByType(@Param("type") ProductCategory.CategoryType type, Pageable pageable);
    
    // Méthodes pour la gestion des statuts
    Page<Product> findByStatus(Product.ProductStatus status, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.status = :status AND p.supplier.email = :supplierEmail")
    Page<Product> findByStatusAndSupplierEmail(Product.ProductStatus status, String supplierEmail, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.status = :status AND p.supplier = :supplier")
    Page<Product> findByStatusAndSupplier(Product.ProductStatus status, com.camoutech.multivendor.model.Supplier supplier, Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.status = :status")
    Long countByStatus(Product.ProductStatus status);




    @Query("SELECT p FROM Product p " +
            "WHERE p.supplier.id = :supplierId " +
            "AND p.receptionStatus = 'PENDING' " +
            "AND p.shipmentStatus = 'NOT_SHIPPED' " +
            "AND p.status = 'APPROVED' " +
            "ORDER BY p.createdAt DESC")
    List<Product> findApprovedAndPendingProductsBySupplier(@Param("supplierId") Long supplierId);


    // Dans Produit expedier par le fournisseur
    @Query("SELECT p FROM Product p " +
            "WHERE p.supplier.id = :supplierId " +
            "AND p.status = 'APPROVED' " +
            "AND p.receptionStatus = 'PENDING' " +
            "AND (p.shipmentStatus = 'SHIPPED' OR p.shipmentStatus = 'NOT_SHIPPED') " +
            "ORDER BY p.createdAt DESC")
    List<Product> findApprovedAndPendingProductsBySupplierExpedier(@Param("supplierId") Long supplierId);


    // Dans Produit expedier par le fournisseur et approuver par l'entrepot
    @Query("SELECT p FROM Product p " +
            "WHERE p.supplier.id = :supplierId " +
            "AND p.receptionStatus = 'RECEIVED' " +
            "AND p.status = 'APPROVED' " +
            "AND p.shipmentStatus = 'DELIVERED' " +
            "ORDER BY p.createdAt DESC")
    List<Product> findApprovedAndPendingProductsBySupplierRecu(@Param("supplierId") Long supplierId);

    // Produits en attente de réception par l'entrepôt (APPROVED + SHIPPED)
    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.supplier s " +
            "LEFT JOIN FETCH s.businessDetails " +
            "LEFT JOIN FETCH s.pickupAddress " +
            "WHERE p.status = 'APPROVED' " +
            "AND p.shipmentStatus = 'SHIPPED' " +
            "AND p.receptionStatus = 'PENDING' " +
            "ORDER BY p.createdAt DESC")
    List<Product> findProductsPendingReception();

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.supplier s " +
            "LEFT JOIN FETCH s.businessDetails " +
            "LEFT JOIN FETCH s.pickupAddress " +
            "WHERE p.status = 'APPROVED' " +
            "AND p.shipmentStatus = 'DELIVERED' " +
            "AND p.receptionStatus = 'RECEIVED' " +
            "ORDER BY p.updatedAt DESC")
    List<Product> findReceivedProducts();
    
    // Méthode pour récupérer tous les produits d'un fournisseur
    @Query("SELECT p FROM Product p WHERE p.supplier.id = :supplierId")
    List<Product> findBySupplierId(@Param("supplierId") Long supplierId);
    
    // Méthode pour récupérer les produits disponibles pour les clients
    @Query("SELECT p FROM Product p WHERE p.status = 'APPROVED' AND p.receptionStatus = 'RECEIVED' AND p.shipmentStatus = 'DELIVERED'")
    Page<Product> findCustomerAvailableProducts(Pageable pageable);
    
    // Méthode pour récupérer les produits par catégorie pour les clients
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.status = 'APPROVED' AND p.receptionStatus = 'RECEIVED' AND p.shipmentStatus = 'DELIVERED'")
    Page<Product> findCustomerProductsByCategory(@Param("categoryId") Long categoryId, Pageable pageable);
    
    // Méthode pour récupérer les produits par sous-catégorie pour les clients
    @Query("SELECT p FROM Product p WHERE p.subCategory.id = :subCategoryId AND p.status = 'APPROVED' AND p.receptionStatus = 'RECEIVED' AND p.shipmentStatus = 'DELIVERED'")
    Page<Product> findCustomerProductsBySubCategory(@Param("subCategoryId") Long subCategoryId, Pageable pageable);
    
    // Méthode pour récupérer les produits par type pour les clients
    @Query("SELECT p FROM Product p WHERE p.category.type = :type AND p.status = 'APPROVED' AND p.receptionStatus = 'RECEIVED' AND p.shipmentStatus = 'DELIVERED'")
    Page<Product> findCustomerProductsByType(@Param("type") ProductCategory.CategoryType type, Pageable pageable);
    
    // Méthode pour rechercher des produits pour les clients
    @Query("SELECT p FROM Product p WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND p.status = 'APPROVED' AND p.receptionStatus = 'RECEIVED' AND p.shipmentStatus = 'DELIVERED'")
    Page<Product> searchCustomerProducts(@Param("query") String query, Pageable pageable);
    
    // Méthode pour récupérer les produits bio pour les clients
    @Query("SELECT p FROM Product p WHERE p.organic = true AND p.status = 'APPROVED' AND p.receptionStatus = 'RECEIVED' AND p.shipmentStatus = 'DELIVERED'")
    Page<Product> findCustomerOrganicProducts(Pageable pageable);
    
    // Méthode pour récupérer les produits locaux pour les clients
    @Query("SELECT p FROM Product p WHERE p.local = true AND p.status = 'APPROVED' AND p.receptionStatus = 'RECEIVED' AND p.shipmentStatus = 'DELIVERED'")
    Page<Product> findCustomerLocalProducts(Pageable pageable);
    
    // Méthode pour récupérer les produits en vedette pour les clients
    @Query("SELECT p FROM Product p WHERE p.status = 'APPROVED' AND p.receptionStatus = 'RECEIVED' AND p.shipmentStatus = 'DELIVERED' ORDER BY p.numRatings DESC")
    List<Product> findCustomerFeaturedProducts();
    
    // Méthode pour récupérer les nouveaux produits pour les clients (approuvés par l'entrepôt dans les 2 derniers jours)
    @Query("SELECT p FROM Product p WHERE p.status = 'APPROVED' AND p.receptionStatus = 'RECEIVED' AND p.shipmentStatus = 'DELIVERED' AND p.statusUpdatedAt >= CURRENT_DATE - 2 ORDER BY p.statusUpdatedAt DESC")
    List<Product> findCustomerNewProducts();
    
    // Méthode pour récupérer les produits par gamme de prix pour les clients
    @Query("SELECT p FROM Product p WHERE p.status = 'APPROVED' AND p.receptionStatus = 'RECEIVED' AND p.shipmentStatus = 'DELIVERED' AND p.sellingPrice >= :minPrice AND p.sellingPrice <= :maxPrice")
    Page<Product> findCustomerAvailableProductsByPriceRange(@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice, Pageable pageable);
    
    // Méthode pour récupérer les produits avec tous les filtres pour les clients
    @Query("SELECT p FROM Product p WHERE p.status = 'APPROVED' AND p.receptionStatus = 'RECEIVED' AND p.shipmentStatus = 'DELIVERED' " +
           "AND (:minPrice IS NULL OR p.sellingPrice >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.sellingPrice <= :maxPrice) " +
           "AND (:organic IS NULL OR p.organic = :organic) " +
           "AND (:local IS NULL OR p.local = :local) " +
           "AND (:category IS NULL OR p.category.id = :category) " +
           "AND (:subCategory IS NULL OR p.subCategory.id = :subCategory)")
    Page<Product> findCustomerAvailableProductsWithFilters(
        @Param("minPrice") Integer minPrice, 
        @Param("maxPrice") Integer maxPrice,
        @Param("organic") Boolean organic,
        @Param("local") Boolean local,
        @Param("category") Long category,
        @Param("subCategory") Long subCategory,
        Pageable pageable);
    
    // Méthodes pour récupérer les prix min/max des produits disponibles
    @Query("SELECT MIN(p.sellingPrice) FROM Product p WHERE p.status = 'APPROVED' AND p.receptionStatus = 'RECEIVED' AND p.shipmentStatus = 'DELIVERED'")
    Integer findMinSellingPrice();
    
    @Query("SELECT MAX(p.sellingPrice) FROM Product p WHERE p.status = 'APPROVED' AND p.receptionStatus = 'RECEIVED' AND p.shipmentStatus = 'DELIVERED'")
    Integer findMaxSellingPrice();
}
