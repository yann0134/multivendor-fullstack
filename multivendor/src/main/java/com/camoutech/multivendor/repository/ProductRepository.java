package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.model.Product;
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
    
    @Query("SELECT p FROM Product p WHERE p.quantity > 0 AND (p.expiryDate IS NULL OR p.expiryDate > CURRENT_TIMESTAMP)")
    Page<Product> findAvailableProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.category.type = :type AND p.quantity > 0 AND (p.expiryDate IS NULL OR p.expiryDate > CURRENT_TIMESTAMP)")
    Page<Product> findAvailableProductsByType(@Param("type") ProductCategory.CategoryType type, Pageable pageable);
    
    // Méthodes pour la gestion des statuts
    Page<Product> findByStatus(Product.ProductStatus status, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.status = :status AND p.supplier.email = :supplierEmail")
    Page<Product> findByStatusAndSupplierEmail(Product.ProductStatus status, String supplierEmail, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.status = :status AND p.supplier = :supplier")
    Page<Product> findByStatusAndSupplier(Product.ProductStatus status, com.camoutech.multivendor.model.Supplier supplier, Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.status = :status")
    Long countByStatus(Product.ProductStatus status);
}
