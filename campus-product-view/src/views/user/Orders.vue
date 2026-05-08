<template>
    <div class="orders-page">
        <div class="orders-header">
            <h2>我的订单</h2>
        </div>

        <el-tabs v-model="activeTab" @tab-click="fetchData">
            <el-tab-pane label="我买的" name="buy"></el-tab-pane>
            <el-tab-pane label="我卖的" name="sell"></el-tab-pane>
        </el-tabs>

        <div v-if="tableData.length" class="orders-list">
            <div v-for="order in tableData" :key="order.id" class="order-card"
                :class="{ 'oc-cancelled': order.status === 5, 'oc-completed': order.status === 4 }">
                <div class="oc-top">
                    <span class="oc-code">订单号: {{ order.code }}</span>
                    <el-tag :type="statusTagType(order.status)" size="small">
                        {{ statusText(order.status) }}
                    </el-tag>
                </div>
                <div class="oc-body">
                    <img :src="$imgUrl(getCoverVal(order.coverList))" class="oc-img"
                        @click="$router.push('/product/detail/' + order.productId)" />
                    <div class="oc-info">
                        <div class="oc-pname" @click="$router.push('/product/detail/' + order.productId)">
                            {{ order.productName }}
                        </div>
                        <div class="oc-meta">
                            <span v-if="activeTab === 'sell'">
                                买家: <a class="link-user" @click="$router.push('/user/' + order.userId)">{{ order.buyerName }}</a>
                            </span>
                            <span v-if="activeTab === 'buy'">
                                卖家: <a class="link-user" @click="$router.push('/user/' + getSellerId(order))">{{ order.sellerName }}</a>
                            </span>
                            <span>{{ order.createTime }}</span>
                        </div>
                        <div v-if="order.address" class="oc-addr">
                            <i class="el-icon-location-outline"></i> {{ order.address }}
                            <span v-if="order.phone"> | <i class="el-icon-phone"></i> {{ order.phone }}</span>
                        </div>
                    </div>
                    <div class="oc-price">¥{{ order.price }}</div>
                </div>

                <!-- status=0: 待支付 -->
                <div class="oc-actions" v-if="order.status === 0">
                    <template v-if="activeTab === 'buy'">
                        <el-button size="small" type="primary" @click="confirmPay(order)">立即付款</el-button>
                        <el-button size="small" type="danger" plain @click="cancelOrder(order)">取消订单</el-button>
                    </template>
                    <template v-if="activeTab === 'sell'">
                        <el-button size="small" type="warning" plain @click="showPriceDialog(order)">修改价格</el-button>
                        <el-button size="small" type="danger" plain @click="cancelOrder(order)">取消订单</el-button>
                    </template>
                </div>

                <!-- status=1: 已支付/待发货 -->
                <div class="oc-actions" v-if="order.status === 1">
                    <template v-if="activeTab === 'sell'">
                        <el-button size="small" type="success" @click="confirmShip(order)">确认发货</el-button>
                    </template>
                    <template v-if="activeTab === 'buy'">
                        <span class="oc-waiting"><i class="el-icon-time"></i> 等待卖家发货</span>
                    </template>
                </div>

                <!-- status=2: 已发货/待收货 -->
                <div class="oc-actions" v-if="order.status === 2">
                    <template v-if="activeTab === 'buy'">
                        <el-button size="small" type="success" @click="confirmReceive(order)">确认收货</el-button>
                    </template>
                    <template v-if="activeTab === 'sell'">
                        <span class="oc-waiting"><i class="el-icon-time"></i> 等待买家确认收货</span>
                    </template>
                </div>

                <!-- status=3: 已收货/待评价 -->
                <div class="oc-actions" v-if="order.status === 3">
                    <template v-if="activeTab === 'buy'">
                        <el-button size="small" type="primary" @click="showReviewDialog(order)">去评价</el-button>
                    </template>
                    <template v-if="activeTab === 'sell'">
                        <span class="oc-waiting"><i class="el-icon-time"></i> 等待买家评价</span>
                    </template>
                </div>

                <!-- status=4: 交易完成 -->
                <div class="oc-actions" v-if="order.status === 4">
                    <span class="oc-done"><i class="el-icon-circle-check"></i> 交易完成</span>
                    <span v-if="order.payTime" class="oc-pay-time">完成时间: {{ order.payTime }}</span>
                </div>

                <!-- status=5: 已取消 -->
                <div class="oc-actions" v-if="order.status === 5">
                    <span class="oc-cancelled-text"><i class="el-icon-circle-close"></i> 已取消</span>
                </div>
            </div>
        </div>

        <div v-else class="empty-state">
            <i class="el-icon-shopping-bag-2" style="font-size:48px;color:#ddd;"></i>
            <p>{{ activeTab === 'buy' ? '暂无购买记录，去逛逛吧' : '暂无售出记录' }}</p>
        </div>

        <!-- 修改价格弹窗 -->
        <el-dialog title="修改订单价格" :visible.sync="priceDialogVisible" width="400px">
            <el-form label-width="80px">
                <el-form-item label="当前价格">
                    <span>¥{{ priceForm.oldPrice }}</span>
                </el-form-item>
                <el-form-item label="新价格">
                    <el-input-number v-model="priceForm.newPrice" :min="0.01" :precision="2" :step="1"></el-input-number>
                </el-form-item>
            </el-form>
            <span slot="footer">
                <el-button @click="priceDialogVisible = false" size="small">取消</el-button>
                <el-button type="primary" @click="updatePrice" size="small">确认修改</el-button>
            </span>
        </el-dialog>

        <!-- 评价弹窗 -->
        <el-dialog title="评价订单" :visible.sync="reviewDialogVisible" width="460px">
            <el-form label-width="60px">
                <el-form-item label="评分">
                    <div class="review-stars">
                        <i v-for="s in 5" :key="s"
                            :class="s <= reviewForm.rating ? 'el-icon-star-on' : 'el-icon-star-off'"
                            @click="reviewForm.rating = s"></i>
                        <span class="star-text">{{ reviewForm.rating }}分</span>
                    </div>
                </el-form-item>
                <el-form-item label="评价">
                    <el-input v-model="reviewForm.content" type="textarea" :rows="4"
                        placeholder="分享你的交易体验..." maxlength="500" show-word-limit></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer">
                <el-button @click="reviewDialogVisible = false" size="small">取消</el-button>
                <el-button type="primary" @click="submitReview" :loading="reviewSaving" size="small">提交评价</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
