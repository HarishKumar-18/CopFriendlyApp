// Date and time utilities
export const dateUtils = {
  formatDate: (date) => {
    if (!date) return ''
    const d = new Date(date)
    return d.toLocaleDateString('en-IN', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    })
  },

  formatDateTime: (date) => {
    if (!date) return ''
    const d = new Date(date)
    return d.toLocaleDateString('en-IN', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  },

  formatTime: (date) => {
    if (!date) return ''
    const d = new Date(date)
    return d.toLocaleTimeString('en-IN', {
      hour: '2-digit',
      minute: '2-digit'
    })
  },

  isToday: (date) => {
    if (!date) return false
    const today = new Date()
    const d = new Date(date)
    return today.toDateString() === d.toDateString()
  },

  getRelativeTime: (date) => {
    if (!date) return ''
    const now = new Date()
    const d = new Date(date)
    const diffInSeconds = Math.floor((now - d) / 1000)

    if (diffInSeconds < 60) return 'Just now'
    if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)} minutes ago`
    if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)} hours ago`
    if (diffInSeconds < 604800) return `${Math.floor(diffInSeconds / 86400)} days ago`
    
    return dateUtils.formatDate(date)
  }
}

// Validation utilities
export const validators = {
  email: (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return emailRegex.test(email)
  },

  phone: (phone) => {
    const phoneRegex = /^[6-9]\d{9}$/
    return phoneRegex.test(phone.replace(/\D/g, ''))
  },

  drivingLicense: (license) => {
    // Indian driving license format
    const licenseRegex = /^[A-Z]{2}[0-9]{2}[A-Z0-9]{11}$/
    return licenseRegex.test(license.replace(/\s/g, ''))
  },

  vehicleNumber: (number) => {
    // Indian vehicle number format
    const vehicleRegex = /^[A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4}$/
    return vehicleRegex.test(number.replace(/\s/g, ''))
  },

  required: (value) => {
    if (typeof value === 'string') return value.trim().length > 0
    if (Array.isArray(value)) return value.length > 0
    return value !== null && value !== undefined
  },

  minLength: (value, min) => {
    if (typeof value === 'string') return value.length >= min
    return false
  },

  maxLength: (value, max) => {
    if (typeof value === 'string') return value.length <= max
    return false
  }
}

// Form validation helper
export const validateForm = (formData, rules) => {
  const errors = {}
  
  Object.keys(rules).forEach(field => {
    const value = formData[field]
    const fieldRules = rules[field]
    
    fieldRules.forEach(rule => {
      if (typeof rule === 'function') {
        const result = rule(value)
        if (result !== true) {
          if (!errors[field]) errors[field] = []
          errors[field].push(result)
        }
      } else if (typeof rule === 'object') {
        const { validator, message } = rule
        if (!validator(value)) {
          if (!errors[field]) errors[field] = []
          errors[field].push(message)
        }
      }
    })
  })
  
  return {
    isValid: Object.keys(errors).length === 0,
    errors
  }
}

// Storage utilities
export const storage = {
  set: (key, value) => {
    try {
      localStorage.setItem(key, JSON.stringify(value))
    } catch (error) {
      console.error('Storage set error:', error)
    }
  },

  get: (key, defaultValue = null) => {
    try {
      const item = localStorage.getItem(key)
      return item ? JSON.parse(item) : defaultValue
    } catch (error) {
      console.error('Storage get error:', error)
      return defaultValue
    }
  },

  remove: (key) => {
    try {
      localStorage.removeItem(key)
    } catch (error) {
      console.error('Storage remove error:', error)
    }
  },

  clear: () => {
    try {
      localStorage.clear()
    } catch (error) {
      console.error('Storage clear error:', error)
    }
  }
}

// Format utilities
export const formatters = {
  currency: (amount, currency = 'INR') => {
    if (!amount && amount !== 0) return ''
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: currency
    }).format(amount)
  },

  number: (number) => {
    if (!number && number !== 0) return ''
    return new Intl.NumberFormat('en-IN').format(number)
  },

  capitalize: (str) => {
    if (!str) return ''
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase()
  },

  titleCase: (str) => {
    if (!str) return ''
    return str.replace(/\w\S*/g, (txt) => 
      txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase()
    )
  },

  truncate: (str, length = 50) => {
    if (!str) return ''
    if (str.length <= length) return str
    return str.substring(0, length) + '...'
  }
}

