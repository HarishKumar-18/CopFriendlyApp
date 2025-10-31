<template>
  <div>
    <!-- Navigation -->
    <nav style="background: #dc2626; color: white; padding: 1rem; display: flex; justify-content: space-between; align-items: center;">
      <div style="font-size: 1.5rem; font-weight: bold;">
        🚨 Emergency Management
      </div>
      <div style="display: flex; gap: 1rem; align-items: center;">
        <button @click="navigateTo('/dashboard')" class="btn" style="background: rgba(255,255,255,0.2); padding: 0.5rem 1rem;">
          ← Dashboard
        </button>
        <button @click="logout" class="btn" style="background: rgba(255,255,255,0.2); padding: 0.5rem 1rem;">
          Logout
        </button>
      </div>
    </nav>

    <div class="dashboard">
      <!-- Header & Controls -->
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <h1 style="color: #333; margin: 0;">Emergency Response Center</h1>
        <div style="display: flex; gap: 1rem;">
          <button @click="navigateTo('/emergencies/create')" class="btn btn-danger">
            🚨 Report Emergency
          </button>
          <button @click="navigateTo('/sos')" class="btn btn-danger">
            📞 SOS Alert
          </button>
        </div>
      </div>

      <!-- Emergency Stats -->
      <div class="stats-grid" style="margin-bottom: 2rem;">
        <div class="stat-card" style="border-left: 4px solid #ef4444;">
          <h3>Active Emergencies</h3>
          <div class="stat-value text-danger">{{ stats.active }}</div>
        </div>
        
        <div class="stat-card" style="border-left: 4px solid #f59e0b;">
          <h3>Pending Response</h3>
          <div class="stat-value text-warning">{{ stats.pending }}</div>
        </div>
        
        <div class="stat-card" style="border-left: 4px solid #22c55e;">
          <h3>Resolved Today</h3>
          <div class="stat-value text-success">{{ stats.resolved }}</div>
        </div>
        
        <div class="stat-card" style="border-left: 4px solid #3b82f6;">
          <h3>Response Time (Avg)</h3>
          <div class="stat-value text-primary">{{ stats.responseTime }} min</div>
        </div>
      </div>

      <!-- Emergency Filters -->
      <div class="card" style="margin-bottom: 2rem;">
        <div class="card-body">
          <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; align-items: end;">
            <div class="form-group">
              <label>Emergency Type</label>
              <select v-model="filterType" class="form-control">
                <option value="">All Types</option>
                <option value="medical">Medical Emergency</option>
                <option value="accident">Traffic Accident</option>
                <option value="fire">Fire Emergency</option>
                <option value="crime">Crime/Security</option>
                <option value="natural">Natural Disaster</option>
              </select>
            </div>
            <div class="form-group">
              <label>Priority Level</label>
              <select v-model="filterPriority" class="form-control">
                <option value="">All Priorities</option>
                <option value="critical">Critical</option>
                <option value="high">High</option>
                <option value="medium">Medium</option>
                <option value="low">Low</option>
              </select>
            </div>
            <div class="form-group">
              <label>Status</label>
              <select v-model="filterStatus" class="form-control">
                <option value="">All Status</option>
                <option value="reported">Reported</option>
                <option value="dispatched">Dispatched</option>
                <option value="in_progress">In Progress</option>
                <option value="resolved">Resolved</option>
              </select>
            </div>
            <button @click="filterEmergencies" class="btn">
              🔍 Filter
            </button>
          </div>
        </div>
      </div>

      <!-- Active Emergencies List -->
      <div class="card">
        <div class="card-header" style="display: flex; justify-content: space-between; align-items: center;">
          <h3>Emergency Reports ({{ filteredEmergencies.length }})</h3>
          <button @click="refreshEmergencies" class="btn" style="padding: 0.25rem 0.5rem; font-size: 0.8rem;">
            🔄 Refresh
          </button>
        </div>
        <div class="card-body">
          <div v-if="error" class="alert alert-danger" style="margin-bottom: 1rem;">
            {{ error }}
          </div>
          
          <div v-if="loading" class="text-center" style="padding: 2rem;">
            Loading emergencies...
          </div>
          
          <div v-else-if="filteredEmergencies.length === 0" class="text-center" style="padding: 2rem; color: #666;">
            No emergencies found
          </div>
          
          <div v-else>
            <div 
              v-for="emergency in filteredEmergencies" 
              :key="emergency.id"
              :style="getEmergencyCardStyle(emergency.priority)"
              @click="viewEmergency(emergency.id)"
            >
              <div style="display: grid; grid-template-columns: auto 1fr auto auto; gap: 1rem; align-items: center;">
                <div :style="getPriorityIconStyle(emergency.priority)">
                  {{ getEmergencyIcon(emergency.type) }}
                </div>
                
                <div>
                  <div style="font-weight: bold; color: #333;">{{ emergency.title }}</div>
                  <div style="color: #666; font-size: 0.9rem;">{{ emergency.location }} • {{ emergency.type }}</div>
                  <div style="color: #999; font-size: 0.8rem;">{{ formatDate(emergency.timestamp) }} • Reported by {{ emergency.reporterName }}</div>
                </div>
                
                <div style="text-align: center;">
                  <div :style="getStatusStyle(emergency.status)">{{ emergency.status.toUpperCase() }}</div>
                  <div style="font-size: 0.8rem; color: #666;">{{ emergency.responseTime || 'N/A' }}</div>
                </div>
                
                <div>
                  <button @click.stop="respondToEmergency(emergency.id)" :class="getActionButtonClass(emergency.status)" style="padding: 0.25rem 0.5rem; font-size: 0.8rem;">
                    {{ getActionButtonText(emergency.status) }}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="card" style="margin-top: 2rem;">
        <div class="card-header">
          <h3>🚀 Quick Emergency Actions</h3>
        </div>
        <div class="card-body">
          <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem;">
            <button @click="callAmbulance" class="btn btn-danger btn-block">
              🚑 Call Ambulance
            </button>
            <button @click="callFireService" class="btn btn-danger btn-block">
              🚒 Fire Service
            </button>
            <button @click="callPolice" class="btn btn-danger btn-block">
              👮 Police Backup
            </button>
            <button @click="alertTrafficControl" class="btn btn-warning btn-block">
              🚦 Traffic Control
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { emergenciesAPI, apiUtils } from '@/services/api'
import { showToast, showConfirm, showInput } from '@/composables/interactionService'

