<template>
  <div class="dashboard">
    <!-- Traffic Sergeant Navigation Bar -->
    <nav style="
      background: #1d4ed8 !important; 
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
        <h1 style="font-size: 1.5rem; font-weight: bold; margin: 0; color: white !important;">Traffic Sergeant Dashboard</h1>
        <p style="margin: 0; color: white !important; opacity: 0.9;">Zone: {{ currentZone }}</p>
      </div>
      <div style="display: flex; gap: 1rem; align-items: center;">
        <span style="color: white !important;">{{ authStore.user?.name || 'Sergeant' }}</span>
        <button @click="logout" style="
          background: white !important; 
          color: #1d4ed8 !important; 
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
      <!-- Quick Action Cards -->
      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-bottom: 2rem;">
        <div class="quick-action-card" @click="navigateTo('/violations/create')" style="background: linear-gradient(135deg, #dc2626, #b91c1c);">
          <div class="action-icon">🚨</div>
          <div class="action-title">Register Violation</div>
          <div class="action-desc">Log traffic violations</div>
        </div>
        
        <div class="quick-action-card" @click="navigateTo('/violations')" style="background: linear-gradient(135deg, #2563eb, #1d4ed8);">
          <div class="action-icon">🎫</div>
          <div class="action-title">Issue Ticket</div>
          <div class="action-desc">Generate e-tickets</div>
        </div>
        
        <div class="quick-action-card" @click="triggerSOS" :class="{ 'sos-active': sosActive }" style="background: linear-gradient(135deg, #dc2626, #991b1b);">
          <div class="action-icon">🆘</div>
          <div class="action-title">{{ sosActive ? 'SOS Active' : 'Raise SOS' }}</div>
          <div class="action-desc">Emergency backup</div>
        </div>
        
        <div class="quick-action-card" @click="navigateTo('/emergencies/create')" style="background: linear-gradient(135deg, #f59e0b, #d97706);">
          <div class="action-icon">🚑</div>
          <div class="action-title">Register Emergency</div>
          <div class="action-desc">Accidents & incidents</div>
        </div>
      </div>

      <!-- Status Overview -->
      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1rem; margin-bottom: 2rem;">
        <div class="stat-card">
          <div class="stat-header">
            <h3>📋 Today's Activity</h3>
          </div>
          <div class="stat-body">
            <div class="stat-item">
              <span>Violations Logged:</span>
              <strong>{{ todayStats.violations }}</strong>
            </div>
            <div class="stat-item">
              <span>Tickets Issued:</span>
              <strong>{{ todayStats.tickets }}</strong>
            </div>
            <div class="stat-item">
              <span>Fines Collected:</span>
              <strong>₹{{ todayStats.fines.toLocaleString() }}</strong>
            </div>
          </div>
        </div>
        
        
      </div>

      <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 2rem;">
        <!-- Recent Violations -->
        <div>
          <div class="card">
            <div class="card-header">
              <h3>📝 My Recent Violations</h3>
              <button @click="navigateTo('/violations')" class="btn">View All</button>
            </div>
            <div class="card-body">
              <div v-if="recentViolations.length === 0" style="text-align: center; color: #6b7280; padding: 2rem;">
                No violations logged today
              </div>
              <div v-else>
                <div 
                  v-for="violation in recentViolations" 
                  :key="violation.id"
                  style="display: flex; justify-content: space-between; align-items: center; padding: 1rem; border-bottom: 1px solid #e5e7eb;"
                >
                  <div>
                    <div style="font-weight: 500;">{{ violation.vehicleNumber }}</div>
                    <div style="font-size: 0.9rem; color: #6b7280;">{{ violation.violationType }}</div>
                    <div style="font-size: 0.8rem; color: #9ca3af;">{{ formatTime(violation.timestamp) }}</div>
                  </div>
                  <div style="text-align: right;">
                    <div style="font-weight: 500;">₹{{ violation.fineAmount }}</div>
                    <div :class="getStatusClass(violation.status)" style="font-size: 0.8rem; padding: 0.25rem 0.5rem; border-radius: 4px;">
                      {{ violation.status }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Pending Tickets -->
          <div class="card" style="margin-top: 2rem;">
            <div class="card-header">
              <h3>🎫 Pending Tickets</h3>
              <div style="font-size: 0.9rem; color: #6b7280;">{{ pendingTickets.length }} unpaid</div>
            </div>
            <div class="card-body">
              <div v-if="pendingTickets.length === 0" style="text-align: center; color: #6b7280; padding: 2rem;">
                All tickets paid! 🎉
              </div>
              <div v-else>
                <div 
                  v-for="ticket in pendingTickets" 
                  :key="ticket.id"
                  style="display: flex; justify-content: space-between; align-items: center; padding: 1rem; border-bottom: 1px solid #e5e7eb;"
                >
                  <div>
                    <div style="font-weight: 500;">{{ ticket.ticketNumber }}</div>
                    <div style="font-size: 0.9rem; color: #6b7280;">{{ ticket.vehicleNumber }} - {{ ticket.violationType }}</div>
                    <div style="font-size: 0.8rem; color: #dc2626;">Due: {{ formatDate(ticket.dueDate) }}</div>
                  </div>
                  <div style="text-align: right;">
                    <div style="font-weight: 500; color: #dc2626;">₹{{ ticket.amount }}</div>
                    <button @click="followUpPayment(ticket)" class="btn-small">Follow Up</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Sidebar -->
        <div>
          

          <!-- Zone Map Section -->
<div class="card mt-3">
  <div class="card-header">
    <h3>📍 Zone Map</h3>
  </div>
  <div class="card-body">
    <p class="text-muted mb-2">
      Current Location: {{ currentLocation }}
    </p>
    <div
      ref="mapContainer"
      id="map"
      style="height: 400px; width: 100%; border-radius: 8px; overflow: hidden; background-color: #f0f0f0;"
    >
      <p v-if="!mapLoaded" class="text-center mt-5 text-gray-500">
        Loading map...
      </p>
    </div>
  </div>
</div>



          <!-- Quick Actions -->
          <div class="card">
            <div class="card-header">
              <h3>⚡ Quick Actions</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; gap: 1rem;">
                <!-- Duty Status Buttons -->
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.5rem;">
                  <button 
                    @click="startDuty" 
                    class="action-btn" 
                    :disabled="onDuty"
                    :style="{ 
                      background: onDuty ? '#10b981' : '#f3f4f6', 
                      color: onDuty ? 'white' : '#374151',
                      border: onDuty ? '2px solid #059669' : '1px solid #d1d5db'
                    }"
                  >
                    {{ onDuty ? '✅ On Duty' : '▶️ On Duty' }}
                  </button>
                  <button 
                    @click="endDuty" 
                    class="action-btn" 
                    :disabled="!onDuty"
                    :style="{ 
                      background: !onDuty ? '#ef4444' : '#f3f4f6', 
                      color: !onDuty ? 'white' : '#374151',
                      border: !onDuty ? '2px solid #dc2626' : '1px solid #d1d5db'
                    }"
                  >
                    {{ !onDuty ? '⏹️ Off Duty' : '⏹️ Off Duty' }}
                  </button>
                </div>
                <button @click="requestBackup" class="action-btn">
                  👮 Request Backup
                </button>
                <button @click="reportIncident" class="action-btn">
                  📢 Report Incident
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
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { dashboardAPI, violationsAPI, emergenciesAPI, usersAPI, apiUtils } from '@/services/api'
import { showToast, showConfirm, showInput } from '@/composables/interactionService'

