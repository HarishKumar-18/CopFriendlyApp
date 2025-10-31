<template>
  <div>
    <!-- Navigation -->
    <nav style="background: #3b82f6; color: white; padding: 1rem; display: flex; justify-content: space-between; align-items: center;">
      <div style="display: flex; align-items: center; gap: 1rem;">
        <button @click="goBack" style="background: rgba(255,255,255,0.2); border: none; color: white; padding: 0.5rem; border-radius: 4px; cursor: pointer;">
          ← Back
        </button>
        <div style="font-size: 1.5rem; font-weight: bold;">
          👥 Personnel Management Center
        </div>
      </div>
      <div style="font-size: 0.9rem;">
        {{ authStore.user?.role || 'Administrator' }}: {{ authStore.user?.name || 'Admin' }}
      </div>
    </nav>

    <div class="dashboard">
      <!-- Quick Stats -->
      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-bottom: 2rem;">
        <div class="stat-card" style="background: linear-gradient(135deg, #3b82f6, #1e40af);">
          <div class="stat-icon">👮</div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.totalOfficers }}</div>
            <div class="stat-label">Total Officers</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #16a34a, #15803d);">
          <div class="stat-icon">✅</div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.onDuty }}</div>
            <div class="stat-label">On Duty</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #f59e0b, #d97706);">
          <div class="stat-icon">⏸️</div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.onBreak }}</div>
            <div class="stat-label">On Break</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #dc2626, #991b1b);">
          <div class="stat-icon">🏠</div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.offDuty }}</div>
            <div class="stat-label">Off Duty</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #7c3aed, #5b21b6);">
          <div class="stat-icon">🎯</div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.specialAssignment }}</div>
            <div class="stat-label">Special Assignment</div>
          </div>
        </div>
      </div>

      <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 2rem;">
        <!-- Main Content -->
        <div>
          <!-- Personnel Overview -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>👥 Personnel Overview</h3>
              <div style="display: flex; gap: 1rem;">
                <select v-model="filters.department" class="form-control" style="width: auto;">
                  <option value="all">All Departments</option>
                  <option value="traffic">Traffic Police</option>
                  <option value="patrol">Patrol Unit</option>
                  <option value="investigation">Investigation</option>
                  <option value="emergency">Emergency Response</option>
                </select>
                <select v-model="filters.status" class="form-control" style="width: auto;">
                  <option value="all">All Status</option>
                  <option value="on_duty">On Duty</option>
                  <option value="off_duty">Off Duty</option>
                  <option value="on_break">On Break</option>
                  <option value="special_assignment">Special Assignment</option>
                </select>
                <button @click="refreshData" class="btn btn-primary">🔄 Refresh</button>
              </div>
            </div>
            <div class="card-body">
              <div style="display: grid; gap: 1rem;">
                <div 
                  v-for="officer in filteredOfficers" 
                  :key="officer.id"
                  class="officer-card"
                >
                  <div style="display: flex; align-items: center; gap: 1rem;">
                    <!-- Profile Picture -->
                    <div style="width: 60px; height: 60px; border-radius: 50%; background: linear-gradient(135deg, #3b82f6, #1e40af); display: flex; align-items: center; justify-content: center; color: white; font-size: 1.2rem; font-weight: bold;">
                      {{ officer.name.split(' ').map(n => n[0]).join('') }}
                    </div>
                    
                    <!-- Officer Details -->
                    <div style="flex: 1;">
                      <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 0.5rem;">
                        <div style="font-size: 1.1rem; font-weight: bold;">{{ officer.name }}</div>
                        <span :style="`padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.8rem; font-weight: 500; background: ${getStatusColor(officer.status)}; color: white;`">
                          {{ officer.status.replace('_', ' ').toUpperCase() }}
                        </span>
                        <span :style="`padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.8rem; font-weight: 500; background: ${getDepartmentColor(officer.department)}; color: white;`">
                          {{ officer.department.toUpperCase() }}
                        </span>
                      </div>
                      
                      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 0.5rem; font-size: 0.9rem; color: #6b7280;">
                        <div>🏷️ {{ officer.rank }} | Badge: {{ officer.badgeNumber }}</div>
                        <div>📱 {{ officer.phoneNumber }}</div>
                        <div>📍 {{ officer.currentLocation || 'Not Available' }}</div>
                        <div>⏰ Shift: {{ officer.shiftTime }}</div>
                      </div>
                      
                      <div v-if="officer.currentAssignment" style="margin-top: 0.5rem; padding: 0.5rem; background: #f0f9ff; border-radius: 4px; font-size: 0.9rem;">
                        📋 Current Assignment: {{ officer.currentAssignment }}
                      </div>
                    </div>
                    
                    <!-- Action Buttons -->
                    <div style="display: flex; gap: 0.5rem;">
                      <button @click="viewProfile(officer)" class="btn btn-info btn-small">
                        👤 Profile
                      </button>
                      <button @click="contactOfficer(officer)" class="btn btn-primary btn-small">
                        📞 Contact
                      </button>
                      <button @click="assignTask(officer)" class="btn btn-success btn-small">
                        📋 Assign
                      </button>
                      <button @click="updateStatus(officer)" class="btn btn-warning btn-small">
                        🔄 Update
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Shift Management -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>🕐 Shift Management</h3>
              <div style="display: flex; gap: 1rem;">
                <input v-model="shiftDate" type="date" class="form-control" style="width: auto;">
                <button @click="createShiftSchedule" class="btn btn-success">➕ Create Schedule</button>
                <button @click="exportSchedule" class="btn btn-secondary">📊 Export Schedule</button>
              </div>
            </div>
            <div class="card-body">
              <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem;">
                <div 
                  v-for="shift in shifts" 
                  :key="shift.id"
                  :style="`border: 2px solid ${shift.color}; border-radius: 8px; padding: 1rem; background: ${shift.bg};`"
                >
                  <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                    <div style="font-weight: bold; font-size: 1.1rem;">{{ shift.name }}</div>
                    <div style="font-size: 0.9rem; color: #6b7280;">{{ shift.time }}</div>
                  </div>
                  
                  <div style="margin-bottom: 1rem;">
                    <div style="font-size: 0.9rem; color: #6b7280; margin-bottom: 0.5rem;">
                      Officers Assigned: {{ shift.officers.length }}/{{ shift.capacity }}
                    </div>
                    <div style="display: flex; gap: 0.25rem; flex-wrap: wrap;">
                      <span 
                        v-for="officer in shift.officers" 
                        :key="officer"
                        style="padding: 0.25rem 0.5rem; background: rgba(59, 130, 246, 0.1); color: #2563eb; border-radius: 4px; font-size: 0.8rem;"
                      >
                        {{ officer }}
                      </span>
                    </div>
                  </div>
                  
                  <div style="display: flex; gap: 0.5rem;">
                    <button @click="editShift(shift)" class="btn btn-warning btn-small" style="flex: 1;">
                      ✏️ Edit
                    </button>
                    <button @click="viewShiftDetails(shift)" class="btn btn-info btn-small" style="flex: 1;">
                      👁️ Details
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Performance Tracking -->
          <div class="card">
            <div class="card-header">
              <h3>📊 Performance Tracking</h3>
              <div style="display: flex; gap: 1rem;">
                <select v-model="performanceFilter" class="form-control" style="width: auto;">
                  <option value="monthly">Monthly</option>
                  <option value="quarterly">Quarterly</option>
                  <option value="yearly">Yearly</option>
                </select>
                <button @click="generatePerformanceReport" class="btn btn-primary">📋 Generate Report</button>
              </div>
            </div>
            <div class="card-body">
              <div style="display: grid; gap: 1rem;">
                <div 
                  v-for="performance in performanceData" 
                  :key="performance.id"
                  style="border: 1px solid #e5e7eb; border-radius: 8px; padding: 1rem; background: #f8fafc;"
                >
                  <div style="display: flex; justify-content: space-between; align-items: start;">
                    <div style="flex: 1;">
                      <div style="font-weight: bold; margin-bottom: 0.5rem;">{{ performance.officerName }}</div>
                      
                      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(120px, 1fr)); gap: 1rem; margin-bottom: 1rem;">
                        <div>
                          <div style="font-size: 0.8rem; color: #6b7280;">Cases Handled</div>
                          <div style="font-size: 1.2rem; font-weight: bold; color: #3b82f6;">{{ performance.casesHandled }}</div>
                        </div>
                        <div>
                          <div style="font-size: 0.8rem; color: #6b7280;">Tickets Issued</div>
                          <div style="font-size: 1.2rem; font-weight: bold; color: #f59e0b;">{{ performance.ticketsIssued }}</div>
                        </div>
                        <div>
                          <div style="font-size: 0.8rem; color: #6b7280;">Response Time</div>
                          <div style="font-size: 1.2rem; font-weight: bold; color: #16a34a;">{{ performance.avgResponseTime }}min</div>
                        </div>
                        <div>
                          <div style="font-size: 0.8rem; color: #6b7280;">Rating</div>
                          <div style="font-size: 1.2rem; font-weight: bold; color: #dc2626;">{{ performance.rating }}/5</div>
                        </div>
                      </div>
                      
                      <div style="margin-bottom: 0.5rem;">
                        <div style="font-size: 0.8rem; color: #6b7280; margin-bottom: 0.25rem;">Performance Score</div>
                        <div style="width: 100%; height: 8px; background: #e5e7eb; border-radius: 4px; overflow: hidden;">
                          <div :style="`width: ${performance.score}%; height: 100%; background: ${getPerformanceColor(performance.score)}; transition: width 0.3s;`"></div>
                        </div>
                        <div style="font-size: 0.8rem; color: #6b7280; margin-top: 0.25rem;">{{ performance.score }}%</div>
                      </div>
                    </div>
                    
                    <div style="display: flex; flex-direction: column; gap: 0.5rem; min-width: 100px;">
                      <button @click="viewDetailedPerformance(performance)" class="btn btn-info btn-small">
                        📊 Details
                      </button>
                      <button @click="provideFeedback(performance)" class="btn btn-primary btn-small">
                        💬 Feedback
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
          <!-- Quick Actions -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>⚡ Quick Actions</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; gap: 0.5rem;">
                <button @click="addNewOfficer" class="action-btn">
                  ➕ Add New Officer
                </button>
                <button @click="bulkAssignment" class="action-btn">
                  👥 Bulk Assignment
                </button>
                <button @click="emergencyDeployment" class="action-btn">
                  🚨 Emergency Deployment
                </button>
                <button @click="leaveManagement" class="action-btn">
                  📅 Leave Management
                </button>
                <button @click="trainingSchedule" class="action-btn">
                  🎓 Training Schedule
                </button>
                <button @click="payrollManagement" class="action-btn">
                  💰 Payroll Management
                </button>
              </div>
            </div>
          </div>

          <!-- Department Summary -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>🏢 Department Summary</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; gap: 0.75rem;">
                <div 
                  v-for="dept in departmentSummary" 
                  :key="dept.name"
                  style="padding: 0.75rem; border: 1px solid #e5e7eb; border-radius: 6px; background: #f8fafc;"
                >
                  <div style="font-weight: 500; margin-bottom: 0.25rem;">{{ dept.name }}</div>
                  <div style="font-size: 0.8rem; color: #6b7280; margin-bottom: 0.5rem;">
                    Total: {{ dept.total }} | Active: {{ dept.active }}
                  </div>
                  <div style="width: 100%; height: 6px; background: #e5e7eb; border-radius: 3px; overflow: hidden;">
                    <div :style="`width: ${(dept.active / dept.total) * 100}%; height: 100%; background: #3b82f6;`"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Recent Activities -->
          <div class="card">
            <div class="card-header">
              <h3>📋 Recent Activities</h3>
            </div>
            <div class="card-body">
              <div style="max-height: 400px; overflow-y: auto;">
                <div 
                  v-for="activity in recentActivities" 
                  :key="activity.id"
                  style="padding: 0.75rem; margin-bottom: 0.5rem; border-left: 3px solid #3b82f6; background: #f8fafc; border-radius: 0 6px 6px 0;"
                >
                  <div style="font-size: 0.9rem; font-weight: 500; margin-bottom: 0.25rem;">{{ activity.action }}</div>
                  <div style="font-size: 0.8rem; color: #6b7280; margin-bottom: 0.25rem;">{{ activity.officer }}</div>
                  <div style="font-size: 0.7rem; color: #9ca3af;">{{ formatDateTime(activity.timestamp) }}</div>
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
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

