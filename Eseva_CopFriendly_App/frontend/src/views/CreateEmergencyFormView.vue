<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Enhanced Horizontal Navigation Bar -->
    <nav style="
      background: #dc2626 !important;
      padding: 1rem 2rem !important;
      display: flex !important;
      justify-content: space-between !important;
      align-items: center !important;
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1) !important;
      position: sticky !important;
      top: 0 !important;
      z-index: 1000 !important;
      color: white !important;
    ">
      <!-- Left Side: Title and Role Info -->
      <div style="display: flex; flex-direction: column; gap: 0.25rem;">
        <h1 style="margin: 0; font-size: 1.5rem; font-weight: bold; display: flex; align-items: center; gap: 0.5rem;">
          🚑 Register Emergency
        </h1>
        <p style="margin: 0; font-size: 0.9rem; opacity: 0.9;">
          {{ authStore.user?.role || 'Officer' }} | Station: {{ authStore.user?.station || 'Central' }}
        </p>
      </div>

      <!-- Right Side: User Info and Actions -->
      <div style="display: flex; align-items: center; gap: 1rem;">
        <span style="font-weight: 500;">{{ authStore.user?.name || 'Officer' }}</span>
        
        <button 
          @click="navigateTo('/emergencies')" 
          style="
            background: rgba(255, 255, 255, 0.2) !important;
            color: white !important;
            border: 1px solid rgba(255, 255, 255, 0.3) !important;
            padding: 0.5rem 1rem !important;
            border-radius: 0.375rem !important;
            font-size: 0.875rem !important;
            cursor: pointer !important;
            transition: all 0.2s !important;
          "
          onmouseover="this.style.background='rgba(255, 255, 255, 0.3) !important'"
          onmouseout="this.style.background='rgba(255, 255, 255, 0.2) !important'"
        >
          ← Back
        </button>
        
        <button 
          @click="navigateTo('/dashboard')" 
          style="
            background: rgba(255, 255, 255, 0.2) !important;
            color: white !important;
            border: 1px solid rgba(255, 255, 255, 0.3) !important;
            padding: 0.5rem 1rem !important;
            border-radius: 0.375rem !important;
            font-size: 0.875rem !important;
            cursor: pointer !important;
            transition: all 0.2s !important;
          "
          onmouseover="this.style.background='rgba(255, 255, 255, 0.3) !important'"
          onmouseout="this.style.background='rgba(255, 255, 255, 0.2) !important'"
        >
          📊 Dashboard
        </button>
        
        <button 
          @click="logout" 
          style="
            background: white !important;
            color: #dc2626 !important;
            border: 1px solid white !important;
            padding: 0.5rem 1rem !important;
            border-radius: 0.375rem !important;
            font-size: 0.875rem !important;
            font-weight: 500 !important;
            cursor: pointer !important;
            transition: all 0.2s !important;
          "
          onmouseover="this.style.background='#f8fafc !important'; this.style.color='#dc2626 !important'"
          onmouseout="this.style.background='white !important'; this.style.color='#dc2626 !important'"
        >
          Logout
        </button>
      </div>
    </nav>

    <div class="container mx-auto px-4 py-8">
      <div class="max-w-4xl mx-auto">
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h2 class="text-2xl font-bold text-gray-900 mb-6">🚨 Emergency Registration Form</h2>
          
          <form @submit.prevent="submitEmergency" class="space-y-6">
            <!-- Emergency Type -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Emergency Type</label>
              <select v-model="emergency.type" required class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500">
                <option value="">Select Emergency Type</option>
                <option value="traffic_accident">Traffic Accident</option>
                <option value="medical_emergency">Medical Emergency</option>
                <option value="fire_incident">Fire Incident</option>
                <option value="natural_disaster">Natural Disaster</option>
                <option value="criminal_activity">Criminal Activity</option>
                <option value="other">Other</option>
              </select>
            </div>

            <!-- Location Information -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Location/Address</label>
                <textarea v-model="emergency.location" required rows="3" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500" placeholder="Exact location of the emergency"></textarea>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Landmark</label>
                <input v-model="emergency.landmark" type="text" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500" placeholder="Nearby landmark">
              </div>
            </div>

            <!-- Emergency Details -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Emergency Description</label>
              <textarea v-model="emergency.description" required rows="4" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500" placeholder="Detailed description of the emergency situation"></textarea>
            </div>

            <!-- Severity and Priority -->
            <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Severity Level</label>
                <select v-model="emergency.severity" required class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500">
                  <option value="">Select Severity</option>
                  <option value="low">Low</option>
                  <option value="medium">Medium</option>
                  <option value="high">High</option>
                  <option value="critical">Critical</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Casualties</label>
                <input v-model="emergency.casualties" type="number" min="0" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500" placeholder="Number of casualties">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Vehicles Involved</label>
                <input v-model="emergency.vehiclesInvolved" type="number" min="0" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500" placeholder="Number of vehicles">
              </div>
            </div>

            <!-- Services Required -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Emergency Services Required</label>
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                <label class="flex items-center">
                  <input v-model="emergency.servicesRequired" value="police" type="checkbox" class="mr-2">
                  <span class="text-sm">🚔 Police</span>
                </label>
                <label class="flex items-center">
                  <input v-model="emergency.servicesRequired" value="ambulance" type="checkbox" class="mr-2">
                  <span class="text-sm">🚑 Ambulance</span>
                </label>
                <label class="flex items-center">
                  <input v-model="emergency.servicesRequired" value="fire" type="checkbox" class="mr-2">
                  <span class="text-sm">🚒 Fire Department</span>
                </label>
                <label class="flex items-center">
                  <input v-model="emergency.servicesRequired" value="traffic" type="checkbox" class="mr-2">
                  <span class="text-sm">🚦 Traffic Control</span>
                </label>
              </div>
            </div>

            <!-- Contact Information -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Reporter Name</label>
                <input v-model="emergency.reporterName" type="text" required class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500" placeholder="Name of person reporting">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Contact Number</label>
                <input v-model="emergency.contactNumber" type="tel" required class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500" placeholder="Contact number">
              </div>
            </div>

            <!-- Additional Notes -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Additional Notes</label>
              <textarea v-model="emergency.notes" rows="3" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500" placeholder="Any additional information"></textarea>
            </div>

            <!-- Submit Button -->
            <div class="flex justify-end space-x-4">
              <button 
                type="button" 
                @click="navigateTo('/emergencies')" 
                class="px-6 py-3 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-gray-500"
              >
                Cancel
              </button>
              <button 
                type="submit" 
                :disabled="isSubmitting"
                class="px-6 py-3 bg-red-600 text-white rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 disabled:opacity-50"
              >
                {{ isSubmitting ? 'Registering...' : '🚨 Register Emergency' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { emergenciesAPI } from '@/services/api'

export default {
  name: 'CreateEmergencyFormView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const isSubmitting = ref(false)
    
    const emergency = ref({
      type: '',
      location: '',
      landmark: '',
      description: '',
      severity: '',
      casualties: 0,
      vehiclesInvolved: 0,
      servicesRequired: [],
      reporterName: '',
      contactNumber: '',
      notes: ''
    })
    
    const navigateTo = (path) => {
      router.push(path)
    }
    
    const logout = () => {
      authStore.logout()
      router.push('/login')
    }
    
    const submitEmergency = async () => {
      isSubmitting.value = true

      try {
        // Map form fields to backend DTO (EmergencyRequest)
        const payload = {
          type: emergency.value.type,
          location: emergency.value.location,
          landmark: emergency.value.landmark || null,
          // Optional GPS (left null unless you add capture)
          latitude: emergency.value.latitude ?? null,
          longitude: emergency.value.longitude ?? null,
          description: emergency.value.description,
          severityLevel: emergency.value.severity, // backend expects severityLevel
          casualties: Number(emergency.value.casualties) || 0,
          vehiclesInvolved: Number(emergency.value.vehiclesInvolved) || 0,
          requiresPolice: emergency.value.servicesRequired?.includes('police') || false,
          requiresAmbulance: emergency.value.servicesRequired?.includes('ambulance') || false,
          requiresFireService: emergency.value.servicesRequired?.includes('fire') || false,
          requiresTrafficControl: emergency.value.servicesRequired?.includes('traffic') || false,
          reporterName: emergency.value.reporterName,
          contactNumber: emergency.value.contactNumber,
          additionalNotes: emergency.value.notes || null,
          evidence: null
        }

        const resp = await emergenciesAPI.create(payload)

        // Success UX
        const saved = resp?.data?.emergency || {}
        alert(
          `🚨 Emergency registered successfully!\n\n` +
          `Type: ${saved.type || payload.type}\n` +
          `Location: ${saved.location || payload.location}\n` +
          `Severity: ${saved.severityLevel || payload.severityLevel}\n\n` +
          `Emergency services have been notified.`
        )
        
        // Navigate to emergencies list
        router.push('/emergencies')
      } catch (error) {
        console.error('Error registering emergency:', error)
        const message = error?.response?.data?.error || error?.message || 'Error registering emergency. Please try again.'
        alert(message)
      } finally {
        isSubmitting.value = false
      }
    }
    
    return {
      router,
      authStore,
      emergency,
      isSubmitting,
      navigateTo,
      logout,
      submitEmergency
    }
  }
}
</script>

<style scoped>
/* Additional custom styles can be added here */
</style>