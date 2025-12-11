<template>
  <div class="dashboard-container">
    <div class="header">
      <h2>📊 {{ user.nickname || '用户' }} 的数据看板</h2>
      <el-button type="primary" plain @click="$router.push('/home')">返回大厅</el-button>
    </div>

    <el-row :gutter="20" style="margin-bottom: 30px;">
      <el-col :span="6">
        <el-card shadow="hover" class="data-card blue">
          <div class="label">👥 平台总用户</div>
          <div class="num">{{ stats.userCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card green">
          <div class="label">📦 参与订单</div>
          <div class="num">{{ stats.orderCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card orange">
          <div class="label">💰 个人交易额</div>
          <div class="num">¥ {{ stats.totalMoney || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card purple">
          <div class="label">✅ 已完成</div>
          <div class="num">{{ stats.status2 || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card style="margin-bottom: 20px;">
          <h3>📈 订单状态分布</h3>
          <div id="barChart" style="width: 100%; height: 300px;"></div>
        </el-card>
        <el-card>
          <h3>🍰 订单占比</h3>
          <div id="pieChart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="rank-card">
          <template #header>
            <div style="display: flex; align-items: center; justify-content: space-between;">
              <span style="font-weight: bold; font-size: 18px;">🏆 校园单王排行榜</span>
              <el-tag type="danger" effect="dark">TOP 5</el-tag>
            </div>
          </template>

          <div class="rank-list">
            <div v-for="(item, index) in rankList" :key="index" class="rank-item">
              <div class="rank-index" :class="'top-' + (index + 1)">{{ index + 1 }}</div>
              <div class="rank-avatar">
                <img :src="item.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
              </div>
              <div class="rank-info">
                <div class="name">{{ item.nickname }}</div>
                <div class="desc">跑腿大神</div>
              </div>
              <div class="rank-score">{{ item.count }} 单</div>
            </div>

            <div v-if="rankList.length === 0" style="text-align: center; padding: 20px; color: #999;">
              暂无数据，快去抢单做第一！
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'

const stats = ref({})
const rankList = ref([]) // 排行榜数据
const user = JSON.parse(localStorage.getItem('user') || '{}')

const initCharts = () => {
  const barChart = echarts.init(document.getElementById('barChart'))
  barChart.setOption({
    tooltip: {},
    xAxis: { data: ['待接单', '配送中', '已完成'] },
    yAxis: {},
    series: [{
      name: '单量',
      type: 'bar',
      data: [stats.value.status0, stats.value.status1, stats.value.status2],
      itemStyle: { color: '#409EFF' },
      barWidth: '40%'
    }]
  })

  const pieChart = echarts.init(document.getElementById('pieChart'))
  pieChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      name: '订单状态',
      type: 'pie',
      radius: '60%',
      data: [
        { value: stats.value.status0, name: '待接单' },
        { value: stats.value.status1, name: '配送中' },
        { value: stats.value.status2, name: '已完成' }
      ],
      emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
    }]
  })
}

onMounted(async () => {
  try {
    // 1. 获取统计数据
    const res = await axios.get(`http://localhost:8080/api/stats/dashboard?userId=${user.id}`)
    if(res.data.code === 200) {
      stats.value = res.data.data
      initCharts()
    }

    // 2. 获取排行榜数据
    const rankRes = await axios.get('http://localhost:8080/api/stats/rank')
    if(rankRes.data.code === 200) {
      rankList.value = rankRes.data.data
    }
  } catch(e) { console.error(e) }
})
</script>

<style scoped>
.dashboard-container { padding: 20px; background-color: #f0f2f5; min-height: 100vh; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.data-card { color: white; text-align: center; border: none; }
.label { font-size: 14px; opacity: 0.9; }
.num { font-size: 28px; font-weight: bold; margin-top: 5px; }

.blue { background: linear-gradient(135deg, #36D1DC, #5B86E5); }
.green { background: linear-gradient(135deg, #11998e, #38ef7d); }
.orange { background: linear-gradient(135deg, #FF512F, #DD2476); }
.purple { background: linear-gradient(135deg, #8E2DE2, #4A00E0); }

/* 排行榜样式 */
.rank-card { height: 100%; min-height: 500px; }
.rank-item { display: flex; align-items: center; padding: 15px 0; border-bottom: 1px solid #f0f0f0; }
.rank-index { width: 30px; height: 30px; line-height: 30px; text-align: center; border-radius: 50%; background: #eee; font-weight: bold; color: #666; margin-right: 15px; }
.rank-index.top-1 { background: #FFD700; color: white; box-shadow: 0 2px 5px rgba(255, 215, 0, 0.5); }
.rank-index.top-2 { background: #C0C0C0; color: white; }
.rank-index.top-3 { background: #CD7F32; color: white; }
.rank-avatar img { width: 45px; height: 45px; border-radius: 50%; margin-right: 15px; border: 2px solid #fff; box-shadow: 0 2px 5px rgba(0,0,0,0.1); object-fit: cover; }
.rank-info { flex: 1; }
.rank-info .name { font-weight: bold; font-size: 16px; color: #333; }
.rank-info .desc { font-size: 12px; color: #999; margin-top: 2px; }
.rank-score { font-size: 16px; font-weight: bold; color: #f56c6c; }
</style>