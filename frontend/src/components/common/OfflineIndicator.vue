<template>
  <div class="fixed bottom-4 left-4 z-50">
    <div class="bg-red-600 text-white px-4 py-2 rounded-lg shadow-lg flex items-center space-x-2">
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 5.636l-12.728 12.728m0 0L5.636 18.364M5.636 5.636l12.728 12.728" />
      </svg>
      <span class="text-sm font-medium">You're offline</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'OfflineIndicator'
}
</script>