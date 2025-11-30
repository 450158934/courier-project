<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>📦 校园快递代取系统</h2>
        </div>
      </template>

      <el-form :model="form" label-width="0">
        <el-form-item>
          <el-input v-model="form.username" placeholder="请输入账号 (admin)" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="请输入密码 (123456)" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%;" size="large">
            立即登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router' // 👈 加这行
// ...其他 import

const router = useRouter() // 👈 加这行初始化
import { ref, reactive } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  if(!form.username || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }

  loading.value = true
  try {
    // 🚀 发送请求给后端 8080 端口
    const res = await axios.post('http://localhost:8080/login', form)

    // 判断后端返回的 code
    if (res.data.code === 200) {
      ElMessage.success('登录成功！')
      router.push('/home')
      console.log('用户信息:', res.data.data)
      // 这里以后可以跳转到首页
    } else {
      ElMessage.error(res.data.msg || '登录失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('连接服务器失败，请确认后端已启动')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: linear-gradient(120deg, #84fab0 0%, #8fd3f4 100%);
}
.login-card {
  width: 400px;
  border-radius: 10px;
}
.card-header h2 {
  text-align: center;
  color: #333;
  margin: 0;
}
</style>