<template>
  <div class="min-h-screen bg-gray-100 p-4">
    <h1 class="text-3xl font-bold text-center text-blue-600 mb-8">✅ TEST: Violations List</h1>
    <div class="max-w-md mx-auto bg-white p-6 rounded-lg shadow">
      <p>If you can see this, the violations list is loading correctly!</p>
      <p>User: {{ authStore.user?.name || 'Not logged in' }}</p>
      <p>Role: {{ authStore.user?.role || 'No role' }}</p>
      <button @click="goToCreate" class="mt-4 bg-blue-500 text-white px-4 py-2 rounded mr-2">Create Violation</button>
      <button @click="goToDashboard" class="mt-4 bg-gray-500 text-white px-4 py-2 rounded">Dashboard</button>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

export default {
  name: 'ViolationsListView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const goToCreate = () => {
      router.push('/violations/create')
    }
    
    const goToDashboard = () => {
      router.push('/dashboard')
    }
    
    return {
      authStore,
      goToCreate,
      goToDashboard
    }
  }
}
</script>