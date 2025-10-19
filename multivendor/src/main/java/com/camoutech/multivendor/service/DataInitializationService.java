package com.camoutech.multivendor.service;

import com.camoutech.multivendor.model.ProductCategory;
import com.camoutech.multivendor.model.ProductSubCategory;
import com.camoutech.multivendor.repository.ProductCategoryRepository;
import com.camoutech.multivendor.repository.ProductSubCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Service pour initialiser les données de base de la plateforme agricole
 */
@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private ProductCategoryRepository categoryRepository;
    
    @Autowired
    private ProductSubCategoryRepository subCategoryRepository;

    @Override
    public void run(String... args) throws Exception {
        // Désactivé temporairement pour éviter les conflits
        // TODO: Migrer vers le nouveau système de catégories
        /*
        initializeCategories();
        */
    }

    private void initializeCategories() {
        // Vérifier si les catégories existent déjà
        if (categoryRepository.count() > 0) {
            return;
        }

        // Créer les catégories principales
        ProductCategory vegetalCategory = createCategory(
            "Produits Végétaux", 
            "Fruits, légumes, céréales et autres produits d'origine végétale",
            ProductCategory.CategoryType.VEGETAL,
            "mdi-leaf",
            "#4CAF50"
        );

        ProductCategory animalCategory = createCategory(
            "Produits Animaux", 
            "Viandes, produits laitiers, œufs et autres produits d'origine animale",
            ProductCategory.CategoryType.ANIMAL,
            "mdi-cow",
            "#FF9800"
        );

        categoryRepository.saveAll(Arrays.asList(vegetalCategory, animalCategory));

        // Créer les sous-catégories végétales
        createVegetalSubCategories(vegetalCategory);
        
        // Créer les sous-catégories animales
        createAnimalSubCategories(animalCategory);
    }

    private ProductCategory createCategory(String name, String description, 
                                         ProductCategory.CategoryType type, 
                                         String icon, String color) {
        ProductCategory category = new ProductCategory();
        category.setName(name);
        category.setDescription(description);
        category.setType(type);
        category.setIcon(icon);
        category.setColor(color);
        category.setActive(true);
        return category;
    }

    private void createVegetalSubCategories(ProductCategory parentCategory) {
        List<ProductSubCategory> vegetalSubCategories = Arrays.asList(
            createSubCategory("Fruits", "Pommes, bananes, oranges, etc.", parentCategory, "mdi-apple"),
            createSubCategory("Légumes", "Tomates, carottes, épinards, etc.", parentCategory, "mdi-carrot"),
            createSubCategory("Céréales", "Riz, blé, maïs, etc.", parentCategory, "mdi-grain"),
            createSubCategory("Légumineuses", "Haricots, lentilles, pois, etc.", parentCategory, "mdi-seed"),
            createSubCategory("Herbes & Épices", "Basilic, thym, poivre, etc.", parentCategory, "mdi-leaf"),
            createSubCategory("Noix & Graines", "Amandes, noix, graines de tournesol, etc.", parentCategory, "mdi-nut"),
            createSubCategory("Tubercules", "Pommes de terre, ignames, manioc, etc.", parentCategory, "mdi-potato")
        );
        
        subCategoryRepository.saveAll(vegetalSubCategories);
    }

    private void createAnimalSubCategories(ProductCategory parentCategory) {
        List<ProductSubCategory> animalSubCategories = Arrays.asList(
            createSubCategory("Viandes", "Bœuf, porc, mouton, volaille, etc.", parentCategory, "mdi-food-drumstick"),
            createSubCategory("Produits Laitiers", "Lait, fromage, yaourt, beurre, etc.", parentCategory, "mdi-bottle-soda"),
            createSubCategory("Œufs", "Œufs de poule, caille, etc.", parentCategory, "mdi-egg"),
            createSubCategory("Poissons", "Poissons frais, fumés, etc.", parentCategory, "mdi-fish"),
            createSubCategory("Miel", "Miel naturel et produits de la ruche", parentCategory, "mdi-bee"),
            createSubCategory("Charcuterie", "Saucisses, jambon, saucissons, etc.", parentCategory, "mdi-food")
        );
        
        subCategoryRepository.saveAll(animalSubCategories);
    }

    private ProductSubCategory createSubCategory(String name, String description, 
                                               ProductCategory parentCategory, String icon) {
        ProductSubCategory subCategory = new ProductSubCategory();
        subCategory.setName(name);
        subCategory.setDescription(description);
        subCategory.setParentCategory(parentCategory);
        subCategory.setIcon(icon);
        subCategory.setActive(true);
        return subCategory;
    }
}
