<template>
  <div class="dashboard">
    <!-- global toasts and confirms -->
    <Toast />
    <ConfirmModal />
    <!-- Medical Emergency Navigation Bar -->
    <nav style="
      background: #dc2626 !important; 
      color: white !important; 
      padding: 1rem !important; 
      display: flex !important; 
      justify-content: space-between !important; 
      align-items: center !important;
      position: sticky !important;
      top: 0 !important;
      z-index: 1000 !important;
      width: 100% !important;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1) !important;
    ">
      <div>
        <h1 style="font-size: 1.5rem; font-weight: bold; margin: 0; color: white !important;">Medical Emergency Dashboard</h1>
        <p style="margin: 0; color: white !important; opacity: 0.9;">Station: {{ station }}</p>
      </div>
      <div style="display: flex; gap: 1rem; align-items: center;">
        <span style="color: white !important; font-weight: 700; font-size: 1.25rem;">
  {{ authStore.user?.name || 'Emergency Officer' }}
</span>

        <button @click="logout" style="
          background: white !important; 
          color: #dc2626 !important; 
          padding: 0.5rem 1rem !important; 
          border: none !important; 
          border-radius: 0.25rem !important; 
          cursor: pointer !important;
          font-weight: 600 !important;
          transition: all 0.2s !important;
        "
        @mouseover="$event.target.style.background='#f3f4f6'"
        @mouseout="$event.target.style.background='white'">
          Logout
        </button>
      </div>
    </nav>

    <div style="padding: 2rem;">
      <!-- Emergency Overview Cards -->
      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1rem; margin-bottom: 2rem;">
        <div class="emergency-card" style="background: linear-gradient(135deg, #dc2626, #991b1b);">
          <div class="card-icon">🚨</div>
          <div class="card-content">
            <div class="card-number">{{ emergencyStats.activeCalls }}</div>
            <div class="card-label">Active Emergency Calls</div>
            <div class="card-detail">{{ emergencyStats.criticalCalls }} critical</div>
          </div>
        </div>
        
        <div class="emergency-card" style="background: linear-gradient(135deg, #f59e0b, #d97706);">
          <div class="card-icon">🚑</div>
          <div class="card-content">
            <div class="card-number">{{ emergencyStats.ambulancesDeployed }}</div>
            <div class="card-label">Ambulances Deployed</div>
            <div class="card-detail">{{ emergencyStats.totalAmbulances }} total available</div>
          </div>
        </div>
        
        <div class="emergency-card" style="background: linear-gradient(135deg, #3b82f6, #1e40af);">
          <div class="card-icon">⏱️</div>
          <div class="card-content">
            <div class="card-number">{{ emergencyStats.avgResponseTime }}</div>
            <div class="card-label">Avg Response Time</div>
            <div class="card-detail">{{ emergencyStats.targetTime }} target</div>
          </div>
        </div>
        
        <div class="emergency-card" style="background: linear-gradient(135deg, #16a34a, #15803d);">
          <div class="card-icon">💚</div>
          <div class="card-content">
            <div class="card-number">{{  }}%</div>
            <div class="card-label">Success Rate</div>
            <div class="card-detail">{{  }} lives saved today</div>
          </div>
        </div>
      </div>

      <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 2rem;">
        <!-- Main Content -->
        <div>
          <!-- Emergency Actions -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>🚨 Emergency Actions</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem;">
                <button @click="dispatchAmbulance" class="emergency-action-btn" style="background: linear-gradient(135deg, #f59e0b, #d97706);">
                  <div class="btn-icon">🚑</div>
                  <div class="btn-text">Dispatch Ambulance</div>
                </button>
                <button @click="coordinateWithHospital" class="emergency-action-btn" style="background: linear-gradient(135deg, #3b82f6, #1e40af);">
                  <div class="btn-icon">🏥</div>
                  <div class="btn-text">Coordinate Hospital</div>
                </button>
                <button @click="alertTrafficControl" class="emergency-action-btn" style="background: linear-gradient(135deg, #16a34a, #15803d);">
                  <div class="btn-icon">🚦</div>
                  <div class="btn-text">Alert Traffic Control</div>
                </button>
                <button @click="requestBackup" class="emergency-action-btn" style="background: linear-gradient(135deg, #7c3aed, #5b21b6);">
                  <div class="btn-icon">🆘</div>
                  <div class="btn-text">Request Backup</div>
                </button>
                <!-- Removed: New Emergency Call and Generate Report buttons (unused) -->
              </div>
            </div>
          </div>

          <!-- Active Emergency Calls -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>🚨 Active Emergency Calls</h3>
              <div style="display: flex; gap: 1rem;">
                <select v-model="callFilter" class="form-control" style="width: auto;">
                  <option value="all">All Calls</option>
                  <option value="critical">Critical</option>
                  <option value="high">High Priority</option>
                  <option value="medium">Medium Priority</option>
                </select>
                <button @click="refreshCalls" class="btn">🔄 Refresh</button>
              </div>
            </div>
            <div class="card-body">
              <div v-if="filteredCalls.length === 0" style="text-align: center; color: #6b7280; padding: 2rem;">
                No active emergency calls 🎯
              </div>
              <div v-else>
                <div 
                  v-for="call in filteredCalls" 
                  :key="call.id"
                  :style="`border: 2px solid ${getPriorityColor(call.priority)}; border-radius: 8px; padding: 1rem; margin-bottom: 1rem; background: ${getPriorityBg(call.priority)};`"
                >
                  <div style="display: flex; justify-content: space-between; align-items: start;">
                    <div style="flex: 1;">
                      <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 0.5rem;">
                        <div style="font-weight: bold; font-size: 1.1rem;">{{ call.type }}</div>
                        <span :style="`padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.8rem; font-weight: 500; background: ${getPriorityColor(call.priority)}; color: white;`">
                          {{ call.priority.toUpperCase() }}
                        </span>
                      </div>
                      <div style="font-size: 0.9rem; color: #6b7280; margin-bottom: 0.5rem;">
                        📍 {{ call.location }} | 📞 {{ call.callerPhone }}
                      </div>
                      <div style="font-size: 0.9rem; margin-bottom: 0.5rem;">{{ call.description }}</div>
                      <div style="font-size: 0.8rem; color: #9ca3af;">
                        Called: {{ formatDateTime(call.timestamp) }} | 
                        Duration: {{ getCallDuration(call.timestamp) }}
                      </div>
                    </div>
                    <div style="display: flex; flex-direction: column; gap: 0.5rem; min-width: 120px;">
                      <button @click="dispatchToCall(call)" class="btn btn-danger btn-small">
                        🚑 Dispatch
                      </button>
                      <button @click="updateCallStatus(call)" class="btn btn-primary btn-small">
                        📝 Update
                      </button>
                      <button @click="escalateCall(call)" class="btn btn-warning btn-small">
                        ⬆️ Escalate
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Ambulance Fleet Status -->
          <div class="card">
            <div class="card-header">
              <h3>🚑 Ambulance Fleet Status</h3>
              <button @click="refreshFleet" class="btn">🔄 Refresh Fleet</button>
            </div>
            <div class="card-body">
              <div style="overflow-x: auto;">
                <table style="width: 100%; border-collapse: collapse;">
                  <thead>
                    <tr style="background: #f8fafc; border-bottom: 2px solid #e5e7eb;">
                      <th style="padding: 0.75rem; text-align: left;">Unit ID</th>
                      <th style="padding: 0.75rem; text-align: center;">Status</th>
                      <th style="padding: 0.75rem; text-align: center;">Location</th>
                      <th style="padding: 0.75rem; text-align: center;">Crew</th>
                      <th style="padding: 0.75rem; text-align: center;">ETA</th>
                      <th style="padding: 0.75rem; text-align: center;">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="ambulance in ambulanceFleet" :key="ambulance.id" style="border-bottom: 1px solid #e5e7eb;">
                      <td style="padding: 0.75rem;">
                        <div style="font-weight: 500;">{{ ambulance.unitId }}</div>
                        <div style="font-size: 0.8rem; color: #6b7280;">{{ ambulance.type }}</div>
                      </td>
                      <td style="padding: 0.75rem; text-align: center;">
                        <span :class="getFleetStatusClass(ambulance.status)" style="padding: 0.25rem 0.5rem; border-radius: 4px; font-size: 0.8rem;">
                          {{ ambulance.status }}
                        </span>
                      </td>
                      <td style="padding: 0.75rem; text-align: center;">{{ ambulance.location }}</td>
                      <td style="padding: 0.75rem; text-align: center;">
                        <div style="font-size: 0.9rem;">{{ ambulance.crew.driver }}</div>
                        <div style="font-size: 0.8rem; color: #6b7280;">{{ ambulance.crew.paramedic }}</div>
                      </td>
                      <td style="padding: 0.75rem; text-align: center;">
                        {{ ambulance.eta || '-' }}
                      </td>
                      <td style="padding: 0.75rem; text-align: center;">
                        <div style="display: flex; gap: 0.5rem; justify-content: center;">
                          <button @click="trackAmbulance(ambulance)" class="btn-small">📍</button>
                          <button @click="contactCrew(ambulance)" class="btn-small">📞</button>
                          <button @click="reassignAmbulance(ambulance)" class="btn-small">🔄</button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>

        <!-- Sidebar -->
        <div>
          <!-- Real-time Map -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>🗺️ Live Emergency Map</h3>
              <button @click="refreshMap" class="btn-small">🔄</button>
            </div>
            <div class="card-body">
              <div class="map-container" style="height: 300px; background: linear-gradient(180deg,#fef2f2,#fff); border-radius: 8px; position: relative;">
                <!-- Simple map background (placeholder) -->
                <div style="position: absolute; inset: 0; display:flex; align-items:center; justify-content:center; color:#9ca3af; font-weight:500; pointer-events:none;">Emergency Response Map</div>

                <!-- Render call markers -->
                <div v-for="call in emergencyCalls" :key="call.id || call._id" :title="call.type + ' — ' + call.location"
                     class="map-marker call-marker"
                     :style="markerStyle(call.latitude, call.longitude, 'call')">
                  📍
                </div>

                <!-- Render ambulance markers -->
                <div v-for="amb in ambulanceFleet" :key="amb.id" :title="amb.unitId + ' — ' + amb.status"
                     class="map-marker amb-marker"
                     :style="markerStyle(amb.latitude, amb.longitude, 'ambulance')">
                  🚑
                </div>

                <!-- Map footer stats -->
                <div style="position: absolute; left: 8px; bottom: 8px; font-size: 12px; color: #6b7280; background: rgba(255,255,255,0.9); padding: 6px 8px; border-radius: 6px;">
                  {{ emergencyCalls.length }} active calls • {{ ambulanceFleet.filter(a => a.status === 'En Route').length }} en route
                </div>
              </div>
            </div>
          </div>

          

          <!-- Quick Contacts -->
          <div class="card">
            <div class="card-header">
              <h3>📞 Quick Contacts</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; gap: 0.5rem;">
                <button @click="callContact('police')" class="contact-btn">
                  🚔 Police Control Room
                </button>
                <button @click="callContact('fire')" class="contact-btn">
                  🚒 Fire Department
                </button>
                <button @click="callContact('hospital')" class="contact-btn">
                  🏥 Hospital Emergency
                </button>
                <button @click="callContact('traffic')" class="contact-btn">
                  🚦 Traffic Control
                </button>
                <button @click="callContact('disaster')" class="contact-btn">
                  🌪️ Disaster Management
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
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { emergenciesAPI } from '@/services/api'
import Toast from '@/components/Toast.vue'
import ConfirmModal from '@/components/ConfirmModal.vue'
import { showToast, showConfirm, showInput } from '@/composables/interactionService'

