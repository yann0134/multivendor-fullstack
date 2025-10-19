<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Mes Tâches de Livraison</h1>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title>Gestion des tâches</v-card-title>
          <v-card-text>
            <v-data-table
              :headers="headers"
              :items="tasks"
              :loading="loading"
            >
              <template v-slot:item.status="{ item }">
                <v-chip :color="getStatusColor(item.status)">
                  {{ item.status }}
                </v-chip>
              </template>
              <template v-slot:item.actions="{ item }">
                <v-btn
                  v-if="item.status === 'ASSIGNED'"
                  color="success"
                  size="small"
                  @click="startTask(item.id)"
                >
                  Commencer
                </v-btn>
                <v-btn
                  v-if="item.status === 'IN_PROGRESS'"
                  color="info"
                  size="small"
                  @click="completeTask(item.id)"
                >
                  Terminer
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

const tasks = ref([])
const loading = ref(false)

const headers = [
  { title: 'ID Tâche', key: 'id' },
  { title: 'Type', key: 'taskType' },
  { title: 'Adresse', key: 'deliveryAddress' },
  { title: 'Statut', key: 'status' },
  { title: 'Date', key: 'createdAt' },
  { title: 'Actions', key: 'actions', sortable: false }
]

const getStatusColor = (status) => {
  const colors = {
    'ASSIGNED': 'orange',
    'IN_PROGRESS': 'blue',
    'COMPLETED': 'green',
    'CANCELLED': 'red'
  }
  return colors[status] || 'grey'
}

const startTask = (taskId) => {
  console.log('Commencer la tâche:', taskId)
}

const completeTask = (taskId) => {
  console.log('Terminer la tâche:', taskId)
}

onMounted(() => {
  // Charger les tâches du livreur
})
</script>