const STATUS_TEXT = { 0: '待支付', 1: '已支付', 2: '已发货', 3: '已收货', 4: '交易完成', 5: '已取消' };
const STATUS_TAG = { 0: 'warning', 1: 'primary', 2: 'info', 3: '', 4: 'success', 5: 'danger' };

export default {
    name: 'Orders',
    data() {
        return {
            activeTab: 'buy',
            tableData: [],
            timer: null,
            // 修改价格
            priceDialogVisible: false,
            priceForm: { orderId: null, oldPrice: 0, newPrice: 0 },
            // 评价
            reviewDialogVisible: false,
            reviewForm: { orderId: null, sellerId: null, rating: 5, content: '' },
            reviewSaving: false,
        };
    },
    created() {
        this.fetchData();
        this.startPoll();
        document.addEventListener('visibilitychange', this.onVis);
    },
    activated() {
        this.fetchData();
    },
    beforeDestroy() {
        clearInterval(this.timer);
        document.removeEventListener('visibilitychange', this.onVis);
    },
    methods: {
        startPoll() {
            clearInterval(this.timer);
            this.timer = setInterval(() => this.fetchData(), 8000);
        },
        onVis() {
            if (document.hidden) { clearInterval(this.timer); }
            else { this.fetchData(); this.startPoll(); }
        },
        getCoverVal(c) { return c ? c.split(',')[0] : null; },
        getSellerId(order) {
            // 从联表数据推断卖家ID（后端未返回sellerId时用productId关联）
            return order.sellerId || '';
        },
        statusText(s) { return STATUS_TEXT[s] || '未知'; },
        statusTagType(s) { return STATUS_TAG[s] || 'info'; },
        fetchData() {
            const role = this.activeTab === 'buy' ? 'buy' : 'sell';
            this.$axios.post('/orders/myOrders', { current: 1, size: 100, role }).then(res => {
                if (res.data.code === 200) {
                    this.tableData = res.data.data || [];
                }
            }).catch(() => {});
        },

        orderAction(row, url, confirmMsg, title, opts) {
            this.$confirm(confirmMsg, title, opts).then(() => {
                this.$axios.put(url, { id: row.id }).then(res => {
                    if (res.data.code === 200) { this.$message.success(opts.successMsg); this.fetchData(); }
                    else { this.$message.error(res.data.msg); }
                });
            }).catch(() => {});
        },
        confirmPay(row) {
            this.orderAction(row, '/orders/pay', `确认支付 ¥${row.price}？付款后将通知卖家发货。`, '安全确认',
                { type: 'warning', confirmButtonText: '确认付款', cancelButtonText: '再想想', successMsg: '付款成功' });
        },
        confirmShip(row) {
            this.orderAction(row, '/orders/ship', '确认已发货？发货后将通知买家。', '确认发货',
                { type: 'info', successMsg: '已确认发货' });
        },
        confirmReceive(row) {
            this.orderAction(row, '/orders/receive', '确认已收到商品？确认后订单将进入待评价状态。', '确认收货',
                { type: 'success', successMsg: '已确认收货' });
        },
        cancelOrder(row) {
            this.orderAction(row, '/orders/cancel', '取消后订单将作废，确定取消？', '取消订单',
                { type: 'warning', successMsg: '订单已取消' });
        },

        // 修改价格
        showPriceDialog(order) {
            this.priceForm = { orderId: order.id, oldPrice: order.price, newPrice: parseFloat(order.price) || 0 };
            this.priceDialogVisible = true;
        },
        updatePrice() {
            if (!this.priceForm.newPrice || this.priceForm.newPrice <= 0) {
                this.$message.error('请输入有效价格'); return;
            }
            this.$axios.put('/orders/updatePrice', {
                id: this.priceForm.orderId,
                price: this.priceForm.newPrice
            }).then(res => {
                if (res.data.code === 200) {
                    this.$message.success('价格已更新');
                    this.priceDialogVisible = false;
                    this.fetchData();
                } else { this.$message.error(res.data.msg); }
            });
        },

        // 评价
        showReviewDialog(order) {
            this.reviewForm = {
                orderId: order.id,
                sellerId: null,
                rating: 5,
                content: ''
            };
            // 获取卖家ID
            this.$axios.get('/product/getById/' + order.productId).then(res => {
                if (res.data.code === 200) {
                    this.reviewForm.sellerId = res.data.data.userId;
                }
            });
            this.reviewDialogVisible = true;
        },
        submitReview() {
            if (!this.reviewForm.content.trim()) {
                this.$message.error('请输入评价内容'); return;
            }
            if (!this.reviewForm.sellerId) {
                this.$message.error('正在获取卖家信息，请稍后'); return;
            }
            this.reviewSaving = true;
            this.$axios.post('/evaluations/insert', {
                contentType: 'USER',
                contentId: this.reviewForm.sellerId,
                orderId: this.reviewForm.orderId,
                rating: this.reviewForm.rating,
                content: this.reviewForm.content,
            }).then(res => {
                if (res.data.code === 200) {
                    this.$message.success('评价成功');
                    this.reviewDialogVisible = false;
                    // 评价后自动完成订单
                    this.$axios.put('/orders/complete', { id: this.reviewForm.orderId }).then(() => {
                        this.fetchData();
                    });
                } else { this.$message.error(res.data.msg); }
            }).finally(() => { this.reviewSaving = false; });
        },
    }
};
</script>

