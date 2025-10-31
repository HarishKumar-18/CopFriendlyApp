<template>
  <div class="dashboard">
    <!-- Admin Navigation Bar -->
    <nav style="
      background: #7c3aed !important; 
      color: white !important; 
      padding: 1rem 2rem !important; 
      display: flex !important; 
      justify-content: space-between !important; 
      align-items: center !important;
      position: sticky !important;
      top: 0 !important;
      z-index: 1000 !important;
      width: 100% !important;
      box-shadow: 0 4px 6px rgba(0,0,0,0.1) !important;
    ">
      <div>
        <h1 style="font-size: 1.5rem; font-weight: bold; margin: 0; color: white !important;">👨‍💼 Admin Dashboard</h1>
        <p style="margin: 0; color: white !important; opacity: 0.9;">System Administrator | Full Access</p>
      </div>
      <div style="display: flex; gap: 1rem; align-items: center;">
        <span style="color: white !important;">{{ authStore.user?.name || 'Admin' }}</span>
        <button @click="logout" style="
          background: white !important; 
          color: #7c3aed !important; 
          padding: 0.5rem 1rem !important; 
          border: none !important; 
          border-radius: 0.375rem !important; 
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
      <!-- Admin Stats -->
      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1rem; margin-bottom: 2rem;">
        <div class="stat-card" style="background: linear-gradient(135deg, #fef3c7, #fbbf24); border-left: 4px solid #f59e0b;">
          <div class="stat-icon">⏳</div>
          <div>
            <div class="stat-value">{{ stats.pendingApprovals }}</div>
            <div class="stat-label">Pending Approvals</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #d1fae5, #34d399); border-left: 4px solid #10b981;">
          <div class="stat-icon">✅</div>
          <div>
            <div class="stat-value">{{ stats.activeUsers }}</div>
            <div class="stat-label">Active Users</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #fecaca, #f87171); border-left: 4px solid #ef4444;">
          <div class="stat-icon">🚫</div>
          <div>
            <div class="stat-value">{{ stats.suspendedUsers }}</div>
            <div class="stat-label">Suspended Users</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #dbeafe, #60a5fa); border-left: 4px solid #3b82f6;">
          <div class="stat-icon">👥</div>
          <div>
            <div class="stat-value">{{ stats.totalUsers }}</div>
            <div class="stat-label">Total Users</div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div style="display: flex; gap: 1rem; margin-bottom: 2rem; flex-wrap: wrap;">
        <button @click="filterStatus = 'pending'" :class="filterStatus === 'pending' ? 'btn-active' : 'btn'" style="background: #fbbf24; color: white;">
          ⏳ Pending Approvals ({{ stats.pendingApprovals }})
        </button>
        <button @click="filterStatus = 'approved'" :class="filterStatus === 'approved' ? 'btn-active' : 'btn'" style="background: #10b981; color: white;">
          ✅ Approved Users
        </button>
        <button @click="filterStatus = 'rejected'" :class="filterStatus === 'rejected' ? 'btn-active' : 'btn'" style="background: #ef4444; color: white;">
          ❌ Rejected Users
        </button>
        <button @click="filterStatus = 'suspended'" :class="filterStatus === 'suspended' ? 'btn-active' : 'btn'" style="background: #f97316; color: white;">
          🚫 Suspended Users
        </button>
        <button @click="filterStatus = 'all'" :class="filterStatus === 'all' ? 'btn-active' : 'btn'" style="background: #3b82f6; color: white;">
          📋 All Users
        </button>
        <button @click="refreshUsers" class="btn" style="background: #8b5cf6; color: white; margin-left: auto;">
          🔄 Refresh
        </button>
      </div>

      <!-- Search and Filter -->
      <div class="card" style="margin-bottom: 2rem;">
        <div class="card-body">
          <div style="display: grid; grid-template-columns: 1fr auto auto; gap: 1rem; align-items: center;">
            <input 
              v-model="searchQuery" 
              type="text" 
              placeholder="Search by name, email, or badge number..." 
              class="form-control"
              style="padding: 0.75rem; font-size: 1rem;"
            >
            <select v-model="filterRole" class="form-control" style="padding: 0.75rem;">
              <option value="all">All Roles</option>
              <option value="traffic_sergeant">Traffic Sergeant</option>
              <option value="traffic_inspector">Traffic Inspector</option>
              <option value="traffic_central_team">Central Team</option>
              <option value="medical_emergency">Medical Emergency</option>
              <option value="medical_emergency_central">Medical Central</option>
            </select>
            <button @click="clearFilters" class="btn">Clear Filters</button>
          </div>
        </div>
      </div>

      <!-- Users Table -->
      <div class="card">
        <div class="card-header">
          <h3>👥 User Management ({{ filteredUsers.length }} users)</h3>
        </div>
        <div class="card-body">
          <div style="overflow-x: auto;">
            <table style="width: 100%; border-collapse: collapse;">
              <thead>
                <tr style="background: #f8fafc; border-bottom: 2px solid #e5e7eb;">
                  <th style="padding: 1rem; text-align: left;">User Info</th>
                  <th style="padding: 1rem; text-align: center;">Role</th>
                  <th style="padding: 1rem; text-align: center;">Status</th>
                  <th style="padding: 1rem; text-align: center;">Signup Date</th>
                  <th style="padding: 1rem; text-align: center;">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="filteredUsers.length === 0">
                  <td colspan="5" style="padding: 2rem; text-align: center; color: #666;">
                    No users found matching your filters
                  </td>
                </tr>
                <tr 
                  v-for="user in filteredUsers" 
                  :key="user.id"
                  style="border-bottom: 1px solid #e5e7eb;"
                  :style="{ background: getRowBackground(user.status) }"
                >
                  <td style="padding: 1rem;">
                    <div style="font-weight: 600; color: #333;">{{ user.name }}</div>
                    <div style="font-size: 0.9rem; color: #666;">{{ user.email }}</div>
                    <div style="font-size: 0.85rem; color: #999;">
                      Badge: {{ user.badgeNumber }} | Zone: {{ user.zone }}
                    </div>
                  </td>
                  <td style="padding: 1rem; text-align: center;">
                    <span :style="getRoleBadgeStyle(user.role)">
                      {{ formatRole(user.role) }}
                    </span>
                  </td>
                  <td style="padding: 1rem; text-align: center;">
                    <span :style="getStatusBadgeStyle(user.status)">
                      {{ getStatusIcon(user.status) }} {{ user.status.toUpperCase() }}
                    </span>
                  </td>
                  <td style="padding: 1rem; text-align: center; color: #666;">
                    {{ formatDate(user.signupDate) }}
                  </td>
                  <td style="padding: 1rem;">
                    <div style="display: flex; gap: 0.5rem; justify-content: center; flex-wrap: wrap;">
                      <button 
                        v-if="user.status === 'pending'"
                        @click="approveUser(user)" 
                        class="btn-small"
                        style="background: #10b981; color: white; padding: 0.5rem 0.75rem;"
                      >
                        ✅ Approve
                      </button>
                      <button 
                        v-if="user.status === 'pending'"
                        @click="rejectUser(user)" 
                        class="btn-small"
                        style="background: #ef4444; color: white; padding: 0.5rem 0.75rem;"
                      >
                        ❌ Reject
                      </button>
                      <button 
                        v-if="user.status === 'approved'"
                        @click="suspendUser(user)" 
                        class="btn-small"
                        style="background: #f97316; color: white; padding: 0.5rem 0.75rem;"
                      >
                        🚫 Suspend
                      </button>
                      <button 
                        v-if="user.status === 'suspended'"
                        @click="reactivateUser(user)" 
                        class="btn-small"
                        style="background: #10b981; color: white; padding: 0.5rem 0.75rem;"
                      >
                        ✅ Reactivate
                      </button>
                      <button 
                        @click="changeRole(user)" 
                        class="btn-small"
                        style="background: #3b82f6; color: white; padding: 0.5rem 0.75rem;"
                      >
                        🔄 Change Role
                      </button>
                      <button 
                        @click="viewUserDetails(user)" 
                        class="btn-small"
                        style="background: #6b7280; color: white; padding: 0.5rem 0.75rem;"
                      >
                        👁️ View
                      </button>
                      <button 
                        @click="deleteUser(user)" 
                        class="btn-small"
                        style="background: #dc2626; color: white; padding: 0.5rem 0.75rem;"
                      >
                        🗑️ Delete
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- Activity Log -->
      <div class="card" style="margin-top: 2rem;">
        <div class="card-header">
          <h3>📋 Recent Admin Activity</h3>
        </div>
        <div class="card-body">
          <div style="display: grid; gap: 0.5rem;">
            <div 
              v-for="activity in recentActivity" 
              :key="activity.id"
              style="padding: 0.75rem; background: #f8fafc; border-left: 3px solid #3b82f6; border-radius: 4px;"
            >
              <div style="display: flex; justify-content: space-between; align-items: start;">
                <div>
                  <span style="font-weight: 500;">{{ activity.action }}</span>
                  <span style="color: #666;"> - {{ activity.details }}</span>
                </div>
                <span style="font-size: 0.85rem; color: #999;">{{ formatDate(activity.timestamp) }}</span>
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
import { dashboardAPI } from '@/services/api'
import { showToast, showConfirm, showInput } from '@/composables/interactionService'

