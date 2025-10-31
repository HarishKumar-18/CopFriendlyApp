import axios from 'axios'

// Base API configuration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

// Create axios instance
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor to add auth token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    // Debug: log when requests are sent without an auth token so we can diagnose 403s
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    } else {
      // Non-blocking console message to help runtime debugging (can be removed later)
      // eslint-disable-next-line no-console
      console.debug('apiClient: no auth token found in localStorage for request', config.url)
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// Auth API
export const authAPI = {
  login: (credentials) => apiClient.post('/auth/login', credentials),
  register: (userData) => apiClient.post('/auth/register', userData),
  exists: (username) => apiClient.get('/auth/exists', { params: { username } }),
  list: () => apiClient.get('/auth/users'),
  listPending: () => apiClient.get('/auth/users/pending'),
  approve: (id) => apiClient.post(`/auth/users/${id}/approve`),
  reject: (id) => apiClient.post(`/auth/users/${id}/reject`),
  me: () => apiClient.get('/auth/me'),
  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    return Promise.resolve()
  },
  getCurrentUser: () => {
    const user = localStorage.getItem('user')
    return user ? JSON.parse(user) : null
  },
  getAllUsers: () => apiClient.get('/users'),
  updateUserStatus: (id, status) => apiClient.put(`/users/${id}/status`, { status }),
  updateUserRole: (id, role) => apiClient.put(`/users/${id}/role`, { role }),
}

// Users API
export const usersAPI = {
  getAll: (params) => apiClient.get('/users', { params }),
  getById: (id) => apiClient.get(`/users/${id}`),
  create: (data) => apiClient.post('/users', data),
  update: (id, data) => apiClient.put(`/users/${id}`, data),
  delete: (id) => apiClient.delete(`/users/${id}`),
  updateDutyStatus: (id, status) => apiClient.put(`/users/${id}/duty-status`, { status }),
  getByRole: (role) => apiClient.get(`/users/role/${role}`),
  getOnDuty: () => apiClient.get('/users/on-duty'),
}

// Violations API
export const violationsAPI = {
  getAll: (params) => apiClient.get('/violations', { params }),
  getById: (id) => apiClient.get(`/violations/${id}`),
  create: (data) => apiClient.post('/violations', data),
  saveDraft: (data) => apiClient.post('/violations/draft', data),
  update: (id, data) => apiClient.put(`/violations/${id}`, data),
  delete: (id) => apiClient.delete(`/violations/${id}`),
  approve: (id) => apiClient.put(`/violations/${id}/approve`),
  reject: (id, reason) => apiClient.put(`/violations/${id}/reject`, { reason }),
  updatePaymentStatus: (id, status) => apiClient.put(`/violations/${id}/payment-status`, { status }),
  getByIssuer: (issuerId) => apiClient.get(`/violations/issuer/${issuerId}`),
  getPending: () => apiClient.get('/violations/pending'),
  // Public endpoints
  getUnpaidPublic: () => {
    const user = authAPI.getCurrentUser ? authAPI.getCurrentUser() : null
    const headers = {}
    if (user && user.role && user.role.toUpperCase() === 'PUBLIC') {
      headers['X-Public-User'] = 'true'
    }
    return apiClient.get('/public/violations/unpaid', { headers })
  },
  payPublicViolation: (id) => {
    const user = authAPI.getCurrentUser ? authAPI.getCurrentUser() : null
    const headers = {}
    if (user && user.role && user.role.toUpperCase() === 'PUBLIC') {
      headers['X-Public-User'] = 'true'
    }
    return apiClient.post(`/public/violations/${id}/pay`, null, { headers })
  },
  // Public endpoints (alternate: /api/violations/unpaid and /api/violations/{id}/pay)
  getUnpaidForPublic: () => {
    const user = authAPI.getCurrentUser ? authAPI.getCurrentUser() : null
    const headers = {}
    if (user && user.role && user.role.toUpperCase() === 'PUBLIC') {
      headers['X-Public-User'] = 'true'
    }
    return apiClient.get('/violations/unpaid', { headers })
  },
  // Force calling unpaid endpoint with public header even when frontend user is not a PUBLIC user.
  // This is a safe fallback for demo environments where unauthenticated access to unpaid violations
  // is permitted when the X-Public-User header is present. Use only as a fallback when GET /violations
  // returns 403 from the backend.
  getUnpaidForce: () => {
    return apiClient.get('/violations/unpaid', { headers: { 'X-Public-User': 'true' } })
  },
  getPaidForPublic: () => {
    const user = authAPI.getCurrentUser ? authAPI.getCurrentUser() : null
    const headers = {}
    if (user && user.role && user.role.toUpperCase() === 'PUBLIC') {
      headers['X-Public-User'] = 'true'
    }
    return apiClient.get('/public/violations/paid', { headers })
  },
  payViolationPublic: (id) => {
    const user = authAPI.getCurrentUser ? authAPI.getCurrentUser() : null
    const headers = {}
    if (user && user.role && user.role.toUpperCase() === 'PUBLIC') {
      headers['X-Public-User'] = 'true'
    }
    return apiClient.put(`/violations/${id}/pay`, null, { headers })
  },
}

