<template>
  <div class="dashboard">
    <nav style="background: #9333ea; color: white; padding: 1rem; display: flex; justify-content: space-between; align-items: center;">
      <div>
        <h1 style="font-size: 1.5rem; font-weight: bold;">Central Dashboard</h1>
        <p>Traffic Central Command Center</p>
      </div>
      <div style="display: flex; gap: 1rem; align-items: center;">
        <span>Central Admin</span>
        <button @click="logout" style="background: white; color: #9333ea; padding: 0.5rem 1rem; border: none; border-radius: 0.25rem; cursor: pointer;">
          Logout
        </button>
      </div>
    </nav>

    <!-- Main Content -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Welcome Section -->
      <div class="bg-white shadow-lg rounded-xl p-6 mb-8">
        <div class="flex items-center justify-between">
          <div>
            <h2 class="text-3xl font-bold text-gray-900 mb-2">Welcome to Central Command</h2>
            <p class="text-gray-600">Monitor and manage traffic operations across all districts</p>
          </div>
          <div class="text-6xl opacity-20">🏛️</div>
        </div>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <div class="bg-gradient-to-br from-blue-500 to-blue-700 rounded-xl shadow-lg p-6 text-white transform hover:scale-105 transition-all duration-200">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-3xl font-bold mb-1">15</div>
              <div class="text-blue-100 font-medium">Districts</div>
              <div class="text-sm text-blue-200 mt-1">12 active now</div>
            </div>
            <div class="text-4xl opacity-80">🌆</div>
          </div>
        </div>

        <div class="bg-gradient-to-br from-green-500 to-green-700 rounded-xl shadow-lg p-6 text-white transform hover:scale-105 transition-all duration-200">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-3xl font-bold mb-1">1,247</div>
              <div class="text-green-100 font-medium">Total Officers</div>
              <div class="text-sm text-green-200 mt-1">985 on duty</div>
            </div>
            <div class="text-4xl opacity-80">👮</div>
          </div>
        </div>

        <div class="bg-gradient-to-br from-yellow-500 to-orange-600 rounded-xl shadow-lg p-6 text-white transform hover:scale-105 transition-all duration-200">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-3xl font-bold mb-1">432</div>
              <div class="text-yellow-100 font-medium">Active Violations</div>
              <div class="text-sm text-yellow-200 mt-1">+12% today</div>
            </div>
            <div class="text-4xl opacity-80">⚠️</div>
          </div>
        </div>

        <div class="bg-gradient-to-br from-red-500 to-red-700 rounded-xl shadow-lg p-6 text-white transform hover:scale-105 transition-all duration-200">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-3xl font-bold mb-1">7</div>
              <div class="text-red-100 font-medium">SOS Alerts</div>
              <div class="text-sm text-red-200 mt-1">2 critical</div>
            </div>
            <div class="text-4xl opacity-80">🚨</div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <!-- Left Column -->
        <div class="space-y-6">
          <div class="bg-white shadow-lg rounded-xl p-6">
            <h3 class="text-xl font-bold text-gray-900 mb-4 flex items-center">
              <span class="mr-2">⚡</span>
              Quick Actions
            </h3>
            <div class="grid grid-cols-2 gap-4">
              <button class="p-4 bg-blue-50 hover:bg-blue-100 rounded-lg text-left transition-colors duration-200 border border-blue-200">
                <div class="text-2xl mb-2">📊</div>
                <div class="font-semibold text-blue-900">System Overview</div>
                <div class="text-sm text-blue-600">View analytics</div>
              </button>
              <button class="p-4 bg-green-50 hover:bg-green-100 rounded-lg text-left transition-colors duration-200 border border-green-200">
                <div class="text-2xl mb-2">👥</div>
                <div class="font-semibold text-green-900">Personnel</div>
                <div class="text-sm text-green-600">Manage staff</div>
              </button>
              <button class="p-4 bg-purple-50 hover:bg-purple-100 rounded-lg text-left transition-colors duration-200 border border-purple-200">
                <div class="text-2xl mb-2">📋</div>
                <div class="font-semibold text-purple-900">Policies</div>
                <div class="text-sm text-purple-600">Policy management</div>
              </button>
              <button class="p-4 bg-red-50 hover:bg-red-100 rounded-lg text-left transition-colors duration-200 border border-red-200">
                <div class="text-2xl mb-2">🚨</div>
                <div class="font-semibold text-red-900">Emergency</div>
                <div class="text-sm text-red-600">Alert system</div>
              </button>
            </div>
          </div>
        </div>

        <!-- Right Column -->
        <div class="space-y-6">
          <div class="bg-white shadow-lg rounded-xl p-6">
            <h3 class="text-xl font-bold text-gray-900 mb-4 flex items-center">
              <span class="mr-2">📈</span>
              Recent Activity
            </h3>
            <div class="space-y-4">
              <div class="flex items-center p-3 bg-blue-50 rounded-lg">
                <div class="w-10 h-10 bg-blue-500 rounded-full flex items-center justify-center text-white font-semibold mr-3">
                  A1
                </div>
                <div class="flex-1">
                  <p class="font-medium text-gray-900">New violation reported</p>
                  <p class="text-sm text-gray-500">Area 1 - 5 minutes ago</p>
                </div>
              </div>
              <div class="flex items-center p-3 bg-green-50 rounded-lg">
                <div class="w-10 h-10 bg-green-500 rounded-full flex items-center justify-center text-white font-semibold mr-3">
                  B2
                </div>
                <div class="flex-1">
                  <p class="font-medium text-gray-900">Officer deployed</p>
                  <p class="text-sm text-gray-500">Zone B2 - 12 minutes ago</p>
                </div>
              </div>
              <div class="flex items-center p-3 bg-yellow-50 rounded-lg">
                <div class="w-10 h-10 bg-yellow-500 rounded-full flex items-center justify-center text-white font-semibold mr-3">
                  C3
                </div>
                <div class="flex-1">
                  <p class="font-medium text-gray-900">Traffic alert cleared</p>
                  <p class="text-sm text-gray-500">Junction C3 - 25 minutes ago</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Central Violations (Approved/Rejected or All) -->
      <div class="bg-white shadow-lg rounded-xl p-6 mt-8">
        <h3 class="text-xl font-bold text-gray-900 mb-4 flex items-center">
          <span class="mr-2">📋</span>
          Violations (Central View)
        </h3>
        <div style="display:flex; gap:1rem; align-items:center; margin-bottom:1rem;">
          <select v-model="centralView" class="form-control" style="width: auto;">
            <option value="approvedRejected">Approved & Rejected ({{ centralApprovedRejected.length }})</option>
            <option value="all">All Violations ({{ centralAll.length }})</option>
          </select>
          <button @click="loadViolations" class="btn-refresh">🔄 Refresh</button>
          <button @click="loadViolations(true)" class="btn" style="background:#f3f4f6;color:#111;border:1px solid #e5e7eb;">⚠️ Use public fallback</button>
          <div style="font-size:0.9rem; color:#6b7280;">Showing: {{ centralView === 'approvedRejected' ? centralApprovedRejected.length : centralAll.length }}</div>
        </div>

        <div v-if="statusMessage" style="color:#b91c1c; margin-bottom:1rem; font-size:0.95rem;">{{ statusMessage }}</div>
        <div style="margin-bottom:1rem; display:flex; gap:0.5rem; align-items:center;">
          <button @click="debugFetch" class="btn-small">🔍 Debug fetch</button>
          <div style="font-size:0.85rem; color:#6b7280;">Click Debug fetch to log raw response and show counts.</div>
        </div>

        <div v-if="(centralView === 'approvedRejected' ? centralApprovedRejected.length : centralAll.length) === 0" style="text-align:center; color:#6b7280; padding:2rem;">No violations to display ✅</div>

        <div v-else>
          <div v-for="violation in (centralView === 'approvedRejected' ? centralApprovedRejected : centralAll)" :key="violation.id" :style="{border:'1px solid #e5e7eb', borderRadius:'8px', padding:'1rem', marginBottom:'1rem', background: violation.status === 'APPROVED' ? '#f0fdf4' : violation.status === 'REJECTED' ? '#fef2f2' : '#ffffff'}">
            <div style="display:flex; justify-content:space-between; align-items:start;">
              <div style="flex:1;">
                <div style="display:flex; align-items:center; gap:0.5rem; margin-bottom:0.5rem;">
                  <div style="font-weight:500;">{{ violation.vehicleNumber }} - {{ violation.violationType }}</div>
                  <span :style="{padding:'0.25rem 0.5rem', borderRadius:'4px', fontSize:'0.75rem', fontWeight:'600', background: violation.status === 'APPROVED' ? '#dcfce7' : violation.status === 'REJECTED' ? '#fecaca' : '#f3f4f6', color: violation.status === 'APPROVED' ? '#166534' : violation.status === 'REJECTED' ? '#991b1b' : '#374151'}">{{ violation.status }}</span>
                </div>
                <div style="font-size:0.9rem; color:#6b7280; margin:0.5rem 0;">Officer: {{ violation.officerName }} | Location: {{ violation.location }}</div>
                <div style="font-size:0.8rem; color:#9ca3af;">{{ formatDateTime(violation.timestamp) }}</div>
              </div>
              <div style="display:flex; gap:0.5rem; align-items:center;">
                <button @click="viewOfficerDetails(violation)" class="btn-small">👁️</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Violation Approval Center (Central copy of inspector view) -->
      <div class="bg-white shadow-lg rounded-xl p-6 mt-8">
        <div class="card-header" style="display:flex; justify-content:space-between; align-items:center;">
          <h3 class="text-xl font-bold text-gray-900">✅ Violation Approval Center</h3>
          <div style="display:flex; gap:1rem; align-items:center;">
            <select v-model="selectedStatus" @change="filterCentralViolations" class="form-control" style="width:auto;">
              <option value="all">All ({{ violationCounts.all }})</option>
              <option value="pending">Pending ({{ violationCounts.pending }})</option>
              <option value="approved">Approved ({{ violationCounts.approved }})</option>
              <option value="rejected">Rejected ({{ violationCounts.rejected }})</option>
            </select>
            <button @click="loadViolations" class="btn-refresh">🔄 Refresh</button>
            <div style="font-size:0.9rem; color:#6b7280;">Showing: {{ filteredCentral.length }}</div>
          </div>
        </div>
        <div class="card-body" style="margin-top:1rem;">
          <div v-if="filteredCentral.length === 0" style="text-align:center; color:#6b7280; padding:2rem;">No violations found for this filter ✅</div>
          <div v-else>
            <div v-for="violation in filteredCentral" :key="violation.id" :style="{border:'1px solid #e5e7eb', borderRadius:'8px', padding:'1rem', marginBottom:'1rem', background: violation.status === 'APPROVED' ? '#f0fdf4' : violation.status === 'REJECTED' ? '#fef2f2' : '#fffbeb'}">
              <div style="display:flex; justify-content:space-between; align-items:start;">
                <div style="flex:1;">
                  <div style="display:flex; align-items:center; gap:0.5rem; margin-bottom:0.5rem;">
                    <div style="font-weight:500;">{{ violation.vehicleNumber }} - {{ violation.violationType }}</div>
                    <span :style="{padding:'0.25rem 0.5rem', borderRadius:'4px', fontSize:'0.75rem', fontWeight:'600', background: violation.status === 'APPROVED' ? '#dcfce7' : violation.status === 'REJECTED' ? '#fecaca' : '#fef3c7', color: violation.status === 'APPROVED' ? '#166534' : violation.status === 'REJECTED' ? '#991b1b' : '#92400e'}">{{ violation.status }}</span>
                  </div>
                  <div style="font-size:0.9rem; color:#6b7280; margin:0.5rem 0;">Officer: {{ violation.officerName }} | Location: {{ violation.location }}</div>
                  <div style="font-size:0.8rem; color:#9ca3af;">{{ formatDateTime(violation.timestamp) }}</div>
                </div>
                <div v-if="violation.status === 'PENDING'" style="display:flex; gap:0.5rem;">
                  <button @click="approveCentralViolation(violation)" class="btn btn-success btn-small">✅ Approve</button>
                  <button @click="rejectCentralViolation(violation)" class="btn btn-danger btn-small">❌ Reject</button>
                  <button @click="requestMoreInfoCentral(violation)" class="btn btn-small">❓ More Info</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

        <!-- Violations History (chronological) -->
        <div class="bg-white shadow-lg rounded-xl p-6 mt-8">
          <h3 class="text-xl font-bold text-gray-900 mb-4 flex items-center">
            <span class="mr-2">🕘</span>
            Violations History
          </h3>
          <div v-if="historyList.length === 0" style="text-align:center; color:#6b7280; padding:2rem;">No historical violations found.</div>
          <div v-else>
            <div v-for="v in historyList" :key="v.id" style="display:flex; justify-content:space-between; align-items:center; padding:0.75rem 0; border-bottom:1px solid #f1f5f9;">
              <div>
                <div style="font-weight:600;">{{ v.vehicleNumber || '—' }} — {{ v.violationType || 'Unknown' }}</div>
                <div style="font-size:0.85rem; color:#6b7280;">Officer: {{ v.officerName }} • {{ v.location }}</div>
              </div>
              <div style="text-align:right; min-width:160px;">
                <div style="font-weight:600; color: #374151;">{{ v.status }}</div>
                <div style="font-size:0.8rem; color:#9ca3af;">{{ formatDateTime(v.timestamp) }}</div>
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
import { violationsAPI } from '@/services/api'

