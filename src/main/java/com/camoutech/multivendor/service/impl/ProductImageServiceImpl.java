/**
 * Implémentation du service de gestion des images de produits
 * Created by camoutech
 * Date :19/10/2024
 * Time :18:02
 * Project Name :multivendor
 */

package com.camoutech.multivendor.service.impl;

import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.ProductImage;
import com.camoutech.multivendor.repository.ProductImageRepository;
import com.camoutech.multivendor.repository.ProductRepository;
import com.camoutech.multivendor.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    @Value("${app.upload.path:/uploads}")
    private String uploadPath;

    @Value("${app.upload.max-size:5242880}") // 5MB
    private long maxFileSize;

    @Override
    public ProductImage uploadProductImage(Long productId, MultipartFile file, String altText, String description) {
        // Validation du fichier
        if (!validateImageFile(file)) {
            throw new IllegalArgumentException("Fichier image invalide");
        }

        // Vérifier que le produit existe
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));

        try {
            // Générer un nom de fichier unique
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Créer le répertoire de destination
            Path uploadDir = Paths.get(uploadPath, "products");
            Files.createDirectories(uploadDir);

            // Sauvegarder le fichier
            Path filePath = uploadDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Obtenir les dimensions de l'image
            BufferedImage image = ImageIO.read(file.getInputStream());
            int width = image.getWidth();
            int height = image.getHeight();

            // Créer l'entité ProductImage
            ProductImage productImage = new ProductImage();
            productImage.setImageUrl("/uploads/products/" + uniqueFilename);
            productImage.setImageName(uniqueFilename);
            productImage.setImageType(getFileExtension(originalFilename).substring(1).toUpperCase());
            productImage.setFileSize(file.getSize());
            productImage.setWidth(width);
            productImage.setHeight(height);
            productImage.setProduct(product);
            productImage.setAltText(altText != null ? altText : "");
            productImage.setDescription(description != null ? description : "");

            // Si c'est la première image, la définir comme principale
            if (productImageRepository.countByProduct(product) == 0) {
                productImage.setIsMainImage(true);
            }

            // Définir l'ordre d'affichage
            productImage.setDisplayOrder((int) productImageRepository.countByProduct(product));

            // Sauvegarder en base
            return productImageRepository.save(productImage);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload de l'image", e);
        }
    }

    @Override
    public List<ProductImage> uploadMultipleProductImages(Long productId, MultipartFile[] files, String[] altTexts, String[] descriptions) {
        List<ProductImage> uploadedImages = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            String altText = (altTexts != null && i < altTexts.length) ? altTexts[i] : "";
            String description = (descriptions != null && i < descriptions.length) ? descriptions[i] : "";

            ProductImage uploadedImage = uploadProductImage(productId, file, altText, description);
            uploadedImages.add(uploadedImage);
        }

        return uploadedImages;
    }

    @Override
    public ProductImage setMainImage(Long productId, Long imageId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));

        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image non trouvée"));

        if (!image.getProduct().getId().equals(productId)) {
            throw new IllegalArgumentException("L'image n'appartient pas à ce produit");
        }

        // Retirer le statut principal de toutes les autres images
        List<ProductImage> allImages = productImageRepository.findByProduct(product);
        allImages.forEach(img -> img.setIsMainImage(false));

        // Définir cette image comme principale
        image.setIsMainImage(true);

        return productImageRepository.save(image);
    }

    @Override
    public List<ProductImage> reorderImages(Long productId, List<Long> imageIds) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));

        List<ProductImage> reorderedImages = new ArrayList<>();
        for (int i = 0; i < imageIds.size(); i++) {
            Long imageId = imageIds.get(i);
            ProductImage image = productImageRepository.findById(imageId)
                    .orElseThrow(() -> new IllegalArgumentException("Image non trouvée"));

            if (image.getProduct().getId().equals(productId)) {
                image.setDisplayOrder(i);
                reorderedImages.add(productImageRepository.save(image));
            }
        }

        return reorderedImages;
    }

    @Override
    public void deleteProductImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image non trouvée"));

        // Supprimer le fichier physique
        try {
            Path filePath = Paths.get(uploadPath, "products", image.getImageName());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log l'erreur mais continuer
            System.err.println("Erreur lors de la suppression du fichier: " + e.getMessage());
        }

        // Supprimer de la base de données
        productImageRepository.delete(image);
    }

    @Override
    public void deleteAllProductImages(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));

        List<ProductImage> images = productImageRepository.findByProduct(product);
        for (ProductImage image : images) {
            deleteProductImage(image.getId());
        }
    }

    @Override
    public List<ProductImage> getProductImages(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));

        return productImageRepository.findByProductOrderByDisplayOrderAsc(product);
    }

    @Override
    public ProductImage getMainProductImage(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));

        return productImageRepository.findByProductAndIsMainImageTrue(product)
                .orElse(null);
    }

    @Override
    public List<ProductImage> getActiveProductImages(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));

        return productImageRepository.findByProductAndIsActiveTrueOrderByDisplayOrderAsc(product);
    }

    @Override
    public ProductImage updateImageMetadata(Long imageId, String altText, String description) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image non trouvée"));

        image.setAltText(altText);
        image.setDescription(description);

        return productImageRepository.save(image);
    }

    @Override
    public String generateThumbnail(String imagePath) {
        // TODO: Implémenter la génération de miniatures
        return imagePath; // Pour l'instant, retourner le chemin original
    }

    @Override
    public boolean validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        // Vérifier la taille
        if (file.getSize() > maxFileSize) {
            return false;
        }

        // Vérifier le type MIME
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return false;
        }

        // Vérifier l'extension
        String filename = file.getOriginalFilename();
        if (filename == null) {
            return false;
        }

        String extension = getFileExtension(filename).toLowerCase();
        return extension.equals(".jpg") || extension.equals(".jpeg") || 
               extension.equals(".png") || extension.equals(".webp");
    }

    @Override
    public String getImageUrl(ProductImage image) {
        return image.getImageUrl();
    }

    @Override
    public String getThumbnailUrl(ProductImage image) {
        return image.getThumbnailPath();
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
