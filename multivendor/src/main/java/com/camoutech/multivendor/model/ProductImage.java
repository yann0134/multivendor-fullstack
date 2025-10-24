/**
 * Created by camoutech
 * Date :19/10/2024
 * Time :17:55
 * Project Name :multivendor
 */

package com.camoutech.multivendor.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "product_images")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String imageName;

    @Column(nullable = false)
    private String imageType; // JPG, PNG, WebP

    @Column(nullable = false)
    private Long fileSize; // Taille en bytes

    @Column(nullable = false)
    private Integer width;

    @Column(nullable = false)
    private Integer height;

    @Column(nullable = false)
    private Boolean isMainImage = false; // Image principale du produit

    @Column(nullable = false)
    private Integer displayOrder = 0; // Ordre d'affichage

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;

    @Column(nullable = false)
    private String altText; // Texte alternatif pour l'accessibilité

    @Column(length = 500)
    private String description; // Description de l'image

    @Column(nullable = false)
    private Boolean isActive = true;

    // Méthodes utilitaires
    public String getImagePath() {
        return "/uploads/products/" + this.imageName;
    }

    public String getThumbnailPath() {
        return "/uploads/products/thumbnails/" + this.imageName;
    }

    public Boolean isMain() {
        return this.isMainImage;
    }
}
