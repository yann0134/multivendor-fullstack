package com.camoutech.multivendor.chat;

import com.camoutech.multivendor.dto.MealPlanDTO;
import com.camoutech.multivendor.model.*;
import com.camoutech.multivendor.request.GenerateMealPlanRequest;
import com.camoutech.multivendor.service.*;
import com.camoutech.multivendor.service.MealPlanDTOConverter;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.camoutech.multivendor.dto.ProduitDTO;

import java.util.List;

@Component
@Service
public class  ToolsChat {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MealPlanningService mealPlanningService;
    
    @Autowired
    private MealPlanDTOConverter mealPlanDTOConverter;

    // ==================== PRODUITS ====================
    
    @Tool(description = "Search products by query")
    public List<ProduitDTO> searchProducts(String query) {
        return productService.searchProductsDTO(query);
    }
    
   /* @Tool(description = "Get product details by ID")
    public Product getProductById(Long id) {
        try {
            return productService.findProductById(id);
        } catch (Exception e) {
            throw new RuntimeException("Produit non trouvé: " + e.getMessage());
        }
    }
    
    @Tool(description = "Get all products with filters")
    public Page<Product> getAllProducts(String category, String brand, String colors, 
                                       String sizes, Integer minPrice, Integer maxPrice, 
                                       Integer minDiscount, String sort, String stock, 
                                       Integer pageNumber) {
        return productService.getAllProducts(category, brand, colors, sizes, minPrice, 
                                           maxPrice, minDiscount, sort, stock, pageNumber);
    }
    
    @Tool(description = "Get approved products for current supplier")
    public List<Product> getApprovedProducts() {
        return productService.getApprovedProductsForCurrentSupplier();
    }

    // ==================== PANIER ====================
    
    @Tool(description = "Get current user's cart contents")
    public Cart getCart(User user) {
        return cartService.findUserCart(user);
    }
    
    @Tool(description = "Add product to cart")
    public CartItem addItemToCart(User user, Long productId, String size, int quantity) {
        try {
            Product product = productService.findProductById(productId);
            if (product == null) {
                throw new RuntimeException("Produit non trouvé");
            }
            
            return cartService.addCartItem(user, product, size, quantity);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'ajout au panier: " + e.getMessage());
        }
    }
    
    @Tool(description = "Update cart item quantity")
    public CartItem updateCartItem(Long userId, Long cartItemId, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(quantity);
        return cartService.updateCartItem(userId, cartItemId, cartItem);
    }
    
    @Tool(description = "Remove item from cart")
    public void removeCartItem(Long userId, Long cartItemId) {
        cartService.removeCartItem(userId, cartItemId);
    }
    
    @Tool(description = "Clear user's cart")
    public void clearCart(User user) {
        try {
            cartService.clearCart(user);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du vidage du panier: " + e.getMessage());
        }
    }

    // ==================== PROFIL UTILISATEUR ====================
    
    @Tool(description = "Get user profile information by JWT token")
    public User getUserProfile(String jwtToken) {
        try {
            return userService.findUserByJwtToken(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException("Utilisateur non trouvé: " + e.getMessage());
        }
    }
    
    @Tool(description = "Update user profile")
    public User updateUserProfile(User user) {
        try {
            return userService.updateUser(user);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour du profil: " + e.getMessage());
        }
    }

    // ==================== PLANNING DE REPAS ====================
    
    @Tool(description = "Generate meal plan")
    public MealPlanDTO generateMealPlan(User user, GenerateMealPlanRequest request) {
        try {
            MealPlan mealPlan = new MealPlan();
            mealPlan.setName(request.getName());
            mealPlan.setDescription(request.getDescription());
            mealPlan.setTotalDays(request.getNumberOfDays());
            mealPlan.setServingsPerMeal(request.getServingsPerMeal());
            mealPlan.setMinPricePerRecipe(request.getMinPricePerRecipe());
            mealPlan.setMealType(request.getMealType());
            
            MealPlan createdPlan = mealPlanningService.createMealPlan(mealPlan, user);
            return mealPlanDTOConverter.toDTO(createdPlan);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création du planning: " + e.getMessage());
        }
    }
    
    @Tool(description = "Get meal plan details by ID")
    public MealPlanDTO getMealPlanById(Long mealPlanId) {
        try {
            MealPlan mealPlan = mealPlanningService.findMealPlanById(mealPlanId);
            return mealPlanDTOConverter.toDTO(mealPlan);
        } catch (Exception e) {
            throw new RuntimeException("Planning non trouvé: " + e.getMessage());
        }
    }
    
    @Tool(description = "Add meal plan to cart")
    public void addMealPlanToCart(Long userId, User user) {
        try {
            mealPlanningService.addMealPlanToCart(userId, user);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'ajout du planning au panier: " + e.getMessage());
        }
    }
    
    @Tool(description = "Delete meal plan")
    public void deleteMealPlan(Long userId, User user) {
        try {
            mealPlanningService.deleteMealPlan(userId, user);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression du planning: " + e.getMessage());
        }
    }

    // ==================== RECHERCHE NUTRITIONNELLE ====================
    
    @Tool(description = "Search products by nutritional information")
    public List<Product> searchProductsByNutrition(String nutritionalInfo) {
        try {
            return productService.searchProductsByNutritionalInfo(nutritionalInfo);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche nutritionnelle: " + e.getMessage());
        }
    }
    
    @Tool(description = "Get products with specific nutritional value")
    public List<Product> getProductsByNutritionalValue(String nutrient, String value) {
        try {
            return productService.getProductsByNutritionalValue(nutrient, value);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche par valeur nutritionnelle: " + e.getMessage());
        }
    }
    
    @Tool(description = "Get products with high protein content")
    public List<Product> getHighProteinProducts() {
        try {
            return productService.getProductsByNutritionalValue("protein", "high");
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche de produits riches en protéines: " + e.getMessage());
        }
    }
    
    @Tool(description = "Get products with high fiber content")
    public List<Product> getHighFiberProducts() {
        try {
            return productService.getProductsByNutritionalValue("fiber", "Fer");
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche de produits riches en fibres: " + e.getMessage());
        }
    }
    
    @Tool(description = "Get products with low sugar content")
    public List<Product> getLowSugarProducts() {
        try {
            return productService.getProductsByNutritionalValue("sugar", "low");
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche de produits pauvres en sucre: " + e.getMessage());
        }
    }
    
    @Tool(description = "Get products with specific vitamins")
    public List<Product> getProductsByVitamins(String vitamins) {
        try {
            return productService.getProductsByNutritionalValue("vitamins", vitamins);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche par vitamines: " + e.getMessage());
        }
    }
    
    @Tool(description = "Get organic products")
    public List<Product> getOrganicProducts() {
        try {
            return productService.getOrganicProducts();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche de produits biologiques: " + e.getMessage());
        }
    }

    // ==================== UTILITAIRES ====================
    
    @Tool(description = "Get products by seller ID")
    public List<Product> getProductsBySeller(Long sellerId) {
        return productService.getProductBySellerId(sellerId);
    }*/
}
