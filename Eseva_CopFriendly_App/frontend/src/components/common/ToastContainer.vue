<template>
  <div class="fixed inset-0 flex items-end justify-center px-4 py-6 pointer-events-none sm:p-6 sm:items-start sm:justify-end z-50">
    <div class="w-full flex flex-col items-center space-y-4 sm:items-end">
      <transition-group
        name="toast"
        tag="div"
        class="space-y-4"
      >
        <div
          v-for="toast in toasts"
          :key="toast.id"
          class="max-w-sm w-full bg-white shadow-lg rounded-lg pointer-events-auto ring-1 ring-black ring-opacity-5 overflow-hidden"
        >
          <div class="p-4">
            <div class="flex items-start">
              <div class="flex-shrink-0">
                <!-- Success icon -->
                <svg
                  v-if="toast.type === 'success'"
                  class="h-6 w-6 text-green-400"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                
                <!-- Error icon -->
                <svg
                  v-else-if="toast.type === 'error'"
                  class="h-6 w-6 text-red-400"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                
                <!-- Warning icon -->
                <svg
                  v-else-if="toast.type === 'warning'"
                  class="h-6 w-6 text-yellow-400"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.464 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z" />
                </svg>
                
                <!-- Info icon -->
                <svg
                  v-else
                  class="h-6 w-6 text-blue-400"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              
              <div class="ml-3 w-0 flex-1 pt-0.5">
                <p class="text-sm font-medium text-gray-900">
                  {{ toast.title }}
                </p>
                <p v-if="toast.message" class="mt-1 text-sm text-gray-500">
                  {{ toast.message }}
                </p>
              </div>
              
              <div class="ml-4 flex-shrink-0 flex">
                <button
                  @click="removeToast(toast.id)"
                  class="bg-white rounded-md inline-flex text-gray-400 hover:text-gray-500 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                >
                  <svg class="h-5 w-5" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
                  </svg>
                </button>
              </div>
            </div>
          </div>
          
          <!-- Progress bar -->
          <div
            v-if="toast.duration"
            class="h-1 bg-gray-200 relative overflow-hidden"
          >
            <div
              :class="[
                'h-full absolute inset-y-0 left-0 animate-progress',
                toast.type === 'success' ? 'bg-green-400' :
                toast.type === 'error' ? 'bg-red-400' :
                toast.type === 'warning' ? 'bg-yellow-400' : 'bg-blue-400'
              ]"
              :style="{ animationDuration: `${toast.duration}ms` }"
            ></div>
          </div>
        </div>
      </transition-group>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'

let toastId = 0

export default {
  name: 'ToastContainer',
  setup() {
    const toasts = ref([])

    const addToast = (toast) => {
      const id = ++toastId
      const newToast = {
        id,
        type: 'info',
        duration: 5000,
        ...toast
      }

      toasts.value.push(newToast)

      // Auto remove after duration
      if (newToast.duration) {
        setTimeout(() => {
          removeToast(id)
        }, newToast.duration)
      }
    }

    const removeToast = (id) => {
      const index = toasts.value.findIndex(toast => toast.id === id)
      if (index > -1) {
        toasts.value.splice(index, 1)
      }
    }

    const clearAll = () => {
      toasts.value = []
    }

    // Global toast methods
    const showSuccess = (title, message = '') => {
      addToast({ type: 'success', title, message })
    }

    const showError = (title, message = '') => {
      addToast({ type: 'error', title, message, duration: 7000 })
    }

    const showWarning = (title, message = '') => {
      addToast({ type: 'warning', title, message })
    }

    const showInfo = (title, message = '') => {
      addToast({ type: 'info', title, message })
    }

    // Make toast methods globally available
    onMounted(() => {
      window.$toast = {
        success: showSuccess,
        error: showError,
        warning: showWarning,
        info: showInfo,
        clear: clearAll
      }
    })

    return {
      toasts,
      removeToast
    }
  }
}
</script>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

@keyframes progress {
  from {
    width: 100%;
  }
  to {
    width: 0%;
  }
}

.animate-progress {
  animation: progress linear forwards;
}
</style>