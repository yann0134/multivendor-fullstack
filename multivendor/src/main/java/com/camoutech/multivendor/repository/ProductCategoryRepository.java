package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    
    List<ProductCategory> findByIsActiveTrue();
    
    List<ProductCategory> findByType(ProductCategory.CategoryType type);
    
    List<ProductCategory> findByTypeAndIsActiveTrue(ProductCategory.CategoryType type);
    
    Optional<ProductCategory> findByName(String name);
    
    @Query("SELECT c FROM ProductCategory c WHERE c.isActive = true ORDER BY c.name")
    List<ProductCategory> findAllActiveOrderByName();
    
    @Query("SELECT c FROM ProductCategory c WHERE c.type = :type AND c.isActive = true ORDER BY c.name")
    List<ProductCategory> findActiveByTypeOrderByName(ProductCategory.CategoryType type);
}
