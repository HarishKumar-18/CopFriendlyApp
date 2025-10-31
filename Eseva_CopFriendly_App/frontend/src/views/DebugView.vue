<template>
  <div class="min-h-screen bg-gray-100 p-4">
    <h1 class="text-3xl font-bold text-center text-red-600 mb-8">🔧 DEBUG PAGE</h1>
    <div class="max-w-2xl mx-auto bg-white p-6 rounded-lg shadow">
      <h2 class="text-xl font-bold mb-4">Authentication Debug Info:</h2>
      
      <div class="mb-4">
        <strong>Auth Store User:</strong>
        <pre class="bg-gray-100 p-2 mt-2 rounded">{{ JSON.stringify(authStore.user, null, 2) }}</pre>
      </div>
      
      <div class="mb-4">
        <strong>Auth Store Token:</strong>
        <pre class="bg-gray-100 p-2 mt-2 rounded">{{ authStore.token || 'No token' }}</pre>
      </div>
      
      <div class="mb-4">
        <strong>Is Authenticated:</strong>
        <pre class="bg-gray-100 p-2 mt-2 rounded">{{ authStore.isAuthenticated }}</pre>
      </div>
      
      <div class="mb-4">
        <strong>Local Storage Token:</strong>
        <pre class="bg-gray-100 p-2 mt-2 rounded">{{ localToken || 'No local token' }}</pre>
      </div>
      
      <div class="mb-4">
        <strong>Local Storage User:</strong>
        <pre class="bg-gray-100 p-2 mt-2 rounded">{{ localUser || 'No local user' }}</pre>
      </div>
      
      <div class="mb-4">
        <strong>Current Route:</strong>
        <pre class="bg-gray-100 p-2 mt-2 rounded">{{ $route.path }}</pre>
      </div>
      
      <button @click="goToLogin" class="bg-blue-500 text-white px-4 py-2 rounded mr-2">Go to Login</button>
      <button @click="goToDashboard" class="bg-green-500 text-white px-4 py-2 rounded mr-2">Go to Dashboard</button>
      <button @click="testAuth" class="bg-yellow-500 text-white px-4 py-2 rounded">Test Auth</button>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

export default {
  name: 'DebugView',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const authStore = useAuthStore()
    
    const localToken = ref('')
    const localUser = ref('')
    
    onMounted(() => {
      localToken.value = localStorage.getItem('token')
      localUser.value = localStorage.getItem('user')
    })
    
    const goToLogin = () => {
      router.push('/login')
    }
    
    const goToDashboard = () => {
      router.push('/dashboard')
    }
    
    const testAuth = () => {
      authStore.initializeAuth()
      localToken.value = localStorage.getItem('token')
      localUser.value = localStorage.getItem('user')
    }
    
    return {
      authStore,
      localToken,
      localUser,
      goToLogin,
      goToDashboard,
      testAuth
    }
  }
}
</script>