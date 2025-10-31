import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authAPI, usersAPI } from '@/services/api'

export const useAuthStore = defineStore('auth', () => {
  // No local mock users — frontend relies entirely on backend for authentication and user management.
  const mockUsers = null

  // State
  const user = ref(null)
  const token = ref(localStorage.getItem('token'))
  const isLoading = ref(false)
  const error = ref(null)

  // Getters
  const isAuthenticated = computed(() => !!token.value && !!user.value)
  const userRole = computed(() => user.value?.role)
  const userName = computed(() => user.value?.name)

  // Actions
  const login = async (credentials) => {
    try {
      isLoading.value = true
      error.value = null
      
      // Allow a PUBLIC role that doesn't require backend authentication.
      // This enables unauthenticated users to view/pay public violations in the demo.
      if (credentials.role === 'public') {
        // create a simple mock token and user object
        const fakeToken = 'public-token-' + Math.random().toString(36).substring(2, 12)
        token.value = fakeToken
        localStorage.setItem('token', fakeToken)
        const publicUser = {
          id: credentials.username || ('public_' + Math.random().toString(36).substring(2,6)),
          username: credentials.username || 'public',
          name: credentials.username || 'Public User',
          email: '',
          role: 'PUBLIC',
          status: 'active'
        }
        user.value = publicUser
        localStorage.setItem('user', JSON.stringify(publicUser))
        isLoading.value = false
        return { success: true }
      }

      // Try backend login first for non-public roles
      try {
        isLoading.value = true
        error.value = null
        const resp = await authAPI.login({ username: credentials.username, password: credentials.password })
        const tok = resp.data.token
        if (tok) {
          token.value = tok
          // store token first so subsequent requests include Authorization header
          localStorage.setItem('token', tok)
          // Save user info from login response
          const userInfo = {
            id: resp.data.id || resp.data.username,  // Use MongoDB ID if available, fallback to username
            username: resp.data.username,
            name: resp.data.fullName,
            email: resp.data.email,
            role: resp.data.role,
            status: 'active'
          }
          user.value = userInfo
          localStorage.setItem('user', JSON.stringify(userInfo))
          return { success: true }
        }
      } catch (backendErr) {
        // Check if backend returned an error message (like "pending approval")
        // Check if backend returned an error message (like "pending approval")
        const backendMsgRaw = backendErr?.response?.data?.error || backendErr?.message || ''
        // If backend says 'Bad credentials' we *could* try a best-effort username vs password
        // distinction by attempting to fetch the users list. However that endpoint is commonly
        // protected and will produce a 403 (seen in console). To avoid noisy network errors in
        // production, only perform the lookup when the feature flag VITE_ALLOW_USERNAME_LOOKUP
        // is explicitly set to 'true' in the environment (useful for local/demo debugging).
        if (typeof backendMsgRaw === 'string' && backendMsgRaw.toLowerCase().includes('bad credential')) {
          try {
            // Always attempt the safe existence check endpoint. It returns only { exists: boolean }.
            const existsResp = await authAPI.exists(credentials.username)
            const exists = existsResp.data?.exists === true
            if (!exists) {
              error.value = 'Username is wrong'
              return { success: false, message: error.value }
            } else {
              error.value = 'Password is wrong'
              return { success: false, message: error.value }
            }
          } catch (existsErr) {
            // If the existence check fails (network, server), fall back to the backend message.
            // Log as debug to avoid noisy console errors in production.
            // eslint-disable-next-line no-console
            console.debug('Username existence lookup failed (ignored):', existsErr?.response?.status || existsErr?.message)
            error.value = backendMsgRaw || 'Bad credentials'
            return { success: false, message: error.value }
          }
        }

        // Check if backend returned an error message (like "pending approval")
        if (backendErr.response && backendErr.response.data && backendErr.response.data.error) {
          error.value = backendErr.response.data.error
          return { success: false, message: error.value }
        }

        // No mock fallback — surface backend error to the UI
        const backendMsg = backendMsgRaw || 'Login failed'
        error.value = backendMsg
        return { success: false, message: backendMsg }
      } finally {
        isLoading.value = false
      }
      
    } catch (err) {
      console.error('Login error:', err)
      error.value = err.message || 'Login failed'
      return { success: false, message: error.value }
    } finally {
      isLoading.value = false
    }
  }

  const signup = async (userData) => {
    try {
      isLoading.value = true
      error.value = null

      // Try registering with the backend first
      try {
        // Generate username from firstName (client-side convenience). Backend also derives username server-side,
        // but we set it here to satisfy client validation and provide a clear UX.
        const sanitize = (s) => (s || '').toString().trim().replace(/[^A-Za-z0-9]/g, '').toLowerCase()
        let base = sanitize(userData.firstName)
        if (!base || base.length < 2) {
          base = sanitize(userData.name) || ('user' + Math.random().toString(36).substring(2,6))
        }

        let resp = null
        let lastError = null
        // Try candidate usernames with numeric suffixes if backend says "Username is already taken!"
        for (let attempt = 0; attempt < 10; attempt++) {
          const candidate = attempt === 0 ? base : (base + attempt)
          const payload = {
            username: candidate,
            password: userData.password,
            fullName: userData.name,
            email: userData.email,
            firstName: userData.firstName,
            lastName: userData.lastName,
            phone: userData.phone,
            dateOfBirth: userData.dateOfBirth,
            role: userData.role,
            confirmPassword: userData.confirmPassword
          }
          console.log('Sending payload to backend (attempt', attempt, '):', payload)
          try {
            resp = await authAPI.register(payload)
            console.log('Backend response:', resp.data)
            break // success
          } catch (err) {
            lastError = err
            const backendMsg = err?.response?.data?.error || err?.response?.data || err.message
            console.warn('Register attempt failed:', backendMsg)
            if (typeof backendMsg === 'string' && backendMsg.toLowerCase().includes('username')) {
              // username collision - try next suffix
              continue
            }
            // Other backend error (validation, email in use, etc.) — rethrow to outer catch
            throw err
          }
        }

        if (!resp) {
          // exhausted attempts
          const backendMsg = lastError?.response?.data?.error || lastError?.message || 'Registration failed'
          error.value = backendMsg
          return { success: false, message: backendMsg }
        }

        // Save token and user info from backend response
        if (resp.data.token) {
          localStorage.setItem('token', resp.data.token)
        }
        const savedUser = { 
          id: resp.data.username || base, 
          username: resp.data.username || base, 
          name: resp.data.fullName || userData.name, 
          email: resp.data.email || userData.email, 
          role: resp.data.role || 'USER', 
          status: 'active', 
          createdAt: new Date().toISOString() 
        }
        localStorage.setItem('user', JSON.stringify(savedUser))
        return { success: true, message: 'Account created successfully! You are now logged in.' }
      } catch (backendErr) {
        // fallback to mock DB when backend is not available
        console.error('Backend signup failed:', backendErr)
        console.error('Error details:', backendErr.response?.data)
        console.warn('Falling back to mock DB')
        // No mock fallback — surface backend error to the UI
        const backendMsg = backendErr?.response?.data?.error || backendErr?.message || 'Registration failed'
        error.value = backendMsg
        return { success: false, message: backendMsg }
      }
    } catch (err) {
      console.error('Signup error:', err)
      error.value = err.message || 'Signup failed'
      return { success: false, message: error.value }
    } finally {
      isLoading.value = false
    }
  }

  const getAllUsers = async () => {
    try {
      const resp = await authAPI.getAllUsers()
      return resp.data.users || []
    } catch (err) {
      // No mock fallback — surface the error
      console.error('getAllUsers error:', err)
      throw err
    }
  }

  const listPendingUsers = async () => {
    try {
      const resp = await authAPI.listPending()
      return resp.data || []
    } catch (err) {
      console.error('listPendingUsers error:', err)
      throw err
    }
  }

  const approveUser = async (userId) => {
    try {
      const resp = await authAPI.approve(userId)
      return resp.data
    } catch (err) {
      console.error('approveUser error:', err)
      throw err
    }
  }

  const rejectUser = async (userId) => {
    try {
      const resp = await authAPI.reject(userId)
      return resp.data
    } catch (err) {
      console.error('rejectUser error:', err)
      throw err
    }
  }

  const updateUserStatus = async (userId, newStatus) => {
    try {
      const resp = await authAPI.updateUserStatus(userId, newStatus)
      return resp.data
    } catch (err) {
      console.error('updateUserStatus error:', err)
      throw err
    }
  }

  const updateUserRole = async (userId, newRole) => {
    try {
      const resp = await authAPI.updateUserRole(userId, newRole)
      return resp.data
    } catch (err) {
      console.error('updateUserRole error:', err)
      throw err
    }
  }

  const deleteUser = async (userId) => {
    try {
      await usersAPI.delete(userId)
      return true
    } catch (err) {
      console.error('deleteUser error:', err)
      throw err
    }
  }

  const logout = async () => {
    try {
      // For demo mode - no API call needed
      // Just clear local state
      user.value = null
      token.value = null
      error.value = null
      
      // Clear localStorage
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    } catch (err) {
      console.error('Logout error:', err)
      // Clear anyway
      user.value = null
      token.value = null
      error.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }

  const initializeAuth = () => {
    const storedToken = localStorage.getItem('token')
    const storedUser = localStorage.getItem('user')
    
    if (storedToken && storedUser) {
      try {
        token.value = storedToken
        user.value = JSON.parse(storedUser)
      } catch (err) {
        console.error('Error parsing stored user data:', err)
        logout()
      }
    }
  }

  const clearError = () => {
    error.value = null
  }

  // Initialize auth state on store creation
  initializeAuth()

  return {
  // State
  user,
  token,
  isLoading,
  error,
    
    // Getters
    isAuthenticated,
    userRole,
    userName,
    
    // Actions
    login,
    signup,
    logout,
    initializeAuth,
    clearError,
    getAllUsers,
    updateUserStatus,
    updateUserRole,
    deleteUser
  }
})