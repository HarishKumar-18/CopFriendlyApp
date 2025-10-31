<template>
  <div class="min-h-screen bg-gray-50 py-6">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="bg-white shadow rounded-lg mb-6">
        <div class="px-6 py-4 border-b border-gray-200">
          <h1 class="text-2xl font-bold text-gray-900">Register Traffic Violation</h1>
          <p class="mt-1 text-sm text-gray-600">Record a new traffic violation with complete details</p>
        </div>
      </div>

      <!-- Violation Form -->
      <form @submit.prevent="submitViolation" class="space-y-6">
        <!-- Violator Information -->
        <div class="bg-white shadow rounded-lg p-6">
          <h2 class="text-lg font-medium text-gray-900 mb-4">Violator Information</h2>
          
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- Violator Name -->
            <div>
              <label for="violatorName" class="block text-sm font-medium text-gray-700">
                Violator's Name *
              </label>
              <input
                id="violatorName"
                v-model="form.violatorName"
                type="text"
                required
                class="mt-1 material-input"
                placeholder="Enter full name"
              />
            </div>

            <!-- Vehicle Registration -->
            <div>
              <label for="registrationNumber" class="block text-sm font-medium text-gray-700">
                Vehicle Registration *
              </label>
              <input
                id="registrationNumber"
                v-model="form.registrationNumber"
                type="text"
                required
                class="mt-1 material-input"
                placeholder="e.g., DL01AB1234"
                style="text-transform: uppercase"
              />
            </div>

            <!-- Violation Type -->
            <div>
              <label for="violationType" class="block text-sm font-medium text-gray-700">
                Violation Type *
              </label>
              <select
                id="violationType"
                v-model="form.violationType"
                required
                class="mt-1 material-select"
              >
                <option value="">Select violation type</option>
                <option value="speeding">Over Speeding</option>
                <option value="parking">Wrong Parking</option>
                <option value="signal">Signal Violation</option>
                <option value="license">No License</option>
                <option value="helmet">No Helmet</option>
                <option value="seatbelt">No Seatbelt</option>
                <option value="drunk_driving">Drunk Driving</option>
                <option value="other">Other</option>
              </select>
            </div>

            <!-- Location -->
            <div>
              <label for="location" class="block text-sm font-medium text-gray-700">
                Location *
              </label>
              <input
                id="location"
                v-model="form.location"
                type="text"
                required
                class="mt-1 material-input"
                placeholder="Enter violation location"
              />
            </div>
          </div>
        </div>

        <!-- Form Actions -->
        <div class="bg-white shadow rounded-lg p-6">
          <div class="flex items-center justify-end space-x-4">
            <button
              type="button"
              @click="resetForm"
              class="material-button bg-gray-600 text-white hover:bg-gray-700"
            >
              Reset Form
            </button>
            
            <button
              type="submit"
              class="material-button material-button-primary"
            >
              Register Violation
            </button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'CreateViolationView',
  setup() {
    const router = useRouter()

    const form = ref({
      violatorName: '',
      registrationNumber: '',
      violationType: '',
      location: ''
    })

    const submitViolation = () => {
      if (window.$toast) {
        window.$toast.success('Violation registered successfully!')
      }
      router.push('/violations')
    }

    const resetForm = () => {
      form.value = {
        violatorName: '',
        registrationNumber: '',
        violationType: '',
        location: ''
      }
    }

    return {
      form,
      submitViolation,
      resetForm
    }
  }
}
</script>