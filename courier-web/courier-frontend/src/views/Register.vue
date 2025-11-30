<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header><h2 style="text-align:center">🚀 注册新账号</h2></template>
      <el-form :model="form" label-width="0">
        <el-form-item><el-input v-model="form.username" placeholder="设置账号" size="large"/></el-form-item>
        <el-form-item><el-input v-model="form.password" type="password" placeholder="设置密码" size="large"/></el-form-item>
        <el-form-item><el-input v-model="form.nickname" placeholder="你的昵称" size="large"/></el-form-item>
        <el-form-item>
          <el-radio-group v-model="form.role">
            <el-radio label="student">我是雇主</el-radio>
            <el-radio label="runner">我是骑手</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="handleReg" style="width: 100%;" size="large">立即注册</el-button>
          <div style="margin-top:10px; width:100%; text-align:center">
            <el-link type="primary" @click="$router.push('/')">已有账号？去登录</el-link>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = reactive({ username: '', password: '', nickname: '', role: 'student' })

const handleReg = async () => {
  if(!form.username || !form.password) return ElMessage.warning("请填完信息")
  const res = await axios.post('http://localhost:8080/register', form)
  if(res.data.code === 200) {
    ElMessage.success("注册成功！请登录")
    router.push('/')
  } else {
    ElMessage.error(res.data.msg)
  }
}
</script>
<style scoped>
/* 复用 Login.vue 的样式 */
.login-container { display: flex; justify-content: center; align-items: center; height: 100vh; background-image: linear-gradient(120deg, #84fab0 0%, #8fd3f4 100%); }
.login-card { width: 400px; border-radius: 10px; }
</style>