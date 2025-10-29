package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.exceptions.ProductException;
import com.camoutech.multivendor.model.*;
import com.camoutech.multivendor.repository.ProductRepository;
import com.camoutech.multivendor.repository.ProductCategoryRepository;
import com.camoutech.multivendor.repository.ProductSubCategoryRepository;
import com.camoutech.multivendor.repository.UserRepository;
import com.camoutech.multivendor.repository.SupplierRepository;
import com.camoutech.multivendor.repository.SupplyOrderRepository;
import com.camoutech.multivendor.request.CreateProductRequest;
import com.camoutech.multivendor.request.UpdateShipmentStatusRequest;
import com.camoutech.multivendor.request.UpdateReceptionStatusRequest;
import com.camoutech.multivendor.request.SupplierProfileUpdateRequest;
import com.camoutech.multivendor.service.ProductService;
import com.camoutech.multivendor.service.DeliveredProductService;
import com.camoutech.multivendor.service.SupplierDashboardService;
import com.camoutech.multivendor.service.WarehouseService;
import com.camoutech.multivendor.model.SupplierDashboardStats;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;




/**
 * Contrôleur pour la gestion des produits agricoles
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductSubCategoryRepository productSubCategoryRepository;
    private final ProductService productService;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final SupplyOrderRepository supplyOrderRepository;
    // private final DeliveredProductService deliveredProductService;
    private final SupplierDashboardService supplierDashboardService;
    private final WarehouseService warehouseService;

    /**
     * Récupérer tous les produits avec pagination (pour les clients)
     */
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Boolean organic,
            @RequestParam(required = false) Boolean local,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) Long subCategory) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Utiliser la méthode avec filtres si fournis
        Page<Product> products;
        if (minPrice != null || maxPrice != null || organic != null || local != null || category != null || subCategory != null) {
            products = productRepository.findCustomerAvailableProductsWithFilters(
                minPrice != null ? minPrice : 0, 
                maxPrice != null ? maxPrice : Integer.MAX_VALUE,
                organic,
                local,
                category,
                subCategory,
                pageable
            );
        } else {
            products = productRepository.findCustomerAvailableProducts(pageable);
        }
        
        return ResponseEntity.ok(products);
    }

    /**
     * Récupérer les prix min/max des produits disponibles
     */
    @GetMapping("/price-range")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Integer>> getPriceRange() {
        Integer minPrice = productRepository.findMinSellingPrice();
        Integer maxPrice = productRepository.findMaxSellingPrice();
        
        Map<String, Integer> priceRange = Map.of(
            "minPrice", minPrice != null ? minPrice : 0,
            "maxPrice", maxPrice != null ? maxPrice : 100000
        );
        
        return ResponseEntity.ok(priceRange);
    }

    /**
     * Récupérer les produits par catégorie (pour les clients)
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<Product>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products = productRepository.findCustomerProductsByCategory(categoryId, pageable);
        
        return ResponseEntity.ok(products);
    }

    /**
     * Récupérer les produits par sous-catégorie (pour les clients)
     */
    @GetMapping("/subcategory/{subCategoryId}")
    public ResponseEntity<Page<Product>> getProductsBySubCategory(
            @PathVariable Long subCategoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products = productRepository.findCustomerProductsBySubCategory(subCategoryId, pageable);
        
        return ResponseEntity.ok(products);
    }

    /**
     * Récupérer les produits par type (ANIMAL ou VEGETAL) (pour les clients)
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<Page<Product>> getProductsByType(
            @PathVariable ProductCategory.CategoryType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products = productRepository.findCustomerProductsByType(type, pageable);
        
        return ResponseEntity.ok(products);
    }

    /**
     * Rechercher des produits par nom (pour les clients)
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products = productRepository.searchCustomerProducts(query, pageable);
        
        return ResponseEntity.ok(products);
    }

    /**
     * Récupérer les produits bio (pour les clients)
     */
    @GetMapping("/organic")
    public ResponseEntity<Page<Product>> getOrganicProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products = productRepository.findCustomerOrganicProducts(pageable);
        
        return ResponseEntity.ok(products);
    }

    /**
     * Récupérer les produits locaux (pour les clients)
     */
    @GetMapping("/local")
    public ResponseEntity<Page<Product>> getLocalProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products = productRepository.findCustomerLocalProducts(pageable);
        
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
     * Récupérer les produits en vedette (pour les clients)
     */
    @GetMapping("/featured")
    public ResponseEntity<List<Product>> getFeaturedProducts() {
        List<Product> products = productRepository.findCustomerFeaturedProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Récupérer les nouveaux produits (pour les clients)
     */
    @GetMapping("/new")
    public ResponseEntity<List<Product>> getNewProducts() {
        List<Product> products = productRepository.findCustomerNewProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/supplier/approved")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
    public ResponseEntity<List<Product>> getApprovedProductsForCurrentSupplier() {
        List<Product> products = productService.getApprovedProductsForCurrentSupplier();
        return ResponseEntity.ok(products);
    }

    /**
     * Créer un produit (réservé au fournisseur)
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        System.out.println("🔍 Création produit - Email: " + email);
        System.out.println("🔍 Création produit - Authorities: " + auth.getAuthorities());

        Supplier supplier = supplierRepository.findByEmail(email).orElse(null);
        System.out.println("🔍 Supplier trouvé: " + (supplier != null ? supplier.getSupplierName() : "NULL"));
        
        if (supplier == null) {
            System.out.println("❌ Supplier non trouvé pour l'email: " + email);
            System.out.println("🔍 Vérification dans la table User...");
            
            // Vérifier si l'utilisateur existe dans la table User
            User user = userRepository.findByEmail(email);
            if (user != null && user.getRole() == com.camoutech.multivendor.domain.USER_ROLE.ROLE_SUPPLIER) {
                System.out.println("✅ Utilisateur trouvé dans la table User avec le rôle SUPPLIER");
                System.out.println("🔧 Création automatique du supplier à partir de l'utilisateur existant...");
                
                // Créer un supplier à partir de l'utilisateur existant
                supplier = new Supplier();
                supplier.setEmail(user.getEmail());
                supplier.setSupplierName(user.getFullName());
                supplier.setRole(user.getRole());
                supplier.setMobile(user.getMobile());
                supplier.setPassword(user.getPassword());
                supplier.setAccountStatus(com.camoutech.multivendor.domain.AccountStatus.ACTIVE);
                
                supplier = supplierRepository.save(supplier);
                System.out.println("✅ Supplier créé automatiquement: " + supplier.getSupplierName());
            } else {
                System.out.println("❌ Utilisateur non trouvé ou rôle incorrect");
                System.out.println("❌ L'utilisateur doit d'abord s'inscrire comme fournisseur");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null); // Retourner une réponse vide avec le statut 403
            }
        }

        try {
            Product created = productService.createProductWithSupplier(req, supplier);
            System.out.println("✅ Produit créé avec succès: " + created.getId());
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (ProductException e) {
            System.out.println("❌ Erreur lors de la création du produit: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Récupérer les produits en attente d'approbation (Admin)
     */
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<Product>> getPendingProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products = productRepository.findByStatus(Product.ProductStatus.PENDING_APPROVAL, pageable);
        
        return ResponseEntity.ok(products);
    }

    /**
     * Récupérer les produits approuvés (Admin)
     */
    @GetMapping("/approved")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<Product>> getApprovedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("statusUpdatedAt").descending());
        Page<Product> products = productRepository.findByStatus(Product.ProductStatus.APPROVED, pageable);
        
        return ResponseEntity.ok(products);
    }

    /**
     * Récupérer les produits rejetés (Admin)
     */
    @GetMapping("/rejected")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<Product>> getRejectedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("statusUpdatedAt").descending());
        Page<Product> products = productRepository.findByStatus(Product.ProductStatus.REJECTED, pageable);
        
        return ResponseEntity.ok(products);
    }

    /**
     * Approuver un produit (Admin)
     */
    @PutMapping("/{productId}/approve")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Product> approveProduct(@PathVariable Long productId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = auth.getName();
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        
        product.setStatus(Product.ProductStatus.APPROVED);
        product.setStatusUpdatedAt(java.time.LocalDateTime.now());
        product.setReviewedBy(adminEmail);
        product.setRejectionReason(null); // Effacer la raison de rejet
        
        Product updated = productRepository.save(product);
        return ResponseEntity.ok(updated);
    }

    /**
     * Demander une quantité spécifique pour un produit (Admin)
     */
    @PutMapping("/{productId}/request-quantity")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Product> requestQuantity(
            @PathVariable Long productId,
            @RequestParam int requestedQuantity) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = auth.getName();
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        
        // Vérifier que la quantité demandée ne dépasse pas la quantité disponible
        Integer supplierAvailableQuantity = product.getSupplierAvailableQuantity();
        if (supplierAvailableQuantity == null || requestedQuantity > supplierAvailableQuantity) {
            return ResponseEntity.badRequest().body(null);
        }
        
        product.setAdminRequestedQuantity(requestedQuantity);
        product.setStockNegotiationPending(true);
        product.setStatusUpdatedAt(java.time.LocalDateTime.now());
        product.setReviewedBy(adminEmail);
        
        Product updated = productRepository.save(product);
        return ResponseEntity.ok(updated);
    }

    /**
     * Confirmer la quantité demandée (Fournisseur)
     */
    @PutMapping("/{productId}/confirm-quantity")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
    public ResponseEntity<Product> confirmQuantity(@PathVariable Long productId) {
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        
        // Vérifier que le produit appartient au fournisseur
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Supplier supplier = supplierRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé"));
        
        if (product.getSupplier() == null || !product.getSupplier().getId().equals(supplier.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        // Confirmer la quantité et approuver le produit
        product.setStockQuantity(product.getAdminRequestedQuantity());
        product.setStockNegotiationPending(false);
        product.setStatus(Product.ProductStatus.APPROVED);
        product.setStatusUpdatedAt(java.time.LocalDateTime.now());
        
        Product updated = productRepository.save(product);
        return ResponseEntity.ok(updated);
    }

    /**
     * Modifier la quantité disponible (Fournisseur)
     */
    @PutMapping("/{productId}/update-quantity")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
    public ResponseEntity<Product> updateQuantity(
            @PathVariable Long productId,
            @RequestParam int newQuantity) {
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        
        // Vérifier que le produit appartient au fournisseur
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Supplier supplier = supplierRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé"));
        
        if (product.getSupplier() == null || !product.getSupplier().getId().equals(supplier.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        // Vérifier que le produit n'est pas encore approuvé
        if (product.getStatus() == Product.ProductStatus.APPROVED) {
            return ResponseEntity.badRequest().body(null);
        }
        
        // Vérifier que la nouvelle quantité est positive
        if (newQuantity < 0) {
            return ResponseEntity.badRequest().body(null);
        }
        
        // Mettre à jour la quantité disponible
        product.setSupplierAvailableQuantity(newQuantity);
        
        // Si l'admin avait demandé une quantité, vérifier si elle est toujours valide
        if (product.getAdminRequestedQuantity() > 0 && newQuantity < product.getAdminRequestedQuantity()) {
            // Réinitialiser la demande admin si la nouvelle quantité est insuffisante
            product.setAdminRequestedQuantity(0);
            product.setStockNegotiationPending(false);
        }
        
        product.setStatusUpdatedAt(java.time.LocalDateTime.now());
        
        Product updated = productRepository.save(product);
        return ResponseEntity.ok(updated);
    }

    /**
     * Refuser la demande de quantité (Fournisseur)
     */
    @PutMapping("/{productId}/reject-quantity-request")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
    public ResponseEntity<Product> rejectQuantityRequest(@PathVariable Long productId) {
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        
        // Vérifier que le produit appartient au fournisseur
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Supplier supplier = supplierRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé"));
        
        if (product.getSupplier() == null || !product.getSupplier().getId().equals(supplier.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        // Vérifier qu'il y a une négociation en cours
        if (!product.isStockNegotiationPending() || product.getAdminRequestedQuantity() <= 0) {
            return ResponseEntity.badRequest().body(null);
        }
        
        // Refuser la demande et remettre le produit en attente
        product.setAdminRequestedQuantity(0);
        product.setStockNegotiationPending(false);
        product.setStatus(Product.ProductStatus.PENDING_APPROVAL);
        product.setStatusUpdatedAt(java.time.LocalDateTime.now());
        
        Product updated = productRepository.save(product);
        return ResponseEntity.ok(updated);
    }

    /**
     * Validation finale après confirmation du fournisseur (Admin)
     */
    @PutMapping("/{productId}/final-approval")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Product> finalApproval(@PathVariable Long productId) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = auth.getName();
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        
        // Vérifier que le produit est en négociation et que le fournisseur a confirmé
        if (!product.isStockNegotiationPending() || product.getAdminRequestedQuantity() <= 0) {
            return ResponseEntity.badRequest().body(null);
        }
        
        // Validation finale - le produit est approuvé
        product.setStatus(Product.ProductStatus.APPROVED);
        product.setStockNegotiationPending(false);
        product.setStatusUpdatedAt(java.time.LocalDateTime.now());
        product.setReviewedBy(adminEmail);
        
        Product updated = productRepository.save(product);
        
        // Créer automatiquement une commande d'approvisionnement
        createSupplyOrderForProduct(updated);
        
        return ResponseEntity.ok(updated);
    }

    /**
     * Créer une commande d'approvisionnement pour un produit approuvé
     */
    private void createSupplyOrderForProduct(Product product) {
        try {
            // Déterminer la quantité à commander
            int quantityToOrder = product.getAdminRequestedQuantity() > 0 ? 
                product.getAdminRequestedQuantity() : product.getSupplierAvailableQuantity();
            
            if (quantityToOrder <= 0) {
                System.out.println("⚠️ Quantité insuffisante pour créer une commande d'approvisionnement");
                return;
            }
            
            // Créer la commande d'approvisionnement
            com.camoutech.multivendor.model.SupplyOrder supplyOrder = new com.camoutech.multivendor.model.SupplyOrder();
            supplyOrder.setSupplier(product.getSupplier());
            supplyOrder.setStatus(com.camoutech.multivendor.domain.SupplyOrderStatus.PENDING);
            supplyOrder.setOrderDate(java.time.LocalDateTime.now());
            
            // Date de livraison par défaut : 2 jours après validation
            supplyOrder.setDeliveryDate(java.time.LocalDateTime.now().plusDays(2));
            
            // Calculer le montant total
            double totalAmount = quantityToOrder * product.getSupplierPrice();
            supplyOrder.setTotalAmount(totalAmount);
            
            // Générer un ID de commande unique
            String orderId = "SO-" + System.currentTimeMillis();
            supplyOrder.setSupplyOrderId(orderId);
            
            // Créer l'item de commande
            com.camoutech.multivendor.model.SupplyOrderItem orderItem = new com.camoutech.multivendor.model.SupplyOrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(quantityToOrder);
            orderItem.setUnitPrice(product.getSupplierPrice());
            orderItem.setTotalPrice(totalAmount);
            orderItem.setSupplyOrder(supplyOrder);
            
            // Ajouter l'item à la commande
            supplyOrder.getSupplyOrderItems().add(orderItem);
            
            // Sauvegarder la commande
            supplyOrderRepository.save(supplyOrder);
            
            System.out.println("✅ Commande d'approvisionnement créée: " + orderId + 
                             " pour " + quantityToOrder + " unités de " + product.getTitle());
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création de la commande d'approvisionnement: " + e.getMessage());
        }
    }

    /**
     * Rejeter un produit (Admin)
     */
    @PutMapping("/{productId}/reject")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Product> rejectProduct(
            @PathVariable Long productId,
            @RequestBody(required = false) String rejectionReason) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = auth.getName();
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        
        product.setStatus(Product.ProductStatus.REJECTED);
        product.setStatusUpdatedAt(java.time.LocalDateTime.now());
        product.setReviewedBy(adminEmail);
        product.setRejectionReason(rejectionReason);
        
        Product updated = productRepository.save(product);
        return ResponseEntity.ok(updated);
    }

    /**
     * Suspendre un produit (Admin)
     */
    @PutMapping("/{productId}/suspend")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Product> suspendProduct(@PathVariable Long productId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = auth.getName();
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        
        product.setStatus(Product.ProductStatus.SUSPENDED);
        product.setStatusUpdatedAt(java.time.LocalDateTime.now());
        product.setReviewedBy(adminEmail);
        
        Product updated = productRepository.save(product);
        return ResponseEntity.ok(updated);
    }

    /**
     * Récupérer les produits par statut (Admin)
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<Product>> getProductsByStatus(
            @PathVariable Product.ProductStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("statusUpdatedAt").descending());
        Page<Product> products = productRepository.findByStatus(status, pageable);

        return ResponseEntity.ok(products);
    }

    /**
     * Récupérer les produits par statut et par fournisseur
     */
    @GetMapping("/status/{status}/supplier")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
    public ResponseEntity<Page<Product>> getProductsByStatusAndSupplier(
            @PathVariable Product.ProductStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        System.out.println("🔍 Récupération produits - Email: " + email);
        System.out.println("🔍 Récupération produits - Statut: " + status);
        
        Supplier supplier = supplierRepository.findByEmail(email).orElse(null);
        if (supplier == null) {
            System.out.println("❌ Supplier non trouvé pour l'email: " + email);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        System.out.println("✅ Supplier trouvé: " + supplier.getSupplierName());
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products = productRepository.findByStatusAndSupplier(status, supplier, pageable);
        
        System.out.println("✅ Produits trouvés: " + products.getTotalElements());
        
        return ResponseEntity.ok(products);
    }

    /**
     * Modifier un produit (Fournisseur)
     */
    @PutMapping("/{productId}")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @RequestBody CreateProductRequest req) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        System.out.println("🔍 Modification produit - Email: " + email);
        System.out.println("🔍 Modification produit - Product ID: " + productId);
        
        Supplier supplier = supplierRepository.findByEmail(email).orElse(null);
        if (supplier == null) {
            System.out.println("❌ Supplier non trouvé pour l'email: " + email);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Vérifier que le produit appartient au fournisseur
        Product existingProduct = productRepository.findById(productId).orElse(null);
        if (existingProduct == null) {
            System.out.println("❌ Produit non trouvé: " + productId);
            return ResponseEntity.notFound().build();
        }
        
        if (existingProduct.getSupplier() == null || !existingProduct.getSupplier().getId().equals(supplier.getId())) {
            System.out.println("❌ Le produit n'appartient pas au fournisseur");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Mettre à jour les champs du produit
        existingProduct.setTitle(req.getTitle());
        existingProduct.setDescription(req.getDescription());
        existingProduct.setMrpPrice(req.getMrpPrice());
        existingProduct.setSellingPrice(req.getSellingPrice());
        existingProduct.setColor(req.getColor());
        existingProduct.setSizes(req.getSizes());
        
        // Mettre à jour la catégorie et sous-catégorie
        if (req.getCategory() != null && !req.getCategory().isEmpty()) {
            System.out.println("🔍 Catégorie demandée: " + req.getCategory());
            
            // Trouver ou créer la catégorie
            ProductCategory category = productCategoryRepository.findByNameIgnoreCase(req.getCategory());
            if (category == null) {
                category = new ProductCategory();
                category.setName(req.getCategory());
                category.setType(ProductCategory.CategoryType.VEGETAL); // Valeur par défaut
                category = productCategoryRepository.save(category);
                System.out.println("✅ Nouvelle catégorie créée: " + category.getName());
            } else {
                System.out.println("✅ Catégorie existante trouvée: " + category.getName());
            }
            existingProduct.setCategory(category);
        }
        
        /*if (req.getCategory2() != null && !req.getCategory2().isEmpty()) {
            System.out.println("🔍 Sous-catégorie demandée: " + req.getCategory2());
            
            // Trouver ou créer la sous-catégorie
            ProductSubCategory subCategory = productSubCategoryRepository.findByNameIgnoreCase(req.getCategory2());
            if (subCategory == null) {
                subCategory = new ProductSubCategory();
                subCategory.setName(req.getCategory2());
                subCategory.setParentCategory(existingProduct.getCategory());
                subCategory = productSubCategoryRepository.save(subCategory);
                System.out.println("✅ Nouvelle sous-catégorie créée: " + subCategory.getName());
            } else {
                System.out.println("✅ Sous-catégorie existante trouvée: " + subCategory.getName());
            }
            existingProduct.setSubCategory(subCategory);
        }*/
        
        // Mettre à jour les informations agricoles
        existingProduct.setOrigin(req.getOrigin());
        existingProduct.setFarmingMethod(req.getFarmingMethod());
        existingProduct.setSeason(req.getSeason());
        existingProduct.setUnit(req.getUnit());
        existingProduct.setWeight(req.getWeight() != null ? req.getWeight() : 0.0);
        existingProduct.setStorageConditions(req.getStorageConditions());
        existingProduct.setNutritionalInfo(req.getNutritionalInfo());
        existingProduct.setAllergens(req.getAllergens());
        existingProduct.setOrganic(req.getOrganic() != null ? req.getOrganic() : false);
        existingProduct.setLocal(req.getLocal() != null ? req.getLocal() : false);
        existingProduct.setFresh(req.getFresh() != null ? req.getFresh() : true);
        
        // Recalculer le pourcentage de remise
        int discountPercentage = calculateDiscountPercentage(req.getMrpPrice(), req.getSellingPrice());
        existingProduct.setDiscountPercent(discountPercentage);
        
        // Mettre à jour le prix général
        existingProduct.setPrice(req.getSellingPrice());
        
        Product updatedProduct = productRepository.save(existingProduct);
        System.out.println("✅ Produit modifié avec succès: " + updatedProduct.getId());
        
        return ResponseEntity.ok(updatedProduct);
    }
    
    /**
     * Supprimer un produit (Fournisseur)
     */
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        System.out.println("🔍 Suppression produit - Email: " + email);
        System.out.println("🔍 Suppression produit - Product ID: " + productId);
        
        Supplier supplier = supplierRepository.findByEmail(email).orElse(null);
        if (supplier == null) {
            System.out.println("❌ Supplier non trouvé pour l'email: " + email);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Vérifier que le produit appartient au fournisseur
        Product existingProduct = productRepository.findById(productId).orElse(null);
        if (existingProduct == null) {
            System.out.println("❌ Produit non trouvé: " + productId);
            return ResponseEntity.notFound().build();
        }
        
        if (existingProduct.getSupplier() == null || !existingProduct.getSupplier().getId().equals(supplier.getId())) {
            System.out.println("❌ Le produit n'appartient pas au fournisseur");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Supprimer le produit
        productRepository.delete(existingProduct);
        System.out.println("✅ Produit supprimé avec succès: " + productId);
        
        return ResponseEntity.ok("Produit supprimé avec succès");
    }
    
    /**
     * Récupérer un produit spécifique du fournisseur
     */
    @GetMapping("/supplier/{productId}")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
    public ResponseEntity<Product> getSupplierProduct(@PathVariable Long productId) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        System.out.println("🔍 Récupération produit fournisseur - Email: " + email);
        System.out.println("🔍 Récupération produit fournisseur - Product ID: " + productId);
        
        Supplier supplier = supplierRepository.findByEmail(email).orElse(null);
        if (supplier == null) {
            System.out.println("❌ Supplier non trouvé pour l'email: " + email);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            System.out.println("❌ Produit non trouvé: " + productId);
            return ResponseEntity.notFound().build();
        }
        
        if (product.getSupplier() == null || !product.getSupplier().getId().equals(supplier.getId())) {
            System.out.println("❌ Le produit n'appartient pas au fournisseur");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        System.out.println("✅ Produit fournisseur récupéré: " + product.getTitle());
        return ResponseEntity.ok(product);
    }
    
    /**
     * Endpoint temporaire pour créer un supplier (à supprimer en production)
     */
    @PostMapping("/create-supplier")
    public ResponseEntity<Supplier> createSupplier(@RequestBody CreateSupplierRequest request) {
        try {
            System.out.println("🔧 Création manuelle du supplier: " + request.getEmail());
            
            Supplier supplier = new Supplier();
            supplier.setEmail(request.getEmail());
            supplier.setSupplierName(request.getSupplierName());
            supplier.setRole(com.camoutech.multivendor.domain.USER_ROLE.ROLE_SUPPLIER);
            supplier.setMobile(request.getMobile());
            supplier.setPassword(""); // Pas de mot de passe pour les suppliers créés manuellement
            supplier.setAccountStatus(com.camoutech.multivendor.domain.AccountStatus.ACTIVE);
            
            supplier = supplierRepository.save(supplier);
            System.out.println("✅ Supplier créé manuellement: " + supplier.getSupplierName());
            
            return ResponseEntity.ok(supplier);
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la création manuelle du supplier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Récupérer les produits expédiés par le fournisseur et acceptés par l'entrepôt
     */
    @GetMapping("/delivered-and-received/supplier")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
    public ResponseEntity<List<Product>> getDeliveredAndReceivedProductsBySupplier() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            System.out.println("🔍 Récupération des produits livrés et reçus pour le fournisseur: " + email);
            
            Supplier supplier = supplierRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé pour l'email: " + email));
            
            System.out.println("✅ Fournisseur trouvé: " + supplier.getSupplierName() + " (ID: " + supplier.getId() + ")");
            
            List<Product> products = productRepository.findApprovedAndPendingProductsBySupplierRecu(supplier.getId());
            
            System.out.println("📦 Produits livrés et reçus trouvés: " + products.size());
            
            for (Product product : products) {
                System.out.println("  📦 Produit: " + product.getTitle() + 
                    " - Statut: " + product.getStatus() + 
                    " - Envoi: " + product.getShipmentStatus() + 
                    " - Réception: " + product.getReceptionStatus());
            }
            
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des produits livrés et reçus: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer tous les produits expédiés et acceptés par l'entrepôt (Admin)
     */
    @GetMapping("/delivered-and-received/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Product>> getAllDeliveredAndReceivedProducts() {
        try {
            System.out.println("🔍 Récupération de tous les produits livrés et reçus (Admin)");
            
            // Récupérer tous les produits avec les statuts appropriés
            List<Product> allProducts = productRepository.findAll();
            List<Product> deliveredAndReceivedProducts = allProducts.stream()
                    .filter(product -> 
                        product.getStatus() == Product.ProductStatus.APPROVED &&
                        product.getShipmentStatus() == Product.ShipmentStatus.SHIPPED &&
                        product.getReceptionStatus() == Product.ReceptionStatus.RECEIVED
                    )
                    .toList();
            
            System.out.println("📦 Total des produits livrés et reçus: " + deliveredAndReceivedProducts.size());
            
            for (Product product : deliveredAndReceivedProducts) {
                System.out.println("  📦 Produit: " + product.getTitle() + 
                    " - Fournisseur: " + (product.getSupplier() != null ? product.getSupplier().getSupplierName() : "N/A") +
                    " - Statut: " + product.getStatus() + 
                    " - Envoi: " + product.getShipmentStatus() + 
                    " - Réception: " + product.getReceptionStatus());
            }
            
            return ResponseEntity.ok(deliveredAndReceivedProducts);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération de tous les produits livrés et reçus: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer les statistiques des produits livrés et reçus
     */
    @GetMapping("/delivered-and-received/stats")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<DeliveredProductStats> getDeliveredProductStats() {
        try {
            System.out.println("📊 Récupération des statistiques des produits livrés et reçus");
            
            // TODO: Réactiver le service DeliveredProductService
            // DeliveredProductStats stats = deliveredProductService.getDeliveredProductStats(null);
            
            // Créer des statistiques temporaires
            DeliveredProductStats stats = new DeliveredProductStats(0, 0, 0);
            
            System.out.println("📊 Statistiques récupérées:");
            System.out.println("  - Total produits: " + stats.getTotalDeliveredProducts());
            System.out.println("  - Valeur totale: " + stats.getTotalValue());
            System.out.println("  - Quantité totale: " + stats.getTotalQuantity());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des statistiques: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Changer le statut d'expédition d'un produit (Fournisseur)
     */
    @PutMapping("/{productId}/shipment-status")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
    public ResponseEntity<Product> updateProductShipmentStatus(
            @PathVariable Long productId,
            @RequestBody UpdateShipmentStatusRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            System.out.println("🚚 Mise à jour du statut d'expédition pour le produit ID: " + productId);
            System.out.println("📧 Fournisseur: " + email);
            System.out.println("📦 Nouveau statut: " + request.getStatus());
            
            // Vérifier que le produit existe
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + productId));
            
            // Vérifier que le produit appartient au fournisseur connecté
            Supplier supplier = supplierRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé pour l'email: " + email));
            
            if (product.getSupplier() == null || !product.getSupplier().getId().equals(supplier.getId())) {
                System.err.println("❌ Le produit n'appartient pas au fournisseur connecté");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Vérifier que le produit est approuvé
            if (product.getStatus() != Product.ProductStatus.APPROVED) {
                System.err.println("❌ Le produit n'est pas approuvé. Statut actuel: " + product.getStatus());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            // Vérifier que le statut actuel permet le changement
            if (product.getShipmentStatus() == Product.ShipmentStatus.SHIPPED && 
                request.getStatus() == Product.ShipmentStatus.NOT_SHIPPED) {
                System.err.println("❌ Impossible de revenir au statut 'Non expédié' depuis 'Expédié'");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            // Mettre à jour le statut d'expédition
            Product.ShipmentStatus newStatus = Product.ShipmentStatus.valueOf(request.getStatus().toString());
            product.setShipmentStatus(newStatus);
            
            // Si le produit est expédié, mettre à jour la date de livraison
            if (newStatus == Product.ShipmentStatus.SHIPPED) {
                java.time.LocalDateTime deliveryDate = java.time.LocalDateTime.now();
                product.setDeliveryDate(deliveryDate);
                product.setUpdatedAt(deliveryDate);
                System.out.println("📦 Date de livraison définie: " + deliveryDate);
                System.out.println("🚚 Produit expédié le: " + deliveryDate.toLocalDate());
            }
            
            // Sauvegarder les modifications
            Product updatedProduct = productRepository.save(product);
            
            System.out.println("✅ Statut d'expédition mis à jour avec succès:");
            System.out.println("  📦 Produit: " + updatedProduct.getTitle());
            System.out.println("  🚚 Ancien statut: " + product.getShipmentStatus());
            System.out.println("  🚚 Nouveau statut: " + updatedProduct.getShipmentStatus());
            System.out.println("  👤 Fournisseur: " + supplier.getSupplierName());
            
            return ResponseEntity.ok(updatedProduct);
            
        } catch (RuntimeException e) {
            System.err.println("❌ Erreur de validation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la mise à jour du statut d'expédition: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lister les produits en attente de réception par l'entrepôt
     * (Produits APPROVED et SHIPPED)
     */
    @GetMapping("/pending-reception")
    @PreAuthorize("hasAuthority('ROLE_WAREHOUSE')")
    public ResponseEntity<List<Product>> getProductsPendingReception() {
        try {
            System.out.println("📦 Récupération des produits en attente de réception");
            
            List<Product> products = productRepository.findProductsPendingReception();
            
            System.out.println("✅ " + products.size() + " produits trouvés en attente de réception");
            
            // Debug: Afficher les informations du fournisseur pour chaque produit
            for (Product product : products) {
                System.out.println("🔍 Debug Produit ID: " + product.getId());
                System.out.println("  - Titre: " + product.getTitle());
                if (product.getSupplier() != null) {
                    System.out.println("  - Fournisseur ID: " + product.getSupplier().getId());
                    System.out.println("  - Fournisseur Email: " + product.getSupplier().getEmail());
                    System.out.println("  - Fournisseur Nom: " + product.getSupplier().getSupplierName());
                    if (product.getSupplier().getPickupAddress() != null) {
                        System.out.println("  - Adresse: " + product.getSupplier().getPickupAddress().getAddress());
                        System.out.println("  - Ville: " + product.getSupplier().getPickupAddress().getCity());
                    }
                    if (product.getSupplier().getBusinessDetails() != null) {
                        System.out.println("  - Business Address: " + product.getSupplier().getBusinessDetails().getBusinessAddress());
                    }
                } else {
                    System.out.println("  - ❌ Fournisseur NULL");
                }
                System.out.println("  ---");
            }
            
            return ResponseEntity.ok(products);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des produits en attente de réception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Debug: Vérifier les données d'un fournisseur
     */
    @GetMapping("/debug/supplier/{supplierId}")
    @PreAuthorize("hasAuthority('ROLE_WAREHOUSE')")
    public ResponseEntity<Supplier> debugSupplier(@PathVariable Long supplierId) {
        try {
            System.out.println("🔍 Debug Fournisseur ID: " + supplierId);
            
            Supplier supplier = supplierRepository.findById(supplierId).orElse(null);
            
            if (supplier != null) {
                System.out.println("✅ Fournisseur trouvé:");
                System.out.println("  - ID: " + supplier.getId());
                System.out.println("  - Email: " + supplier.getEmail());
                System.out.println("  - Nom: " + supplier.getSupplierName());
                System.out.println("  - Mobile: " + supplier.getMobile());
                
                if (supplier.getPickupAddress() != null) {
                    System.out.println("  - Adresse pickup: " + supplier.getPickupAddress().getAddress());
                    System.out.println("  - Ville pickup: " + supplier.getPickupAddress().getCity());
                } else {
                    System.out.println("  - ❌ Adresse pickup NULL");
                }
                
                if (supplier.getBusinessDetails() != null) {
                    System.out.println("  - Business Name: " + supplier.getBusinessDetails().getBusinessName());
                    System.out.println("  - Business Address: " + supplier.getBusinessDetails().getBusinessAddress());
                } else {
                    System.out.println("  - ❌ Business Details NULL");
                }
            } else {
                System.out.println("❌ Fournisseur non trouvé avec l'ID: " + supplierId);
            }
            
            return ResponseEntity.ok(supplier);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du debug du fournisseur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer le profil du fournisseur connecté
     */
    @GetMapping("/supplier/profile")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
    public ResponseEntity<Supplier> getSupplierProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            System.out.println("👤 Récupération du profil fournisseur: " + email);
            
            // Récupérer le fournisseur connecté
            Supplier supplier = supplierRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé pour l'email: " + email));
            
            System.out.println("✅ Profil fournisseur récupéré: " + supplier.getSupplierName());
            return ResponseEntity.ok(supplier);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération du profil fournisseur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Mettre à jour le profil du fournisseur connecté
     */
    @PutMapping("/supplier/update-profile")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
    public ResponseEntity<Supplier> updateSupplierProfile(@RequestBody SupplierProfileUpdateRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            System.out.println("👤 Mise à jour du profil fournisseur: " + email);
            
            // Récupérer le fournisseur connecté
            Supplier supplier = supplierRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé pour l'email: " + email));
            
            // Mettre à jour les informations de base
            if (request.getSupplierName() != null) {
                supplier.setSupplierName(request.getSupplierName());
            }
            if (request.getMobile() != null) {
                supplier.setMobile(request.getMobile());
            }
            
            // Mettre à jour l'adresse pickup
            if (request.getPickupAddress() != null) {
                if (supplier.getPickupAddress() == null) {
                    supplier.setPickupAddress(new Address());
                }
                Address pickupAddress = supplier.getPickupAddress();
                if (request.getPickupAddress().getAddress() != null) {
                    pickupAddress.setAddress(request.getPickupAddress().getAddress());
                }
                if (request.getPickupAddress().getCity() != null) {
                    pickupAddress.setCity(request.getPickupAddress().getCity());
                }
                if (request.getPickupAddress().getState() != null) {
                    pickupAddress.setState(request.getPickupAddress().getState());
                }
                if (request.getPickupAddress().getPinCode() != null) {
                    pickupAddress.setPinCode(request.getPickupAddress().getPinCode());
                }
            }
            
            // Mettre à jour les détails business
            if (request.getBusinessDetails() != null) {
                if (supplier.getBusinessDetails() == null) {
                    supplier.setBusinessDetails(new BusinessDetails());
                }
                BusinessDetails businessDetails = supplier.getBusinessDetails();
                if (request.getBusinessDetails().getBusinessName() != null) {
                    businessDetails.setBusinessName(request.getBusinessDetails().getBusinessName());
                }
                if (request.getBusinessDetails().getBusinessAddress() != null) {
                    businessDetails.setBusinessAddress(request.getBusinessDetails().getBusinessAddress());
                }
                if (request.getBusinessDetails().getBusinessEmail() != null) {
                    businessDetails.setBusinessEmail(request.getBusinessDetails().getBusinessEmail());
                }
                if (request.getBusinessDetails().getBusinessMobile() != null) {
                    businessDetails.setBusinessMobile(request.getBusinessDetails().getBusinessMobile());
                }
            }
            
            // Sauvegarder les modifications
            Supplier updatedSupplier = supplierRepository.save(supplier);
            
            System.out.println("✅ Profil fournisseur mis à jour avec succès:");
            System.out.println("  - Nom: " + updatedSupplier.getSupplierName());
            System.out.println("  - Mobile: " + updatedSupplier.getMobile());
            if (updatedSupplier.getPickupAddress() != null) {
                System.out.println("  - Adresse: " + updatedSupplier.getPickupAddress().getAddress());
                System.out.println("  - Ville: " + updatedSupplier.getPickupAddress().getCity());
            }
            if (updatedSupplier.getBusinessDetails() != null) {
                System.out.println("  - Business: " + updatedSupplier.getBusinessDetails().getBusinessName());
            }
            
            return ResponseEntity.ok(updatedSupplier);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la mise à jour du profil: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Corriger automatiquement les données du fournisseur ID 1
     */
    @PostMapping("/debug/fix-supplier-data")
    @PreAuthorize("hasAuthority('ROLE_WAREHOUSE')")
    public ResponseEntity<String> fixSupplierData() {
        try {
            System.out.println("🔧 Correction des données du fournisseur...");
            
            // Récupérer le fournisseur ID 1
            Supplier supplier = supplierRepository.findById(1L).orElse(null);
            
            if (supplier == null) {
                return ResponseEntity.badRequest().body("Fournisseur ID 1 non trouvé");
            }
            
            System.out.println("📝 Fournisseur trouvé: " + supplier.getSupplierName() + " (" + supplier.getEmail() + ")");
            
            // Créer et assigner l'adresse pickup si elle n'existe pas
            if (supplier.getPickupAddress() == null) {
                Address pickupAddress = new Address();
                pickupAddress.setAddress("Douala, Cameroun");
                pickupAddress.setCity("Douala");
                pickupAddress.setState("Littoral");
                pickupAddress.setPinCode("00000");
                supplier.setPickupAddress(pickupAddress);
                System.out.println("✅ Adresse pickup créée");
            }
            
            // Créer et assigner les détails business si ils n'existent pas
            if (supplier.getBusinessDetails() == null) {
                BusinessDetails businessDetails = new BusinessDetails();
                businessDetails.setBusinessName("Ferme " + supplier.getSupplierName());
                businessDetails.setBusinessAddress("Douala, Cameroun");
                businessDetails.setBusinessEmail(supplier.getEmail());
                businessDetails.setBusinessMobile(supplier.getMobile());
                supplier.setBusinessDetails(businessDetails);
                System.out.println("✅ Détails business créés");
            }
            
            // Sauvegarder les modifications
            Supplier updatedSupplier = supplierRepository.save(supplier);
            
            System.out.println("✅ Fournisseur mis à jour avec succès !");
            System.out.println("  - Nom: " + updatedSupplier.getSupplierName());
            System.out.println("  - Email: " + updatedSupplier.getEmail());
            if (updatedSupplier.getPickupAddress() != null) {
                System.out.println("  - Adresse: " + updatedSupplier.getPickupAddress().getAddress());
                System.out.println("  - Ville: " + updatedSupplier.getPickupAddress().getCity());
            }
            if (updatedSupplier.getBusinessDetails() != null) {
                System.out.println("  - Business: " + updatedSupplier.getBusinessDetails().getBusinessName());
            }
            
            return ResponseEntity.ok("✅ Données du fournisseur corrigées ! Nom: " + updatedSupplier.getSupplierName() + ", Localisation: " + updatedSupplier.getPickupAddress().getCity());
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la correction: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur: " + e.getMessage());
        }
    }

    /**
     * Mettre à jour automatiquement le fournisseur ID 1 (fabien)
     */
    @PostMapping("/debug/fix-supplier-info")
    @PreAuthorize("hasAuthority('ROLE_WAREHOUSE')")
    public ResponseEntity<String> fixSupplierInfo() {
        try {
            System.out.println("🔧 Correction automatique des informations du fournisseur...");
            
            // Récupérer le fournisseur ID 1 (fabien)
            Supplier supplier = supplierRepository.findById(1L).orElse(null);
            
            if (supplier == null) {
                return ResponseEntity.badRequest().body("Fournisseur ID 1 non trouvé");
            }
            
            System.out.println("📝 Fournisseur trouvé: " + supplier.getSupplierName() + " (" + supplier.getEmail() + ")");
            
            // Créer et assigner l'adresse pickup
            Address pickupAddress = new Address();
            pickupAddress.setAddress("Douala, Cameroun");
            pickupAddress.setCity("Douala");
            pickupAddress.setState("Littoral");
            pickupAddress.setPinCode("00000");
            supplier.setPickupAddress(pickupAddress);
            
            // Créer et assigner les détails business
            BusinessDetails businessDetails = new BusinessDetails();
            businessDetails.setBusinessName("Ferme " + supplier.getSupplierName());
            businessDetails.setBusinessAddress("Douala, Cameroun");
            businessDetails.setBusinessEmail(supplier.getEmail());
            supplier.setBusinessDetails(businessDetails);
            
            // Sauvegarder
            Supplier updatedSupplier = supplierRepository.save(supplier);
            
            System.out.println("✅ Fournisseur mis à jour avec succès !");
            System.out.println("  - Nom: " + updatedSupplier.getSupplierName());
            System.out.println("  - Adresse: " + updatedSupplier.getPickupAddress().getAddress());
            System.out.println("  - Ville: " + updatedSupplier.getPickupAddress().getCity());
            System.out.println("  - Business: " + updatedSupplier.getBusinessDetails().getBusinessName());
            
            return ResponseEntity.ok("✅ Informations du fournisseur corrigées ! Nom: " + updatedSupplier.getSupplierName() + ", Localisation: " + updatedSupplier.getPickupAddress().getCity());
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la correction: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur: " + e.getMessage());
        }
    }

    /**
     * Mettre à jour les informations d'un fournisseur existant
     */
    @PutMapping("/debug/update-supplier/{supplierId}")
    @PreAuthorize("hasAuthority('ROLE_WAREHOUSE')")
    public ResponseEntity<String> updateSupplierInfo(@PathVariable Long supplierId) {
        try {
            System.out.println("🔧 Mise à jour du fournisseur ID: " + supplierId);
            
            Supplier supplier = supplierRepository.findById(supplierId).orElse(null);
            
            if (supplier == null) {
                return ResponseEntity.badRequest().body("Fournisseur non trouvé avec l'ID: " + supplierId);
            }
            
            // Mettre à jour l'adresse pickup
            if (supplier.getPickupAddress() == null) {
                supplier.setPickupAddress(new Address());
            }
            supplier.getPickupAddress().setAddress("Douala, Cameroun");
            supplier.getPickupAddress().setCity("Douala");
            supplier.getPickupAddress().setState("Littoral");
            supplier.getPickupAddress().setPinCode("00000");
            
            // Mettre à jour les détails business
            if (supplier.getBusinessDetails() == null) {
                supplier.setBusinessDetails(new BusinessDetails());
            }
            supplier.getBusinessDetails().setBusinessName("Ferme " + supplier.getSupplierName());
            supplier.getBusinessDetails().setBusinessAddress("Douala, Cameroun");
            supplier.getBusinessDetails().setBusinessEmail(supplier.getEmail());
            
            // Sauvegarder les modifications
            Supplier updatedSupplier = supplierRepository.save(supplier);
            
            System.out.println("✅ Fournisseur mis à jour:");
            System.out.println("  - Nom: " + updatedSupplier.getSupplierName());
            System.out.println("  - Adresse: " + updatedSupplier.getPickupAddress().getAddress());
            System.out.println("  - Ville: " + updatedSupplier.getPickupAddress().getCity());
            System.out.println("  - Business: " + updatedSupplier.getBusinessDetails().getBusinessName());
            
            return ResponseEntity.ok("Fournisseur mis à jour avec succès ! ID: " + updatedSupplier.getId());
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la mise à jour du fournisseur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Créer des données de test avec des fournisseurs complets
     */
    @PostMapping("/debug/create-test-data")
    @PreAuthorize("hasAuthority('ROLE_WAREHOUSE')")
    public ResponseEntity<String> createTestData() {
        try {
            System.out.println("🧪 Création de données de test...");
            
            // Créer un fournisseur avec des données complètes
            Supplier testSupplier = new Supplier();
            testSupplier.setEmail("test-supplier@example.com");
            testSupplier.setSupplierName("Ferme Test Bio");
            testSupplier.setMobile("+237 6XX XX XX XX");
            
            // Créer une adresse
            Address address = new Address();
            address.setAddress("Douala, Cameroun");
            address.setCity("Douala");
            address.setState("Littoral");
            address.setPinCode("00000");
            testSupplier.setPickupAddress(address);
            
            // Créer des détails business
            BusinessDetails businessDetails = new BusinessDetails();
            businessDetails.setBusinessName("Ferme Test Bio");
            businessDetails.setBusinessAddress("Douala, Cameroun");
            businessDetails.setBusinessEmail("contact@fermetest.com");
            testSupplier.setBusinessDetails(businessDetails);
            
            // Sauvegarder le fournisseur
            Supplier savedSupplier = supplierRepository.save(testSupplier);
            System.out.println("✅ Fournisseur créé avec l'ID: " + savedSupplier.getId());
            
            // Créer un produit de test
            Product testProduct = new Product();
            testProduct.setTitle("Tomates Bio Test");
            testProduct.setDescription("Tomates biologiques de test");
            testProduct.setStatus(Product.ProductStatus.APPROVED);
            testProduct.setShipmentStatus(Product.ShipmentStatus.SHIPPED);
            testProduct.setReceptionStatus(Product.ReceptionStatus.PENDING);
            testProduct.setAdminRequestedQuantity(50);
            testProduct.setSupplierAvailableQuantity(100);
            testProduct.setSupplierPrice(1500.0);
            testProduct.setUnit("kg");
            testProduct.setSupplier(savedSupplier);
            
            // Sauvegarder le produit
            Product savedProduct = productRepository.save(testProduct);
            System.out.println("✅ Produit créé avec l'ID: " + savedProduct.getId());
            
            return ResponseEntity.ok("Données de test créées avec succès ! Fournisseur ID: " + savedSupplier.getId() + ", Produit ID: " + savedProduct.getId());
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création des données de test: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer les produits reçus par l'entrepôt (pour l'inventaire)
     */
    @GetMapping("/received-products")
    @PreAuthorize("hasAuthority('ROLE_WAREHOUSE')")
    public ResponseEntity<List<Product>> getReceivedProducts() {
        try {
            System.out.println("🏭 Récupération des produits reçus par l'entrepôt...");
            List<Product> products = productRepository.findReceivedProducts();
            System.out.println("📦 Produits reçus trouvés: " + products.size());
            
            for (Product product : products) {
                System.out.println("📦 Produit reçu: " + product.getTitle() + 
                    " | Statut: " + product.getStatus() + 
                    " | Expédition: " + product.getShipmentStatus() + 
                    " | Réception: " + product.getReceptionStatus() +
                    " | Date réception: " + product.getUpdatedAt());
            }
            
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des produits reçus: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Valider la réception d'un produit par l'entrepôt
     */
    @PutMapping("/{productId}/reception-status")
    @PreAuthorize("hasAuthority('ROLE_WAREHOUSE')")
    public ResponseEntity<Product> updateProductReceptionStatus(
            @PathVariable Long productId,
            @RequestBody UpdateReceptionStatusRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            System.out.println("📦 Mise à jour du statut de réception pour le produit ID: " + productId);
            System.out.println("🏭 Entrepôt: " + email);
            System.out.println("📋 Nouveau statut: " + request.getStatus());
            
            // Vérifier que le produit existe
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + productId));
            
            // Vérifier que le produit est en statut SHIPPED
            if (product.getShipmentStatus() != Product.ShipmentStatus.SHIPPED) {
                System.err.println("❌ Le produit n'est pas expédié. Statut actuel: " + product.getShipmentStatus());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            // Vérifier que le produit est approuvé
            if (product.getStatus() != Product.ProductStatus.APPROVED) {
                System.err.println("❌ Le produit n'est pas approuvé. Statut actuel: " + product.getStatus());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            // Mettre à jour le statut de réception
            Product.ReceptionStatus newStatus = Product.ReceptionStatus.valueOf(request.getStatus().toString());
            product.setReceptionStatus(newStatus);
            
            // Si la réception est validée, mettre à jour le statut d'envoi et la date de réception
            if (newStatus == Product.ReceptionStatus.RECEIVED) {
                product.setShipmentStatus(Product.ShipmentStatus.DELIVERED);
                // Mettre à jour la date de réception avec la date actuelle
                java.time.LocalDateTime receptionDate = java.time.LocalDateTime.now();
                product.setReceivedAt(receptionDate);
                product.setUpdatedAt(receptionDate);
                
                // IMPORTANT: Initialiser le stock d'entrepôt avec la quantité livrée
                // La quantité livrée est stockée dans adminRequestedQuantity ou supplierAvailableQuantity
                int quantityToDeliver = product.getAdminRequestedQuantity() > 0 ? 
                    product.getAdminRequestedQuantity() : product.getSupplierAvailableQuantity();
                
                if (quantityToDeliver > 0) {
                    // Stocker la quantité livrée
                    product.setDeliveredQuantity(quantityToDeliver);
                    
                    // Initialiser le stock d'entrepôt avec la quantité livrée
                    product.setWarehouseQuantity(quantityToDeliver);
                    product.setStockQuantity(quantityToDeliver);
                    
                    System.out.println("📦 Stock d'entrepôt initialisé:");
                    System.out.println("  🏭 Produit: " + product.getTitle());
                    System.out.println("  📊 Quantité livrée: " + quantityToDeliver);
                    System.out.println("  📦 Stock entrepôt: " + product.getWarehouseQuantity());
                    System.out.println("  📦 Stock total: " + product.getStockQuantity());
                }
                
                // NE PAS modifier la deliveryDate - elle doit rester la vraie date d'expédition
                // La deliveryDate est définie lors de l'expédition par le fournisseur
                // La receivedAt est définie lors de la validation par l'entrepôt
                
                System.out.println("📅 Date de réception définie: " + receptionDate);
                System.out.println("📦 Produit reçu le: " + receptionDate.toLocalDate());
                System.out.println("🚚 Date de livraison originale: " + product.getDeliveryDate());
                System.out.println("🏭 Réception confirmée par l'entrepôt le: " + receptionDate.toLocalDate());
                System.out.println("⏱️ Délai de traitement: " + 
                    (product.getDeliveryDate() != null ? 
                        java.time.Duration.between(product.getDeliveryDate(), receptionDate).toMinutes() + " minutes" : 
                        "Non calculable"));
            }
            
            // Sauvegarder les modifications
            Product updatedProduct = productRepository.save(product);
            
            System.out.println("✅ Statut de réception mis à jour avec succès:");
            System.out.println("  📦 Produit: " + updatedProduct.getTitle());
            System.out.println("  📋 Ancien statut: " + product.getReceptionStatus());
            System.out.println("  📋 Nouveau statut: " + updatedProduct.getReceptionStatus());
            System.out.println("  🚚 Statut d'envoi: " + updatedProduct.getShipmentStatus());
            
            return ResponseEntity.ok(updatedProduct);
            
        } catch (RuntimeException e) {
            System.err.println("❌ Erreur de validation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la mise à jour du statut de réception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Méthode utilitaire pour calculer le pourcentage de remise
    private int calculateDiscountPercentage(int mrpPrice, int sellingPrice) {
        // Si le prix MRP est 0 ou négatif, pas de remise
        if (mrpPrice <= 0) {
            return 0;
        }
        
        // Si le prix de vente est supérieur au prix MRP, c'est une augmentation
        if (sellingPrice >= mrpPrice) {
            return 0; // Pas de remise, prix normal ou augmentation
        }
        
        double discount = mrpPrice - sellingPrice;
        double discountPercentage = (discount / mrpPrice) * 100;
        return (int) discountPercentage;
    }

    // Classe interne pour la requête
    public static class CreateSupplierRequest {
        private String email;
        private String supplierName;
        private String mobile;

        // Getters et setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getSupplierName() { return supplierName; }
        public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }
    }

    // ==================== ENDPOINTS TABLEAU DE BORD FOURNISSEUR ====================

    /**
     * Récupérer les statistiques complètes du tableau de bord du fournisseur
     */
    @GetMapping("/supplier/dashboard/stats")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
    public ResponseEntity<SupplierDashboardStats> getSupplierDashboardStats() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            System.out.println("📊 Récupération des statistiques du tableau de bord pour: " + email);
            
            // Récupérer l'ID du fournisseur à partir de l'email
            Supplier supplier = supplierRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec l'email: " + email));
            
            SupplierDashboardStats stats = supplierDashboardService.getSupplierDashboardStats(supplier.getId());
            
            System.out.println("📊 Statistiques récupérées:");
            System.out.println("  - Total produits: " + stats.getTotalProducts());
            System.out.println("  - Produits approuvés: " + stats.getApprovedProducts());
            System.out.println("  - Revenus totaux: " + stats.getTotalRevenue());
            System.out.println("  - Montant reçu: " + stats.getReceivedAmount());
            System.out.println("  - Montant en attente: " + stats.getPendingAmount());
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des statistiques: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer les revenus totaux du fournisseur
     */
    @GetMapping("/supplier/dashboard/revenue")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
    public ResponseEntity<Map<String, Object>> getSupplierRevenue() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            System.out.println("💰 Récupération des revenus pour: " + email);
            
            Supplier supplier = supplierRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec l'email: " + email));
            
            java.math.BigDecimal totalRevenue = supplierDashboardService.getTotalRevenue(supplier.getId());
            java.math.BigDecimal receivedAmount = supplierDashboardService.getReceivedAmount(supplier.getId());
            java.math.BigDecimal pendingAmount = supplierDashboardService.getPendingAmount(supplier.getId());
            java.math.BigDecimal thisMonthRevenue = supplierDashboardService.getThisMonthRevenue(supplier.getId());
            java.math.BigDecimal lastMonthRevenue = supplierDashboardService.getLastMonthRevenue(supplier.getId());
            
            Map<String, Object> revenueData = Map.of(
                "totalRevenue", totalRevenue,
                "receivedAmount", receivedAmount,
                "pendingAmount", pendingAmount,
                "thisMonthRevenue", thisMonthRevenue,
                "lastMonthRevenue", lastMonthRevenue
            );
            
            System.out.println("💰 Données de revenus récupérées:");
            System.out.println("  - Revenus totaux: " + totalRevenue);
            System.out.println("  - Montant reçu: " + receivedAmount);
            System.out.println("  - Montant en attente: " + pendingAmount);
            
            return ResponseEntity.ok(revenueData);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des revenus: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer les statistiques de performance du fournisseur
     */
    @GetMapping("/supplier/dashboard/performance")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
    public ResponseEntity<Map<String, Object>> getSupplierPerformance() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            System.out.println("📈 Récupération des statistiques de performance pour: " + email);
            
            Supplier supplier = supplierRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec l'email: " + email));
            
            SupplierDashboardStats stats = supplierDashboardService.getSupplierDashboardStats(supplier.getId());
            
            Map<String, Object> performanceData = Map.of(
                "averageProcessingTime", stats.getAverageProcessingTime(),
                "totalOrders", stats.getTotalOrders(),
                "completedOrders", stats.getCompletedOrders(),
                "completionRate", stats.getCompletionRate(),
                "topSellingProduct", stats.getTopSellingProduct(),
                "topSellingQuantity", stats.getTopSellingQuantity(),
                "lastActivity", stats.getLastActivity(),
                "accountCreated", stats.getAccountCreated()
            );
            
            System.out.println("📈 Données de performance récupérées:");
            System.out.println("  - Taux de complétion: " + stats.getCompletionRate() + "%");
            System.out.println("  - Produit le plus vendu: " + stats.getTopSellingProduct());
            System.out.println("  - Temps moyen de traitement: " + stats.getAverageProcessingTime() + " heures");
            
            return ResponseEntity.ok(performanceData);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des statistiques de performance: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer les images d'un produit spécifique
     */
    @GetMapping("/{productId}/images")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductImage>> getProductImages(@PathVariable Long productId) {
        try {
            System.out.println("🖼️ Récupération des images pour le produit: " + productId);
            
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + productId));
            
            System.out.println("📦 Produit trouvé: " + product.getTitle());
            
            List<ProductImage> images = product.getImages();
            
            System.out.println("🖼️ Images trouvées: " + images.size());
            if (images.isEmpty()) {
                System.out.println("⚠️ Aucune image trouvée pour ce produit");
            } else {
                // Construire des URLs complètes pour les images
                for (ProductImage image : images) {
                    String fullUrl = "http://localhost:3026" + image.getImageUrl();
                    image.setImageUrl(fullUrl);
                    System.out.println("  - Image: " + image.getImageName() + " (URL: " + image.getImageUrl() + ")");
                }
            }
            
            return ResponseEntity.ok(images);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des images: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Ajouter une image à un produit
     */
    @PostMapping("/{productId}/images")
    @PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
    public ResponseEntity<ProductImage> addProductImage(
            @PathVariable Long productId,
            @RequestParam("image") MultipartFile image) {
        try {
            System.out.println("📸 Ajout d'une image au produit: " + productId);
            
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + productId));
            
            // Sauvegarder le fichier physiquement
            String uploadDir = "C:\\Users\\ELITEBOOK 850 G6\\Desktop\\ecommerce1\\multivendor-fullstack\\multivendor\\src\\main\\resources\\uploads\\products\\";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
                System.out.println("📁 Répertoire créé: " + uploadDir);
            }
            
            // Générer un nom de fichier unique
            String originalFilename = image.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // Créer un nom unique avec timestamp et UUID
            long timestamp = System.currentTimeMillis();
            String uniqueId = java.util.UUID.randomUUID().toString().substring(0, 8);
            String filename = "product_" + timestamp + "_" + uniqueId + fileExtension;
            
            // Créer une nouvelle image
            ProductImage productImage = new ProductImage();
            productImage.setImageName(filename); // Utiliser le nom unique généré
            productImage.setImageType(image.getContentType());
            productImage.setFileSize(image.getSize());
            productImage.setProduct(product);
            productImage.setAltText("Image du produit " + product.getTitle());
            productImage.setDescription("Image uploadée pour " + product.getTitle());
            
            String relativeUrl = "/uploads/products/" + filename;
            String filePath = uploadDir + filename;
            
            System.out.println("💾 Tentative de sauvegarde vers: " + filePath);
            
            // Sauvegarder le fichier
            try {
                image.transferTo(new File(filePath));
                System.out.println("💾 Fichier sauvegardé: " + filePath);
            } catch (IOException e) {
                System.err.println("❌ Erreur lors de la sauvegarde du fichier: " + e.getMessage());
                throw new RuntimeException("Erreur lors de la sauvegarde de l'image", e);
            }
            
            productImage.setImageUrl(relativeUrl);
            productImage.setWidth(800); // Valeur par défaut
            productImage.setHeight(600); // Valeur par défaut
            
            // Ajouter l'image au produit
            product.getImages().add(productImage);
            productRepository.save(product);
            
            // Construire l'URL complète pour la réponse
            String fullUrl = "http://localhost:3026" + relativeUrl;
            productImage.setImageUrl(fullUrl);
            
            System.out.println("✅ Image ajoutée avec succès: " + productImage.getImageName());
            
            return ResponseEntity.ok(productImage);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'ajout de l'image: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Initialiser le stock d'entrepôt pour tous les produits reçus
     * Cette méthode corrige les produits existants qui n'ont pas leur warehouseQuantity initialisé
     */
    @PostMapping("/initialize-warehouse-stock")
    @PreAuthorize("hasAuthority('ROLE_WAREHOUSE')")
    public ResponseEntity<Map<String, Object>> initializeWarehouseStock() {
        System.out.println("🏭 Initialisation du stock d'entrepôt pour tous les produits reçus");
        
        try {
            // Récupérer tous les produits reçus par l'entrepôt
            List<Product> receivedProducts = productRepository.findReceivedProducts();
            int updatedCount = 0;
            
            for (Product product : receivedProducts) {
                // Si le warehouseQuantity est 0, l'initialiser avec la quantité livrée
                if (product.getWarehouseQuantity() == 0) {
                    int quantityToDeliver = product.getAdminRequestedQuantity() > 0 ? 
                        product.getAdminRequestedQuantity() : product.getSupplierAvailableQuantity();
                    
                    if (quantityToDeliver > 0) {
                        // Stocker la quantité livrée
                        product.setDeliveredQuantity(quantityToDeliver);
                        
                        // Initialiser le stock d'entrepôt
                        product.setWarehouseQuantity(quantityToDeliver);
                        product.setStockQuantity(quantityToDeliver);
                        productRepository.save(product);
                        updatedCount++;
                        
                        System.out.println("📦 Stock initialisé pour " + product.getTitle() + 
                            ": " + quantityToDeliver + " unités");
                    }
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Stock d'entrepôt initialisé avec succès");
            response.put("totalProducts", receivedProducts.size());
            response.put("updatedProducts", updatedCount);
            response.put("success", true);
            
            System.out.println("✅ Initialisation terminée: " + updatedCount + 
                " produits mis à jour sur " + receivedProducts.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation du stock d'entrepôt: " + e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors de l'initialisation");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}