<template>
  <div>
    <!-- Navigation -->
    <nav style="background: #3b82f6; color: white; padding: 1rem; display: flex; justify-content: space-between; align-items: center;">
      <div style="display: flex; align-items: center; gap: 1rem;">
        <button @click="goBack" style="background: rgba(255,255,255,0.2); border: none; color: white; padding: 0.5rem; border-radius: 4px; cursor: pointer;">
          ← Back
        </button>
        <div style="font-size: 1.5rem; font-weight: bold;">
          🚦 Real-Time Traffic Monitoring Center
        </div>
      </div>
      <div style="font-size: 0.9rem;">
        {{ authStore.user?.role || 'Traffic Officer' }}: {{ authStore.user?.name || 'Officer' }}
      </div>
    </nav>

    <div class="dashboard">
      <!-- Live Stats -->
      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-bottom: 2rem;">
        <div class="stat-card" style="background: linear-gradient(135deg, #16a34a, #15803d);">
          <div class="stat-icon">🟢</div>
          <div class="stat-content">
            <div class="stat-number">{{ liveStats.normalFlow }}</div>
            <div class="stat-label">Normal Traffic Flow</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #f59e0b, #d97706);">
          <div class="stat-icon">🟡</div>
          <div class="stat-content">
            <div class="stat-number">{{ liveStats.moderateCongestion }}</div>
            <div class="stat-label">Moderate Congestion</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #dc2626, #991b1b);">
          <div class="stat-icon">🔴</div>
          <div class="stat-content">
            <div class="stat-number">{{ liveStats.heavyTraffic }}</div>
            <div class="stat-label">Heavy Traffic Zones</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #3b82f6, #1e40af);">
          <div class="stat-icon">📊</div>
          <div class="stat-content">
            <div class="stat-number">{{ liveStats.avgSpeed }}</div>
            <div class="stat-label">Avg Speed (km/h)</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #7c3aed, #5b21b6);">
          <div class="stat-icon">🚨</div>
          <div class="stat-content">
            <div class="stat-number">{{ liveStats.incidents }}</div>
            <div class="stat-label">Active Incidents</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #059669, #047857);">
          <div class="stat-icon">👮</div>
          <div class="stat-content">
            <div class="stat-number">{{ liveStats.officersDeployed }}</div>
            <div class="stat-label">Officers Deployed</div>
          </div>
        </div>
      </div>

      <div style="display: grid; grid-template-columns: 1fr 350px; gap: 2rem;">
        <!-- Main Content -->
        <div>
          <!-- Control Center -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>🎛️ Traffic Control Center</h3>
              <div style="display: flex; gap: 1rem;">
                <button @click="refreshData" class="btn btn-primary">🔄 Refresh Data</button>
                <button @click="exportReport" class="btn btn-secondary">📊 Export Report</button>
                <button @click="toggleAutoRefresh" :class="autoRefresh ? 'btn btn-success' : 'btn btn-warning'">
                  {{ autoRefresh ? '⏸️ Auto Refresh ON' : '▶️ Auto Refresh OFF' }}
                </button>
              </div>
            </div>
            <div class="card-body">
              <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1rem;">
                <!-- Traffic Signal Controls -->
                <div style="border: 1px solid #e5e7eb; border-radius: 8px; padding: 1rem;">
                  <h4 style="margin: 0 0 1rem 0; color: #374151;">🚦 Signal Controls</h4>
                  <div style="display: grid; gap: 0.5rem;">
                    <button @click="overrideSignals" class="control-btn control-warning">
                      🚨 Override Signals
                    </button>
                    <button @click="optimizeTimings" class="control-btn control-success">
                      ⚡ Optimize Timings
                    </button>
                    <button @click="emergencyMode" class="control-btn control-danger">
                      🚑 Emergency Mode
                    </button>
                  </div>
                </div>

                <!-- Speed Monitoring -->
                <div style="border: 1px solid #e5e7eb; border-radius: 8px; padding: 1rem;">
                  <h4 style="margin: 0 0 1rem 0; color: #374151;">📡 Speed Monitoring</h4>
                  <div style="display: grid; gap: 0.5rem;">
                    <button @click="viewSpeedCameras" class="control-btn control-info">
                      📹 View Cameras
                    </button>
                    <button @click="speedViolations" class="control-btn control-warning">
                      ⚡ Speed Violations
                    </button>
                    <button @click="calibrateCameras" class="control-btn control-secondary">
                      🔧 Calibrate Cameras
                    </button>
                  </div>
                </div>

                <!-- Route Management -->
                <div style="border: 1px solid #e5e7eb; border-radius: 8px; padding: 1rem;">
                  <h4 style="margin: 0 0 1rem 0; color: #374151;">🗺️ Route Management</h4>
                  <div style="display: grid; gap: 0.5rem;">
                    <button @click="suggestAlternates" class="control-btn control-primary">
                      🔀 Suggest Alternates
                    </button>
                    <button @click="blockRoute" class="control-btn control-danger">
                      🚧 Block Route
                    </button>
                    <button @click="clearBlockage" class="control-btn control-success">
                      ✅ Clear Blockage
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Live Traffic Map -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>🗺️ Live Traffic Map</h3>
              <div style="display: flex; gap: 1rem;">
                <select v-model="mapFilter" class="form-control" style="width: auto;">
                  <option value="all">All Areas</option>
                  <option value="central">Central Mumbai</option>
                  <option value="western">Western Mumbai</option>
                  <option value="eastern">Eastern Mumbai</option>
                  <option value="harbour">Harbour Line</option>
                </select>
                <button @click="centerMap" class="btn btn-info">🎯 Center Map</button>
              </div>
            </div>
            <div class="card-body">
              <!-- Mock Map Interface -->
              <div style="height: 400px; background: linear-gradient(135deg, #f0f9ff, #e0f2fe); border-radius: 8px; position: relative; overflow: hidden;">
                <!-- Map Grid -->
                <div style="position: absolute; inset: 0; background-image: linear-gradient(rgba(0,0,0,0.1) 1px, transparent 1px), linear-gradient(90deg, rgba(0,0,0,0.1) 1px, transparent 1px); background-size: 20px 20px;"></div>
                
                <!-- Traffic Zones -->
                <div 
                  v-for="zone in visibleTrafficZones" 
                  :key="zone.id"
                  :style="`position: absolute; left: ${zone.x}%; top: ${zone.y}%; width: ${zone.width}px; height: ${zone.height}px; background: ${zone.color}; border-radius: 6px; padding: 0.5rem; color: white; font-size: 0.8rem; font-weight: bold; cursor: pointer; display: flex; align-items: center; justify-content: center;`"
                  @click="selectZone(zone)"
                >
                  {{ zone.name }}
                  <div style="margin-left: 0.5rem;">{{ zone.status }}</div>
                </div>
                
                <!-- Officers on Map -->
                <div 
                  v-for="officer in deployedOfficers" 
                  :key="officer.id"
                  :style="`position: absolute; left: ${officer.x}%; top: ${officer.y}%; width: 30px; height: 30px; background: #3b82f6; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: white; font-size: 0.8rem; cursor: pointer; border: 2px solid white; box-shadow: 0 2px 4px rgba(0,0,0,0.2);`"
                  @click="contactOfficer(officer)"
                  :title="officer.name"
                >
                  👮
                </div>
                
                <!-- Incidents on Map -->
                <div 
                  v-for="incident in mapIncidents" 
                  :key="incident.id"
                  :style="`position: absolute; left: ${incident.x}%; top: ${incident.y}%; width: 25px; height: 25px; background: #dc2626; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: white; font-size: 0.8rem; cursor: pointer; animation: pulse 2s infinite;`"
                  @click="viewIncident(incident)"
                  :title="incident.type"
                >
                  🚨
                </div>
                
                <!-- Map Legend -->
                <div style="position: absolute; bottom: 10px; left: 10px; background: rgba(255,255,255,0.9); padding: 0.75rem; border-radius: 6px; font-size: 0.8rem;">
                  <div style="margin-bottom: 0.25rem;"><span style="display: inline-block; width: 12px; height: 12px; background: #16a34a; border-radius: 2px; margin-right: 0.5rem;"></span>Normal Flow</div>
                  <div style="margin-bottom: 0.25rem;"><span style="display: inline-block; width: 12px; height: 12px; background: #f59e0b; border-radius: 2px; margin-right: 0.5rem;"></span>Moderate</div>
                  <div style="margin-bottom: 0.25rem;"><span style="display: inline-block; width: 12px; height: 12px; background: #dc2626; border-radius: 2px; margin-right: 0.5rem;"></span>Heavy Traffic</div>
                  <div style="margin-bottom: 0.25rem;"><span style="display: inline-block; width: 12px; height: 12px; background: #3b82f6; border-radius: 50%; margin-right: 0.5rem;"></span>Officers</div>
                  <div><span style="display: inline-block; width: 12px; height: 12px; background: #dc2626; border-radius: 50%; margin-right: 0.5rem;"></span>Incidents</div>
                </div>
              </div>
            </div>
          </div>

          <!-- Recent Traffic Violations -->
          <div class="card">
            <div class="card-header">
              <h3>⚡ Recent Traffic Violations</h3>
              <div style="display: flex; gap: 1rem;">
                <select v-model="violationFilter" class="form-control" style="width: auto;">
                  <option value="all">All Violations</option>
                  <option value="speeding">Speeding</option>
                  <option value="signal_jump">Signal Jumping</option>
                  <option value="wrong_lane">Wrong Lane</option>
                  <option value="no_helmet">No Helmet</option>
                </select>
                <button @click="refreshViolations" class="btn btn-primary">🔄 Refresh</button>
              </div>
            </div>
            <div class="card-body">
              <div v-if="filteredViolations.length === 0" style="text-align: center; color: #6b7280; padding: 2rem;">
                <div style="font-size: 3rem; margin-bottom: 1rem;">⚡</div>
                <div>No recent violations found</div>
              </div>
              
              <div v-else style="display: grid; gap: 1rem;">
                <div 
                  v-for="violation in filteredViolations.slice(0, 5)" 
                  :key="violation.id"
                  style="border: 1px solid #e5e7eb; border-radius: 8px; padding: 1rem; background: #fafafa;"
                >
                  <div style="display: flex; justify-content: between; align-items: start;">
                    <div style="flex: 1;">
                      <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 0.5rem;">
                        <div style="font-weight: bold; color: #dc2626;">{{ violation.type }}</div>
                        <span style="padding: 0.25rem 0.5rem; background: #fee2e2; color: #dc2626; border-radius: 12px; font-size: 0.8rem;">
                          ₹{{ violation.fine }}
                        </span>
                        <span style="padding: 0.25rem 0.5rem; background: #f3f4f6; color: #6b7280; border-radius: 12px; font-size: 0.8rem;">
                          {{ violation.vehicleType }}
                        </span>
                      </div>
                      
                      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 0.5rem; font-size: 0.9rem; color: #6b7280;">
                        <div>🚗 {{ violation.vehicleNumber }}</div>
                        <div>📍 {{ violation.location }}</div>
                        <div>⏰ {{ formatTime(violation.timestamp) }}</div>
                        <div>📹 Camera {{ violation.cameraId }}</div>
                      </div>
                    </div>
                    
                    <div style="display: flex; gap: 0.5rem;">
                      <button @click="viewViolationDetails(violation)" class="btn btn-info btn-small">
                        👁️ View
                      </button>
                      <button @click="issueTicket(violation)" class="btn btn-warning btn-small">
                        🎫 Ticket
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Right Sidebar -->
        <div>
          <!-- Live Alerts -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>🚨 Live Alerts</h3>
              <button @click="clearAllAlerts" class="btn btn-danger btn-small">Clear All</button>
            </div>
            <div class="card-body">
              <div style="max-height: 300px; overflow-y: auto;">
                <div 
                  v-for="alert in liveAlerts" 
                  :key="alert.id"
                  :style="`padding: 0.75rem; margin-bottom: 0.5rem; border-radius: 6px; border-left: 4px solid ${alert.color}; background: ${alert.bg};`"
                >
                  <div style="font-weight: 500; margin-bottom: 0.25rem;">{{ alert.title }}</div>
                  <div style="font-size: 0.8rem; color: #6b7280; margin-bottom: 0.5rem;">{{ alert.message }}</div>
                  <div style="font-size: 0.7rem; color: #9ca3af;">{{ formatTime(alert.timestamp) }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- Signal Status -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>🚦 Signal Status</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; gap: 0.75rem;">
                <div 
                  v-for="signal in trafficSignals" 
                  :key="signal.id"
                  style="padding: 0.75rem; border: 1px solid #e5e7eb; border-radius: 6px; background: #f8fafc;"
                >
                  <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                    <div style="font-weight: 500;">{{ signal.name }}</div>
                    <div :style="`width: 12px; height: 12px; border-radius: 50%; background: ${signal.color};`"></div>
                  </div>
                  <div style="font-size: 0.8rem; color: #6b7280;">{{ signal.status }}</div>
                  <div style="font-size: 0.8rem; color: #6b7280;">Timer: {{ signal.timer }}s</div>
                </div>
              </div>
            </div>
          </div>

          <!-- Quick Actions -->
          <div class="card">
            <div class="card-header">
              <h3>⚡ Quick Actions</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; gap: 0.5rem;">
                <button @click="deployOfficer" class="action-btn">
                  👮 Deploy Officer
                </button>
                <button @click="reportIncident" class="action-btn">
                  📝 Report Incident
                </button>
                <button @click="broadcastMessage" class="action-btn">
                  📢 Broadcast Message
                </button>
                <button @click="requestBackup" class="action-btn">
                  🆘 Request Backup
                </button>
                <button @click="trafficAnalytics" class="action-btn">
                  📊 View Analytics
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
import { showToast, showConfirm, showInput } from '@/composables/interactionService'

export default {
  name: 'TrafficMonitoringView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const autoRefresh = ref(true)
    const mapFilter = ref('all')
    const violationFilter = ref('all')
    let refreshInterval = null
    
    const liveStats = ref({
      normalFlow: 42,
      moderateCongestion: 18,
      heavyTraffic: 7,
      avgSpeed: 35,
      incidents: 3,
      officersDeployed: 24
    })
    
    const liveAlerts = ref([
      {
        id: 1,
        title: 'Heavy Traffic Jam',
        message: 'Major congestion on Eastern Express Highway due to accident',
        timestamp: new Date(),
        color: '#dc2626',
        bg: '#fef2f2'
      },
      {
        id: 2,
        title: 'Signal Malfunction',
        message: 'Traffic signal at Bandra Junction not working properly',
        timestamp: new Date(Date.now() - 5 * 60 * 1000),
        color: '#f59e0b',
        bg: '#fffbeb'
      },
      {
        id: 3,
        title: 'Speed Camera Alert',
        message: 'Multiple speeding violations detected on Marine Drive',
        timestamp: new Date(Date.now() - 10 * 60 * 1000),
        color: '#3b82f6',
        bg: '#eff6ff'
      }
    ])
    
    const trafficZones = ref([
      { id: 1, name: 'Bandra', x: 20, y: 15, width: 80, height: 40, status: '🟢', color: '#16a34a' },
      { id: 2, name: 'Andheri', x: 60, y: 25, width: 70, height: 35, status: '🟡', color: '#f59e0b' },
      { id: 3, name: 'Dadar', x: 15, y: 55, width: 75, height: 38, status: '🔴', color: '#dc2626' },
      { id: 4, name: 'Powai', x: 70, y: 60, width: 65, height: 30, status: '🟢', color: '#16a34a' },
      { id: 5, name: 'Worli', x: 35, y: 70, width: 60, height: 25, status: '🟡', color: '#f59e0b' }
    ])
    
    const deployedOfficers = ref([
      { id: 1, name: 'Sgt. Sharma', x: 25, y: 30 },
      { id: 2, name: 'Sgt. Patel', x: 45, y: 45 },
      { id: 3, name: 'Sgt. Kumar', x: 65, y: 35 },
      { id: 4, name: 'Sgt. Singh', x: 80, y: 75 }
    ])
    
    const mapIncidents = ref([
      { id: 1, type: 'Accident', x: 30, y: 25 },
      { id: 2, type: 'Vehicle Breakdown', x: 55, y: 60 },
      { id: 3, type: 'Road Block', x: 75, y: 40 }
    ])
    
    const trafficSignals = ref([
      { id: 1, name: 'Bandra Junction', status: 'Normal Operation', timer: 45, color: '#16a34a' },
      { id: 2, name: 'Andheri Signal', status: 'Manual Override', timer: 0, color: '#f59e0b' },
      { id: 3, name: 'Dadar Circle', status: 'Emergency Mode', timer: 30, color: '#dc2626' },
      { id: 4, name: 'Powai Junction', status: 'Normal Operation', timer: 60, color: '#16a34a' }
    ])
    
    const violations = ref([
      {
        id: 1,
        type: 'Overspeeding',
        vehicleNumber: 'MH 01 AB 1234',
        vehicleType: 'Car',
        location: 'Marine Drive',
        timestamp: new Date(),
        fine: 2000,
        cameraId: 'CAM001'
      },
      {
        id: 2,
        type: 'Signal Jump',
        vehicleNumber: 'MH 02 CD 5678',
        vehicleType: 'Motorcycle',
        location: 'Bandra Junction',
        timestamp: new Date(Date.now() - 2 * 60 * 1000),
        fine: 1000,
        cameraId: 'CAM005'
      },
      {
        id: 3,
        type: 'Wrong Lane',
        vehicleNumber: 'MH 03 EF 9012',
        vehicleType: 'Truck',
        location: 'Eastern Express Highway',
        timestamp: new Date(Date.now() - 5 * 60 * 1000),
        fine: 1500,
        cameraId: 'CAM012'
      },
      {
        id: 4,
        type: 'No Helmet',
        vehicleNumber: 'MH 04 GH 3456',
        vehicleType: 'Motorcycle',
        location: 'SV Road',
        timestamp: new Date(Date.now() - 8 * 60 * 1000),
        fine: 500,
        cameraId: 'CAM008'
      }
    ])
    
    const visibleTrafficZones = computed(() => {
      if (mapFilter.value === 'all') return trafficZones.value
      // In real app, filter zones based on selected area
      return trafficZones.value
    })
    
    const filteredViolations = computed(() => {
      if (violationFilter.value === 'all') return violations.value
      return violations.value.filter(v => v.type.toLowerCase().includes(violationFilter.value.replace('_', ' ')))
    })
    
    const startAutoRefresh = () => {
      refreshInterval = setInterval(() => {
        if (autoRefresh.value) {
          refreshData()
        }
      }, 30000) // Refresh every 30 seconds
    }
    
    const stopAutoRefresh = () => {
      if (refreshInterval) {
        clearInterval(refreshInterval)
        refreshInterval = null
      }
    }
    
    onMounted(() => {
      startAutoRefresh()
    })
    
    onUnmounted(() => {
      stopAutoRefresh()
    })
    
    const goBack = () => {
      router.go(-1)
    }
    
    const refreshData = () => {
      // Simulate data refresh
      liveStats.value.normalFlow = Math.floor(Math.random() * 10) + 40
      liveStats.value.moderateCongestion = Math.floor(Math.random() * 10) + 15
      liveStats.value.heavyTraffic = Math.floor(Math.random() * 5) + 5
      liveStats.value.avgSpeed = Math.floor(Math.random() * 15) + 30
      showToast({ title: 'Traffic', message: 'Traffic data refreshed successfully!', type: 'success' })
    }
    
    const exportReport = () => {
      showToast({ title: 'Export', message: 'Exporting traffic monitoring report...', type: 'info' })
    }
    
    const toggleAutoRefresh = () => {
      autoRefresh.value = !autoRefresh.value
      if (autoRefresh.value) {
        startAutoRefresh()
      } else {
        stopAutoRefresh()
      }
    }
    
    const overrideSignals = () => {
      showToast({ title: 'Signals', message: 'Opening signal override controls...', type: 'info' })
    }
    
    const optimizeTimings = () => {
      showToast({ title: 'Optimize', message: 'Optimizing signal timings based on current traffic...', type: 'info' })
    }
    
    const emergencyMode = () => {
      showToast({ title: 'Emergency', message: 'Activating emergency signal mode...', type: 'warning' })
    }
    
    const viewSpeedCameras = () => {
      showToast({ title: 'Cameras', message: 'Opening speed camera monitoring dashboard...', type: 'info' })
    }
    
    const speedViolations = () => {
      showToast({ title: 'Violations', message: 'Viewing speed violation reports...', type: 'info' })
    }
    
    const calibrateCameras = () => {
      showToast({ title: 'Calibration', message: 'Opening camera calibration interface...', type: 'info' })
    }
    
    const suggestAlternates = () => {
      showToast({ title: 'Routes', message: 'Generating alternative route suggestions...', type: 'info' })
    }
    
    const blockRoute = () => {
      showToast({ title: 'Route', message: 'Setting up route blockage...', type: 'warning' })
    }
    
    const clearBlockage = () => {
      showToast({ title: 'Route', message: 'Clearing route blockage...', type: 'success' })
    }
    
    const centerMap = () => {
      showToast({ title: 'Map', message: 'Centering map to current location...', type: 'info' })
    }
    
    const selectZone = (zone) => {
      showToast({ title: 'Zone', message: `Selected traffic zone: ${zone.name}`, type: 'info' })
    }
    
    const contactOfficer = (officer) => {
      showToast({ title: 'Contact', message: `Contacting ${officer.name}...`, type: 'info' })
    }
    
    const viewIncident = (incident) => {
      showToast({ title: 'Incident', message: `Viewing incident: ${incident.type}`, type: 'info' })
    }
    
    const refreshViolations = () => {
      showToast({ title: 'Violations', message: 'Refreshing violation data...', type: 'success' })
    }
    
    const viewViolationDetails = (violation) => {
      showToast({ title: 'Violation', message: `Viewing details for violation: ${violation.type} - ${violation.vehicleNumber}`, type: 'info' })
    }
    
    const issueTicket = (violation) => {
      showToast({ title: 'Ticket', message: `Issuing ticket for: ${violation.type} - ${violation.vehicleNumber}`, type: 'info' })
    }
    
    const clearAllAlerts = async () => {
      const ok = await showConfirm({ title: 'Clear Alerts', message: 'Clear all alerts?' })
      if (ok) liveAlerts.value = []
    }
    
    const deployOfficer = () => {
      showToast({ title: 'Deploy', message: 'Opening officer deployment interface...', type: 'info' })
    }
    
    const reportIncident = () => {
      showToast({ title: 'Report', message: 'Opening incident reporting form...', type: 'info' })
    }
    
    const broadcastMessage = () => {
      showToast({ title: 'Broadcast', message: 'Opening broadcast message interface...', type: 'info' })
    }
    
    const requestBackup = () => {
      showToast({ title: 'Backup', message: 'Sending backup request to command center...', type: 'info' })
    }
    
    const trafficAnalytics = () => {
      showToast({ title: 'Analytics', message: 'Opening traffic analytics dashboard...', type: 'info' })
    }
    
    const formatTime = (date) => {
      return date.toLocaleTimeString('en-IN')
    }
    
    return {
      authStore,
      autoRefresh,
      mapFilter,
      violationFilter,
      liveStats,
      liveAlerts,
      trafficSignals,
      violations,
      visibleTrafficZones,
      deployedOfficers,
      mapIncidents,
      filteredViolations,
      goBack,
      refreshData,
      exportReport,
      toggleAutoRefresh,
      overrideSignals,
      optimizeTimings,
      emergencyMode,
      viewSpeedCameras,
      speedViolations,
      calibrateCameras,
      suggestAlternates,
      blockRoute,
      clearBlockage,
      centerMap,
      selectZone,
      contactOfficer,
      viewIncident,
      refreshViolations,
      viewViolationDetails,
      issueTicket,
      clearAllAlerts,
      deployOfficer,
      reportIncident,
      broadcastMessage,
      requestBackup,
      trafficAnalytics,
      formatTime
    }
  }
}
</script>

<style scoped>
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.stat-card {
  padding: 1.5rem;
  border-radius: 12px;
  color: white;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.stat-icon {
  font-size: 2rem;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 1.8rem;
  font-weight: bold;
  margin-bottom: 0.25rem;
}

.stat-label {
  font-size: 0.9rem;
  opacity: 0.9;
}

.control-btn {
  width: 100%;
  padding: 0.75rem;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.control-success {
  background: #dcfce7;
  color: #166534;
}

.control-warning {
  background: #fef3c7;
  color: #92400e;
}

.control-danger {
  background: #fecaca;
  color: #dc2626;
}

.control-info {
  background: #dbeafe;
  color: #2563eb;
}

.control-primary {
  background: #eff6ff;
  color: #1d4ed8;
}

.control-secondary {
  background: #f3f4f6;
  color: #374151;
}

.control-btn:hover {
  opacity: 0.8;
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
  text-align: left;
}

.action-btn:hover {
  background: #e5e7eb;
}

.btn-small {
  padding: 0.5rem 0.75rem;
  font-size: 0.8rem;
}
</style>