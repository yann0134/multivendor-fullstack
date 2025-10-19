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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
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

    private int discountPercent;

    private int quantity; // Quantité disponible

    private String color; // Couleur du produit

    @ElementCollection
    private List<String> images = new ArrayList<>();

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
    
    private boolean isOrganic = false; // Produit bio
    
    private boolean isLocal = false; // Produit local
    
    private String nutritionalInfo; // Informations nutritionnelles
    
    private String allergens; // Allergènes présents

    // Relations
    @ManyToOne
    private ProductCategory category; // Catégorie principale

    @ManyToOne
    private ProductSubCategory subCategory; // Sous-catégorie

    @ManyToOne
    private Seller seller; // Vendeur (fermier)

    @ManyToOne
    private Supplier supplier; // Fournisseur (fermier)

    private double supplierPrice; // Prix d'achat au fournisseur

    private int warehouseQuantity; // Stock en entrepôt

    private int reservedQuantity; // Stock réservé

    private LocalDateTime createdAt = LocalDateTime.now();

    private String sizes; // Tailles disponibles

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
    
    // Méthodes utilitaires
    public boolean isAvailable() {
        return quantity > 0 && isActive();
    }
    
    public boolean isActive() {
        return expiryDate == null || expiryDate.isAfter(LocalDateTime.now());
    }
    
    public String getFormattedPrice() {
        return String.format("%.2f FCFA", sellingPrice);
    }
}
