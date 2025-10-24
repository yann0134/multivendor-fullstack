# 👤 Chargement des données du profil fournisseur

## 🎯 **Objectif :**
Permettre l'affichage des données du fournisseur connecté dans l'interface profil fournisseur.

## ✅ **Améliorations apportées :**

### **1. Ajout de l'endpoint GET dans ProductController :**

```java
/**
 * Récupérer le profil du fournisseur connecté
 */
@GetMapping("/supplier/profile")
@PreAuthorize("hasAuthority('ROLE_SUPPLIER')")
public ResponseEntity<Supplier> getSupplierProfile() {
    try {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        System.out.println("👤 Récupération du profil fournisseur: " + email);
        
        // Récupérer le fournisseur connecté
        Supplier supplier = supplierRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé pour l'email: " + email));
        
        System.out.println("✅ Profil fournisseur récupéré: " + supplier.getSupplierName());
        return ResponseEntity.ok(supplier);
        
    } catch (Exception e) {
        System.err.println("❌ Erreur lors de la récupération du profil fournisseur: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
```

### **2. Mise à jour de la fonction loadProfile dans le frontend :**

**AVANT (problématique) :**
```javascript
const loadProfile = async () => {
  try {
    console.log('👤 Chargement du profil fournisseur...')
    
    // Ici vous pouvez ajouter un endpoint pour récupérer le profil
    // const response = await api.get('/api/supplier/profile')
    // profileData.value = response.data
    
    console.log('✅ Profil chargé')
  } catch (error) {
    console.error('❌ Erreur lors du chargement du profil:', error)
  }
}
```

**APRÈS (fonctionnel) :**
```javascript
const loadProfile = async () => {
  try {
    console.log('👤 Chargement du profil fournisseur...')
    
    // Récupérer le profil fournisseur
    const response = await api.get('/api/products/supplier/profile')
    console.log('📊 Réponse API:', response.data)
    
    // Mapper les données reçues vers le format attendu
    const supplier = response.data
    profileData.value = {
      supplierName: supplier.supplierName || '',
      mobile: supplier.mobile || '',
      pickupAddress: {
        address: supplier.pickupAddress?.address || '',
        city: supplier.pickupAddress?.city || '',
        state: supplier.pickupAddress?.state || '',
        pinCode: supplier.pickupAddress?.pinCode || ''
      },
      businessDetails: {
        businessName: supplier.businessDetails?.businessName || '',
        businessAddress: supplier.businessDetails?.businessAddress || '',
        businessEmail: supplier.businessDetails?.businessEmail || '',
        businessMobile: supplier.businessDetails?.businessMobile || ''
      }
    }
    
    console.log('✅ Profil chargé:', profileData.value)
  } catch (error) {
    console.error('❌ Erreur lors du chargement du profil:', error)
    errorMessage.value = error.response?.data?.message || 'Erreur lors du chargement du profil'
    showError.value = true
  }
}
```

## 🎯 **Fonctionnalités ajoutées :**

### **✅ Endpoint GET /api/products/supplier/profile :**
- **Méthode** : `GET`
- **URL** : `/api/products/supplier/profile`
- **Headers** : `Authorization: Bearer <jwt_token>`
- **Response** : `Supplier` object complet
- **Sécurité** : Seuls les fournisseurs authentifiés peuvent accéder

### **✅ Données chargées automatiquement :**
- **Informations personnelles** : Nom du fournisseur, téléphone mobile
- **Adresse de ramassage** : Adresse, ville, état, code postal
- **Détails de l'entreprise** : Nom, adresse, email, téléphone de l'entreprise

### **✅ Mapping des données :**
```javascript
// Structure des données reçues du backend
const supplier = {
  supplierName: "Ferme Bio Dupont",
  mobile: "+33 6 12 34 56 78",
  pickupAddress: {
    address: "123 Rue de la Ferme",
    city: "Paris",
    state: "Île-de-France",
    pinCode: "75001"
  },
  businessDetails: {
    businessName: "Ferme Bio Dupont SARL",
    businessAddress: "123 Rue de la Ferme, 75001 Paris",
    businessEmail: "contact@fermebio.fr",
    businessMobile: "+33 1 23 45 67 89"
  }
}

// Mapping vers le format frontend
profileData.value = {
  supplierName: supplier.supplierName || '',
  mobile: supplier.mobile || '',
  pickupAddress: { /* ... */ },
  businessDetails: { /* ... */ }
}
```

## 🔄 **Flux de données :**

### **1. Chargement de la page :**
```javascript
onMounted(() => {
  loadProfile()  // ✅ Appel automatique
})
```

### **2. Appel API :**
```javascript
const response = await api.get('/api/products/supplier/profile')
```

### **3. Traitement backend :**
```java
// Récupérer le fournisseur connecté
Supplier supplier = supplierRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé"));
return ResponseEntity.ok(supplier);
```

### **4. Mapping frontend :**
```javascript
// Mapper les données vers le format attendu
profileData.value = {
  supplierName: supplier.supplierName || '',
  mobile: supplier.mobile || '',
  // ... autres champs
}
```

### **5. Affichage dans l'interface :**
- **Champs pré-remplis** : Toutes les informations du fournisseur
- **Formulaire modifiable** : L'utilisateur peut modifier et sauvegarder
- **Validation** : Règles de validation appliquées

## 🎯 **Données affichées :**

### **✅ Informations personnelles :**
- **Nom du fournisseur** : `supplierName`
- **Téléphone mobile** : `mobile`

### **✅ Adresse de ramassage :**
- **Adresse** : `pickupAddress.address`
- **Ville** : `pickupAddress.city`
- **État** : `pickupAddress.state`
- **Code postal** : `pickupAddress.pinCode`

### **✅ Détails de l'entreprise :**
- **Nom de l'entreprise** : `businessDetails.businessName`
- **Adresse de l'entreprise** : `businessDetails.businessAddress`
- **Email de l'entreprise** : `businessDetails.businessEmail`
- **Téléphone de l'entreprise** : `businessDetails.businessMobile`

## 🚀 **Résultat :**

L'interface profil fournisseur affiche maintenant **automatiquement** les données du fournisseur connecté ! 🎉

### **✅ Fonctionnalités disponibles :**
- **Chargement automatique** : Données pré-remplies au chargement
- **Affichage complet** : Toutes les informations du fournisseur
- **Modification** : Possibilité de modifier et sauvegarder
- **Validation** : Règles de validation appliquées
- **Gestion d'erreurs** : Messages d'erreur en cas de problème

### **✅ Tests recommandés :**
1. **Se connecter** en tant que fournisseur
2. **Accéder à la page profil** : `/supplier/profile`
3. **Vérifier l'affichage** des données du fournisseur
4. **Modifier et sauvegarder** les informations

Les données du fournisseur sont maintenant **automatiquement chargées** dans l'interface ! 🚀
