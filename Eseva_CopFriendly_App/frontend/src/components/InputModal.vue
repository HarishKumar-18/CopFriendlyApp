<template>
  <div v-if="queue.length" class="input-modal-backdrop">
    <div class="input-modal">
      <h3>{{ current.title || 'Input' }}</h3>
      <p v-if="current.message">{{ current.message }}</p>
  <input ref="inputEl" v-model="value" :placeholder="current.placeholder || ''" class="input-field" />
      <div class="actions">
        <button @click="cancel">Cancel</button>
        <button @click="ok">OK</button>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, ref, watch, nextTick } from 'vue'
import { getInputQueue, resolveInput } from '@/composables/interactionService'

export default {
  name: 'InputModal',
  setup() {
    // Keep the reactive ref so template updates when queue changes
    const queueRef = getInputQueue()
    const value = ref('')
    const inputEl = ref(null)

    const current = computed(() => queueRef.value.length ? queueRef.value[0] : null)

    // When current changes (modal opens), reset the input and focus
    watch(current, async (newVal) => {
      value.value = ''
      await nextTick()
      if (newVal && inputEl.value && typeof inputEl.value.focus === 'function') {
        inputEl.value.focus()
      }
    })

    function ok() {
      if (!current.value) return
      resolveInput(current.value.id, value.value)
      value.value = ''
    }
    function cancel() {
      if (!current.value) return
      resolveInput(current.value.id, null)
      value.value = ''
    }

    return { queue: queueRef, current, value, ok, cancel, inputEl }
  }
}
</script>

<style scoped>
.input-modal-backdrop { position: fixed; inset: 0; display:flex; align-items:center; justify-content:center; background: rgba(0,0,0,0.4); }
.input-modal { background: white; padding: 1rem; border-radius: 6px; width: 320px; box-shadow: 0 6px 18px rgba(0,0,0,0.2); }
.input-field { width: 100%; padding: .5rem; margin: .5rem 0; }
.actions { display:flex; justify-content:flex-end; gap:.5rem }
</style>