export default {
  name: 'MedicalEmergencyDashboardView',
  components: { Toast, ConfirmModal },
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const station = ref('Emergency Station Alpha')
    const callFilter = ref('all')
    
    // Start with realistic fleet totals
    const emergencyStats = ref({
      activeCalls: 4,
      criticalCalls: 2,
      ambulancesDeployed: 2,
      totalAmbulances: 30,
      avgResponseTime: '7.5min',
      targetTime: '8min',
      successRate: 94,
      livesToday: 12
    })
    
    // Local list of emergency calls; will be populated from backend on mount
    const emergencyCalls = ref([])
    
    // Generate 30 ambulances for realistic fleet
    const ambulanceFleet = ref(Array.from({ length: 30 }).map((_, i) => {
      const idx = i + 1
      const types = ['Advanced Life Support', 'Basic Life Support', 'Critical Care']
      const type = types[i % types.length]
      const drivers = ['Ravi Kumar','Amit Patel','Suresh Gupta','Deepak Singh','Manoj Joshi','Rahul Verma','Sunil Rao','Vikas Sharma']
      const paramedics = ['Dr. Priya Shah','Nurse Sunita','Dr. Rahul Joshi','Nurse Meera','Paramedic Anu','Paramedic Rohit','Paramedic Kavita','Dr. Neha']
      return {
        id: idx,
        unitId: `AMB-${String(idx).padStart(3,'0')}`,
        type,
        status: 'Available',
        location: 'Station Alpha',
        crew: { driver: drivers[i % drivers.length], paramedic: paramedics[i % paramedics.length] },
        eta: null
      }
    }))

  // Sync stats with fleet
  emergencyStats.value.totalAmbulances = ambulanceFleet.value.length
  emergencyStats.value.ambulancesDeployed = ambulanceFleet.value.filter(a => a.status !== 'Available').length

  // Generate mock coordinates for ambulances so we can render markers on a simple map
  ambulanceFleet.value.forEach((a, i) => {
    // center around a mock city lat/lng (Pune approx)
    const baseLat = 18.5204
    const baseLng = 73.8567
    const offsetLat = (Math.random() - 0.5) * 0.08
    const offsetLng = (Math.random() - 0.5) * 0.08
    a.latitude = +(baseLat + offsetLat).toFixed(6)
    a.longitude = +(baseLng + offsetLng).toFixed(6)
  })
    
    const hospitals = ref([
      {
        id: 1,
        name: 'City General Hospital',
        location: 'MG Road',
        phone: '+91-20-12345678',
        availableBeds: 8,
        totalBeds: 50
      },
      {
        id: 2,
        name: 'Emergency Care Center',
        location: 'Koregaon Park',
        phone: '+91-20-12345679',
        availableBeds: 3,
        totalBeds: 30
      },
      {
        id: 3,
        name: 'Trauma Center',
        location: 'Viman Nagar',
        phone: '+91-20-12345680',
        availableBeds: 12,
        totalBeds: 40
      }
    ])
    
    const responseStats = ref({
      todayCalls: 28,
      avgResponse: 7,
      successfulRescues: 26
    })
    
    const filteredCalls = computed(() => {
      if (callFilter.value === 'all') {
        return emergencyCalls.value
      }
      return emergencyCalls.value.filter(call => call.priority === callFilter.value)
    })
    
    const navigateTo = (path) => {
      router.push(path)
    }
    
    const logout = () => {
      authStore.logout()
      router.push('/login')
    }
    
    // Emergency Actions
      const createEmergencyCall = async () => {
        // Prompt operator to create a new emergency call (simulated incoming request)
        const type = await showInput({ title: 'Emergency type', message: 'Enter emergency type (e.g. Heart Attack, Road Traffic Accident):', placeholder: 'Road Traffic Accident' })
        if (!type) return
        const location = await showInput({ title: 'Location', message: 'Enter location for the emergency:', placeholder: 'Unknown Location' }) || 'Unknown Location'
        const priority = await showInput({ title: 'Priority', message: 'Priority (critical/high/medium/low):', placeholder: 'high' }) || 'high'
        const phone = await showInput({ title: 'Caller phone', message: 'Caller phone number:', placeholder: '+91-9999999999' }) || '+91-0000000000'
        const description = await showInput({ title: 'Description', message: 'Short description:', placeholder: `${type} reported at ${location}` }) || ''

        // Build payload for backend persistence
        const payload = {
          type,
          location,
          landmark: '',
          severityLevel: (priority || 'high').toLowerCase(),
          description,
          latitude: (18.5204 + (Math.random() - 0.5) * 0.05).toFixed(6),
          longitude: (73.8567 + (Math.random() - 0.5) * 0.05).toFixed(6),
          reporterName: authStore.user?.name || 'Operator',
          contactNumber: phone || authStore.user?.phone || 'operator',
          requiresAmbulance: (priority || '').toLowerCase() === 'critical'
        }

        emergenciesAPI.create(payload)
          .then(res => {
            const saved = res.data?.emergency || res.data
            // normalize timestamp and id fields
            const normalized = {
              id: saved.id || saved._id || saved.emergencyId || Date.now(),
              _id: saved._id,
              type: saved.type,
              priority: (saved.priority || saved.severityLevel || payload.severityLevel).toLowerCase(),
              location: saved.location || payload.location,
              callerPhone: saved.contactNumber || payload.contactNumber || phone,
              description: saved.description || payload.description || '',
              timestamp: saved.createdAt ? new Date(saved.createdAt) : new Date(),
              latitude: saved.latitude ? +saved.latitude : +payload.latitude,
              longitude: saved.longitude ? +saved.longitude : +payload.longitude
            }
            emergencyCalls.value.unshift(normalized)
            emergencyStats.value.activeCalls = emergencyCalls.value.length
            if (normalized.priority === 'critical') emergencyStats.value.criticalCalls++

            showConfirm({ title: 'Dispatch now?', message: 'Create call and dispatch nearest available ambulance now?' })
              .then(accepted => {
                if (accepted) dispatchToCall(normalized)
                else showToast({ message: 'New emergency call added and persisted to backend.', type: 'info' })
              })
          })
          .catch(err => {
            console.error('Failed to create emergency', err)
            showToast({ message: 'Failed to persist emergency to backend — added locally instead.', type: 'warning' })
            const fallback = {
              id: Date.now(), type, priority: (priority || 'high').toLowerCase(), location, callerPhone: phone, description, timestamp: new Date(), latitude: payload.latitude, longitude: payload.longitude
            }
            emergencyCalls.value.unshift(fallback)
            emergencyStats.value.activeCalls = emergencyCalls.value.length
          })
      }
    const dispatchAmbulance = async () => {
      // Operator-triggered dispatch: ask how many ambulances to send, then dispatch up to that many available units.
      const availableUnits = ambulanceFleet.value.filter(a => a.status === 'Available')
      if (availableUnits.length === 0) {
        showToast({ message: 'No ambulances currently available — consider requesting backup.', type: 'warning' })
        return
      }

      // Ask operator how many ambulances to send
      const input = await showInput({ title: 'Dispatch Ambulances', message: `How many ambulances do you want to dispatch? (Available: ${availableUnits.length})`, placeholder: '1' })
      if (!input) return
      const requested = parseInt(String(input).trim(), 10)
      if (isNaN(requested) || requested <= 0) {
        showToast({ message: 'Invalid number entered — dispatch cancelled.', type: 'warning' })
        return
      }

      let toSend = requested
      if (requested > availableUnits.length) {
        toSend = availableUnits.length
        showToast({ message: `Only ${toSend} ambulances are available — dispatching ${toSend}.`, type: 'info' })
      }

      // Build emergency payload for this operator dispatch
      const payload = {
        type: 'Manual Dispatch',
        location: 'Dispatched by operator',
        severityLevel: 'medium',
        description: `Operator dispatch of ${toSend} unit(s)`,
        latitude: availableUnits[0].latitude || 18.5204,
        longitude: availableUnits[0].longitude || 73.8567,
        reporterName: authStore.user?.name || 'Operator',
        contactNumber: authStore.user?.phone || 'operator',
        requiresAmbulance: true
      }

      // Persist emergency, then dispatch the selected units
      emergenciesAPI.create(payload)
        .then(res => {
          const saved = res.data?.emergency || res.data
          const emergencyId = saved?.id || saved?._id || saved?.emergencyId || saved?._id || String(Date.now())

          const unitsToDispatch = availableUnits.slice(0, toSend)
          // dispatch each unit locally and call backend dispatch endpoint where possible
          const dispatchPromises = unitsToDispatch.map(unit => {
            unit.status = 'En Route'
            unit.location = payload.location
            unit.eta = '5 min'
            const teamData = { unitId: unit.unitId, driverId: unit.crew.driver, unitName: unit.unitId }
            // call backend; ignore individual failures but log them
            return emergenciesAPI.dispatchTeam(emergencyId, teamData).catch(err => {
              console.debug('dispatchTeam failed for', unit.unitId, err?.response?.data || err?.message)
              return null
            })
          })

          Promise.all(dispatchPromises).then(() => {
            emergencyStats.value.ambulancesDeployed = ambulanceFleet.value.filter(a => a.status !== 'Available').length
            showToast({ message: `${toSend} ambulance(s) dispatched and persisted to backend (where available).`, type: 'success' })
            // show a popup confirmation with the number dispatched
            showConfirm({ title: 'Ambulances Sent', message: `${toSend} ambulance(s) have been sent to: ${payload.location}` })
          })
        })
        .catch(err => {
          console.error('Operator dispatch failed', err)
          if (err?.response?.data) {
            console.debug('Emergency create response body:', err.response.data)
          }
          // fallback to local update for requested units
          const unitsToDispatch = availableUnits.slice(0, toSend)
          unitsToDispatch.forEach(unit => {
            unit.status = 'En Route'
            unit.eta = '5 min'
          })
          emergencyStats.value.ambulancesDeployed = ambulanceFleet.value.filter(a => a.status !== 'Available').length
          showToast({ message: `${unitsToDispatch.length} ambulance(s) dispatched (local fallback) — backend persistence failed.`, type: 'warning' })
          showConfirm({ title: 'Ambulances Sent (Local)', message: `${unitsToDispatch.length} ambulance(s) have been sent to: ${payload.location}` })
        })
    }
    
    const coordinateWithHospital = () => {
      const hospitalList = hospitals.value
        .map((h, i) => `${i + 1}. ${h.name}\n   📍 ${h.location}\n   🛏️ ${h.availableBeds} beds available\n   📞 ${h.phone}`)
        .join('\n\n')
      
      // simple in-app prompt using confirm chain
      showConfirm({ title: 'Select Hospital', message: `Notify hospital of incoming patient?\n\nSuggested: ${hospitals.value[0].name}` })
        .then(accepted => {
          if (accepted) showToast({ message: `Connected to ${hospitals.value[0].name}. Calling ${hospitals.value[0].phone}`, type: 'info' })
        })
    }
    
    const alertTrafficControl = () => {
      showConfirm({ title: 'Alert Traffic Control', message: 'Send emergency route clearance request? This will alert traffic police and enable priority passage.' })
        .then(accepted => {
          if (accepted) showToast({ message: 'Traffic Control alerted — route clearance activated', type: 'success' })
        })
    }
    
    const requestBackup = () => {
      const backupOptions = [
        '1. Additional Ambulance',
        '2. Fire Department Assistance',
        '3. Police Escort',
        '4. Air Ambulance (Helicopter)',
        '5. Disaster Response Team'
      ]
      
      showConfirm({ title: 'Request Backup', message: 'Request additional backup resources?' })
        .then(accepted => {
          if (accepted) showToast({ message: 'Backup requested — coordination team notified', type: 'info' })
        })
    }
    
  const generateReport = () => {
      const report = `📊 EMERGENCY RESPONSE REPORT\n${'='.repeat(40)}\n\n` +
        `📅 Date: ${new Date().toLocaleDateString()}\n` +
        `⏰ Time: ${new Date().toLocaleTimeString()}\n\n` +
        `🚨 Active Calls: ${emergencyStats.value.activeCalls}\n` +
        `⚠️ Critical: ${emergencyStats.value.criticalCalls}\n` +
        `🚑 Deployed Ambulances: ${emergencyStats.value.ambulancesDeployed}/${emergencyStats.value.totalAmbulances}\n` +
        `⏱️ Avg Response: ${emergencyStats.value.avgResponseTime}\n` +
        `💚 Success Rate: ${emergencyStats.value.successRate}%\n` +
        `🏥 Lives Saved Today: ${emergencyStats.value.livesToday}\n\n` +
        `${'='.repeat(40)}\n` +
        `Report generated successfully!`
      
      showToast({ message: 'Report generated (preview in console).', type: 'info' })
      console.log('Downloading report...')
    }
    
    const refreshCalls = () => {
      const newCallsCount = Math.floor(Math.random() * 3)
      if (newCallsCount > 0) {
        showToast({ message: `${newCallsCount} new emergency call(s) received — please review.`, type: 'warning' })
      } else {
        showToast({ message: 'Emergency calls list refreshed — no new calls', type: 'info' })
      }
    }
    
    const dispatchToCall = (call) => {
      // helper to determine if the original emergency form requested an ambulance
      const callRequestedAmbulance = (c) => {
        if (!c) return false
        // possible variations from different payload shapes
        const v = c.ambulanceRequested ?? c.requestedAmbulance ?? c.needsAmbulance ?? c.ambulance ?? c.ambulance_request
        if (v === undefined || v === null) return false
        // accept boolean true, string 'yes'/'true', number 1
        if (typeof v === 'boolean') return v
        if (typeof v === 'number') return v === 1
        if (typeof v === 'string') return ['yes', 'true', '1'].includes(v.toLowerCase())
        return false
      }
      // helper to detect if the call was submitted by a sergeant
      const callSubmittedBySergeant = (c) => {
        if (!c) return false
        const roleCandidates = [
          c.reporterRole,
          c.reporter?.role,
          c.submittedByRole,
          c.reporterTitle,
          c.submittedBy,
          c.reporterName
        ]
        for (const r of roleCandidates) {
          if (!r) continue
          const s = String(r).toLowerCase()
          if (s.includes('sergeant') || s === 'sgt' || s === 'srgt' || s === 'serg') return true
        }
        return false
      }

  const availableUnits = ambulanceFleet.value.filter(a => a.status === 'Available')
      
      if (availableUnits.length === 0) {
        showToast({ message: `⚠️ NO AMBULANCES AVAILABLE — All units are currently deployed. Consider requesting backup for ${call.type}.`, type: 'warning' })
        return
      }
      
      const unitList = availableUnits
        .map((u, i) => `${i + 1}. ${u.unitId} - ${u.type}\n   Driver: ${u.crew.driver}\n   Paramedic: ${u.crew.paramedic}`)
        .join('\n\n')
      
      // present a simple confirm and pick first unit for UX simplicity
      const unit = availableUnits[0]
      if (!unit) {
        showToast({ message: 'No available units to dispatch', type: 'warning' })
        return
      }
      showConfirm({ title: 'Dispatch Unit', message: `Dispatch ${unit.unitId} to ${call.location}?` })
        .then(accepted => {
          if (!accepted) return

        // Call backend to persist dispatch assignment
        const teamData = {
          unitId: unit.unitId,
          driverId: unit.crew.driver,
          unitName: unit.unitId
        }

        emergenciesAPI.dispatchTeam(call.id || call._id || call.emergencyId || call.id, teamData)
          .then(res => {
            const updated = res.data?.emergency || res.data
            // update local ambulance and call state using response
            unit.status = 'En Route'
            unit.location = call.location
            unit.eta = '4 min'
            emergencyStats.value.ambulancesDeployed = ambulanceFleet.value.filter(a => a.status !== 'Available').length
            // update call in list if present
            const idx = emergencyCalls.value.findIndex(c => c.id === call.id || c._id === call._id || c.id === updated.id || c._id === updated._id)
            if (idx !== -1) {
              emergencyCalls.value[idx] = { ...emergencyCalls.value[idx], ...updated }
            }
            showToast({ message: `${unit.unitId} dispatched to ${call.location} — ETA ${unit.eta}`, type: 'success' })
            // Only show the confirmation popup that an ambulance was sent to the form-entered location
            // when the original emergency form explicitly requested an ambulance.
            if (callRequestedAmbulance(call)) {
              if (callSubmittedBySergeant(call)) {
                showConfirm({ title: 'Ambulance Sent', message: `The ambulance has been sent to the location "${call.location}" by the sergeant.` })
              } else {
                showConfirm({ title: 'Ambulance Sent', message: `An ambulance has been sent to the location provided in the Emergency Form: ${call.location}` })
              }
            }
          })
          .catch(err => {
            console.error('Dispatch failed', err)
            // fallback local update
            unit.status = 'En Route'
            unit.location = call.location
            unit.eta = '4 min'
            emergencyStats.value.ambulancesDeployed = ambulanceFleet.value.filter(a => a.status !== 'Available').length
            showToast({ message: `${unit.unitId} dispatched (local fallback) — backend dispatch failed.`, type: 'warning' })
            if (callRequestedAmbulance(call)) {
              if (callSubmittedBySergeant(call)) {
                showConfirm({ title: 'Ambulance Sent (Local)', message: `The ambulance has been sent to the location "${call.location}" by the sergeant.` })
              } else {
                showConfirm({ title: 'Ambulance Sent (Local)', message: `An ambulance has been sent to the location provided in the Emergency Form: ${call.location}` })
              }
            }
          })
        })
    }
    
    const updateCallStatus = (call) => {
      const statusOptions = [
        '1. Ambulance Dispatched',
        '2. Crew Arrived at Scene',
        '3. Patient Stabilized',
        '4. En Route to Hospital',
        '5. Handed to Hospital',
        '6. Call Completed'
      ]
      
      showConfirm({ title: 'Update Call Status', message: `Mark ${call.type} as 'At Scene'?` })
        .then(accepted => {
          if (accepted) showToast({ message: `Status updated: ${call.type} — At Scene`, type: 'info' })
        })
    }
    
    const escalateCall = (call) => {
      showConfirm({ title: 'Escalate Emergency', message: `Escalate ${call.type} and request additional resources?` })
        .then(accepted => {
          if (accepted) showToast({ message: `Emergency escalated: ${call.type} — Command notified`, type: 'warning' })
        })
    }
    
    const refreshFleet = () => {
      showToast({ message: `Fleet refreshed — ${ambulanceFleet.value.length} units tracked`, type: 'info' })
    }
    
    const trackAmbulance = (ambulance) => {
      showToast({ message: `${ambulance.unitId} — ${ambulance.status} at ${ambulance.location}`, type: 'info' })
    }
    
  const contactCrew = async (ambulance) => {
      const options = [
        '1. Voice Call',
        '2. Send Message',
        '3. Emergency Alert'
      ]
      
      const selection = await showInput({ title: 'Contact Crew', message: `📞 CONTACT CREW: ${ambulance.unitId}\n\nDriver: ${ambulance.crew.driver}\nParamedic: ${ambulance.crew.paramedic}\nStatus: ${ambulance.status}\n\n${options.join('\n')}\n\nSelect option (1-3):`, placeholder: '1' })

      if (selection === '1') {
        showToast({ message: `Calling ${ambulance.crew.driver}...`, type: 'info' })
      } else if (selection === '2') {
        const message = await showInput({ title: 'Send Message', message: `📱 Send message to ${ambulance.unitId}:`, placeholder: '' })
        if (message) {
          showToast({ message: `Message sent to ${ambulance.unitId}`, type: 'success' })
        }
      } else if (selection === '3') {
        showToast({ message: `Emergency alert sent to ${ambulance.unitId}`, type: 'warning' })
      }
    }
    
    const reassignAmbulance = (ambulance) => {
      if (ambulance.status === 'Available') {
        showToast({ message: `ℹ️ ${ambulance.unitId} is available — Use the dispatch function to assign to an emergency call.`, type: 'info' })
        return
      }
      
      showConfirm({ title: 'Reassign Unit', message: `Reassign ${ambulance.unitId} and mark as Available?` })
        .then(accepted => {
          if (!accepted) return
          ambulance.status = 'Available'
          ambulance.eta = null
          emergencyStats.value.ambulancesDeployed = ambulanceFleet.value.filter(a => a.status !== 'Available').length
          showToast({ message: `${ambulance.unitId} reassigned and marked Available`, type: 'info' })
        })
    }
    
    const refreshMap = () => {
      // simulate a refresh by slightly jittering ambulance positions
      ambulanceFleet.value.forEach(a => {
        a.latitude = +(a.latitude + (Math.random() - 0.5) * 0.002).toFixed(6)
        a.longitude = +(a.longitude + (Math.random() - 0.5) * 0.002).toFixed(6)
      })
      // Optionally refetch active emergencies
      fetchActiveEmergencies()
    }

    // Fetch active emergencies from backend and populate emergencyCalls
    const fetchActiveEmergencies = () => {
      emergenciesAPI.getActive()
        .then(res => {
          const list = res.data?.emergencies || res.data || []
          // normalize createdAt/timestamps
          emergencyCalls.value = list.map(e => ({
            id: e.id || e._id || e.emergencyId || e.id,
            _id: e._id,
            type: e.type,
            priority: (e.priority || e.severityLevel || 'medium').toLowerCase(),
            location: e.location || e.landmark || 'Unknown',
            callerPhone: e.callerPhone || e.contactNumber || 'Unknown',
            description: e.description || e.additionalNotes || '',
            timestamp: e.createdAt ? new Date(e.createdAt) : new Date(),
            latitude: e.latitude ? +e.latitude : (18.5204 + (Math.random() - 0.5) * 0.05),
            longitude: e.longitude ? +e.longitude : (73.8567 + (Math.random() - 0.5) * 0.05)
          }))
          emergencyStats.value.activeCalls = emergencyCalls.value.length
          emergencyStats.value.criticalCalls = emergencyCalls.value.filter(c => c.priority === 'critical').length
        })
        .catch(err => {
          console.error('Failed to fetch active emergencies', err)
        })
    }

    // on mount, load active emergencies and start polling so externally-created emergencies appear
    let pollTimer = null
    onMounted(() => {
      fetchActiveEmergencies()
      // poll every 8 seconds
      pollTimer = setInterval(() => {
        fetchActiveEmergencies()
      }, 8000)
    })

    onUnmounted(() => {
      if (pollTimer) clearInterval(pollTimer)
    })
    
  const callContact = async (type) => {
      const contacts = {
        police: { 
          name: 'Police Control Room',
          number: '100',
          description: 'For law enforcement and security backup'
        },
        fire: { 
          name: 'Fire Department',
          number: '101',
          description: 'For fire emergencies and rescue operations'
        },
        hospital: { 
          name: 'Hospital Emergency',
          number: '102',
          description: 'For direct hospital coordination'
        },
        traffic: { 
          name: 'Traffic Control',
          number: '103',
          description: 'For route clearance and traffic management'
        },
        disaster: { 
          name: 'Disaster Management',
          number: '104',
          description: 'For major incidents and disaster response'
        }
      }
      
      const contact = contacts[type]
      const confirmed = await showConfirm({ title: `Call ${contact.name}`, message: `Number: ${contact.number}\nPurpose: ${contact.description}\n\nProceed with call?` })
      if (confirmed) {
        showToast({ message: `✅ Calling ${contact.name}... Dialing ${contact.number}. Please state your emergency.`, type: 'info' })
      }
    }
    
    const formatDateTime = (date) => {
      return date.toLocaleString('en-IN')
    }
    
    const getCallDuration = (timestamp) => {
      const now = new Date()
      const diff = now - timestamp
      const minutes = Math.floor(diff / (1000 * 60))
      return `${minutes} min`
    }
    
    const getPriorityColor = (priority) => {
      const colors = {
        'critical': '#dc2626',
        'high': '#f59e0b',
        'medium': '#3b82f6',
        'low': '#16a34a'
      }
      return colors[priority] || '#6b7280'
    }
    
    const getPriorityBg = (priority) => {
      const backgrounds = {
        'critical': '#fef2f2',
        'high': '#fefce8',
        'medium': '#eff6ff',
        'low': '#f0fdf4'
      }
      return backgrounds[priority] || '#f9fafb'
    }
    
    const getFleetStatusClass = (status) => {
      const classes = {
        'Available': 'bg-green-100 text-green-800',
        'En Route': 'bg-blue-100 text-blue-800',
        'At Hospital': 'bg-yellow-100 text-yellow-800',
        'Maintenance': 'bg-red-100 text-red-800',
        'Off Duty': 'bg-gray-100 text-gray-800'
      }
      return classes[status] || 'bg-gray-100 text-gray-800'
    }
    
    // Simple equirectangular projection for mock map positioning inside the container
    const latLngToPoint = (lat, lng, containerWidth = 320, containerHeight = 200) => {
      // using bounds around Pune approx +/-0.06 deg so markers remain inside
      const minLat = 18.44
      const maxLat = 18.60
      const minLng = 73.80
      const maxLng = 73.92

      const x = ((lng - minLng) / (maxLng - minLng)) * containerWidth
      const y = (1 - (lat - minLat) / (maxLat - minLat)) * containerHeight
      return { x: Math.max(0, Math.min(containerWidth, x)), y: Math.max(0, Math.min(containerHeight, y)) }
    }

    const markerStyle = (lat, lng, type = 'call') => {
      const containerWidth = 320
      const containerHeight = 300
      const point = lat && lng ? latLngToPoint(+lat, +lng, containerWidth, containerHeight) : { x: Math.random() * containerWidth, y: Math.random() * containerHeight }
      const size = type === 'ambulance' ? 28 : 22
      const bg = type === 'ambulance' ? 'rgba(59,130,246,0.9)' : 'rgba(220,38,38,0.95)'
      return {
        position: 'absolute',
        left: `${Math.round(point.x - size / 2)}px`,
        top: `${Math.round(point.y - size / 2)}px`,
        width: `${size}px`,
        height: `${size}px`,
        borderRadius: '50%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        fontSize: type === 'ambulance' ? '14px' : '12px',
        color: 'white',
        background: bg,
        boxShadow: '0 2px 6px rgba(0,0,0,0.2)',
        pointerEvents: 'auto'
      }
    }

    return {
      authStore,
      station,
      callFilter,
      markerStyle,
      latLngToPoint,
      emergencyStats,
      emergencyCalls,
      ambulanceFleet,
      hospitals,
      responseStats,
      filteredCalls,
      navigateTo,
      logout,
      createEmergencyCall,
      dispatchAmbulance,
      coordinateWithHospital,
      alertTrafficControl,
      requestBackup,
      generateReport,
      refreshCalls,
      dispatchToCall,
      updateCallStatus,
      escalateCall,
      refreshFleet,
      trackAmbulance,
      contactCrew,
      reassignAmbulance,
      refreshMap,
      callContact,
      formatDateTime,
      getCallDuration,
      getPriorityColor,
      getPriorityBg,
      getFleetStatusClass
    }
  }
}
</script>

