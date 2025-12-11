<template>
  <div class="home-container">
    <el-container class="layout-container">
      <el-header class="header">
        <div class="logo">
          <span style="font-size: 24px; margin-right: 10px;">📦</span>
          校园快递代取
        </div>
        <div class="user-actions">
          <span style="margin-right: 15px; font-weight: bold; color: #666">
            {{ user.nickname || '未登录' }}
          </span>
          <el-button color="#626aef" round @click="$router.push('/dashboard')">📊 数据监控</el-button>
          <el-button type="primary" round @click="$router.push('/profile')">👤 个人中心</el-button>
          <el-button type="danger" link @click="logout">退出</el-button>
        </div>
      </el-header>

      <el-main>
        <div class="main-content">
          <div class="banner">
            <h1>让校园生活更轻松 ✨</h1>
            <p>空闲时间跑跑腿，轻松赚取零花钱</p>
          </div>

          <el-card class="order-card" shadow="hover">
            <div class="card-header">
              <div class="left">
                <h3>🔥 实时任务大厅</h3>
                <span class="badge">{{ orders.length }} 个待接单</span>
              </div>
              <div style="display: flex; gap: 10px;">
                <el-input
                    v-model="searchText"
                    placeholder="搜取件点/宿舍..."
                    style="width: 220px;"
                    clearable
                    @clear="fetchOrders"
                    @keyup.enter="fetchOrders"
                >
                  <template #append><el-button @click="fetchOrders">🔍</el-button></template>
                </el-input>
                <el-button type="primary" size="large" @click="dialogVisible = true">发布需求</el-button>
              </div>
            </div>

            <el-table :data="orders" style="width: 100%; margin-top: 20px;" size="large">
              <el-table-column prop="pickupAddr" label="📍 取件点" width="180" />
              <el-table-column prop="destAddr" label="🏢 送达宿舍" />
              <el-table-column prop="price" label="💰 赏金" width="120">
                <template #default="scope"><span class="price-tag">¥ {{ scope.row.price }}</span></template>
              </el-table-column>
              <el-table-column label="操作" width="120" align="center">
                <template #default="scope">
                  <el-button v-if="scope.row.customerId !== user.id" type="success" round size="small" @click="takeOrder(scope.row.id)">⚡ 抢单</el-button>
                  <el-tag v-else type="info">我发的</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </el-main>
    </el-container>

    <el-dialog v-model="dialogVisible" title="发布新需求" width="500px" align-center>
      <el-form :model="newOrder" label-width="80px" size="large">
        <el-form-item label="取件点">
          <el-select v-model="newOrder.pickupAddr" placeholder="请选择" style="width: 100%">
            <el-option label="📍 北门菜鸟驿站" value="北门菜鸟驿站" />
            <el-option label="📍 南区丰巢柜" value="南区丰巢柜" />
            <el-option label="📍 京东派" value="京东派" />
          </el-select>
        </el-form-item>
        <el-form-item label="取件码"><el-input v-model="newOrder.pickupCode" /></el-form-item>
        <el-form-item label="送达宿舍"><el-input v-model="newOrder.destAddr" /></el-form-item>
        <el-form-item label="赏金"><el-input-number v-model="newOrder.price" :min="1" :step="0.5" /> 元</el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitOrder">🚀 立即发布</el-button>
        </span>
      </template>
    </el-dialog>

    <div class="chat-btn" @click="chatVisible = true">💬</div>

    <el-drawer v-model="chatVisible" title="💬 校园广场热聊" direction="rtl" size="380px">
      <div class="chat-box">
        <div class="msg-list" ref="msgListRef">
          <div v-for="(msg, i) in chatHistory" :key="i" class="msg-item">
            {{ msg }}
          </div>
        </div>
        <div class="input-area">
          <el-input v-model="chatInput" placeholder="说点什么..." @keyup.enter="sendMsg">
            <template #append><el-button @click="sendMsg">发送</el-button></template>
          </el-input>
        </div>
      </div>
    </el-drawer>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')
const orders = ref([])
const dialogVisible = ref(false)
const searchText = ref('')

// 聊天相关变量
const chatVisible = ref(false)
const chatInput = ref('')
const chatHistory = ref([])
let socket = null

