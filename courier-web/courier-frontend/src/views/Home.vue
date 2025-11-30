<template>
  <div class="home-container">
    <el-container class="layout-container">
      <el-header class="header">
        <div class="logo">
          <span style="font-size: 24px; margin-right: 10px;">📦</span>
          校园快递代取
        </div>
        <div class="user-actions">
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
              <div>
                <el-button type="primary" size="large" icon="Plus" @click="dialogVisible = true">发布需求</el-button>
                <el-button icon="Refresh" circle @click="fetchOrders"></el-button>
              </div>
            </div>

            <el-table :data="orders" style="width: 100%; margin-top: 20px;" size="large">
              <el-table-column prop="pickupAddr" label="📍 取件点" width="180" />
              <el-table-column prop="destAddr" label="🏢 送达宿舍" />
              <el-table-column prop="price" label="💰 赏金" width="120">
                <template #default="scope">
                  <span class="price-tag">¥ {{ scope.row.price }}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120" align="center">
                <template #default="scope">
                  <el-button type="success" round size="small" @click="takeOrder(scope.row.id)">⚡ 抢单</el-button>
                </template>
              </el-table-column>
            </el-table>

            <div v-if="orders.length === 0" class="empty-state">
              <p>暂时没有订单，去发一个吧~</p>
            </div>
          </el-card>
        </div>
      </el-main>
    </el-container>

    <el-dialog v-model="dialogVisible" title="发布新需求" width="500px" align-center>
      <el-form :model="newOrder" label-width="80px" size="large">
        <el-form-item label="取件点">
          <el-select v-model="newOrder.pickupAddr" placeholder="请选择取件点" style="width: 100%">
            <el-option label="📍 北门菜鸟驿站" value="北门菜鸟驿站" />
            <el-option label="📍 南区丰巢柜" value="南区丰巢柜" />
            <el-option label="📍 京东派" value="京东派" />
            <el-option label="📍 西门顺丰" value="西门顺丰" />
          </el-select>
        </el-form-item>
        <el-form-item label="取件码">
          <el-input v-model="newOrder.pickupCode" placeholder="例如：3-2056" />
        </el-form-item>
        <el-form-item label="送达宿舍">
          <el-input v-model="newOrder.destAddr" placeholder="例如：5号楼302" />
        </el-form-item>
        <el-form-item label="赏金">
          <el-input-number v-model="newOrder.price" :min="1" :step="0.5" /> <span style="margin-left:10px">元</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitOrder">🚀 立即发布</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const orders = ref([])
const dialogVisible = ref(false)

const newOrder = reactive({
  pickupAddr: '',
  pickupCode: '',
  destAddr: '',
  price: 2.0,
  customerId: 1
})

const fetchOrders = async () => {
  const res = await axios.get('http://localhost:8080/api/orders/list')
  if (res.data.code === 200) {
    orders.value = res.data.data
  }
}

const submitOrder = async () => {
  if(!newOrder.pickupCode || !newOrder.destAddr) {
    ElMessage.warning('请把信息填写完整')
    return
  }
  try {
    const res = await axios.post('http://localhost:8080/api/orders/add', newOrder)
    if(res.data.code === 200) {
      ElMessage.success('发布成功！')
      dialogVisible.value = false
      fetchOrders()
      newOrder.pickupCode = ''
      newOrder.destAddr = ''
    }
  } catch(e) { ElMessage.error('系统错误') }
}

const takeOrder = async (orderId) => {
  try {
    const res = await axios.post('http://localhost:8080/api/orders/take', { id: orderId })
    if (res.data.code === 200) {
      ElMessage.success('抢单成功！请尽快配送')
      fetchOrders()
    } else {
      ElMessage.warning(res.data.msg)
      fetchOrders()
    }
  } catch (error) { ElMessage.error('网络错误') }
}

onMounted(() => { fetchOrders() })
const logout = () => { router.push('/') }
</script>

<style scoped>
.home-container { height: 100vh; background-color: #f0f2f5; }
.header {
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  z-index: 10;
}
.logo { font-size: 22px; font-weight: 800; color: #333; display: flex; align-items: center;}
.main-content { max-width: 1000px; margin: 0 auto; padding-top: 30px; }
.banner { text-align: center; margin-bottom: 30px; color: #333; }
.banner h1 { margin-bottom: 10px; color: #2c3e50; }
.banner p { color: #7f8c8d; }
.order-card { border-radius: 12px; border: none; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.left { display: flex; align-items: center; gap: 15px; }
.badge { background: #ffeceb; color: #f56c6c; padding: 4px 10px; border-radius: 20px; font-size: 12px; font-weight: bold; }
.price-tag { color: #f56c6c; font-weight: bold; font-size: 16px; }
.empty-state { text-align: center; padding: 40px; color: #999; }
</style>