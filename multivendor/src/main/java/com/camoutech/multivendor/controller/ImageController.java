package com.camoutech.multivendor.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contrôleur pour servir les images statiques
 */
@RestController
@RequestMapping("/uploads")
public class ImageController {

    /**
     * Servir les images depuis le dossier uploads
     */
    @GetMapping("/products/**")
    public ResponseEntity<Resource> getImage(HttpServletRequest request) {
        try {
            // Extraire le nom du fichier depuis l'URL
            String requestURI = request.getRequestURI();
            String filename = requestURI.substring(requestURI.lastIndexOf("/") + 1);
            
            // Si pas de filename, utiliser une image par défaut
            if (filename == null || filename.isEmpty() || filename.equals("products")) {
                filename = "default-product.jpg";
            }
            
            // Chemin absolu vers le dossier uploads
            String uploadPath = "C:\\Users\\ELITEBOOK 850 G6\\Desktop\\ecommerce1\\multivendor-fullstack\\multivendor\\src\\main\\resources\\uploads\\products\\" + filename;
            Resource resource = new UrlResource("file:" + uploadPath);
            
            if (resource.exists()) {
                // Déterminer le type de contenu basé sur l'extension
                String contentType = getContentType(filename);
                
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
            } else {
                // Retourner une image par défaut si le fichier n'existe pas
                return getDefaultImage();
            }
        } catch (Exception e) {
            return getDefaultImage();
        }
    }
    
    /**
     * Déterminer le type de contenu basé sur l'extension du fichier
     */
    private String getContentType(String filename) {
        if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else if (filename.toLowerCase().endsWith(".gif")) {
            return "image/gif";
        } else if (filename.toLowerCase().endsWith(".svg")) {
            return "image/svg+xml";
        } else {
            return "image/jpeg"; // Par défaut
        }
    }
    
    /**
     * Retourner une image par défaut
     */
    private ResponseEntity<Resource> getDefaultImage() {
        try {
            // Créer une image SVG par défaut
            String svgContent = """
                <svg width="400" height="300" xmlns="http://www.w3.org/2000/svg">
                    <rect width="400" height="300" fill="#f5f5f5"/>
                    <text x="200" y="150" text-anchor="middle" font-family="Arial" font-size="16" fill="#666">
                        Image non disponible
                    </text>
                </svg>
                """;
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/svg+xml"))
                .body(new org.springframework.core.io.ByteArrayResource(svgContent.getBytes()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