<style scoped lang="scss">
.orders-page { background: #fff; padding: 24px; border-radius: 12px; min-height: 400px; }
.orders-header h2 { margin: 0 0 16px; font-size: 20px; color: #303133; }

.orders-list { margin-top: 16px; }
.order-card {
    border: 1px solid #f0f0f0; border-radius: 10px; margin-bottom: 14px;
    overflow: hidden; transition: box-shadow .2s;
}
.order-card:hover { box-shadow: 0 2px 12px rgba(0,0,0,.06); }
.order-card.oc-cancelled { opacity: 0.6; background: #fafafa; }
.order-card.oc-completed { border-color: #e1f3d8; }

.oc-top {
    display: flex; justify-content: space-between; align-items: center;
    padding: 10px 16px; background: #fafafa; border-bottom: 1px solid #f0f0f0;
}
.oc-code { font-size: 12px; color: #999; }

.oc-body { display: flex; align-items: center; gap: 14px; padding: 14px 16px; }
.oc-img { width: 64px; height: 64px; object-fit: cover; border-radius: 8px; flex-shrink: 0; cursor: pointer; }
.oc-info { flex: 1; min-width: 0; }
.oc-pname { font-size: 15px; color: #303133; font-weight: 500; margin-bottom: 6px; cursor: pointer; }
.oc-pname:hover { color: #409EFF; }
.oc-meta { font-size: 12px; color: #999; display: flex; gap: 12px; flex-wrap: wrap; }
.oc-addr { font-size: 11px; color: #bbb; margin-top: 4px; }
.oc-price { font-size: 20px; color: #f56c6c; font-weight: 700; flex-shrink: 0; }

.link-user { color: #409EFF; cursor: pointer; }
.link-user:hover { text-decoration: underline; }

.oc-actions {
    display: flex; gap: 8px; justify-content: flex-end; align-items: center;
    padding: 10px 16px; border-top: 1px solid #f5f5f5;
}
.oc-done { color: #67C23A; font-size: 13px; font-weight: 500; }
.oc-waiting { color: #E6A23C; font-size: 13px; }
.oc-cancelled-text { color: #909399; font-size: 13px; }
.oc-pay-time { color: #bbb; font-size: 11px; margin-left: 12px; }

.review-stars {
    i { font-size: 24px; color: #E6A23C; cursor: pointer; margin-right: 4px; }
    .el-icon-star-off { color: #ddd; }
    .star-text { font-size: 13px; color: #909399; margin-left: 8px; }
}

.empty-state { text-align: center; padding: 80px 0; color: #999; }
.empty-state p { margin-top: 12px; font-size: 14px; }
</style>
