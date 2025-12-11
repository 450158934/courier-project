<template>
  <div class="profile-bg">
    <div class="profile-container">

      <el-card class="user-card">
        <div class="user-header">
          <el-upload
              class="avatar-uploader"
              action="http://localhost:8080/api/files/upload"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
          >
            <div v-if="user.avatar" class="avatar">
              <img :src="user.avatar" class="real-avatar">
            </div>
            <div v-else class="avatar">🤠</div> <div class="upload-tip">点击更换</div>
          </el-upload>

          <div class="info">
            <h2>{{ user.nickname || '未命名用户' }}</h2>
            <p>ID: {{ user.id }} | 角色: {{ user.role === 'runner' ? '骑手' : '雇主' }}</p>
          </div>
          <el-button type="primary" plain round @click="$router.push('/home')">返回大厅</el-button>
        </div>
      </el-card>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="8"><el-card shadow="hover" class="wallet-card" @click="showWalletDialog"><div class="stat-title">💰 我的余额 (点击看明细)</div><div class="stat-value">¥ {{ calculateEarnings }}</div></el-card></el-col>
        <el-col :span="8"><el-card shadow="hover" class="wallet-card"><div class="stat-title">📦 已送达</div><div class="stat-value">{{ completedCount }} 单</div></el-card></el-col>
        <el-col :span="8"><el-card shadow="hover" class="wallet-card"><div class="stat-title">🏃 配送中</div><div class="stat-value" style="color: #E6A23C">{{ runningCount }} 单</div></el-card></el-col>
      </el-row>

      <el-card class="list-card" style="margin-top: 20px;">
        <el-tabs v-model="activeTab" @tab-change="loadData">
          <el-tab-pane label="🚀 我配送的" name="runner">
            <el-table :data="runnerList" stripe style="width: 100%">
              <el-table-column prop="pickupAddr" label="取件点" />
              <el-table-column prop="destAddr" label="送达宿舍" />
              <el-table-column prop="price" label="赏金"><template #default="scope"><span style="color: #f56c6c;">¥{{scope.row.price}}</span></template></el-table-column>
              <el-table-column label="状态"><template #default="scope"><el-tag v-if="scope.row.status===1" type="warning">配送中</el-tag><el-tag v-else-if="scope.row.status===2" type="success">已送达</el-tag></template></el-table-column>
              <el-table-column label="操作"><template #default="scope"><el-button v-if="scope.row.status===1" type="primary" size="small" round @click="confirmDone(scope.row.id)">确认送达</el-button><span v-else>✅ 完成</span></template></el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="📝 我发布的" name="customer">
            <el-table :data="customerList" stripe>
              <el-table-column prop="pickupCode" label="取件码" />
              <el-table-column prop="destAddr" label="送达宿舍" />
              <el-table-column label="状态"><template #default="scope"><el-tag v-if="scope.row.status===0" type="info">待接单</el-tag><el-tag v-else-if="scope.row.status===1" type="warning">配送中</el-tag><el-tag v-else type="success">已完成</el-tag></template></el-table-column>
              <el-table-column label="操作"><template #default="scope"><el-button v-if="scope.row.status===0" type="danger" size="small" link @click="cancelOrder(scope.row.id)">取消</el-button></template></el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-card>

      <el-dialog v-model="walletVisible" title="💸 资金明细" width="500px">
        <el-timeline style="padding-top: 10px;">
          <el-timeline-item v-for="(log, index) in walletLogs" :key="index" :timestamp="new Date(log.createTime).toLocaleString()" :type="log.type === 1 ? 'success' : 'danger'">
            <h4>{{ log.remark }}</h4>
            <p style="font-size: 16px; font-weight: bold;" :style="{color: log.type===1 ? '#67C23A' : '#F56C6C'}">{{ log.type===1 ? '+' : '-' }} ¥{{ log.amount }}</p>
          </el-timeline-item>
        </el-timeline>
        <div v-if="walletLogs.length === 0" style="text-align: center; color: #999;">暂无流水记录</div>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, reactive } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('runner')
const runnerList = ref([])
const customerList = ref([])
const walletLogs = ref([])
const walletVisible = ref(false)

// 读取用户信息（reactive包裹，为了让头像更新时页面能自动刷新）
const localUser = JSON.parse(localStorage.getItem('user') || '{}')
const user = reactive(localUser)

const calculateEarnings = computed(() => {
  let total = 0
  runnerList.value.forEach(order => { if(order.status === 2) total += order.price })
  return total.toFixed(2)
})
const completedCount = computed(() => runnerList.value.filter(o => o.status === 2).length)
const runningCount = computed(() => runnerList.value.filter(o => o.status === 1).length)

const loadData = async () => {
  if (!user.id) return;
  try {
    const res1 = await axios.get(`http://localhost:8080/api/orders/my-orders?type=2&userId=${user.id}`)
    runnerList.value = res1.data.data
    const res2 = await axios.get(`http://localhost:8080/api/orders/my-orders?type=1&userId=${user.id}`)
    customerList.value = res2.data.data
  } catch(e) { console.error(e) }
}

const confirmDone = async (id) => {
  try {
    const res = await axios.post('http://localhost:8080/api/orders/complete', { id: id })
    if(res.data.code === 200) { ElMessage.success("订单已送达！赏金到账！"); loadData() }
  } catch(e) { ElMessage.error("操作失败") }
}

const cancelOrder = (id) => {
  ElMessageBox.confirm('确定取消？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
      .then(async () => {
        const res = await axios.post('http://localhost:8080/api/orders/cancel', { id: id })
        if(res.data.code === 200) { ElMessage.success("已取消"); loadData() }
      }).catch(() => {})
}

const showWalletDialog = async () => {
  walletVisible.value = true
  const res = await axios.get(`http://localhost:8080/api/orders/my-wallet-logs?userId=${user.id}`)
  if(res.data.code === 200) walletLogs.value = res.data.data
}

// 👇 上传成功回调
const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    user.avatar = response.data // 设置新头像 URL
    // 更新本地缓存
    localStorage.setItem('user', JSON.stringify(user))
    ElMessage.success('头像上传成功！')
  } else {
    ElMessage.error('上传失败')
  }
}

// 👇 上传前检查
const beforeAvatarUpload = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('头像必须是 JPG 或 PNG 格式!')
    return false
  }
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

onMounted(() => { loadData() })
</script>

<style scoped>
.profile-bg { background-color: #f5f7fa; min-height: 100vh; padding-top: 20px; }
.profile-container { max-width: 900px; margin: 0 auto; }
.user-card { border-radius: 12px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; }
.user-header { display: flex; align-items: center; }
/* 👇 头像样式 */
.avatar-uploader { display: flex; flex-direction: column; align-items: center; cursor: pointer; margin-right: 20px; position: relative; }
.avatar { width: 80px; height: 80px; background: white; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 40px; overflow: hidden; border: 2px solid rgba(255,255,255,0.5); }
.real-avatar { width: 100%; height: 100%; object-fit: cover; }
.upload-tip { font-size: 12px; margin-top: 5px; opacity: 0.8; }
.info h2 { margin: 0; font-size: 24px; }
.info p { margin: 5px 0 0; opacity: 0.8; font-size: 14px; }
.wallet-card { text-align: center; border-radius: 12px; cursor: pointer; transition: transform 0.2s; }
.wallet-card:hover { transform: translateY(-5px); }
.stat-title { color: #909399; font-size: 14px; margin-bottom: 10px; }
.stat-value { font-size: 24px; font-weight: bold; color: #303133; }
.list-card { border-radius: 12px; }
</style>