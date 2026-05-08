<template>
    <div class="profile-page">
        <!-- 用户信息卡 -->
        <div class="profile-header">
            <div class="profile-card">
                <div class="avatar-section">
                    <el-upload :action="uploadUrl"
                        :show-file-list="false" :on-success="onAvatarSuccess" :headers="uploadHeaders">
                        <div class="avatar-wrap">
                            <img :src="$imgUrl(userInfo.userAvatar)" class="profile-avatar" @error="e=>e.target.style.display='none'" />
                            <div class="avatar-overlay"><i class="el-icon-camera"></i> 更换头像</div>
                        </div>
                    </el-upload>
                </div>
                <div class="user-detail">
                    <div class="user-name">{{ userInfo.userName || '未设置昵称' }}</div>
                    <div class="user-meta">
                        <span><i class="el-icon-time"></i> {{ userInfo.createTime }}</span>
                        <span :class="userInfo.isLogin ? 'text-danger' : 'text-success'">
                            {{ userInfo.isLogin ? '已封禁' : '正常' }}
                        </span>
                    </div>
                </div>
                <div class="user-stats">
                    <div class="stat-item" @click="$router.push('/myProduct')">
                        <div class="stat-num">{{ stats.myProducts || 0 }}</div>
                        <div class="stat-text">我的商品</div>
                    </div>
                    <div class="stat-item" @click="$router.push('/orders')">
                        <div class="stat-num">{{ stats.myOrders || 0 }}</div>
                        <div class="stat-text">我的订单</div>
                    </div>
                    <div class="stat-item" @click="$router.push('/mySave')">
                        <div class="stat-num">{{ stats.mySaves || 0 }}</div>
                        <div class="stat-text">收藏</div>
                    </div>
                    <div class="stat-item" @click="$router.push('/forum')">
                        <div class="stat-num">{{ stats.myPosts || 0 }}</div>
                        <div class="stat-text">帖子</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Tab 导航 -->
        <div class="profile-tabs">
            <span v-for="t in tabs" :key="t.key"
                :class="{ active: activeTab === t.key }" @click="switchTab(t.key)">
                <i :class="t.icon"></i> {{ t.label }}
            </span>
        </div>

        <!-- 个人资料 -->
        <div v-if="activeTab === 'info'" class="profile-section">
            <el-form label-width="80px" class="info-form">
                <el-form-item label="头像">
                    <el-upload :action="uploadUrl"
                        :show-file-list="false" :on-success="onAvatarSuccess" :headers="uploadHeaders">
                        <img v-if="userInfo.userAvatar" :src="$imgUrl(userInfo.userAvatar)" class="form-avatar" />
                        <el-button v-else size="small" icon="el-icon-plus">上传头像</el-button>
                    </el-upload>
                </el-form-item>
                <el-form-item label="昵称">
                    <el-input v-model="editForm.userName" placeholder="请输入昵称" maxlength="20"></el-input>
                </el-form-item>
                <el-form-item label="邮箱">
                    <el-input v-model="editForm.userEmail" placeholder="请输入邮箱" maxlength="50"></el-input>
                </el-form-item>
                <el-form-item label="个性签名">
                    <el-input v-model="editForm.signature" type="textarea" :rows="2" placeholder="介绍一下自己吧" maxlength="200" show-word-limit></el-input>
                </el-form-item>
                <el-form-item label="账号状态">
                    <el-tag :type="userInfo.isLogin ? 'danger' : 'success'" size="small">
                        {{ userInfo.isLogin ? '已封禁' : '正常' }}
                    </el-tag>
                    <el-tag :type="userInfo.isWord ? 'danger' : 'success'" size="small" style="margin-left: 6px;">
                        {{ userInfo.isWord ? '已禁言' : '可发言' }}
                    </el-tag>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="saveProfile" :loading="saving">保存修改</el-button>
                </el-form-item>
            </el-form>
        </div>

        <!-- 安全设置 -->
        <div v-if="activeTab === 'security'" class="profile-section">
            <div class="security-list">
                <div class="security-item">
                    <div class="security-info">
                        <div class="security-title">修改密码</div>
                        <div class="security-desc">定期更换密码保障账号安全</div>
                    </div>
                    <el-button size="small" type="primary" plain @click="showPwd = !showPwd">
                        {{ showPwd ? '收起' : '修改' }}
                    </el-button>
                </div>
                <div v-if="showPwd" class="pwd-form">
                    <el-input v-model="pwdForm.oldPwd" type="password" placeholder="原密码" style="margin-bottom:10px;"></el-input>
                    <el-input v-model="pwdForm.newPwd" type="password" placeholder="新密码" style="margin-bottom:10px;"></el-input>
                    <el-input v-model="pwdForm.againPwd" type="password" placeholder="确认新密码" style="margin-bottom:10px;"></el-input>
                    <el-button type="primary" size="small" @click="changePwd" :loading="pwdSaving">确认修改</el-button>
                </div>

                <div class="security-item" style="margin-top:16px;">
                    <div class="security-info">
                        <div class="security-title">退出登录</div>
                        <div class="security-desc">退出当前账号</div>
                    </div>
                    <el-button size="small" type="danger" plain @click="logout">退出</el-button>
                </div>
            </div>
        </div>

        <!-- 收支数据 -->
        <div v-if="activeTab === 'finance'" class="profile-section">
            <div class="finance-cards">
                <div class="fin-card fin-spend">
                    <div class="fin-card-icon"><i class="el-icon-download"></i></div>
                    <div class="fin-card-info">
                        <div class="fin-card-label">总支出</div>
                        <div class="fin-card-num">¥{{ formatNum(financeData.buyAmount) }}</div>
                    </div>
                </div>
                <div class="fin-card fin-earn">
                    <div class="fin-card-icon"><i class="el-icon-upload2"></i></div>
                    <div class="fin-card-info">
                        <div class="fin-card-label">总收入</div>
                        <div class="fin-card-num">¥{{ formatNum(financeData.sellAmount) }}</div>
                    </div>
                </div>
                <div class="fin-card fin-orders">
                    <div class="fin-card-icon"><i class="el-icon-shopping-bag-1"></i></div>
                    <div class="fin-card-info">
                        <div class="fin-card-label">交易笔数</div>
                        <div class="fin-card-num">{{ (parseInt(financeData.buyTotal)||0) + (parseInt(financeData.sellTotal)||0) }}</div>
                    </div>
                </div>
            </div>
            <el-row :gutter="20" style="margin-top:20px;">
                <el-col :span="10">
                    <div class="chart-card">
                        <div class="chart-title">收支占比</div>
                        <div ref="pieChart" style="width:100%;height:280px;"></div>
                    </div>
                </el-col>
                <el-col :span="14">
                    <div class="chart-card">
                        <div class="chart-title">近6个月趋势</div>
                        <div ref="barChart" style="width:100%;height:280px;"></div>
                    </div>
                </el-col>
            </el-row>
        </div>

        <!-- 订单统计 -->
        <div v-if="activeTab === 'orderChart'" class="profile-section">
            <div class="finance-cards">
                <div class="fin-card fin-pending">
                    <div class="fin-card-icon"><i class="el-icon-time"></i></div>
                    <div class="fin-card-info">
                        <div class="fin-card-label">待支付</div>
                        <div class="fin-card-num">{{ orderStats.pending || 0 }}</div>
                    </div>
                </div>
                <div class="fin-card fin-paid">
                    <div class="fin-card-icon"><i class="el-icon-circle-check"></i></div>
                    <div class="fin-card-info">
                        <div class="fin-card-label">已支付</div>
                        <div class="fin-card-num">{{ orderStats.paid || 0 }}</div>
                    </div>
                </div>
                <div class="fin-card fin-total-orders">
                    <div class="fin-card-icon"><i class="el-icon-s-order"></i></div>
                    <div class="fin-card-info">
                        <div class="fin-card-label">订单总数</div>
                        <div class="fin-card-num">{{ (parseInt(orderStats.pending)||0) + (parseInt(orderStats.paid)||0) }}</div>
                    </div>
                </div>
            </div>
            <div class="chart-card" style="margin-top:20px;max-width:500px;">
                <div class="chart-title">订单状态分布</div>
                <div ref="orderPieChart" style="width:100%;height:300px;"></div>
            </div>
        </div>

        <!-- 地址管理 -->
        <div v-if="activeTab === 'address'" class="profile-section">
            <div class="addr-header">
                <span class="addr-title">我的收货地址</span>
                <el-button size="small" type="primary" icon="el-icon-plus" @click="showAddrDialog()">新增地址</el-button>
            </div>
            <div v-if="addresses.length" class="addr-list">
                <div v-for="a in addresses" :key="a.id" class="addr-card">
                    <div class="addr-info">
                        <div class="addr-top">
                            <span class="addr-name">{{ a.name }}</span>
                            <span class="addr-phone">{{ a.phone }}</span>
                            <el-tag v-if="a.isDefault" type="danger" size="mini" style="margin-left:8px;">默认</el-tag>
                        </div>
                        <div class="addr-detail">{{ a.address }}</div>
                    </div>
                    <div class="addr-actions">
                        <el-button size="mini" type="text" @click="showAddrDialog(a)">编辑</el-button>
                        <el-button size="mini" type="text" v-if="!a.isDefault" @click="setDefaultAddr(a.id)">设为默认</el-button>
                        <el-button size="mini" type="text" style="color:#F56C6C;" @click="deleteAddr(a.id)">删除</el-button>
                    </div>
                </div>
            </div>
            <div v-else class="empty-state">
                <i class="el-icon-location-outline" style="font-size:48px;color:#ddd;"></i>
                <p>暂无收货地址</p>
            </div>
        </div>

        <!-- 地址编辑弹窗 -->
        <el-dialog :visible.sync="addrDialogVisible" width="460px" :show-close="true" :close-on-click-modal="false">
            <div slot="title" class="dialog-title-wrap">{{ addrForm.id ? '编辑地址' : '新增地址' }}</div>
            <el-form label-width="70px">
                <el-form-item label="收件人">
                    <el-input v-model="addrForm.name" placeholder="收件人姓名" maxlength="20"></el-input>
                </el-form-item>
                <el-form-item label="手机号">
                    <el-input v-model="addrForm.phone" placeholder="手机号码" maxlength="20"></el-input>
                </el-form-item>
                <el-form-item label="地址">
                    <el-input v-model="addrForm.address" type="textarea" :rows="3" placeholder="详细地址" maxlength="200"></el-input>
                </el-form-item>
                <el-form-item label="默认">
                    <el-switch v-model="addrForm.isDefaultBool"></el-switch>
                </el-form-item>
            </el-form>
            <span slot="footer">
                <el-button @click="addrDialogVisible = false" size="small">取消</el-button>
                <el-button type="primary" @click="saveAddr" :loading="addrSaving" size="small">保存</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import { getToken, clearToken } from "@/utils/storage";
