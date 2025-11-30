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
          <el-input
              v-model="form.username"
              placeholder="请输入账号"
              size="large"
              prefix-icon="User"
          />
        </el-form-item>
        <el-form-item>
          <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              prefix-icon="Lock"
              show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%;" size="large">
            立即登录
          </el-button>
        </el-form-item>

        <div style="text-align: right;">
          <el-link type="primary" @click="$router.push('/register')">没有账号？去注册一个</el-link>
        </div>

      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue' // 引入图标美化一下

const router = useRouter()
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
    const res = await axios.post('http://localhost:8080/login', form)

    if (res.data.code === 200) {
      ElMessage.success('登录成功！')

      // ⚠️ 关键步骤：把用户信息存到浏览器，方便主页使用
      localStorage.setItem('user', JSON.stringify(res.data.data))

      router.push('/home')
    } else {
      ElMessage.error(res.data.msg || '登录失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('连接服务器失败')
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
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.card-header {
  text-align: center;
}
.card-header h2 {
  margin: 0;
  color: #303133;
}
</style>