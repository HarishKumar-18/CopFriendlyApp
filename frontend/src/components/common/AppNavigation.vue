<template>
  <div>
    <!-- Mobile menu overlay -->
    <div 
      v-if="isMobileMenuOpen" 
      class="fixed inset-0 z-40 lg:hidden" 
      @click="closeMobileMenu"
    >
      <div class="fixed inset-0 bg-gray-600 bg-opacity-75"></div>
    </div>

    <!-- Sidebar -->
    <div 
      :class="[
        'fixed inset-y-0 left-0 z-50 w-64 bg-white shadow-lg transform transition-transform duration-300 ease-in-out lg:translate-x-0 lg:static lg:inset-0',
        isMobileMenuOpen ? 'translate-x-0' : '-translate-x-full'
      ]"
    >
      <!-- Logo and title -->
      <div class="flex items-center justify-between h-16 px-4 border-b border-gray-200">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <div class="w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center">
              <svg class="w-5 h-5 text-white" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </div>
          </div>
          <div class="ml-3">
            <h1 class="text-lg font-semibold text-gray-900">CopFriendly</h1>
            <p class="text-xs text-gray-500">ESeva Portal</p>
          </div>
        </div>
        
        <!-- Close button for mobile -->
        <button
          @click="closeMobileMenu"
          class="lg:hidden p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100"
        >
          <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- User info -->
      <div class="px-4 py-4 border-b border-gray-200">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <div class="w-10 h-10 bg-primary-100 rounded-full flex items-center justify-center">
              <span class="text-sm font-medium text-primary-600">
                {{ userInitials }}
              </span>
            </div>
          </div>
          <div class="ml-3">
            <p class="text-sm font-medium text-gray-900">{{ userName }}</p>
            <p class="text-xs text-gray-500">{{ userRoleLabel }}</p>
          </div>
        </div>
      </div>

      <!-- Navigation menu -->
      <nav class="mt-5 px-2 space-y-1">
        <template v-for="item in navigationItems" :key="item.name">
          <router-link
            v-if="!item.children && hasAccess(item.roles)"
            :to="item.href"
            :class="[
              isActive(item.href) 
                ? 'bg-primary-100 border-r-2 border-primary-500 text-primary-700' 
                : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900',
              'group flex items-center px-2 py-2 text-sm font-medium rounded-md'
            ]"
            @click="closeMobileMenu"
          >
            <component 
              :is="item.icon" 
              :class="[
                isActive(item.href) ? 'text-primary-500' : 'text-gray-400 group-hover:text-gray-500',
                'mr-3 h-5 w-5'
              ]"
            />
            {{ item.name }}
          </router-link>

          <!-- Submenu -->
          <div v-else-if="item.children && hasAccess(item.roles)" class="space-y-1">
            <button
              @click="toggleSubmenu(item.name)"
              :class="[
                'text-gray-600 hover:bg-gray-50 hover:text-gray-900 group flex items-center w-full px-2 py-2 text-sm font-medium rounded-md'
              ]"
            >
              <component 
                :is="item.icon" 
                class="text-gray-400 group-hover:text-gray-500 mr-3 h-5 w-5"
              />
              {{ item.name }}
              <svg 
                :class="[
                  openSubmenus[item.name] ? 'rotate-90' : '',
                  'ml-auto h-4 w-4 transform transition-transform'
                ]"
                fill="currentColor" 
                viewBox="0 0 20 20"
              >
                <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
              </svg>
            </button>
            
            <div v-show="openSubmenus[item.name]" class="space-y-1 pl-8">
              <router-link
                v-for="child in item.children"
                :key="child.name"
                :to="child.href"
                :class="[
                  isActive(child.href)
                    ? 'bg-primary-100 border-r-2 border-primary-500 text-primary-700'
                    : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900',
                  'group flex items-center px-2 py-2 text-sm font-medium rounded-md'
                ]"
                @click="closeMobileMenu"
              >
                {{ child.name }}
              </router-link>
            </div>
          </div>
        </template>
      </nav>

      <!-- SOS Button -->
      <div class="absolute bottom-4 left-4 right-4">
        <button
          @click="triggerSOS"
          :disabled="sosActive"
          :class="[
            'w-full py-3 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white',
            sosActive 
              ? 'bg-red-400 cursor-not-allowed' 
              : 'bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 sos-pulse'
          ]"
        >
          <svg class="w-5 h-5 inline mr-2" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
          </svg>
          {{ sosActive ? 'SOS Active' : 'Emergency SOS' }}
        </button>
      </div>
    </div>

    <!-- Top bar for mobile -->
    <div class="lg:hidden">
      <div class="bg-white shadow-sm border-b border-gray-200">
        <div class="px-4 sm:px-6 lg:px-8">
          <div class="flex justify-between h-16">
            <div class="flex items-center">
              <button
                @click="openMobileMenu"
                class="p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100"
              >
                <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
                </svg>
              </button>
              <h1 class="ml-3 text-xl font-semibold text-gray-900">CopFriendly ESeva</h1>
            </div>
            
            <div class="flex items-center space-x-4">
              <!-- Logout button -->
              <button
                @click="handleLogout"
                class="p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100"
              >
                <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useEmergenciesStore } from '@/stores/emergencies'
