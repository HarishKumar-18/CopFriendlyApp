import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// Import views
import SimpleLoginView from '@/views/SimpleLoginView.vue'
import SignupView from '@/views/SignupView.vue'
import SimpleDashboardView from '@/views/SimpleDashboardView.vue'
import ViolationsListView from '@/views/ViolationsListView.vue'
import CreateViolationFormView from '@/views/CreateViolationFormView.vue'
import IssueTicketView from '@/views/IssueTicketView.vue'
import EmergencyManagementView from '@/views/EmergencyManagementView.vue'
import CentralTeamDashboardView from '@/views/CentralTeamDashboardView.vue'
import ReportsAnalyticsView from '@/views/ReportsAnalyticsView.vue'
import SOSView from '@/views/SOSView.vue'
import PaymentView from '@/views/PaymentView.vue'
import AdminDashboardView from '@/views/AdminDashboardView.vue'
import PublicViolationsView from '@/views/PublicViolationsView.vue'
import PendingPaymentsView from '@/views/public/PendingPaymentsView.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: SimpleLoginView,
    meta: { 
      requiresAuth: false,
      title: 'Login - CopFriendly ESeva'
    }
  },
  {
    path: '/signup',
    name: 'Signup',
    component: SignupView,
    meta: { 
      requiresAuth: false,
      title: 'Sign Up - CopFriendly ESeva'
    }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: SimpleDashboardView,
    meta: { 
      requiresAuth: true,
      title: 'Dashboard - CopFriendly ESeva'
    }
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: AdminDashboardView,
    meta: { 
      requiresAuth: true,
      roles: ['admin'],
      title: 'Admin Dashboard - CopFriendly ESeva'
    }
  },
  {
    path: '/public/violations',
    name: 'PublicViolations',
    component: PublicViolationsView,
    meta: {
      requiresAuth: false,
      title: 'Public - Unpaid Violations'
    }
  },
  {
    path: '/public/payments',
    name: 'PendingPayments',
    component: PendingPaymentsView,
    meta: {
      requiresAuth: false,
      title: 'Public - Pending Payments'
    }
  },
  {
    path: '/public/payment-history',
    name: 'PaymentHistory',
    component: () => import('@/views/public/PaymentHistoryView.vue'),
    meta: {
      requiresAuth: false,
      title: 'Public - Payment History'
    }
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: () => import('@/views/AdminUsersView.vue'),
    meta: {
      requiresAuth: true,
      roles: ['admin'],
      title: 'Admin - Users'
    }
  },
  {
    path: '/violations',
    name: 'Violations',
    component: ViolationsListView,
    meta: { 
      requiresAuth: true,
      roles: ['traffic_sergeant', 'traffic_inspector'],
      title: 'Traffic Violations - CopFriendly ESeva'
    }
  },
  {
    path: '/violations/create',
    name: 'CreateViolation',
    component: CreateViolationFormView,
    meta: { 
      requiresAuth: true,
      roles: ['traffic_sergeant', 'traffic_inspector'],
      title: 'Register Violation - CopFriendly ESeva'
    }
  },
  {
    path: '/violations/:id/ticket',
    name: 'IssueTicket',
    component: IssueTicketView,
    meta: { 
      requiresAuth: true,
      roles: ['traffic_sergeant', 'traffic_inspector'],
      title: 'Issue Ticket - CopFriendly ESeva'
    }
  },
  {
    path: '/emergencies',
    name: 'Emergencies',
    component: EmergencyManagementView,
    meta: { 
      requiresAuth: true,
      roles: ['traffic_sergeant', 'traffic_sergent', 'traffic_inspector', 'medical_emergency', 'medical_emergency_central'],
      title: 'Emergency Management - CopFriendly ESeva'
    }
  },
  {
    path: '/emergencies/create',
    name: 'CreateEmergency',
    component: () => import('@/views/CreateEmergencyFormView.vue'),
    meta: { 
      requiresAuth: true,
      roles: ['traffic_sergeant', 'traffic_sergent', 'traffic_inspector', 'medical_emergency', 'medical_emergency_central'],
      title: 'Register Emergency - CopFriendly ESeva'
    }
  },
  {
    path: '/central',
    name: 'CentralDashboard',
    component: CentralTeamDashboardView,
    meta: { 
      requiresAuth: true,
      roles: ['traffic_central_team'],
      title: 'Central Dashboard - CopFriendly ESeva'
    }
  },
  {
    path: '/dashboard/sergeant',
    name: 'TrafficSergeantDashboard',
    component: () => import('@/views/TrafficSergeantDashboardView.vue'),
    meta: { 
      requiresAuth: true,
      roles: ['traffic_sergeant'],
      title: 'Sergeant Dashboard - CopFriendly ESeva'
    }
  },
  {
    path: '/dashboard/inspector',
    name: 'TrafficInspectorDashboard',
    component: () => import('@/views/TrafficInspectorDashboardView.vue'),
    meta: { 
      requiresAuth: true,
      roles: ['traffic_inspector'],
      title: 'Inspector Dashboard - CopFriendly ESeva'
    }
  },
  {
    path: '/dashboard/medical',
    name: 'MedicalEmergencyDashboard',
    component: () => import('@/views/MedicalEmergencyDashboardView.vue'),
    meta: { 
      requiresAuth: true,
      roles: ['medical_emergency', 'medical_emergency_central'],
      title: 'Medical Emergency Dashboard - CopFriendly ESeva'
    }
  },
  {
    path: '/reports',
    name: 'Reports',
    component: ReportsAnalyticsView,
    meta: { 
      requiresAuth: true,
      roles: ['traffic_central_team', 'traffic_inspector'],
      title: 'Reports & Analytics - CopFriendly ESeva'
    }
  },
  {
    path: '/sos',
    name: 'SOS',
    component: SOSView,
    meta: { 
      requiresAuth: true,
      title: 'SOS Alert - CopFriendly ESeva'
    }
  },
  {
    path: '/payment/:ticketId',
    name: 'Payment',
    component: PaymentView,
    meta: { 
      requiresAuth: true,
      title: 'Payment - CopFriendly ESeva'
    }
  },
  {
    path: '/violation-approval',
    name: 'ViolationApproval',
    component: () => import('@/views/ViolationApprovalView.vue'),
    meta: { 
      requiresAuth: true,
      roles: ['traffic_inspector'],
      title: 'Violation Approval - CopFriendly ESeva'
    }
  },
  {
    path: '/dispute-resolution',
    name: 'DisputeResolution',
    component: () => import('@/views/DisputeResolutionView.vue'),
    meta: { 
      requiresAuth: true,
      roles: ['traffic_inspector', 'traffic_central_team'],
      title: 'Dispute Resolution - CopFriendly ESeva'
    }
  },
  {
    path: '/special-duty-assignment',
    name: 'SpecialDutyAssignment',
    component: () => import('@/views/SpecialDutyAssignmentView.vue'),
    meta: { 
      requiresAuth: true,
      roles: ['traffic_inspector', 'traffic_central_team'],
      title: 'Special Duty Assignment - CopFriendly ESeva'
    }
  },
  {
    path: '/traffic-monitoring',
    name: 'TrafficMonitoring',
    component: () => import('@/views/TrafficMonitoringView.vue'),
    meta: { 
      requiresAuth: true,
      roles: ['traffic_sergeant', 'traffic_inspector', 'traffic_central_team'],
      title: 'Traffic Monitoring - CopFriendly ESeva'
    }
  },
  {
    path: '/personnel-management',
    name: 'PersonnelManagement',
    component: () => import('@/views/PersonnelManagementView.vue'),
    meta: { 
      requiresAuth: true,
      roles: ['traffic_inspector', 'traffic_central_team'],
      title: 'Personnel Management - CopFriendly ESeva'
    }
  },
  {
    path: '/emergency-alert-system',
    name: 'EmergencyAlertSystem',
    component: () => import('@/views/EmergencyAlertSystemView.vue'),
    meta: { 
      requiresAuth: true,
      roles: ['traffic_central_team', 'medical_emergency'],
      title: 'Emergency Alert System - CopFriendly ESeva'
    }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation Guards
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // Set page title
  if (to.meta.title) {
    document.title = to.meta.title
  }
  
  // Check if route requires authentication
  if (to.meta.requiresAuth) {
    if (!authStore.isAuthenticated) {
      next('/login')
      return
    }
    
    // Check role-based access
    if (to.meta.roles && !to.meta.roles.includes(authStore.user?.role)) {
      next('/dashboard')
      return
    }
  }
  
  // Redirect to dashboard if already logged in and trying to access login
  if (to.name === 'Login' && authStore.isAuthenticated) {
    next('/dashboard')
    return
  }
  
  next()
})

export default router