import md5 from 'js-md5';
import * as echarts from 'echarts';

const UPLOAD_URL = (process.env.VUE_APP_API_BASE_URL || 'http://localhost:21090/api/campus-product-sys/v1.0') + '/file/upload';

export default {
    name: 'Myself',
    data() {
        return {
            activeTab: 'info',
            tabs: [
                { key: 'info', label: '个人资料', icon: 'el-icon-user' },
                { key: 'finance', label: '收支数据', icon: 'el-icon-data-line' },
                { key: 'orderChart', label: '订单统计', icon: 'el-icon-pie-chart' },
                { key: 'address', label: '地址管理', icon: 'el-icon-location' },
                { key: 'security', label: '安全设置', icon: 'el-icon-lock' },
            ],
            userInfo: {},
            editForm: { userName: '', userEmail: '', signature: '' },
            stats: { myProducts: 0, myOrders: 0, mySaves: 0, myPosts: 0 },
            saving: false,
            showPwd: false,
            pwdForm: { oldPwd: '', newPwd: '', againPwd: '' },
            pwdSaving: false,
            // 收支数据
            financeData: { buyTotal: 0, sellTotal: 0, buyAmount: 0, sellAmount: 0, monthly: [] },
            orderStats: { pending: 0, paid: 0 },
            // 地址管理
            addresses: [],
            addrDialogVisible: false,
            addrForm: { id: null, name: '', phone: '', address: '', isDefaultBool: false },
            addrSaving: false,
            // charts
            pieChart: null, barChart: null, orderPieChart: null,
        };
    },
    computed: {
        uploadHeaders() { return { token: getToken() }; },
        uploadUrl() { return UPLOAD_URL; }
    },
    created() { this.loadUser(); this.loadStats(); },
    beforeDestroy() {
        this.disposeChart('pieChart');
        this.disposeChart('barChart');
        this.disposeChart('orderPieChart');
    },
    methods: {
        switchTab(key) {
            this.activeTab = key;
            if (key === 'finance') this.$nextTick(() => this.loadFinance());
            if (key === 'orderChart') this.$nextTick(() => this.loadOrderChart());
            if (key === 'address') this.loadAddresses();
        },
        disposeChart(name) {
            if (this[name]) { this[name].dispose(); this[name] = null; }
        },
        formatNum(n) {
            const v = parseFloat(n) || 0;
            return v.toFixed(2);
        },
        loadUser() {
            this.$axios.get('/user/auth').then(res => {
                if (res.data.code === 200) {
                    this.userInfo = res.data.data;
                    this.editForm = { userName: res.data.data.userName, userEmail: res.data.data.userEmail, signature: res.data.data.signature || '' };
                }
            });
        },
        loadStats() {
            Promise.all([
                this.$axios.post('/product/myProducts', { current: 1, size: 1000 }),
                this.$axios.post('/orders/myOrders', { current: 1, size: 1000 }),
                this.$axios.get('/interaction/mySave', { params: { current: 1, size: 1000 } }),
                this.$axios.post('/forum/myPosts', { current: 1, size: 1000 }),
            ]).then(([p, o, s, f]) => {
                if (p.data.code === 200) this.stats.myProducts = p.data.total || 0;
                if (o.data.code === 200) this.stats.myOrders = o.data.total || 0;
                if (s.data.code === 200) this.stats.mySaves = s.data.total || 0;
                if (f.data.code === 200) this.stats.myPosts = f.data.total || 0;
            }).catch(() => {});
        },
        onAvatarSuccess(res) {
            if ((res.code || res.msg) && !res.code) return;
            const url = res.data || res.msg;
            this.userInfo.userAvatar = url;
            this.$axios.put('/user/update', {
                userAvatar: url, userName: this.userInfo.userName, userEmail: this.userInfo.userEmail
            }).then(() => { this.$message.success('头像已更新'); this.loadUser(); });
        },
        saveProfile() {
            this.saving = true;
            this.$axios.put('/user/update', {
                userAvatar: this.userInfo.userAvatar,
                userName: this.editForm.userName,
                userEmail: this.editForm.userEmail,
                signature: this.editForm.signature
            }).then(res => {
                if (res.data.code === 200) { this.$message.success('保存成功'); this.loadUser(); }
            }).finally(() => { this.saving = false; });
        },
        changePwd() {
            if (!this.pwdForm.oldPwd || !this.pwdForm.newPwd || !this.pwdForm.againPwd) {
                this.$message.error('请填写完整密码信息'); return;
            }
            if (this.pwdForm.newPwd !== this.pwdForm.againPwd) {
                this.$message.error('两次密码不一致'); return;
            }
            this.pwdSaving = true;
            const dto = {
                oldPwd: md5(md5(this.pwdForm.oldPwd)),
                newPwd: md5(md5(this.pwdForm.newPwd)),
                againPwd: md5(md5(this.pwdForm.againPwd)),
            };
            this.$axios.put('/user/updatePwd', dto).then(res => {
                if (res.data.code === 200) { this.$message.success('密码修改成功，请重新登录'); clearToken(); this.$router.push('/login'); }
                else { this.$message.error(res.data.msg); }
            }).finally(() => { this.pwdSaving = false; });
        },
        async logout() {
            const ok = await this.$swalConfirm({ title: '退出登录', text: '确认退出？', icon: 'warning' });
            if (ok) { clearToken(); this.$router.push('/login'); }
        },

        // ===== 收支数据 =====
        loadFinance() {
            this.$axios.get('/orders/stats').then(res => {
                if (res.data.code === 200) {
                    const d = res.data.data;
                    this.financeData = d;
                    this.orderStats = d.statusCounts || {};
                    this.$nextTick(() => {
                        this.renderPieChart();
                        this.renderBarChart();
                    });
                }
            });
        },
        renderPieChart() {
            if (!this.$refs.pieChart) return;
            this.disposeChart('pieChart');
            this.pieChart = echarts.init(this.$refs.pieChart);
            const buy = parseFloat(this.financeData.buyAmount) || 0;
            const sell = parseFloat(this.financeData.sellAmount) || 0;
            this.pieChart.setOption({
                tooltip: { trigger: 'item', formatter: '{b}: ¥{c} ({d}%)' },
                legend: { bottom: 0, data: ['支出', '收入'] },
                color: ['#F56C6C', '#67C23A'],
                series: [{
                    type: 'pie', radius: ['40%', '70%'], center: ['50%', '45%'],
                    label: { formatter: '{b}\n¥{c}', fontSize: 12 },
                    data: [
                        { value: buy.toFixed(2), name: '支出' },
                        { value: sell.toFixed(2), name: '收入' },
                    ]
                }]
            });
        },
        renderBarChart() {
            if (!this.$refs.barChart) return;
            this.disposeChart('barChart');
            this.barChart = echarts.init(this.$refs.barChart);
            const monthly = this.financeData.monthly || [];
            const months = monthly.map(m => m.month ? m.month.substring(5) : '');
            const spend = monthly.map(m => parseFloat(m.spend) || 0);
            const earn = monthly.map(m => parseFloat(m.earn) || 0);
            this.barChart.setOption({
                tooltip: { trigger: 'axis' },
                legend: { bottom: 0, data: ['支出', '收入'] },
                color: ['#F56C6C', '#67C23A'],
                grid: { left: 50, right: 20, top: 20, bottom: 50 },
                xAxis: { type: 'category', data: months },
                yAxis: { type: 'value', axisLabel: { formatter: '¥{value}' } },
                series: [
                    { name: '支出', type: 'bar', data: spend, barWidth: 20, itemStyle: { borderRadius: [4,4,0,0] } },
                    { name: '收入', type: 'bar', data: earn, barWidth: 20, itemStyle: { borderRadius: [4,4,0,0] } },
                ]
            });
        },

        // ===== 订单统计 =====
        loadOrderChart() {
            this.$axios.get('/orders/stats').then(res => {
                if (res.data.code === 200) {
                    const d = res.data.data;
                    this.orderStats = d.statusCounts || {};
                    this.$nextTick(() => this.renderOrderPieChart());
                }
            });
        },
        renderOrderPieChart() {
            if (!this.$refs.orderPieChart) return;
            this.disposeChart('orderPieChart');
            this.orderPieChart = echarts.init(this.$refs.orderPieChart);
            const pending = parseInt(this.orderStats.pending) || 0;
            const paid = parseInt(this.orderStats.paid) || 0;
            this.orderPieChart.setOption({
                tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
                legend: { bottom: 0, data: ['待支付', '已支付'] },
                color: ['#E6A23C', '#67C23A'],
                series: [{
                    type: 'pie', radius: ['40%', '70%'], center: ['50%', '45%'],
                    label: { formatter: '{b}\n{c}笔', fontSize: 13 },
                    data: [
                        { value: pending, name: '待支付' },
                        { value: paid, name: '已支付' },
                    ]
                }]
            });
        },

        // ===== 地址管理 =====
        loadAddresses() {
            this.$axios.get('/address/list').then(res => {
                if (res.data.code === 200) this.addresses = res.data.data || [];
            });
        },
        showAddrDialog(addr) {
            if (addr) {
                this.addrForm = { id: addr.id, name: addr.name, phone: addr.phone, address: addr.address, isDefaultBool: !!addr.isDefault };
            } else {
                this.addrForm = { id: null, name: '', phone: '', address: '', isDefaultBool: false };
            }
            this.addrDialogVisible = true;
        },
        saveAddr() {
            if (!this.addrForm.name) { this.$message.error('请输入收件人'); return; }
            if (!this.addrForm.phone) { this.$message.error('请输入手机号'); return; }
            if (!this.addrForm.address) { this.$message.error('请输入地址'); return; }
            this.addrSaving = true;
            this.$axios.post('/address/save', {
                id: this.addrForm.id,
                name: this.addrForm.name,
                phone: this.addrForm.phone,
                address: this.addrForm.address,
                isDefault: this.addrForm.isDefaultBool ? 1 : 0,
            }).then(res => {
                if (res.data.code === 200) {
                    this.$message.success('保存成功');
                    this.addrDialogVisible = false;
                    this.loadAddresses();
                } else { this.$message.error(res.data.msg); }
            }).finally(() => { this.addrSaving = false; });
        },
        deleteAddr(id) {
            this.$confirm('确定删除该地址吗？', '提示', { type: 'warning' }).then(() => {
                this.$axios.delete('/address/delete/' + id).then(res => {
                    if (res.data.code === 200) { this.$message.success('已删除'); this.loadAddresses(); }
                });
            }).catch(() => {});
        },
        setDefaultAddr(id) {
            this.$axios.put('/address/default/' + id).then(res => {
                if (res.data.code === 200) { this.$message.success('已设为默认'); this.loadAddresses(); }
            });
        },
    }
};
</script>

