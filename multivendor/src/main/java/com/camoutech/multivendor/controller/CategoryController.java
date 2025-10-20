package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.model.ProductCategory;
import com.camoutech.multivendor.model.ProductSubCategory;
import com.camoutech.multivendor.repository.ProductCategoryRepository;
import com.camoutech.multivendor.repository.ProductSubCategoryRepository;
import com.camoutech.multivendor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepository userRepository;

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
     * Récupérer les sous-catégories par nom de catégorie
     */
    @GetMapping("/name/{categoryName}/subcategories")
    public ResponseEntity<List<ProductSubCategory>> getSubCategoriesByCategoryName(@PathVariable String categoryName) {
        // Trouver la catégorie par nom
        ProductCategory category = categoryRepository.findByNameIgnoreCase(categoryName);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<ProductSubCategory> subCategories = subCategoryRepository.findByParentCategoryIdAndIsActiveTrue(category.getId());
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

    /**
     * Créer une catégorie (simple, sans sécurité)
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProductCategory> createCategory(@RequestBody ProductCategory request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        var admin = userRepository.findByEmail(email);
        if (admin == null || admin.getRole() == null || !admin.getRole().name().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        request.setCreatedByEmail(admin.getEmail());
        request.setCreatedByName(admin.getFullName());

        ProductCategory saved = categoryRepository.save(request);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * Créer une sous-catégorie sous une catégorie donnée (simple, sans sécurité)
     */
    @PostMapping("/{categoryId}/subcategories")
    public ResponseEntity<ProductSubCategory> createSubCategory(
            @PathVariable Long categoryId,
            @RequestBody ProductSubCategory request
    ) {
        return categoryRepository.findById(categoryId)
                .map(parent -> {
                    request.setParentCategory(parent);
                    ProductSubCategory saved = subCategoryRepository.save(request);
                    return new ResponseEntity<>(saved, HttpStatus.CREATED);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
