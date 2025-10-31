<template>
  <div>
    <!-- Navigation -->
    <nav style="background: #7c3aed; color: white; padding: 1rem; display: flex; justify-content: space-between; align-items: center;">
      <div style="display: flex; align-items: center; gap: 1rem;">
        <button @click="goBack" style="background: rgba(255,255,255,0.2); border: none; color: white; padding: 0.5rem; border-radius: 4px; cursor: pointer;">
          ← Back
        </button>
        <div style="font-size: 1.5rem; font-weight: bold;">
          ⚖️ Dispute Resolution Center
        </div>
      </div>
      <div style="font-size: 0.9rem;">
        Officer: {{ authStore.user?.name || 'Admin' }}
      </div>
    </nav>

    <div class="dashboard">
      <!-- Statistics Cards -->
      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-bottom: 2rem;">
        <div class="stat-card" style="background: linear-gradient(135deg, #f59e0b, #d97706);">
          <div class="stat-icon">⏳</div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.pendingDisputes }}</div>
            <div class="stat-label">Pending Disputes</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #16a34a, #15803d);">
          <div class="stat-icon">✅</div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.resolvedToday }}</div>
            <div class="stat-label">Resolved Today</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #dc2626, #991b1b);">
          <div class="stat-icon">🔥</div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.urgentDisputes }}</div>
            <div class="stat-label">Urgent Cases</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #3b82f6, #1e40af);">
          <div class="stat-icon">⏱️</div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.avgResolutionTime }}</div>
            <div class="stat-label">Avg Resolution Time</div>
          </div>
        </div>
      </div>

      <!-- Filter Controls -->
      <div class="card" style="margin-bottom: 2rem;">
        <div class="card-body">
          <div style="display: flex; gap: 1rem; align-items: center; flex-wrap: wrap;">
            <select v-model="filters.status" class="form-control" style="width: auto;">
              <option value="all">All Status</option>
              <option value="pending">Pending</option>
              <option value="under_review">Under Review</option>
              <option value="resolved">Resolved</option>
              <option value="rejected">Rejected</option>
            </select>
            <select v-model="filters.priority" class="form-control" style="width: auto;">
              <option value="all">All Priority</option>
              <option value="urgent">Urgent</option>
              <option value="high">High</option>
              <option value="medium">Medium</option>
              <option value="low">Low</option>
            </select>
            <select v-model="filters.type" class="form-control" style="width: auto;">
              <option value="all">All Types</option>
              <option value="incorrect_fine">Incorrect Fine</option>
              <option value="vehicle_not_present">Vehicle Not Present</option>
              <option value="emergency_situation">Emergency Situation</option>
              <option value="technical_error">Technical Error</option>
              <option value="officer_misconduct">Officer Misconduct</option>
            </select>
            <input 
              v-model="filters.search" 
              placeholder="Search by ticket number..." 
              class="form-control" 
              style="width: 250px;"
            >
            <button @click="refreshDisputes" class="btn btn-primary">🔄 Refresh</button>
          </div>
        </div>
      </div>

      <!-- Disputes List -->
      <div class="card">
        <div class="card-header">
          <h3>📋 Active Disputes ({{ filteredDisputes.length }})</h3>
          <div style="display: flex; gap: 1rem;">
            <button @click="bulkResolve" class="btn btn-success" :disabled="!selectedDisputes.length">
              ✅ Bulk Resolve ({{ selectedDisputes.length }})
            </button>
            <button @click="exportDisputes" class="btn btn-secondary">
              📊 Export Data
            </button>
          </div>
        </div>
        <div class="card-body">
          <div v-if="filteredDisputes.length === 0" style="text-align: center; color: #6b7280; padding: 3rem;">
            <div style="font-size: 3rem; margin-bottom: 1rem;">⚖️</div>
            <div style="font-size: 1.2rem; margin-bottom: 0.5rem;">No disputes found</div>
            <div>All cases are resolved or try adjusting your filters</div>
          </div>
          
          <div v-else>
            <div 
              v-for="dispute in paginatedDisputes" 
              :key="dispute.id"
              :style="`border: 2px solid ${getPriorityColor(dispute.priority)}; border-radius: 8px; padding: 1.5rem; margin-bottom: 1rem; background: ${getPriorityBg(dispute.priority)};`"
            >
              <div style="display: flex; justify-content: space-between; align-items: start;">
                <div style="flex: 1;">
                  <!-- Dispute Header -->
                  <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem;">
                    <input 
                      type="checkbox" 
                      :value="dispute.id" 
                      v-model="selectedDisputes"
                      style="transform: scale(1.2);"
                    >
                    <div style="font-size: 1.2rem; font-weight: bold;">{{ dispute.ticketNumber }}</div>
                    <span :style="`padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.8rem; font-weight: 500; background: ${getPriorityColor(dispute.priority)}; color: white;`">
                      {{ dispute.priority.toUpperCase() }}
                    </span>
                    <span :style="`padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.8rem; font-weight: 500; background: ${getStatusColor(dispute.status)}; color: white;`">
                      {{ dispute.status.replace('_', ' ').toUpperCase() }}
                    </span>
                  </div>

                  <!-- Dispute Details -->
                  <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1rem; margin-bottom: 1rem;">
                    <div>
                      <div style="font-size: 0.9rem; color: #6b7280; margin-bottom: 0.25rem;">Dispute Type</div>
                      <div style="font-weight: 500;">{{ dispute.type.replace('_', ' ').toUpperCase() }}</div>
                    </div>
                    <div>
                      <div style="font-size: 0.9rem; color: #6b7280; margin-bottom: 0.25rem;">Complainant</div>
                      <div>👤 {{ dispute.complainantName }}</div>
                      <div style="font-size: 0.8rem; color: #6b7280;">📞 {{ dispute.complainantPhone }}</div>
                    </div>
                    <div>
                      <div style="font-size: 0.9rem; color: #6b7280; margin-bottom: 0.25rem;">Original Fine</div>
                      <div style="font-weight: bold; color: #dc2626;">₹{{ dispute.originalFine.toLocaleString() }}</div>
                    </div>
                    <div>
                      <div style="font-size: 0.9rem; color: #6b7280; margin-bottom: 0.25rem;">Days Pending</div>
                      <div :style="`font-weight: bold; color: ${getDaysPendingColor(dispute.daysPending)}`">
                        {{ dispute.daysPending }} days
                      </div>
                    </div>
                  </div>

                  <!-- Dispute Reason -->
                  <div style="background: rgba(255,255,255,0.5); padding: 1rem; border-radius: 6px; margin-bottom: 1rem;">
                    <div style="font-size: 0.9rem; color: #6b7280; margin-bottom: 0.5rem;">Dispute Reason</div>
                    <div>{{ dispute.reason }}</div>
                  </div>

                  <!-- Evidence & Documents -->
                  <div v-if="dispute.documents.length" style="margin-bottom: 1rem;">
                    <div style="font-size: 0.9rem; color: #6b7280; margin-bottom: 0.5rem;">Supporting Documents</div>
                    <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                      <span 
                        v-for="doc in dispute.documents" 
                        :key="doc" 
                        @click="viewDocument(doc)"
                        style="padding: 0.25rem 0.5rem; background: #3b82f6; color: white; border-radius: 4px; font-size: 0.8rem; cursor: pointer;"
                      >
                        📄 {{ doc }}
                      </span>
                    </div>
                  </div>

                  <!-- Resolution Notes (if any) -->
                  <div v-if="dispute.resolutionNotes" style="background: #f0fdf4; padding: 1rem; border-radius: 6px; margin-bottom: 1rem; border-left: 4px solid #16a34a;">
                    <div style="font-size: 0.9rem; color: #15803d; margin-bottom: 0.5rem; font-weight: 500;">Resolution Notes</div>
                    <div style="color: #166534;">{{ dispute.resolutionNotes }}</div>
                  </div>

                  <!-- Timestamps -->
                  <div style="font-size: 0.8rem; color: #9ca3af;">
                    Filed: {{ formatDateTime(dispute.filedDate) }}
                    <span v-if="dispute.lastUpdated">
                      | Updated: {{ formatDateTime(dispute.lastUpdated) }}
                    </span>
                  </div>
                </div>

                <!-- Action Buttons -->
                <div style="display: flex; flex-direction: column; gap: 0.5rem; min-width: 140px;">
                  <button 
                    v-if="dispute.status === 'pending'" 
                    @click="acceptDispute(dispute)" 
                    class="btn btn-success btn-small"
                  >
                    ✅ Accept
                  </button>
                  <button 
                    v-if="dispute.status === 'pending'" 
                    @click="rejectDispute(dispute)" 
                    class="btn btn-danger btn-small"
                  >
                    ❌ Reject
                  </button>
                  <button 
                    v-if="dispute.status === 'pending'" 
                    @click="startReview(dispute)" 
                    class="btn btn-warning btn-small"
                  >
                    🔍 Start Review
                  </button>
                  <button @click="viewFullDetails(dispute)" class="btn btn-primary btn-small">
                    👁️ Full Details
                  </button>
                  <button @click="contactComplainant(dispute)" class="btn btn-secondary btn-small">
                    📞 Contact
                  </button>
                  <button @click="escalateDispute(dispute)" class="btn btn-warning btn-small">
                    ⬆️ Escalate
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Pagination -->
          <div v-if="totalPages > 1" style="display: flex; justify-content: center; align-items: center; gap: 1rem; margin-top: 2rem;">
            <button 
              @click="currentPage--" 
              :disabled="currentPage === 1"
              class="btn btn-secondary btn-small"
            >
              ← Previous
            </button>
            <span>Page {{ currentPage }} of {{ totalPages }}</span>
            <button 
              @click="currentPage++" 
              :disabled="currentPage === totalPages"
              class="btn btn-secondary btn-small"
            >
              Next →
            </button>
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
import { showToast, showConfirm, showInput } from '@/composables/interactionService'

