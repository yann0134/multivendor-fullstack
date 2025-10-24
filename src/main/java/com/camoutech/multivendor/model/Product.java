/**
 * Produit agricole (animal ou végétal)
 * Created by camoutech
 * Date :13/10/2024
 * Time :02:04
 * Project Name :multivendor
 */

package com.camoutech.multivendor.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title; // Nom du produit

    private String description;

    private int mrpPrice; // Prix de marché recommandé

    private int sellingPrice; // Prix de vente
    
    @Column(name = "price")
    private int price = 0; // Prix général (peut être utilisé comme prix de base)

    private int discountPercent;

    private int quantity; // Quantité disponible

    private String color; // Couleur du produit

    private int numRatings; // Nombre d'évaluations

    // Informations agricoles spécifiques
    private String origin; // Origine du produit (région, ferme)
    
    private String farmingMethod; // Méthode de culture/élevage (bio, conventionnel, etc.)
    
    private String season; // Saison de production
    
    private String unit; // Unité de vente (kg, pièce, litre, etc.)
    
    private double weight; // Poids moyen
    
    private String storageConditions; // Conditions de stockage
    
    private LocalDateTime harvestDate; // Date de récolte
    
    private LocalDateTime expiryDate; // Date d'expiration

    @Column(name = "is_organic")
    private boolean organic = false; // Produit bio

    @Column(name = "is_local")
    private boolean local = false; // Produit local
    
    @Column(name = "is_fresh")
    private boolean fresh = true; // Produit frais
    
    @Column(name = "fresh")
    @JsonIgnore
    private Boolean freshField = true; // Champ fresh supplémentaire
    
    private String nutritionalInfo; // Informations nutritionnelles
    
    private String allergens; // Allergènes présents

    // Images du produit
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductImage> images = new ArrayList<>();

    // Relations
    @ManyToOne
    private ProductCategory category; // Catégorie principale

    @ManyToOne
    @JsonIgnore
    private ProductSubCategory subCategory; // Sous-catégorie

    @ManyToOne
    @JsonIgnore
    private Seller seller; // Vendeur (fermier)

    @ManyToOne
    @JsonIgnoreProperties({"suppliedProducts", "supplyOrders"})
    private Supplier supplier; // Fournisseur (fermier)

    private double supplierPrice; // Prix d'achat au fournisseur

    private int warehouseQuantity; // Stock en entrepôt

    private int reservedQuantity; // Stock réservé
    
    @Column(name = "stock_quantity")
    private int stockQuantity = 0; // Quantité en stock
    
    // Gestion des stocks fournisseur/admin
    private int supplierAvailableQuantity = 0; // Quantité disponible chez le fournisseur
    private int adminRequestedQuantity = 0; // Quantité demandée par l'admin
    private boolean stockNegotiationPending = false; // Négociation de stock en cours
    
    // Gestion de l'envoi et de la réception
    @Enumerated(EnumType.STRING)
    private ShipmentStatus shipmentStatus = ShipmentStatus.NOT_SHIPPED; // Statut d'envoi par le fournisseur
    
    @Enumerated(EnumType.STRING)
    private ReceptionStatus receptionStatus = ReceptionStatus.PENDING; // Statut de réception par l'entrepôt

    private LocalDateTime createdAt = LocalDateTime.now();

    private String sizes; // Tailles disponibles

    // Statut de validation du produit
    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.PENDING_APPROVAL; // Statut par défaut

    private String rejectionReason; // Raison du rejet si applicable

    private LocalDateTime statusUpdatedAt; // Date de dernière mise à jour du statut
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Date de dernière mise à jour générale
    
    @Column(name = "received_at")
    private LocalDateTime receivedAt; // Date de réception par l'entrepôt
    
    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate; // Date de livraison (expédition par le fournisseur)
    
    @PrePersist
    @PreUpdate
    private void updateTimestamps() {
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    private String reviewedBy; // Email de l'administrateur qui a validé/rejeté

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
    
    // Méthodes utilitaires
    public boolean isAvailable() {
        return quantity > 0 && isActive();
    }
    
    public boolean isActive() {
        return expiryDate == null || expiryDate.isAfter(LocalDateTime.now());
    }
    
    @JsonIgnore
    public String getFormattedPrice() {
        return String.format("%.2f FCFA", sellingPrice);
    }
    
    // Méthodes pour les images
    public ProductImage getMainImage() {
        return images.stream()
                .filter(ProductImage::isMain)
                .findFirst()
                .orElse(images.isEmpty() ? null : images.get(0));
    }
    
    public List<ProductImage> getActiveImages() {
        return images.stream()
                .filter(ProductImage::getIsActive)
                .sorted((a, b) -> a.getDisplayOrder().compareTo(b.getDisplayOrder()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    public boolean hasImages() {
        return !images.isEmpty();
    }
    
    public int getImageCount() {
        return images.size();
    }

    // Enum pour les statuts de produit
    public enum ProductStatus {
        PENDING_APPROVAL("En attente d'approbation"),
        APPROVED("Approuvé"),
        REJECTED("Rejeté"),
        SUSPENDED("Suspendu"),
        DRAFT("Brouillon");

        private final String description;

        ProductStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Enum pour le statut d'envoi par le fournisseur
    public enum ShipmentStatus {
        NOT_SHIPPED("Non expédié"),
        PREPARING("En préparation"),
        SHIPPED("Expédié"),
        IN_TRANSIT("En transit"),
        DELIVERED("Livré à l'entrepôt"),
        FAILED_DELIVERY("Échec de livraison");

        private final String description;

        ShipmentStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Enum pour le statut de réception par l'entrepôt
    public enum ReceptionStatus {
        PENDING("En attente de réception"),
        RECEIVED("Reçu"),
        PARTIALLY_RECEIVED("Partiellement reçu"),
        REJECTED("Rejeté"),
        DAMAGED("Endommagé"),
        QUANTITY_MISMATCH("Écart de quantité");

        private final String description;

        ReceptionStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