export default {
  name: 'PersonnelManagementView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const filters = ref({
      department: 'all',
      status: 'all'
    })
    
    const shiftDate = ref(new Date().toISOString().split('T')[0])
    const performanceFilter = ref('monthly')
    
    const stats = ref({
      totalOfficers: 45,
      onDuty: 28,
      onBreak: 8,
      offDuty: 7,
      specialAssignment: 2
    })
    
    const officers = ref([
      {
        id: 1,
        name: 'Sergeant Rajesh Sharma',
        rank: 'Sergeant',
        badgeNumber: 'SG001',
        department: 'traffic',
        status: 'on_duty',
        phoneNumber: '+91 98765 43210',
        currentLocation: 'Bandra Junction',
        shiftTime: '08:00 - 16:00',
        currentAssignment: 'Traffic Control - Marine Drive'
      },
      {
        id: 2,
        name: 'Inspector Priya Patel',
        rank: 'Inspector',
        badgeNumber: 'IN002',
        department: 'investigation',
        status: 'on_duty',
        phoneNumber: '+91 98765 43211',
        currentLocation: 'Police Station',
        shiftTime: '14:00 - 22:00',
        currentAssignment: 'Bank Robbery Investigation'
      },
      {
        id: 3,
        name: 'Constable Amit Kumar',
        rank: 'Police Constable',
        badgeNumber: 'PC003',
        department: 'patrol',
        status: 'on_break',
        phoneNumber: '+91 98765 43212',
        currentLocation: 'Station Canteen',
        shiftTime: '06:00 - 14:00',
        currentAssignment: null
      },
      {
        id: 4,
        name: 'Sub-Inspector Maya Singh',
        rank: 'Sub-Inspector',
        badgeNumber: 'SI004',
        department: 'emergency',
        status: 'special_assignment',
        phoneNumber: '+91 98765 43213',
        currentLocation: 'VIP Security Detail',
        shiftTime: '00:00 - 24:00',
        currentAssignment: 'Chief Minister Security Detail'
      },
      {
        id: 5,
        name: 'Head Constable Ravi Verma',
        rank: 'Head Constable',
        badgeNumber: 'HC005',
        department: 'traffic',
        status: 'off_duty',
        phoneNumber: '+91 98765 43214',
        currentLocation: 'Home',
        shiftTime: '22:00 - 06:00',
        currentAssignment: null
      }
    ])
    
    const shifts = ref([
      {
        id: 1,
        name: 'Morning Shift',
        time: '06:00 - 14:00',
        capacity: 15,
        officers: ['Sgt. Sharma', 'PC Kumar', 'HC Verma', 'PC Gupta'],
        color: '#f59e0b',
        bg: '#fffbeb'
      },
      {
        id: 2,
        name: 'Afternoon Shift',
        time: '14:00 - 22:00',
        capacity: 18,
        officers: ['Insp. Patel', 'SI Singh', 'PC Agarwal', 'Sgt. Mehta', 'PC Joshi'],
        color: '#3b82f6',
        bg: '#eff6ff'
      },
      {
        id: 3,
        name: 'Night Shift',
        time: '22:00 - 06:00',
        capacity: 12,
        officers: ['HC Verma', 'PC Rao', 'SI Chopra'],
        color: '#7c3aed',
        bg: '#faf5ff'
      }
    ])
    
    const performanceData = ref([
      {
        id: 1,
        officerName: 'Sergeant Rajesh Sharma',
        casesHandled: 45,
        ticketsIssued: 128,
        avgResponseTime: 8,
        rating: 4.5,
        score: 92
      },
      {
        id: 2,
        officerName: 'Inspector Priya Patel',
        casesHandled: 23,
        ticketsIssued: 67,
        avgResponseTime: 12,
        rating: 4.2,
        score: 88
      },
      {
        id: 3,
        officerName: 'Constable Amit Kumar',
        casesHandled: 67,
        ticketsIssued: 203,
        avgResponseTime: 6,
        rating: 4.8,
        score: 95
      }
    ])
    
    const departmentSummary = ref([
      { name: 'Traffic Police', total: 20, active: 15 },
      { name: 'Patrol Unit', total: 12, active: 9 },
      { name: 'Investigation', total: 8, active: 6 },
      { name: 'Emergency Response', total: 5, active: 4 }
    ])
    
    const recentActivities = ref([
      {
        id: 1,
        action: 'Officer assigned to special duty',
        officer: 'SI Maya Singh',
        timestamp: new Date()
      },
      {
        id: 2,
        action: 'Shift change completed',
        officer: 'Sgt. Rajesh Sharma',
        timestamp: new Date(Date.now() - 30 * 60 * 1000)
      },
      {
        id: 3,
        action: 'Performance review submitted',
        officer: 'PC Amit Kumar',
        timestamp: new Date(Date.now() - 60 * 60 * 1000)
      },
      {
        id: 4,
        action: 'Leave request approved',
        officer: 'HC Ravi Verma',
        timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000)
      }
    ])
    
    const filteredOfficers = computed(() => {
      let filtered = officers.value
      
      if (filters.value.department !== 'all') {
        filtered = filtered.filter(officer => officer.department === filters.value.department)
      }
      
      if (filters.value.status !== 'all') {
        filtered = filtered.filter(officer => officer.status === filters.value.status)
      }
      
      return filtered
    })
    
    const goBack = () => {
      router.go(-1)
    }
    
    const refreshData = () => {
      alert('Refreshing personnel data...')
    }
    
    const viewProfile = (officer) => {
      alert(`Opening profile for ${officer.name}`)
    }
    
    const contactOfficer = (officer) => {
      alert(`Contacting ${officer.name} at ${officer.phoneNumber}`)
    }
    
    const assignTask = (officer) => {
      alert(`Opening task assignment for ${officer.name}`)
    }
    
    const updateStatus = (officer) => {
      alert(`Updating status for ${officer.name}`)
    }
    
    const createShiftSchedule = () => {
      alert('Opening shift schedule creator...')
    }
    
    const exportSchedule = () => {
      alert('Exporting shift schedule...')
    }
    
    const editShift = (shift) => {
      alert(`Editing ${shift.name}`)
    }
    
    const viewShiftDetails = (shift) => {
      alert(`Viewing details for ${shift.name}`)
    }
    
    const generatePerformanceReport = () => {
      alert('Generating performance report...')
    }
    
    const viewDetailedPerformance = (performance) => {
      alert(`Viewing detailed performance for ${performance.officerName}`)
    }
    
    const provideFeedback = (performance) => {
      alert(`Providing feedback for ${performance.officerName}`)
    }
    
    const addNewOfficer = () => {
      alert('Opening new officer registration form...')
    }
    
    const bulkAssignment = () => {
      alert('Opening bulk assignment interface...')
    }
    
    const emergencyDeployment = () => {
      alert('Opening emergency deployment panel...')
    }
    
    const leaveManagement = () => {
      alert('Opening leave management system...')
    }
    
    const trainingSchedule = () => {
      alert('Opening training schedule manager...')
    }
    
    const payrollManagement = () => {
      alert('Opening payroll management system...')
    }
    
    const formatDateTime = (date) => {
      return date.toLocaleString('en-IN')
    }
    
    const getStatusColor = (status) => {
      const colors = {
        'on_duty': '#16a34a',
        'off_duty': '#dc2626',
        'on_break': '#f59e0b',
        'special_assignment': '#7c3aed'
      }
      return colors[status] || '#6b7280'
    }
    
    const getDepartmentColor = (department) => {
      const colors = {
        'traffic': '#3b82f6',
        'patrol': '#16a34a',
        'investigation': '#f59e0b',
        'emergency': '#dc2626'
      }
      return colors[department] || '#6b7280'
    }
    
    const getPerformanceColor = (score) => {
      if (score >= 90) return '#16a34a'
      if (score >= 80) return '#f59e0b'
      if (score >= 70) return '#f97316'
      return '#dc2626'
    }
    
    return {
      authStore,
      filters,
      shiftDate,
      performanceFilter,
      stats,
      officers,
      shifts,
      performanceData,
      departmentSummary,
      recentActivities,
      filteredOfficers,
      goBack,
      refreshData,
      viewProfile,
      contactOfficer,
      assignTask,
      updateStatus,
      createShiftSchedule,
      exportSchedule,
      editShift,
      viewShiftDetails,
      generatePerformanceReport,
      viewDetailedPerformance,
      provideFeedback,
      addNewOfficer,
      bulkAssignment,
      emergencyDeployment,
      leaveManagement,
      trainingSchedule,
      payrollManagement,
      formatDateTime,
      getStatusColor,
      getDepartmentColor,
      getPerformanceColor
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

.officer-card {
  padding: 1rem;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #ffffff;
  transition: all 0.2s;
}

.officer-card:hover {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
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
  padding: 0.5rem 0.75rem;
  font-size: 0.8rem;
}
</style>