import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './style.css'
import Toast from './components/Toast.vue'
import ConfirmModal from './components/ConfirmModal.vue'
import InputModal from './components/InputModal.vue'

const app = createApp(App)

app.use(createPinia())
app.use(router)

// Register global overlay components to ensure they're always resolvable
app.component('Toast', Toast)
app.component('ConfirmModal', ConfirmModal)
app.component('InputModal', InputModal)

app.mount('#app')
