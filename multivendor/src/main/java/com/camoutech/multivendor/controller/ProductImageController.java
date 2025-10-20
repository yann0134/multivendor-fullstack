/**
 * Contrôleur pour la gestion des images de produits
 * Created by camoutech
 * Date :19/10/2024
 * Time :18:05
 * Project Name :multivendor
 */

package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.model.ProductImage;
import com.camoutech.multivendor.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product-images")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductImageController {

    private final ProductImageService productImageService;

    // Upload d'une image pour un produit
    @PostMapping("/upload/{productId}")
    public ResponseEntity<Map<String, Object>> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "altText", required = false) String altText,
            @RequestParam(value = "description", required = false) String description) {
        
        try {
            ProductImage uploadedImage = productImageService.uploadProductImage(productId, file, altText, description);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Image uploadée avec succès");
            response.put("image", uploadedImage);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de l'upload: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Upload de plusieurs images pour un produit
    @PostMapping("/upload-multiple/{productId}")
    public ResponseEntity<Map<String, Object>> uploadMultipleProductImages(
            @PathVariable Long productId,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "altTexts", required = false) String[] altTexts,
            @RequestParam(value = "descriptions", required = false) String[] descriptions) {
        
        try {
            List<ProductImage> uploadedImages = productImageService.uploadMultipleProductImages(
                productId, files, altTexts, descriptions);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", uploadedImages.size() + " images uploadées avec succès");
            response.put("images", uploadedImages);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de l'upload: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Obtenir toutes les images d'un produit
    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getProductImages(@PathVariable Long productId) {
        try {
            List<ProductImage> images = productImageService.getProductImages(productId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("images", images);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération des images: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Obtenir l'image principale d'un produit
    @GetMapping("/product/{productId}/main")
    public ResponseEntity<Map<String, Object>> getMainProductImage(@PathVariable Long productId) {
        try {
            ProductImage mainImage = productImageService.getMainProductImage(productId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mainImage", mainImage);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la récupération de l'image principale: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Définir une image comme principale
    @PutMapping("/{imageId}/set-main")
    public ResponseEntity<Map<String, Object>> setMainImage(@PathVariable Long imageId) {
        try {
            ProductImage mainImage = productImageService.setMainImage(
                productImageService.getProductImages(
                    productImageService.getProductImages(imageId).get(0).getProduct().getId()
                ).get(0).getProduct().getId(), imageId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Image définie comme principale");
            response.put("mainImage", mainImage);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la définition de l'image principale: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Réorganiser les images
    @PutMapping("/reorder/{productId}")
    public ResponseEntity<Map<String, Object>> reorderImages(
            @PathVariable Long productId,
            @RequestBody List<Long> imageIds) {
        try {
            List<ProductImage> reorderedImages = productImageService.reorderImages(productId, imageIds);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Ordre des images mis à jour");
            response.put("images", reorderedImages);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la réorganisation: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Mettre à jour les métadonnées d'une image
    @PutMapping("/{imageId}/metadata")
    public ResponseEntity<Map<String, Object>> updateImageMetadata(
            @PathVariable Long imageId,
            @RequestParam String altText,
            @RequestParam String description) {
        try {
            ProductImage updatedImage = productImageService.updateImageMetadata(imageId, altText, description);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Métadonnées mises à jour");
            response.put("image", updatedImage);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la mise à jour: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Supprimer une image
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Map<String, Object>> deleteProductImage(@PathVariable Long imageId) {
        try {
            productImageService.deleteProductImage(imageId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Image supprimée avec succès");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la suppression: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Supprimer toutes les images d'un produit
    @DeleteMapping("/product/{productId}/all")
    public ResponseEntity<Map<String, Object>> deleteAllProductImages(@PathVariable Long productId) {
        try {
            productImageService.deleteAllProductImages(productId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Toutes les images ont été supprimées");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la suppression: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
