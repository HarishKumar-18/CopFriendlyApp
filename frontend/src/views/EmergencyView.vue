<template>
  <div class="p-6">
    <div class="bg-white shadow rounded-lg p-6">
      <h1 class="text-2xl font-bold text-gray-900 mb-4">Emergencies</h1>
      <p class="text-gray-600">This page will display all emergencies.</p>
      <div class="mt-4">
        <router-link 
          to="/emergencies/create" 
          class="material-button material-button-primary"
        >
          Register New Emergency
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'EmergencyView'
}
</script>