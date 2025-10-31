<template>
  <div class="dashboard">
    <!-- Simple Dashboard Navigation Bar -->
    <nav style="
      background: #2563eb !important; 
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
        <h1 style="font-size: 1.5rem; font-weight: bold; margin: 0; color: white !important;">Simple Dashboard</h1>
        <p style="margin: 0; color: white !important; opacity: 0.9;">{{ userRole }}</p>
      </div>
      
      <div style="display: flex; gap: 1rem; align-items: center;">
        <span style="color: white !important;">{{ userName }}</span>
        <button @click="logout" style="
          background: white !important; 
          color: #2563eb !important; 
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
      <h1 style="font-size: 2rem; font-weight: bold; margin-bottom: 2rem;">Dashboard - {{ userRole }}</h1>
      
      <!-- Public Actions (visible to PUBLIC users) -->
      <div v-if="isPublicUser" style="margin-top: 2rem;">
        <div class="card">
          <div class="card-header">
            <h3>Public Actions</h3>
          </div>
          <div class="card-body">
            <div style="display:flex; gap:1rem; flex-wrap:wrap;">
              <button @click="navigateTo('/public/payments')" class="btn btn-success">
                💳 Pending Payments
              </button>
              <button @click="navigateTo('/public/payment-history')" class="btn btn-primary">
                📋 Payment History
              </button>
            </div>
          </div>
        </div>
      </div>

  <!-- Quick Actions (hidden for PUBLIC users) -->
  <div v-if="!isPublicUser" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 2rem; margin-top: 3rem;">
        <!-- Quick Actions Card -->
        <div class="card">
          <div class="card-header">
            <h3>Quick Actions</h3>
          </div>
          <div class="card-body">
            <div style="display: grid; gap: 1rem;">
              <button @click="navigateTo('/violations/create')" class="btn btn-success btn-block">
                ➕ Register New Violation
              </button>
              
              <button @click="navigateTo('/violations')" class="btn btn-block">
                📋 View All Violations
              </button>
              
              <button @click="navigateTo('/emergencies/create')" class="btn btn-danger btn-block">
                🚨 Report Emergency
              </button>
              
              <button @click="navigateTo('/emergencies')" class="btn btn-block">
                🏥 View Emergencies
              </button>
            </div>
          </div>
        </div>

        <!-- Recent Activity Card -->
        <div class="card">
          <div class="card-header">
            <h3>Recent Activity</h3>
          </div>
          <div class="card-body">
            <div v-if="recentActivity.length === 0" class="text-center" style="padding: 2rem; color: #666;">
              No recent activity
            </div>
            <div v-else>
              <div 
                v-for="activity in recentActivity" 
                :key="activity.id"
                style="padding: 0.75rem; border-bottom: 1px solid #eee; cursor: pointer;"
                @click="viewActivity(activity)"
              >
                <div style="font-weight: 500;">{{ activity.title }}</div>
                <div style="font-size: 0.9rem; color: #666;">{{ activity.description }}</div>
                <div style="font-size: 0.8rem; color: #999;">{{ formatDate(activity.timestamp) }}</div>
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

export default {
  name: 'SimpleDashboardView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()

    // Reactive data
    const stats = ref({
      todayViolations: 42,
      pendingTickets: 15,
      activeEmergencies: 3,
      todayRevenue: 85000
    })

    const recentActivity = ref([
      {
        id: 1,
        title: 'Traffic Violation Registered',
        description: 'Speeding violation - Vehicle MH01AB1234',
        timestamp: new Date(Date.now() - 30 * 60 * 1000) // 30 minutes ago
      },
      {
        id: 2,
        title: 'Emergency Response Completed',
        description: 'Medical emergency at Baner Junction',
        timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000) // 2 hours ago
      },
      {
        id: 3,
        title: 'Payment Received',
        description: 'Fine payment for ticket #TK2025001',
        timestamp: new Date(Date.now() - 4 * 60 * 60 * 1000) // 4 hours ago
      }
    ])

    // Computed properties
    const userName = computed(() => authStore.user?.name || 'User')
    const userRole = computed(() => authStore.user?.role || 'Unknown')
    const isPublicUser = computed(() => {
      const r = authStore.user?.role
      return !!r && r.toString().toUpperCase() === 'PUBLIC'
    })

    // Methods
    const navigateTo = (path) => {
      console.log('Navigating to:', path)
      router.push(path)
    }

    const logout = () => {
      authStore.logout()
      router.push('/login')
    }

    const viewActivity = (activity) => {
      alert(`Viewing: ${activity.title}\n${activity.description}`)
    }

    const formatDate = (date) => {
      return date.toLocaleString()
    }

    // Lifecycle
    onMounted(() => {
      console.log('Dashboard mounted for user:', userName.value)
      
      // Redirect to role-specific dashboards
      const role = authStore.user?.role
      if (role) {
        switch (role) {
          case 'admin':
            router.replace('/admin')
            break
          case 'traffic_sergeant':
            router.replace('/dashboard/sergeant')
            break
          case 'traffic_inspector':
            router.replace('/dashboard/inspector')
            break
          case 'traffic_central_team':
            router.replace('/central')
            break
          case 'medical_emergency':
          case 'medical_emergency_central':
            router.replace('/dashboard/medical')
            break
          default:
            console.log('Using default dashboard for role:', role)
        }
      }
    })

    return {
      stats,
      recentActivity,
      userName,
      userRole,
      isPublicUser,
      navigateTo,
      logout,
      viewActivity,
      formatDate
    }
  }
}
</script>