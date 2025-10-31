<template>
  <div>
    <!-- Enhanced Navigation Bar -->
    <nav style="
      background: #1e40af !important; 
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
        <h1 style="font-size: 1.5rem; font-weight: bold; margin: 0; color: white !important;">📊 Reports & Analytics</h1>
        <p style="margin: 0; color: white !important; opacity: 0.9;">{{ authStore.user?.role || 'Analyst' }} | Department: {{ authStore.user?.department || 'Traffic' }}</p>
      </div>
      <div style="display: flex; gap: 1rem; align-items: center;">
        <span style="color: white !important;">{{ authStore.user?.name || 'Analyst' }}</span>
        <button @click="navigateTo('/dashboard')" style="
          background: rgba(255,255,255,0.2) !important; 
          color: white !important; 
          padding: 0.5rem 1rem !important; 
          border: none !important; 
          border-radius: 0.25rem !important; 
          cursor: pointer !important;
          margin-right: 0.5rem !important;
        ">
          ← Dashboard
        </button>
        <button @click="scheduleReport" style="
          background: rgba(255,255,255,0.2) !important; 
          color: white !important; 
          padding: 0.5rem 1rem !important; 
          border: none !important; 
          border-radius: 0.25rem !important; 
          cursor: pointer !important;
          margin-right: 0.5rem !important;
        ">
          📅 Schedule
        </button>
        <button @click="logout" style="
          background: white !important; 
          color: #1e40af !important; 
          padding: 0.5rem 1rem !important; 
          border: none !important; 
          border-radius: 0.25rem !important; 
          cursor: pointer !important;
          font-weight: 600 !important;
        ">
          Logout
        </button>
      </div>
    </nav>

    <div class="dashboard">
      <!-- Report Filters -->
      <div class="card" style="margin-bottom: 2rem;">
        <div class="card-header">
          <h3>🔍 Report Filters</h3>
          <button @click="generateReport" class="btn btn-primary" :disabled="generating">
            {{ generating ? 'Generating...' : '📋 Generate Report' }}
          </button>
        </div>
        <div class="card-body">
          <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem;">
            <div class="form-group">
              <label for="reportType">Report Type</label>
              <select id="reportType" v-model="filters.reportType" class="form-control">
                <option value="violations">Violations Summary</option>
                <option value="revenue">Revenue Analysis</option>
                <option value="officers">Officer Performance</option>
                <option value="zones">Zone-wise Statistics</option>
                <option value="trends">Trend Analysis</option>
                <option value="compliance">Compliance Report</option>
              </select>
            </div>
            
            <div class="form-group">
              <label for="dateRange">Date Range</label>
              <select id="dateRange" v-model="filters.dateRange" class="form-control">
                <option value="today">Today</option>
                <option value="week">This Week</option>
                <option value="month">This Month</option>
                <option value="quarter">This Quarter</option>
                <option value="year">This Year</option>
                <option value="custom">Custom Range</option>
              </select>
            </div>
            
            <div class="form-group">
              <label for="zone">Zone/Area</label>
              <select id="zone" v-model="filters.zone" class="form-control">
                <option value="all">All Zones</option>
                <option value="zone-a">Zone A (Downtown)</option>
                <option value="zone-b">Zone B (Highway)</option>
                <option value="zone-c">Zone C (Residential)</option>
                <option value="zone-d">Zone D (Commercial)</option>
              </select>
            </div>
            
            <div class="form-group">
              <label for="violationType">Violation Type</label>
              <select id="violationType" v-model="filters.violationType" class="form-control">
                <option value="all">All Violations</option>
                <option value="speeding">Speeding</option>
                <option value="parking">Illegal Parking</option>
                <option value="signal">Signal Violation</option>
                <option value="documents">Document Issues</option>
                <option value="other">Others</option>
              </select>
            </div>
          </div>
          
          <!-- Custom Date Range -->
          <div v-if="filters.dateRange === 'custom'" style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 1rem; margin-top: 1rem;">
            <div class="form-group">
              <label for="startDate">Start Date</label>
              <input id="startDate" v-model="filters.startDate" type="date" class="form-control">
            </div>
            <div class="form-group">
              <label for="endDate">End Date</label>
              <input id="endDate" v-model="filters.endDate" type="date" class="form-control">
            </div>
          </div>
        </div>
      </div>

      <!-- Report Content -->
      <div style="display: grid; grid-template-columns: 1fr 350px; gap: 2rem;">
        <!-- Main Report Area -->
        <div>
          <!-- Summary Statistics -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>📈 {{ getReportTitle() }} Summary</h3>
              <div style="display: flex; gap: 1rem;">
                <button @click="exportReport('pdf')" class="btn">📄 Export PDF</button>
                <button @click="exportReport('excel')" class="btn">📊 Export Excel</button>
              </div>
            </div>
            <div class="card-body">
              <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-bottom: 2rem;">
                <div class="metric-card" style="background: linear-gradient(135deg, #3b82f6, #1d4ed8); color: white;">
                  <div style="font-size: 2rem; font-weight: bold;">{{ reportData.totalCount.toLocaleString() }}</div>
                  <div style="font-size: 0.9rem; opacity: 0.9;">Total {{ getReportUnit() }}</div>
                  <div style="font-size: 0.8rem; margin-top: 0.5rem;">{{ reportData.changePercent }}% vs last period</div>
                </div>
                
                <div class="metric-card" style="background: linear-gradient(135deg, #10b981, #047857); color: white;">
                  <div style="font-size: 2rem; font-weight: bold;">₹{{ reportData.revenue.toLocaleString() }}</div>
                  <div style="font-size: 0.9rem; opacity: 0.9;">Revenue Generated</div>
                  <div style="font-size: 0.8rem; margin-top: 0.5rem;">Average: ₹{{ Math.round(reportData.revenue / reportData.totalCount) }}</div>
                </div>
                
                <div class="metric-card" style="background: linear-gradient(135deg, #f59e0b, #d97706); color: white;">
                  <div style="font-size: 2rem; font-weight: bold;">{{ reportData.resolution }}%</div>
                  <div style="font-size: 0.9rem; opacity: 0.9;">Resolution Rate</div>
                  <div style="font-size: 0.8rem; margin-top: 0.5rem;">{{ reportData.pending }} pending</div>
                </div>
              </div>

              <!-- Chart Area -->
              <div style="height: 400px; background: #f8fafc; border: 1px solid #e5e7eb; border-radius: 8px; display: flex; align-items: center; justify-content: center;">
                <div style="text-align: center; color: #6b7280;">
                  <div style="font-size: 4rem; margin-bottom: 1rem;">📊</div>
                  <div style="font-size: 1.2rem; margin-bottom: 0.5rem;">{{ getReportTitle() }} Chart</div>
                  <div style="font-size: 0.9rem;">Interactive charts will be rendered here</div>
                  <div style="font-size: 0.8rem; color: #9ca3af; margin-top: 1rem;">
                    Chart.js / D3.js integration pending
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Detailed Data Table -->
          <div class="card">
            <div class="card-header">
              <h3>📋 Detailed Data</h3>
              <div style="display: flex; gap: 1rem; align-items: center;">
                <input 
                  v-model="searchTerm" 
                  type="text" 
                  placeholder="Search data..." 
                  class="form-control" 
                  style="width: 200px;"
                >
                <select v-model="sortBy" class="form-control" style="width: 150px;">
                  <option value="date">Sort by Date</option>
                  <option value="amount">Sort by Amount</option>
                  <option value="location">Sort by Location</option>
                </select>
              </div>
            </div>
            <div class="card-body">
              <div style="overflow-x: auto;">
                <table style="width: 100%; border-collapse: collapse;">
                  <thead>
                    <tr style="background: #f8fafc; border-bottom: 2px solid #e5e7eb;">
                      <th style="padding: 0.75rem; text-align: left; font-weight: 600;">Date</th>
                      <th style="padding: 0.75rem; text-align: left; font-weight: 600;">ID</th>
                      <th style="padding: 0.75rem; text-align: left; font-weight: 600;">Type</th>
                      <th style="padding: 0.75rem; text-align: left; font-weight: 600;">Location</th>
                      <th style="padding: 0.75rem; text-align: left; font-weight: 600;">Amount</th>
                      <th style="padding: 0.75rem; text-align: left; font-weight: 600;">Status</th>
                      <th style="padding: 0.75rem; text-align: left; font-weight: 600;">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr 
                      v-for="(item, index) in filteredTableData" 
                      :key="item.id"
                      :style="{ backgroundColor: index % 2 === 0 ? '#ffffff' : '#f8fafc', borderBottom: '1px solid #e5e7eb' }"
                    >
                      <td style="padding: 0.75rem;">{{ formatDate(item.date) }}</td>
                      <td style="padding: 0.75rem; font-family: monospace;">{{ item.id }}</td>
                      <td style="padding: 0.75rem;">{{ item.type }}</td>
                      <td style="padding: 0.75rem;">{{ item.location }}</td>
                      <td style="padding: 0.75rem; font-weight: 500;">₹{{ item.amount.toLocaleString() }}</td>
                      <td style="padding: 0.75rem;">
                        <span :class="getStatusClass(item.status)" style="padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.8rem;">
                          {{ item.status }}
                        </span>
                      </td>
                      <td style="padding: 0.75rem;">
                        <div style="display: flex; gap: 0.5rem;">
                          <button @click="viewDetails(item)" class="btn-small">👁️ View</button>
                          <button @click="exportSingle(item)" class="btn-small">📄 Export</button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
              
              <!-- Pagination -->
              <div style="display: flex; justify-content: between; align-items: center; margin-top: 1rem; padding-top: 1rem; border-top: 1px solid #e5e7eb;">
                <div style="color: #6b7280; font-size: 0.9rem;">
                  Showing {{ Math.min(currentPage * itemsPerPage, totalItems) }} of {{ totalItems }} entries
                </div>
                <div style="display: flex; gap: 0.5rem;">
                  <button 
                    @click="previousPage" 
                    :disabled="currentPage === 1"
                    class="btn btn-small"
                  >
                    ← Previous
                  </button>
                  <span style="padding: 0.5rem 1rem; background: #f3f4f6; border-radius: 4px;">
                    Page {{ currentPage }} of {{ totalPages }}
                  </span>
                  <button 
                    @click="nextPage" 
                    :disabled="currentPage === totalPages"
                    class="btn btn-small"
                  >
                    Next →
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Sidebar -->
        <div>
          <!-- Quick Stats -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>⚡ Quick Insights</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; gap: 1rem;">
                <div style="padding: 1rem; background: linear-gradient(135deg, #f0fdf4, #dcfce7); border-radius: 8px;">
                  <div style="font-size: 1.5rem; font-weight: bold; color: #166534;">{{ insights.topZone }}</div>
                  <div style="font-size: 0.9rem; color: #15803d;">Highest Activity Zone</div>
                </div>
                
                <div style="padding: 1rem; background: linear-gradient(135deg, #fef3c7, #fde68a); border-radius: 8px;">
                  <div style="font-size: 1.5rem; font-weight: bold; color: #a16207;">{{ insights.peakTime }}</div>
                  <div style="font-size: 0.9rem; color: #ca8a04;">Peak Violation Time</div>
                </div>
                
                <div style="padding: 1rem; background: linear-gradient(135deg, #ede9fe, #ddd6fe); border-radius: 8px;">
                  <div style="font-size: 1.5rem; font-weight: bold; color: #5b21b6;">{{ insights.efficiency }}%</div>
                  <div style="font-size: 0.9rem; color: #6d28d9;">System Efficiency</div>
                </div>
              </div>
            </div>
          </div>

          <!-- Report History -->
          <div class="card" style="margin-bottom: 2rem;">
            <div class="card-header">
              <h3>📚 Report History</h3>
              <button @click="clearHistory" class="btn" style="padding: 0.25rem 0.75rem; font-size: 0.8rem;">
                Clear
              </button>
            </div>
            <div class="card-body">
              <div style="max-height: 300px; overflow-y: auto;">
                <div 
                  v-for="report in reportHistory" 
                  :key="report.id"
                  style="padding: 0.75rem; margin-bottom: 0.5rem; background: #f8fafc; border-radius: 6px; border-left: 4px solid #3b82f6;"
                >
                  <div style="font-weight: 500; font-size: 0.9rem;">{{ report.name }}</div>
                  <div style="font-size: 0.8rem; color: #6b7280; margin: 0.25rem 0;">{{ report.type }}</div>
                  <div style="display: flex; justify-content: between; align-items: center;">
                    <div style="font-size: 0.7rem; color: #9ca3af;">{{ formatDate(report.createdAt) }}</div>
                    <div style="display: flex; gap: 0.5rem;">
                      <button @click="downloadReport(report)" class="btn-tiny">📥</button>
                      <button @click="shareReport(report)" class="btn-tiny">📤</button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Export Options -->
          <div class="card">
            <div class="card-header">
              <h3>📤 Export Options</h3>
            </div>
            <div class="card-body">
              <div style="display: grid; gap: 1rem;">
                <button @click="exportReport('pdf')" class="action-btn">
                  📄 PDF Report
                </button>
                <button @click="exportReport('excel')" class="action-btn">
                  📊 Excel Spreadsheet
                </button>
                <button @click="exportReport('csv')" class="action-btn">
                  📋 CSV Data
                </button>
                <button @click="exportReport('json')" class="action-btn">
                  🔗 JSON API
                </button>
                <button @click="emailReport" class="action-btn">
                  📧 Email Report
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'ReportsAnalyticsView',
  setup() {
    const router = useRouter()
    const generating = ref(false)
    const searchTerm = ref('')
    const sortBy = ref('date')
    const currentPage = ref(1)
    const itemsPerPage = ref(10)
    
    const filters = ref({
      reportType: 'violations',
      dateRange: 'month',
      zone: 'all',
      violationType: 'all',
      startDate: '',
      endDate: ''
    })
    
    // Mock report data
    const reportData = ref({
      totalCount: 1547,
      revenue: 3420000,
      resolution: 87,
      pending: 201,
      changePercent: 12
    })
    
    const insights = ref({
      topZone: 'Zone B',
      peakTime: '5-7 PM',
      efficiency: 94
    })
    
    const tableData = ref([
      {
        id: 'V001',
        date: new Date('2024-01-15'),
        type: 'Speeding',
        location: 'Highway 1',
        amount: 2000,
        status: 'Paid'
      },
      {
        id: 'V002',
        date: new Date('2024-01-14'),
        type: 'Parking',
        location: 'Downtown',
        amount: 500,
        status: 'Pending'
      },
      {
        id: 'V003',
        date: new Date('2024-01-14'),
        type: 'Signal',
        location: 'Main St',
        amount: 1000,
        status: 'Paid'
      },
      {
        id: 'V004',
        date: new Date('2024-01-13'),
        type: 'Documents',
        location: 'Zone A',
        amount: 1500,
        status: 'Contested'
      },
      {
        id: 'V005',
        date: new Date('2024-01-13'),
        type: 'Speeding',
        location: 'Zone B',
        amount: 3000,
        status: 'Paid'
      }
    ])
    
    const reportHistory = ref([
      {
        id: 1,
        name: 'Monthly Violations Report',
        type: 'Violations Summary',
        createdAt: new Date('2024-01-10'),
        format: 'PDF'
      },
      {
        id: 2,
        name: 'Revenue Analysis Q4',
        type: 'Revenue Analysis',
        createdAt: new Date('2024-01-05'),
        format: 'Excel'
      },
      {
        id: 3,
        name: 'Officer Performance Dec',
        type: 'Officer Performance',
        createdAt: new Date('2023-12-30'),
        format: 'PDF'
      }
    ])
    
    const filteredTableData = computed(() => {
      let filtered = tableData.value
      
      if (searchTerm.value) {
        filtered = filtered.filter(item => 
          item.id.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
          item.type.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
          item.location.toLowerCase().includes(searchTerm.value.toLowerCase())
        )
      }
      
      // Apply sorting
      filtered.sort((a, b) => {
        if (sortBy.value === 'date') {
          return b.date - a.date
        } else if (sortBy.value === 'amount') {
          return b.amount - a.amount
        } else if (sortBy.value === 'location') {
          return a.location.localeCompare(b.location)
        }
        return 0
      })
      
      // Apply pagination
      const start = (currentPage.value - 1) * itemsPerPage.value
      const end = start + itemsPerPage.value
      return filtered.slice(start, end)
    })
    
    const totalItems = computed(() => tableData.value.length)
    const totalPages = computed(() => Math.ceil(totalItems.value / itemsPerPage.value))
    
    const navigateTo = (path) => {
      router.push(path)
    }
    
    const logout = () => {
      authStore.logout()
      router.push('/login')
    }
    
    const generateReport = async () => {
      generating.value = true
      try {
        // Simulate report generation
        await new Promise(resolve => setTimeout(resolve, 2000))
        
        // Update report data based on filters
        reportData.value = {
          totalCount: Math.floor(Math.random() * 2000) + 1000,
          revenue: Math.floor(Math.random() * 5000000) + 2000000,
          resolution: Math.floor(Math.random() * 20) + 80,
          pending: Math.floor(Math.random() * 300) + 100,
          changePercent: Math.floor(Math.random() * 30) - 15
        }
        
        alert('Report generated successfully!')
      } catch (error) {
        console.error('Error generating report:', error)
        alert('Failed to generate report. Please try again.')
      } finally {
        generating.value = false
      }
    }
    
    const getReportTitle = () => {
      const titles = {
        violations: 'Violations',
        revenue: 'Revenue',
        officers: 'Officer Performance',
        zones: 'Zone Statistics',
        trends: 'Trend Analysis',
        compliance: 'Compliance'
      }
      return titles[filters.value.reportType] || 'Report'
    }
    
    const getReportUnit = () => {
      const units = {
        violations: 'Violations',
        revenue: 'Transactions',
        officers: 'Officers',
        zones: 'Zones',
        trends: 'Data Points',
        compliance: 'Checks'
      }
      return units[filters.value.reportType] || 'Items'
    }
    
    const exportReport = (format) => {
      alert(`Exporting ${getReportTitle()} report as ${format.toUpperCase()}...`)
    }
    
    const scheduleReport = () => {
      alert('Opening report scheduling dialog...')
    }
    
    const emailReport = () => {
      alert('Opening email composer...')
    }
    
    const viewDetails = (item) => {
      alert(`Viewing details for ${item.id}`)
    }
    
    const exportSingle = (item) => {
      alert(`Exporting single record: ${item.id}`)
    }
    
    const downloadReport = (report) => {
      alert(`Downloading: ${report.name}`)
    }
    
    const shareReport = (report) => {
      alert(`Sharing: ${report.name}`)
    }
    
    const clearHistory = () => {
      reportHistory.value = []
    }
    
    const previousPage = () => {
      if (currentPage.value > 1) {
        currentPage.value--
      }
    }
    
    const nextPage = () => {
      if (currentPage.value < totalPages.value) {
        currentPage.value++
      }
    }
    
    const formatDate = (date) => {
      return date.toLocaleDateString()
    }
    
    const getStatusClass = (status) => {
      const classes = {
        'Paid': 'status-completed',
        'Pending': 'status-warning',
        'Contested': 'status-info',
        'Cancelled': 'status-urgent'
      }
      return classes[status] || 'status-info'
    }
    
    return {
      generating,
      searchTerm,
      sortBy,
      currentPage,
      itemsPerPage,
      filters,
      reportData,
      insights,
      tableData,
      reportHistory,
      filteredTableData,
      totalItems,
      totalPages,
      authStore,
      navigateTo,
      logout,
      generateReport,
      getReportTitle,
      getReportUnit,
      exportReport,
      scheduleReport,
      emailReport,
      viewDetails,
      exportSingle,
      downloadReport,
      shareReport,
      clearHistory,
      previousPage,
      nextPage,
      formatDate,
      getStatusClass
    }
  }
}
</script>

<style scoped>
.metric-card {
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.action-btn {
  width: 100%;
  padding: 0.75rem 1rem;
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  color: #374151;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
}

.action-btn:hover {
  background: #e5e7eb;
  transform: translateY(-1px);
}

.btn-small {
  padding: 0.25rem 0.5rem;
  font-size: 0.8rem;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  cursor: pointer;
}

.btn-tiny {
  padding: 0.125rem 0.25rem;
  font-size: 0.7rem;
  background: none;
  border: none;
  cursor: pointer;
  border-radius: 2px;
}

.btn-tiny:hover {
  background: #e5e7eb;
}

.status-completed {
  background: #bbf7d0;
  color: #16a34a;
  border: 1px solid #4ade80;
}

.status-warning {
  background: #fed7aa;
  color: #ea580c;
  border: 1px solid #fb923c;
}

.status-info {
  background: #dbeafe;
  color: #2563eb;
  border: 1px solid #60a5fa;
}

.status-urgent {
  background: #fecaca;
  color: #dc2626;
  border: 1px solid #f87171;
}
</style>