export default {
  name: 'DisputeResolutionView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const currentPage = ref(1)
    const itemsPerPage = ref(4)
    const selectedDisputes = ref([])
    
    const filters = ref({
      status: 'all',
      priority: 'all',
      type: 'all',
      search: ''
    })
    
    const stats = ref({
      pendingDisputes: 18,
      resolvedToday: 7,
      urgentDisputes: 3,
      avgResolutionTime: '2.5 days'
    })
    
    const disputes = ref([
      {
        id: 1,
        ticketNumber: 'TK202501001',
        type: 'vehicle_not_present',
        priority: 'urgent',
        status: 'pending',
        complainantName: 'Rajesh Kumar',
        complainantPhone: '+91-9876543210',
        originalFine: 5000,
        reason: 'My vehicle was in the garage for repair on the date mentioned in the ticket. I have repair invoice as proof.',
        documents: ['Garage Invoice', 'Vehicle Service Receipt', 'Identity Proof'],
        filedDate: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000),
        lastUpdated: null,
        resolutionNotes: null,
        daysPending: 3
      },
      {
        id: 2,
        ticketNumber: 'TK202501005',
        type: 'emergency_situation',
        priority: 'high',
        status: 'under_review',
        complainantName: 'Priya Sharma',
        complainantPhone: '+91-9876543211',
        originalFine: 2000,
        reason: 'I was rushing my father to hospital due to heart attack. This was a genuine medical emergency.',
        documents: ['Hospital Admission Receipt', 'Medical Emergency Certificate', 'Doctor\'s Note'],
        filedDate: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000),
        lastUpdated: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000),
        resolutionNotes: 'Documents verified. Medical emergency confirmed. Case under final review.',
        daysPending: 5
      },
      {
        id: 3,
        ticketNumber: 'TK202501010',
        type: 'incorrect_fine',
        priority: 'medium',
        status: 'pending',
        complainantName: 'Amit Patel',
        complainantPhone: '+91-9876543212',
        originalFine: 1500,
        reason: 'The fine amount seems incorrect. Similar violations in the area have different penalty amounts.',
        documents: ['Previous Ticket Copy', 'Rate Chart Screenshot'],
        filedDate: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000),
        lastUpdated: null,
        resolutionNotes: null,
        daysPending: 2
      },
      {
        id: 4,
        ticketNumber: 'TK202501008',
        type: 'technical_error',
        priority: 'low',
        status: 'resolved',
        complainantName: 'Sunita Singh',
        complainantPhone: '+91-9876543213',
        originalFine: 1000,
        reason: 'GPS location in ticket shows wrong place. I was not at the mentioned location.',
        documents: ['GPS History', 'Google Maps Timeline'],
        filedDate: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000),
        lastUpdated: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000),
        resolutionNotes: 'Technical error confirmed. GPS calibration issue identified. Fine waived.',
        daysPending: 6
      },
      {
        id: 5,
        ticketNumber: 'TK202501012',
        type: 'officer_misconduct',
        priority: 'urgent',
        status: 'pending',
        complainantName: 'Deepak Gupta',
        complainantPhone: '+91-9876543214',
        originalFine: 3000,
        reason: 'The officer was rude and did not follow proper procedure. No proper explanation was given.',
        documents: ['Video Recording', 'Witness Statement'],
        filedDate: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000),
        lastUpdated: null,
        resolutionNotes: null,
        daysPending: 1
      }
    ])
    
    const filteredDisputes = computed(() => {
      return disputes.value.filter(dispute => {
        const matchesStatus = filters.value.status === 'all' || dispute.status === filters.value.status
        const matchesPriority = filters.value.priority === 'all' || dispute.priority === filters.value.priority
        const matchesType = filters.value.type === 'all' || dispute.type === filters.value.type
        const matchesSearch = !filters.value.search || 
          dispute.ticketNumber.toLowerCase().includes(filters.value.search.toLowerCase()) ||
          dispute.complainantName.toLowerCase().includes(filters.value.search.toLowerCase())
        
        return matchesStatus && matchesPriority && matchesType && matchesSearch
      })
    })
    
    const paginatedDisputes = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage.value
      return filteredDisputes.value.slice(start, start + itemsPerPage.value)
    })
    
    const totalPages = computed(() => {
      return Math.ceil(filteredDisputes.value.length / itemsPerPage.value)
    })
    
    const goBack = () => {
      router.go(-1)
    }
    
    const refreshDisputes = () => {
      showToast({ message: 'Refreshing disputes list...', type: 'info' })
    }
    
    const bulkResolve = () => {
      if (selectedDisputes.value.length > 0) {
        showToast({ message: `Bulk resolving ${selectedDisputes.value.length} disputes...`, type: 'info' })
      }
    }
    
    const exportDisputes = () => {
      showToast({ message: 'Exporting dispute data to Excel...', type: 'info' })
    }
    
    const acceptDispute = async (dispute) => {
      const ok = await showConfirm({ title: 'Accept Dispute', message: `Accept dispute for ticket ${dispute.ticketNumber}?\nThis will waive the fine of ₹${dispute.originalFine}` })
      if (!ok) return
      dispute.status = 'resolved'
      dispute.lastUpdated = new Date()
      dispute.resolutionNotes = 'Dispute accepted. Fine waived due to valid reason.'
      showToast({ message: 'Dispute accepted and fine waived!', type: 'success' })
    }
    
    const rejectDispute = async (dispute) => {
      const reason = await showInput({ title: 'Reject Dispute', message: `Reject dispute for ticket ${dispute.ticketNumber}?\nPlease provide rejection reason:`, placeholder: '' })
      if (!reason) return
      dispute.status = 'rejected'
      dispute.lastUpdated = new Date()
      dispute.resolutionNotes = `Dispute rejected. Reason: ${reason}`
      showToast({ message: 'Dispute rejected!', type: 'info' })
    }
    
    const startReview = (dispute) => {
      dispute.status = 'under_review'
      dispute.lastUpdated = new Date()
      showToast({ message: `Started detailed review for ticket ${dispute.ticketNumber}`, type: 'info' })
    }
    
    const viewFullDetails = (dispute) => {
      showToast({ message: `Opening detailed view for dispute ${dispute.id}`, type: 'info' })
    }
    
    const contactComplainant = (dispute) => {
      showToast({ message: `Contacting ${dispute.complainantName} at ${dispute.complainantPhone}`, type: 'info' })
    }
    
    const escalateDispute = (dispute) => {
      showToast({ message: `Escalating dispute ${dispute.ticketNumber} to senior authority`, type: 'warning' })
    }
    
    const viewDocument = (document) => {
      showToast({ message: `Opening document: ${document}`, type: 'info' })
    }
    
    const formatDateTime = (date) => {
      return date.toLocaleString('en-IN')
    }
    
    const getPriorityColor = (priority) => {
      const colors = {
        'urgent': '#dc2626',
        'high': '#f59e0b',
        'medium': '#3b82f6',
        'low': '#16a34a'
      }
      return colors[priority] || '#6b7280'
    }
    
    const getPriorityBg = (priority) => {
      const backgrounds = {
        'urgent': '#fef2f2',
        'high': '#fefce8',
        'medium': '#eff6ff',
        'low': '#f0fdf4'
      }
      return backgrounds[priority] || '#f9fafb'
    }
    
    const getStatusColor = (status) => {
      const colors = {
        'pending': '#f59e0b',
        'under_review': '#3b82f6',
        'resolved': '#16a34a',
        'rejected': '#dc2626'
      }
      return colors[status] || '#6b7280'
    }
    
    const getDaysPendingColor = (days) => {
      if (days >= 7) return '#dc2626'
      if (days >= 3) return '#f59e0b'
      return '#16a34a'
    }
    
    return {
      authStore,
      currentPage,
      itemsPerPage,
      selectedDisputes,
      filters,
      stats,
      disputes,
      filteredDisputes,
      paginatedDisputes,
      totalPages,
      goBack,
      refreshDisputes,
      bulkResolve,
      exportDisputes,
      acceptDispute,
      rejectDispute,
      startReview,
      viewFullDetails,
      contactComplainant,
      escalateDispute,
      viewDocument,
      formatDateTime,
      getPriorityColor,
      getPriorityBg,
      getStatusColor,
      getDaysPendingColor
    }
  }
}
</script>

<style scoped>
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

.btn-small {
  padding: 0.5rem 0.75rem;
  font-size: 0.8rem;
  width: 100%;
  margin-bottom: 0.25rem;
}

.btn-success {
  background: #dcfce7;
  color: #166534;
  border: 1px solid #bbf7d0;
}

.btn-danger {
  background: #fecaca;
  color: #dc2626;
  border: 1px solid #f87171;
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