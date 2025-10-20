/**
 * Service pour la gestion des images de produits
 * Created by camoutech
 * Date :19/10/2024
 * Time :18:00
 * Project Name :multivendor
 */

package com.camoutech.multivendor.service;

import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {
    
    // Upload d'une image pour un produit
    ProductImage uploadProductImage(Long productId, MultipartFile file, String altText, String description);
    
    // Upload de plusieurs images pour un produit
    List<ProductImage> uploadMultipleProductImages(Long productId, MultipartFile[] files, String[] altTexts, String[] descriptions);
    
    // Définir une image comme principale
    ProductImage setMainImage(Long productId, Long imageId);
    
    // Réorganiser l'ordre des images
    List<ProductImage> reorderImages(Long productId, List<Long> imageIds);
    
    // Supprimer une image
    void deleteProductImage(Long imageId);
    
    // Supprimer toutes les images d'un produit
    void deleteAllProductImages(Long productId);
    
    // Obtenir toutes les images d'un produit
    List<ProductImage> getProductImages(Long productId);
    
    // Obtenir l'image principale d'un produit
    ProductImage getMainProductImage(Long productId);
    
    // Obtenir les images actives d'un produit
    List<ProductImage> getActiveProductImages(Long productId);
    
    // Mettre à jour les métadonnées d'une image
    ProductImage updateImageMetadata(Long imageId, String altText, String description);
    
    // Générer une miniature
    String generateThumbnail(String imagePath);
    
    // Valider un fichier image
    boolean validateImageFile(MultipartFile file);
    
    // Obtenir l'URL complète d'une image
    String getImageUrl(ProductImage image);
    
    // Obtenir l'URL de la miniature
    String getThumbnailUrl(ProductImage image);
}
