<template>
  <v-container fluid>
    <v-row class="mb-4" align="center">
      <v-col cols="12" md="4">
        <h2 class="text-h5">Catégories</h2>
      </v-col>
      <v-col cols="12" md="5">
        <v-text-field
          v-model="search"
          placeholder="Rechercher par nom ou type..."
          append-inner-icon="mdi-magnify"
          density="comfortable"
          hide-details
          clearable
        />
      </v-col>
      <v-col cols="12" md="3" class="text-right">
        <v-btn color="primary" @click="openCreateCategoryDialog">
          <v-icon left>mdi-tag-plus</v-icon>
          Nouvelle Catégorie
        </v-btn>
      </v-col>
    </v-row>

    <v-data-table :headers="headers" :items="filteredItems" :loading="loading">
      <template #item.type="{ item }">
        <v-chip :color="item.type === 'ANIMAL' ? 'orange' : 'green'" size="small" text-color="white">{{ item.type }}</v-chip>
      </template>
      <template #item.active="{ item }">
        <v-chip :color="item.active ? 'green' : 'red'" size="small" text-color="white">{{ item.active ? 'Actif' : 'Inactif' }}</v-chip>
      </template>
      <template #item.createdBy="{ item }">
        <div class="text-caption">{{ item.createdByName || '—' }}</div>
        <div class="text-caption text-grey">{{ item.createdByEmail || '—' }}</div>
      </template>
    </v-data-table>
  </v-container>
  
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
  
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getAllCategories } from '@/services/categories'
import { createCategory } from '@/services/categories'

const loading = ref(false)
const items = ref([])
const search = ref('')
const headers = [
  { title: 'Nom', key: 'name' },
  { title: 'Type', key: 'type' },
  { title: 'Icône', key: 'icon' },
  { title: 'Couleur', key: 'color' },
  { title: 'Actif', key: 'active' },
  { title: 'Créé par', key: 'createdBy' },
  { title: 'Créée le', key: 'createdAt' }
]

const filteredItems = computed(() => {
  if (!search.value) return items.value
  const q = search.value.toLowerCase()
  return items.value.filter(c =>
    (c.name || '').toLowerCase().includes(q) ||
    (c.type || '').toLowerCase().includes(q)
  )
})

const load = async () => {
  try {
    loading.value = true
    const data = await getAllCategories()
    items.value = data
  } finally {
    loading.value = false
  }
}

onMounted(load)

// Dialog de création local à la page
const createCategoryDialog = ref(false)
const creating = ref(false)
const categoryForm = ref({ name: '', description: '', type: 'VEGETAL', icon: '', color: '', active: true })

const openCreateCategoryDialog = () => {
  createCategoryDialog.value = true
}

const submitCreateCategory = async () => {
  try {
    creating.value = true
    const payload = { ...categoryForm.value }
    await createCategory(payload)
    createCategoryDialog.value = false
    categoryForm.value = { name: '', description: '', type: 'VEGETAL', icon: '', color: '', active: true }
    await load()
  } finally {
    creating.value = false
  }
}
</script>


