<template>
  <div class="dashboard">
    <!-- Traffic Inspector Navigation Bar -->
    <nav style="
      background: #16a34a !important; 
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
        <h1 style="font-size: 1.5rem; font-weight: bold; margin: 0; color: white !important;">Traffic Inspector Dashboard</h1>
        <p style="margin: 0; color: white !important; opacity: 0.9;">District: {{ district }}</p>
      </div>
      <div style="display: flex; gap: 1rem; align-items: center;">
        <span style="color: white !important;">{{ authStore.user?.name || 'Inspector' }}</span>
        <button @click="logout" style="
          background: white !important; 
          color: #16a34a !important; 
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
      <!-- Supervisory Overview Cards -->
      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1rem; margin-bottom: 2rem;">
        <div class="supervisor-card" style="background: linear-gradient(135deg, #3b82f6, #1e40af);">
          <div class="card-icon">👮</div>
          <div class="card-content">
            <div class="card-number">{{ supervisionStats.sergeants }}</div>
            <div class="card-label">Sergeants Under Supervision</div>
            <div class="card-detail">{{ supervisionStats.onDuty }} currently on duty</div>
          </div>
        </div>
        
        <div class="supervisor-card" style="background: linear-gradient(135deg, #dc2626, #991b1b);">
          <div class="card-icon">🚨</div>
          <div class="card-content">
            <div class="card-number">{{ supervisionStats.violations }}</div>
            <div class="card-label">Violations</div>
            <div class="card-detail">{{ supervisionStats.pending }} pending approval</div>
          </div>
        </div>
        
        <div class="supervisor-card" style="background: linear-gradient(135deg, #16a34a, #15803d);">
          <div class="card-icon">💰</div>
          <div class="card-content">
            <div class="card-number">₹{{ supervisionStats.revenue.toLocaleString() }}</div>
            <div class="card-label">Revenue Generated</div>
            <div class="card-detail">{{ supervisionStats.collectionRate }}% collection rate</div>
          </div>
        </div>
        
        <div class="supervisor-card" style="background: linear-gradient(135deg, #f59e0b, #d97706);">
          <div class="card-icon">📊</div>
          <div class="card-content">
            <div class="card-number">{{ supervisionStats.efficiency }}%</div>
            <div class="card-label">Team Efficiency</div>
            <div class="card-detail">{{ supervisionStats.disputes }} disputes handled</div>
          </div>
        </div>
      </div>

      <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 2rem;">
        <!-- Main Content -->
        <div>
          <!-- Quick Actions for Inspector -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>⚡ Inspector Actions</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem;">
                <button @click="navigateTo('/violations/create')" class="inspector-action-btn">
                  <div class="btn-icon">🚨</div>
                  <div class="btn-text">Register Violation</div>
                </button>
                <button @click="navigateTo('/violation-approval')" class="inspector-action-btn">
                  <div class="btn-icon">✅</div>
                  <div class="btn-text">Approve Violations</div>
                </button>
                
              </div>
            </div>
          </div>

          <!-- Team Performance -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>👥 Team Performance</h3>
              <div style="display: flex; gap: 1rem;">
                <select v-model="selectedPeriod" class="form-control" style="width: auto;">
                  <option value="today">Today</option>
                  <option value="week">This Week</option>
                  <option value="month">This Month</option>
                </select>
                <button @click="refreshDashboard" class="btn-refresh">
                  🔄 Refresh Now
                </button>
                <button @click="exportPerformance" class="btn">📊 Export</button>
              </div>
            </div>
            <div class="card-body">
              <div style="overflow-x: auto;">
                <table style="width: 100%; border-collapse: collapse;">
                  <thead>
                    <tr style="background: #f8fafc; border-bottom: 2px solid #e5e7eb;">
                      <th style="padding: 0.75rem; text-align: left;">Officer</th>
                      <th style="padding: 0.75rem; text-align: center;">Violations</th>
                      <th style="padding: 0.75rem; text-align: center;">Fines Collected</th>
                      <th style="padding: 0.75rem; text-align: center;">Status</th>
                      <th style="padding: 0.75rem; text-align: center;">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="officer in teamPerformance" :key="officer.id" style="border-bottom: 1px solid #e5e7eb;">
                      <td style="padding: 0.75rem;">
                        <div style="font-weight: 500;">{{ officer.name }}</div>
                        <div style="font-size: 0.8rem; color: #6b7280;">{{ officer.badgeNumber }}</div>
                      </td>
                      <td style="padding: 0.75rem; text-align: center;">{{ officer.violations }}</td>
                      <td style="padding: 0.75rem; text-align: center;">₹{{ officer.finesCollected.toLocaleString() }}</td>
                      <td style="padding: 0.75rem; text-align: center;">
                        <span :class="getStatusClass(officer.status)" style="padding: 0.25rem 0.5rem; border-radius: 4px; font-size: 0.8rem;">
                          {{ officer.status }}
                        </span>
                      </td>
                      <td style="padding: 0.75rem; text-align: center;">
                        <div style="display: flex; gap: 0.5rem; justify-content: center;">
                          <button @click="viewOfficerDetails(officer)" class="btn-small">👁️</button>
                          <button @click="sendMessage(officer)" class="btn-small">💬</button>
                          <button @click="assignTask(officer)" class="btn-small">📋</button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <!-- Violations Requiring Approval -->
          <div class="card">
            <div class="card-header">
              <h3>✅ Violation Approval Center</h3>
              <div style="display: flex; gap: 1rem; align-items: center;">
                <select v-model="selectedStatus" @change="filterViolations" class="form-control" style="width: auto;">
                  <option value="all">All ({{ violationCounts.all }})</option>
                  <option value="pending">Pending ({{ violationCounts.pending }})</option>
                  <option value="approved">Approved ({{ violationCounts.approved }})</option>
                  <option value="rejected">Rejected ({{ violationCounts.rejected }})</option>
                </select>
                <button @click="refreshDashboard" class="btn-refresh">
                  🔄 Refresh
                </button>
                <div style="font-size: 0.9rem; color: #6b7280;">
                  Showing: {{ pendingApprovals.length }}
                </div>
              </div>
            </div>
            <div class="card-body">
              <div v-if="pendingApprovals.length === 0" style="text-align: center; color: #6b7280; padding: 2rem;">
                No violations found for this filter ✅
              </div>
              <div v-else>
                <div 
                  v-for="violation in pendingApprovals" 
                  :key="violation.id"
                  :style="{
                    border: '1px solid #e5e7eb',
                    borderRadius: '8px',
                    padding: '1rem',
                    marginBottom: '1rem',
                    background: violation.status === 'APPROVED' ? '#f0fdf4' : violation.status === 'REJECTED' ? '#fef2f2' : '#fffbeb'
                  }"
                >
                  <div style="display: flex; justify-content: space-between; align-items: start;">
                    <div style="flex: 1;">
                      <div style="display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.5rem;">
                        <div style="font-weight: 500;">{{ violation.vehicleNumber }} - {{ violation.violationType }}</div>
                        <span 
                          :style="{
                            padding: '0.25rem 0.5rem',
                            borderRadius: '4px',
                            fontSize: '0.75rem',
                            fontWeight: '600',
                            background: violation.status === 'APPROVED' ? '#dcfce7' : violation.status === 'REJECTED' ? '#fecaca' : '#fef3c7',
                            color: violation.status === 'APPROVED' ? '#166534' : violation.status === 'REJECTED' ? '#991b1b' : '#92400e'
                          }"
                        >
                          {{ violation.status }}
                        </span>
                      </div>
                      <div style="font-size: 0.9rem; color: #6b7280; margin: 0.5rem 0;">
                        Officer: {{ violation.officerName }} | Location: {{ violation.location }}
                      </div>
                      <div style="font-size: 0.8rem; color: #9ca3af;">{{ formatDateTime(violation.timestamp) }}</div>
                    </div>
                    <div v-if="violation.status === 'PENDING'" style="display: flex; gap: 0.5rem;">
                      <button @click="approveViolation(violation)" class="btn btn-success btn-small">✅ Approve</button>
                      <button @click="rejectViolation(violation)" class="btn btn-danger btn-small">❌ Reject</button>
                      <button @click="requestMoreInfo(violation)" class="btn btn-small">❓ More Info</button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Sidebar -->
        
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { violationsAPI, usersAPI } from '@/services/api'