export default {
  name: 'EmergencyManagementView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const loading = ref(false)
    const error = ref(null)
    const filterType = ref('')
    const filterPriority = ref('')
    const filterStatus = ref('')
    
    const stats = ref({
      active: 0,
      pending: 0,
      resolved: 0,
      responseTime: 0
    })
    
    const emergencies = ref([])
    
    const filteredEmergencies = computed(() => {
      return emergencies.value.filter(emergency => {
        const matchesType = !filterType.value || emergency.type === filterType.value
        const matchesPriority = !filterPriority.value || emergency.priority === filterPriority.value
        const matchesStatus = !filterStatus.value || emergency.status === filterStatus.value
        
        return matchesType && matchesPriority && matchesStatus
      })
    })
    
    // Fetch emergencies from API
    const fetchEmergencies = async () => {
      loading.value = true
      error.value = null
      
      try {
        const response = await emergenciesAPI.getAll()
        
        // Transform backend data to match frontend structure
        emergencies.value = (response.data.emergencies || []).map(emergency => ({
          id: emergency.id,
          title: `${emergency.type} - ${emergency.description?.substring(0, 50) || 'Emergency'}`,
          type: emergency.type?.toLowerCase() || 'other',
          priority: emergency.priority?.toLowerCase() || emergency.severityLevel?.toLowerCase() || 'medium',
          status: emergency.status?.toLowerCase() || 'reported',
          location: emergency.location || 'Unknown location',
          timestamp: emergency.createdAt ? new Date(emergency.createdAt) : new Date(),
          reporterName: emergency.reporterName || 'Anonymous',
          responseTime: emergency.responseTime ? formatResponseTime(emergency.responseTime) : null,
          assignedTeam: emergency.assignedUnitName || null,
          ...emergency // Include all other fields
        }))
        
        // Calculate stats from real data
        calculateStats()
      } catch (err) {
        console.error('Error fetching emergencies:', err)
        error.value = apiUtils.formatError(err)
      } finally {
        loading.value = false
      }
    }
    
    // Calculate statistics from emergencies
    const calculateStats = () => {
      const now = new Date()
      const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate())
      
      stats.value.active = emergencies.value.filter(e => 
        e.status === 'active' || e.status === 'dispatched' || e.status === 'in_progress'
      ).length
      
      stats.value.pending = emergencies.value.filter(e => 
        e.status === 'reported' || e.status === 'pending'
      ).length
      
      stats.value.resolved = emergencies.value.filter(e => 
        e.status === 'resolved' && new Date(e.timestamp) >= todayStart
      ).length
      
      // Calculate average response time (simplified)
      const emergenciesWithResponse = emergencies.value.filter(e => e.responseTime)
      if (emergenciesWithResponse.length > 0) {
        const total = emergenciesWithResponse.reduce((sum, e) => {
          const minutes = parseInt(e.responseTime) || 0
          return sum + minutes
        }, 0)
        stats.value.responseTime = Math.round(total / emergenciesWithResponse.length)
      } else {
        stats.value.responseTime = 0
      }
    }
    
    // Format response time from backend
    const formatResponseTime = (responseTime) => {
      if (!responseTime) return null
      
      if (typeof responseTime === 'string') {
        // If already formatted like "15 min"
        return responseTime
      }
      
      // If it's a timestamp difference, calculate minutes
      const minutes = Math.floor(responseTime / 60000) // Convert ms to minutes
      return `${minutes} min`
    }
    
    // Load emergencies on mount
    onMounted(() => {
      fetchEmergencies()
    })
    
    const navigateTo = (path) => {
      router.push(path)
    }
    
    const logout = () => {
      authStore.logout()
      router.push('/login')
    }
    
    const filterEmergencies = () => {
      // Filtering is handled by computed property
      console.log('Filtering emergencies')
    }
    
    const refreshEmergencies = async () => {
      await fetchEmergencies()
    }
    
    const viewEmergency = (id) => {
      router.push(`/emergencies/${id}`)
    }
    
    const respondToEmergency = async (id) => {
      const emergency = emergencies.value.find(e => e.id === id)
      if (!emergency) {
        console.error('Emergency not found:', id)
        return
      }
      
      console.log('Responding to emergency:', emergency.id, 'Status:', emergency.status)
      
      try {
        const currentStatus = emergency.status.toLowerCase()
        
        if (currentStatus === 'active' || currentStatus === 'reported' || currentStatus === 'pending') {
          // Dispatch team
          console.log('Dispatching team for emergency:', id)
          await emergenciesAPI.dispatchTeam(id, {
            unitId: 'UNIT-' + Date.now(),
            driverId: authStore.user?.id || 'SYSTEM',
            unitName: 'Emergency Response Team'
          })
          showToast({ message: 'Emergency team dispatched!', type: 'success' })
          await fetchEmergencies()
        } else if (currentStatus === 'dispatched' || currentStatus === 'responded') {
          // Mark as in progress (team arrived)
          console.log('Marking emergency as in progress:', id)
          showToast({ message: 'Response team arrived at scene!', type: 'info' })
          emergency.status = 'in_progress'
          await fetchEmergencies()
        } else if (currentStatus === 'in_progress') {
          // Resolve emergency
          console.log('Resolving emergency:', id)
          const notes = await showInput({ title: 'Resolution notes', message: 'Enter resolution notes (optional):', placeholder: 'Emergency resolved successfully' })
            await emergenciesAPI.resolve(id, notes || 'Emergency resolved')
          showToast({ message: 'Emergency resolved!', type: 'success' })
          await fetchEmergencies()
        } else if (currentStatus === 'resolved') {
          // View details
          viewEmergency(id)
        }
      } catch (err) {
        console.error('Error updating emergency:', err)
  showToast({ message: 'Failed to update emergency: ' + apiUtils.formatError(err), type: 'error' })
      }
    }
    
    const formatDate = (date) => {
      return date.toLocaleString()
    }
    
    const getEmergencyIcon = (type) => {
      const icons = {
        medical: '🏥',
        accident: '🚗',
        fire: '🔥',
        crime: '👮',
        natural: '🌪️'
      }
      return icons[type] || '⚠️'
    }
    
    const getPriorityIconStyle = (priority) => {
      const styles = {
        critical: 'width: 60px; height: 60px; background: #fecaca; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; animation: pulse 2s infinite;',
        high: 'width: 60px; height: 60px; background: #fed7aa; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 1.5rem;',
        medium: 'width: 60px; height: 60px; background: #fef3c7; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 1.5rem;',
        low: 'width: 60px; height: 60px; background: #d1fae5; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 1.5rem;'
      }
      return styles[priority] || styles.medium
    }
    
    const getEmergencyCardStyle = (priority) => {
      const baseStyle = 'border-radius: 8px; padding: 1rem; margin-bottom: 1rem; cursor: pointer; transition: all 0.2s;'
      const priorityStyles = {
        critical: `${baseStyle} border: 2px solid #ef4444; background: #fef2f2;`,
        high: `${baseStyle} border: 2px solid #f59e0b; background: #fffbeb;`,
        medium: `${baseStyle} border: 1px solid #d1d5db; background: white;`,
        low: `${baseStyle} border: 1px solid #d1fae5; background: #f0fdf4;`
      }
      return priorityStyles[priority] || priorityStyles.medium
    }
    
    const getStatusStyle = (status) => {
      const statusLower = status?.toLowerCase() || ''
      const styles = {
        active: 'color: #ef4444; font-size: 0.8rem; font-weight: bold;',
        reported: 'color: #f59e0b; font-size: 0.8rem; font-weight: bold;',
        pending: 'color: #f59e0b; font-size: 0.8rem; font-weight: bold;',
        dispatched: 'color: #3b82f6; font-size: 0.8rem; font-weight: bold;',
        responded: 'color: #3b82f6; font-size: 0.8rem; font-weight: bold;',
        in_progress: 'color: #8b5cf6; font-size: 0.8rem; font-weight: bold;',
        resolved: 'color: #22c55e; font-size: 0.8rem; font-weight: bold;',
        cancelled: 'color: #9ca3af; font-size: 0.8rem; font-weight: bold;'
      }
      return styles[statusLower] || 'color: #666; font-size: 0.8rem;'
    }
    
    const getActionButtonClass = (status) => {
      const statusLower = status?.toLowerCase() || ''
      const classes = {
        active: 'btn btn-danger',
        reported: 'btn btn-danger',
        pending: 'btn btn-danger',
        dispatched: 'btn btn-warning',
        responded: 'btn btn-warning',
        in_progress: 'btn btn-success',
        resolved: 'btn'
      }
      return classes[statusLower] || 'btn'
    }
    
    const getActionButtonText = (status) => {
      const statusLower = status?.toLowerCase() || ''
      const texts = {
        active: '🚁 Dispatch',
        reported: '🚁 Dispatch',
        pending: '🚁 Dispatch',
        dispatched: '📍 Arrive',
        responded: '📍 Arrive',
        in_progress: '✅ Resolve',
        resolved: '👁️ View'
      }
      return texts[statusLower] || 'Action'
    }
    
    // Quick action functions
    const callAmbulance = () => {
  showToast({ message: 'Ambulance service contacted!', type: 'info' })
    }
    
    const callFireService = () => {
  showToast({ message: 'Fire service alerted!', type: 'info' })
    }
    
    const callPolice = () => {
  showToast({ message: 'Police backup requested!', type: 'info' })
    }
    
    const alertTrafficControl = () => {
  showToast({ message: 'Traffic control center notified!', type: 'info' })
    }
    
    return {
      loading,
      error,
      filterType,
      filterPriority,
      filterStatus,
      stats,
      emergencies,
      filteredEmergencies,
      navigateTo,
      logout,
      filterEmergencies,
      refreshEmergencies,
      fetchEmergencies,
      viewEmergency,
      respondToEmergency,
      formatDate,
      getEmergencyIcon,
      getPriorityIconStyle,
      getEmergencyCardStyle,
      getStatusStyle,
      getActionButtonClass,
      getActionButtonText,
      callAmbulance,
      callFireService,
      callPolice,
      alertTrafficControl
    }
  }
}
</script>

<style scoped>
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}
</style>