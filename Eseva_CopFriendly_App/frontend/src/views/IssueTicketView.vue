<template>
  <div>
    <!-- Navigation -->
    <nav style="background: #7c3aed; color: white; padding: 1rem; display: flex; justify-content: space-between; align-items: center;">
      <div style="font-size: 1.5rem; font-weight: bold;">
        📋 Issue E-Ticket
      </div>
      <div style="display: flex; gap: 1rem; align-items: center;">
        <button @click="navigateTo('/violations')" class="btn" style="background: rgba(255,255,255,0.2); padding: 0.5rem 1rem;">
          ← Back to Violations
        </button>
        <button @click="navigateTo('/dashboard')" class="btn" style="background: rgba(255,255,255,0.2); padding: 0.5rem 1rem;">
          Dashboard
        </button>
      </div>
    </nav>

    <div class="dashboard">
      <div style="display: flex; justify-content: between; align-items: center; margin-bottom: 2rem;">
        <h1 style="color: #333; margin: 0;">Generate E-Ticket</h1>
        <div style="display: flex; gap: 1rem;">
          <button @click="scanQR" class="btn">
            📱 Scan QR Code
          </button>
          <button @click="printTicket" class="btn btn-success" :disabled="!ticketGenerated">
            🖨️ Print Ticket
          </button>
        </div>
      </div>

      <div style="display: grid; grid-template-columns: 1fr 400px; gap: 2rem;">
        <!-- Ticket Form -->
        <div>
          <!-- Violation Summary -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>🚗 Violation Summary</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 1rem;">
                <div>
                  <strong>Vehicle Number:</strong><br>
                  <span style="font-size: 1.2rem; color: #2563eb;">{{ violation.vehicleNumber }}</span>
                </div>
                <div>
                  <strong>Violation Type:</strong><br>
                  <span style="color: #dc2626;">{{ violation.violationType }}</span>
                </div>
                <div>
                  <strong>Location:</strong><br>
                  {{ violation.location }}
                </div>
                <div>
                  <strong>Date & Time:</strong><br>
                  {{ formatDate(violation.timestamp) }}
                </div>
                <div>
                  <strong>Officer:</strong><br>
                  {{ violation.officerName }}
                </div>
                <div>
                  <strong>Fine Amount:</strong><br>
                  <span style="font-size: 1.2rem; font-weight: bold; color: #16a34a;">₹{{ violation.fineAmount }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Ticket Details -->
          <div class="card">
            <div class="card-header">
              <h3>📋 Ticket Information</h3>
            </div>
            <div class="card-body">
              <form @submit.prevent="generateTicket">
                <div style="display: grid; gap: 1rem;">
                  <div class="form-group">
                    <label for="ticketNumber">Ticket Number *</label>
                    <input 
                      id="ticketNumber"
                      v-model="ticketForm.ticketNumber" 
                      type="text" 
                      class="form-control" 
                      readonly
                      style="background: #f3f4f6;"
                    >
                  </div>
                  
                  <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 1rem;">
                    <div class="form-group">
                      <label for="dueDate">Payment Due Date *</label>
                      <input 
                        id="dueDate"
                        v-model="ticketForm.dueDate" 
                        type="date" 
                        class="form-control" 
                        required
                      >
                    </div>
                    
                    <div class="form-group">
                      <label for="courtDate">Court Date (if applicable)</label>
                      <input 
                        id="courtDate"
                        v-model="ticketForm.courtDate" 
                        type="date" 
                        class="form-control"
                      >
                    </div>
                  </div>
                  
                  <div class="form-group">
                    <label for="paymentOptions">Payment Options</label>
                    <div style="display: grid; gap: 0.5rem;">
                      <label style="display: flex; align-items: center; gap: 0.5rem;">
                        <input type="checkbox" v-model="ticketForm.allowOnlinePayment">
                        Online Payment (UPI, Cards, Net Banking)
                      </label>
                      <label style="display: flex; align-items: center; gap: 0.5rem;">
                        <input type="checkbox" v-model="ticketForm.allowCashPayment">
                        Cash Payment at Traffic Office
                      </label>
                      <label style="display: flex; align-items: center; gap: 0.5rem;">
                        <input type="checkbox" v-model="ticketForm.allowCourtAppearance">
                        Contest in Court
                      </label>
                    </div>
                  </div>
                  
                  <div class="form-group">
                    <label for="additionalNotes">Additional Notes</label>
                    <textarea 
                      id="additionalNotes"
                      v-model="ticketForm.additionalNotes" 
                      class="form-control" 
                      rows="3"
                      placeholder="Any additional instructions or notes for the violator..."
                    ></textarea>
                  </div>
                  
                  <div class="form-group">
                    <label style="display: flex; align-items: center; gap: 0.5rem;">
                      <input type="checkbox" v-model="ticketForm.sendSMS" checked>
                      Send SMS notification to driver
                    </label>
                  </div>
                  
                  <div class="form-group">
                    <label style="display: flex; align-items: center; gap: 0.5rem;">
                      <input type="checkbox" v-model="ticketForm.sendEmail">
                      Send email notification (if email available)
                    </label>
                  </div>
                </div>
                
                <div style="margin-top: 2rem; display: flex; gap: 1rem;">
                  <button type="submit" class="btn btn-success btn-block" :disabled="generating">
                    {{ generating ? 'Generating...' : '📋 Generate E-Ticket' }}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>

        <!-- Ticket Preview -->
        <div>
          <div class="card">
            <div class="card-header" style="background: #f8fafc;">
              <h3 style="margin: 0; text-align: center;">📄 E-Ticket Preview</h3>
            </div>
            <div class="card-body" style="padding: 0;">
              <div id="ticket-preview" style="padding: 1.5rem; font-family: 'Courier New', monospace; border: 2px solid #e5e7eb; background: white;">
                <!-- Ticket Header -->
                <div style="text-align: center; border-bottom: 2px solid #000; padding-bottom: 1rem; margin-bottom: 1rem;">
                  <h2 style="margin: 0; font-size: 1.2rem;">TRAFFIC POLICE E-TICKET</h2>
                  <div style="font-size: 0.9rem;">CopFriendly ESeva System</div>
                  <div style="font-size: 0.8rem; margin-top: 0.5rem;">{{ currentDate }}</div>
                </div>
                
                <!-- Ticket Number -->
                <div style="text-align: center; margin-bottom: 1rem;">
                  <div style="font-size: 1.1rem; font-weight: bold;">TICKET NO: {{ ticketForm.ticketNumber }}</div>
                  <div style="font-size: 0.8rem; margin-top: 0.5rem;">{{ generateQRCode() }}</div>
                </div>
                
                <!-- Violation Details -->
                <div style="margin-bottom: 1rem; font-size: 0.9rem;">
                  <div style="border-bottom: 1px solid #ccc; padding: 0.25rem 0;"><strong>Vehicle Number:</strong> {{ violation.vehicleNumber }}</div>
                  <div style="border-bottom: 1px solid #ccc; padding: 0.25rem 0;"><strong>Driver Name:</strong> {{ violation.driverName }}</div>
                  <div style="border-bottom: 1px solid #ccc; padding: 0.25rem 0;"><strong>Violation:</strong> {{ violation.violationType }}</div>
                  <div style="border-bottom: 1px solid #ccc; padding: 0.25rem 0;"><strong>Location:</strong> {{ violation.location }}</div>
                  <div style="border-bottom: 1px solid #ccc; padding: 0.25rem 0;"><strong>Date/Time:</strong> {{ formatDate(violation.timestamp) }}</div>
                  <div style="border-bottom: 1px solid #ccc; padding: 0.25rem 0;"><strong>Officer:</strong> {{ violation.officerName }}</div>
                </div>
                
                <!-- Fine Details -->
                <div style="background: #f9fafb; padding: 0.75rem; margin-bottom: 1rem; border: 1px solid #d1d5db;">
                  <div style="display: flex; justify-content: space-between; font-weight: bold;">
                    <span>FINE AMOUNT:</span>
                    <span>₹{{ violation.fineAmount }}</span>
                  </div>
                  <div style="display: flex; justify-content: space-between; font-size: 0.8rem; margin-top: 0.25rem;">
                    <span>Due Date:</span>
                    <span>{{ ticketForm.dueDate || 'Not set' }}</span>
                  </div>
                </div>
                
                <!-- Payment Instructions -->
                <div style="font-size: 0.8rem; margin-bottom: 1rem;">
                  <div style="font-weight: bold; margin-bottom: 0.5rem;">PAYMENT OPTIONS:</div>
                  <div v-if="ticketForm.allowOnlinePayment">• Online: UPI, Cards, Net Banking</div>
                  <div v-if="ticketForm.allowCashPayment">• Cash: Traffic Police Office</div>
                  <div v-if="ticketForm.allowCourtAppearance">• Contest: Appear in Court</div>
                </div>
                
                <!-- Footer -->
                <div style="border-top: 2px solid #000; padding-top: 0.5rem; font-size: 0.7rem; text-align: center;">
                  <div>For queries: Call 1800-XXX-XXXX</div>
                  <div>Visit: www.copfriendly-eseva.gov.in</div>
                  <div style="margin-top: 0.5rem; font-style: italic;">
                    "Safety First - Follow Traffic Rules"
                  </div>
                </div>
              </div>
              
              <!-- Print Button -->
              <div style="padding: 1rem; text-align: center; background: #f8fafc;">
                <button @click="printTicket" class="btn btn-success" :disabled="!ticketGenerated">
                  🖨️ Print This Ticket
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { showToast } from '@/composables/interactionService'

export default {
  name: 'IssueTicketView',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const authStore = useAuthStore()
    
    const generating = ref(false)
    const ticketGenerated = ref(false)
    const currentDate = ref('')
    
    // Violation data (load from backend by id)
    const violation = ref({
      id: route.params.id || '',
      vehicleNumber: '',
      driverName: '',
      violationType: '',
      location: '',
      fineAmount: 0,
      timestamp: new Date(),
      officerName: ''
    })
    
    const ticketForm = ref({
      ticketNumber: '',
      dueDate: '',
      courtDate: '',
      allowOnlinePayment: true,
      allowCashPayment: true,
      allowCourtAppearance: false,
      additionalNotes: '',
      sendSMS: true,
      sendEmail: false
    })
    
    const navigateTo = (path) => {
      router.push(path)
    }
    
    const generateTicketNumber = () => {
      const prefix = 'TK'
      const year = new Date().getFullYear()
      const random = Math.floor(Math.random() * 100000).toString().padStart(5, '0')
      return `${prefix}${year}${random}`
    }
    
    const generateTicket = async () => {
      try {
        generating.value = true
        
        // Simulate ticket generation
        await new Promise(resolve => setTimeout(resolve, 2000))
        
        ticketGenerated.value = true
  showToast({ message: 'E-Ticket generated successfully!', type: 'success' })
        
        // In real app, would save to database and send notifications
        if (ticketForm.value.sendSMS) {
          showToast({ message: 'SMS notification sent to driver!', type: 'info' })
        }
        
      } catch (error) {
        console.error('Error generating ticket:', error)
  showToast({ message: 'Failed to generate ticket. Please try again.', type: 'error' })
      } finally {
        generating.value = false
      }
    }
    
    const printTicket = () => {
      const printContent = document.getElementById('ticket-preview')
      const originalContent = document.body.innerHTML
      
      document.body.innerHTML = printContent.outerHTML
      window.print()
      document.body.innerHTML = originalContent
      
      // Reload the page to restore functionality
      window.location.reload()
    }
    
    const scanQR = () => {
  showToast({ message: 'QR Code scanner will be implemented with camera API', type: 'info' })
    }
    
    const formatDate = (date) => {
      try { return new Date(date).toLocaleString() } catch(e) { return String(date) }
    }
    
    const generateQRCode = () => {
      return `[QR: ${ticketForm.value.ticketNumber}]`
    }
    
    onMounted(async () => {
      // Set current date
      currentDate.value = new Date().toLocaleDateString()
      
      // Generate ticket number
      ticketForm.value.ticketNumber = generateTicketNumber()
      
      // Set default due date (30 days from now)
      const dueDate = new Date()
      dueDate.setDate(dueDate.getDate() + 30)
      ticketForm.value.dueDate = dueDate.toISOString().split('T')[0]

      // Load violation by id
      try {
        const { violationsAPI } = await import('@/services/api')
        if (route.params.id) {
          const resp = await violationsAPI.getById(route.params.id)
          const v = resp?.data?.violation
          if (v) {
            violation.value.id = v.id || v._id || route.params.id
            violation.value.vehicleNumber = v.vehicleNumber || ''
            violation.value.driverName = v.driverName || '' // driver name as entered by sergeant
            violation.value.violationType = v.violationType || ''
            violation.value.location = v.location || ''
            violation.value.fineAmount = v.fineAmount || v.amount || 0
            violation.value.timestamp = v.violationDateTime || v.createdAt || new Date().toISOString()
            // Officer must be the sergeant name (issuerName) or current user name as fallback
            violation.value.officerName = v.issuerName || authStore.user?.name || authStore.user?.username || 'Officer'

            // If dueDate exists from backend use it for preview defaults
            if (v.dueDate) {
              try { ticketForm.value.dueDate = new Date(v.dueDate).toISOString().split('T')[0] } catch(e) {}
            }
          }
        }
      } catch (e) {
        console.warn('Failed to load violation for ticket view:', e?.message || e)
        // Fallback: ensure officer shows current sergeant/inspector
        violation.value.officerName = authStore.user?.name || authStore.user?.username || 'Officer'
      }
    })
    
    return {
      violation,
      ticketForm,
      generating,
      ticketGenerated,
      currentDate,
      navigateTo,
      generateTicket,
      printTicket,
      scanQR,
      formatDate,
      generateQRCode
    }
  }
}
</script>

<style scoped>
@media print {
  body * {
    visibility: hidden;
  }
  
  #ticket-preview, #ticket-preview * {
    visibility: visible;
  }
  
  #ticket-preview {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
  }
}
</style>