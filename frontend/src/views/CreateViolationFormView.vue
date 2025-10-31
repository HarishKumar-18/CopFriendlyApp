<template>
  <div>
    <!-- Navigation -->
    <nav style="background: #16a34a; color: white; padding: 1rem; display: flex; justify-content: space-between; align-items: center;">
      <div style="font-size: 1.5rem; font-weight: bold;">
        ➕ Register New Violation
      </div>
      <div style="display: flex; gap: 1rem; align-items: center;">
        <button @click="navigateTo('/violations')" class="btn" style="background: rgba(255,255,255,0.2); padding: 0.5rem 1rem;">
          ← Back to List
        </button>
        <button @click="navigateTo('/dashboard')" class="btn" style="background: rgba(255,255,255,0.2); padding: 0.5rem 1rem;">
          Dashboard
        </button>
      </div>
    </nav>

    <div class="dashboard">
      <h1 style="color: #333; margin-bottom: 2rem;">Register Traffic Violation</h1>

      <form @submit.prevent="submitViolation">
        <div style="display: grid; gap: 2rem;">
          <!-- Vehicle Information -->
          <div class="card">
            <div class="card-header">
              <h3>🚗 Vehicle Information</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1rem;">
                <div class="form-group">
                  <label for="vehicleNumber">Vehicle Number *</label>
                  <input 
                    id="vehicleNumber"
                    v-model="form.vehicleNumber" 
                    type="text" 
                    class="form-control" 
                    placeholder="e.g., TN12AB1234"
                    required
                    style="text-transform: uppercase;"
                  >
                  <button type="button" class="btn btn-primary mt-3" @click="validateVehicleNumber">
                    Submit
                  </button>
                </div>
                
                <div class="form-group">
                  <label for="vehicleType">Vehicle Type *</label>
                  <select id="vehicleType" v-model="form.vehicleType" class="form-control" required>
                    <option value="">Select Vehicle Type</option>
                    <option value="car">Car</option>
                    <option value="motorcycle">Motorcycle</option>
                    <option value="truck">Truck</option>
                    <option value="bus">Bus</option>
                    <option value="auto">Auto Rickshaw</option>
                    <option value="bicycle">Bicycle</option>
                  </select>
                </div>
                
                <div class="form-group">
                  <label for="vehicleModel">Vehicle Model</label>
                  <input 
                    id="vehicleModel"
                    v-model="form.vehicleModel" 
                    type="text" 
                    class="form-control" 
                    placeholder="e.g., Maruti Swift"
                  >
                </div>
                
                <div class="form-group">
                  <label for="vehicleColor">Vehicle Color</label>
                  <input 
                    id="vehicleColor"
                    v-model="form.vehicleColor" 
                    type="text" 
                    class="form-control" 
                    placeholder="e.g., White"
                  >
                </div>
              </div>
            </div>
          </div>

          <!-- Driver Information -->
          <div class="card">
            <div class="card-header">
              <h3>👤 Driver Information</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1rem;">
                
                <!-- Driver Name -->
                <div class="form-group">
                  <label for="driverName">Driver Name *</label>
                  <input 
                    id="driverName"
                    v-model="form.driverName" 
                    type="text" 
                    class="form-control" 
                    placeholder="Full Name"
                    required
                  >
                </div>
                
                <!-- License Number -->
                <div class="form-group">
                  <label for="licenseNumber">License Number *</label>
                  <input 
                    id="licenseNumber"
                    v-model="form.licenseNumber" 
                    type="text" 
                    class="form-control" 
                    placeholder="e.g., TN14 2012000123456"
                    required
                    style="text-transform: uppercase;"
                  >
                </div>
                
                <!-- Phone Number -->
                <div class="form-group">
                  <label for="phoneNumber">Phone Number *</label>
                  <input 
                    id="phoneNumber"
                    v-model="form.phoneNumber" 
                    type="tel" 
                    class="form-control" 
                    placeholder="10-digit Mobile Number"
                    required
                    maxlength="10"
                  >
                </div>
                
                <!-- Address -->
                <div class="form-group">
                  <label for="address">Address</label>
                  <input 
                    id="address"
                    v-model="form.address" 
                    type="text" 
                    class="form-control" 
                    placeholder="Driver Address"
                    maxlength="100"
                  >
                </div>
              </div>

              <button type="button" class="btn btn-primary mt-3" @click="validateDriverInfo">
                Submit
              </button>
            </div>
          </div>

          <!-- Violation Details -->
          <div class="card">
            <div class="card-header">
              <h3>⚠️ Violation Details</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1rem;">
                <div class="form-group">
                  <label for="violationType">Violation Type *</label>
                  <select id="violationType" v-model="form.violationType" class="form-control" required>
                    <option value="">Select Violation</option>
                    <option value="speeding">Speeding</option>
                    <option value="signal_violation">Signal Violation</option>
                    <option value="illegal_parking">Illegal Parking</option>
                    <option value="no_helmet">No Helmet</option>
                    <option value="no_seatbelt">No Seatbelt</option>
                    <option value="mobile_driving">Mobile While Driving</option>
                    <option value="drunk_driving">Drunk Driving</option>
                    <option value="document_violation">Document Violation</option>
                    <option value="other">Other</option>
                  </select>
                </div>
                
                <div class="form-group">
                  <label for="fineAmount">Fine Amount (₹) *</label>
                  <input 
                    id="fineAmount"
                    v-model="form.fineAmount" 
                    type="number" 
                    class="form-control" 
                    placeholder="Enter fine amount"
                    required
                    min="0"
                  >
                </div>
                
                <div class="form-group">
                  <label for="location">Location *</label>
                  <input 
                    id="location"
                    v-model="form.location" 
                    type="text" 
                    class="form-control" 
                    placeholder="Violation Location"
                    required
                  >
                </div>
                
                <div class="form-group">
                  <label for="dateTime">Date & Time *</label>
                  <input 
                    id="dateTime"
                    v-model="form.dateTime" 
                    type="datetime-local" 
                    class="form-control" 
                    required
                    :max="maxDateTime"
                  >
                </div>
              </div>
              
              <div class="form-group" style="margin-top: 1rem;">
                <label for="description">Violation Description</label>
                <textarea 
                  id="description"
                  v-model="form.description" 
                  class="form-control" 
                  rows="3"
                  placeholder="Detailed description of the violation..."
                ></textarea>
              </div>
            </div>
          </div>

          <!-- Evidence -->
          <div class="card">
            <div class="card-header">
              <h3>📸 Evidence</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; gap: 1rem;">
                <div class="form-group">
                  <label>Photo Evidence</label>
                  <div style="border: 2px dashed #ccc; padding: 2rem; text-align: center; border-radius: 8px;">
                    <div style="color: #666;">
                      📸 Click to capture photo or upload image
                    </div>
                    <button type="button" @click="capturePhoto" class="btn" style="margin-top: 1rem;">
                      📱 Capture Photo
                    </button>
                    <input type="file" accept="image/*" style="margin-top: 1rem;" @change="uploadPhoto">
                  </div>
                </div>
                
                <div class="form-group">
                  <label>GPS Coordinates</label>
                  <div style="display: flex; gap: 1rem;">
                    <button type="button" @click="getCurrentLocation" class="btn">
                      📍 Get Current Location
                    </button>
                    <span style="color: #666; align-self: center;">{{ locationText }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Submit Buttons -->
          <div style="display: flex; gap: 1rem; justify-content: flex-end;">
            <button type="button" @click="saveDraft" class="btn" :disabled="submitting">
              💾 Save as Draft
            </button>
            <button type="submit" class="btn btn-success" :disabled="submitting">
              {{ submitting ? 'Submitting...' : '✅ Register Violation' }}
            </button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>
<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { showToast } from '@/composables/interactionService'

export default {
  name: 'CreateViolationView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const submitting = ref(false)
    const locationText = ref('Location not captured')
    const maxDateTime = ref('')
    const minDateTime = ref('')
    
    const form = ref({
      vehicleNumber: '',
      vehicleType: '',
      vehicleModel: '',
      vehicleColor: '',
      driverName: '',
      licenseNumber: '',
      phoneNumber: '',
      address: '',
      violationType: '',
      fineAmount: '',
      location: '',
      dateTime: '',
      description: '',
      latitude: null,
      longitude: null,
      photos: []
    })

    const navigateTo = (path) => router.push(path)

    // ✅ Validate only Vehicle Number
    const validateVehicleNumber = () => {
      const vehiclePattern = /^[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}$/
      const vehicleNumber = form.value.vehicleNumber?.toUpperCase().trim()
      if (!vehiclePattern.test(vehicleNumber)) {
        showToast({ title: 'Invalid Vehicle Number', message: 'Format: TN12AB1234', type: 'warning' })
      } else {
        showToast({ title: '✅ Valid Vehicle Number', message: vehicleNumber + ' is valid.', type: 'success' })
      }
    }

    // ✅ Validate only Driver Info
    const validateDriverInfo = () => {
      const namePattern = /^[A-Za-z\s]{3,30}$/
      // ✅ Updated License Pattern (handles formats like MH142012000123456, TN1420120001234, etc.)
      const licensePattern = /^[A-Z]{2}[0-9]{2}[0-9]{4}[0-9]{6,9}$/
      const phonePattern = /^[6-9][0-9]{9}$/

      if (!namePattern.test(form.value.driverName)) {
        showToast({ title: 'Invalid Name', message: 'Name must be 3–30 letters.', type: 'warning' })
        return
      }
      if (!licensePattern.test(form.value.licenseNumber.replace(/\s+/g, ''))) {
        showToast({ title: 'Invalid License', message: 'Format: MH142012000123456', type: 'warning' })
        return
      }
      if (!phonePattern.test(form.value.phoneNumber)) {
        showToast({ title: 'Invalid Phone', message: '10 digits starting with 6–9.', type: 'warning' })
        return
      }
      showToast({ title: '✅ Valid Driver Info', message: 'All driver details are valid.', type: 'success' })
    }

    // ✅ Main Validation (for final submit)
    const validateAllFields = () => {
      const vehiclePattern = /^[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}$/
      const namePattern = /^[A-Za-z\s]{3,30}$/
      const licensePattern = /^[A-Z]{2}[0-9]{2}[0-9]{4}[0-9]{6,9}$/
      const phonePattern = /^[6-9][0-9]{9}$/

      form.value.vehicleNumber = form.value.vehicleNumber?.toUpperCase().trim()
      form.value.licenseNumber = form.value.licenseNumber?.toUpperCase().trim()

      if (!vehiclePattern.test(form.value.vehicleNumber)) {
        showToast({ title: 'Validation', message: '❌ Invalid Vehicle Number! Format: MH12AB1234', type: 'warning' })
        return false
      }
      if (!namePattern.test(form.value.driverName)) {
        showToast({ title: 'Validation', message: '❌ Invalid Name! 3–30 letters.', type: 'warning' })
        return false
      }
      if (form.value.licenseNumber && !licensePattern.test(form.value.licenseNumber.replace(/\s+/g, ''))) {
        showToast({ title: 'Validation', message: '❌ Invalid License Format!', type: 'warning' })
        return false
      }
      if (form.value.phoneNumber && !phonePattern.test(form.value.phoneNumber)) {
        showToast({ title: 'Validation', message: '❌ Invalid Phone Number!', type: 'warning' })
        return false
      }
      return true
    }

    // ✅ Submit Violation
    const submitViolation = async () => {
      try {
        submitting.value = true
        if (!form.value.vehicleNumber || !form.value.driverName || !form.value.violationType || !form.value.fineAmount || !form.value.location || !form.value.dateTime) {
          showToast({ title: 'Validation', message: 'Please fill all required fields.', type: 'warning' })
          return
        }
        if (!validateAllFields()) return

        const payload = {
          vehicleNumber: form.value.vehicleNumber,
          vehicleType: form.value.vehicleType,
          vehicleModel: form.value.vehicleModel,
          vehicleColor: form.value.vehicleColor,
          driverName: form.value.driverName,
          licenseNumber: form.value.licenseNumber,
          driverPhoneNumber: form.value.phoneNumber,
          driverAddress: form.value.address,
          violationType: form.value.violationType,
          fineAmount: Number(form.value.fineAmount),
          location: form.value.location,
          violationDateTime: form.value.dateTime,
          description: form.value.description,
          latitude: form.value.latitude,
          longitude: form.value.longitude
        }

        const { violationsAPI } = await import('@/services/api')
        const resp = await violationsAPI.create(payload)
        if (resp?.data?.violation) {
          showToast({ title: 'Violation', message: 'Violation registered successfully!', type: 'success' })
          router.push('/violations')
        } else throw new Error('Unexpected server response')
      } catch (e) {
        console.error(e)
        showToast({ title: 'Error', message: 'Failed to register violation.', type: 'error' })
      } finally {
        submitting.value = false
      }
    }

    // ✅ Save Draft
    const saveDraft = async () => {
      try {
        const { violationsAPI } = await import('@/services/api')
        await violationsAPI.saveDraft(form.value)
        showToast({ title: 'Draft Saved', message: 'Saved successfully.', type: 'success' })
      } catch {
        localStorage.setItem('violationDraft', JSON.stringify(form.value))
        showToast({ title: 'Draft', message: 'Saved locally (offline).', type: 'info' })
      }
    }

    // ✅ Photo Upload Handlers
    const capturePhoto = () => showToast({ title: 'Photo', message: 'Camera integration coming soon.', type: 'info' })
    const uploadPhoto = e => e.target.files?.[0] && showToast({ title: 'Photo', message: `Uploaded ${e.target.files[0].name}`, type: 'success' })

    // ✅ Geolocation
    const getCurrentLocation = () => {
      if (!navigator.geolocation) return showToast({ title: 'Error', message: 'Geolocation not supported.', type: 'warning' })
      navigator.geolocation.getCurrentPosition(
        p => {
          form.value.latitude = p.coords.latitude
          form.value.longitude = p.coords.longitude
          locationText.value = `${p.coords.latitude.toFixed(6)}, ${p.coords.longitude.toFixed(6)}`
        },
        e => showToast({ title: 'Location', message: e.message, type: 'warning' })
      )
    }

    onMounted(() => {
      const now = new Date()

      const pad = n => String(n).padStart(2, '0')
      const localISO = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}`

      // ✅ Restrict date to today only
      const todayDate = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}`
      minDateTime.value = `${todayDate}T00:00`
      maxDateTime.value = `${todayDate}T23:59`

      // ✅ Set current time as default
      form.value.dateTime = localISO

      // ✅ Watch for future time selection
      const input = document.getElementById('dateTime')
      if (input) {
        input.addEventListener('change', e => {
          const selected = new Date(e.target.value)
          const nowLocal = new Date()
          if (selected > nowLocal) {
            showToast({
              title: 'Invalid Time',
              message: 'You cannot select a future time today.',
              type: 'warning'
            })
            e.target.value = localISO
            form.value.dateTime = localISO
          }
        })
      }

      // Load draft if any
      const draft = localStorage.getItem('violationDraft')
      if (draft) form.value = { ...form.value, ...JSON.parse(draft) }
    })



    return {
      form,
      submitting,
      locationText,
      maxDateTime,
      minDateTime,
      navigateTo,
      submitViolation,
      saveDraft,
      capturePhoto,
      uploadPhoto,
      getCurrentLocation,
      validateVehicleNumber,
      validateDriverInfo
    }
  }
}
</script>
