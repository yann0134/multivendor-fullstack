# 🐛 Debug - Problème 404 Cart

## 🔍 **Étapes de débogage**

### 1. **Vérifier les logs dans la console**
Ouvrez la console du navigateur (F12) et regardez les logs :
```
🚀 Requête API: { method: 'PUT', url: '/cart/add', ... }
🛒 Ajout au panier: { productId: 1, quantity: 1, size: 'kg' }
```

### 2. **Vérifier l'authentification**
```javascript
// Dans la console du navigateur
console.log('Token JWT:', localStorage.getItem('jwt_token'))
```

### 3. **Tester l'API directement**
Ouvrez `test-cart-api.html` dans le navigateur et cliquez sur "Tester l'API Cart"

### 4. **Vérifier le backend**
```bash
# Vérifier que le serveur backend fonctionne
curl -X PUT http://localhost:3026/api/cart/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"productId":1,"quantity":1,"size":"kg"}'
```

## 🚨 **Causes possibles**

### A. **Cache du navigateur**
- Solution : Vider le cache (Ctrl+Shift+R)
- Ou ouvrir en navigation privée

### B. **Token JWT manquant/invalide**
- Vérifier que l'utilisateur est connecté
- Vérifier que le token n'est pas expiré

### C. **Configuration CORS**
- Le backend doit autoriser les requêtes PUT
- Vérifier les headers CORS

### D. **Problème de routage**
- Vérifier que l'endpoint `/cart/add` existe
- Vérifier que la méthode PUT est supportée

## 🔧 **Solutions**

### 1. **Forcer le rechargement**
```bash
# Vider le cache et recharger
Ctrl + Shift + R
```

### 2. **Vérifier l'URL complète**
Les logs doivent afficher :
```
🌐 URL complète: http://localhost:3026/api/cart/add
📡 Méthode HTTP: PUT
```

### 3. **Tester avec Postman/Insomnia**
```
Method: PUT
URL: http://localhost:3026/api/cart/add
Headers: 
  - Content-Type: application/json
  - Authorization: Bearer YOUR_TOKEN
Body:
{
  "productId": 1,
  "quantity": 1,
  "size": "kg"
}
```

## 📝 **Logs à surveiller**

### Frontend (Console)
```
🚀 Requête API: { method: 'PUT', url: '/cart/add', ... }
🛒 Ajout au panier: { productId: 1, quantity: 1, size: 'kg' }
🔑 Token JWT: Présent
```

### Backend (Logs serveur)
```
PUT /cart/add - 200 OK
ou
PUT /cart/add - 404 Not Found
```

## 🎯 **Résolution attendue**

Après ces corrections, vous devriez voir :
- ✅ Méthode PUT dans les logs
- ✅ URL complète correcte
- ✅ Token JWT présent
- ✅ Réponse 200 OK du backend
