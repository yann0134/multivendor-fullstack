<template>
  <v-app>
    <!-- Navigation latérale -->
    <v-navigation-drawer
      v-model="drawer"
      :temporary="!drawer"
      location="left"
      width="280"
      class="agri-slide-in agri-drawer-scroll"
    >
      <div class="pa-4">
        <div class="d-flex align-center mb-4">
          <v-icon size="32" color="error" class="mr-2">⚙️</v-icon>
          <span class="text-h6 font-weight-bold">AgriMarket</span>
        </div>
        <v-divider class="mb-4"></v-divider>
        <div class="text-caption text-grey-600 mb-2">Espace Administrateur</div>
        <div class="text-body-2 font-weight-medium">{{ userEmail }}</div>
      </div>

      <v-list density="comfortable">
        <v-list-item 
          prepend-icon="mdi-view-dashboard" 
          title="Tableau de bord" 
          to="/admin"
          class="mb-1"
        />
        
        <v-divider class="my-2"></v-divider>
        
        <v-list-subheader class="text-error font-weight-bold">
          ✅ Validation Produits
        </v-list-subheader>
        
        <v-list-item 
          prepend-icon="mdi-clipboard-check" 
          title="Validation Produits" 
          to="/admin/validation"
          class="mb-1"
        />

        <v-list-item 
          prepend-icon="mdi-tag" 
          title="Lister Catégories" 
          to="/admin/categories"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-clock-outline" 
          title="En Attente" 
          to="/admin/pending"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-check-circle" 
          title="Approuvés" 
          to="/admin/approved"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-close-circle" 
          title="Rejetés" 
          to="/admin/rejected"
          class="mb-1"
        />
        
        <v-divider class="my-2"></v-divider>
        
        <v-list-subheader class="text-error font-weight-bold">
          👥 Gestion Utilisateurs
        </v-list-subheader>
        
        <v-list-item 
          prepend-icon="mdi-account-group" 
          title="Tous les Utilisateurs" 
          to="/admin/users"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-account-hard-hat" 
          title="Fournisseurs" 
          to="/admin/suppliers"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-account" 
          title="Clients" 
          to="/admin/customers"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-truck" 
          title="Agents/Livreurs" 
          to="/admin/delivery"
          class="mb-1"
        />
        
        <v-divider class="my-2"></v-divider>
        
        <v-list-subheader class="text-error font-weight-bold">
          📊 Analytics & Rapports
        </v-list-subheader>
        
        <v-list-item 
          prepend-icon="mdi-chart-line" 
          title="Analytics" 
          to="/admin/analytics"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-chart-pie" 
          title="Rapports" 
          to="/admin/reports"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-currency-usd" 
          title="Finances" 
          to="/admin/finances"
          class="mb-1"
        />
        
        <v-divider class="my-2"></v-divider>
        
        <v-list-subheader class="text-error font-weight-bold">
          ⚙️ Configuration
        </v-list-subheader>
        
        <v-list-item 
          prepend-icon="mdi-cog" 
          title="Paramètres" 
          to="/admin/settings"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-database" 
          title="Base de Données" 
          to="/admin/database"
          class="mb-1"
        />
        
        <v-list-item 
          prepend-icon="mdi-logout" 
          title="Déconnexion" 
          @click="logout"
          class="mb-1"
        />
      </v-list>
    </v-navigation-drawer>

    <!-- Barre d'application -->
    <v-app-bar color="error" dark elevation="2" class="agri-fade-in">
      <v-app-bar-nav-icon @click="drawer = !drawer" />
      
      <v-toolbar-title class="d-flex align-center">
        <v-icon size="24" class="mr-2">⚙️</v-icon>
        <span class="text-h6">AgriMarket - Administration</span>
      </v-toolbar-title>
      
      <v-spacer />
      
      <!-- Notifications critiques -->
      <v-btn icon class="mr-2">
        <v-badge content="5" color="orange" size="small">
        <v-icon>mdi-bell</v-icon>
        </v-badge>
      </v-btn>
      
      <!-- Menu utilisateur -->
      <v-menu>
        <template v-slot:activator="{ props }">
          <v-btn icon v-bind="props">
            <v-avatar size="32" color="white">
              <v-icon>mdi-account</v-icon>
            </v-avatar>
          </v-btn>
        </template>
        
        <v-list>
          <v-list-item>
            <v-list-item-title>Mon Profil</v-list-item-title>
          </v-list-item>
          <v-list-item>
            <v-list-item-title>Paramètres</v-list-item-title>
          </v-list-item>
          <v-divider></v-divider>
          <v-list-item @click="logout">
            <v-list-item-title>Déconnexion</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-app-bar>

    <!-- Contenu principal -->
    <v-main>
      <router-view />
    </v-main>

    <!-- Dialog création catégorie -->
    <v-dialog v-model="createCategoryDialog" max-width="520">
      <v-card>
        <v-card-title>Nouvelle Catégorie</v-card-title>
        <v-card-text>
          <v-form @submit.prevent="submitCreateCategory">
            <v-text-field v-model="categoryForm.name" label="Nom" required />
            <v-text-field v-model="categoryForm.description" label="Description" />
            <v-select
              v-model="categoryForm.type"
              :items="[{title:'Animal', value:'ANIMAL'},{title:'Végétal', value:'VEGETAL'}]"
              label="Type"
              required
            />
            <v-text-field v-model="categoryForm.icon" label="Icône (mdi-*)" />
            <v-text-field v-model="categoryForm.color" label="Couleur (#RRGGBB)" />
            <v-switch v-model="categoryForm.active" label="Active" />
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="createCategoryDialog=false">Annuler</v-btn>
          <v-btn color="primary" @click="submitCreateCategory" :loading="creating">Créer</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-app>
</template>

<script setup>
import { ref, computed } from 'vue'
import { createCategory } from '@/services/categories'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const drawer = ref(false)

// État calculé
const userEmail = computed(() => authStore.user?.email || 'admin@agrimarket.com')

// Méthodes
const logout = () => {
  authStore.logout()
  router.push('/')
}

// Création catégorie
const createCategoryDialog = ref(false)
const creating = ref(false)
const categoryForm = ref({ name: '', description: '', type: 'VEGETAL', icon: '', color: '', active: true })

const openCreateCategoryDialog = () => {
  createCategoryDialog.value = true
}

const submitCreateCategory = async () => {
  try {
    creating.value = true
    const payload = {
      name: categoryForm.value.name,
      description: categoryForm.value.description,
      type: categoryForm.value.type,
      icon: categoryForm.value.icon,
      color: categoryForm.value.color,
      active: categoryForm.value.active
    }
    await createCategory(payload)
    createCategoryDialog.value = false
    categoryForm.value = { name: '', description: '', type: 'VEGETAL', icon: '', color: '', active: true }
  } catch (e) {
    console.error('Erreur création catégorie', e)
  } finally {
    creating.value = false
  }
}
</script>
