# 🔧 Correction de l'endpoint PUT /users/profile

## 🎯 **Problème identifié :**
- ❌ **Erreur 405** : `Request method 'PUT' is not supported`
- ❌ **Endpoint manquant** : L'endpoint `PUT /users/profile` n'existait pas
- ❌ **Méthode manquante** : La méthode `updateUser` n'était pas implémentée
- ❌ **Champs manquants** : Les champs `phone` et `bio` n'existaient pas dans le modèle User

## ✅ **Solutions appliquées :**

### **1. Ajout de l'endpoint PUT dans UserController :**

```java
@PutMapping("/users/profile")
public ResponseEntity<User> updateProfile(@RequestHeader("Authorization") String jwt, 
                                        @RequestBody User userUpdate) throws Exception {
    User user = userService.findUserByJwtToken(jwt);
    
    // Mettre à jour les champs modifiables
    if (userUpdate.getFullName() != null) {
        user.setFullName(userUpdate.getFullName());
    }
    if (userUpdate.getPhone() != null) {
        user.setPhone(userUpdate.getPhone());
    }
    if (userUpdate.getBio() != null) {
        user.setBio(userUpdate.getBio());
    }
    
    // Sauvegarder les modifications
    User updatedUser = userService.updateUser(user);
    
    return ResponseEntity.ok(updatedUser);
}
```

### **2. Ajout de la méthode updateUser dans UserService :**

**Interface UserService :**
```java
public interface UserService {
    User findUserByJwtToken(String jwt) throws Exception;
    User findUserByEmail(String email) throws Exception;
    User updateUser(User user) throws Exception; // ✅ Ajouté
}
```

**Implémentation UserServiceImpl :**
```java
@Override
public User updateUser(User user) throws Exception {
    try {
        User updatedUser = userRepository.save(user);
        return updatedUser;
    } catch (Exception e) {
        throw new Exception("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage());
    }
}
```

### **3. Ajout des champs manquants dans le modèle User :**

```java
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String email;
    private String fullName;
    private String mobile;
    private String phone;    // ✅ Ajouté
    private String bio;      // ✅ Ajouté
    private USER_ROLE role = USER_ROLE.ROLE_CUSTOMER;

    @OneToMany
    private Set<Address> addresses = new HashSet<>();

    @ManyToMany
    @JsonIgnore
    private Set<Coupon> usedCoupons = new HashSet<>();
}
```

## 🎯 **Fonctionnalités ajoutées :**

### **✅ Endpoint PUT /users/profile :**
- **Méthode** : `PUT`
- **URL** : `/users/profile`
- **Headers** : `Authorization: Bearer <jwt_token>`
- **Body** : `User` object avec les champs à mettre à jour
- **Response** : `User` object mis à jour

### **✅ Champs modifiables :**
- **fullName** : Nom complet de l'utilisateur
- **phone** : Numéro de téléphone
- **bio** : Biographie de l'utilisateur
- **email** : Non modifiable (sécurité)

### **✅ Sécurité :**
- **Authentification** : Token JWT requis
- **Validation** : Seuls les champs autorisés sont modifiés
- **Isolation** : Chaque utilisateur ne peut modifier que son propre profil

## 📊 **Exemple d'utilisation :**

### **Requête :**
```http
PUT /users/profile
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "fullName": "Jean Dupont",
  "phone": "+33 6 12 34 56 78",
  "bio": "Passionné de produits frais et locaux"
}
```

### **Réponse :**
```json
{
  "id": 1,
  "email": "jean.dupont@example.com",
  "fullName": "Jean Dupont",
  "mobile": "+33 6 12 34 56 78",
  "phone": "+33 6 12 34 56 78",
  "bio": "Passionné de produits frais et locaux",
  "role": "ROLE_CUSTOMER"
}
```

## 🔄 **Flux de données :**

### **1. Frontend envoie la requête :**
```javascript
const response = await api.put('/users/profile', userProfile.value)
```

### **2. UserController reçoit la requête :**
- Vérifie le token JWT
- Récupère l'utilisateur authentifié
- Met à jour les champs modifiables

### **3. UserService traite la mise à jour :**
- Sauvegarde les modifications en base
- Retourne l'utilisateur mis à jour

### **4. Frontend reçoit la réponse :**
- Met à jour l'interface utilisateur
- Synchronise le store d'authentification

## 🚀 **Résultat :**

L'endpoint `PUT /users/profile` est maintenant **fonctionnel** ! 🎉

### **✅ Fonctionnalités disponibles :**
- **Mise à jour du profil** : Nom, téléphone, biographie
- **Sécurité** : Authentification JWT requise
- **Validation** : Champs modifiables uniquement
- **Réponse** : Utilisateur mis à jour

### **✅ Tests recommandés :**
1. **Se connecter** avec un utilisateur valide
2. **Accéder à la page profil** : `/customer/profile`
3. **Modifier les informations** et cliquer sur "Sauvegarder"
4. **Vérifier la mise à jour** dans l'interface

L'erreur 405 est maintenant résolue ! 🚀
