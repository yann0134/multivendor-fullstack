package com.camoutech.multivendor.controller;

import com.camoutech.multivendor.dto.MealPlanDTO;
import com.camoutech.multivendor.model.MealPlan;
import com.camoutech.multivendor.model.User;
import com.camoutech.multivendor.service.MealPlanningService;
import com.camoutech.multivendor.service.MealPlanDTOConverter;
import com.camoutech.multivendor.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour la gestion du planning de repas
 */
@RestController
@RequestMapping("/api/meal-plans")
@RequiredArgsConstructor
public class MealPlanningController {

    private final MealPlanningService mealPlanningService;
    private final UserService userService;
    private final MealPlanDTOConverter dtoConverter;

    /**
     * Créer un nouveau planning de repas
     */
    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<MealPlan> createMealPlan(
            @RequestBody MealPlan mealPlan,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        MealPlan createdMealPlan = mealPlanningService.createMealPlan(mealPlan, user);
        return new ResponseEntity<>(createdMealPlan, HttpStatus.CREATED);
    }

    /**
     * Générer automatiquement un planning de repas
     */
    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<MealPlan> generateMealPlan(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);

        String name = (String) request.get("name");
        String description = (String) request.get("description");
        Integer numberOfDays = (Integer) request.get("numberOfDays");
        Integer servingsPerMeal = (Integer) request.get("servingsPerMeal");
        Integer minPricePerRecipe = (Integer) request.get("minPricePerRecipe");
        String mealType = (String) request.get("mealType");

        MealPlan generatedMealPlan = mealPlanningService.generateMealPlan(
                name, description, numberOfDays, servingsPerMeal, minPricePerRecipe, mealType, user);

        return new ResponseEntity<>(generatedMealPlan, HttpStatus.CREATED);
    }

    /**
     * Récupérer un planning par ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<MealPlanDTO> getMealPlan(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        MealPlan mealPlan = mealPlanningService.findMealPlanById(id);

        if (mealPlan == null) {
            return ResponseEntity.notFound().build();
        }

        // Vérifier que l'utilisateur est le propriétaire
        if (!mealPlan.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MealPlanDTO dto = dtoConverter.toDTO(mealPlan);
        return ResponseEntity.ok(dto);
    }

    /**
     * Récupérer les plannings de l'utilisateur
     */
    @GetMapping("/my-plans")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Page<MealPlan>> getMyMealPlans(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        Page<MealPlan> mealPlans = mealPlanningService.findUserMealPlans(user, page, size);
        return ResponseEntity.ok(mealPlans);
    }

    /**
     * Récupérer tous les plannings de l'utilisateur (sans pagination)
     */
    @GetMapping("/my-plans/all")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<List<MealPlan>> getAllMyMealPlans(
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        List<MealPlan> mealPlans = mealPlanningService.findUserMealPlansList(user);
        return ResponseEntity.ok(mealPlans);
    }

    /**
     * Mettre à jour un planning
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<MealPlan> updateMealPlan(
            @PathVariable Long id,
            @RequestBody MealPlan mealPlan,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        MealPlan updatedMealPlan = mealPlanningService.updateMealPlan(id, mealPlan, user);

        if (updatedMealPlan == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedMealPlan);
    }

    /**
     * Supprimer un planning
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Void> deleteMealPlan(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        mealPlanningService.deleteMealPlan(id, user);
        return ResponseEntity.ok().build();
    }

    /**
     * Calculer le résumé des produits d'un planning
     */
    @GetMapping("/{id}/products")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Map<String, Object>> getMealPlanProducts(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        MealPlan mealPlan = mealPlanningService.findMealPlanById(id);

        if (mealPlan == null || !mealPlan.getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> productSummary = mealPlanningService.calculateProductSummary(mealPlan);
        return ResponseEntity.ok(productSummary);
    }

    /**
     * Ajouter les produits d'un planning au panier
     */
    @PostMapping("/{id}/add-to-cart")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Map<String, String>> addMealPlanToCart(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        
        try {
            mealPlanningService.addMealPlanToCart(id, user);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Produits du planning ajoutés au panier avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Erreur lors de l'ajout au panier: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les statistiques d'un planning
     */
    @GetMapping("/{id}/stats")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Map<String, Object>> getMealPlanStats(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        MealPlan mealPlan = mealPlanningService.findMealPlanById(id);

        if (mealPlan == null || !mealPlan.getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> stats = mealPlanningService.getMealPlanStats(mealPlan);
        return ResponseEntity.ok(stats);
    }
}
