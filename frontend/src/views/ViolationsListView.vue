<template>
  <div>
    <!-- Navigation -->
    <nav style="background: #2563eb; color: white; padding: 1rem; display: flex; justify-content: space-between; align-items: center;">
      <div style="font-size: 1.5rem; font-weight: bold;">
        🚨 Traffic Violations
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
        <h1 style="color: #333; margin: 0;">All Traffic Violations</h1>
        <button @click="navigateTo('/violations/create')" class="btn btn-success">
          ➕ Register New Violation
        </button>
      </div>

      <!-- Search & Filters -->
      <div class="card" style="margin-bottom: 2rem;">
        <div class="card-body">
          <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; align-items: end;">
            <div class="form-group">
              <label>Search</label>
              <input v-model="searchQuery" type="text" class="form-control" placeholder="Vehicle number, driver name...">
            </div>
            <div class="form-group">
              <label>Status</label>
              <select v-model="filterStatus" class="form-control">
                <option value="">All Status</option>
                <option value="unpaid">Unpaid</option>
                <option value="paid">Paid</option>
                <option value="overdue">Overdue</option>
              </select>
            </div>
            <div class="form-group">
              <label>Violation Type</label>
              <select v-model="filterType" class="form-control">
                <option value="">All Types</option>
                <option value="speeding">Speeding</option>
                <option value="parking">Illegal Parking</option>
                <option value="signal">Signal Violation</option>
                <option value="helmet">No Helmet</option>
                <option value="seatbelt">No Seatbelt</option>
              </select>
            </div>
            <button @click="searchViolations" class="btn">
              🔍 Search
            </button>
          </div>
        </div>
      </div>

      <!-- Violations List -->
      <div class="card">
        <div class="card-header">
          <h3>Violations ({{ filteredViolations.length }})</h3>
        </div>
        <div class="card-body">
          <div v-if="loading" class="text-center" style="padding: 2rem;">
            Loading violations...
          </div>
          
          <div v-else-if="error" class="text-center" style="padding: 2rem; color: #ef4444;">
            {{ error }}
            <div style="margin-top: 1rem;">
              <button @click="fetchViolations" class="btn">
                🔄 Retry
              </button>
            </div>
          </div>
          
          <div v-else-if="filteredViolations.length === 0" class="text-center" style="padding: 2rem; color: #666;">
            No violations found
          </div>
          
          <div v-else>
            <div 
              v-for="violation in filteredViolations" 
              :key="violation.id"
              style="border: 1px solid #eee; border-radius: 8px; padding: 1rem; margin-bottom: 1rem; cursor: pointer;"
              @click="viewViolation(violation.id)"
            >
              <div style="display: grid; grid-template-columns: auto 1fr auto auto; gap: 1rem; align-items: center;">
                <div style="width: 60px; height: 60px; background: #f0f9ff; border-radius: 8px; display: flex; align-items: center; justify-content: center;">
                  🚗
                </div>
                
                <div>
                  <div style="font-weight: bold; color: #333;">{{ violation.vehicleNumber }}</div>
                  <div style="color: #666; font-size: 0.9rem;">{{ violation.violationType }} • {{ violation.location }}</div>
                  <div style="color: #999; font-size: 0.8rem;">{{ formatDate(violation.timestamp) }} • By {{ violation.officerName }}</div>
                </div>
                
                <div style="text-align: right;">
                  <div style="font-weight: bold; color: #333;">₹{{ violation.fineAmount }}</div>
                  <div :style="getStatusStyle(violation.paymentStatus || violation.status)">
                    {{ (violation.paymentStatus || violation.status || 'PENDING').toUpperCase() }}
                  </div>
                </div>
                
                <div>
                  <button @click.stop="issueTicket(violation.id)" class="btn" style="padding: 0.25rem 0.5rem; font-size: 0.8rem;">
                    📋 Ticket
                  </button>
                </div>
              </div>
            </div>
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
import { violationsAPI } from '@/services/api'
import { showToast, showConfirm, showInput } from '@/composables/interactionService'

export default {
  name: 'ViolationsListView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const loading = ref(false)
    const error = ref(null)
    const searchQuery = ref('')
    const filterStatus = ref('')
    const filterType = ref('')
    
    const violations = ref([])
    
    // Fetch violations from backend
    const fetchViolations = async () => {
      try {
        loading.value = true
        error.value = null
        const response = await violationsAPI.getAll()
        const payload = response?.data
        const list = Array.isArray(payload)
          ? payload
          : (payload && Array.isArray(payload.violations) ? payload.violations : [])

        violations.value = list.map(v => ({
          ...v,
          timestamp: new Date(v.createdAt || v.violationDateTime),
          officerName: v.issuerName || 'Unknown Officer'
        }))
        console.log('Loaded violations:', violations.value)
      } catch (err) {
        console.error('Failed to fetch violations:', err)
        // Try to surface backend error message if available
        const backendMsg = err?.response?.data?.error || err?.message
        error.value = backendMsg || 'Failed to load violations. Please make sure you are logged in.'
      } finally {
        loading.value = false
      }
    }
    
    // Load violations on mount
    onMounted(() => {
      fetchViolations()
    })
    
    const filteredViolations = computed(() => {
      return violations.value.filter(violation => {
        const matchesSearch = !searchQuery.value || 
          violation.vehicleNumber.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          (violation.driverName && violation.driverName.toLowerCase().includes(searchQuery.value.toLowerCase()))
        
        const matchesStatus = !filterStatus.value || 
          (violation.paymentStatus && violation.paymentStatus.toLowerCase() === filterStatus.value.toLowerCase())
        const matchesType = !filterType.value || violation.violationType.toLowerCase().includes(filterType.value.toLowerCase())
        
        return matchesSearch && matchesStatus && matchesType
      })
    })
    
    const navigateTo = (path) => {
      router.push(path)
    }
    
    const logout = () => {
      authStore.logout()
      router.push('/login')
    }
    
    const searchViolations = () => {
      console.log('Searching violations with:', { searchQuery: searchQuery.value, filterStatus: filterStatus.value, filterType: filterType.value })
      fetchViolations()
    }
    
    const viewViolation = (id) => {
      showToast({ title: 'Violation', message: `Viewing violation details for ID: ${id}`, type: 'info' })
    }
    
    const issueTicket = (id) => {
      router.push(`/violations/${id}/ticket`)
    }
    
    const formatDate = (date) => {
      return date.toLocaleString()
    }
    
    const getStatusStyle = (status) => {
      if (!status) return 'color: #666; font-size: 0.8rem;'
      const normalizedStatus = status.toLowerCase()
      const styles = {
        pending: 'color: #f59e0b; font-size: 0.8rem; font-weight: bold;',
        paid: 'color: #22c55e; font-size: 0.8rem; font-weight: bold;',
        overdue: 'color: #ef4444; font-size: 0.8rem; font-weight: bold;',
        unpaid: 'color: #f59e0b; font-size: 0.8rem; font-weight: bold;',
        approved: 'color: #22c55e; font-size: 0.8rem; font-weight: bold;',
        rejected: 'color: #ef4444; font-size: 0.8rem; font-weight: bold;',
        draft: 'color: #94a3b8; font-size: 0.8rem; font-weight: bold;'
      }
      return styles[normalizedStatus] || 'color: #666; font-size: 0.8rem;'
    }
    
    return {
      loading,
      error,
      searchQuery,
      filterStatus,
      filterType,
      violations,
      filteredViolations,
      fetchViolations,
      navigateTo,
      logout,
      searchViolations,
      viewViolation,
      issueTicket,
      formatDate,
      getStatusStyle
    }
  }
}
</script>
