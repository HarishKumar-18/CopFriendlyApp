import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { emergenciesAPI } from '@/services/api'

export const useEmergenciesStore = defineStore('emergencies', () => {
  // State
  const emergencies = ref([])
  const currentEmergency = ref(null)
  const isLoading = ref(false)
  const error = ref(null)
  const sosActive = ref(false)
  const lastSOSLocation = ref(null)

  // Getters
  const totalEmergencies = computed(() => emergencies.value.length)
  const activeEmergencies = computed(() => 
    emergencies.value.filter(e => e.status === 'active')
  )
  const pendingEmergencies = computed(() => 
    emergencies.value.filter(e => e.status === 'pending')
  )
  const resolvedEmergencies = computed(() => 
    emergencies.value.filter(e => e.status === 'resolved')
  )
  const highPriorityEmergencies = computed(() => 
    emergencies.value.filter(e => e.priority === 'high' && e.status !== 'resolved')
  )

  // Actions
  const fetchEmergencies = async (params = {}) => {
    try {
      isLoading.value = true
      error.value = null
      
      const response = await emergenciesAPI.getAll(params)
      
      if (response.data.success) {
        emergencies.value = response.data.emergencies
      } else {
        error.value = response.data.message || 'Failed to fetch emergencies'
      }
    } catch (err) {
      console.error('Fetch emergencies error:', err)
      error.value = err.response?.data?.message || 'Network error'
    } finally {
      isLoading.value = false
    }
  }

  const fetchEmergency = async (id) => {
    try {
      isLoading.value = true
      error.value = null
      
      const response = await emergenciesAPI.getById(id)
      
      if (response.data.success) {
        currentEmergency.value = response.data.emergency
      } else {
        error.value = response.data.message || 'Failed to fetch emergency'
      }
    } catch (err) {
      console.error('Fetch emergency error:', err)
      error.value = err.response?.data?.message || 'Network error'
    } finally {
      isLoading.value = false
    }
  }

  const createEmergency = async (emergencyData) => {
    try {
      isLoading.value = true
      error.value = null
      
      const response = await emergenciesAPI.create(emergencyData)
      
      if (response.data.success) {
        const newEmergency = response.data.emergency
        emergencies.value.unshift(newEmergency)
        return { success: true, emergency: newEmergency }
      } else {
        error.value = response.data.message || 'Failed to create emergency'
        return { success: false, message: error.value }
      }
    } catch (err) {
      console.error('Create emergency error:', err)
      error.value = err.response?.data?.message || 'Network error'
      return { success: false, message: error.value }
    } finally {
      isLoading.value = false
    }
  }

  const updateEmergency = async (id, emergencyData) => {
    try {
      isLoading.value = true
      error.value = null
      
      const response = await emergenciesAPI.update(id, emergencyData)
      
      if (response.data.success) {
        const updatedEmergency = response.data.emergency
        const index = emergencies.value.findIndex(e => e.id === id)
        if (index !== -1) {
          emergencies.value[index] = updatedEmergency
        }
        if (currentEmergency.value?.id === id) {
          currentEmergency.value = updatedEmergency
        }
        return { success: true, emergency: updatedEmergency }
      } else {
        error.value = response.data.message || 'Failed to update emergency'
        return { success: false, message: error.value }
      }
    } catch (err) {
      console.error('Update emergency error:', err)
      error.value = err.response?.data?.message || 'Network error'
      return { success: false, message: error.value }
    } finally {
      isLoading.value = false
    }
  }

  const triggerSOS = async (location) => {
    try {
      sosActive.value = true
      lastSOSLocation.value = location
      
      const sosData = {
        type: 'sos',
        priority: 'high',
        location: location,
        description: 'SOS Alert triggered by officer',
        timestamp: new Date().toISOString()
      }
      
      const response = await emergenciesAPI.triggerSOS(sosData)
      
      if (response.data.success) {
        const sosEmergency = response.data.emergency
        emergencies.value.unshift(sosEmergency)
        return { success: true, emergency: sosEmergency }
      } else {
        sosActive.value = false
        error.value = response.data.message || 'Failed to trigger SOS'
        return { success: false, message: error.value }
      }
    } catch (err) {
      console.error('SOS trigger error:', err)
      sosActive.value = false
      error.value = err.response?.data?.message || 'Network error'
      return { success: false, message: error.value }
    }
  }

  const cancelSOS = async () => {
    try {
      if (lastSOSLocation.value) {
        await emergenciesAPI.cancelSOS()
      }
    } catch (err) {
      console.error('Cancel SOS error:', err)
    } finally {
      sosActive.value = false
      lastSOSLocation.value = null
    }
  }

  const dispatchTeam = async (emergencyId, teamData) => {
    try {
      isLoading.value = true
      error.value = null
      
      const response = await emergenciesAPI.dispatchTeam(emergencyId, teamData)
      
      if (response.data.success) {
        // Update the emergency status
        const index = emergencies.value.findIndex(e => e.id === emergencyId)
        if (index !== -1) {
          emergencies.value[index].status = 'dispatched'
          emergencies.value[index].dispatchedTeam = teamData
        }
        return { success: true }
      } else {
        error.value = response.data.message || 'Failed to dispatch team'
        return { success: false, message: error.value }
      }
    } catch (err) {
      console.error('Dispatch team error:', err)
      error.value = err.response?.data?.message || 'Network error'
      return { success: false, message: error.value }
    } finally {
      isLoading.value = false
    }
  }

  const clearError = () => {
    error.value = null
  }

  const clearCurrentEmergency = () => {
    currentEmergency.value = null
  }

  return {
    // State
    emergencies,
    currentEmergency,
    isLoading,
    error,
    sosActive,
    lastSOSLocation,
    
    // Getters
    totalEmergencies,
    activeEmergencies,
    pendingEmergencies,
    resolvedEmergencies,
    highPriorityEmergencies,
    
    // Actions
    fetchEmergencies,
    fetchEmergency,
    createEmergency,
    updateEmergency,
    triggerSOS,
    cancelSOS,
    dispatchTeam,
    clearError,
    clearCurrentEmergency
  }
})