<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-white bg-opacity-90">
    <div class="text-center">
      <!-- Loading spinner -->
      <div class="spinner-large mb-4"></div>
      
      <!-- Loading text -->
      <h2 class="text-xl font-semibold text-gray-800 mb-2">
        CopFriendly ESeva
      </h2>
      <p class="text-gray-600">
        {{ message || 'Loading...' }}
      </p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoadingOverlay',
  props: {
    message: {
      type: String,
      default: 'Loading...'
    }
  }
}
</script>

<style scoped>
.spinner-large {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3b82f6;
  border-radius: 50%;
  width: 48px;
  height: 48px;
  animation: spin 1s linear infinite;
  margin: 0 auto;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>