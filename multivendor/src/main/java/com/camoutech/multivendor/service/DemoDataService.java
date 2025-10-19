package com.camoutech.multivendor.service;

import com.camoutech.multivendor.model.*;
import com.camoutech.multivendor.repository.*;
import com.camoutech.multivendor.domain.USER_ROLE;
import com.camoutech.multivendor.domain.AccountStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Service pour créer des données de démonstration pour la plateforme agricole
 */
@Service
public class DemoDataService implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductCategoryRepository categoryRepository;
    
    @Autowired
    private ProductSubCategoryRepository subCategoryRepository;
    
    @Autowired
    private SellerRepository sellerRepository;
    
    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    public void run(String... args) throws Exception {
        // Désactivé temporairement pour éviter les conflits de contraintes
        // TODO: Migrer vers le nouveau système de catégories
        /*
        if (productRepository.count() == 0) {
            createDemoProducts();
        }
        */
    }

    private void createDemoProducts() {
        // Récupérer les catégories et sous-catégories
        ProductCategory vegetalCategory = categoryRepository.findByName("Produits Végétaux").orElse(null);
        ProductCategory animalCategory = categoryRepository.findByName("Produits Animaux").orElse(null);
        
        if (vegetalCategory == null || animalCategory == null) {
            return; // Les catégories n'existent pas encore
        }

        // Créer un fermier vendeur
        Seller farmer = createFarmer();
        sellerRepository.save(farmer);
        
        // Créer un fournisseur fermier
        Supplier supplier = createSupplier();
        supplierRepository.save(supplier);

        // Créer des produits végétaux
        createVegetalProducts(vegetalCategory, farmer, supplier);
        
        // Créer des produits animaux
        createAnimalProducts(animalCategory, farmer, supplier);
    }

    private Seller createFarmer() {
        Seller farmer = new Seller();
        farmer.setEmail("fermier@example.com");
        farmer.setMobile("+225 07 12 34 56 78");
        farmer.setRole(USER_ROLE.ROLE_SELLER);
        farmer.setPassword("password123");
        farmer.setGSTIN("GST123456789");
        farmer.setSellerName("Ferme Bio Kouassi");
        farmer.setBusinessDetails(new BusinessDetails());
        farmer.getBusinessDetails().setBusinessName("Ferme Bio Kouassi");
        farmer.getBusinessDetails().setBusinessAddress("Bouaké, Côte d'Ivoire");
        farmer.getPickupAddress().setAddress("Bouaké, Côte d'Ivoire");
        farmer.getPickupAddress().setCity("Bouaké");
        farmer.setAccountStatus(AccountStatus.ACTIVE);
        return farmer;
    }

    private Supplier createSupplier() {
        Supplier supplier = new Supplier();
        supplier.setEmail("fournisseur@example.com");
        supplier.setMobile("+225 05 98 76 54 32");
        supplier.setRole(USER_ROLE.ROLE_SUPPLIER);
        supplier.setPassword("password123");
        supplier.setGSTIN("GST987654321");
        supplier.setSupplierName("Élevage Traoré");
        supplier.setBusinessDetails(new BusinessDetails());
        supplier.getBusinessDetails().setBusinessName("Élevage Traoré");
        supplier.getBusinessDetails().setBusinessAddress("Korhogo, Côte d'Ivoire");
        supplier.getPickupAddress().setAddress("Korhogo, Côte d'Ivoire");
        supplier.getPickupAddress().setCity("Korhogo");
        supplier.setAccountStatus(AccountStatus.ACTIVE);
        return supplier;
    }

    private void createVegetalProducts(ProductCategory category, Seller seller, Supplier supplier) {
        // Récupérer les sous-catégories végétales
        List<ProductSubCategory> subCategories = subCategoryRepository.findByParentCategoryIdAndIsActiveTrue(category.getId());
        
        // Fruits
        ProductSubCategory fruits = subCategories.stream()
            .filter(sc -> sc.getName().equals("Fruits"))
            .findFirst().orElse(null);
        
        if (fruits != null) {
            createFruitProducts(fruits, seller, supplier);
        }

        // Légumes
        ProductSubCategory legumes = subCategories.stream()
            .filter(sc -> sc.getName().equals("Légumes"))
            .findFirst().orElse(null);
        
        if (legumes != null) {
            createVegetableProducts(legumes, seller, supplier);
        }
    }

    private void createFruitProducts(ProductSubCategory subCategory, Seller seller, Supplier supplier) {
        List<Product> fruits = Arrays.asList(
            createProduct("Pommes Golden", "Pommes Golden fraîches et croquantes", 
                800, 600, 25, 50, "Vert", subCategory, seller, supplier, true, true,
                "Bouaké", "Bio", "Toute l'année", "kg", 1.0, "Frais", 
                LocalDateTime.now().plusDays(7), "Riche en vitamine C", "Aucun"),
            
            createProduct("Bananes Plantain", "Bananes plantain mûres pour cuisson", 
                500, 400, 20, 30, "Jaune", subCategory, seller, supplier, true, true,
                "Yamoussoukro", "Bio", "Toute l'année", "kg", 2.0, "Frais", 
                LocalDateTime.now().plusDays(5), "Riche en potassium", "Aucun"),
            
            createProduct("Oranges Valencia", "Oranges Valencia juteuses et sucrées", 
                700, 550, 21, 40, "Orange", subCategory, seller, supplier, true, true,
                "San-Pédro", "Bio", "Décembre-Mars", "kg", 1.5, "Frais", 
                LocalDateTime.now().plusDays(10), "Riche en vitamine C", "Aucun")
        );
        
        productRepository.saveAll(fruits);
    }

    private void createVegetableProducts(ProductSubCategory subCategory, Seller seller, Supplier supplier) {
        List<Product> vegetables = Arrays.asList(
            createProduct("Tomates Cerises", "Tomates cerises bio du jardin", 
                1200, 900, 25, 25, "Rouge", subCategory, seller, supplier, true, true,
                "Bouaké", "Bio", "Toute l'année", "kg", 0.5, "Frais", 
                LocalDateTime.now().plusDays(7), "Riche en lycopène", "Aucun"),
            
            createProduct("Carottes Nouvelles", "Carottes nouvelles croquantes", 
                600, 450, 25, 35, "Orange", subCategory, seller, supplier, true, true,
                "Korhogo", "Bio", "Toute l'année", "kg", 1.0, "Frais", 
                LocalDateTime.now().plusDays(14), "Riche en bêta-carotène", "Aucun"),
            
            createProduct("Épinards Frais", "Épinards frais du potager", 
                800, 600, 25, 20, "Vert", subCategory, seller, supplier, true, true,
                "Bouaké", "Bio", "Toute l'année", "kg", 0.3, "Frais", 
                LocalDateTime.now().plusDays(3), "Riche en fer", "Aucun")
        );
        
        productRepository.saveAll(vegetables);
    }

    private void createAnimalProducts(ProductCategory category, Seller seller, Supplier supplier) {
        List<ProductSubCategory> subCategories = subCategoryRepository.findByParentCategoryIdAndIsActiveTrue(category.getId());
        
        // Viandes
        ProductSubCategory viandes = subCategories.stream()
            .filter(sc -> sc.getName().equals("Viandes"))
            .findFirst().orElse(null);
        
        if (viandes != null) {
            createMeatProducts(viandes, seller, supplier);
        }

        // Produits laitiers
        ProductSubCategory laitiers = subCategories.stream()
            .filter(sc -> sc.getName().equals("Produits Laitiers"))
            .findFirst().orElse(null);
        
        if (laitiers != null) {
            createDairyProducts(laitiers, seller, supplier);
        }
    }

    private void createMeatProducts(ProductSubCategory subCategory, Seller seller, Supplier supplier) {
        List<Product> meats = Arrays.asList(
            createProduct("Poulet Fermier", "Poulet fermier élevé en plein air", 
                2500, 2000, 20, 15, "Rose", subCategory, seller, supplier, true, true,
                "Korhogo", "Élevage traditionnel", "Toute l'année", "pièce", 1.5, "Réfrigéré", 
                LocalDateTime.now().plusDays(3), "Riche en protéines", "Aucun"),
            
            createProduct("Bœuf Tenderloin", "Tenderloin de bœuf local", 
                4500, 3800, 15, 10, "Rouge", subCategory, seller, supplier, true, true,
                "Korhogo", "Élevage traditionnel", "Toute l'année", "kg", 1.0, "Réfrigéré", 
                LocalDateTime.now().plusDays(5), "Riche en fer", "Aucun")
        );
        
        productRepository.saveAll(meats);
    }

    private void createDairyProducts(ProductSubCategory subCategory, Seller seller, Supplier supplier) {
        List<Product> dairy = Arrays.asList(
            createProduct("Lait Frais", "Lait frais de vache locale", 
                800, 650, 19, 20, "Blanc", subCategory, seller, supplier, true, true,
                "Korhogo", "Élevage traditionnel", "Toute l'année", "litre", 1.0, "Réfrigéré", 
                LocalDateTime.now().plusDays(3), "Riche en calcium", "Lactose"),
            
            createProduct("Fromage Local", "Fromage artisanal local", 
                2000, 1600, 20, 12, "Blanc", subCategory, seller, supplier, true, true,
                "Korhogo", "Artisanal", "Toute l'année", "kg", 0.5, "Réfrigéré", 
                LocalDateTime.now().plusDays(7), "Riche en protéines", "Lactose")
        );
        
        productRepository.saveAll(dairy);
    }

    private Product createProduct(String title, String description, int mrpPrice, int sellingPrice, 
                                int discountPercent, int quantity, String color, ProductSubCategory subCategory,
                                Seller seller, Supplier supplier, boolean isOrganic, boolean isLocal,
                                String origin, String farmingMethod, String season, String unit, double weight,
                                String storageConditions, LocalDateTime expiryDate, String nutritionalInfo, String allergens) {
        
        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description);
        product.setMrpPrice(mrpPrice);
        product.setSellingPrice(sellingPrice);
        product.setDiscountPercent(discountPercent);
        product.setQuantity(quantity);
        product.setColor(color);
        product.setCategory(subCategory.getParentCategory());
        product.setSubCategory(subCategory);
        product.setSeller(seller);
        product.setSupplier(supplier);
        product.setOrigin(origin);
        product.setFarmingMethod(farmingMethod);
        product.setSeason(season);
        product.setUnit(unit);
        product.setWeight(weight);
        product.setStorageConditions(storageConditions);
        product.setExpiryDate(expiryDate);
        product.setOrganic(isOrganic);
        product.setLocal(isLocal);
        product.setNutritionalInfo(nutritionalInfo);
        product.setAllergens(allergens);
        product.setCreatedAt(LocalDateTime.now());
        product.setNumRatings(0);
        
        return product;
    }
}
