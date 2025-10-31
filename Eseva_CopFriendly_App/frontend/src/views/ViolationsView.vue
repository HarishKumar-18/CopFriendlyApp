<template>
  <div class="p-6">
    <div class="bg-white shadow rounded-lg p-6">
      <h1 class="text-2xl font-bold text-gray-900 mb-4">Traffic Violations</h1>
      <p class="text-gray-600">This page will display all traffic violations.</p>
      <div class="mt-4">
        <router-link 
          to="/violations/create" 
          class="material-button material-button-primary"
        >
          Register New Violation
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ViolationsView'
}
</script>