// Device detection
export const device = {
  isMobile: () => {
    return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
  },

  isTablet: () => {
    return /iPad|Android(?!.*Mobile)/i.test(navigator.userAgent)
  },

  isDesktop: () => {
    return !device.isMobile() && !device.isTablet()
  },

  getScreenSize: () => {
    return {
      width: window.innerWidth,
      height: window.innerHeight
    }
  }
}

// Color utilities for status, priority, etc.
export const colorUtils = {
  getStatusColor: (status) => {
    const colors = {
      pending: 'text-warning-600 bg-warning-100',
      active: 'text-primary-600 bg-primary-100',
      resolved: 'text-success-600 bg-success-100',
      cancelled: 'text-gray-600 bg-gray-100',
      ticketed: 'text-secondary-600 bg-secondary-100',
      paid: 'text-success-600 bg-success-100',
      overdue: 'text-danger-600 bg-danger-100'
    }
    return colors[status] || 'text-gray-600 bg-gray-100'
  },

  getPriorityColor: (priority) => {
    const colors = {
      low: 'text-success-600 bg-success-100',
      medium: 'text-warning-600 bg-warning-100',
      high: 'text-danger-600 bg-danger-100',
      critical: 'text-red-800 bg-red-200'
    }
    return colors[priority] || 'text-gray-600 bg-gray-100'
  },

  getViolationTypeColor: (type) => {
    const colors = {
      speeding: 'text-red-600 bg-red-100',
      parking: 'text-blue-600 bg-blue-100',
      signal: 'text-orange-600 bg-orange-100',
      license: 'text-purple-600 bg-purple-100',
      helmet: 'text-yellow-600 bg-yellow-100',
      seatbelt: 'text-green-600 bg-green-100',
      drunk_driving: 'text-red-800 bg-red-200',
      rash_driving: 'text-red-700 bg-red-150'
    }
    return colors[type] || 'text-gray-600 bg-gray-100'
  }
}

// Constants
export const constants = {
  VIOLATION_TYPES: [
    { value: 'speeding', label: 'Over Speeding' },
    { value: 'parking', label: 'Wrong Parking' },
    { value: 'signal', label: 'Signal Violation' },
    { value: 'license', label: 'No License' },
    { value: 'helmet', label: 'No Helmet' },
    { value: 'seatbelt', label: 'No Seatbelt' },
    { value: 'drunk_driving', label: 'Drunk Driving' },
    { value: 'rash_driving', label: 'Rash Driving' },
    { value: 'mobile_usage', label: 'Mobile Usage While Driving' },
    { value: 'other', label: 'Other' }
  ],

  VEHICLE_TYPES: [
    { value: 'car', label: 'Car' },
    { value: 'bike', label: 'Bike/Motorcycle' },
    { value: 'lorry', label: 'Lorry/Truck' },
    { value: 'bus', label: 'Bus' },
    { value: 'auto', label: 'Auto Rickshaw' },
    { value: 'taxi', label: 'Taxi' },
    { value: 'other', label: 'Other' }
  ],

  EMERGENCY_TYPES: [
    { value: 'accident', label: 'Traffic Accident' },
    { value: 'medical', label: 'Medical Emergency' },
    { value: 'fire', label: 'Fire Emergency' },
    { value: 'crime', label: 'Crime in Progress' },
    { value: 'natural_disaster', label: 'Natural Disaster' },
    { value: 'sos', label: 'SOS Alert' },
    { value: 'other', label: 'Other Emergency' }
  ],

  PRIORITY_LEVELS: [
    { value: 'low', label: 'Low' },
    { value: 'medium', label: 'Medium' },
    { value: 'high', label: 'High' },
    { value: 'critical', label: 'Critical' }
  ],

  USER_ROLES: [
    { value: 'traffic_sergeant', label: 'Traffic Sergeant' },
    { value: 'traffic_inspector', label: 'Traffic Inspector' },
    { value: 'traffic_central_team', label: 'Traffic Central Team' },
    { value: 'medical_emergency_central', label: 'Medical Emergency Central' }
  ],

  PAYMENT_METHODS: [
    { value: 'upi', label: 'UPI' },
    { value: 'card', label: 'Credit/Debit Card' },
    { value: 'netbanking', label: 'Net Banking' },
    { value: 'wallet', label: 'Digital Wallet' },
    { value: 'cash', label: 'Cash' }
  ]
}