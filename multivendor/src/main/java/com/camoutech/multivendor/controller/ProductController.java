package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.exceptions.ProductException;
import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.ProductCategory;
import com.camoutech.multivendor.model.ProductSubCategory;
import com.camoutech.multivendor.model.Supplier;
import com.camoutech.multivendor.model.User;
import com.camoutech.multivendor.repository.ProductRepository;
import com.camoutech.multivendor.repository.ProductCategoryRepository;
import com.camoutech.multivendor.repository.ProductSubCategoryRepository;
import com.camoutech.multivendor.repository.UserRepository;
import com.camoutech.multivendor.repository.SupplierRepository;
import com.camoutech.multivendor.repository.SupplyOrderRepository;
import com.camoutech.multivendor.request.CreateProductRequest;
import com.camoutech.multivendor.service.impl.ProductServiceImpl;
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

import java.util.List;


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
    private final ProductServiceImpl productService;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final SupplyOrderRepository supplyOrderRepository;

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
        Page<Product> products = productRepository.findByOrganicTrue(pageable);
        
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
        Page<Product> products = productRepository.findByLocalTrue(pageable);
        
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
    
    // Méthode utilitaire pour calculer le pourcentage de remise
    private int calculateDiscountPercentage(int mrpPrice, int sellingPrice) {
        if (mrpPrice <= 0) {
            return 0;
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
}