export default {
  name: 'TrafficInspectorDashboardView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const district = ref('Central District')
    const selectedPeriod = ref('today')
    const selectedStatus = ref('pending')
    
    const supervisionStats = ref({
      sergeants: 0,
      onDuty: 0,
      violations: 0,
      pending: 0,
      revenue: 0,
      collectionRate: 0,
      efficiency: 0,
      disputes: 0
    })
    
  const teamPerformance = ref([])
  // Smart refresh state
  let refreshIntervalId = null
  let visibilityHandler = null
  let storageHandler = null
    
    const pendingApprovals = ref([])
    const allViolations = ref([])
    const violationCounts = ref({
      all: 0,
      pending: 0,
      approved: 0,
      rejected: 0
    })
    
    const sosAlerts = ref([
      {
        id: 1,
        officerName: 'Sergeant Kumar',
        location: 'Koregaon Park',
        timestamp: new Date(Date.now() - 15 * 60 * 1000),
        type: 'Backup Required'
      }
    ])
    
    const districtStats = ref({
      topViolationType: 'Speeding',
      hotspot: 'MG Road',
      repeatOffenders: 23
    })
    
    const disputes = ref([
      {
        id: 1,
        ticketNumber: 'TK202401001',
        reason: 'Vehicle not present at location',
        filedDate: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000)
      }
    ])
    
    const navigateTo = (path) => {
      router.push(path)
    }
    
    const logout = () => {
      authStore.logout()
      router.push('/login')
    }
    
    // Inspector-specific actions
    const showDisputeResolution = () => {
      // Simulate navigation to dispute resolution center
      router.push('/disputes')
    }

    const assignSpecialDuty = () => {
      // Simulate navigation to special duty assignment page
      router.push('/special-duty')
    }

    const escalateCase = () => {
      // Simulate navigation to case escalation page
      router.push('/case-escalation')
    }

    const generateReport = () => {
      // Simulate navigation to reports/analytics page
      router.push('/reports')
    }

    const exportPerformance = () => {
      // Export real team performance data to CSV
      if (teamPerformance.value.length === 0) {
        alert('No team performance data to export!')
        return
      }
      
      const csv = 'Officer,Badge Number,Violations,Fines Collected,Status\n' +
        teamPerformance.value.map(o => `${o.name},${o.badgeNumber},${o.violations},${o.finesCollected},${o.status}`).join('\n')
      const blob = new Blob([csv], { type: 'text/csv' })
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = `team_performance_${new Date().toISOString().split('T')[0]}.csv`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      alert('Team performance exported as CSV!')
    }
    
    const viewOfficerDetails = (officer) => {
      alert(`Viewing details for ${officer.name}`)
    }
    
    const sendMessage = (officer) => {
      alert(`Sending message to ${officer.name}`)
    }
    
    const assignTask = (officer) => {
      alert(`Assigning task to ${officer.name}`)
    }
    
    const approveViolation = (violation) => {
      // Approve only; payment becomes available publicly after approval
      violationsAPI.approve(violation.id)
        .then(() => {
          // Notify public tabs to refresh their pending payments list
          try {
            localStorage.setItem('violationApproved', Date.now().toString())
          } catch (e) { /* ignore */ }

          alert(`Violation ${violation.vehicleNumber || violation.id} approved. Payment is now available to public.`)
          // Reload violations to update counts and list
          refreshDashboard()
        })
        .catch((error) => {
          alert(`Failed to approve violation: ${error?.response?.data?.error || error.message}`)
        })
    }
    
    const rejectViolation = (violation) => {
      violationsAPI.reject(violation.id, 'Rejected by inspector')
        .then(() => {
          alert(`Violation ${violation.vehicleNumber || violation.id} rejected.`)
          // Reload violations to update counts and list
          loadViolations()
        })
        .catch((error) => {
          alert(`Failed to reject violation: ${error?.response?.data?.error || error.message}`)
        })
    }
    
    const requestMoreInfo = (violation) => {
      alert(`Requesting more information for violation ${violation.id}`)
    }
    
    const refreshAlerts = () => {
      alert('SOS alerts refreshed!')
    }
    
    const dispatchBackup = (alert) => {
      alert(`Dispatching backup to ${alert.location} for ${alert.officerName}`)
    }
    
    const contactOfficer = (alert) => {
      alert(`Calling ${alert.officerName}...`)
    }
    
    const resolveDispute = (dispute) => {
      alert(`Opening dispute resolution for ${dispute.ticketNumber}`)
    }
    
    const deployExtraPatrol = () => {
      alert('Deploying extra patrol units...')
    }
    
    const requestAdditionalOfficers = () => {
      alert('Requesting additional officers from Central...')
    }
    
    const scheduleTraining = () => {
      alert('Opening training schedule manager...')
    }
    
    const manageShifts = () => {
      alert('Opening shift management system...')
    }
    
    const formatTime = (date) => {
      return date.toLocaleTimeString('en-IN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
    }
    
    const formatDate = (date) => {
      return date.toLocaleDateString('en-IN')
    }
    
    const formatDateTime = (date) => {
      return date.toLocaleString('en-IN')
    }
    
    const getStatusClass = (status) => {
      const classes = {
        'On Duty': 'bg-green-100 text-green-800',
        'Off Duty': 'bg-gray-100 text-gray-800',
        'Break': 'bg-yellow-100 text-yellow-800'
      }
      return classes[status] || 'bg-gray-100 text-gray-800'
    }
    
    // Load all violations and compute counts
    const loadViolations = async () => {
      try {
        const response = await violationsAPI.getAll()
        const violations = response.data.violations || []
        
        // Store all violations (normalize fields)
        allViolations.value = violations.map(v => ({
          id: v.id || v._id || v.violationId,
          vehicleNumber: v.vehicleNumber || v.vehicleNo || v.vehicle_num || '',
          violationType: v.violationType || v.type || '',
          officerName: v.issuerName || v.officerName || 'Unknown',
          location: v.location || v.place || '',
          timestamp: v.createdAt ? new Date(v.createdAt) : (v.violationDateTime ? new Date(v.violationDateTime) : new Date()),
          amount: Number(v.fineAmount || v.amount || 0),
          status: (v.status || v.paymentStatus || '').toString().toUpperCase()
        }))
        
        // Calculate counts by status
        violationCounts.value = {
          all: allViolations.value.length,
          pending: allViolations.value.filter(v => v.status === 'PENDING').length,
          approved: allViolations.value.filter(v => v.status === 'APPROVED').length,
          rejected: allViolations.value.filter(v => v.status === 'REJECTED').length
        }
        
        // Filter based on selected status
        filterViolations()
      } catch (error) {
        console.error('Failed to load violations:', error)

        // If the backend forbids access to the protected `GET /api/violations` endpoint
        // try a safe public fallback: combine pending violations (open) and unpaid (public) lists.
        if (error?.response?.status === 403) {
          console.warn('GET /api/violations returned 403 — attempting public fallbacks (pending + unpaid)')
          try {
            const [pendingResp, unpaidResp] = await Promise.all([
              violationsAPI.getPending(),
              // force X-Public-User header to retrieve unpaid (controller accepts this header)
              violationsAPI.getUnpaidForce()
            ])

            const pending = pendingResp.data?.violations || []
            const unpaid = unpaidResp.data?.violations || []

            const merged = [...pending, ...unpaid]

            allViolations.value = merged.map(v => ({
              id: v.id || v._id || v.violationId,
              vehicleNumber: v.vehicleNumber || v.vehicleNo || v.vehicle_num || '',
              violationType: v.violationType || v.type || '',
              officerName: v.issuerName || v.officerName || (v.issuer && v.issuer.fullName) || 'Unknown',
              location: v.location || v.place || '',
              timestamp: v.createdAt ? new Date(v.createdAt) : (v.violationDateTime ? new Date(v.violationDateTime) : new Date()),
              amount: Number(v.fineAmount || v.amount || v.fine || 0),
              status: (v.status || v.paymentStatus || '').toString().toUpperCase()
            }))

            violationCounts.value = {
              all: allViolations.value.length,
              pending: allViolations.value.filter(v => v.status === 'PENDING').length,
              approved: allViolations.value.filter(v => v.status === 'APPROVED').length,
              rejected: allViolations.value.filter(v => v.status === 'REJECTED').length
            }

            // Apply selected status filter
            filterViolations()
            return
          } catch (fallbackErr) {
            console.error('Fallback fetch (pending/unpaid) failed:', fallbackErr)
          }
        }
      }
    }
    
    // Filter violations based on selected status
    const filterViolations = () => {
      if (selectedStatus.value === 'all') {
        pendingApprovals.value = allViolations.value
      } else {
        const statusMap = {
          'pending': 'PENDING',
          'approved': 'APPROVED',
          'rejected': 'REJECTED'
        }
        pendingApprovals.value = allViolations.value.filter(
          v => v.status === statusMap[selectedStatus.value]
        )
      }
    }
    
    // Load team performance (sergeants + include inspector's own performance)
    const loadTeamPerformance = async () => {
      try {
        // Fetch sergeants by role
        const response = await usersAPI.getByRole('traffic_sergeant')
        const sergeants = response.data.users || []  // Extract users array from response
        
        console.log('Fetched sergeants:', sergeants.length)
        
        // Fetch violations for each sergeant
        const performancePromises = sergeants.map(async (sergeant) => {
          try {
            const violationsResponse = await violationsAPI.getByIssuer(sergeant.id)
            const violations = violationsResponse.data.violations || []
            
            // Calculate fines collected (only from PAID violations)
            const finesCollected = violations
              .filter(v => v.paymentStatus === 'PAID')
              .reduce((sum, v) => sum + (v.fineAmount || v.amount || 0), 0)
            
            return {
              id: sergeant.id,
              name: sergeant.fullName || sergeant.username,
              badgeNumber: sergeant.badgeNumber || sergeant.id.substring(0, 6).toUpperCase(),
              violations: violations.length,
              finesCollected: finesCollected,
              status: sergeant.dutyStatus || 'Off Duty'
            }
          } catch (error) {
            console.error(`Failed to load violations for sergeant ${sergeant.id}:`, error)
            return {
              id: sergeant.id,
              name: sergeant.fullName || sergeant.username,
              badgeNumber: sergeant.badgeNumber || sergeant.id.substring(0, 6).toUpperCase(),
              violations: 0,
              finesCollected: 0,
              status: sergeant.dutyStatus || 'Off Duty'
            }
          }
        })
        
        const sergeantPerformance = await Promise.all(performancePromises)

        // Include the Inspector's own performance so payments for their tickets count in revenue
        let inspectorPerformance = []
        try {
          const currentUser = authStore.user
          if (currentUser && currentUser.id) {
            const inspectorViolationsResp = await violationsAPI.getByIssuer(currentUser.id)
            const myViolations = inspectorViolationsResp.data.violations || []
            const myFinesCollected = myViolations
              .filter(v => v.paymentStatus === 'PAID')
              .reduce((sum, v) => sum + (v.fineAmount || v.amount || 0), 0)
            inspectorPerformance = [{
              id: currentUser.id,
              name: (currentUser.name || currentUser.username || 'You') + ' (Inspector)',
              badgeNumber: (currentUser.id || '').substring(0, 6).toUpperCase(),
              violations: myViolations.length,
              finesCollected: myFinesCollected,
              status: 'On Duty'
            }]
          }
        } catch (e) {
          console.warn('Failed to load inspector performance, continuing without it:', e?.message || e)
        }

  // Combine inspector + sergeants for display and totals
  teamPerformance.value = [...inspectorPerformance, ...sergeantPerformance]
        
        // Update supervision stats
        supervisionStats.value.sergeants = sergeants.length
        supervisionStats.value.onDuty = sergeants.filter(s => s.dutyStatus === 'On Duty').length
        
        // Calculate total violations and revenue
        supervisionStats.value.violations = teamPerformance.value.reduce((sum, s) => sum + s.violations, 0)
        supervisionStats.value.revenue = teamPerformance.value.reduce((sum, s) => sum + s.finesCollected, 0)
        // Broadcast inspector revenue to other tabs (Central dashboard listens for this)
        try {
          localStorage.setItem('inspectorRevenue', JSON.stringify({ amount: supervisionStats.value.revenue, ts: Date.now() }))
        } catch (e) {
          // ignore storage errors (privacy mode, quota)
        }
        
        console.log('Team performance updated:', teamPerformance.value.length, 'sergeants')
      } catch (error) {
        console.error('Failed to load team performance:', error)

        // Fallback: if users API is unavailable or returns no sergeants, build team performance
        // from the violations we already loaded (group by issuerId/officerName).
        try {
          console.warn('Falling back to building team performance from violations data')

          // Ensure we have violations loaded
          const violationsList = allViolations.value || []

          // Group by issuer (use officerName as fallback key)
          const map = new Map()
          for (const v of violationsList) {
            const key = (v.officerId || v.issuerId || v.officerName || v.officer || 'unknown').toString()
            const name = v.officerName || v.issuerName || (v.issuer && v.issuer.fullName) || key
            const entry = map.get(key) || { id: key, name, badgeNumber: (key || '').substring(0,6).toUpperCase(), violations: 0, finesCollected: 0, status: 'Off Duty' }
            entry.violations += 1
            // treat status/amount fields conservatively
            const amt = Number(v.amount || v.fineAmount || v.fine || 0)
            if ((v.status === 'PAID') || (v.paymentStatus === 'PAID')) {
              entry.finesCollected += amt
            }
            map.set(key, entry)
          }

          const derived = Array.from(map.values())

          // Include inspector's own performance if available
          const currentUser = authStore.user
          if (currentUser && currentUser.id) {
            const myKey = currentUser.id
            const myEntry = derived.find(d => d.id === myKey)
            if (myEntry) {
              myEntry.name = (currentUser.name || currentUser.username || 'You') + ' (Inspector)'
              myEntry.badgeNumber = (currentUser.id || '').substring(0,6).toUpperCase()
              myEntry.status = 'On Duty'
            } else {
              // if inspector had no violations in this window, add a zero row
              derived.unshift({ id: myKey, name: (currentUser.name || currentUser.username || 'You') + ' (Inspector)', badgeNumber: (currentUser.id || '').substring(0,6).toUpperCase(), violations: 0, finesCollected: 0, status: 'On Duty' })
            }
          }

          teamPerformance.value = derived

          supervisionStats.value.sergeants = derived.length
          supervisionStats.value.onDuty = derived.filter(s => s.status === 'On Duty').length
          supervisionStats.value.violations = derived.reduce((sum, s) => sum + s.violations, 0)
          supervisionStats.value.revenue = derived.reduce((sum, s) => sum + s.finesCollected, 0)

          // Broadcast inspector revenue to other tabs (Central dashboard listens for this)
          try {
            localStorage.setItem('inspectorRevenue', JSON.stringify({ amount: supervisionStats.value.revenue, ts: Date.now() }))
          } catch (e) { /* ignore */ }

          console.log('Derived team performance from violations:', teamPerformance.value.length)
          return
        } catch (fallbackErr) {
          console.error('Failed to derive team performance from violations fallback:', fallbackErr)
        }
      }
    }
    
    // Refresh all dashboard data
    const refreshDashboard = async () => {
      await Promise.all([
        loadViolations(),
        loadTeamPerformance()
      ])
    }
    
    // Load data when component mounts
    onMounted(() => {
      // Initial load
      refreshDashboard()
      
      // Smart auto-refresh: Only refresh when tab is visible and every 10 seconds
      refreshIntervalId = setInterval(() => {
        // Only refresh if the page is visible (user is actively viewing it)
        if (document.visibilityState === 'visible') {
          console.log('Auto-refreshing dashboard data (page is visible)...')
          refreshDashboard()
        }
      }, 10000) // 10 seconds - reasonable refresh rate
      
      // Also refresh when user returns to the tab
      const handleVisibilityChange = () => {
        if (document.visibilityState === 'visible') {
          console.log('Tab became visible - refreshing dashboard...')
          refreshDashboard()
        }
      }
      document.addEventListener('visibilitychange', handleVisibilityChange)
      
      // Store the event handler so we can remove it later
      visibilityHandler = handleVisibilityChange

      // Listen for payment events from Public pages (cross-tab update)
      storageHandler = (e) => {
        if (e.key === 'violationPaid') {
          console.log('Payment detected from another tab - refreshing dashboard...')
          refreshDashboard()
        }
      }
      window.addEventListener('storage', storageHandler)
    })
    
    // Clean up interval when component unmounts
    onBeforeUnmount(() => {
      if (refreshIntervalId) {
        clearInterval(refreshIntervalId)
        console.log('Dashboard auto-refresh stopped')
      }
      if (visibilityHandler) {
        document.removeEventListener('visibilitychange', visibilityHandler)
      }
      if (storageHandler) {
        window.removeEventListener('storage', storageHandler)
      }
    })
    
    return {
      authStore,
      district,
      selectedPeriod,
      selectedStatus,
      supervisionStats,
      teamPerformance,
      pendingApprovals,
      violationCounts,
      sosAlerts,
      districtStats,
      disputes,
      navigateTo,
      logout,
      showDisputeResolution,
      assignSpecialDuty,
      escalateCase,
      generateReport,
      exportPerformance,
      viewOfficerDetails,
      sendMessage,
      assignTask,
      approveViolation,
      rejectViolation,
      requestMoreInfo,
      refreshAlerts,
      dispatchBackup,
      contactOfficer,
      resolveDispute,
      deployExtraPatrol,
      requestAdditionalOfficers,
      scheduleTraining,
      manageShifts,
      formatTime,
      formatDate,
      formatDateTime,
      getStatusClass,
      filterViolations,
      refreshDashboard
    }
  }
}
</script>

<style scoped>
.supervisor-card {
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

.inspector-action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.inspector-action-btn:hover {
  background: #e2e8f0;
  transform: translateY(-1px);
}

.btn-icon {
  font-size: 1.5rem;
}

.btn-text {
  font-size: 0.9rem;
  font-weight: 500;
}

.btn {
  padding: 0.5rem 1rem;
  background: #2563eb;
  color: white;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
  transition: background 0.2s;
}

.btn:hover {
  background: #1e40af;
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

.btn-refresh {
  padding: 0.5rem 0.75rem;
  font-size: 0.875rem;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.2s;
}

.btn-refresh:hover {
  background: #e5e7eb;
}

.btn-success {
  background: #dcfce7;
  color: #166534;
  border-color: #bbf7d0;
}

.btn-danger {
  background: #fecaca;
  color: #dc2626;
  border-color: #f87171;
}
</style>