import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import Profile from '../views/Profile.vue'
import Register from '../views/Register.vue' // 新增
import Dashboard from '../views/Dashboard.vue' // 新增

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        { path: '/', name: 'login', component: Login },
        { path: '/home', name: 'home', component: Home },
        { path: '/profile', name: 'profile', component: Profile },
        { path: '/register', name: 'register', component: Register }, // 新增
        { path: '/dashboard', name: 'dashboard', component: Dashboard } // 新增
    ]
})
export default router