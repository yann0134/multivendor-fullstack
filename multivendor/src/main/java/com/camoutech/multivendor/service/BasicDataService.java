package com.camoutech.multivendor.service;

import com.camoutech.multivendor.model.Category;
import com.camoutech.multivendor.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

/**
 * Service pour initialiser les données de base avec l'ancien système de catégories
 */
@Service
public class BasicDataService implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeBasicCategories();
    }

    private void initializeBasicCategories() {
        // Vérifier si les catégories existent déjà
        if (categoryRepository.count() > 0) {
            return;
        }

        // Créer des catégories de base pour les produits agricoles
        Category vegetalCategory = createCategory("Produits Végétaux", "VEGETAL", 1);
        Category animalCategory = createCategory("Produits Animaux", "ANIMAL", 1);
        
        categoryRepository.save(vegetalCategory);
        categoryRepository.save(animalCategory);

        // Créer des sous-catégories végétales
        Category fruits = createSubCategory("Fruits", "FRUITS", 2, vegetalCategory);
        Category legumes = createSubCategory("Légumes", "LEGUMES", 2, vegetalCategory);
        Category cereales = createSubCategory("Céréales", "CEREALES", 2, vegetalCategory);
        
        categoryRepository.save(fruits);
        categoryRepository.save(legumes);
        categoryRepository.save(cereales);

        // Créer des sous-catégories animales
        Category viandes = createSubCategory("Viandes", "VIANDES", 2, animalCategory);
        Category laitiers = createSubCategory("Produits Laitiers", "LAITIERS", 2, animalCategory);
        Category oeufs = createSubCategory("Œufs", "OEUFS", 2, animalCategory);
        
        categoryRepository.save(viandes);
        categoryRepository.save(laitiers);
        categoryRepository.save(oeufs);
    }

    private Category createCategory(String name, String categoryId, int level) {
        Category category = new Category();
        category.setName(name);
        category.setCategoryId(categoryId);
        category.setLevel(level);
        return category;
    }

    private Category createSubCategory(String name, String categoryId, int level, Category parentCategory) {
        Category category = new Category();
        category.setName(name);
        category.setCategoryId(categoryId);
        category.setLevel(level);
        category.setParentCategory(parentCategory);
        return category;
    }
}