<style scoped lang="scss">
.profile-page { min-height: 400px; }
.profile-header { margin-bottom: 20px; }
.profile-card {
    background: #fff; border-radius: 12px; padding: 28px 32px;
    display: flex; align-items: center; gap: 24px; box-shadow: 0 2px 12px rgba(0,0,0,.04);
}
.avatar-section { flex-shrink: 0; }
.avatar-wrap {
    position: relative; width: 80px; height: 80px; border-radius: 50%; overflow: hidden; cursor: pointer;
}
.profile-avatar { width: 100%; height: 100%; object-fit: cover; }
.avatar-overlay {
    position: absolute; inset: 0; background: rgba(0,0,0,.4); color: #fff;
    display: flex; align-items: center; justify-content: center; font-size: 12px;
    opacity: 0; transition: all .3s;
}
.avatar-wrap:hover .avatar-overlay { opacity: 1; }
.user-detail { flex: 1;
    .user-name { font-size: 22px; font-weight: 600; color: #303133; margin-bottom: 8px; }
    .user-meta { display: flex; gap: 16px; font-size: 12px; color: #909399;
        .text-success { color: #67C23A; } .text-danger { color: #F56C6C; }
    }
}
.user-stats { display: flex; gap: 28px; flex-shrink: 0;
    .stat-item { text-align: center; cursor: pointer; padding: 8px 12px; border-radius: 8px; transition: all .2s;
        &:hover { background: #f5f7fa; }
        .stat-num { font-size: 22px; font-weight: 700; color: #303133; }
        .stat-text { font-size: 11px; color: #909399; margin-top: 4px; }
    }
}

.profile-tabs { display: flex; gap: 0; margin-bottom: 16px; flex-wrap: wrap;
    span { padding: 10px 20px; cursor: pointer; font-size: 14px; color: #909399; border-bottom: 2px solid transparent; transition: all .2s;
        &:hover { color: #409EFF; }
        &.active { color: #409EFF; font-weight: 600; border-bottom-color: #409EFF; }
    }
}

.profile-section { background: #fff; border-radius: 8px; padding: 24px; box-shadow: 0 2px 12px rgba(0,0,0,.04); }
.info-form { max-width: 500px;
    .form-avatar { width: 64px; height: 64px; border-radius: 50%; object-fit: cover; cursor: pointer; }
}

.security-list {
    .security-item { display: flex; justify-content: space-between; align-items: center; padding: 14px 0; border-bottom: 1px solid #f5f5f5; }
    .security-title { font-size: 14px; color: #303133; }
    .security-desc { font-size: 12px; color: #bbb; margin-top: 4px; }
}
.pwd-form { padding: 10px 0 16px; max-width: 320px; }

// 收支数据
.finance-cards { display: flex; gap: 16px; flex-wrap: wrap; }
.fin-card {
    flex: 1; min-width: 160px; display: flex; align-items: center; gap: 14px;
    padding: 20px; border-radius: 12px; background: #f8f9fb;
}
.fin-card-icon {
    width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center;
    font-size: 22px; color: #fff;
}
.fin-spend .fin-card-icon { background: linear-gradient(135deg, #F56C6C, #e74c3c); }
.fin-earn .fin-card-icon { background: linear-gradient(135deg, #67C23A, #27ae60); }
.fin-orders .fin-card-icon { background: linear-gradient(135deg, #409EFF, #2980b9); }
.fin-pending .fin-card-icon { background: linear-gradient(135deg, #E6A23C, #f39c12); }
.fin-paid .fin-card-icon { background: linear-gradient(135deg, #67C23A, #27ae60); }
.fin-total-orders .fin-card-icon { background: linear-gradient(135deg, #409EFF, #2980b9); }
.fin-card-label { font-size: 12px; color: #909399; }
.fin-card-num { font-size: 22px; font-weight: 700; color: #303133; margin-top: 4px; }

.chart-card {
    background: #f8f9fb; border-radius: 12px; padding: 16px;
}
.chart-title { font-size: 14px; font-weight: 600; color: #303133; margin-bottom: 8px; }

// 地址管理
.addr-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.addr-title { font-size: 16px; font-weight: 600; color: #303133; }
.addr-list { display: flex; flex-direction: column; gap: 12px; }
.addr-card {
    display: flex; justify-content: space-between; align-items: center;
    padding: 16px; border: 1px solid #f0f0f0; border-radius: 10px; transition: all .2s;
    &:hover { border-color: #409EFF; background: #fafbff; }
}
.addr-info { flex: 1; }
.addr-top { display: flex; align-items: center; margin-bottom: 6px; }
.addr-name { font-size: 15px; font-weight: 600; color: #303133; }
.addr-phone { font-size: 13px; color: #666; margin-left: 12px; }
.addr-detail { font-size: 13px; color: #909399; }
.addr-actions { display: flex; gap: 4px; flex-shrink: 0; }

.empty-state { text-align: center; padding: 60px 0; color: #999; }
.empty-state p { margin-top: 12px; font-size: 14px; }
.dialog-title-wrap { font-size: 16px; font-weight: 600; }
</style>
