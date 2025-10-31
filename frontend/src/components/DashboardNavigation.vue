<template>
  <nav class="bg-gradient-to-r from-blue-600 to-blue-800 text-white shadow-lg">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between items-center h-16">
        <!-- Left side - Title and icon -->
        <div class="flex items-center space-x-4">
          <div class="flex-shrink-0">
            <span class="text-2xl">{{ icon }}</span>
          </div>
          <div>
            <h1 class="text-xl font-bold tracking-tight">{{ title }}</h1>
            <p v-if="subtitle" class="text-sm text-blue-100">{{ subtitle }}</p>
          </div>
        </div>

        <!-- Center - Quick actions (optional) -->
        <div v-if="showQuickActions" class="hidden md:flex items-center space-x-4">
          <slot name="quick-actions"></slot>
        </div>

        <!-- Right side - User info and actions -->
        <div class="flex items-center space-x-4">
          <!-- Notifications -->
          <button 
            v-if="showNotifications" 
            class="relative p-2 rounded-full hover:bg-blue-700 transition-colors duration-200"
            @click="$emit('notifications-clicked')"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                    d="M15 17h5l-5 5v-5zm-5-9a5 5 0 1110 0v3.5l1.5 1.5v1H4v-1L5.5 11.5V8z"/>
            </svg>
            <span v-if="notificationCount > 0" 
                  class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
              {{ notificationCount }}
            </span>
          </button>

          <!-- User info -->
          <div class="flex items-center space-x-3 bg-white bg-opacity-10 rounded-lg px-3 py-2">
            <div class="w-8 h-8 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
              <span class="text-sm font-semibold">{{ userInitials }}</span>
            </div>
            <div class="hidden sm:block text-right">
              <p class="text-sm font-medium">{{ userName }}</p>
              <p class="text-xs text-blue-100">{{ userRole }}</p>
            </div>
          </div>

          <!-- Actions dropdown -->
          <div class="relative">
            <button 
              @click="showDropdown = !showDropdown"
              class="p-2 rounded-full hover:bg-blue-700 transition-colors duration-200"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
              </svg>
            </button>
            
            <!-- Dropdown menu -->
            <div v-if="showDropdown" 
                 class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-50"
                 @click="showDropdown = false">
              <a href="#" @click="$emit('profile-clicked')" 
                 class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                👤 Profile
              </a>
              <a href="#" @click="$emit('settings-clicked')" 
                 class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                ⚙️ Settings
              </a>
              <a href="#" @click="$emit('help-clicked')" 
                 class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                ❓ Help
              </a>
              <hr class="my-1">
              <a href="#" @click="$emit('logout-clicked')" 
                 class="block px-4 py-2 text-sm text-red-600 hover:bg-red-50">
                🚪 Logout
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'DashboardNavigation',
  emits: ['notifications-clicked', 'profile-clicked', 'settings-clicked', 'help-clicked', 'logout-clicked'],
  props: {
    title: {
      type: String,
      required: true
    },
    subtitle: {
      type: String,
      default: ''
    },
    icon: {
      type: String,
      required: true
    },
    userName: {
      type: String,
      default: 'User'
    },
    userRole: {
      type: String,
      default: 'Officer'
    },
    showNotifications: {
      type: Boolean,
      default: true
    },
    notificationCount: {
      type: Number,
      default: 0
    },
    showQuickActions: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const showDropdown = ref(false)
    
    const userInitials = computed(() => {
      return props.userName
        .split(' ')
        .map(name => name.charAt(0))
        .join('')
        .toUpperCase()
        .slice(0, 2)
    })
    
    return {
      showDropdown,
      userInitials
    }
  }
}
</script>

<style scoped>
/* Additional custom styles if needed */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>