export default {
  name: 'CentralDashboardView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()

    const logout = () => {
      authStore.logout()
      router.push('/login')
    }

    // Violations state for Central view
    const allViolations = ref([])
    const centralView = ref('approvedRejected') // 'approvedRejected' | 'all'
    const centralApprovedRejected = computed(() => allViolations.value.filter(v => v.status === 'APPROVED' || v.status === 'REJECTED'))
    const centralAll = computed(() => allViolations.value)
    const historyList = computed(() => {
      // Return a copy sorted newest first
      return [...allViolations.value].sort((a, b) => {
        const ta = a.timestamp ? new Date(a.timestamp).getTime() : 0
        const tb = b.timestamp ? new Date(b.timestamp).getTime() : 0
        return tb - ta
      })
    })

    const normalize = (v) => ({
      id: v.id || v._id || v.violationId,
      vehicleNumber: v.vehicleNumber || v.vehicleNo || v.vehicle_num || '',
      violationType: v.violationType || v.type || '',
      officerName: v.issuerName || v.officerName || (v.issuer && v.issuer.fullName) || 'Unknown',
      location: v.location || v.place || '',
      timestamp: v.createdAt ? new Date(v.createdAt) : (v.violationDateTime ? new Date(v.violationDateTime) : new Date()),
      amount: Number(v.fineAmount || v.amount || 0),
      status: (v.status || v.paymentStatus || '').toString().toUpperCase()
    })

    const statusMessage = ref('')

    const extractViolationsFromResponse = (resp) => {
      // Support multiple possible response shapes
      if (!resp) return []
      if (Array.isArray(resp)) return resp
      if (resp.data) {
        if (Array.isArray(resp.data)) return resp.data
        if (resp.data.violations) return resp.data.violations
        if (resp.data.data && Array.isArray(resp.data.data)) return resp.data.data
        if (resp.data.data && resp.data.data.violations) return resp.data.data.violations
      }
      // If resp itself has .violations
      if (resp.violations) return resp.violations
      return []
    }

    const loadViolations = async (useFallback = false) => {
      statusMessage.value = ''
      try {
        let resp
        if (!useFallback) {
          resp = await violationsAPI.getAll()
        } else {
          // use public fallback endpoints
          const [pendingResp, unpaidResp] = await Promise.all([
            violationsAPI.getPending(),
            violationsAPI.getUnpaidForce()
          ])
          const merged = [...extractViolationsFromResponse(pendingResp), ...extractViolationsFromResponse(unpaidResp)]
          allViolations.value = merged.map(normalize)
          if (allViolations.value.length === 0) statusMessage.value = 'No violations returned from fallback endpoints.'
          return
        }

        const violations = extractViolationsFromResponse(resp)
        allViolations.value = violations.map(normalize)
        if (allViolations.value.length === 0) {
          statusMessage.value = 'No violations returned from server. Try the fallback option.'
        }
      } catch (error) {
        console.error('Central: failed to load violations', error)
        statusMessage.value = `Failed to load violations: ${error?.response?.status || error.message || 'unknown'}`
        // try fallback automatically when auth/403 or empty
        try {
          const [pendingResp, unpaidResp] = await Promise.all([
            violationsAPI.getPending(),
            violationsAPI.getUnpaidForce()
          ])
          const merged = [...extractViolationsFromResponse(pendingResp), ...extractViolationsFromResponse(unpaidResp)]
          allViolations.value = merged.map(normalize)
          if (allViolations.value.length === 0) statusMessage.value = statusMessage.value + ' — fallback returned no data.'
        } catch (e) {
          console.error('Central: fallback also failed', e)
          statusMessage.value = statusMessage.value + ' — fallback failed.'
        }
      }
    }

    // Violation Approval Center helpers
    const selectedStatus = ref('all')
    const violationCounts = computed(() => ({
      all: allViolations.value.length,
      pending: allViolations.value.filter(v => v.status === 'PENDING').length,
      approved: allViolations.value.filter(v => v.status === 'APPROVED').length,
      rejected: allViolations.value.filter(v => v.status === 'REJECTED').length,
    }))

    const filteredCentral = computed(() => {
      if (selectedStatus.value === 'all') return allViolations.value
      if (selectedStatus.value === 'pending') return allViolations.value.filter(v => v.status === 'PENDING')
      if (selectedStatus.value === 'approved') return allViolations.value.filter(v => v.status === 'APPROVED')
      if (selectedStatus.value === 'rejected') return allViolations.value.filter(v => v.status === 'REJECTED')
      return allViolations.value
    })

    const filterCentralViolations = () => {
      // computed will update automatically; keep function for @change handler
      return
    }

    const approveCentralViolation = async (violation) => {
      try {
        await violationsAPI.approve(violation.id)
        try { localStorage.setItem('violationApproved', Date.now().toString()) } catch(e){}
        alert(`Violation ${violation.vehicleNumber || violation.id} approved.`)
        loadViolations()
      } catch (e) { alert('Failed to approve: ' + (e?.response?.data?.error || e.message)) }
    }

    const rejectCentralViolation = async (violation) => {
      try {
        await violationsAPI.reject(violation.id, 'Rejected by central')
        try { localStorage.setItem('violationRejected', Date.now().toString()) } catch(e){}
        alert(`Violation ${violation.vehicleNumber || violation.id} rejected.`)
        loadViolations()
      } catch (e) { alert('Failed to reject: ' + (e?.response?.data?.error || e.message)) }
    }

    const requestMoreInfoCentral = (violation) => { alert(`Requesting more info for ${violation.id}`) }

    const formatDateTime = (date) => date ? new Date(date).toLocaleString('en-IN') : ''

    // Cross-tab sync: refresh when other tabs set these keys
    let storageHandler = null
    onMounted(() => {
      // If there's no auth token, the GET /api/violations endpoint will be secured (403).
      // In that case, show a helpful message and attempt the public fallback automatically.
      const token = localStorage.getItem('token')
      if (!token) {
        statusMessage.value = 'No auth token found. /api/violations is secured — attempting public fallback.'
        loadViolations(true)
      } else {
        loadViolations()
      }
      storageHandler = (e) => {
        const keysToRefresh = ['violationPaid', 'violationApproved', 'violationRejected', 'violationCreated']
        if (e && keysToRefresh.includes(e.key)) {
          console.log('Central detected cross-tab event', e.key, '- reloading violations')
          loadViolations()
        }
      }
      window.addEventListener('storage', storageHandler)
    })

    onBeforeUnmount(() => {
      if (storageHandler) window.removeEventListener('storage', storageHandler)
    })

    const viewOfficerDetails = (violation) => {
      alert(`Violation ${violation.id} — Officer: ${violation.officerName}`)
    }

    const debugFetch = async () => {
      try {
        const resp = await violationsAPI.getAll()
        console.log('DEBUG /api/violations response:', resp)
        const extracted = extractViolationsFromResponse(resp)
        alert(`Debug fetch: HTTP ${resp.status || 'unknown'} — found ${extracted.length} items (see console for full response)`)
      } catch (e) {
        console.error('DEBUG fetch failed', e)
        alert('Debug fetch failed: ' + (e?.response?.status || e.message || 'unknown'))
      }
    }

    return {
      logout,
      // central
      allViolations,
      centralView,
      centralApprovedRejected,
      centralAll,
      historyList,
      selectedStatus,
      violationCounts,
      filteredCentral,
      loadViolations,
      formatDateTime,
      viewOfficerDetails,
      approveCentralViolation,
      rejectCentralViolation,
      requestMoreInfoCentral,
      statusMessage
    }
  }
}
</script>

<style scoped>
/* Additional hover effects */
.transform:hover {
  transform: translateY(-2px);
}
</style>