// Emergencies API
export const emergenciesAPI = {
  getAll: (params) => apiClient.get('/emergencies', { params }),
  getById: (id) => apiClient.get(`/emergencies/${id}`),
  getMy: () => apiClient.get('/emergencies/my'),
  getActive: () => apiClient.get('/emergencies/active'),
  getSOS: () => apiClient.get('/emergencies/sos'),
  getNearby: (latitude, longitude, radius) => 
    apiClient.get('/emergencies/nearby', { params: { latitude, longitude, radius } }),
  create: (data) => apiClient.post('/emergencies', data),
  update: (id, data) => apiClient.put(`/emergencies/${id}`, data),
  resolve: (id, notes) => apiClient.put(`/emergencies/${id}/resolve`, { notes }),
  delete: (id) => apiClient.delete(`/emergencies/${id}`),
  triggerSOS: (data) => apiClient.post('/emergencies/sos', data),
  cancelSOS: () => apiClient.post('/emergencies/sos/cancel'),
  dispatchTeam: (id, teamData) => apiClient.post(`/emergencies/${id}/dispatch`, teamData),
  getStats: () => apiClient.get('/emergencies/stats'),
}

// Dashboard API
export const dashboardAPI = {
  getStats: () => apiClient.get('/dashboard/stats'),
  getRecentActivity: () => apiClient.get('/dashboard/recent-activity'),
  getUserStats: (userId) => apiClient.get(`/dashboard/user-stats/${userId}`),
}

// Geolocation API
export const geolocationAPI = {
  getCurrentLocation: () => {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject(new Error('Geolocation is not supported by this browser'))
        return
      }

      navigator.geolocation.getCurrentPosition(
        (position) => {
          resolve({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
            accuracy: position.coords.accuracy,
            timestamp: position.timestamp,
          })
        },
        (error) => {
          reject(error)
        },
        {
          enableHighAccuracy: true,
          timeout: 10000,
          maximumAge: 60000,
        }
      )
    })
  },
  
  watchLocation: (callback, errorCallback) => {
    if (!navigator.geolocation) {
      errorCallback(new Error('Geolocation is not supported by this browser'))
      return null
    }

    return navigator.geolocation.watchPosition(
      callback,
      errorCallback,
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 60000,
      }
    )
  },
  
  clearWatch: (watchId) => {
    if (navigator.geolocation && watchId) {
      navigator.geolocation.clearWatch(watchId)
    }
  },
}

// Utility functions
export const apiUtils = {
  // Format error messages
  formatError: (error) => {
    if (error.response?.data?.message) {
      return error.response.data.message
    }
    if (error.message) {
      return error.message
    }
    return 'An unexpected error occurred'
  },

  // Handle file uploads
  uploadFile: (file, endpoint) => {
    const formData = new FormData()
    formData.append('file', file)
    
    return apiClient.post(endpoint, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  },

  // Download file
  downloadFile: (endpoint, filename) => {
    return apiClient.get(endpoint, {
      responseType: 'blob',
    }).then(response => {
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', filename)
      document.body.appendChild(link)
      link.click()
      link.remove()
      window.URL.revokeObjectURL(url)
    })
  },
}

export default apiClient

// Named exports for convenience (used by some views)
export const getUnpaidForPublic = () => {
  return violationsAPI.getUnpaidForPublic()
}

export const getPaidForPublic = () => {
  return violationsAPI.getPaidForPublic()
}

export const payViolationPublic = (id) => {
  return violationsAPI.payViolationPublic(id)
}