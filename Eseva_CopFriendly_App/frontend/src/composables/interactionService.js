import { ref } from 'vue'

const toasts = ref([])
let idCounter = 1

export function showToast({ title = '', message = '', type = 'info', timeout = 4000 } = {}) {
  const id = idCounter++
  toasts.value.push({ id, title, message, type })
  if (timeout > 0) {
    setTimeout(() => removeToast(id), timeout)
  }
  return id
}

export function removeToast(id) {
  toasts.value = toasts.value.filter(t => t.id !== id)
}

const confirmCallbacks = ref([])
export function showConfirm({ title = 'Confirm', message = '' } = {}) {
  return new Promise((resolve) => {
    confirmCallbacks.value.push({ id: idCounter++, title, message, resolve })
  })
}
export function getConfirmQueue() { return confirmCallbacks }
export function resolveConfirm(id, accepted) {
  const idx = confirmCallbacks.value.findIndex(c => c.id === id)
  if (idx !== -1) {
    const cb = confirmCallbacks.value[idx]
    cb.resolve(accepted)
    confirmCallbacks.value.splice(idx, 1)
  }
}

export { toasts }

// Input modal queue
const inputQueue = ref([])
export function showInput({ title = 'Input', message = '', placeholder = '' } = {}) {
  return new Promise((resolve) => {
    inputQueue.value.push({ id: idCounter++, title, message, placeholder, resolve })
  })
}
export function getInputQueue() { return inputQueue }
export function resolveInput(id, value) {
  const idx = inputQueue.value.findIndex(i => i.id === id)
  if (idx !== -1) {
    const it = inputQueue.value[idx]
    it.resolve(value)
    inputQueue.value.splice(idx, 1)
  }
}
