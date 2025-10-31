<template>
  <div v-if="queue.length" class="confirm-backdrop">
    <div class="confirm-center">
      <div v-for="item in queue" :key="item.id" class="confirm-card">
        <h3 class="confirm-title">{{ item.title }}</h3>
        <div class="confirm-message">{{ item.message }}</div>
        <div class="confirm-actions">
          <button class="btn btn-cancel" @click="respond(item.id, false)">Cancel</button>
          <button class="btn btn-accept" @click="respond(item.id, true)">OK</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { getConfirmQueue, resolveConfirm } from '@/composables/interactionService'

const queueRef = getConfirmQueue()
const queue = computed(() => queueRef.value)

const respond = (id, accepted) => {
  resolveConfirm(id, accepted)
}
</script>

<style scoped>
.confirm-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,0.45); display:flex; align-items:center; justify-content:center; z-index:10000 }
.confirm-center { width: 420px; }
.confirm-card { background: white; padding: 18px; border-radius: 8px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); }
.confirm-title { margin: 0 0 8px 0; font-size: 1.1rem }
.confirm-message { color: #374151; margin-bottom: 12px }
.confirm-actions { display:flex; justify-content:flex-end; gap: 8px }
.btn { padding: 8px 12px; border-radius: 6px; cursor:pointer; border: none; font-weight:600 }
.btn-cancel { background: #f3f4f6; color: #374151 }
.btn-accept { background: #10b981; color: white }
</style>
