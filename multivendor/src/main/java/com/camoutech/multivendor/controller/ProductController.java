package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.ProductCategory;
import com.camoutech.multivendor.repository.ProductRepository;
import com.camoutech.multivendor.repository.ProductCategoryRepository;
import com.camoutech.multivendor.repository.ProductSubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour la gestion des produits agricoles
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductSubCategoryRepository subCategoryRepository;

    /**
     * Récupérer tous les produits avec pagination
     */
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> products = productRepository.findAll(pageable);
        
        return ResponseEntity.ok(products);
    }

    /**
     * Récupérer les produits par catégorie
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<Product>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products = productRepository.findByCategoryId(categoryId, pageable);
        
        return ResponseEntity.ok(products);
    }

    /**
     * Récupérer les produits par sous-catégorie
     */
    @GetMapping("/subcategory/{subCategoryId}")
    public ResponseEntity<Page<Product>> getProductsBySubCategory(
            @PathVariable Long subCategoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products = productRepository.findBySubCategoryId(subCategoryId, pageable);
        
        return ResponseEntity.ok(products);
    }

    /**
     * Récupérer les produits par type (ANIMAL ou VEGETAL)
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<Page<Product>> getProductsByType(
            @PathVariable ProductCategory.CategoryType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products = productRepository.findByCategoryType(type, pageable);
        
        return ResponseEntity.ok(products);
    }

    /**
     * Rechercher des produits par nom
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products = productRepository.findByTitleContainingIgnoreCase(query, pageable);
        
        return ResponseEntity.ok(products);
    }

    /**
     * Récupérer les produits bio
     */
    @GetMapping("/organic")
    public ResponseEntity<Page<Product>> getOrganicProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products = productRepository.findByIsOrganicTrue(pageable);
        
        return ResponseEntity.ok(products);
    }

    /**
     * Récupérer les produits locaux
     */
    @GetMapping("/local")
    public ResponseEntity<Page<Product>> getLocalProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products = productRepository.findByIsLocalTrue(pageable);
        
        return ResponseEntity.ok(products);
    }

    /**
     * Récupérer un produit par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupérer les produits en vedette
     */
    @GetMapping("/featured")
    public ResponseEntity<List<Product>> getFeaturedProducts() {
        List<Product> products = productRepository.findTop8ByOrderByNumRatingsDesc();
        return ResponseEntity.ok(products);
    }

    /**
     * Récupérer les nouveaux produits
     */
    @GetMapping("/new")
    public ResponseEntity<List<Product>> getNewProducts() {
        List<Product> products = productRepository.findTop8ByOrderByCreatedAtDesc();
        return ResponseEntity.ok(products);
    }
}