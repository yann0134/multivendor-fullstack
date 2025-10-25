package com.camoutech.multivendor.service;

import com.camoutech.multivendor.model.Cart;
import com.camoutech.multivendor.model.CartItem;
import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.User;

public interface CartService {

    public CartItem addCartItem(
            User user,
            Product product,
            String size,
            int quantity
    );

    public Cart findUserCart(User user);
    
    public void clearCart(User user);
    
    public void removeCartItem(Long userId, Long cartItemId);
    
    public CartItem updateCartItem(Long userId, Long cartItemId, CartItem cartItem);
}
