package com.camoutech.multivendor.service;

import com.camoutech.multivendor.dto.ProduitDTO;
import com.camoutech.multivendor.exceptions.ProductException;
import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.Seller;
import com.camoutech.multivendor.model.Supplier;
import com.camoutech.multivendor.request.CreateProductRequest;
import org.springframework.data.domain.Page;


import java.util.List;

public interface ProductService {

    public Product createProduct(CreateProductRequest req, Seller seller) throws ProductException;
    public Product createProductWithSupplier(CreateProductRequest req, Supplier supplier) throws ProductException;
    public void deleteProduct(Long productId) throws ProductException;
    public Product updateProduct(Long productId, Product product) throws ProductException;
    Product findProductById(Long productId) throws ProductException;
    List<Product> searchProducts(String query);
    List<ProduitDTO> searchProductsDTO(String query);
    public Page<Product> getAllProducts(
            String category,
            String brand,
            String colors,
            String sizes,
            Integer minPrice,
            Integer maxPrice,
            Integer minDiscount,
            String sort,
            String stock,
            Integer pageNumber
    );

    List<Product> getProductBySellerId(Long sellerId);
    List<Product> getApprovedProductsForCurrentSupplier();
    
    // Méthodes pour la recherche nutritionnelle
    List<Product> searchProductsByNutritionalInfo(String nutritionalInfo);
    List<Product> getProductsByNutritionalValue(String nutrient, String value);
    List<Product> getOrganicProducts();
}
