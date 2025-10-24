package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.Supplier;
import com.camoutech.multivendor.repository.ProductRepository;
import com.camoutech.multivendor.repository.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@Transactional
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Test
    @WithMockUser(authorities = "ROLE_SUPPLIER", username = "supplier@test.com")
    public void testGetDeliveredAndReceivedProductsBySupplier() throws Exception {
        // Créer un fournisseur de test
        Supplier supplier = new Supplier();
        supplier.setEmail("supplier@test.com");
        supplier.setSupplierName("Test Supplier");
        supplier = supplierRepository.save(supplier);

        // Créer un produit de test avec les statuts appropriés
        Product product = new Product();
        product.setTitle("Test Product");
        product.setDescription("Test Description");
        product.setStatus(Product.ProductStatus.APPROVED);
        product.setShipmentStatus(Product.ShipmentStatus.SHIPPED);
        product.setReceptionStatus(Product.ReceptionStatus.RECEIVED);
        product.setSupplier(supplier);
        productRepository.save(product);

        // Tester l'endpoint
        mockMvc.perform(get("/api/products/delivered-and-received/supplier"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Test Product"))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[0].shipmentStatus").value("SHIPPED"))
                .andExpect(jsonPath("$[0].receptionStatus").value("RECEIVED"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void testGetAllDeliveredAndReceivedProducts() throws Exception {
        // Créer un fournisseur de test
        Supplier supplier = new Supplier();
        supplier.setEmail("supplier@test.com");
        supplier.setSupplierName("Test Supplier");
        supplier = supplierRepository.save(supplier);

        // Créer un produit de test avec les statuts appropriés
        Product product = new Product();
        product.setTitle("Test Product");
        product.setDescription("Test Description");
        product.setStatus(Product.ProductStatus.APPROVED);
        product.setShipmentStatus(Product.ShipmentStatus.SHIPPED);
        product.setReceptionStatus(Product.ReceptionStatus.RECEIVED);
        product.setSupplier(supplier);
        productRepository.save(product);

        // Tester l'endpoint admin
        mockMvc.perform(get("/api/products/delivered-and-received/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Test Product"))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[0].shipmentStatus").value("SHIPPED"))
                .andExpect(jsonPath("$[0].receptionStatus").value("RECEIVED"));
    }
}