import { geolocationAPI } from '@/services/api'
import { formatters } from '@/utils/helpers'

// Icons (you can replace these with actual icon components)
const DashboardIcon = 'div'
const ViolationsIcon = 'div' 
const EmergencyIcon = 'div'
const CentralIcon = 'div'
const MedicalIcon = 'div'

export default {
  name: 'AppNavigation',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const authStore = useAuthStore()
    const emergenciesStore = useEmergenciesStore()
    
    const isMobileMenuOpen = ref(false)
    const openSubmenus = ref({})

    // Computed properties
    const userName = computed(() => authStore.userName || 'User')
    const userRole = computed(() => authStore.userRole)
    const userRoleLabel = computed(() => {
      const roleLabels = {
        'traffic_sergeant': 'Traffic Sergeant',
        'traffic_inspector': 'Traffic Inspector', 
        'traffic_central_team': 'Central Team',
        'medical_emergency_central': 'Medical Team'
      }
      return roleLabels[userRole.value] || 'User'
    })
    const userInitials = computed(() => {
      return userName.value
        .split(' ')
        .map(n => n[0])
        .join('')
        .toUpperCase()
        .slice(0, 2)
    })
    const sosActive = computed(() => emergenciesStore.sosActive)

    // Navigation items based on user role
    const navigationItems = computed(() => {
      const baseItems = [
        {
          name: 'Dashboard',
          href: '/dashboard',
          icon: DashboardIcon,
          roles: ['traffic_sergeant', 'traffic_inspector', 'traffic_central_team', 'medical_emergency_central']
        }
      ]

      // Traffic officer specific items
      if (userRole.value === 'traffic_sergeant' || userRole.value === 'traffic_inspector') {
        baseItems.push(
          {
            name: 'Violations',
            href: '/violations',
            icon: ViolationsIcon,
            roles: ['traffic_sergeant', 'traffic_inspector'],
            children: [
              { name: 'View All', href: '/violations' },
              { name: 'Register New', href: '/violations/create' }
            ]
          },
          {
            name: 'Emergencies',
            href: '/emergencies',
            icon: EmergencyIcon,
            roles: ['traffic_sergeant', 'traffic_inspector'],
            children: [
              { name: 'View All', href: '/emergencies' },
              { name: 'Register New', href: '/emergencies/create' }
            ]
          }
        )
      }

      // Central team specific items
      if (userRole.value === 'traffic_central_team') {
        baseItems.push({
          name: 'Central Control',
          href: '/central',
          icon: CentralIcon,
          roles: ['traffic_central_team']
        })
      }

      // Medical team specific items
      if (userRole.value === 'medical_emergency_central') {
        baseItems.push({
          name: 'Medical Control',
          href: '/medical',
          icon: MedicalIcon,
          roles: ['medical_emergency_central']
        })
      }

      return baseItems
    })

    // Methods
    const openMobileMenu = () => {
      isMobileMenuOpen.value = true
    }

    const closeMobileMenu = () => {
      isMobileMenuOpen.value = false
    }

    const toggleSubmenu = (name) => {
      openSubmenus.value[name] = !openSubmenus.value[name]
    }

    const isActive = (href) => {
      return route.path === href || route.path.startsWith(href + '/')
    }

    const hasAccess = (roles) => {
      if (!roles || roles.length === 0) return true
      return roles.includes(userRole.value)
    }

    const handleLogout = async () => {
      try {
        await authStore.logout()
        router.push('/login')
      } catch (error) {
        console.error('Logout error:', error)
      }
    }

    const triggerSOS = async () => {
      try {
        const location = await geolocationAPI.getCurrentLocation()
        const result = await emergenciesStore.triggerSOS(location)
        
        if (result.success) {
          // Navigate to SOS view or show confirmation
          router.push('/sos')
        }
      } catch (error) {
        console.error('SOS trigger error:', error)
        // Show error message
      }
    }

    return {
      isMobileMenuOpen,
      openSubmenus,
      userName,
      userRole,
      userRoleLabel,
      userInitials,
      sosActive,
      navigationItems,
      openMobileMenu,
      closeMobileMenu,
      toggleSubmenu,
      isActive,
      hasAccess,
      handleLogout,
      triggerSOS
    }
  }
}
</script>

<style scoped>
.sos-pulse {
  animation: pulse-red 2s infinite;
}

@keyframes pulse-red {
  0% {
    box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(239, 68, 68, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(239, 68, 68, 0);
  }
}
</style>