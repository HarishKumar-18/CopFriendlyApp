<template>
  <div style="min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); display: flex; align-items: center; justify-content: center; padding: 1rem;">
    <div style="width: 100%; max-width: 500px; background: white; border-radius: 16px; box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25); overflow: hidden;">
      
      <!-- Header -->
      <div style="background: linear-gradient(135deg, #3b82f6, #1e40af); color: white; padding: 2rem; text-align: center;">
        <div style="font-size: 3rem; margin-bottom: 0.5rem;">🚓</div>
        <h1 style="font-size: 1.8rem; font-weight: bold; margin: 0;">CopFriendly ESeva</h1>
        <p style="margin: 0.5rem 0 0 0; opacity: 0.9;">Create Your Account</p>
      </div>

      <!-- Registration Form -->
      <div style="padding: 2rem;">
        <form @submit.prevent="handleSignup">
          
          <!-- Personal Information -->
          <div style="margin-bottom: 2rem;">
            <h3 style="color: #374151; margin-bottom: 1rem; font-size: 1.1rem;">👤 Personal Information</h3>
            
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem;">
              <div class="form-group">
                <label for="firstName">First Name *</label>
                <input 
                  id="firstName"
                  v-model="signupForm.firstName" 
                  @input="console.log('First name changed:', signupForm.firstName)"
                  type="text" 
                  class="form-control" 
                  required
                  placeholder="Enter first name"
                >
              </div>
              <div class="form-group">
                <label for="lastName">Last Name *</label>
                <input 
                  id="lastName"
                  v-model="signupForm.lastName" 
                  type="text" 
                  class="form-control" 
                  required
                  placeholder="Enter last name"
                >
              </div>
            </div>

            <div class="form-group" style="margin-bottom: 1rem;">
              <label for="email">Email Address *</label>
              <input 
                id="email"
                v-model="signupForm.email" 
                type="email" 
                class="form-control" 
                required
                placeholder="Enter your email"
              >
              <div v-if="emailError" style="color: #dc2626; font-size: 0.8rem; margin-top: 0.25rem;">
                {{ emailError }}
              </div>
            </div>

            <div class="form-group" style="margin-bottom: 1rem;">
              <label for="phone">Phone Number *</label>
              <input 
                id="phone"
                v-model="signupForm.phone" 
                type="tel" 
                class="form-control" 
                required
                placeholder="Enter 10-digit phone number"
                pattern="[0-9]{10}"
              >
            </div>

            <div class="form-group" style="margin-bottom: 1rem;">
              <label for="dateOfBirth">Date of Birth *</label>
              <input 
                id="dateOfBirth"
                v-model="signupForm.dateOfBirth" 
                type="date" 
                class="form-control" 
                required
                :max="maxDate"
              >
            </div>
          </div>

          <!-- Account Information -->
          <div style="margin-bottom: 2rem;">
            <h3 style="color: #374151; margin-bottom: 1rem; font-size: 1.1rem;">🔐 Account Security</h3>
            
            <div class="form-group" style="margin-bottom: 1rem;">
              <label for="role">Account Type *</label>
              <select 
                id="role"
                v-model="signupForm.role" 
                class="form-control" 
                required
              >
                <option value="">Select your role</option>
                <option value="traffic_sergeant">👮 Traffic Sergeant</option>
                <option value="traffic_inspector">🎖️ Traffic Inspector</option>
                <option value="traffic_central_team">🏛️ Central Team Officer</option>
                <option value="medical_emergency_central">🚑 Medical Emergency Team</option>
              </select>
            </div>


            <div class="form-group" style="margin-bottom: 1rem;">
              <label for="password">Password *</label>
              <div style="position: relative;">
                <input 
                  id="password"
                  v-model="signupForm.password" 
                  :type="showPassword ? 'text' : 'password'" 
                  class="form-control" 
                  required
                  placeholder="Create a strong password"
                  style="padding-right: 3rem;"
                >
                <button 
                  type="button" 
                  @click="showPassword = !showPassword"
                  style="position: absolute; right: 0.75rem; top: 50%; transform: translateY(-50%); background: none; border: none; color: #6b7280; cursor: pointer;"
                >
                  {{ showPassword ? '👁️' : '🙈' }}
                </button>
              </div>
              <div style="margin-top: 0.5rem;">
                <div style="font-size: 0.8rem; color: #6b7280;">Password strength:</div>
                <div style="display: flex; gap: 0.25rem; margin-top: 0.25rem;">
                  <div 
                    v-for="(strength, index) in passwordStrength" 
                    :key="index"
                    :style="{ 
                      height: '4px', 
                      flex: 1, 
                      borderRadius: '2px',
                      background: strength ? getStrengthColor(index) : '#e5e7eb'
                    }"
                  ></div>
                </div>
                <div style="font-size: 0.7rem; color: #6b7280; margin-top: 0.25rem;">
                  {{ passwordStrengthText }}
                </div>
              </div>
            </div>

            <div class="form-group" style="margin-bottom: 1rem;">
              <label for="confirmPassword">Confirm Password *</label>
              <input 
                id="confirmPassword"
                v-model="signupForm.confirmPassword" 
                type="password" 
                class="form-control" 
                required
                placeholder="Re-enter your password"
              >
              <div v-if="passwordMismatch" style="color: #dc2626; font-size: 0.8rem; margin-top: 0.25rem;">
                Passwords do not match
              </div>
            </div>
          </div>

          <!-- Terms and Conditions -->
          <div style="margin-bottom: 2rem;">
            <label style="display: flex; align-items: start; gap: 0.75rem; cursor: pointer;">
              <input 
                type="checkbox" 
                v-model="signupForm.acceptTerms" 
                required
                style="margin-top: 0.125rem;"
              >
              <span style="font-size: 0.9rem; color: #374151; line-height: 1.4;">
                I agree to the 
                <a href="#" style="color: #3b82f6; text-decoration: underline;">Terms of Service</a> 
                and 
                <a href="#" style="color: #3b82f6; text-decoration: underline;">Privacy Policy</a>
              </span>
            </label>
          </div>

          <div style="margin-bottom: 1rem;">
            <label style="display: flex; align-items: start; gap: 0.75rem; cursor: pointer;">
              <input 
                type="checkbox" 
                v-model="signupForm.acceptNotifications"
                style="margin-top: 0.125rem;"
              >
              <span style="font-size: 0.9rem; color: #374151; line-height: 1.4;">
                I want to receive SMS and email notifications about violations, payments, and system updates
              </span>
            </label>
          </div>

          <!-- Submit Button -->
          <button 
            type="submit"
            class="btn btn-success btn-block"
            :class="{ 'btn-disabled': !signupForm.acceptTerms || loading }"
            :disabled="!signupForm.acceptTerms || loading"
            @click="handleSignup"
            style="width: 100%; padding: 1rem; margin-bottom: 1rem; font-size: 1.1rem; font-weight: 600;"
          >
            {{ loading ? '⏳ Creating Account...' : '📝 Create Account' }}
          </button>



          <!-- Login Link -->
          <div style="text-align: center; font-size: 0.9rem; color: #6b7280;">
            Already have an account?
            <button 
              type="button" 
              @click="navigateToLogin"
              style="background: none; border: none; color: #3b82f6; text-decoration: underline; cursor: pointer; font-size: 0.9rem;"
            >
              Sign In
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

