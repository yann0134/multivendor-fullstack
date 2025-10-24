# 🔧 Correction du problème 404 - Panier

## 🚨 **Problème identifié**
- Erreur 404 sur `POST /cart/add`
- L'endpoint backend utilise `PUT /api/cart/add` et non `POST`
- L'API attend un header `Authorization` avec le JWT

## ✅ **Corrections apportées**

### 1. **Store Cart (`cart.js`)**
- ✅ Changé `POST` → `PUT` pour `/cart/add`
- ✅ Ajout de vérification d'authentification
- ✅ Gestion d'erreurs améliorée avec messages spécifiques
- ✅ Logs de débogage pour tracer les problèmes

### 2. **ProductDetail.vue**
- ✅ Ajout de notifications de succès/erreur
- ✅ Gestion des erreurs avec messages utilisateur
- ✅ Import du composant NotificationToast

### 3. **Nouveau composant NotificationToast.vue**
- ✅ Notifications visuelles pour l'utilisateur
- ✅ Support des types : success, error, warning, info
- ✅ Auto-dismiss avec timeout configurable

## 🔍 **Endpoints backend identifiés**
```java
// CartController.java
@GetMapping("/api/cart")                    // GET panier
@PutMapping("/api/cart/add")                // PUT ajouter article
@PutMapping("/api/cart/item/{cartItemId}")  // PUT modifier quantité
@DeleteMapping("/api/cart/item/{cartItemId}") // DELETE supprimer article
```

## 🛠️ **Données envoyées**
```javascript
{
  productId: number,
  quantity: number,
  size: string  // conditionnement (kg, piece, Sac)
}
```

## 🔐 **Authentification requise**
- Header `Authorization: Bearer <jwt_token>`
- Token stocké dans `localStorage.getItem('jwt_token')`
- Redirection vers login si token manquant/expiré

## 🎯 **Test de fonctionnement**
1. Vérifier que l'utilisateur est connecté
2. Tester l'ajout d'un produit au panier
3. Vérifier les logs dans la console
4. Confirmer la notification de succès/erreur

## 📝 **Messages d'erreur possibles**
- "Utilisateur non authentifié. Veuillez vous connecter."
- "Session expirée. Veuillez vous reconnecter."
- "Service panier non disponible."
