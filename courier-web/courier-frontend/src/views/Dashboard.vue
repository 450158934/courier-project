<template>
  <div style="padding: 20px;">
    <h1>📊 平台数据监控</h1>
    <el-button @click="$router.push('/home')">返回大厅</el-button>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="6"><el-card><h3>👥 总用户</h3><h1>{{ stats.userCount }}</h1></el-card></el-col>
      <el-col :span="6"><el-card><h3>📦 总订单</h3><h1>{{ stats.orderCount }}</h1></el-card></el-col>
      <el-col :span="6"><el-card><h3>✅ 已完成</h3><h1>{{ stats.finishedCount }}</h1></el-card></el-col>
      <el-col :span="6"><el-card><h3>💰 交易额</h3><h1>¥{{ stats.totalAmount }}</h1></el-card></el-col>
    </el-row>
    <div id="main" style="width: 100%; height: 400px; margin-top: 50px;"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'

const stats = ref({})

onMounted(async () => {
  const res = await axios.get('http://localhost:8080/api/stats')
  stats.value = res.data.data

  // 初始化图表
  var myChart = echarts.init(document.getElementById('main'));
  myChart.setOption({
    title: { text: '订单状态分布' },
    tooltip: {},
    xAxis: { data: ['待接单', '配送中', '已完成'] },
    yAxis: {},
    series: [{
      name: '单量',
      type: 'bar',
      data: [stats.value.orderCount - stats.value.finishedCount, 5, stats.value.finishedCount], // 模拟数据
      itemStyle: { color: '#409EFF' }
    }]
  });
})
</script>