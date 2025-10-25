/**
 * Created by camoutech
 * Date :18/10/2024
 * Time :23:56
 * Project Name :multivendor
 */

package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.model.Cart;
import com.camoutech.multivendor.model.CartItem;
import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.Recipe;
import com.camoutech.multivendor.model.RecipeIngredient;
import com.camoutech.multivendor.model.User;
import com.camoutech.multivendor.request.AddItemRequest;
import com.camoutech.multivendor.response.ApiResponse;
import com.camoutech.multivendor.service.CartItemService;
import com.camoutech.multivendor.service.CartService;
import com.camoutech.multivendor.service.ProductService;
import com.camoutech.multivendor.service.RecipeService;
import com.camoutech.multivendor.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final CartItemService cartItemService;
    private final UserService userService;
    private final ProductService productService;
    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<Cart> findUserCartHandler(
            @RequestHeader(value = "Authorization", required = false) String jwt) throws Exception {
        // Si pas de token, retourner un panier vide
        if (jwt == null || jwt.isEmpty()) {
            Cart emptyCart = new Cart();
            return new ResponseEntity<Cart>(emptyCart, HttpStatus.OK);
        }
        
        User user = userService.findUserByJwtToken(jwt);
        Cart cart = cartService.findUserCart(user);
        return new ResponseEntity<Cart>(cart, HttpStatus.OK);
    }

    @PutMapping("/add")
    public ResponseEntity<CartItem> addItemToCart(@RequestBody AddItemRequest req,
                                                  @RequestHeader(value = "Authorization", required = false) String jwt) throws Exception {
        // Créer un utilisateur temporaire pour les utilisateurs non authentifiés
        User user;
        if (jwt == null || jwt.isEmpty()) {
            // Créer un utilisateur temporaire ou utiliser un utilisateur par défaut
            user = new User();
            user.setId(0L); // ID temporaire pour les utilisateurs non authentifiés
            user.setEmail("guest@temporary.com");
            user.setFullName("Guest User");
        } else {
            user = userService.findUserByJwtToken(jwt);
        }
        
        Product product = productService.findProductById(req.getProductId());
        CartItem item = cartService.addCartItem(user, product, req.getSize(), req.getQuantity());

        ApiResponse res = new ApiResponse();
        res.setMessage("Item Added To Cart Successfully");

        return new ResponseEntity<>(item, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<ApiResponse> deleteCartItemHandler(
            @PathVariable Long cartItemId,
            @RequestHeader(value = "Authorization", required = false) String jwt) throws Exception {
        User user;
        if (jwt == null || jwt.isEmpty()) {
            user = new User();
            user.setId(0L);
        } else {
            user = userService.findUserByJwtToken(jwt);
        }
        
        cartItemService.removeCartItem(user.getId(), cartItemId);

        ApiResponse res = new ApiResponse();
        res.setMessage("Item Remove From Cart");
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItemHandler(
            @PathVariable Long cartItemId,
            @RequestBody CartItem cartItem,
            @RequestHeader(value = "Authorization", required = false) String jwt) throws Exception {

        User user;
        if (jwt == null || jwt.isEmpty()) {
            user = new User();
            user.setId(0L);
        } else {
            user = userService.findUserByJwtToken(jwt);
        }

        CartItem updatedCartItem = null;
        if (cartItem.getQuantity()>0){
            updatedCartItem = cartItemService.updateCartItem(user.getId(), cartItemId, cartItem);
        }

        return new ResponseEntity<>(updatedCartItem, HttpStatus.ACCEPTED);
    }
    
    /**
     * Ajouter une recette complète au panier
     */
    @PostMapping("/add-recipe")
    public ResponseEntity<ApiResponse> addRecipeToCart(@RequestBody Map<String, Object> req,
                                                       @RequestHeader(value = "Authorization", required = false) String jwt) throws Exception {
        User user;
        if (jwt == null || jwt.isEmpty()) {
            user = new User();
            user.setId(0L);
            user.setEmail("guest@temporary.com");
            user.setFullName("Guest User");
        } else {
            user = userService.findUserByJwtToken(jwt);
        }
        
        // Récupérer la recette
        Long recipeId = Long.valueOf(req.get("recipeId").toString());
        Integer servings = Integer.valueOf(req.get("servings").toString());
        
        Recipe recipe = recipeService.findRecipeById(recipeId);
        if (recipe == null) {
            ApiResponse res = new ApiResponse();
            res.setMessage("Recette non trouvée");
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }
        
        // Utiliser les quantités calculées par le frontend
        @SuppressWarnings("unchecked")
        java.util.List<Map<String, Object>> ingredients = (java.util.List<Map<String, Object>>) req.get("ingredients");
        
        if (ingredients != null) {
            for (Map<String, Object> ingredientData : ingredients) {
                Long productId = Long.valueOf(ingredientData.get("productId").toString());
                Integer quantity = Integer.valueOf(ingredientData.get("quantity").toString());
                
                // Trouver le produit par son ID
                Product product = productService.findProductById(productId);
                if (product != null) {
                    cartService.addCartItem(user, product, "DEFAULT", quantity);
                }
            }
        } else {
            // Fallback: utiliser les ingrédients de la recette avec calcul backend
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                int adjustedQuantity = Math.round((ingredient.getQuantity() * servings) / recipe.getServings());
                cartService.addCartItem(user, ingredient.getProduct(), "DEFAULT", adjustedQuantity);
            }
        }
        
        ApiResponse res = new ApiResponse();
        res.setMessage("Recette ajoutée au panier avec succès");
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }
}
