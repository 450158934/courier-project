import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
// 👇 1. 引入新页面
import Profile from '../views/Profile.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        { path: '/', name: 'login', component: Login },
        { path: '/home', name: 'home', component: Home },
        // 👇 2. 添加新路由
        { path: '/profile', name: 'profile', component: Profile }
    ]
})

export default router