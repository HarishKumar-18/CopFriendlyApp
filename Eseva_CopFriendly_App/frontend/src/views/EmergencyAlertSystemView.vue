<template>
  <div>
    <!-- Navigation -->
    <nav style="background: #dc2626; color: white; padding: 1rem; display: flex; justify-content: space-between; align-items: center;">
      <div style="display: flex; align-items: center; gap: 1rem;">
        <button @click="goBack" style="background: rgba(255,255,255,0.2); border: none; color: white; padding: 0.5rem; border-radius: 4px; cursor: pointer;">
          ← Back
        </button>
        <div style="font-size: 1.5rem; font-weight: bold;">
          🚨 Emergency Alert & Response Center
        </div>
      </div>
      <div style="font-size: 0.9rem;">
        {{ authStore.user?.role || 'Emergency Officer' }}: {{ authStore.user?.name || 'Officer' }}
      </div>
    </nav>

    <div class="dashboard">
      <!-- Emergency Status Dashboard -->
      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-bottom: 2rem;">
        <div class="stat-card" style="background: linear-gradient(135deg, #dc2626, #991b1b); animation: pulse 2s infinite;">
          <div class="stat-icon">🚨</div>
          <div class="stat-content">
            <div class="stat-number">{{ alertStats.activeEmergencies }}</div>
            <div class="stat-label">Active Emergencies</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #f59e0b, #d97706);">
          <div class="stat-icon">⏱️</div>
          <div class="stat-content">
            <div class="stat-number">{{ alertStats.avgResponseTime }}</div>
            <div class="stat-label">Avg Response Time (min)</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #3b82f6, #1e40af);">
          <div class="stat-icon">🚑</div>
          <div class="stat-content">
            <div class="stat-number">{{ alertStats.unitsDeployed }}</div>
            <div class="stat-label">Response Units Deployed</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #16a34a, #15803d);">
          <div class="stat-icon">✅</div>
          <div class="stat-content">
            <div class="stat-number">{{ alertStats.resolvedToday }}</div>
            <div class="stat-label">Resolved Today</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #7c3aed, #5b21b6);">
          <div class="stat-icon">📻</div>
          <div class="stat-content">
            <div class="stat-number">{{ alertStats.broadcastsSent }}</div>
            <div class="stat-label">Alerts Broadcast</div>
          </div>
        </div>
      </div>

      <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 2rem;">
        <!-- Main Content -->
        <div>
          <!-- Emergency Alert Panel -->
          <div class="card" style="margin-bottom: 2rem; border: 2px solid #dc2626;">
            <div class="card-header" style="background: linear-gradient(135deg, #dc2626, #991b1b); color: white;">
              <h3>🚨 Emergency Alert Control Panel</h3>
              <div style="display: flex; gap: 1rem;">
                <button @click="testAlertSystem" class="btn" style="background: rgba(255,255,255,0.2); color: white; border: 1px solid rgba(255,255,255,0.3);">
                  🔍 Test System
                </button>
                <button @click="viewAlertHistory" class="btn" style="background: rgba(255,255,255,0.2); color: white; border: 1px solid rgba(255,255,255,0.3);">
                  📜 Alert History
                </button>
              </div>
            </div>
            <div class="card-body">
              <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem;">
                <!-- Create New Alert -->
                <div>
                  <h4 style="color: #dc2626; margin-bottom: 1rem;">🆘 Create Emergency Alert</h4>
                  
                  <div style="display: grid; gap: 1rem;">
                    <div>
                      <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Alert Type</label>
                      <select v-model="newAlert.type" class="form-control">
                        <option value="">Select Emergency Type</option>
                        <option value="natural_disaster">🌪️ Natural Disaster</option>
                        <option value="terrorist_threat">💣 Terrorist Threat</option>
                        <option value="fire_emergency">🔥 Fire Emergency</option>
                        <option value="medical_emergency">🏥 Medical Emergency</option>
                        <option value="traffic_accident">🚗 Major Traffic Accident</option>
                        <option value="riot_situation">👥 Riot/Civil Unrest</option>
                        <option value="chemical_hazard">☣️ Chemical Hazard</option>
                        <option value="missing_person">👤 Missing Person</option>
                        <option value="other">⚠️ Other Emergency</option>
                      </select>
                    </div>
                    
                    <div>
                      <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Severity Level</label>
                      <select v-model="newAlert.severity" class="form-control">
                        <option value="low">🟡 Low - Information Only</option>
                        <option value="medium">🟠 Medium - Advisory</option>
                        <option value="high">🔴 High - Warning</option>
                        <option value="critical">🚨 Critical - Immediate Action</option>
                      </select>
                    </div>
                    
                    <div>
                      <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Location</label>
                      <input v-model="newAlert.location" type="text" class="form-control" placeholder="Specific location or area">
                    </div>
                    
                    <div>
                      <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Alert Message</label>
                      <textarea v-model="newAlert.message" class="form-control" rows="3" placeholder="Detailed emergency message"></textarea>
                    </div>
                    
                    <div>
                      <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Broadcast To</label>
                      <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 0.5rem;">
                        <label style="display: flex; align-items: center; gap: 0.5rem;">
                          <input v-model="newAlert.recipients" type="checkbox" value="police_units">
                          <span>👮 Police Units</span>
                        </label>
                        <label style="display: flex; align-items: center; gap: 0.5rem;">
                          <input v-model="newAlert.recipients" type="checkbox" value="medical_teams">
                          <span>🚑 Medical Teams</span>
                        </label>
                        <label style="display: flex; align-items: center; gap: 0.5rem;">
                          <input v-model="newAlert.recipients" type="checkbox" value="fire_department">
                          <span>🚒 Fire Department</span>
                        </label>
                        <label style="display: flex; align-items: center; gap: 0.5rem;">
                          <input v-model="newAlert.recipients" type="checkbox" value="public_broadcast">
                          <span>📢 Public Broadcast</span>
                        </label>
                      </div>
                    </div>
                    
                    <div style="display: flex; gap: 1rem; margin-top: 1rem;">
                      <button @click="sendEmergencyAlert" class="btn btn-danger" :disabled="!isAlertValid" style="flex: 1;">
                        🚨 SEND EMERGENCY ALERT
                      </button>
                      <button @click="clearAlertForm" class="btn btn-secondary">
                        🔄 Clear Form
                      </button>
                    </div>
                  </div>
                </div>

                <!-- Quick Alert Buttons -->
                <div>
                  <h4 style="color: #dc2626; margin-bottom: 1rem;">⚡ Quick Alert Templates</h4>
                  
                  <div style="display: grid; gap: 0.75rem;">
                    <button 
                      v-for="template in quickAlertTemplates" 
                      :key="template.id"
                      @click="useQuickAlert(template)"
                      :class="`quick-alert-btn ${template.severity}`"
                    >
                      <div style="font-size: 1.2rem; margin-bottom: 0.25rem;">{{ template.icon }}</div>
                      <div style="font-weight: bold; margin-bottom: 0.25rem;">{{ template.title }}</div>
                      <div style="font-size: 0.8rem;">{{ template.description }}</div>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Active Emergencies -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>🚨 Active Emergency Alerts</h3>
              <div style="display: flex; gap: 1rem;">
                <select v-model="emergencyFilter" class="form-control" style="width: auto;">
                  <option value="all">All Emergencies</option>
                  <option value="critical">Critical Only</option>
                  <option value="high">High Priority</option>
                  <option value="recent">Last 24 Hours</option>
                </select>
                <button @click="refreshEmergencies" class="btn btn-primary">🔄 Refresh</button>
              </div>
            </div>
            <div class="card-body">
              <div v-if="filteredEmergencies.length === 0" style="text-align: center; color: #6b7280; padding: 2rem;">
                <div style="font-size: 3rem; margin-bottom: 1rem;">✅</div>
                <div>No active emergencies</div>
              </div>
              
              <div v-else>
                <div 
                  v-for="emergency in filteredEmergencies" 
                  :key="emergency.id"
                  :style="`border: 2px solid ${getSeverityColor(emergency.severity)}; border-radius: 8px; padding: 1.5rem; margin-bottom: 1rem; background: ${getSeverityBg(emergency.severity)}; ${emergency.severity === 'critical' ? 'animation: pulse 2s infinite;' : ''}`"
                >
                  <div style="display: flex; justify-content: space-between; align-items: start;">
                    <div style="flex: 1;">
                      <!-- Emergency Header -->
                      <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem;">
                        <div style="font-size: 1.5rem;">{{ getEmergencyIcon(emergency.type) }}</div>
                        <div style="font-size: 1.2rem; font-weight: bold;">{{ emergency.title }}</div>
                        <span :style="`padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.8rem; font-weight: 500; background: ${getSeverityColor(emergency.severity)}; color: white;`">
                          {{ emergency.severity.toUpperCase() }}
                        </span>
                        <span :style="`padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.8rem; font-weight: 500; background: ${getStatusColor(emergency.status)}; color: white;`">
                          {{ emergency.status.replace('_', ' ').toUpperCase() }}
                        </span>
                      </div>

                      <!-- Emergency Details -->
                      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-bottom: 1rem;">
                        <div>
                          <div style="font-size: 0.9rem; color: #6b7280; margin-bottom: 0.25rem;">Location</div>
                          <div>📍 {{ emergency.location }}</div>
                        </div>
                        <div>
                          <div style="font-size: 0.9rem; color: #6b7280; margin-bottom: 0.25rem;">Reported Time</div>
                          <div>⏰ {{ formatDateTime(emergency.reportedTime) }}</div>
                        </div>
                        <div>
                          <div style="font-size: 0.9rem; color: #6b7280; margin-bottom: 0.25rem;">Response Time</div>
                          <div>⏱️ {{ emergency.responseTime || 'Pending' }}</div>
                        </div>
                        <div>
                          <div style="font-size: 0.9rem; color: #6b7280; margin-bottom: 0.25rem;">Units Deployed</div>
                          <div>🚑 {{ emergency.unitsDeployed }}</div>
                        </div>
                      </div>

                      <!-- Message -->
                      <div style="background: rgba(255,255,255,0.7); padding: 1rem; border-radius: 6px; margin-bottom: 1rem;">
                        <div>{{ emergency.message }}</div>
                      </div>

                      <!-- Response Teams -->
                      <div v-if="emergency.responseTeams.length" style="margin-bottom: 1rem;">
                        <div style="font-size: 0.9rem; color: #6b7280; margin-bottom: 0.5rem;">Response Teams</div>
                        <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                          <span 
                            v-for="team in emergency.responseTeams" 
                            :key="team"
                            style="padding: 0.25rem 0.5rem; background: #3b82f6; color: white; border-radius: 4px; font-size: 0.8rem;"
                          >
                            {{ team }}
                          </span>
                        </div>
                      </div>

                      <!-- Timestamps -->
                      <div style="font-size: 0.8rem; color: #9ca3af;">
                        Reported: {{ formatDateTime(emergency.reportedTime) }}
                        <span v-if="emergency.resolvedTime">
                          | Resolved: {{ formatDateTime(emergency.resolvedTime) }}
                        </span>
                      </div>
                    </div>

                    <!-- Action Buttons -->
                    <div style="display: flex; flex-direction: column; gap: 0.5rem; min-width: 150px;">
                      <button @click="updateEmergencyStatus(emergency)" class="btn btn-primary btn-small">
                        🔄 Update Status
                      </button>
                      <button @click="deployAdditionalUnits(emergency)" class="btn btn-warning btn-small">
                        🚑 Deploy Units
                      </button>
                      <button @click="broadcastUpdate(emergency)" class="btn btn-info btn-small">
                        📢 Broadcast Update
                      </button>
                      <button @click="escalateEmergency(emergency)" class="btn btn-danger btn-small">
                        ⬆️ Escalate
                      </button>
                      <button 
                        v-if="emergency.status === 'active'" 
                        @click="resolveEmergency(emergency)" 
                        class="btn btn-success btn-small"
                      >
                        ✅ Mark Resolved
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
          <!-- Emergency Contacts -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>📞 Emergency Contacts</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; gap: 0.75rem;">
                <div 
                  v-for="contact in emergencyContacts" 
                  :key="contact.id"
                  style="padding: 0.75rem; border: 1px solid #e5e7eb; border-radius: 6px; background: #f8fafc;"
                >
                  <div style="display: flex; justify-content: space-between; align-items: center;">
                    <div>
                      <div style="font-weight: 500;">{{ contact.name }}</div>
                      <div style="font-size: 0.8rem; color: #6b7280;">{{ contact.department }}</div>
                      <div style="font-size: 0.8rem; color: #6b7280;">📞 {{ contact.phone }}</div>
                    </div>
                    <button @click="callContact(contact)" class="btn btn-success btn-small">
                      📞 Call
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Alert Statistics -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>📊 Alert Statistics</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; gap: 1rem;">
                <div style="text-align: center;">
                  <div style="font-size: 2rem; font-weight: bold; color: #dc2626;">{{ alertStats.alertsSentToday }}</div>
                  <div style="font-size: 0.9rem; color: #6b7280;">Alerts Sent Today</div>
                </div>
                
                <div style="border-top: 1px solid #e5e7eb; padding-top: 1rem;">
                  <div style="display: grid; gap: 0.5rem;">
                    <div style="display: flex; justify-content: space-between;">
                      <span>Critical:</span>
                      <span style="color: #dc2626; font-weight: bold;">{{ alertStats.criticalAlerts }}</span>
                    </div>
                    <div style="display: flex; justify-content: space-between;">
                      <span>High:</span>
                      <span style="color: #f59e0b; font-weight: bold;">{{ alertStats.highAlerts }}</span>
                    </div>
                    <div style="display: flex; justify-content: space-between;">
                      <span>Medium:</span>
                      <span style="color: #3b82f6; font-weight: bold;">{{ alertStats.mediumAlerts }}</span>
                    </div>
                    <div style="display: flex; justify-content: space-between;">
                      <span>Low:</span>
                      <span style="color: #16a34a; font-weight: bold;">{{ alertStats.lowAlerts }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- System Status -->
          <div class="card">
            <div class="card-header">
              <h3>🔧 System Status</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; gap: 0.75rem;">
                <div 
                  v-for="system in systemStatus" 
                  :key="system.name"
                  style="display: flex; justify-content: space-between; align-items: center; padding: 0.5rem; border-radius: 4px; background: #f8fafc;"
                >
                  <span>{{ system.name }}</span>
                  <div :style="`width: 12px; height: 12px; border-radius: 50%; background: ${system.status === 'online' ? '#16a34a' : '#dc2626'};`"></div>
                </div>
              </div>
              
              <button @click="runSystemCheck" class="btn btn-primary" style="width: 100%; margin-top: 1rem;">
                🔍 Run System Check
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { showToast, showConfirm } from '@/composables/interactionService'

export default {
  name: 'EmergencyAlertSystemView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const emergencyFilter = ref('all')
    
    const newAlert = ref({
      type: '',
      severity: 'medium',
      location: '',
      message: '',
      recipients: []
    })
    
    const alertStats = ref({
      activeEmergencies: 3,
      avgResponseTime: 8,
      unitsDeployed: 12,
      resolvedToday: 7,
      broadcastsSent: 15,
      alertsSentToday: 23,
      criticalAlerts: 2,
      highAlerts: 5,
      mediumAlerts: 8,
      lowAlerts: 8
    })
    
    const quickAlertTemplates = ref([
      {
        id: 1,
        icon: '🌪️',
        title: 'Natural Disaster',
        description: 'Earthquake, flood, cyclone alert',
        severity: 'critical'
      },
      {
        id: 2,
        icon: '🔥',
        title: 'Fire Emergency',
        description: 'Building fire, forest fire',
        severity: 'high'
      },
      {
        id: 3,
        icon: '🚗',
        title: 'Major Accident',
        description: 'Multi-vehicle collision',
        severity: 'high'
      },
      {
        id: 4,
        icon: '👥',
        title: 'Civil Unrest',
        description: 'Riot, protest situation',
        severity: 'medium'
      }
    ])
    
    const emergencies = ref([
      {
        id: 1,
        title: 'Building Fire at Commercial Complex',
        type: 'fire_emergency',
        severity: 'critical',
        status: 'active',
        location: 'Phoenix Mall, Lower Parel',
        message: 'Major fire reported on 3rd floor of Phoenix Mall. Multiple fire units and ambulances dispatched. Evacuation in progress.',
        reportedTime: new Date(Date.now() - 45 * 60 * 1000),
        responseTime: '6 minutes',
        unitsDeployed: 8,
        responseTeams: ['Fire Brigade Unit 1', 'Fire Brigade Unit 2', 'Ambulance 101', 'Police Team Alpha'],
        resolvedTime: null
      },
      {
        id: 2,
        title: 'Multi-Vehicle Accident',
        type: 'traffic_accident',
        severity: 'high',
        status: 'responding',
        location: 'Eastern Express Highway, Ghatkopar',
        message: 'Chain collision involving 5 vehicles. Multiple injuries reported. Traffic blocked on both directions.',
        reportedTime: new Date(Date.now() - 20 * 60 * 1000),
        responseTime: '4 minutes',
        unitsDeployed: 6,
        responseTeams: ['Traffic Control Unit', 'Ambulance 202', 'Police Patrol 15'],
        resolvedTime: null
      },
      {
        id: 3,
        title: 'Missing Child Alert',
        type: 'missing_person',
        severity: 'medium',
        status: 'investigating',
        location: 'Juhu Beach Area',
        message: '8-year-old child reported missing from Juhu Beach. Last seen wearing blue t-shirt and khaki shorts.',
        reportedTime: new Date(Date.now() - 90 * 60 * 1000),
        responseTime: '8 minutes',
        unitsDeployed: 4,
        responseTeams: ['Police Search Team', 'Beach Patrol Unit'],
        resolvedTime: null
      }
    ])
    
    const emergencyContacts = ref([
      { id: 1, name: 'Fire Chief Kumar', department: 'Fire Department', phone: '+91 98765 10001' },
      { id: 2, name: 'Medical Director Shah', department: 'Emergency Medical', phone: '+91 98765 10002' },
      { id: 3, name: 'Police Commissioner', department: 'Mumbai Police', phone: '+91 98765 10003' },
      { id: 4, name: 'Disaster Management', department: 'NDRF Coordinator', phone: '+91 98765 10004' }
    ])
    
    const systemStatus = ref([
      { name: 'Alert Broadcasting System', status: 'online' },
      { name: 'Emergency Communication', status: 'online' },
      { name: 'GPS Tracking System', status: 'online' },
      { name: 'Public Address System', status: 'offline' },
      { name: 'SMS Gateway', status: 'online' },
      { name: 'Radio Communication', status: 'online' }
    ])
    
    const filteredEmergencies = computed(() => {
      if (emergencyFilter.value === 'all') return emergencies.value
      if (emergencyFilter.value === 'critical') return emergencies.value.filter(e => e.severity === 'critical')
      if (emergencyFilter.value === 'high') return emergencies.value.filter(e => e.severity === 'high')
      if (emergencyFilter.value === 'recent') {
        const oneDayAgo = Date.now() - 24 * 60 * 60 * 1000
        return emergencies.value.filter(e => e.reportedTime.getTime() > oneDayAgo)
      }
      return emergencies.value
    })
    
    const isAlertValid = computed(() => {
      return newAlert.value.type && 
             newAlert.value.location && 
             newAlert.value.message && 
             newAlert.value.recipients.length > 0
    })
    
    const goBack = () => {
      router.go(-1)
    }
    
    const sendEmergencyAlert = () => {
      if (isAlertValid.value) {
  showToast({ message: `EMERGENCY ALERT SENT! Type: ${newAlert.value.type} Severity: ${newAlert.value.severity}`, type: 'success' })
        clearAlertForm()
        alertStats.value.alertsSentToday++
      }
    }
    
    const clearAlertForm = () => {
      newAlert.value = {
        type: '',
        severity: 'medium',
        location: '',
        message: '',
        recipients: []
      }
    }
    
    const useQuickAlert = (template) => {
  showToast({ message: `Using quick alert template: ${template.title}`, type: 'info' })
    }
    
    const testAlertSystem = () => {
  showToast({ message: 'Running system test — check console for details', type: 'info' })
    }
    
    const viewAlertHistory = () => {
  showToast({ message: 'Opening alert history dashboard...', type: 'info' })
    }
    
    const refreshEmergencies = () => {
  showToast({ message: 'Refreshing emergency data...', type: 'info' })
    }
    
    const updateEmergencyStatus = (emergency) => {
  showToast({ message: `Updating status for: ${emergency.title}`, type: 'info' })
    }
    
    const deployAdditionalUnits = (emergency) => {
  showToast({ message: `Deploying additional response units for: ${emergency.title}`, type: 'info' })
    }
    
    const broadcastUpdate = (emergency) => {
  showToast({ message: `Broadcasting update for: ${emergency.title}`, type: 'info' })
    }
    
    const escalateEmergency = (emergency) => {
      showConfirm({ title: 'Escalate Emergency', message: `Escalate emergency: ${emergency.title}?` })
        .then(accepted => {
          if (!accepted) return
          emergency.severity = 'critical'
          showToast({ message: 'Emergency escalated to critical level!', type: 'warning' })
        })
    }
    
    const resolveEmergency = (emergency) => {
      showConfirm({ title: 'Resolve Emergency', message: `Mark emergency as resolved: ${emergency.title}?` })
        .then(accepted => {
          if (!accepted) return
          emergency.status = 'resolved'
          emergency.resolvedTime = new Date()
          alertStats.value.resolvedToday++
          showToast({ message: 'Emergency marked as resolved!', type: 'success' })
        })
    }
    
    const callContact = (contact) => {
      showToast({ message: `Calling ${contact.name} at ${contact.phone}...`, type: 'info' })
    }
    
    const runSystemCheck = () => {
      showToast({ message: 'Running comprehensive system check — check console for details', type: 'info' })
    }
    
    const formatDateTime = (date) => {
      return date.toLocaleString('en-IN')
    }
    
    const getSeverityColor = (severity) => {
      const colors = {
        'critical': '#dc2626',
        'high': '#f59e0b',
        'medium': '#3b82f6',
        'low': '#16a34a'
      }
      return colors[severity] || '#6b7280'
    }
    
    const getSeverityBg = (severity) => {
      const backgrounds = {
        'critical': '#fef2f2',
        'high': '#fefce8',
        'medium': '#eff6ff',
        'low': '#f0fdf4'
      }
      return backgrounds[severity] || '#f9fafb'
    }
    
    const getStatusColor = (status) => {
      const colors = {
        'active': '#dc2626',
        'responding': '#f59e0b',
        'investigating': '#3b82f6',
        'resolved': '#16a34a'
      }
      return colors[status] || '#6b7280'
    }
    
    const getEmergencyIcon = (type) => {
      const icons = {
        'fire_emergency': '🔥',
        'traffic_accident': '🚗',
        'missing_person': '👤',
        'natural_disaster': '🌪️',
        'medical_emergency': '🏥',
        'terrorist_threat': '💣',
        'riot_situation': '👥',
        'chemical_hazard': '☣️'
      }
      return icons[type] || '🚨'
    }
    
    return {
      authStore,
      emergencyFilter,
      newAlert,
      alertStats,
      quickAlertTemplates,
      emergencies,
      emergencyContacts,
      systemStatus,
      filteredEmergencies,
      isAlertValid,
      goBack,
      sendEmergencyAlert,
      clearAlertForm,
      useQuickAlert,
      testAlertSystem,
      viewAlertHistory,
      refreshEmergencies,
      updateEmergencyStatus,
      deployAdditionalUnits,
      broadcastUpdate,
      escalateEmergency,
      resolveEmergency,
      callContact,
      runSystemCheck,
      formatDateTime,
      getSeverityColor,
      getSeverityBg,
      getStatusColor,
      getEmergencyIcon
    }
  }
}
</script>

<style scoped>
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
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

.quick-alert-btn {
  width: 100%;
  padding: 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
}

.quick-alert-btn.critical {
  background: #fef2f2;
  color: #dc2626;
  border: 2px solid #fecaca;
}

.quick-alert-btn.high {
  background: #fefce8;
  color: #f59e0b;
  border: 2px solid #fef3c7;
}

.quick-alert-btn.medium {
  background: #eff6ff;
  color: #3b82f6;
  border: 2px solid #dbeafe;
}

.quick-alert-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.btn-small {
  padding: 0.5rem 0.75rem;
  font-size: 0.8rem;
}

.btn-danger {
  background: #fecaca;
  color: #dc2626;
  border: 1px solid #f87171;
}

.btn-success {
  background: #dcfce7;
  color: #166534;
  border: 1px solid #bbf7d0;
}

.btn-primary {
  background: #dbeafe;
  color: #2563eb;
  border: 1px solid #93c5fd;
}

.btn-warning {
  background: #fef3c7;
  color: #92400e;
  border: 1px solid #fcd34d;
}

.btn-info {
  background: #f0f9ff;
  color: #0369a1;
  border: 1px solid #7dd3fc;
}

.btn-secondary {
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>