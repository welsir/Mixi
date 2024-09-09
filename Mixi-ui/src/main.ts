import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { useAuthStore } from "@/stores/authStore";
import ElementPlus from 'element-plus';
import 'element-plus/theme-chalk/index.css';
import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use( createPinia())
app.use(router)
app.use(ElementPlus);
app.mount('#app')
