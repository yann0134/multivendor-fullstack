package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.model.ProductSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSubCategoryRepository extends JpaRepository<ProductSubCategory, Long> {
    
    List<ProductSubCategory> findByIsActiveTrue();
    
    List<ProductSubCategory> findByParentCategoryIdAndIsActiveTrue(Long parentCategoryId);
    
    Optional<ProductSubCategory> findByName(String name);
    
    List<ProductSubCategory> findByParentCategoryId(Long parentCategoryId);
}