<style scoped>
.emergency-card {
  padding: 1.5rem;
  border-radius: 12px;
  color: white;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.card-icon {
  font-size: 2.5rem;
}

.card-content {
  flex: 1;
}

.card-number {
  font-size: 1.8rem;
  font-weight: bold;
  margin-bottom: 0.25rem;
}

.card-label {
  font-size: 1rem;
  opacity: 0.9;
  margin-bottom: 0.25rem;
}

.card-detail {
  font-size: 0.8rem;
  opacity: 0.8;
}

.emergency-action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  color: white;
  font-weight: 500;
  transition: all 0.2s;
}

.emergency-action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.btn-icon {
  font-size: 1.5rem;
}

.btn-text {
  font-size: 0.9rem;
}

.map-container {
  position: relative;
  overflow: hidden;
}

.contact-btn {
  width: 100%;
  padding: 0.75rem;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
  font-size: 0.9rem;
}

.contact-btn:hover {
  background: #e5e7eb;
}

.btn-small {
  padding: 0.25rem 0.5rem;
  font-size: 0.8rem;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  cursor: pointer;
}

.btn-danger {
  background: #fecaca;
  color: #dc2626;
  border-color: #f87171;
}

.btn-primary {
  background: #dbeafe;
  color: #2563eb;
  border-color: #93c5fd;
}

.btn-warning {
  background: #fef3c7;
  color: #92400e;
  border-color: #fcd34d;
}
</style>