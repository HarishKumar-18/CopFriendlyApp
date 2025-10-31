<template>
  <div>
    <!-- Navigation -->
    <nav style="background: #059669; color: white; padding: 1rem; display: flex; justify-content: space-between; align-items: center;">
      <div style="display: flex; align-items: center; gap: 1rem;">
        <button @click="goBack" style="background: rgba(255,255,255,0.2); border: none; color: white; padding: 0.5rem; border-radius: 4px; cursor: pointer;">
          ← Back
        </button>
        <div style="font-size: 1.5rem; font-weight: bold;">
          ✅ Violation Approval Center
        </div>
      </div>
      <div style="font-size: 0.9rem;">
        Inspector: {{ authStore.user?.name || 'Inspector' }}
      </div>
    </nav>

    <div class="dashboard">
      <!-- Filter Controls -->
      <div class="card" style="margin-bottom: 2rem;">
        <div class="card-body">
          <div style="display: flex; gap: 1rem; align-items: center; flex-wrap: wrap;">
            <select v-model="filters.status" class="form-control" style="width: auto;">
              <option value="all">All Status</option>
              <option value="pending">Pending Approval</option>
              <option value="approved">Approved</option>
              <option value="rejected">Rejected</option>
            </select>
            <input 
              v-model="filters.search" 
              placeholder="Search by vehicle number..." 
              class="form-control" 
              style="width: 250px;"
            >
            <button @click="fetchViolations" class="btn btn-primary">🔄 Refresh</button>
          </div>
        </div>
      </div>

      <!-- Violations List -->
      <div class="card">
        <div class="card-header">
          <h3>📋 Violations Requiring Approval ({{ filteredViolations.length }})</h3>
        </div>
        <div class="card-body">
          <div v-if="loading" style="text-align: center; color: #6b7280; padding: 3rem;">
            <div style="font-size: 2rem;">Loading...</div>
          </div>
          <div v-else-if="filteredViolations.length === 0" style="text-align: center; color: #6b7280; padding: 3rem;">
            <div style="font-size: 3rem; margin-bottom: 1rem;">📭</div>
            <div style="font-size: 1.2rem; margin-bottom: 0.5rem;">No violations found</div>
            <div>Try adjusting your filters or check back later</div>
          </div>
          <div v-else>
            <div 
              v-for="violation in filteredViolations" 
              :key="violation.id"
              style="border: 1px solid #059669; border-radius: 8px; padding: 1.5rem; margin-bottom: 1rem; background: #f0fdf4;"
            >
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <div>
                  <div style="font-size: 1.2rem; font-weight: bold;">{{ violation.vehicleNumber }}</div>
                  <div>Status: <span :style="{color: violation.status === 'pending' ? '#d97706' : violation.status === 'approved' ? '#059669' : '#dc2626'}">{{ violation.status }}</span></div>
                  <div>Fine: ₹{{ violation.fineAmount }}</div>
                  <div>Description: {{ violation.description }}</div>
                </div>
                <div>
                  <button v-if="violation.status === 'pending'" @click="approveViolation(violation)" class="btn btn-success btn-small">✅ Approve</button>
                  <button v-if="violation.status === 'pending'" @click="rejectViolation(violation)" class="btn btn-danger btn-small">❌ Reject</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { violationsAPI } from '@/services/api';
import { showToast, showConfirm, showInput } from '@/composables/interactionService'
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

const filters = ref({
  status: 'pending',
  search: ''
});
const violations = ref([]);
const loading = ref(false);

const fetchViolations = async () => {
  loading.value = true;
  try {
    const response = await violationsAPI.getPending();
    const data = Array.isArray(response.data.violations) ? response.data.violations : response.data;
    violations.value = data.map(v => ({
      id: v._id || v.id || v.violationId, // Use MongoDB _id as primary
      vehicleNumber: v.vehicleNumber,
      status: v.status?.toLowerCase() || 'pending',
      fineAmount: v.fineAmount,
      description: v.description
    }));
  } catch (error) {
    showToast({ message: 'Failed to fetch pending violations: ' + (error?.response?.data?.error || error.message), type: 'error' })
    violations.value = [];
  } finally {
    loading.value = false;
  }
};

const approveViolation = async (violation) => {
  const ok = await showConfirm({ title: 'Approve Violation', message: 'Approve violation for ' + violation.vehicleNumber + '?' })
  if (!ok) return;
  try {
    // Use _id for backend compatibility
    await violationsAPI.approve(violation._id || violation.id || violation.violationId);
    violation.status = 'approved';
  showToast({ message: 'Violation approved!', type: 'success' })
  } catch (error) {
    alert('Failed to approve violation: ' + (error?.response?.data?.error || error.message));
  }
};

const rejectViolation = async (violation) => {
  const reason = await showInput({ title: 'Rejection reason', message: 'Enter reason for rejection:', placeholder: '' })
  if (!reason) return;
  try {
    await violationsAPI.reject(violation.id, reason);
    violation.status = 'rejected';
  showToast({ message: 'Violation rejected!', type: 'info' })
  } catch (error) {
    showToast({ message: 'Failed to reject violation: ' + (error?.response?.data?.error || error.message), type: 'error' })
  }
};

const filteredViolations = computed(() => {
  return violations.value.filter(v => {
    const matchesStatus = filters.value.status === 'all' || v.status === filters.value.status;
    const matchesSearch = !filters.value.search || v.vehicleNumber?.toLowerCase().includes(filters.value.search.toLowerCase());
    return matchesStatus && matchesSearch;
  });
});

const goBack = () => router.go(-1);

onMounted(fetchViolations);
</script>

<style scoped>
.btn {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  margin-right: 0.5rem;
}
.btn-success {
  background: #059669;
  color: white;
}
.btn-danger {
  background: #dc2626;
  color: white;
}
.btn-small {
  font-size: 0.9rem;
  padding: 0.3rem 0.7rem;
}
.card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  margin-bottom: 2rem;
}
.card-header {
  padding: 1rem;
  border-bottom: 1px solid #e5e7eb;
}
.card-body {
  padding: 1rem;
}
</style>