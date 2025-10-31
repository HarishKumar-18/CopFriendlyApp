<template>
  <div class="p-8">
    <h1 class="text-2xl font-bold mb-6 text-blue-600">💳 Pending Payments</h1>

    <div v-if="loading" class="text-gray-500">Loading pending violations...</div>
    <div v-else-if="violations.length === 0" class="text-gray-600">
      🎉 No pending payments found.
    </div>

    <table v-else class="min-w-full bg-white border border-gray-200 rounded-lg shadow">
      <thead class="bg-blue-600 text-white">
        <tr>
          <th class="py-3 px-4 text-left">Ticket ID</th>
          <th class="py-3 px-4 text-left">Vehicle Number</th>
          <th class="py-3 px-4 text-left">Amount (₹)</th>
          <th class="py-3 px-4 text-left">Date</th>
          <th class="py-3 px-4 text-center">Action</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="violation in violations" :key="violation.id" class="border-b hover:bg-gray-50">
          <td class="py-3 px-4">{{ violation.ticketNumber || violation.id }}</td>
          <td class="py-3 px-4">{{ violation.vehicleNumber }}</td>
          <td class="py-3 px-4">{{ violation.fineAmount || violation.amount }}</td>
          <td class="py-3 px-4">{{ formatDate(violation.createdAt || violation.date) }}</td>
          <td class="py-3 px-4 text-center">
            <button
              @click="confirmPay(violation)"
              class="bg-green-600 hover:bg-green-700 text-white py-1 px-4 rounded-lg font-medium transition-all"
            >
              Pay
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUnpaidForPublic, payViolationPublic } from '@/services/api'
import { showConfirm, showToast } from '@/composables/interactionService'

const violations = ref([])
const loading = ref(true)

const load = async () => {
  loading.value = true
  try {
    const res = await getUnpaidForPublic()
    // backend returns { violations: [...] }
    const raw = res.data?.violations || []
    // Safety: show only approved, unpaid items even if backend misconfig
    violations.value = raw.filter(v => v.paymentStatus === 'UNPAID' && v.status === 'APPROVED')
  } catch (err) {
    console.error('Error fetching unpaid violations:', err)
    showToast({ title: 'Pending Payments', message: 'Failed to fetch unpaid violations', type: 'error' })
  } finally {
    loading.value = false
  }
}

const confirmPay = async (v) => {
  const ok = await showConfirm({ title: 'Confirm Payment', message: `Pay ₹${v.fineAmount || v.amount} for ${v.ticketNumber || v.id}?` })
  if (!ok) return

  try {
    await payViolationPublic(v.id)
    showToast({ title: 'Payment', message: 'Payment successful', type: 'success' })
    // Notify other tabs (e.g., Inspector dashboard) to refresh
    try { localStorage.setItem('violationPaid', Date.now().toString()) } catch (e) {}
    // reload list
    await load()
  } catch (err) {
    console.error('Error updating payment:', err)
    showToast({ title: 'Payment', message: 'Payment failed', type: 'error' })
  }
}

const formatDate = (d) => {
  if (!d) return ''
  return new Date(d).toLocaleString()
}

onMounted(() => {
  load()
  // Refresh when inspector approves a violation (cross-tab event)
  try {
    window.addEventListener('storage', (e) => {
      if (e.key === 'violationApproved') {
        load()
      }
    })
  } catch (e) {}
})
</script>
