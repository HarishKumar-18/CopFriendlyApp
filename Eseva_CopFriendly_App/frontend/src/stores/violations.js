import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { violationsAPI } from '@/services/api'

export const useViolationsStore = defineStore('violations', () => {
  // State
  const violations = ref([])
  const currentViolation = ref(null)
  const isLoading = ref(false)
  const error = ref(null)
  const pagination = ref({
    currentPage: 1,
    totalPages: 1,
    totalItems: 0,
    itemsPerPage: 10
  })

  // Getters
  const totalViolations = computed(() => violations.value.length)
  const pendingViolations = computed(() => 
    violations.value.filter(v => v.status === 'pending')
  )
  const ticketedViolations = computed(() => 
    violations.value.filter(v => v.status === 'ticketed')
  )
  const paidViolations = computed(() => 
    violations.value.filter(v => v.status === 'paid')
  )

  // Actions
  const fetchViolations = async (params = {}) => {
    try {
      isLoading.value = true
      error.value = null
      
      const response = await violationsAPI.getAll(params)
      
      if (response.data.success) {
        violations.value = response.data.violations
        pagination.value = response.data.pagination
      } else {
        error.value = response.data.message || 'Failed to fetch violations'
      }
    } catch (err) {
      console.error('Fetch violations error:', err)
      error.value = err.response?.data?.message || 'Network error'
    } finally {
      isLoading.value = false
    }
  }

  const fetchViolation = async (id) => {
    try {
      isLoading.value = true
      error.value = null
      
      const response = await violationsAPI.getById(id)
      
      if (response.data.success) {
        currentViolation.value = response.data.violation
      } else {
        error.value = response.data.message || 'Failed to fetch violation'
      }
    } catch (err) {
      console.error('Fetch violation error:', err)
      error.value = err.response?.data?.message || 'Network error'
    } finally {
      isLoading.value = false
    }
  }

  const createViolation = async (violationData) => {
    try {
      isLoading.value = true
      error.value = null
      
      const response = await violationsAPI.create(violationData)
      
      if (response.data.success) {
        const newViolation = response.data.violation
        violations.value.unshift(newViolation)
        return { success: true, violation: newViolation }
      } else {
        error.value = response.data.message || 'Failed to create violation'
        return { success: false, message: error.value }
      }
    } catch (err) {
      console.error('Create violation error:', err)
      error.value = err.response?.data?.message || 'Network error'
      return { success: false, message: error.value }
    } finally {
      isLoading.value = false
    }
  }

  const updateViolation = async (id, violationData) => {
    try {
      isLoading.value = true
      error.value = null
      
      const response = await violationsAPI.update(id, violationData)
      
      if (response.data.success) {
        const updatedViolation = response.data.violation
        const index = violations.value.findIndex(v => v.id === id)
        if (index !== -1) {
          violations.value[index] = updatedViolation
        }
        if (currentViolation.value?.id === id) {
          currentViolation.value = updatedViolation
        }
        return { success: true, violation: updatedViolation }
      } else {
        error.value = response.data.message || 'Failed to update violation'
        return { success: false, message: error.value }
      }
    } catch (err) {
      console.error('Update violation error:', err)
      error.value = err.response?.data?.message || 'Network error'
      return { success: false, message: error.value }
    } finally {
      isLoading.value = false
    }
  }

  const deleteViolation = async (id) => {
    try {
      isLoading.value = true
      error.value = null
      
      const response = await violationsAPI.delete(id)
      
      if (response.data.success) {
        violations.value = violations.value.filter(v => v.id !== id)
        if (currentViolation.value?.id === id) {
          currentViolation.value = null
        }
        return { success: true }
      } else {
        error.value = response.data.message || 'Failed to delete violation'
        return { success: false, message: error.value }
      }
    } catch (err) {
      console.error('Delete violation error:', err)
      error.value = err.response?.data?.message || 'Network error'
      return { success: false, message: error.value }
    } finally {
      isLoading.value = false
    }
  }

  const clearError = () => {
    error.value = null
  }

  const clearCurrentViolation = () => {
    currentViolation.value = null
  }

  return {
    // State
    violations,
    currentViolation,
    isLoading,
    error,
    pagination,
    
    // Getters
    totalViolations,
    pendingViolations,
    ticketedViolations,
    paidViolations,
    
    // Actions
    fetchViolations,
    fetchViolation,
    createViolation,
    updateViolation,
    deleteViolation,
    clearError,
    clearCurrentViolation
  }
})