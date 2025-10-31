<template>
  <div class="admin-users p-4">
    <h2 class="text-2xl font-bold mb-4">Admin — Users</h2>

    <div class="mb-4">
      <label style="margin-right:1rem">
        <input type="checkbox" v-model="usePendingOnly" /> Show pending only
      </label>
      <button @click="loadUsers" class="btn">Refresh</button>
    </div>

    <div v-if="loading">Loading users...</div>
    <div v-else>
      <div v-if="error" class="text-red-600 mb-2">{{ error }}</div>
      <table v-if="users.length" class="min-w-full table-auto border-collapse">
        <thead>
          <tr>
            <th class="border px-2 py-1">ID</th>
            <th class="border px-2 py-1">Username</th>
            <th class="border px-2 py-1">Name</th>
            <th class="border px-2 py-1">Email</th>
            <th class="border px-2 py-1">Role</th>
            <th class="border px-2 py-1">Created At</th>
            <th class="border px-2 py-1">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="u in users" :key="u.id || u._id">
            <td class="border px-2 py-1">{{ u.id || u._id }}</td>
            <td class="border px-2 py-1">{{ u.username }}</td>
            <td class="border px-2 py-1">{{ u.name }}</td>
            <td class="border px-2 py-1">{{ u.email }}</td>
            <td class="border px-2 py-1">{{ u.role || u.roles || 'user' }}</td>
            <td class="border px-2 py-1">{{ formatDate(u.createdAt) }}</td>
            <td class="border px-2 py-1">
              <button @click="approve(u.id || u._id)" class="btn" style="margin-right:.5rem">Approve</button>
              <button @click="reject(u.id || u._id)" class="btn" style="background:#e53e3e">Reject</button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else class="text-gray-600">No users found.</div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { authAPI } from '@/services/api'

export default {
  name: 'AdminUsersView',
  setup() {
    const users = ref([])
    const loading = ref(false)
    const error = ref(null)
    const usePendingOnly = ref(true)

    const loadUsers = async () => {
      loading.value = true
      error.value = null
      try {
        const res = usePendingOnly.value ? await authAPI.listPending() : await authAPI.list()
        users.value = res.data || []
      } catch (err) {
        console.error('Failed to load users', err)
        users.value = []
        error.value = 'Failed to load users from backend'
      } finally {
        loading.value = false
      }
    }

    const approve = async (id) => {
      try {
        await authAPI.approve(id)
        await loadUsers()
      } catch (err) {
        console.error('Approve failed', err)
        error.value = 'Approve failed'
      }
    }

    const reject = async (id) => {
      try {
        await authAPI.reject(id)
        await loadUsers()
      } catch (err) {
        console.error('Reject failed', err)
        error.value = 'Reject failed'
      }
    }

    const formatDate = (v) => {
      if (!v) return ''
      const d = new Date(v)
      return d.toLocaleString()
    }

    // load once on mount
    loadUsers()

    return { users, loading, loadUsers, formatDate, approve, reject, usePendingOnly, error }
  },
}
</script>

<style scoped>
.btn {
  background: #2b6cb0;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 0.25rem;
}

table th, table td {
  text-align: left;
}
</style>
