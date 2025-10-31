<template>
  <div class="p-6">
    <div class="bg-white shadow rounded-lg p-6">
      <h1 class="text-2xl font-bold text-gray-900 mb-4">Payment</h1>
      <p class="text-gray-600">Payment interface for ticket ID: {{ $route.params.ticketId }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PaymentView'
}
</script>