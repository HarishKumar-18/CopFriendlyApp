<template>
  <div class="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8 relative overflow-hidden">
    <!-- Background image -->
    <div class="absolute inset-0 bg-cover bg-center" style="background-image: url('/public/bg-login.jpg'); filter: saturate(0.95) contrast(1.05);"></div>
    <!-- Dark overlay -->
    <div class="absolute inset-0 bg-gradient-to-br from-black/45 via-black/35 to-black/45"></div>

    <div class="relative z-10 max-w-md w-full space-y-8 backdrop-blur-md bg-white/10 border border-white/10 rounded-2xl p-8">
      <!-- Header -->
      <div>
  <div class="mx-auto h-12 w-12 bg-primary-600/85 rounded-lg flex items-center justify-center shadow-lg">
          <svg class="h-8 w-8 text-white" fill="currentColor" viewBox="0 0 20 20">
            <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
        </div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
          CopFriendly ESeva
        </h2>
        <p class="mt-2 text-center text-sm text-gray-200">
          Sign in to your account
        </p>
      </div>

      <!-- Login Form -->
      <form @submit.prevent="handleLogin" class="mt-8 space-y-6">
        <!-- Error Message -->
        <div v-if="error" class="bg-red-600/20 border border-red-500/20 rounded-md p-4">
          <div class="flex">
            <svg class="h-5 w-5 text-red-400" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
            </svg>
            <div class="ml-3">
              <p class="text-sm text-red-100">{{ error }}</p>
            </div>
          </div>
        </div>

        <div class="space-y-4">
          <!-- Username -->
          <div>
            <label for="username" class="block text-sm font-medium text-gray-700">
              Username
            </label>
            <div class="mt-1">
              <input
                id="username"
                v-model="loginForm.username"
                @input="clearError"
                type="text"
                autocomplete="username"
                required
                :disabled="loading"
                class="material-input bg-white/10 placeholder-gray-200 text-white"
                placeholder="Enter your username"
              />
            </div>
          </div>

          <!-- Password -->
          <div>
            <label for="password" class="block text-sm font-medium text-gray-700">
              Password
            </label>
            <div class="mt-1 relative">
              <input
                id="password"
                v-model="loginForm.password"
                @input="clearError"
                :type="showPassword ? 'text' : 'password'"
                autocomplete="current-password"
                required
                :disabled="loading"
                class="material-input pr-10 bg-white/10 placeholder-gray-200 text-white"
                style="padding-right: 2.5rem"
                placeholder="Enter your password"
              />
              <button
                type="button"
                @click="togglePasswordVisibility"
                :aria-pressed="showPassword"
                :aria-label="showPassword ? 'Hide password' : 'Show password'"
                title="Toggle password visibility"
                class="absolute inset-y-0 right-0 pr-3 flex items-center z-10"
                style="position: absolute; right: 0.5rem; top: 50%; transform: translateY(-50%); background: transparent; border: none; padding: 0; color: #374151;"
              >
                <!-- Open eye when password is visible -->
                <svg v-if="showPassword" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
                <!-- Slashed/closed eye when password is hidden -->
                <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029" />
                  <path d="M6.22 6.22L3 3" />
                  <path d="M9.878 9.878l4.242 4.242" />
                </svg>
              </button>
            </div>
          </div>

          <!-- User Role -->
          <div>
            <label for="role" class="block text-sm font-medium text-gray-700">
              User Type
            </label>
            <div class="mt-1">
              <select
                id="role"
                v-model="loginForm.role"
                required
                :disabled="loading"
                class="material-select bg-white/10 text-white"
              >
                <option value="">Select your role</option>
                <option value="admin">👨‍💼 System Administrator</option>
                <option value="traffic_sergeant">👮 Traffic Sergeant</option>
                <option value="traffic_inspector">🎖️ Traffic Inspector</option>
                <option value="traffic_central_team">🏛️ Traffic Central Team</option>
                <option value="medical_emergency_central">🚑 Medical Emergency Team</option>
                <option value="public">🌐 Public User</option>
              </select>
            </div>
          </div>

          <!-- Remember me -->
          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <input
                id="remember-me"
                v-model="loginForm.rememberMe"
                type="checkbox"
                class="material-checkbox"
              />
              <label for="remember-me" class="ml-2 block text-sm text-gray-900">
                Remember me
              </label>
            </div>

            <div class="text-sm">
              <button
                type="button"
                class="font-medium text-white/90 hover:text-white/70"
                @click="showForgotPassword"
              >
                Forgot password?
              </button>
            </div>
          </div>
        </div>

        <!-- Submit Button -->
        <div>
            <button
            type="submit"
            :disabled="loading || !isFormValid"
            class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <span v-if="loading" class="absolute left-0 inset-y-0 flex items-center pl-3">
              <div class="spinner"></div>
            </span>
            <span v-else class="absolute left-0 inset-y-0 flex items-center pl-3">
              <svg class="h-5 w-5 text-primary-500 group-hover:text-primary-400" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd" />
              </svg>
            </span>
            {{ loading ? 'Signing in...' : 'Sign in' }}
          </button>
        </div>

      </form>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { validators } from '@/utils/helpers'

