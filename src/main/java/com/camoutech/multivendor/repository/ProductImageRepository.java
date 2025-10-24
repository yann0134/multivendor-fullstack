/**
 * Created by camoutech
 * Date :19/10/2024
 * Time :17:58
 * Project Name :multivendor
 */

package com.camoutech.multivendor.repository;

import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    
    // Trouver toutes les images d'un produit
    List<ProductImage> findByProduct(Product product);
    
    // Trouver toutes les images d'un produit par ID
    List<ProductImage> findByProductId(Long productId);
    
    // Trouver toutes les images d'un produit triées par ordre d'affichage
    List<ProductImage> findByProductOrderByDisplayOrderAsc(Product product);
    
    // Trouver toutes les images actives d'un produit
    List<ProductImage> findByProductAndIsActiveTrueOrderByDisplayOrderAsc(Product product);
    
    // Trouver toutes les images actives d'un produit par ID
    List<ProductImage> findByProductIdAndIsActiveTrueOrderByDisplayOrderAsc(Long productId);
    
    // Trouver l'image principale d'un produit
    Optional<ProductImage> findByProductAndIsMainImageTrue(Product product);
    
    // Trouver les images principales d'un produit par ID
    List<ProductImage> findByProductIdAndIsMainImageTrue(Long productId);
    
    // Compter les images d'un produit
    long countByProduct(Product product);
    
    // Trouver les images par type
    List<ProductImage> findByProductAndImageTypeOrderByDisplayOrderAsc(Product product, String imageType);
    
    // Trouver les images par taille (plus grandes qu'une certaine taille)
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product = :product AND pi.width >= :minWidth AND pi.height >= :minHeight")
    List<ProductImage> findByProductAndSizeGreaterThan(@Param("product") Product product, 
                                                      @Param("minWidth") Integer minWidth, 
                                                      @Param("minHeight") Integer minHeight);
    
    // Supprimer toutes les images d'un produit
    void deleteByProduct(Product product);
    
    // Trouver les images par nom
    Optional<ProductImage> findByImageName(String imageName);
    
    // Vérifier si un produit a des images
    boolean existsByProduct(Product product);
    
    // Trouver les images principales de plusieurs produits
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product IN :products AND pi.isMainImage = true")
    List<ProductImage> findMainImagesByProducts(@Param("products") List<Product> products);
}
