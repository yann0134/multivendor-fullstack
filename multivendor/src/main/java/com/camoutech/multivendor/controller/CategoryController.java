package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.model.ProductCategory;
import com.camoutech.multivendor.model.ProductSubCategory;
import com.camoutech.multivendor.repository.ProductCategoryRepository;
import com.camoutech.multivendor.repository.ProductSubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des catégories de produits agricoles
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ProductCategoryRepository categoryRepository;
    private final ProductSubCategoryRepository subCategoryRepository;

    /**
     * Récupérer toutes les catégories actives
     */
    @GetMapping
    public ResponseEntity<List<ProductCategory>> getAllCategories() {
        List<ProductCategory> categories = categoryRepository.findAllActiveOrderByName();
        return ResponseEntity.ok(categories);
    }

    /**
     * Récupérer les catégories par type (ANIMAL ou VEGETAL)
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<ProductCategory>> getCategoriesByType(@PathVariable ProductCategory.CategoryType type) {
        List<ProductCategory> categories = categoryRepository.findActiveByTypeOrderByName(type);
        return ResponseEntity.ok(categories);
    }

    /**
     * Récupérer une catégorie par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductCategory> getCategoryById(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupérer les sous-catégories d'une catégorie
     */
    @GetMapping("/{categoryId}/subcategories")
    public ResponseEntity<List<ProductSubCategory>> getSubCategoriesByCategory(@PathVariable Long categoryId) {
        List<ProductSubCategory> subCategories = subCategoryRepository.findByParentCategoryIdAndIsActiveTrue(categoryId);
        return ResponseEntity.ok(subCategories);
    }

    /**
     * Récupérer toutes les sous-catégories
     */
    @GetMapping("/subcategories")
    public ResponseEntity<List<ProductSubCategory>> getAllSubCategories() {
        List<ProductSubCategory> subCategories = subCategoryRepository.findByIsActiveTrue();
        return ResponseEntity.ok(subCategories);
    }

    /**
     * Récupérer une sous-catégorie par ID
     */
    @GetMapping("/subcategories/{id}")
    public ResponseEntity<ProductSubCategory> getSubCategoryById(@PathVariable Long id) {
        return subCategoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