export default {
  name: 'SignupView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const loading = ref(false)
    const showPassword = ref(false)
    const emailError = ref('')
    const maxDate = ref('')
    
    const signupForm = ref({
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      dateOfBirth: '',
      role: '',
      password: '',
      confirmPassword: '',
      acceptTerms: false,
      acceptNotifications: false
    })
    
    // ✅ Ensure the button starts disabled
    // (Optional but helps verify reactivity)
    console.log('Initial acceptTerms state:', signupForm.value.acceptTerms)

    // Password validation
    const passwordStrength = computed(() => {
      const password = signupForm.value.password
      const checks = [
        password.length >= 8,
        /[a-z]/.test(password),
        /[A-Z]/.test(password),
        /[0-9]/.test(password),
        /[^A-Za-z0-9]/.test(password)
      ]
      return checks
    })
    
    const passwordStrengthText = computed(() => {
      const count = passwordStrength.value.filter(Boolean).length
      if (count === 0) return 'Enter password'
      if (count <= 2) return 'Weak'
      if (count <= 3) return 'Fair'
      if (count <= 4) return 'Good'
      return 'Strong'
    })
    
    const passwordMismatch = computed(() => {
      return signupForm.value.confirmPassword && 
             signupForm.value.password !== signupForm.value.confirmPassword
    })
    
    const canSubmit = computed(() => {
      const form = signupForm.value
      const result = form.firstName && 
             form.lastName && 
             form.email && 
             form.phone && 
             form.dateOfBirth && 
             form.role && 
             form.password && 
             form.confirmPassword && 
             !passwordMismatch.value &&
             passwordStrength.value.filter(Boolean).length >= 1 && // Reduced from 3 to 1
             form.acceptTerms  // ✅ Must accept T&C before enabling

      console.log('canSubmit check:', {
        firstName: !!form.firstName,
        lastName: !!form.lastName,
        email: !!form.email,
        phone: !!form.phone,
        dateOfBirth: !!form.dateOfBirth,
        role: !!form.role,
        password: !!form.password,
        confirmPassword: !!form.confirmPassword,
        passwordMismatch: passwordMismatch.value,
        passwordStrength: passwordStrength.value.filter(Boolean).length,
        acceptTerms: form.acceptTerms,
        result
      })
      
      return result
    })

    // ✅ New computed property just for T&C checkbox
    const termsAccepted = computed(() => signupForm.value.acceptTerms)

    const getStrengthColor = (index) => {
      const colors = ['#ef4444', '#f59e0b', '#f59e0b', '#10b981', '#10b981']
      return colors[index] || '#e5e7eb'
    }
    
    const validateEmail = () => {
      const email = signupForm.value.email
      if (!email) {
        emailError.value = ''
        return
      }
      
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!emailRegex.test(email)) {
        emailError.value = 'Please enter a valid email address'
      } else {
        emailError.value = ''
        // In real app, check if email already exists
      }
    }
    
    const handleSignup = async () => {
      console.log('🚀 handleSignup function called!')
      try {
        loading.value = true
        
        console.log('Form data:', signupForm.value)
        console.log('Can submit:', canSubmit.value)
        
        // Validate form
        validateEmail()
        if (emailError.value) {
          console.log('Email validation failed:', emailError.value)
          return
        }
        
        if (passwordMismatch.value) {
          alert('Passwords do not match!')
          return
        }
        
        if (passwordStrength.value.filter(Boolean).length < 3) {
          alert('Password is too weak. Please create a stronger password.')
          return
        }

        // ✅ Block signup if T&C not accepted (extra safety)
        if (!signupForm.value.acceptTerms) {
          alert('Please accept the Terms and Conditions before continuing.')
          return
        }
        
        // Create username from email
        const username = signupForm.value.email.split('@')[0]
        
        // Prepare user data
        const userData = {
          username: username,
          password: signupForm.value.password,
          name: `${signupForm.value.firstName} ${signupForm.value.lastName}`,
          email: signupForm.value.email,
          firstName: signupForm.value.firstName,
          lastName: signupForm.value.lastName,
          phone: signupForm.value.phone,
          dateOfBirth: signupForm.value.dateOfBirth,
          role: signupForm.value.role,
          confirmPassword: signupForm.value.confirmPassword
        }
        
        // Call auth store signup method
        const result = await authStore.signup(userData)
        
        if (result.success) {
          alert(`✅ ${result.message}\n\nUsername: ${username}\n\nYou can now log in with your credentials after admins approval.`)
          router.push('/login')
        } else {
          alert(`❌ Signup Failed\n\n${result.message}`)
        }
        
      } catch (error) {
        console.error('Signup error:', error)
        alert('Failed to create account. Please try again.')
      } finally {
        loading.value = false
      }
    }
    
    const navigateToLogin = () => {
      router.push('/login')
    }
    
    onMounted(() => {
      console.log('🚀 SignupView mounted!')
      console.log('Initial form data:', signupForm.value)
      
      // Set max date to 18 years ago
      const today = new Date()
      const eighteenYearsAgo = new Date(today.getFullYear() - 18, today.getMonth(), today.getDate())
      maxDate.value = eighteenYearsAgo.toISOString().split('T')[0]
      
      // Add email validation listener
      signupForm.value.email && validateEmail()
    })
    
    return {
      signupForm,
      loading,
      showPassword,
      emailError,
      maxDate,
      passwordStrength,
      passwordStrengthText,
      passwordMismatch,
      canSubmit,
      termsAccepted, // ✅ Export new property
      getStrengthColor,
      validateEmail,
      handleSignup,
      navigateToLogin
    }
  }
}
</script>

<style scoped>
/* Form styling is inherited from global styles */
</style>
