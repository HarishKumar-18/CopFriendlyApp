<template>
  <div>
    <!-- Navigation -->
    <nav style="background: #3b82f6; color: white; padding: 1rem; display: flex; justify-content: space-between; align-items: center;">
      <div style="display: flex; align-items: center; gap: 1rem;">
        <button @click="goBack" style="background: rgba(255,255,255,0.2); border: none; color: white; padding: 0.5rem; border-radius: 4px; cursor: pointer;">
          ← Back
        </button>
        <div style="font-size: 1.5rem; font-weight: bold;">
          📍 Special Duty Assignment Center
        </div>
      </div>
      <div style="font-size: 0.9rem;">
        Inspector: {{ authStore.user?.name || 'Inspector' }}
      </div>
    </nav>

    <div class="dashboard">
      <!-- Quick Stats -->
      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-bottom: 2rem;">
        <div class="stat-card" style="background: linear-gradient(135deg, #3b82f6, #1e40af);">
          <div class="stat-icon">👮</div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.availableOfficers }}</div>
            <div class="stat-label">Available Officers</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #f59e0b, #d97706);">
          <div class="stat-icon">📋</div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.activeAssignments }}</div>
            <div class="stat-label">Active Assignments</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #dc2626, #991b1b);">
          <div class="stat-icon">🚨</div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.urgentRequests }}</div>
            <div class="stat-label">Urgent Requests</div>
          </div>
        </div>
        
        <div class="stat-card" style="background: linear-gradient(135deg, #16a34a, #15803d);">
          <div class="stat-icon">✅</div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.completedToday }}</div>
            <div class="stat-label">Completed Today</div>
          </div>
        </div>
      </div>

      <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 2rem;">
        <!-- Main Content -->
        <div>
          <!-- Create New Assignment -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>➕ Create New Special Duty Assignment</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div>
                  <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Assignment Type</label>
                  <select v-model="newAssignment.type" class="form-control">
                    <option value="">Select Type</option>
                    <option value="vip_security">VIP Security Detail</option>
                    <option value="event_management">Event Management</option>
                    <option value="traffic_control">Special Traffic Control</option>
                    <option value="investigation">Investigation Support</option>
                    <option value="emergency_response">Emergency Response</option>
                    <option value="patrol_duty">Extended Patrol Duty</option>
                  </select>
                </div>
                
                <div>
                  <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Priority</label>
                  <select v-model="newAssignment.priority" class="form-control">
                    <option value="low">Low Priority</option>
                    <option value="medium">Medium Priority</option>
                    <option value="high">High Priority</option>
                    <option value="urgent">Urgent</option>
                  </select>
                </div>
                
                <div>
                  <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Location</label>
                  <input v-model="newAssignment.location" type="text" class="form-control" placeholder="Assignment location">
                </div>
                
                <div>
                  <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Date & Time</label>
                  <input v-model="newAssignment.datetime" type="datetime-local" class="form-control">
                </div>
                
                <div style="grid-column: 1 / -1;">
                  <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Description</label>
                  <textarea v-model="newAssignment.description" class="form-control" rows="3" placeholder="Detailed description of the assignment"></textarea>
                </div>
                
                <div>
                  <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Required Officers</label>
                  <input v-model="newAssignment.requiredOfficers" type="number" class="form-control" min="1" max="20">
                </div>
                
                <div>
                  <label style="display: block; margin-bottom: 0.5rem; font-weight: 500;">Duration (Hours)</label>
                  <input v-model="newAssignment.duration" type="number" class="form-control" min="1" max="24">
                </div>
              </div>
              
              <div style="margin-top: 1rem; display: flex; gap: 1rem;">
                <button @click="createAssignment" class="btn btn-success" :disabled="!isFormValid">
                  ➕ Create Assignment
                </button>
                <button @click="clearForm" class="btn btn-secondary">
                  🔄 Clear Form
                </button>
              </div>
            </div>
          </div>

          <!-- Active Assignments -->
          <div class="card">
            <div class="card-header">
              <h3>📋 Active Special Duty Assignments</h3>
              <div style="display: flex; gap: 1rem;">
                <select v-model="filters.type" class="form-control" style="width: auto;">
                  <option value="all">All Types</option>
                  <option value="vip_security">VIP Security</option>
                  <option value="event_management">Event Management</option>
                  <option value="traffic_control">Traffic Control</option>
                  <option value="investigation">Investigation</option>
                  <option value="emergency_response">Emergency Response</option>
                  <option value="patrol_duty">Patrol Duty</option>
                </select>
                <button @click="refreshAssignments" class="btn btn-primary">🔄 Refresh</button>
              </div>
            </div>
            <div class="card-body">
              <div v-if="filteredAssignments.length === 0" style="text-align: center; color: #6b7280; padding: 2rem;">
                <div style="font-size: 3rem; margin-bottom: 1rem;">📋</div>
                <div>No active assignments found</div>
              </div>
              
              <div v-else>
                <div 
                  v-for="assignment in filteredAssignments" 
                  :key="assignment.id"
                  :style="`border: 2px solid ${getPriorityColor(assignment.priority)}; border-radius: 8px; padding: 1.5rem; margin-bottom: 1rem; background: ${getPriorityBg(assignment.priority)};`"
                >
                  <div style="display: flex; justify-content: space-between; align-items: start;">
                    <div style="flex: 1;">
                      <!-- Assignment Header -->
                      <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem;">
                        <div style="font-size: 1.2rem; font-weight: bold;">{{ assignment.title }}</div>
                        <span :style="`padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.8rem; font-weight: 500; background: ${getPriorityColor(assignment.priority)}; color: white;`">
                          {{ assignment.priority.toUpperCase() }}
                        </span>
                        <span :style="`padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.8rem; font-weight: 500; background: ${getStatusColor(assignment.status)}; color: white;`">
                          {{ assignment.status.replace('_', ' ').toUpperCase() }}
                        </span>
                      </div>

                      <!-- Assignment Details -->
                      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-bottom: 1rem;">
                        <div>
                          <div style="font-size: 0.9rem; color: #6b7280; margin-bottom: 0.25rem;">Location</div>
                          <div>📍 {{ assignment.location }}</div>
                        </div>
                        <div>
                          <div style="font-size: 0.9rem; color: #6b7280; margin-bottom: 0.25rem;">Date & Time</div>
                          <div>📅 {{ formatDateTime(assignment.scheduledTime) }}</div>
                        </div>
                        <div>
                          <div style="font-size: 0.9rem; color: #6b7280; margin-bottom: 0.25rem;">Duration</div>
                          <div>⏱️ {{ assignment.duration }} hours</div>
                        </div>
                        <div>
                          <div style="font-size: 0.9rem; color: #6b7280; margin-bottom: 0.25rem;">Officers Assigned</div>
                          <div>👮 {{ assignment.assignedOfficers.length }}/{{ assignment.requiredOfficers }}</div>
                        </div>
                      </div>

                      <!-- Description -->
                      <div style="background: rgba(255,255,255,0.5); padding: 1rem; border-radius: 6px; margin-bottom: 1rem;">
                        <div>{{ assignment.description }}</div>
                      </div>

                      <!-- Assigned Officers -->
                      <div v-if="assignment.assignedOfficers.length" style="margin-bottom: 1rem;">
                        <div style="font-size: 0.9rem; color: #6b7280; margin-bottom: 0.5rem;">Assigned Officers</div>
                        <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                          <span 
                            v-for="officer in assignment.assignedOfficers" 
                            :key="officer"
                            style="padding: 0.25rem 0.5rem; background: #3b82f6; color: white; border-radius: 4px; font-size: 0.8rem;"
                          >
                            👮 {{ officer }}
                          </span>
                        </div>
                      </div>

                      <!-- Timestamps -->
                      <div style="font-size: 0.8rem; color: #9ca3af;">
                        Created: {{ formatDateTime(assignment.createdAt) }}
                        <span v-if="assignment.updatedAt">
                          | Updated: {{ formatDateTime(assignment.updatedAt) }}
                        </span>
                      </div>
                    </div>

                    <!-- Action Buttons -->
                    <div style="display: flex; flex-direction: column; gap: 0.5rem; min-width: 140px;">
                      <button 
                        v-if="assignment.assignedOfficers.length < assignment.requiredOfficers" 
                        @click="assignOfficers(assignment)" 
                        class="btn btn-primary btn-small"
                      >
                        👮 Assign Officers
                      </button>
                      <button @click="editAssignment(assignment)" class="btn btn-warning btn-small">
                        ✏️ Edit Assignment
                      </button>
                      <button @click="viewProgress(assignment)" class="btn btn-info btn-small">
                        📊 View Progress
                      </button>
                      <button @click="sendUpdates(assignment)" class="btn btn-secondary btn-small">
                        📢 Send Updates
                      </button>
                      <button 
                        v-if="assignment.status === 'active'" 
                        @click="completeAssignment(assignment)" 
                        class="btn btn-success btn-small"
                      >
                        ✅ Mark Complete
                      </button>
                      <button @click="cancelAssignment(assignment)" class="btn btn-danger btn-small">
                        ❌ Cancel
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Sidebar -->
        <div>
          <!-- Available Officers -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>👮 Available Officers</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; gap: 0.5rem;">
                <div 
                  v-for="officer in availableOfficers" 
                  :key="officer.id"
                  style="padding: 0.75rem; border: 1px solid #e5e7eb; border-radius: 6px; background: #f8fafc;"
                >
                  <div style="font-weight: 500;">{{ officer.name }}</div>
                  <div style="font-size: 0.8rem; color: #6b7280;">
                    {{ officer.rank }} | Badge: {{ officer.badge }}
                  </div>
                  <div style="font-size: 0.8rem; color: #6b7280;">
                    📍 {{ officer.currentLocation }}
                  </div>
                  <div :style="`font-size: 0.8rem; color: ${officer.availability === 'Available' ? '#16a34a' : '#f59e0b'};`">
                    {{ officer.availability }}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Quick Actions -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>⚡ Quick Actions</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; gap: 0.5rem;">
                <button @click="viewOfficerSchedules" class="action-btn">
                  📅 View Officer Schedules
                </button>
                <button @click="generateReport" class="action-btn">
                  📊 Generate Report
                </button>
                <button @click="requestAdditionalOfficers" class="action-btn">
                  👥 Request Officers
                </button>
                <button @click="viewAssignmentHistory" class="action-btn">
                  📜 Assignment History
                </button>
              </div>
            </div>
          </div>

          <!-- Assignment Templates -->
          <div class="card">
            <div class="card-header">
              <h3>📋 Assignment Templates</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; gap: 0.5rem;">
                <button 
                  v-for="template in assignmentTemplates" 
                  :key="template.id"
                  @click="useTemplate(template)"
                  class="template-btn"
                >
                  {{ template.icon }} {{ template.name }}
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
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

