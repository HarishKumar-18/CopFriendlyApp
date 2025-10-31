<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Enhanced Horizontal Navigation Bar -->
    <nav style="
      background: linear-gradient(135deg, #1e40af, #3b82f6) !important;
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
          🏛️ Central Team Dashboard
        </h1>
        <p style="margin: 0; font-size: 0.9rem; opacity: 0.9;">
          {{ authStore.user?.role || 'System Administrator' }} | Department: {{ authStore.user?.department || 'Central Command' }}
        </p>
      </div>

      <!-- Right Side: User Info and Actions -->
      <div style="display: flex; align-items: center; gap: 1rem;">
        <span style="font-weight: 500;">{{ authStore.user?.name || 'Central Admin' }}</span>
        
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
            color: #1e40af !important;
            border: 1px solid white !important;
            padding: 0.5rem 1rem !important;
            border-radius: 0.375rem !important;
            font-size: 0.875rem !important;
            font-weight: 500 !important;
            cursor: pointer !important;
            transition: all 0.2s !important;
          "
          onmouseover="this.style.background='#f8fafc !important'; this.style.color='#1e40af !important'"
          onmouseout="this.style.background='white !important'; this.style.color='#1e40af !important'"
        >
          Logout
        </button>
      </div>
    </nav>

    <div class="dashboard max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Central Command Overview -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <div class="central-card" style="background: linear-gradient(135deg, #3b82f6, #1e40af);">
          <div class="card-icon">🏛️</div>
          <div class="card-content">
            <div class="card-number">{{ centralStats.totalDistricts }}</div>
            <div class="card-label">Districts Monitored</div>
            <div class="card-detail">{{ centralStats.activeDistricts }} currently active</div>
          </div>
        </div>
        
        <div class="central-card" style="background: linear-gradient(135deg, #dc2626, #991b1b);">
          <div class="card-icon">�</div>
          <div class="card-content">
            <div class="card-number">{{ centralStats.totalOfficers }}</div>
            <div class="card-label">Total Officers</div>
            <div class="card-detail">{{ centralStats.onlineOfficers }} online now</div>
          </div>
        </div>
        
        <div class="central-card" style="background: linear-gradient(135deg, #16a34a, #15803d);">
          <div class="card-icon">�</div>
          <div class="card-content">
            <div class="card-number">₹{{ centralStats.totalRevenue.toLocaleString() }}</div>
            <div class="card-label">Total Revenue (Month)</div>
            <div class="card-detail">{{ centralStats.revenueGrowth }}% vs last month</div>
          </div>
        </div>
      </div>

      <!-- Team Performance (live) -->
      <div class="card mb-6">
        <div class="card-header">
          <h3>👥 Team Performance (Live)</h3>
        </div>
        <div class="card-body">
          <div style="overflow-x: auto;">
            <table style="width:100%; border-collapse: collapse;">
              <thead>
                <tr style="background: #f8fafc; border-bottom: 2px solid #e5e7eb;">
                  <th style="text-align:left; padding: 0.75rem;">Officer / Sergeant</th>
                  <th style="text-align:center; padding: 0.75rem;">Tickets Paid</th>
                  <th style="text-align:right; padding: 0.75rem;">Fines Collected (₹)</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="teamPerformance.length === 0">
                  <td colspan="3" style="padding:1rem; text-align:center; color:#666;">No team performance data available</td>
                </tr>
                <tr v-for="row in teamPerformance" :key="row.issuerId" style="border-bottom:1px solid #eee;">
                  <td style="padding:0.75rem;">{{ row.issuerName || 'Unknown' }}</td>
                  <td style="padding:0.75rem; text-align:center;">{{ row.paidCount }}</td>
                  <td style="padding:0.75rem; text-align:right;">₹{{ row.finesCollected.toLocaleString() }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem; margin-bottom: 2rem;">
        

        <!-- Command Center Actions -->
        <div class="card">
          <div class="card-header">
            <h3>⚡ Command Center</h3>
          </div>
          <div class="card-body">
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
              <button @click="issueStateWideAlert" class="command-btn" style="background: linear-gradient(135deg, #dc2626, #991b1b);">
                <div class="btn-icon">🚨</div>
                <div class="btn-text">State-wide Alert</div>
              </button>
              <button @click="manageOfficerDeployment" class="command-btn" style="background: linear-gradient(135deg, #3b82f6, #1e40af);">
                <div class="btn-icon">👮</div>
                <div class="btn-text">Deploy Officers</div>
              </button>
              
            </div>
          </div>
        </div>
      </div>

      <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 2rem;">
        <!-- Main Content -->
        <!-- Central: Approved & Rejected Violations -->
        <div>
          <div class="card">
            <div class="card-header" style="display:flex; justify-content:space-between; align-items:center">
              <h3>📚 Approved & Rejected Violations</h3>
              <div style="display:flex; gap:0.5rem; align-items:center">
                <button @click="debugFetch" class="btn-small">🔍 Debug fetch</button>
              </div>
            </div>
            <div class="card-body">
              <div style="margin-bottom:0.5rem; color:#555">{{ statusMessage }}</div>
              <div v-if="centralApprovedRejected.length === 0" style="padding:1rem; color:#666">No approved or rejected violations found</div>
              <ul v-else style="list-style:none; margin:0; padding:0; display:flex; flex-direction:column; gap:0.5rem;">
                <li v-for="v in centralApprovedRejected" :key="v.id" style="padding:0.5rem; border:1px solid #eee; border-radius:6px; display:flex; justify-content:space-between; align-items:center; gap:0.5rem;">
                  <div>
                    <div style="font-weight:600">{{ v.vehicleNumber || '—' }} <span style="font-weight:400; color:#666; margin-left:0.5rem">{{ v.violationType }}</span></div>
                    <div style="font-size:0.85rem; color:#666">Officer: {{ v.officerName || 'Unknown' }} · ₹{{ v.amount }} · {{ new Date(v.timestamp).toLocaleString() }}</div>
                  </div>
                  <div>
                    <span v-if="v.status === 'APPROVED'" class="btn-success" style="padding:0.25rem 0.5rem; border-radius:4px;">APPROVED</span>
                    <span v-else-if="v.status === 'REJECTED'" class="btn-warning" style="padding:0.25rem 0.5rem; border-radius:4px;">REJECTED</span>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </div>


        <!-- Sidebar -->
        <div>
          <!-- System Actions -->
          <div class="card">
            <div class="card-header">
              <h3>⚙️ System Actions</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; gap: 1rem;">
                <button @click="backupSystem" class="action-btn">
                  💾 Backup System
                </button>
                <button @click="generateReports" class="action-btn">
                  📊 Generate Reports
                </button>
                <button @click="updatePolicies" class="action-btn">
                  📝 Update Policies
                </button>
                <button @click="broadcastMessage" class="action-btn">
                  📢 Broadcast Message
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
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { showToast, showConfirm, showInput } from '@/composables/interactionService'
import { violationsAPI } from '@/services/api'

export default {
  name: 'CentralTeamDashboardView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const userRole = ref('System Administrator')
    const selectedView = ref('violations')
    const performancePeriod = ref('today')
    
    const centralStats = ref({
      totalDistricts: 15,
      activeDistricts: 14,
      totalOfficers: 450,
      onlineOfficers: 287,
      totalRevenue: 12500000,
      revenueGrowth: 15,
      criticalAlerts: 3,
      resolvedAlerts: 12
    })
    
    const liveData = ref({
      violations: [{ id: 1 }, { id: 2 }, { id: 3 }],
      officers: [{ id: 1 }, { id: 2 }, { id: 3 }, { id: 4 }],
      emergencies: [{ id: 1 }],
      revenue: [{ id: 1 }, { id: 2 }]
    })
    
    const districtPerformance = ref([
      {
        id: 1,
        name: 'Central District',
        inspector: 'Inspector Sharma',
        officers: 35,
        violations: 125,
        revenue: 450000,
        efficiency: 92,
        status: 'Optimal'
      },
      {
        id: 2,
        name: 'Eastern District',
        inspector: 'Inspector Patel',
        officers: 28,
        violations: 89,
        revenue: 320000,
        efficiency: 87,
        status: 'Good'
      },
      {
        id: 3,
        name: 'Western District',
        inspector: 'Inspector Kumar',
        officers: 32,
        violations: 67,
        revenue: 280000,
        efficiency: 75,
        status: 'Needs Attention'
      }
    ])
    
    const criticalEscalations = ref([
      {
        id: 1,
        type: 'Major Incident',
        district: 'Central District',
        officer: 'Inspector Sharma',
        description: 'Multi-vehicle accident with potential fatality - requires immediate central coordination',
        timestamp: new Date(Date.now() - 30 * 60 * 1000)
      },
      {
        id: 2,
        type: 'System Issue',
        district: 'Eastern District',
        officer: 'Inspector Patel',
        description: 'Payment gateway malfunction affecting fine collection in multiple locations',
        timestamp: new Date(Date.now() - 60 * 60 * 1000)
      }
    ])
    
    const activeAlerts = ref([
      {
        id: 1,
        title: 'High Traffic Volume',
        message: 'Unusual traffic spike detected in Central District',
        severity: 'medium',
        timestamp: new Date(Date.now() - 15 * 60 * 1000)
      },
      {
        id: 2,
        title: 'System Performance',
        message: 'Payment gateway response time degraded',
        severity: 'low',
        timestamp: new Date(Date.now() - 45 * 60 * 1000)
      }
    ])
    
    const revenueAnalytics = ref({
      daily: 850000,
      topDistrict: 'Central District',
      growth: 15
    })

    // Team performance aggregated from violations/payments
    const teamPerformance = ref([])
    let centralPollInterval = null
  let baseCollectedFromAPI = 0
  let lastInspectorTs = 0

    // Violations state (Central view)
    const allViolations = ref([])
    const statusMessage = ref('')

    const normalizeViolation = (raw) => {
      const id = raw?.id || raw?._id || raw?.violationId || ''
      const vehicleNumber = raw?.vehicleNumber || raw?.vehicle_number || raw?.vehicle || ''
      const violationType = raw?.violationType || raw?.type || raw?.violation || ''
      const officerName = raw?.issuerName || raw?.issuer || raw?.officerName || raw?.issuerObject?.name || ''
      const amount = normalizeAmount(raw)
      const status = (raw?.status || raw?.paymentStatus || raw?.state || '') + ''
      // Prefer explicit timestamps, fall back to createdAt or now
      const tsRaw = raw?.violationDateTime || raw?.createdAt || raw?.created_at || raw?.timestamp || raw?.date
      const timestamp = tsRaw ? new Date(tsRaw).getTime() : Date.now()
      return { id, vehicleNumber, violationType, officerName, amount, status: (status || '').toUpperCase(), timestamp, raw }
    }

    const extractViolationsFromResponse = (resp) => {
      if (!resp) return []
      const data = resp?.data
      if (!data) return []
      // common shapes: { violations: [...] } or [...]
      const list = Array.isArray(data) ? data : (Array.isArray(data.violations) ? data.violations : (Array.isArray(data.data) ? data.data : []))
      return list
    }

    const loadViolations = async () => {
      statusMessage.value = 'Loading violations...'
      try {
        const resp = await violationsAPI.getAll()
        // normalize many shapes
        const list = Array.isArray(resp?.data) ? resp.data : (resp?.data?.violations ?? resp?.data ?? [])
        const rawList = Array.isArray(list) ? list : []
        const normalized = rawList.map(normalizeViolation).sort((a, b) => (b.timestamp || 0) - (a.timestamp || 0))
        allViolations.value = normalized
        statusMessage.value = `Loaded ${normalized.length} violations`
        // also update central revenue aggregation after loading violations
        try { loadCentralData() } catch (e) { /* best-effort */ }
        return normalized
      } catch (err) {
        console.error('loadViolations error', err)
        statusMessage.value = 'Failed to load violations — trying public fallback'
        // fallback: try unpaid forced endpoint as safe demo fallback
        try {
          const resp2 = await violationsAPI.getUnpaidForce()
          const list2 = Array.isArray(resp2?.data) ? resp2.data : (resp2?.data?.violations ?? resp2?.data ?? [])
          const normalized2 = (Array.isArray(list2) ? list2 : []).map(normalizeViolation).sort((a, b) => (b.timestamp || 0) - (a.timestamp || 0))
          allViolations.value = normalized2
          statusMessage.value = `Loaded ${normalized2.length} violations (public fallback)`
          return normalized2
        } catch (err2) {
          console.error('public fallback failed', err2)
          statusMessage.value = 'Failed to load violations (no fallback)'
          allViolations.value = []
          return []
        }
      }
    }

    const debugFetch = async () => {
      try {
        const resp = await violationsAPI.getAll()
        // eslint-disable-next-line no-console
        console.log('Central debug fetch response:', resp)
        statusMessage.value = `Debug: received ${Array.isArray(resp?.data) ? resp.data.length : (resp?.data?.violations ? resp.data.violations.length : 0)} items`
        // reload normalized view
        await loadViolations()
      } catch (e) {
        console.error('debugFetch failed', e)
        statusMessage.value = 'Debug fetch failed — see console'
      }
    }

    const centralApprovedRejected = computed(() => allViolations.value.filter(v => (v.status || '').toUpperCase() === 'APPROVED' || (v.status || '').toUpperCase() === 'REJECTED'))

    const normalizeAmount = (v) => {
      const raw = v?.fineAmount ?? v?.amount ?? v?.fine ?? v?.fine_amount ?? 0
      const n = parseFloat(raw)
      return Number.isFinite(n) ? n : 0
    }

    const normalizePaymentStatus = (v) => {
      const s = (v?.paymentStatus || v?.paymentstatus || v?.status || v?.payment || '') + ''
      return s.trim().toUpperCase()
    }

    // Load central statistics from violations (fallback-friendly)
    const loadCentralData = async () => {
      try {
        // Try to get all violations; the API may return { violations: [...] } or an array
        const resp = await violationsAPI.getAll()
        const list = resp?.data?.violations ?? resp?.data ?? []

        // Sum paid fines for revenue and aggregate by issuer/sergeant
        let totalCollected = 0
        const byIssuer = {}

        for (const raw of list) {
          const status = normalizePaymentStatus(raw)
          const amount = normalizeAmount(raw)

          // treat PAID as revenue
          if (status === 'PAID' || status === 'PAID_ONLINE') {
            totalCollected += amount
            const issuerId = (raw?.issuerId || raw?.issuer || raw?.issuer_id || raw?.issuerObject?.id || raw?.issuerObject?._id) + ''
            const issuerName = raw?.issuerName || raw?.issuer || raw?.officerName || raw?.issuerObject?.name || raw?.issuerObject?.fullName || 'Unknown'
            if (!byIssuer[issuerId]) byIssuer[issuerId] = { issuerId, issuerName, paidCount: 0, finesCollected: 0 }
            byIssuer[issuerId].paidCount += 1
            byIssuer[issuerId].finesCollected += amount
          }
        }

        // Update central stats and teamPerformance
        // Merge with existing monthly revenue (assume centralStats.totalRevenue is month figure)
        // Also include any inspector-reported revenue available via localStorage
        // Remember API-collected baseline so we can safely merge inspector-local tallies
        baseCollectedFromAPI = Math.round(totalCollected)

        let inspectorAmount = 0
        try {
          const payload = JSON.parse(localStorage.getItem('inspectorRevenue'))
          // only include if timestamp is newer than what we've applied
          const ts = Number(payload?.ts || 0)
          const amt = Number(payload?.amount || 0)
          if (ts && ts > lastInspectorTs) {
            lastInspectorTs = ts
            inspectorAmount = amt
          } else {
            inspectorAmount = 0
          }
        } catch (e) {
          inspectorAmount = 0
        }

        // Combined live total = API-collected + inspector-local tally (if any)
        const combined = Math.round(baseCollectedFromAPI + (inspectorAmount || 0))
        // Prefer the live combined value when available (avoids showing stale demo baseline)
        if (combined > 0) {
          centralStats.value.totalRevenue = combined
        }

        teamPerformance.value = Object.values(byIssuer).sort((a, b) => b.finesCollected - a.finesCollected)
      } catch (err) {
        console.error('Failed to load central data:', err)
      }
    }
    
    const navigateTo = (path) => {
      router.push(path)
    }
    
    const logout = () => {
      authStore.logout()
      router.push('/login')
    }
    
    // Central Team Actions
    const issueStateWideAlert = () => {
      showToast({ title: 'Alert', message: 'Issuing state-wide alert to all districts and officers...', type: 'warning' })
    }
    
    const manageOfficerDeployment = () => {
      showToast({ title: 'Deployment', message: 'Opening officer deployment and resource allocation system...', type: 'info' })
    }
    
    const analyzePatterns = () => {
      showToast({ title: 'Analysis', message: 'Starting AI-powered pattern analysis across all districts...', type: 'info' })
    }
    
    const coordinateDistricts = () => {
      showToast({ title: 'Coordination', message: 'Opening inter-district coordination platform...', type: 'info' })
    }
    
    const managePolicies = () => {
      showToast({ title: 'Policies', message: 'Opening policy management and update system...', type: 'info' })
    }
    
    const systemMaintenance = () => {
      showToast({ title: 'Maintenance', message: 'Accessing system maintenance and monitoring tools...', type: 'info' })
    }
    
    const refreshMap = () => {
      showToast({ title: 'Map', message: 'Refreshing real-time operations map...', type: 'success' })
    }
    
    const exportDistrictData = () => {
      showToast({ title: 'Export', message: 'Exporting comprehensive district performance data...', type: 'info' })
    }
    
    const viewDistrictDetails = (district) => {
      showToast({ title: 'District', message: `Opening detailed analytics for ${district.name}`, type: 'info' })
    }
    
    const sendDirective = (district) => {
      showToast({ title: 'Directive', message: `Sending directive to ${district.name}`, type: 'info' })
    }
    
    const deploySupport = (district) => {
      showToast({ title: 'Support', message: `Deploying additional support to ${district.name}`, type: 'info' })
    }
    
    const approveEscalation = (escalation) => {
      showToast({ title: 'Escalation', message: `Approving escalation: ${escalation.type}`, type: 'success' })
      // Remove from list
      const index = criticalEscalations.value.findIndex(e => e.id === escalation.id)
      if (index > -1) {
        criticalEscalations.value.splice(index, 1)
      }
    }
    
    const investigateEscalation = (escalation) => {
      showToast({ title: 'Investigate', message: `Initiating investigation for: ${escalation.type}`, type: 'info' })
    }
    
    const rejectEscalation = (escalation) => {
      showToast({ title: 'Escalation', message: `Rejecting escalation: ${escalation.type}`, type: 'warning' })
      // Remove from list
      const index = criticalEscalations.value.findIndex(e => e.id === escalation.id)
      if (index > -1) {
        criticalEscalations.value.splice(index, 1)
      }
    }
    
    const refreshSystemStatus = () => {
      showToast({ title: 'System', message: 'System status refreshed!', type: 'success' })
    }
    
    const acknowledgeAlert = (alert) => {
      showToast({ title: 'Alert', message: `Alert acknowledged: ${alert.title}`, type: 'info' })
      // Remove from active alerts
      const index = activeAlerts.value.findIndex(a => a.id === alert.id)
      if (index > -1) {
        activeAlerts.value.splice(index, 1)
      }
    }

    // React to cross-tab signals (violationPaid etc.) to keep central dashboard in sync
    const storageHandler = (ev) => {
      if (!ev) return
      const key = ev.key
      if (!key) return
      if (key === 'violationPaid' || key === 'violationApproved' || key === 'violationRejected' || key === 'violationCreated' || key === 'violationUpdated' || key === 'sergeantUpdated' || key === 'sergeantAdded') {
        // small debounce not needed; just reload both central aggregates and violations list
        try { loadCentralData() } catch (e) { /* best-effort */ }
        try { loadViolations() } catch (e) { /* best-effort */ }
      }
      if (key === 'inspectorRevenue') {
        // incorporate inspector's computed revenue into central view immediately but avoid double-counting
        try {
          const payload = JSON.parse(localStorage.getItem('inspectorRevenue'))
          const inspectorAmount = Number(payload?.amount || 0)
          const ts = Number(payload?.ts || 0)
          if (ts && ts > lastInspectorTs) {
            lastInspectorTs = ts
            const combined = Math.round((baseCollectedFromAPI || 0) + (inspectorAmount || 0))
            // Apply inspector update only if it yields a meaningful combined amount
            if (combined > 0) {
              centralStats.value.totalRevenue = combined
            }
          }
        } catch (e) {
          console.warn('Failed to parse inspectorRevenue from storage:', e)
        }
      }
    }
    
    const backupSystem = () => {
      showToast({ title: 'Backup', message: 'Initiating system-wide backup...', type: 'info' })
    }
    
    const generateReports = () => {
      showToast({ title: 'Reports', message: 'Generating comprehensive system reports...', type: 'info' })
    }
    
    const updatePolicies = () => {
      showToast({ title: 'Policies', message: 'Opening policy update interface...', type: 'info' })
    }
    
    const broadcastMessage = () => {
      showToast({ title: 'Broadcast', message: 'Opening broadcast message system...', type: 'info' })
    }
    
    const formatTime = (date) => {
      return date.toLocaleTimeString('en-IN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
    }
    
    const formatDateTime = (date) => {
      return date.toLocaleString('en-IN')
    }
    
    const getDistrictStatusClass = (status) => {
      const classes = {
        'Optimal': 'bg-green-100 text-green-800',
        'Good': 'bg-blue-100 text-blue-800',
        'Needs Attention': 'bg-yellow-100 text-yellow-800',
        'Critical': 'bg-red-100 text-red-800'
      }
      return classes[status] || 'bg-gray-100 text-gray-800'
    }
    
    const getAlertColor = (severity) => {
      const colors = {
        'low': '#22c55e',
        'medium': '#f59e0b',
        'high': '#dc2626'
      }
      return colors[severity] || '#6b7280'
    }
    
    const getAlertBg = (severity) => {
      const backgrounds = {
        'low': '#f0fdf4',
        'medium': '#fefce8',
        'high': '#fef2f2'
      }
      return backgrounds[severity] || '#f9fafb'
    }
    
    onMounted(() => {
      // initial load
      loadCentralData()
      loadViolations()

      // Short-poll while page visible
      centralPollInterval = setInterval(() => {
        if (document.visibilityState === 'visible') {
          loadCentralData()
          loadViolations()
        }
      }, 10000)

      window.addEventListener('storage', storageHandler)
    })

    onBeforeUnmount(() => {
      if (centralPollInterval) clearInterval(centralPollInterval)
      window.removeEventListener('storage', storageHandler)
    })
    return {
      authStore,
      userRole,
      selectedView,
      performancePeriod,
      centralStats,
      liveData,
      districtPerformance,
      criticalEscalations,
      activeAlerts,
      revenueAnalytics,
      navigateTo,
      logout,
      issueStateWideAlert,
      manageOfficerDeployment,
      analyzePatterns,
      coordinateDistricts,
      managePolicies,
      systemMaintenance,
      refreshMap,
      exportDistrictData,
      viewDistrictDetails,
      sendDirective,
      deploySupport,
      approveEscalation,
      investigateEscalation,
      rejectEscalation,
      refreshSystemStatus,
      acknowledgeAlert,
      backupSystem,
      generateReports,
  teamPerformance,
      updatePolicies,
      broadcastMessage,
      // Violations (Central)
      allViolations,
      statusMessage,
      loadViolations,
      centralApprovedRejected,
      debugFetch,
      formatTime,
      formatDateTime,
      getDistrictStatusClass,
      getAlertColor,
      getAlertBg
    }
  }
}
</script>

<style scoped>
.central-card {
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

.command-btn {
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

.command-btn:hover {
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
  padding: 0.25rem 0.5rem;
  font-size: 0.8rem;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  cursor: pointer;
}

.btn-success {
  background: #dcfce7;
  color: #166534;
  border-color: #bbf7d0;
}

.btn-warning {
  background: #fef3c7;
  color: #92400e;
  border-color: #fcd34d;
}

.btn-danger {
  background: #fecaca;
  color: #dc2626;
  border-color: #f87171;
}
</style>