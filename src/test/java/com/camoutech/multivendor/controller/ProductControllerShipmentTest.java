package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.model.Product;
import com.camoutech.multivendor.model.Supplier;
import com.camoutech.multivendor.repository.ProductRepository;
import com.camoutech.multivendor.repository.SupplierRepository;
import com.camoutech.multivendor.request.UpdateShipmentStatusRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@Transactional
public class ProductControllerShipmentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Test
    @WithMockUser(authorities = "ROLE_SUPPLIER", username = "supplier@test.com")
    public void testUpdateProductShipmentStatus() throws Exception {
        // Créer un fournisseur de test
        Supplier supplier = new Supplier();
        supplier.setEmail("supplier@test.com");
        supplier.setSupplierName("Test Supplier");
        supplier = supplierRepository.save(supplier);

        // Créer un produit de test
        Product product = new Product();
        product.setTitle("Test Product");
        product.setDescription("Test Description");
        product.setStatus(Product.ProductStatus.APPROVED);
        product.setShipmentStatus(Product.ShipmentStatus.NOT_SHIPPED);
        product.setSupplier(supplier);
        product = productRepository.save(product);

        // Tester l'endpoint
        mockMvc.perform(put("/api/products/" + product.getId() + "/shipment-status")
                .contentType("application/json")
                .content("{\"status\": \"SHIPPED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.shipmentStatus").value("SHIPPED"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_SUPPLIER", username = "supplier@test.com")
    public void testUpdateProductShipmentStatusUnauthorized() throws Exception {
        // Créer un autre fournisseur
        Supplier otherSupplier = new Supplier();
        otherSupplier.setEmail("other@test.com");
        otherSupplier.setSupplierName("Other Supplier");
        otherSupplier = supplierRepository.save(otherSupplier);

        // Créer un produit appartenant à un autre fournisseur
        Product product = new Product();
        product.setTitle("Test Product");
        product.setDescription("Test Description");
        product.setStatus(Product.ProductStatus.APPROVED);
        product.setShipmentStatus(Product.ShipmentStatus.NOT_SHIPPED);
        product.setSupplier(otherSupplier);
        product = productRepository.save(product);

        // Tester l'endpoint (doit échouer)
        mockMvc.perform(put("/api/products/" + product.getId() + "/shipment-status")
                .contentType("application/json")
                .content("{\"status\": \"SHIPPED\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_SUPPLIER", username = "supplier@test.com")
    public void testUpdateProductShipmentStatusNotApproved() throws Exception {
        // Créer un fournisseur de test
        Supplier supplier = new Supplier();
        supplier.setEmail("supplier@test.com");
        supplier.setSupplierName("Test Supplier");
        supplier = supplierRepository.save(supplier);

        // Créer un produit non approuvé
        Product product = new Product();
        product.setTitle("Test Product");
        product.setDescription("Test Description");
        product.setStatus(Product.ProductStatus.PENDING_APPROVAL);
        product.setShipmentStatus(Product.ShipmentStatus.NOT_SHIPPED);
        product.setSupplier(supplier);
        product = productRepository.save(product);

        // Tester l'endpoint (doit échouer)
        mockMvc.perform(put("/api/products/" + product.getId() + "/shipment-status")
                .contentType("application/json")
                .content("{\"status\": \"SHIPPED\"}"))
                .andExpect(status().isBadRequest());
    }
}
