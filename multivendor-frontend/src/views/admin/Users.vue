<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Gestion des Utilisateurs</h1>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title>Liste des utilisateurs</v-card-title>
          <v-card-text>
            <v-data-table
              :headers="headers"
              :items="users"
              :loading="loading"
            >
              <template v-slot:item.role="{ item }">
                <v-chip :color="getRoleColor(item.role)">
                  {{ item.role }}
                </v-chip>
              </template>
              <template v-slot:item.actions="{ item }">
                <v-btn icon @click="editUser(item)">
                  <v-icon>mdi-pencil</v-icon>
                </v-btn>
                <v-btn icon @click="deleteUser(item.id)">
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
              </template>
            </v-data-table>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const users = ref([])
const loading = ref(false)

const headers = [
  { title: 'Nom', key: 'fullName' },
  { title: 'Email', key: 'email' },
  { title: 'Rôle', key: 'role' },
  { title: 'Statut', key: 'status' },
  { title: 'Date d\'inscription', key: 'createdAt' },
  { title: 'Actions', key: 'actions', sortable: false }
]

const getRoleColor = (role) => {
  const colors = {
    'ROLE_CUSTOMER': 'blue',
    'ROLE_SELLER': 'green',
    'ROLE_SUPPLIER': 'orange',
    'ROLE_DELIVERY': 'info',
    'ROLE_WAREHOUSE': 'purple',
    'ROLE_ADMIN': 'red'
  }
  return colors[role] || 'grey'
}

const editUser = (user) => {
  console.log('Éditer l\'utilisateur:', user)
}

const deleteUser = (userId) => {
  console.log('Supprimer l\'utilisateur:', userId)
}

onMounted(() => {
  // Charger la liste des utilisateurs
})
</script>
