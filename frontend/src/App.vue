<template>
  <div id="app" class="min-h-screen bg-gray-50">
    <!-- Loading overlay -->
    <div v-if="isInitializing" class="fixed inset-0 bg-white flex items-center justify-center z-50">
      <div class="text-center">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mx-auto mb-4"></div>
        <p class="text-gray-600">Loading CopFriendlyApp...</p>
      </div>
    </div>
    
    <!-- Main application -->
    <template v-else>
      <!-- Main content -->
      <main>
        <!-- Global overlays for toasts and modals -->
        <Toast />
        <ConfirmModal />
        <InputModal />
        <router-view />
      </main>
    </template>
  </div>
</template>

<script>
import Toast from '@/components/Toast.vue'
import ConfirmModal from '@/components/ConfirmModal.vue'
import InputModal from '@/components/InputModal.vue'
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

export default {
  name: 'App',
  components: {
    Toast,
    ConfirmModal,
    InputModal
  },
  setup() {
    const route = useRoute()
    const authStore = useAuthStore()
    const isInitializing = ref(true)

    // Computed properties
    const isAuthenticated = computed(() => authStore.isAuthenticated)

    // Initialize app
    const initializeApp = async () => {
      try {
        // Initialize authentication state
        authStore.initializeAuth()
        
        // Add small delay for smooth transition
        await new Promise(resolve => setTimeout(resolve, 800))
      } catch (error) {
        console.error('App initialization error:', error)
      } finally {
        isInitializing.value = false
      }
    }

    onMounted(() => {
      initializeApp()
    })

    return {
      isInitializing,
      isAuthenticated
    }
  }
}
</script>

<style>
/* Global styles are imported in main.js */
</style>
