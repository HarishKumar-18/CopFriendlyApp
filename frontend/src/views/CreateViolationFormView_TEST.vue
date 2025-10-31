<template>
  <div class="min-h-screen bg-gray-100 p-4">
    <h1 class="text-3xl font-bold text-center text-green-600 mb-8">✅ TEST: Create Violation Form</h1>
    <div class="max-w-md mx-auto bg-white p-6 rounded-lg shadow">
      <p>If you can see this, the component is loading correctly!</p>
      <p>User: {{ authStore.user?.name || 'Not logged in' }}</p>
      <p>Role: {{ authStore.user?.role || 'No role' }}</p>
      <button @click="goBack" class="mt-4 bg-green-500 text-white px-4 py-2 rounded">Go Back</button>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

export default {
  name: 'CreateViolationFormView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const goBack = () => {
      router.back()
    }
    
    return {
      authStore,
      goBack
    }
  }
}
</script>