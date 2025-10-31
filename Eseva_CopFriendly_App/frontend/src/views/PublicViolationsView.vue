<template>
  <div class="card" style="padding: 1rem;">
    <h2>Public Violations (Unpaid)</h2>
    <div v-if="loading">Loading...</div>
    <div v-else>
      <div v-if="violations.length === 0">No unpaid violations</div>
      <table v-else style="width:100%; border-collapse:collapse;">
        <thead>
          <tr style="background:#f8fafc;">
            <th style="padding:0.5rem; text-align:left">Ticket</th>
            <th style="padding:0.5rem; text-align:left">Vehicle</th>
            <th style="padding:0.5rem; text-align:left">Location</th>
            <th style="padding:0.5rem; text-align:left">Fine</th>
            <th style="padding:0.5rem; text-align:left">Status</th>
            <th style="padding:0.5rem; text-align:left">Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="v in violations" :key="v.id" style="border-bottom:1px solid #eee;">
            <td style="padding:0.5rem">{{ v.ticketNumber }}</td>
            <td style="padding:0.5rem">{{ v.vehicleNumber }} ({{ v.vehicleType }})</td>
            <td style="padding:0.5rem">{{ v.location }}</td>
            <td style="padding:0.5rem">{{ v.fineAmount }}</td>
            <td style="padding:0.5rem">{{ v.paymentStatus }}</td>
            <td style="padding:0.5rem">
              <button :disabled="v.paymentStatus==='PAID' || paying[v.id]" @click="pay(v)" class="btn-small" style="background:#10b981;color:white">Pay</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { violationsAPI } from '@/services/api'
import { showToast } from '@/composables/interactionService'

export default {
  name: 'PublicViolationsView',
  setup() {
    const violations = ref([])
    const loading = ref(false)
    const paying = ref({})

    const load = async () => {
      loading.value = true
      try {
        const resp = await violationsAPI.getUnpaidPublic()
        violations.value = resp.data?.violations || []
      } catch (err) {
        console.error(err)
        showToast({ title: 'Public Violations', message: 'Failed to load unpaid violations', type: 'error' })
      } finally {
        loading.value = false
      }
    }

    const pay = async (v) => {
      if (v.paymentStatus === 'PAID') return
      paying.value = { ...paying.value, [v.id]: true }
      try {
        // optimistic update
        const original = { ...v }
        v.paymentStatus = 'PAID'
        await violationsAPI.payPublicViolation(v.id)
        showToast({ title: 'Payment', message: `Payment recorded for ${v.ticketNumber}`, type: 'success' })
        // Refresh list to remove paid items
        await load()
      } catch (err) {
        console.error(err)
        v.paymentStatus = 'UNPAID'
        showToast({ title: 'Payment', message: 'Payment failed', type: 'error' })
      } finally {
        paying.value = { ...paying.value, [v.id]: false }
      }
    }

    onMounted(load)

    return { violations, loading, pay, paying }
  }
}
</script>

<style scoped>
.btn-small { padding: 0.35rem 0.6rem; border-radius: 4px; border:none; cursor:pointer }
</style>