export default {
  name: 'AdminDashboardView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const searchQuery = ref('')
    const filterStatus = ref('pending')
    const filterRole = ref('all')
    const users = ref([])
    const loading = ref(false)
    
    const stats = computed(() => {
      const allUsers = users.value
      return {
        pendingApprovals: allUsers.filter(u => u.status === 'pending').length,
        activeUsers: allUsers.filter(u => u.status === 'approved').length,
        suspendedUsers: allUsers.filter(u => u.status === 'suspended').length,
        totalUsers: allUsers.length
      }
    })
    
    // recentActivity is loaded from backend dashboard API
    const recentActivity = ref([])

    const loadRecentActivity = async () => {
      try {
        const resp = await dashboardAPI.getRecentActivity()
        // Expecting resp.data to be an array of activity objects
        if (resp && resp.data) {
          // Normalize timestamps to Date objects where applicable
          recentActivity.value = (Array.isArray(resp.data) ? resp.data : resp.data.activities || [])
            .map(a => ({ ...a, timestamp: a.timestamp ? new Date(a.timestamp) : new Date() }))
        } else {
          recentActivity.value = []
        }
      } catch (err) {
        console.error('Failed to load recent activity:', err)
        recentActivity.value = []
      }
    }
    
    const filteredUsers = computed(() => {
      let result = users.value
      
      // Filter by status
      if (filterStatus.value !== 'all') {
        result = result.filter(u => u.status === filterStatus.value)
      }
      
      // Filter by role
      if (filterRole.value !== 'all') {
        result = result.filter(u => u.role === filterRole.value)
      }
      
      // Search filter
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        result = result.filter(u => {
          // Coerce possible undefined fields to empty strings before matching
          const name = (u.name || u.fullName || u.username || '') + ''
          const email = (u.email || '') + ''
          const username = (u.username || '') + ''
          const badge = (u.badgeNumber || '') + ''
          return (
            name.toLowerCase().includes(query) ||
            email.toLowerCase().includes(query) ||
            username.toLowerCase().includes(query) ||
            badge.toLowerCase().includes(query)
          )
        })
      }
      
      return result.sort((a, b) => {
        const aDate = a.createdAt ? new Date(a.createdAt) : new Date(0)
        const bDate = b.createdAt ? new Date(b.createdAt) : new Date(0)
        return bDate - aDate
      })
    })
    
  const loadUsers = async () => {
      try {
        loading.value = true
        const allUsers = await authStore.getAllUsers()
        // Map backend user format to frontend format
        users.value = allUsers.map(u => ({
          id: u.id,
          username: u.username,
          name: u.fullName || u.name || u.username,
          email: u.email,
          phone: u.phone || 'N/A',
          badgeNumber: u.badgeNumber || 'PENDING',
          role: u.role || 'USER',
          zone: u.zone || 'N/A',
          status: u.status || 'pending',
          signupDate: u.createdAt ? new Date(u.createdAt) : new Date(),
          active: u.active !== undefined ? u.active : false
        }))
      } catch (error) {
        console.error('Failed to load users:', error)
        showToast({ title: 'Users', message: '⚠️ Failed to load users. Please try again.', type: 'error' })
      } finally {
        loading.value = false
      }
    }
    
    const logout = () => {
      authStore.logout()
      router.push('/login')
    }
    
    const approveUser = async (user) => {
      const ok = await showConfirm({ title: 'Approve User', message: `Approve ${user.name} (${user.email})?` })
      if (!ok) return
      try {
        loading.value = true
        await authStore.updateUserStatus(user.id, 'approved')
        recentActivity.value.unshift({ id: Date.now(), action: 'User Approved', details: `${user.name} (${user.badgeNumber}) - ${formatRole(user.role)}`, timestamp: new Date() })
        await loadUsers()
        await loadRecentActivity()
        showToast({ title: 'User', message: `✅ ${user.name} approved and notified.`, type: 'success' })
      } catch (error) {
        console.error('Failed to approve user:', error)
        showToast({ title: 'Users', message: '⚠️ Failed to approve user. Please try again.', type: 'error' })
      } finally {
        loading.value = false
      }
    }
    
    const rejectUser = async (user) => {
      const reason = await showInput({ title: 'Reject User', message: `Enter rejection reason for ${user.name}`, placeholder: '' })
      if (!reason) return
      try {
        loading.value = true
        await authStore.updateUserStatus(user.id, 'rejected')
        recentActivity.value.unshift({ id: Date.now(), action: 'User Rejected', details: `${user.name} (${user.badgeNumber}) - Reason: ${reason}`, timestamp: new Date() })
        await loadUsers()
        await loadRecentActivity()
        showToast({ title: 'User', message: `❌ ${user.name} rejected. Email sent.`, type: 'info' })
      } catch (error) {
        console.error('Failed to reject user:', error)
        showToast({ title: 'Users', message: '⚠️ Failed to reject user. Please try again.', type: 'error' })
      } finally {
        loading.value = false
      }
    }
    
    const suspendUser = async (user) => {
      const reason = await showInput({ title: 'Suspend User', message: `Enter suspension reason for ${user.name}`, placeholder: '' })
      if (!reason) return
      try {
        loading.value = true
        await authStore.updateUserStatus(user.id, 'suspended')
        recentActivity.value.unshift({ id: Date.now(), action: 'User Suspended', details: `${user.name} (${user.badgeNumber}) - ${reason}`, timestamp: new Date() })
        await loadUsers()
        await loadRecentActivity()
        showToast({ title: 'User', message: `🚫 ${user.name} suspended.`, type: 'warning' })
      } catch (error) {
        console.error('Failed to suspend user:', error)
        showToast({ title: 'Users', message: '⚠️ Failed to suspend user. Please try again.', type: 'error' })
      } finally {
        loading.value = false
      }
    }
    
    const reactivateUser = async (user) => {
      const ok = await showConfirm({ title: 'Reactivate User', message: `Reactivate ${user.name}?` })
      if (!ok) return
      try {
        loading.value = true
        await authStore.updateUserStatus(user.id, 'approved')
        recentActivity.value.unshift({ id: Date.now(), action: 'User Reactivated', details: `${user.name} (${user.badgeNumber})`, timestamp: new Date() })
        await loadUsers()
        await loadRecentActivity()
        showToast({ title: 'User', message: `✅ ${user.name} reactivated.`, type: 'success' })
      } catch (error) {
        console.error('Failed to reactivate user:', error)
        showToast({ title: 'Users', message: '⚠️ Failed to reactivate user. Please try again.', type: 'error' })
      } finally {
        loading.value = false
      }
    }
    
    const changeRole = async (user) => {
      const roles = ['1. Traffic Sergeant','2. Traffic Inspector','3. Central Team','4. Medical Emergency']
      const selection = await showInput({ title: 'Change Role', message: `Current Role: ${formatRole(user.role)}\n\n${roles.join('\n')}\n\nSelect new role (1-4):`, placeholder: '' })
      if (!selection) return
      if (selection && selection >= 1 && selection <= 4) {
        try {
          loading.value = true
          const roleMap = ['traffic_sergeant','traffic_inspector','traffic_central_team','medical_emergency']
          const oldRole = user.role
          const newRole = roleMap[parseInt(selection) - 1]
          await authStore.updateUserRole(user.id, newRole)
          recentActivity.value.unshift({ id: Date.now(), action: 'Role Changed', details: `${user.name} - ${formatRole(oldRole)} → ${formatRole(newRole)}`, timestamp: new Date() })
          await loadUsers()
          await loadRecentActivity()
          showToast({ title: 'User', message: `✅ Role updated for ${user.name}`, type: 'success' })
        } catch (error) {
          console.error('Failed to change user role:', error)
          showToast({ title: 'Users', message: '⚠️ Failed to change user role. Please try again.', type: 'error' })
        } finally {
          loading.value = false
        }
      }
    }
    
    const viewUserDetails = (user) => {
      showToast({ title: 'User Details', message: `${user.name} • ${user.email} • ${formatRole(user.role)}`, type: 'info' })
    }
    
    const deleteUser = async (user) => {
      const ok = await showConfirm({ title: 'Delete User', message: `Permanently delete ${user.name}? This cannot be undone.` })
      if (!ok) return
      const doubleConfirm = await showInput({ title: 'Confirm Delete', message: 'Type DELETE to confirm', placeholder: '' })
      if (doubleConfirm !== 'DELETE') return
      try {
        loading.value = true
        await authStore.deleteUser(user.id)
        recentActivity.value.unshift({ id: Date.now(), action: 'User Deleted', details: `${user.name} (${user.badgeNumber})`, timestamp: new Date() })
        await loadUsers()
        showToast({ title: 'User', message: `🗑️ ${user.name} deleted.`, type: 'success' })
      } catch (error) {
        console.error('Failed to delete user:', error)
        showToast({ title: 'Users', message: '⚠️ Failed to delete user. Please try again.', type: 'error' })
      } finally {
        loading.value = false
      }
    }
    
    const refreshUsers = async () => {
      await loadUsers()
      showToast({ title: 'Users', message: '🔄 User list refreshed! ✓ All data up to date', type: 'success' })
    }
    
    const clearFilters = () => {
      searchQuery.value = ''
      filterStatus.value = 'all'
      filterRole.value = 'all'
    }
    
    const formatRole = (role) => {
      const roleNames = {
        'traffic_sergeant': 'Traffic Sergeant',
        'traffic_inspector': 'Traffic Inspector',
        'traffic_central_team': 'Central Team',
        'medical_emergency': 'Medical Emergency',
        'medical_emergency_central': 'Medical Central',
        'admin': 'Administrator'
      }
      return roleNames[role] || role
    }
    
    const formatDate = (date) => {
      if (!date) return 'N/A'
      return new Date(date).toLocaleString()
    }
    
    const getStatusIcon = (status) => {
      const icons = {
        'pending': '⏳',
        'approved': '✅',
        'rejected': '❌',
        'suspended': '🚫'
      }
      return icons[status] || '❓'
    }
    
    const getRowBackground = (status) => {
      const backgrounds = {
        'pending': '#fef3c7',
        'approved': '#d1fae5',
        'rejected': '#fee2e2',
        'suspended': '#fed7aa'
      }
      return backgrounds[status] || 'white'
    }
    
    const getRoleBadgeStyle = (role) => {
      const colors = {
        'traffic_sergeant': 'background: #dbeafe; color: #1e40af; padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.85rem; font-weight: 500;',
        'traffic_inspector': 'background: #fef3c7; color: #92400e; padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.85rem; font-weight: 500;',
        'traffic_central_team': 'background: #e0e7ff; color: #3730a3; padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.85rem; font-weight: 500;',
        'medical_emergency': 'background: #fecaca; color: #991b1b; padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.85rem; font-weight: 500;',
        'medical_emergency_central': 'background: #fecdd3; color: #9f1239; padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.85rem; font-weight: 500;'
      }
      return colors[role] || 'background: #f3f4f6; color: #374151; padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.85rem; font-weight: 500;'
    }
    
    const getStatusBadgeStyle = (status) => {
      const styles = {
        'pending': 'background: #fef3c7; color: #92400e; padding: 0.4rem 0.75rem; border-radius: 12px; font-size: 0.85rem; font-weight: 600;',
        'approved': 'background: #d1fae5; color: #065f46; padding: 0.4rem 0.75rem; border-radius: 12px; font-size: 0.85rem; font-weight: 600;',
        'rejected': 'background: #fee2e2; color: #991b1b; padding: 0.4rem 0.75rem; border-radius: 12px; font-size: 0.85rem; font-weight: 600;',
        'suspended': 'background: #fed7aa; color: #9a3412; padding: 0.4rem 0.75rem; border-radius: 12px; font-size: 0.85rem; font-weight: 600;'
      }
      return styles[status] || ''
    }
    
    // Load users and recent activity on mount
    onMounted(() => {
      loadUsers()
      loadRecentActivity()
    })
    
    return {
      authStore,
      searchQuery,
      filterStatus,
      filterRole,
      stats,
      users,
      recentActivity,
      filteredUsers,
      loading,
      logout,
      approveUser,
      rejectUser,
      suspendUser,
      reactivateUser,
      changeRole,
      viewUserDetails,
      deleteUser,
      refreshUsers,
      clearFilters,
      formatRole,
      formatDate,
      getStatusIcon,
      getRowBackground,
      getRoleBadgeStyle,
      getStatusBadgeStyle
    }
  }
}
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.stat-icon {
  font-size: 2.5rem;
}

.stat-value {
  font-size: 2rem;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 0.9rem;
  color: #666;
}

.btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.btn-active {
  padding: 0.5rem 1rem;
  border: 2px solid #333;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.btn-small {
  padding: 0.35rem 0.6rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-small:hover {
  opacity: 0.9;
}
</style>
