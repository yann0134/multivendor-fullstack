# ToolsChat - Intégration Chat IA

## Description
La classe `ToolsChat` rassemble tous les endpoints principaux de l'application multivendor avec les annotations `@Tool` pour l'intégration avec un chat IA.

## Fonctionnalités disponibles

### 🛍️ **Produits**
- `searchProducts(String query)` - Rechercher des produits par mot-clé
- `getProductById(Long id)` - Obtenir les détails d'un produit
- `getAllProducts(...)` - Lister tous les produits avec filtres
- `getApprovedProducts()` - Obtenir les produits approuvés
- `getProductsBySeller(Long sellerId)` - Produits par vendeur

### 🛒 **Panier**
- `getCart(User user)` - Obtenir le contenu du panier
- `addItemToCart(User user, Long productId, String size, int quantity)` - Ajouter un produit
- `updateCartItem(Long userId, Long cartItemId, int quantity)` - Modifier la quantité
- `removeCartItem(Long userId, Long cartItemId)` - Supprimer un article
- `clearCart(User user)` - Vider le panier

### 👤 **Profil Utilisateur**
- `getUserProfile(String jwtToken)` - Obtenir le profil utilisateur
- `updateUserProfile(User user)` - Mettre à jour le profil

### 📅 **Planning de Repas**
- `generateMealPlan(User user, GenerateMealPlanRequest request)` - Créer un planning
- `getMealPlanById(Long mealPlanId)` - Détails d'un planning
- `addMealPlanToCart(Long userId, User user)` - Ajouter au panier
- `deleteMealPlan(Long userId, User user)` - Supprimer un planning

### 🥗 **Recherche Nutritionnelle**
- `searchProductsByNutrition(String nutritionalInfo)` - Rechercher par informations nutritionnelles
- `getProductsByNutritionalValue(String nutrient, String value)` - Produits par valeur nutritionnelle
- `getHighProteinProducts()` - Produits riches en protéines
- `getHighFiberProducts()` - Produits riches en fibres
- `getLowSugarProducts()` - Produits pauvres en sucre
- `getProductsByVitamins(String vitamins)` - Produits par vitamines
- `getOrganicProducts()` - Produits biologiques

## Utilisation

### 1. Configuration Spring AI
Assurez-vous que les dépendances Spring AI sont correctement configurées dans le `pom.xml`.

### 2. Injection dans le Chat
```java
@Autowired
private ToolsChat toolsChat;
```

### 3. Exemples d'utilisation
```java
// Rechercher des produits
List<Product> products = toolsChat.searchProducts("tomate");

// Obtenir un produit
Product product = toolsChat.getProductById(1L);

// Ajouter au panier
CartItem item = toolsChat.addItemToCart(user, 1L, "M", 2);

// Créer un planning de repas
GenerateMealPlanRequest request = new GenerateMealPlanRequest();
request.setName("Planning Semaine");
request.setNumberOfDays(7);
MealPlanDTO plan = toolsChat.generateMealPlan(user, request);

// Recherche nutritionnelle
List<Product> highProtein = toolsChat.getHighProteinProducts();
List<Product> organic = toolsChat.getOrganicProducts();
List<Product> lowSugar = toolsChat.getLowSugarProducts();
List<Product> vitaminC = toolsChat.getProductsByVitamins("vitamine C");
```

## Avantages

✅ **Centralisation** : Tous les outils IA en un seul endroit  
✅ **Simplicité** : Interface claire et documentée  
✅ **Flexibilité** : Facilement extensible  
✅ **Maintenance** : Code organisé et maintenable  

## Notes importantes

- Tous les outils gèrent les exceptions et retournent des messages d'erreur clairs
- Les paramètres utilisateur sont validés avant utilisation
- Les annotations `@Tool` permettent l'intégration automatique avec Spring AI
- La classe est annotée `@Component` pour l'injection de dépendances
