<template>
  <div class="profile-bg">
    <div class="profile-container">

      <el-card class="user-card">
        <div class="user-header">
          <div class="avatar">🤠</div>
          <div class="info">
            <h2>{{ user.nickname || '我是骑手' }}</h2>
            <p>ID: {{ user.id || '8888' }} | 信誉分: 100</p>
          </div>
          <el-button type="primary" plain round @click="$router.push('/home')">返回大厅</el-button>
        </div>
      </el-card>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="8">
          <el-card shadow="hover" class="wallet-card">
            <div class="stat-title">💰 我的余额</div>
            <div class="stat-value">¥ {{ calculateEarnings }}</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="wallet-card">
            <div class="stat-title">📦 已送达</div>
            <div class="stat-value">{{ completedCount }} 单</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="wallet-card">
            <div class="stat-title">🏃 配送中</div>
            <div class="stat-value" style="color: #E6A23C">{{ runningCount }} 单</div>
          </el-card>
        </el-col>
      </el-row>

      <el-card class="list-card" style="margin-top: 20px;">
        <el-tabs v-model="activeTab" @tab-change="loadData">
          <el-tab-pane label="🚀 我配送的" name="runner">
            <el-table :data="runnerList" stripe style="width: 100%">
              <el-table-column prop="pickupAddr" label="取件点" />
              <el-table-column prop="destAddr" label="送达宿舍" />
              <el-table-column prop="price" label="赏金" width="100">
                <template #default="scope">
                  <span style="color: #f56c6c; font-weight: bold;">¥{{scope.row.price}}</span>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="120">
                <template #default="scope">
                  <el-tag v-if="scope.row.status===1" type="warning" effect="dark">配送中</el-tag>
                  <el-tag v-else-if="scope.row.status===2" type="success" effect="dark">已送达</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="scope">
                  <el-button v-if="scope.row.status===1" type="primary" size="small" round @click="confirmDone(scope.row.id)">确认送达</el-button>
                  <span v-else style="color: #67C23A; font-size: 12px;">✅ 任务完成</span>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="📝 我发布的" name="customer">
            <el-table :data="customerList" stripe>
              <el-table-column prop="pickupCode" label="取件码" />
              <el-table-column prop="destAddr" label="送达宿舍" />
              <el-table-column label="状态" width="120">
                <template #default="scope">
                  <el-tag v-if="scope.row.status===0" type="info">待接单</el-tag>
                  <el-tag v-else-if="scope.row.status===1" type="warning">配送中</el-tag>
                  <el-tag v-else type="success">已完成</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const activeTab = ref('runner')
const runnerList = ref([])
const customerList = ref([])
const user = ref({ nickname: '管理员', id: 'Admin' }) // 模拟用户数据

// 计算属性：自动算出赚了多少钱 (只算已送达的)
const calculateEarnings = computed(() => {
  let total = 0
  runnerList.value.forEach(order => {
    if(order.status === 2) total += order.price
  })
  return total.toFixed(2)
})

// 计算属性：统计单数
const completedCount = computed(() => runnerList.value.filter(o => o.status === 2).length)
const runningCount = computed(() => runnerList.value.filter(o => o.status === 1).length)

const loadData = async () => {
  try {
    const res1 = await axios.get('http://localhost:8080/api/orders/my-orders?type=2')
    runnerList.value = res1.data.data
    const res2 = await axios.get('http://localhost:8080/api/orders/my-orders?type=1')
    customerList.value = res2.data.data
  } catch(e) {
    console.error(e)
  }
}

const confirmDone = async (id) => {
  try {
    const res = await axios.post('http://localhost:8080/api/orders/complete', { id: id })
    if(res.data.code === 200) {
      ElMessage.success("订单已送达！赏金到账！")
      loadData()
    }
  } catch(e) {
    ElMessage.error("操作失败")
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.profile-bg {
  background-color: #f5f7fa;
  min-height: 100vh;
  padding-top: 20px;
}
.profile-container {
  max-width: 900px;
  margin: 0 auto;
}
.user-card {
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
}
.user-header {
  display: flex;
  align-items: center;
}
.avatar {
  width: 60px;
  height: 60px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  margin-right: 20px;
}
.info h2 { margin: 0; font-size: 24px; }
.info p { margin: 5px 0 0; opacity: 0.8; font-size: 14px; }

.wallet-card {
  text-align: center;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.2s;
}
.wallet-card:hover { transform: translateY(-5px); }
.stat-title { color: #909399; font-size: 14px; margin-bottom: 10px; }
.stat-value { font-size: 24px; font-weight: bold; color: #303133; }
.list-card { border-radius: 12px; }
</style>