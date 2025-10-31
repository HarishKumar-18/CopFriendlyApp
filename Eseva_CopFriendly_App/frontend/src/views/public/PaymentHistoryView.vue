<template>
  <div class="p-8">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-gray-700">📋 Payment History</h1>
      <div class="text-sm text-gray-600">
        Total Paid: <span class="font-bold text-green-600">₹{{ totalPaid }}</span>
      </div>
    </div>

    <div v-if="loading" class="text-gray-500">Loading payment history...</div>
    <div v-else-if="paidViolations.length === 0" class="text-gray-600">
      No payment history found.
    </div>

    <table v-else class="min-w-full bg-white border border-gray-200 rounded-lg shadow">
      <thead class="bg-green-600 text-white">
        <tr>
          <th class="py-3 px-4 text-left">Ticket ID</th>
          <th class="py-3 px-4 text-left">Vehicle Number</th>
          <th class="py-3 px-4 text-left">Amount (₹)</th>
          <th class="py-3 px-4 text-left">Paid Date</th>
          <th class="py-3 px-4 text-center">Status</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="violation in paidViolations" :key="violation.id" class="border-b hover:bg-gray-50">
          <td class="py-3 px-4">{{ violation.ticketNumber || violation.id }}</td>
          <td class="py-3 px-4">{{ violation.vehicleNumber }}</td>
          <td class="py-3 px-4">{{ violation.fineAmount || violation.amount }}</td>
          <td class="py-3 px-4">{{ formatDate(violation.paidDate) }}</td>
          <td class="py-3 px-4 text-center">
            <span class="bg-green-100 text-green-800 py-1 px-3 rounded-full text-xs font-medium">
              ✅ Paid
            </span>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getPaidForPublic } from '@/services/api'

const paidViolations = ref([])
const loading = ref(true)

const totalPaid = computed(() => {
  return paidViolations.value.reduce((sum, v) => sum + (v.fineAmount || v.amount || 0), 0)
})

const loadPaymentHistory = async () => {
  loading.value = true
  try {
    const res = await getPaidForPublic()
    paidViolations.value = res.data?.violations || []
  } catch (err) {
    console.error('Error fetching payment history:', err)
  } finally {
    loading.value = false
  }
}

const formatDate = (d) => {
  if (!d) return ''
  return new Date(d).toLocaleString()
}

onMounted(loadPaymentHistory)
</script>