export default {
  name: 'LoginView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()

    // Reactive state
    const loginForm = ref({
      username: '',
      password: '',
      role: '',
      rememberMe: false
    })
    
    const showPassword = ref(false)
    const loading = ref(false)
    const error = ref('')

    // Computed properties
    const isFormValid = computed(() => {
      return loginForm.value.username.trim() &&
             loginForm.value.password.trim() &&
             loginForm.value.role
    })

    // Methods
    const togglePasswordVisibility = () => {
      showPassword.value = !showPassword.value
    }

    const handleLogin = async () => {
      if (!isFormValid.value) {
        error.value = 'Please fill in all required fields'
        return
      }

      try {
        loading.value = true
        error.value = ''

        const result = await authStore.login({
          username: loginForm.value.username.trim(),
          password: loginForm.value.password,
          role: loginForm.value.role,
          rememberMe: loginForm.value.rememberMe
        })

        if (result.success) {
          // Show success message
          if (window.$toast) {
            window.$toast.success('Login successful', 'Welcome to CopFriendly ESeva')
          }

          // If public role, route to public violations page
          if (loginForm.value.role === 'public') {
            router.push('/public/violations')
          } else {
            // Redirect to dashboard for other roles
            router.push('/dashboard')
          }
        } else {
          error.value = result.message || 'Login failed'
          // show backend message as a toast (if available) for immediate feedback
          if (result.message) {
            // Prefer to show a styled toast if available, otherwise fallback to a native alert
            if (window.$toast) {
              window.$toast.error(result.message, 'Login failed')
            } else {
              // For critical auth errors (username/password), show a native alert so operator notices immediately
              if (result.message === 'Username is wrong' || result.message === 'Password is wrong') {
                try { window.alert(result.message) } catch (e) { /* ignore */ }
              }
            }
          }
        }
      } catch (err) {
        console.error('Login error:', err)
        error.value = 'Network error. Please try again.'
      } finally {
        loading.value = false
      }
    }

    const showForgotPassword = () => {
      if (window.$toast) {
        window.$toast.info('Contact Administrator', 'Please contact your system administrator for password reset')
      }
    }

    // Demo autofill removed for production

    // Clear error when form changes
    const clearError = () => {
      if (error.value) {
        error.value = ''
      }
    }

    onMounted(() => {
      // Clear any existing auth errors
      authStore.clearError()
      
      // Focus on username field
      const usernameField = document.getElementById('username')
      if (usernameField) {
        usernameField.focus()
      }
    })

    return {
      loginForm,
      showPassword,
      loading,
      error,
      isFormValid,
      togglePasswordVisibility,
      handleLogin,
      showForgotPassword,
      clearError
    }
  }
}
</script>

<style scoped>
/* Additional custom styles if needed */
</style>