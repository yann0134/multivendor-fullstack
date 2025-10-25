/**
 * Created by camoutech
 * Date :18/10/2024
 * Time :20:20
 * Project Name :multivendor
 */

package com.camoutech.multivendor.service.impl;

import com.camoutech.multivendor.model.Cart;
import com.camoutech.multivendor.model.CartItem;
import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.User;
import com.camoutech.multivendor.repository.CartItemRepository;
import com.camoutech.multivendor.repository.CartRepository;
import com.camoutech.multivendor.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public CartItem addCartItem(User user, Product product, String size, int quantity) {
        Cart cart = findUserCart(user);

        CartItem isPresent = cartItemRepository.findByCartAndProductAndSize(cart, product, size);

        if (isPresent == null){
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUserId(user.getId());
            cartItem.setSize(size);

            int totalPrice = quantity * product.getSellingPrice();
            cartItem.setSellingPrice(totalPrice);
            cartItem.setMrpPrice(quantity*product.getMrpPrice());

            cart.getCartItems().add(cartItem);
            cartItem.setCart(cart);

            return cartItemRepository.save(cartItem);
        }
        return isPresent;
    }

    @Override
    public Cart findUserCart(User user) {
        Cart cart = cartRepository.findByUserId(user.getId());
        
        // Si aucun panier n'existe, en créer un nouveau
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setCartItems(new ArrayList<>());
            cart = cartRepository.save(cart);
        }

        int totalPrice = 0;
        int totalDiscountedPrice = 0;
        int totalItem = 0;

        for(CartItem cartItem: cart.getCartItems()){
            totalPrice+=cartItem.getMrpPrice();
            totalDiscountedPrice+=cartItem.getSellingPrice();
            totalItem+=cartItem.getQuantity();
        }

        cart.setTotalMrpPrice(totalPrice);
        cart.setTotalItem(totalItem);
        cart.setTotalSellingPrice(totalDiscountedPrice);
        cart.setDiscount(calculateDiscountPercentage(totalPrice, totalDiscountedPrice));
        cart.setTotalItem(totalItem);
        return cart;
    }

    private int calculateDiscountPercentage(int mrpPrice, int sellingPrice) {
        // Si le prix MRP est 0 ou négatif, pas de remise
        if (mrpPrice <= 0) {
            return 0;
        }
        
        // Si le prix de vente est supérieur au prix MRP, c'est une augmentation
        if (sellingPrice >= mrpPrice) {
            return 0; // Pas de remise, prix normal ou augmentation
        }
        
        double discount = mrpPrice - sellingPrice;
        double discountPercentage = (discount / mrpPrice) * 100;
        return (int) discountPercentage;
    }

    @Override
    public void clearCart(User user) {
        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart != null) {
            // Supprimer tous les items du panier
            cartItemRepository.deleteByCart(cart);
            
            // Réinitialiser le panier
            cart.setTotalItem(0);
            cart.setTotalSellingPrice(0);
            cart.setDiscount(0);
            cart.setTotalMrpPrice(0);
            cartRepository.save(cart);
        }
    }

    @Override
    public void removeCartItem(Long userId, Long cartItemId) {
        // Trouver le panier de l'utilisateur
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            // Supprimer l'item du panier
            cartItemRepository.deleteById(cartItemId);
            
            // Recalculer les totaux
            cart.setTotalItem(cart.getCartItems().size());
            cart.setTotalSellingPrice(cart.getCartItems().stream()
                .mapToDouble(item -> item.getProduct().getSellingPrice() * item.getQuantity())
                .sum());
            cartRepository.save(cart);
        }
    }

    @Override
    public CartItem updateCartItem(Long userId, Long cartItemId, CartItem cartItem) {
        // Trouver l'item existant
        CartItem existingItem = cartItemRepository.findById(cartItemId).orElse(null);
        if (existingItem != null) {
            // Mettre à jour la quantité
            existingItem.setQuantity(cartItem.getQuantity());
            existingItem.setSize(cartItem.getSize());
            
            // Sauvegarder
            CartItem updatedItem = cartItemRepository.save(existingItem);
            
            // Recalculer les totaux du panier
            Cart cart = cartRepository.findByUserId(userId);
            if (cart != null) {
                cart.setTotalItem(cart.getCartItems().size());
                cart.setTotalSellingPrice(cart.getCartItems().stream()
                    .mapToDouble(item -> item.getProduct().getSellingPrice() * item.getQuantity())
                    .sum());
                cartRepository.save(cart);
            }
            
            return updatedItem;
        }
        return null;
    }
}
