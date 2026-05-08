<template>
    <div class="dashboard">
        <el-row :gutter="20" class="stat-cards">
            <el-col :span="6">
                <el-card shadow="never" class="stat-card" style="border-top:4px solid #409EFF;">
                    <div class="stat-icon" style="color:#409EFF;"><i class="el-icon-user"></i></div>
                    <div class="stat-label">用户总数</div>
                    <div class="stat-value">{{ stats.userCount || 0 }}</div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="never" class="stat-card" style="border-top:4px solid #67C23A;">
                    <div class="stat-icon" style="color:#67C23A;"><i class="el-icon-goods"></i></div>
                    <div class="stat-label">商品总数</div>
                    <div class="stat-value">{{ stats.productCount || 0 }}</div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="never" class="stat-card" style="border-top:4px solid #E6A23C;">
                    <div class="stat-icon" style="color:#E6A23C;"><i class="el-icon-files"></i></div>
                    <div class="stat-label">分类数量</div>
                    <div class="stat-value">{{ stats.categoryCount || 0 }}</div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="never" class="stat-card" style="border-top:4px solid #F56C6C;">
                    <div class="stat-icon" style="color:#F56C6C;"><i class="el-icon-chat-dot-round"></i></div>
                    <div class="stat-label">评论总数</div>
                    <div class="stat-value">{{ stats.orderCount || 'N/A' }}</div>
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-top:20px;">
            <el-col :span="12">
                <el-card shadow="hover">
                    <div slot="header">用户角色分布</div>
                    <div ref="pieChart" style="height:300px;"></div>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card shadow="hover">
                    <div slot="header">近7天注册趋势</div>
                    <div ref="lineChart" style="height:300px;"></div>
                </el-card>
            </el-col>
        </el-row>

        <el-row style="margin-top:20px;">
            <el-card shadow="hover">
                <div slot="header">快捷入口</div>
                <el-row>
                    <el-button type="primary" icon="el-icon-user" @click="$router.push('/userManage')">用户管理</el-button>
                    <el-button type="success" icon="el-icon-goods" @click="$router.push('/productManage')" style="margin-left:15px;">商品管理</el-button>
                    <el-button type="warning" icon="el-icon-files" @click="$router.push('/categoryManage')" style="margin-left:15px;">分类管理</el-button>
                    <el-button type="info" icon="el-icon-chat-dot-round" @click="$router.push('/evaluations')" style="margin-left:15px;">评论管理</el-button>
                </el-row>
            </el-card>
        </el-row>
    </div>
</template>

<script>
export default {
    data() {
        return {
            stats: {},
        };
    },
    mounted() {
        this.fetchStats();
    },
    methods: {
        fetchStats() {
            this.$axios.get('/admin/statistics').then(res => {
                if (res.data.code === 200) {
                    this.stats = res.data.data;
                    this.$nextTick(() => {
                        this.initPieChart();
                        this.initLineChart();
                    });
                }
            }).catch(() => {});
        },
        initPieChart() {
            const chart = this.$echarts.init(this.$refs.pieChart);
            const pieData = this.stats.chart ? this.stats.chart.pieData : [];
            chart.setOption({
                tooltip: { trigger: 'item' },
                legend: { bottom: '0%' },
                series: [{
                    type: 'pie',
                    radius: ['45%', '70%'],
                    avoidLabelOverlap: false,
                    itemStyle: {
                        borderRadius: 8,
                        borderColor: '#fff',
                        borderWidth: 2
                    },
                    label: { show: true, formatter: '{b}: {c}' },
                    data: pieData
                }]
            });
        },
        initLineChart() {
            const chart = this.$echarts.init(this.$refs.lineChart);
            const lineData = this.stats.chart ? this.stats.chart.lineData : [];
            chart.setOption({
                tooltip: { trigger: 'axis' },
                xAxis: { type: 'category', data: lineData.map(d => d.name) },
                yAxis: { type: 'value' },
                series: [{
                    data: lineData.map(d => d.value),
                    type: 'line',
                    smooth: true,
                    areaStyle: { color: 'rgba(64,158,255,0.15)' },
                    itemStyle: { color: '#409EFF' }
                }]
            });
        },
    }
};
</script>

<style scoped lang="scss">
.dashboard { padding: 10px 0; }
.stat-cards {
    .stat-card {
        text-align: center; padding: 16px 0; border-radius: 12px; cursor: default;
        transition: all .3s; border: none;
        ::v-deep .el-card__body { padding: 16px; }
    }
    .stat-card:hover { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(0,0,0,.08); }
}
.stat-label { font-size: 13px; color: #909399; margin-bottom: 10px; }
.stat-value { font-size: 36px; font-weight: 700; color: #303133; }
.stat-icon { font-size: 28px; margin-bottom: 6px; }
</style>