const newOrder = reactive({ pickupAddr: '', pickupCode: '', destAddr: '', price: 2.0, customerId: user.id })

const fetchOrders = async () => {
  const url = searchText.value ? `http://localhost:8080/api/orders/list?keyword=${searchText.value}` : 'http://localhost:8080/api/orders/list'
  const res = await axios.get(url)
  if (res.data.code === 200) orders.value = res.data.data
}

const submitOrder = async () => {
  newOrder.customerId = user.id
  try {
    const res = await axios.post('http://localhost:8080/api/orders/add', newOrder)
    if(res.data.code === 200) { ElMessage.success('发布成功！'); dialogVisible.value = false; fetchOrders() }
  } catch(e) { ElMessage.error('系统错误') }
}

const takeOrder = async (orderId) => {
  if (!user.id) return router.push('/')
  try {
    const res = await axios.post('http://localhost:8080/api/orders/take', { id: orderId, runnerId: user.id })
    if (res.data.code === 200) { ElMessage.success('抢单成功！'); fetchOrders() }
    else { ElMessage.warning(res.data.msg); fetchOrders() }
  } catch (error) { ElMessage.error('网络错误') }
}

const logout = () => { localStorage.removeItem('user'); router.push('/') }

// 👇👇👇 WebSocket 核心逻辑 (已修复) 👇👇👇
const initWebSocket = () => {
  if (typeof WebSocket === 'undefined') return console.log('浏览器不支持WebSocket')

  // ⚠️ 重点修改：这里加上了 user.nickname
  // 如果没有昵称，就默认叫 "神秘人"
  const wsUrl = `ws://localhost:8080/ws/${user.id}/${user.nickname || '神秘人'}`

  socket = new WebSocket(wsUrl)

  socket.onopen = () => { chatHistory.value.push("🟢 系统: 连接成功！") }
  socket.onmessage = (msg) => { chatHistory.value.push(msg.data) }
  socket.onclose = () => { chatHistory.value.push("🔴 系统: 连接断开") }
}

const sendMsg = () => {
  if (!chatInput.value) return
  if (socket && socket.readyState === WebSocket.OPEN) {
    socket.send(chatInput.value)
    chatInput.value = ''
  } else { ElMessage.error("未连接") }
}

onMounted(() => {
  fetchOrders()
  initWebSocket() // 启动连接
})
</script>

<style scoped>
.home-container { height: 100vh; background-color: #f0f2f5; }
.header { background: white; box-shadow: 0 2px 8px rgba(0,0,0,0.05); display: flex; justify-content: space-between; align-items: center; padding: 0 40px; z-index: 10; }
.logo { font-size: 22px; font-weight: 800; color: #333; display: flex; align-items: center;}
.main-content { max-width: 1000px; margin: 0 auto; padding-top: 30px; }
.banner { text-align: center; margin-bottom: 30px; color: #333; }
.banner h1 { margin-bottom: 10px; color: #2c3e50; }
.banner p { color: #7f8c8d; }
.order-card { border-radius: 12px; border: none; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.badge { background: #ffeceb; color: #f56c6c; padding: 4px 10px; border-radius: 20px; font-size: 12px; font-weight: bold; }
.price-tag { color: #f56c6c; font-weight: bold; font-size: 16px; }
.empty-state { text-align: center; padding: 40px; color: #999; }

/* 聊天样式 */
.chat-btn {
  position: fixed; bottom: 30px; right: 30px;
  width: 60px; height: 60px;
  background: #409EFF; color: white;
  border-radius: 50%;
  font-size: 30px; line-height: 60px; text-align: center;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
  cursor: pointer; z-index: 999;
  transition: transform 0.2s;
}
.chat-btn:hover { transform: scale(1.1); }
.chat-box { height: 100%; display: flex; flex-direction: column; }
.msg-list { flex: 1; overflow-y: auto; padding: 10px; background: #f5f7fa; border-radius: 8px; margin-bottom: 10px; }
.msg-item { padding: 8px 10px; border-bottom: 1px dashed #eee; font-size: 14px; background: white; margin-bottom: 5px; border-radius: 4px; }
</style>