export default {
  name: 'TrafficSergeantDashboardView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()

    const currentZone = ref()
    const gpsActive = ref(true)
    const onDuty = ref(false)
    const dutyStartTime = ref('—')
    const sosActive = ref(false)

    const todayStats = ref({
      violations: 0,
      tickets: 0,
      fines: 0
    })

    const recentViolations = ref([])
    const pendingTickets = ref([])
    const nearbyAlerts = ref([])

    const navigateTo = (path) => router.push(path)

    const logout = () => {
      authStore.logout()
      router.push('/login')
    }

    const loadDashboard = async () => {
      try {
        const statsResp = await dashboardAPI.getStats()
        todayStats.value.violations = statsResp.data.violations || 0
        todayStats.value.emergencies = statsResp.data.emergencies || 0
        todayStats.value.officersOnDuty = statsResp.data.officersOnDuty || 0

        const recent = await dashboardAPI.getRecentActivity()
        recentViolations.value = recent.data.violations || []
        nearbyAlerts.value = recent.data.emergencies || []
      } catch (err) {
        console.warn('Error loading dashboard:', apiUtils.formatError(err))
      }
    }

    const triggerSOS = async () => {
      sosActive.value = !sosActive.value
      try {
        if (sosActive.value) {
          // try to fetch geolocation, fallback to nulls
          let coords = { latitude: null, longitude: null }
          try {
            coords = await (new Promise((resolve, reject) => {
              navigator.geolocation.getCurrentPosition(pos => resolve({ latitude: pos.coords.latitude, longitude: pos.coords.longitude }), reject, { enableHighAccuracy: true, timeout: 8000 })
            }))
          } catch (e) { /* ignore */ }

          await emergenciesAPI.triggerSOS({ latitude: coords.latitude, longitude: coords.longitude })
          showToast({ title: 'SOS', message: 'SOS Alert sent! Backup requested.', type: 'success' })
          await loadDashboard()
        } else {
          await emergenciesAPI.cancelSOS()
          showToast({ title: 'SOS', message: 'SOS Alert cancelled.', type: 'info' })
        }
      } catch (err) {
        showToast({ title: 'SOS Error', message: 'Failed to send SOS: ' + apiUtils.formatError(err), type: 'error' })
      }
    }
    

    // ✅ Safe Google Map Setup
    const mapContainer = ref(null)
    const map = ref(null)
    const mapLoaded = ref(false)
    const currentLocation = ref('Locating...')

    const loadGoogleMaps = () => {
      return new Promise((resolve, reject) => {
        if (window.google && window.google.maps) return resolve()
        const apiKey = import.meta.env.VITE_MAPS_API_KEY
        if (!apiKey) {
          console.warn('⚠️ Missing Google Maps API key')
          return reject('Missing API key')
        }
        const script = document.createElement('script')
        script.src = `https://maps.googleapis.com/maps/api/js?key=${apiKey}&libraries=places`
        script.async = true
        script.defer = true
        script.onload = resolve
        script.onerror = (err) => reject(err)
        document.head.appendChild(script)
      })
    }

    const initMap = async () => {
      try {
        await loadGoogleMaps()

        navigator.geolocation.getCurrentPosition(
          (pos) => {
            const { latitude, longitude } = pos.coords
            currentLocation.value = `${latitude.toFixed(5)}, ${longitude.toFixed(5)}`
            map.value = new google.maps.Map(mapContainer.value, {
              center: { lat: latitude, lng: longitude },
              zoom: 15,
              mapTypeId: google.maps.MapTypeId.ROADMAP,
            })
            new google.maps.Marker({
              position: { lat: latitude, lng: longitude },
              map: map.value,
              title: 'You are here',
            })
            mapLoaded.value = true
          },
          (err) => {
            console.warn('⚠️ Geolocation error:', err.message)
            currentLocation.value = 'Location unavailable'
            map.value = new google.maps.Map(mapContainer.value, {
              center: { lat: 19.076, lng: 72.8777 },
              zoom: 10,
            })
            mapLoaded.value = true
          },
          { enableHighAccuracy: true, timeout: 8000 }
        )
      } catch (error) {
        console.error('Map initialization failed:', error)
        currentLocation.value = 'Map load failed'
        mapLoaded.value = false
      }
    }

    onMounted(async () => {
      try {
        await initMap()
      } catch (e) {
        console.error('Mount map error:', e)
      }
    })



    const refreshLocation = async () => {
      try {
        const pos = await new Promise((resolve, reject) => {
          navigator.geolocation.getCurrentPosition(resolve, reject, { enableHighAccuracy: true, timeout: 8000 })
        })
        currentLocation.value = `${pos.coords.latitude.toFixed(5)}, ${pos.coords.longitude.toFixed(5)}`
        gpsActive.value = true
      } catch (e) {
        gpsActive.value = false
        showToast({ title: 'Location', message: 'Unable to refresh location', type: 'warning' })
      }
    }

    const refreshAlerts = async () => {
      try {
        await loadDashboard()
        showToast({ title: 'Alerts', message: 'Alerts refreshed!', type: 'success' })
      } catch (err) {
        showToast({ title: 'Alerts', message: 'Failed to refresh alerts: ' + apiUtils.formatError(err), type: 'error' })
      }
    }

    const followUpPayment = async (ticket) => {
      try {
        await violationsAPI.updatePaymentStatus(ticket.id, 'FollowUp')
        showToast({ title: 'Ticket', message: `Following up on ticket ${ticket.ticketNumber}`, type: 'info' })
      } catch (err) {
        showToast({ title: 'Ticket Error', message: 'Failed to follow up payment: ' + apiUtils.formatError(err), type: 'error' })
      }
    }

    const respondToAlert = async (alertItem) => {
      try {
        const unitId = await showInput({ title: 'Dispatch Unit', message: 'Enter backup unit id to dispatch (optional)', placeholder: '' })
        const driverId = await showInput({ title: 'Driver ID', message: 'Enter driver id (optional)', placeholder: '' })
        if (!alertItem.id) {
          showToast({ title: 'Dispatch', message: 'Alert has no id to dispatch', type: 'warning' })
          return
        }
        await emergenciesAPI.dispatchTeam(alertItem.id, { unitId, driverId })
        showToast({ title: 'Dispatch', message: 'Dispatched to alert', type: 'success' })
        await loadDashboard()
      } catch (err) {
        showToast({ title: 'Dispatch Error', message: 'Failed to respond: ' + apiUtils.formatError(err), type: 'error' })
      }
    }

    const startDuty = async () => {
      try {
        const userId = authStore.user?.id || authStore.user?.username
        if (!userId) {
          showToast({ title: 'Duty', message: 'No user id available', type: 'warning' })
          return
        }
        await usersAPI.updateDutyStatus(userId, true)
        onDuty.value = true
        dutyStartTime.value = new Date().toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit' })
        showToast({ title: 'Duty', message: 'Duty started! You are now ON DUTY.', type: 'success' })
      } catch (err) {
        showToast({ title: 'Duty Error', message: 'Failed to start duty: ' + apiUtils.formatError(err), type: 'error' })
      }
    }

    const endDuty = async () => {
      try {
        const userId = authStore.user?.id || authStore.user?.username
        if (!userId) {
          showToast({ title: 'Duty', message: 'No user id available', type: 'warning' })
          return
        }
        await usersAPI.updateDutyStatus(userId, false)
        onDuty.value = false
        dutyStartTime.value = '—'
        showToast({ title: 'Duty', message: 'Duty ended. You are now OFF DUTY.', type: 'info' })
      } catch (err) {
        showToast({ title: 'Duty Error', message: 'Failed to end duty: ' + apiUtils.formatError(err), type: 'error' })
      }
    }

    const requestBackup = async () => {
      try {
        // create an emergency as a backup request
        let coords = { latitude: null, longitude: null }
        try {
          coords = await (new Promise((resolve, reject) => {
            navigator.geolocation.getCurrentPosition(pos => resolve({ latitude: pos.coords.latitude, longitude: pos.coords.longitude }), reject, { enableHighAccuracy: true, timeout: 8000 })
          }))
        } catch (e) { /* ignore */ }
        await emergenciesAPI.triggerSOS({ latitude: coords.latitude, longitude: coords.longitude })
        showToast({ title: 'Backup', message: 'Backup requested! Nearby officers notified.', type: 'success' })
        await loadDashboard()
      } catch (err) {
        showToast({ title: 'Backup Error', message: 'Failed to request backup: ' + apiUtils.formatError(err), type: 'error' })
      }
    }

    const reportIncident = async () => {
      try {
        // navigate to create emergency form
        router.push('/emergencies/create')
      } catch (err) {
        showToast({ title: 'Error', message: 'Failed to open incident form', type: 'error' })
      }
    }

    const formatTime = (date) => {
      try { return new Date(date).toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit' }) } catch (e) { return date }
    }

    const formatDate = (date) => {
      try { return new Date(date).toLocaleDateString('en-IN') } catch (e) { return date }
    }

    const getStatusClass = (status) => {
      const classes = {
        'Pending': 'bg-yellow-100 text-yellow-800',
        'Paid': 'bg-green-100 text-green-800',
        'Overdue': 'bg-red-100 text-red-800'
      }
      return classes[status] || 'bg-gray-100 text-gray-800'
    }

    onMounted(async () => {
      await loadDashboard()
      await initMap()
    })


    onMounted(async () => {
      await loadDashboard()
      try {
        const onDutyResp = await usersAPI.getOnDuty()
        onDuty.value = Array.isArray(onDutyResp.data) && onDutyResp.data.some(u => u.username === authStore.user?.username || u.id === authStore.user?.id)
      } catch (e) { /* ignore */ }
    })

    return {
      mapContainer,
      mapLoaded,
      currentLocation,
      authStore,
      currentZone,
      currentLocation,
      gpsActive,
      onDuty,
      dutyStartTime,
      sosActive,
      todayStats,
      recentViolations,
      pendingTickets,
      nearbyAlerts,
      navigateTo,
      logout,
      triggerSOS,
      refreshLocation,
      refreshAlerts,
      followUpPayment,
      respondToAlert,
      startDuty,
      endDuty,
      requestBackup,
      reportIncident,
      formatTime,
      formatDate,
      getStatusClass
    }
  }
}
</script>

<style scoped>
.quick-action-card {
  padding: 1.5rem;
  border-radius: 12px;
  color: white;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  text-align: center;
}

.quick-action-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.sos-active {
  animation: pulse-red 2s infinite;
}

@keyframes pulse-red {
  0% { box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.7); }
  70% { box-shadow: 0 0 0 10px rgba(239, 68, 68, 0); }
  100% { box-shadow: 0 0 0 0 rgba(239, 68, 68, 0); }
}

.action-icon {
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
}

.action-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 0.25rem;
}

.action-desc {
  font-size: 0.9rem;
  opacity: 0.9;
}

.stat-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
}

.stat-header {
  padding: 1rem 1.5rem 0.5rem 1.5rem;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-header h3 {
  margin: 0;
  color: #374151;
}

.stat-body {
  padding: 1rem 1.5rem;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.action-btn {
  width: 100%;
  padding: 0.75rem;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover:not(:disabled) {
  background: #e5e7eb;
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-small {
  padding: 0.25rem 0.75rem;
  font-size: 0.8rem;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  cursor: pointer;
}
</style>