<template>
  <div class="dashboard">
    <nav style="background: #2563eb; color: white; padding: 1rem; display: flex; justify-content: space-between; align-items: center;">
      <div>
        <h1 style="font-size: 1.5rem; font-weight: bold;">CopFriendly ESeva Dashboard</h1>
        <p style="font-size: 0.875rem;">{{ userRole }}</p>
      </div>
      <div style="display: flex; gap: 1rem; align-items: center;">
        <span>{{ userName }}</span>
        <button @click="logout" style="background: white; color: #2563eb; padding: 0.5rem 1rem; border: none; border-radius: 0.25rem; cursor: pointer;">
          Logout
        </button>
      </div>
    </nav>

    <!-- Main Content -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
    <!-- Welcome Banner -->
    <div class="bg-white shadow-lg rounded-xl p-6">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-3xl font-bold text-gray-900 mb-2">Welcome back, {{ userName }}</h2>
          <p class="text-gray-600">Here's what's happening with your traffic operations today.</p>
        </div>
        <div class="text-6xl opacity-20">👋</div>
      </div>
    </div>

    <!-- Statistics Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <!-- Today's Violations -->
      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-red-100 rounded-full flex items-center justify-center">
                <svg class="w-5 h-5 text-red-600" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M13.477 14.89A6 6 0 015.11 6.524l8.367 8.368zm1.414-1.414L6.524 5.11a6 6 0 018.367 8.367zM18 10a8 8 0 11-16 0 8 8 0 0116 0z" clip-rule="evenodd" />
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Today's Violations</dt>
                <dd class="text-lg font-medium text-gray-900">{{ stats.todayViolations }}</dd>
              </dl>
            </div>
          </div>
        </div>
        <div class="bg-gray-50 px-5 py-3">
          <div class="text-sm">
            <router-link to="/violations" class="font-medium text-cyan-700 hover:text-cyan-900">
              View all violations
            </router-link>
          </div>
        </div>
      </div>

      <!-- Pending Tickets -->
      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-yellow-100 rounded-full flex items-center justify-center">
                <svg class="w-5 h-5 text-yellow-600" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Pending Tickets</dt>
                <dd class="text-lg font-medium text-gray-900">{{ stats.pendingTickets }}</dd>
              </dl>
            </div>
          </div>
        </div>
        <div class="bg-gray-50 px-5 py-3">
          <div class="text-sm">
            <router-link to="/violations?status=pending" class="font-medium text-cyan-700 hover:text-cyan-900">
              View pending tickets
            </router-link>
          </div>
        </div>
      </div>

      <!-- Active Emergencies -->
      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-red-100 rounded-full flex items-center justify-center">
                <svg class="w-5 h-5 text-red-600" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Active Emergencies</dt>
                <dd class="text-lg font-medium text-gray-900">{{ stats.activeEmergencies }}</dd>
              </dl>
            </div>
          </div>
        </div>
        <div class="bg-gray-50 px-5 py-3">
          <div class="text-sm">
            <router-link to="/emergencies" class="font-medium text-cyan-700 hover:text-cyan-900">
              View emergencies
            </router-link>
          </div>
        </div>
      </div>

      <!-- Revenue Today -->
      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="p-5">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center">
                <svg class="w-5 h-5 text-green-600" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M8.433 7.418c.155-.103.346-.196.567-.267v1.698a2.305 2.305 0 01-.567-.267C8.07 8.34 8 8.114 8 8c0-.114.07-.34.433-.582zM11 12.849v-1.698c.22.071.412.164.567.267.364.243.433.468.433.582 0 .114-.07.34-.433.582a2.305 2.305 0 01-.567.267z" />
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-13a1 1 0 10-2 0v.092a4.535 4.535 0 00-1.676.662C6.602 6.234 6 7.009 6 8c0 .99.602 1.765 1.324 2.246.48.32 1.054.545 1.676.662v1.941c-.391-.127-.68-.317-.843-.504a1 1 0 10-1.51 1.31c.562.649 1.413 1.076 2.353 1.253V15a1 1 0 102 0v-.092a4.535 4.535 0 001.676-.662C13.398 13.766 14 12.991 14 12c0-.99-.602-1.765-1.324-2.246A4.535 4.535 0 0011 9.092V7.151c.391.127.68.317.843.504a1 1 0 101.511-1.31c-.563-.649-1.413-1.076-2.354-1.253V5z" clip-rule="evenodd" />
                </svg>
              </div>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-gray-500 truncate">Today's Revenue</dt>
                <dd class="text-lg font-medium text-gray-900">{{ formatCurrency(stats.todayRevenue) }}</dd>
              </dl>
            </div>
          </div>
        </div>
        <div class="bg-gray-50 px-5 py-3">
          <div class="text-sm">
            <button class="font-medium text-cyan-700 hover:text-cyan-900">
              View revenue report
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Recent Activity and Quick Actions -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Recent Violations -->
      <div class="bg-white shadow rounded-lg">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-medium text-gray-900">Recent Violations</h3>
        </div>
        <div class="divide-y divide-gray-200">
          <div v-if="loading" class="p-6 text-center">
            <div class="spinner mx-auto"></div>
            <p class="mt-2 text-sm text-gray-500">Loading recent violations...</p>
          </div>
          
          <div v-else-if="recentViolations.length === 0" class="p-6 text-center">
            <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <p class="mt-2 text-sm text-gray-500">No recent violations</p>
          </div>
          
          <div v-else>
            <div 
              v-for="violation in recentViolations" 
              :key="violation.id"
              class="p-6 hover:bg-gray-50 cursor-pointer"
              @click="viewViolation(violation.id)"
            >
              <div class="flex items-center justify-between">
                <div class="flex-1">
                  <p class="text-sm font-medium text-gray-900">{{ violation.violatorName }}</p>
                  <p class="text-sm text-gray-500">{{ violation.violationType }} • {{ violation.vehicleNumber }}</p>
                  <p class="text-xs text-gray-400">{{ formatDateTime(violation.createdAt) }}</p>
                </div>
                <div>
                  <span :class="getStatusBadgeClass(violation.status)">
                    {{ violation.status }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="px-6 py-3 bg-gray-50 border-t border-gray-200">
          <router-link 
            to="/violations" 
            class="text-sm font-medium text-primary-600 hover:text-primary-500"
          >
            View all violations →
          </router-link>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="bg-white shadow rounded-lg">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-medium text-gray-900">Quick Actions</h3>
        </div>
        <div class="p-6 space-y-4">
          <!-- Register Violation -->
          <router-link
            v-if="canRegisterViolation"
            to="/violations/create"
            class="flex items-center p-4 bg-blue-50 hover:bg-blue-100 rounded-lg transition-colors"
          >
            <div class="flex-shrink-0">
              <div class="w-10 h-10 bg-blue-500 rounded-lg flex items-center justify-center">
                <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                </svg>
              </div>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-900">Register Violation</p>
              <p class="text-sm text-gray-500">Add a new traffic violation</p>
            </div>
          </router-link>

          <!-- Register Emergency -->
          <router-link
            v-if="canRegisterEmergency"
            to="/emergencies/create"
            class="flex items-center p-4 bg-red-50 hover:bg-red-100 rounded-lg transition-colors"
          >
            <div class="flex-shrink-0">
              <div class="w-10 h-10 bg-red-500 rounded-lg flex items-center justify-center">
                <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.464 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z" />
                </svg>
              </div>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-900">Register Emergency</p>
              <p class="text-sm text-gray-500">Report an emergency situation</p>
            </div>
          </router-link>

          <!-- Generate Reports -->
          <button
            v-if="canGenerateReports"
            @click="generateReport"
            class="flex items-center w-full p-4 bg-green-50 hover:bg-green-100 rounded-lg transition-colors"
          >
            <div class="flex-shrink-0">
              <div class="w-10 h-10 bg-green-500 rounded-lg flex items-center justify-center">
                <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              </div>
            </div>
            <div class="ml-4 text-left">
              <p class="text-sm font-medium text-gray-900">Generate Reports</p>
              <p class="text-sm text-gray-500">View analytics and reports</p>
            </div>
          </button>

          <!-- Scan QR Code -->
          <button
            @click="scanQRCode"
            class="flex items-center w-full p-4 bg-purple-50 hover:bg-purple-100 rounded-lg transition-colors"
          >
            <div class="flex-shrink-0">
              <div class="w-10 h-10 bg-purple-500 rounded-lg flex items-center justify-center">
                <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 20h4M4 12h4m12 0h.01M5 8h2a1 1 0 001-1V5a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm12 0h2a1 1 0 001-1V5a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1zM5 20h2a1 1 0 001-1v-2a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1z" />
                </svg>
              </div>
            </div>
            <div class="ml-4 text-left">
              <p class="text-sm font-medium text-gray-900">Scan QR Code</p>
              <p class="text-sm text-gray-500">Quick violation lookup</p>
            </div>
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
import { useViolationsStore } from '@/stores/violations'
import { useEmergenciesStore } from '@/stores/emergencies'
import { dashboardAPI } from '@/services/api'
import { dateUtils, formatters, colorUtils } from '@/utils/helpers'

export default {
  name: 'DashboardView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const violationsStore = useViolationsStore()
    const emergenciesStore = useEmergenciesStore()

    // Reactive state
    const loading = ref(false)
    const stats = ref({
      todayViolations: 0,
      pendingTickets: 0,
      activeEmergencies: 0,
      todayRevenue: 0
    })
    const recentViolations = ref([])
    const currentTime = ref('')
    const currentDate = ref('')

    // Computed properties
    const userName = computed(() => authStore.userName)
    const userRole = computed(() => authStore.userRole)
    
    const canRegisterViolation = computed(() => 
      ['traffic_sergeant', 'traffic_inspector'].includes(userRole.value)
    )
    
    const canRegisterEmergency = computed(() => 
      ['traffic_sergeant', 'traffic_inspector'].includes(userRole.value)
    )
    
    const canGenerateReports = computed(() => 
      ['traffic_central_team', 'medical_emergency_central'].includes(userRole.value)
    )

    // Methods
    const updateTime = () => {
      const now = new Date()
      currentTime.value = dateUtils.formatTime(now)
      currentDate.value = dateUtils.formatDate(now)
    }

    const fetchDashboardData = async () => {
      try {
        loading.value = true
        
        // Fetch overview stats
        const response = await dashboardAPI.getOverview()
        if (response.data.success) {
          stats.value = response.data.stats
        }
        
        // Fetch recent violations for traffic officers
        if (canRegisterViolation.value) {
          await violationsStore.fetchViolations({ limit: 5, sort: '-createdAt' })
          recentViolations.value = violationsStore.violations.slice(0, 5)
        }
        
      } catch (error) {
        console.error('Dashboard fetch error:', error)
        if (window.$toast) {
          window.$toast.error('Failed to load dashboard data')
        }
      } finally {
        loading.value = false
      }
    }

    const formatCurrency = (amount) => {
      return formatters.currency(amount)
    }

    const formatDateTime = (date) => {
      return dateUtils.formatDateTime(date)
    }

    const getStatusBadgeClass = (status) => {
      const baseClasses = 'inline-flex px-2 py-1 text-xs font-semibold rounded-full'
      const statusClasses = colorUtils.getStatusColor(status)
      return `${baseClasses} ${statusClasses}`
    }

    const viewViolation = (id) => {
      router.push(`/violations/${id}`)
    }

    const generateReport = () => {
      if (window.$toast) {
        window.$toast.info('Coming Soon', 'Report generation feature will be available soon')
      }
    }

    const scanQRCode = () => {
      if (window.$toast) {
        window.$toast.info('Coming Soon', 'QR code scanning feature will be available soon')
      }
    }

    const logout = () => {
      authStore.logout()
      router.push('/login')
    }

    // Lifecycle
    onMounted(() => {
      updateTime()
      fetchDashboardData()
      
      // Update time every minute
      setInterval(updateTime, 60000)
    })

    return {
      loading,
      stats,
      recentViolations,
      currentTime,
      currentDate,
      userName,
      userRole,
      canRegisterViolation,
      canRegisterEmergency,
      canGenerateReports,
      formatCurrency,
      formatDateTime,
      getStatusBadgeClass,
      viewViolation,
      generateReport,
      scanQRCode,
      logout
    }
  }
}
</script>

<style scoped>
/* Additional component-specific styles */
</style>