export default {
  name: 'SpecialDutyAssignmentView',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const filters = ref({
      type: 'all'
    })
    
    const newAssignment = ref({
      type: '',
      priority: 'medium',
      location: '',
      datetime: '',
      description: '',
      requiredOfficers: 1,
      duration: 8
    })
    
    const stats = ref({
      availableOfficers: 12,
      activeAssignments: 8,
      urgentRequests: 2,
      completedToday: 5
    })
    
    const assignments = ref([
      {
        id: 1,
        title: 'VIP Security Detail - Chief Minister Visit',
        type: 'vip_security',
        priority: 'urgent',
        status: 'active',
        location: 'Raj Bhavan, Mumbai',
        scheduledTime: new Date(Date.now() + 2 * 60 * 60 * 1000),
        duration: 6,
        requiredOfficers: 8,
        assignedOfficers: ['Sgt. Sharma', 'Sgt. Patel', 'Sgt. Kumar', 'Sgt. Singh', 'Sgt. Gupta'],
        description: 'Provide security coverage for Chief Minister\'s public address and meeting with local officials.',
        createdAt: new Date(Date.now() - 24 * 60 * 60 * 1000),
        updatedAt: new Date(Date.now() - 2 * 60 * 60 * 1000)
      },
      {
        id: 2,
        title: 'Marathon Event Traffic Control',
        type: 'event_management',
        priority: 'high',
        status: 'scheduled',
        location: 'Marine Drive to Bandra',
        scheduledTime: new Date(Date.now() + 48 * 60 * 60 * 1000),
        duration: 8,
        requiredOfficers: 15,
        assignedOfficers: ['Sgt. Mehta', 'Sgt. Joshi', 'Sgt. Rao'],
        description: 'Traffic management and route control for Mumbai Marathon. Coordinate with event organizers.',
        createdAt: new Date(Date.now() - 48 * 60 * 60 * 1000),
        updatedAt: null
      },
      {
        id: 3,
        title: 'Special Investigation - Bank Robbery',
        type: 'investigation',
        priority: 'high',
        status: 'active',
        location: 'Andheri West',
        scheduledTime: new Date(Date.now() - 4 * 60 * 60 * 1000),
        duration: 12,
        requiredOfficers: 4,
        assignedOfficers: ['Sgt. Verma', 'Sgt. Agarwal', 'Sgt. Nair', 'Sgt. Reddy'],
        description: 'Assist CID in investigation of bank robbery. Secure crime scene and gather witness statements.',
        createdAt: new Date(Date.now() - 6 * 60 * 60 * 1000),
        updatedAt: new Date(Date.now() - 1 * 60 * 60 * 1000)
      }
    ])
    
    const availableOfficers = ref([
      {
        id: 1,
        name: 'Sergeant Desai',
        rank: 'Sergeant',
        badge: 'SG015',
        currentLocation: 'Station Alpha',
        availability: 'Available'
      },
      {
        id: 2,
        name: 'Sergeant Iyer',
        rank: 'Sergeant',
        badge: 'SG016',
        currentLocation: 'Patrol Route 3',
        availability: 'Available'
      },
      {
        id: 3,
        name: 'Sergeant Chopra',
        rank: 'Sergeant',
        badge: 'SG017',
        currentLocation: 'Station Beta',
        availability: 'On Break'
      },
      {
        id: 4,
        name: 'Sergeant Kapoor',
        rank: 'Sergeant',
        badge: 'SG018',
        currentLocation: 'MG Road Junction',
        availability: 'Available'
      }
    ])
    
    const assignmentTemplates = ref([
      { id: 1, name: 'VIP Security', icon: '🛡️' },
      { id: 2, name: 'Traffic Control', icon: '🚦' },
      { id: 3, name: 'Event Management', icon: '🎉' },
      { id: 4, name: 'Emergency Response', icon: '🚨' },
      { id: 5, name: 'Investigation Support', icon: '🔍' },
      { id: 6, name: 'Patrol Duty', icon: '🚔' }
    ])
    
    const filteredAssignments = computed(() => {
      if (filters.value.type === 'all') {
        return assignments.value
      }
      return assignments.value.filter(assignment => assignment.type === filters.value.type)
    })
    
    const isFormValid = computed(() => {
      return newAssignment.value.type && 
             newAssignment.value.location && 
             newAssignment.value.datetime && 
             newAssignment.value.description
    })
    
    const goBack = () => {
      router.go(-1)
    }
    
    const createAssignment = () => {
      if (isFormValid.value) {
        const assignment = {
          id: assignments.value.length + 1,
          title: `${newAssignment.value.type.replace('_', ' ')} - ${newAssignment.value.location}`,
          type: newAssignment.value.type,
          priority: newAssignment.value.priority,
          status: 'scheduled',
          location: newAssignment.value.location,
          scheduledTime: new Date(newAssignment.value.datetime),
          duration: newAssignment.value.duration,
          requiredOfficers: newAssignment.value.requiredOfficers,
          assignedOfficers: [],
          description: newAssignment.value.description,
          createdAt: new Date(),
          updatedAt: null
        }
        
        assignments.value.unshift(assignment)
        clearForm()
        alert('Special duty assignment created successfully!')
      }
    }
    
    const clearForm = () => {
      newAssignment.value = {
        type: '',
        priority: 'medium',
        location: '',
        datetime: '',
        description: '',
        requiredOfficers: 1,
        duration: 8
      }
    }
    
    const refreshAssignments = () => {
      alert('Refreshing assignments...')
    }
    
    const assignOfficers = (assignment) => {
      alert(`Opening officer assignment for: ${assignment.title}`)
    }
    
    const editAssignment = (assignment) => {
      alert(`Editing assignment: ${assignment.title}`)
    }
    
    const viewProgress = (assignment) => {
      alert(`Viewing progress for: ${assignment.title}`)
    }
    
    const sendUpdates = (assignment) => {
      alert(`Sending updates for: ${assignment.title}`)
    }
    
    const completeAssignment = (assignment) => {
      if (confirm(`Mark assignment as complete: ${assignment.title}?`)) {
        assignment.status = 'completed'
        assignment.updatedAt = new Date()
        alert('Assignment marked as completed!')
      }
    }
    
    const cancelAssignment = (assignment) => {
      if (confirm(`Cancel assignment: ${assignment.title}?`)) {
        assignment.status = 'cancelled'
        assignment.updatedAt = new Date()
        alert('Assignment cancelled!')
      }
    }
    
    const viewOfficerSchedules = () => {
      alert('Opening officer schedules overview...')
    }
    
    const generateReport = () => {
      alert('Generating special duty assignments report...')
    }
    
    const requestAdditionalOfficers = () => {
      alert('Requesting additional officers from central command...')
    }
    
    const viewAssignmentHistory = () => {
      alert('Opening assignment history...')
    }
    
    const useTemplate = (template) => {
      alert(`Loading template: ${template.name}`)
      // In real app, populate form with template data
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
        'scheduled': '#f59e0b',
        'active': '#3b82f6',
        'completed': '#16a34a',
        'cancelled': '#dc2626'
      }
      return colors[status] || '#6b7280'
    }
    
    return {
      authStore,
      filters,
      newAssignment,
      stats,
      assignments,
      availableOfficers,
      assignmentTemplates,
      filteredAssignments,
      isFormValid,
      goBack,
      createAssignment,
      clearForm,
      refreshAssignments,
      assignOfficers,
      editAssignment,
      viewProgress,
      sendUpdates,
      completeAssignment,
      cancelAssignment,
      viewOfficerSchedules,
      generateReport,
      requestAdditionalOfficers,
      viewAssignmentHistory,
      useTemplate,
      formatDateTime,
      getPriorityColor,
      getPriorityBg,
      getStatusColor
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

.template-btn {
  width: 100%;
  padding: 0.75rem;
  background: #eff6ff;
  border: 1px solid #93c5fd;
  border-radius: 6px;
  color: #2563eb;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
}

.template-btn:hover {
  background: #dbeafe;
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

.btn-info {
  background: #f0f9ff;
  color: #0369a1;
  border: 1px solid #7dd3fc;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>