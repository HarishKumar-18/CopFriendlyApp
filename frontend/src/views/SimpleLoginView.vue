<template>
  <div class="login-container">
    <div class="login-card">
      <h1>🚨 CopFriendlyApp - ESeva</h1>
      <p class="text-center mb-3">Welcome to Traffic Management System</p>
      
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">Username / Employee ID</label>
          <input
            id="username"
            v-model="form.username"
            type="text"
            class="form-control"
            placeholder="Enter your username"
            required
          >
        </div>
        
        <div class="form-group">
          <label for="password">Password</label>
          <div class="input-affix">
            <input
              id="password"
              v-model="form.password"
              :type="showPassword ? 'text' : 'password'"
              class="form-control password-input"
              placeholder="Enter your password"
              required
            />
            <button
              type="button"
              @click="togglePasswordVisibility"
              :aria-pressed="showPassword"
              :aria-label="showPassword ? 'Hide password' : 'Show password'"
              title="Toggle password visibility"
              class="password-toggle"
            >
              <svg v-if="!showPassword" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029" />
                <path d="M6.22 6.22L3 3" />
                <path d="M9.878 9.878l4.242 4.242" />
              </svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                <path d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
              </svg>
            </button>
          </div>
        </div>
        
        <div class="form-group">
          <label for="role">Role</label>
          <select id="role" v-model="form.role" class="form-control" required>
            <option value="">Select your role</option>
            <option value="admin">👨‍💼 System Administrator</option>
            <option value="traffic_sergeant">Traffic Sergeant</option>
            <option value="traffic_inspector">Traffic Inspector</option>
            <option value="traffic_central_team">Central Team</option>
            <option value="medical_emergency_central">Medical Emergency Team</option>
            <option value="public">🌐 Public User</option>
          </select>
        </div>
        
        <button type="submit" class="btn btn-block" :disabled="loading">
          {{ loading ? 'Logging in...' : 'Login' }}
        </button>
      </form>
      
      <!-- Demo credentials removed for production -->
      
      <!-- Sign Up Link -->
      <div style="text-align: center; margin-top: 2rem; padding-top: 1rem; border-top: 1px solid #e5e7eb;">
        <p style="color: #6b7280; margin-bottom: 0.5rem;">Don't have an account?</p>
        <button 
          type="button" 
          @click="navigateToSignup"
          style="background: none; border: none; color: #3b82f6; text-decoration: underline; cursor: pointer; font-size: 1rem; font-weight: 500;"
        >
          📝 Create New Account
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

export default {
  name: 'SimpleLoginView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const loading = ref(false)
    const form = ref({
      username: '',
      password: '',
      role: ''
    })

    const showPassword = ref(false)

    const togglePasswordVisibility = () => {
      showPassword.value = !showPassword.value
    }

    const handleLogin = async () => {
      try {
        loading.value = true
        
        // Use auth store login method with user status checking
        const result = await authStore.login({
          username: form.value.username,
          password: form.value.password,
          role: form.value.role
        })
        
        if (result.success) {
          // Redirect to dashboard (SimpleDashboardView will auto-redirect based on role)
          router.push('/dashboard')
        } else {
          alert('❌ Login Failed\n\n' + result.message)
        }
        
      } catch (error) {
        console.error('Login error:', error)
        alert('Login failed: ' + error.message)
      } finally {
        loading.value = false
      }
    }

    const navigateToSignup = () => {
      router.push('/signup')
    }

    return {
      form,
      loading,
      showPassword,
      togglePasswordVisibility,
      handleLogin,
      navigateToSignup
    }
  }
}
</script>

<style scoped>
.input-affix { position: relative; }
.password-input { padding-right: 2.5rem; }
.password-toggle {
  position: absolute;
  right: 0.6rem;
  top: 50%;
  transform: translateY(-50%);
  background: transparent;
  border: none;
  padding: 0;
  color: #374151; /* fallback color */
  display: flex;
  align-items: center;
  justify-content: center;
  width: 1.9rem;
  height: 1.9rem;
  cursor: pointer;
  z-index: 2;
}
.password-toggle:focus { outline: none; }
.password-toggle svg { display: block; }
</style>