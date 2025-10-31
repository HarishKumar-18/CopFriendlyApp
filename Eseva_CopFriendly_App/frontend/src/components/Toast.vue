<template>
  <div class="toast-wrap" aria-live="polite">
    <transition-group name="toast" tag="div">
      <div v-for="t in toastList" :key="t.id" :class="`toast toast-${t.type}`">
        <div class="toast-body">
          <strong v-if="t.title" class="toast-title">{{ t.title }}</strong>
          <div class="toast-message">{{ t.message }}</div>
        </div>
        <button class="toast-close" @click="remove(t.id)">✕</button>
      </div>
    </transition-group>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { toasts, removeToast } from '@/composables/interactionService'

const remove = (id) => removeToast(id)
const toastList = computed(() => toasts.value)
</script>

<style scoped>
.toast-wrap {
  position: fixed;
  right: 16px;
  top: 16px;
  width: 320px;
  z-index: 9999;
}
.toast-enter-active, .toast-leave-active { transition: all .25s ease; }
.toast-enter-from { opacity: 0; transform: translateY(-8px); }
.toast-leave-to { opacity: 0; transform: translateY(-8px); }
.toast { display:flex; align-items:center; justify-content:space-between; padding: 10px 12px; margin-bottom: 10px; border-radius:8px; color: #fff; box-shadow: 0 6px 18px rgba(0,0,0,0.08); }
.toast-body { flex:1; padding-right:8px; }
.toast-title { display:block; font-weight:700; margin-bottom:4px; }
.toast-message { font-size:0.95rem; }
.toast-success { background: linear-gradient(90deg,#10b981,#059669); }
.toast-info { background: linear-gradient(90deg,#3b82f6,#2563eb); }
.toast-warning { background: linear-gradient(90deg,#f59e0b,#d97706); }
.toast-error { background: linear-gradient(90deg,#ef4444,#dc2626); }
.toast-close { border: none; background: transparent; color: rgba(255,255,255,0.9); font-weight:700; cursor:pointer; }
</style>
