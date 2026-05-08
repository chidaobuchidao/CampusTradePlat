<template>
    <div class="detail-container" v-if="product.id">
        <div class="breadcrumb">
            <span @click="$router.push('/product')" class="crumb-link">首页</span>
            <span class="crumb-sep">/</span>
            <span @click="$router.back()" class="crumb-link">商品列表</span>
            <span class="crumb-sep">/</span>
            <span class="crumb-current">{{ product.name }}</span>
        </div>

        <el-row :gutter="24">
            <el-col :span="13">
                <el-carousel height="420px" v-if="coverList.length">
                    <el-carousel-item v-for="(img, idx) in coverList" :key="idx">
                        <img :src="$imgUrl(img)" style="width:100%;height:420px;object-fit:cover;border-radius:10px;" />
                    </el-carousel-item>
                </el-carousel>
                <img v-else :src="coverList[0] || ''" style="width:100%;height:420px;object-fit:cover;border-radius:10px;" />
            </el-col>
            <el-col :span="11">
                <h2 class="product-title">{{ product.name }}</h2>
                <div class="price-row">
                    <span class="price">¥{{ product.price }}</span>
                    <el-tag size="small" :type="levelType(product.oldLevel)" style="margin-left:10px;">{{ levelText(product.oldLevel) }}</el-tag>
                    <el-tag v-if="product.isBargain" type="warning" size="small" style="margin-left:6px;">可砍价</el-tag>
                </div>
                <div class="meta-row">
                    <span><i class="el-icon-box"></i> 库存 {{ product.inventory || 1 }} 件</span>
                    <span><i class="el-icon-time"></i> {{ timeAgo(product.createTime) }}</span>
                </div>

                <!-- 卖家信息卡片 -->
                <div class="seller-card">
                    <el-avatar :src="$imgUrl(sellerAvatar)" size="medium" style="margin-right:12px;cursor:pointer;" @click.native="goSellerProfile"></el-avatar>
                    <div class="seller-info" style="cursor:pointer;" @click="goSellerProfile">
                        <div class="seller-name">{{ sellerName }}
                            <i class="el-icon-right" style="font-size:12px;color:#bbb;"></i>
                        </div>
                        <div class="seller-hint">点击查看卖家主页</div>
                    </div>
                    <el-button size="small" type="primary" plain icon="el-icon-chat-dot-round" @click.stop="chatWithSeller">聊一聊</el-button>
                </div>

                <!-- 操作按钮 -->
                <div class="action-bar">
                    <el-button size="large" :type="isSaved?'warning':'default'" icon="el-icon-star-off"
                        @click="toggleSave" v-if="isLoggedIn" class="action-btn save-btn">
                        {{ isSaved ? '已收藏' : '收藏' }}
                    </el-button>
                    <el-button size="large" type="danger" icon="el-icon-shopping-cart-full"
                        @click="buyNow" v-if="isLoggedIn" class="action-btn buy-btn">
                        立即购买
                    </el-button>
                </div>

                <div v-if="!isLoggedIn" class="login-tip">
                    请先<a href="/login">登录</a>后再进行操作
                </div>
            </el-col>
        </el-row>

        <el-row style="margin-top:30px;">
            <el-tabs v-model="activeTab">
                <el-tab-pane label="商品描述" name="desc">
                    <div v-html="product.detail" class="desc-content"></div>
                </el-tab-pane>
                <el-tab-pane label="商品评论" name="comments">
                    <Evaluations :contentId="product.id" contentType="PRODUCT" />
                </el-tab-pane>
            </el-tabs>
        </el-row>

        <!-- 下单确认弹窗（闲鱼风格） -->
        <el-dialog :visible.sync="orderVisible" width="480px" :show-close="true" :close-on-click-modal="false"
            custom-class="order-dialog-custom">
            <div slot="title" class="dialog-title-wrap">
                <span class="dialog-title-icon">📦</span> 确认订单信息
            </div>
            <div class="order-dialog">
                <!-- 商品信息 -->
                <div class="od-section">
                    <div class="od-product">
                        <img :src="$imgUrl(getCover(product.coverList))" class="od-cover" />
                        <div class="od-pinfo">
                            <div class="od-pname">{{ product.name }}</div>
                            <div class="od-pprice">¥{{ product.price }}</div>
                        </div>
                    </div>
                </div>

                <!-- 收货信息 -->
                <div class="od-section">
                    <div class="od-section-title"><i class="el-icon-location-outline"></i> 收货信息</div>
                    <el-select v-if="addresses.length" v-model="selectedAddrId" placeholder="选择已保存地址" size="small"
                        style="width:100%;margin-bottom:8px;" @change="onAddrSelect" clearable>
                        <el-option v-for="a in addresses" :key="a.id" :label="a.name + ' - ' + a.address" :value="a.id">
                            <span>{{ a.name }}</span><span style="float:right;color:#999;font-size:12px;">{{ a.phone }}</span>
                        </el-option>
                    </el-select>
                    <el-input v-model="orderAddress" placeholder="请输入收货地址（如：XX大学XX宿舍楼XXX）"
                        size="small" class="od-input" maxlength="100"></el-input>
                    <el-input v-model="orderPhone" placeholder="联系电话" size="small" class="od-input" maxlength="20"
                        style="margin-top:8px;"></el-input>
                </div>

                <!-- 数量 -->
                <div class="od-section">
                    <div class="od-section-title"><i class="el-icon-s-data"></i> 购买数量</div>
                    <div class="od-qty-row">
                        <el-input-number v-model="orderQty" :min="1" :max="product.inventory || 1" size="small"></el-input-number>
                        <span class="od-stock">库存 {{ product.inventory || 1 }} 件</span>
                    </div>
                </div>

                <!-- 备注 -->
                <div class="od-section">
                    <div class="od-section-title"><i class="el-icon-edit-outline"></i> 订单备注</div>
                    <el-input v-model="orderRemark" placeholder="选填：颜色、尺码等要求" size="small" class="od-input" maxlength="100"></el-input>
                </div>

                <!-- 金额汇总 -->
                <div class="od-summary">
                    <div class="od-sum-row">
                        <span>商品总额</span><span>¥{{ product.price.toFixed ? product.price.toFixed(2) : product.price }}</span>
                    </div>
                    <div class="od-sum-row">
                        <span>运费</span><span style="color:#67C23A;">免运费</span>
                    </div>
                    <div class="od-sum-total">
                        <span>合计</span><span class="total-num">¥{{ (product.price * orderQty).toFixed(2) }}</span>
                    </div>
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="orderVisible = false" size="small">我再想想</el-button>
                <el-button type="danger" @click="submitOrder" :loading="submitting" size="small">提交订单</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import Evaluations from "@/components/Evaluations.vue";
import { getToken } from "@/utils/storage";
import { timeAgo } from "@/utils/data";
export default {
    name: 'ProductDetail',
    components: { Evaluations },
    data() {
        return {
            product: {}, coverList: [],
            sellerAvatar: '', sellerName: '', sellerId: null,
            isLoggedIn: false, isSaved: false, activeTab: 'desc',
            orderVisible: false, orderQty: 1,
            orderAddress: '', orderPhone: '', orderRemark: '',
            submitting: false, addresses: [], selectedAddrId: null,
            autoBuy: false,
        };
    },
    created() {
        this.isLoggedIn = !!getToken();
        this.autoBuy = this.$route.query.buy === '1';
        this.loadDetail();
    },
    watch: {
        'product.id'(val) {
            if (val && this.autoBuy) { this.autoBuy = false; this.$nextTick(() => this.buyNow()); }
        }
    },
    methods: {
        getCover(c) { return c ? c.split(',')[0] : null; },
        timeAgo,
        levelText(l) { return {1:'全新',2:'几乎全新',3:'有使用痕迹',4:'较旧'}[l]||''; },
        levelType(l) { return {1:'success',2:'',3:'warning',4:'danger'}[l]||''; },
        loadDetail() {
            const id = this.$route.params.id;
            this.$axios.get('/product/getById/' + id).then(res => {
                if (res.data.code === 200) {
                    this.product = res.data.data;
                    this.coverList = this.product.coverList ? this.product.coverList.split(',') : [];
                    this.sellerId = this.product.userId;
                    this.loadSeller(this.product.userId);
                    this.recordView();
                } else { this.$message.error('商品不存在'); }
            }).catch(() => { this.$message.error('加载失败'); });
        },
        loadSeller(userId) {
            this.$axios.get('/user/getById/' + userId).then(res => {
                if (res.data.code === 200) {
                    this.sellerName = res.data.data.userName;
                    this.sellerAvatar = res.data.data.userAvatar;
                }
            }).catch(() => {});
        },
        recordView() {
            if (!this.isLoggedIn) return;
            this.$axios.post('/interaction/recordView/' + this.product.id).catch(() => {});
        },
        goSellerProfile() {
            if (!this.sellerId) return;
            this.$router.push('/user/' + this.sellerId);
        },
        promptLogin(msg) {
            this.$confirm(msg || '该操作需要登录，是否前往登录？', '提示', {
                confirmButtonText: '去登录',
                cancelButtonText: '取消',
                type: 'warning',
            }).then(() => {
                this.$router.push({ path: '/login', query: { redirect: this.$route.fullPath } });
            }).catch(() => {});
        },
        chatWithSeller() {
            if (!this.isLoggedIn) { this.promptLogin('登录后才能与卖家聊天，是否前往登录？'); return; }
            if (!this.sellerId) return;
            const targetPath = '/chat/' + this.sellerId + '?pid=' + this.product.id;
            if (this.$router.currentRoute.fullPath.startsWith('/chat/' + this.sellerId)) {
                this.$router.replace(targetPath);
            } else {
                this.$router.push(targetPath);
            }
        },
        toggleSave() {
            this.$axios.post('/interaction/toggleSave/' + this.product.id).then(res => {
                if (res.data.code === 200) { this.isSaved = !this.isSaved; this.$message.success(res.data.msg); }
            });
        },
        buyNow() {
            if (!this.isLoggedIn) { this.promptLogin('登录后才能购买商品，是否前往登录？'); return; }
            if (!this.product.inventory || this.product.inventory <= 0) {
                this.$message.error('该商品已售罄'); return;
            }
            this.orderQty = 1; this.orderRemark = '';
            this.orderAddress = ''; this.orderPhone = ''; this.selectedAddrId = null;
            this.orderVisible = true;
            this.loadAddresses();
        },
        loadAddresses() {
            this.$axios.get('/address/list').then(res => {
                if (res.data.code === 200) {
                    this.addresses = res.data.data || [];
                    const def = this.addresses.find(a => a.isDefault);
                    if (def) { this.selectedAddrId = def.id; this.orderAddress = def.address; this.orderPhone = def.phone; }
                }
            }).catch(() => {});
        },
        onAddrSelect(id) {
            if (!id) { this.orderAddress = ''; this.orderPhone = ''; return; }
            const a = this.addresses.find(x => x.id === id);
            if (a) { this.orderAddress = a.address; this.orderPhone = a.phone; }
        },
        submitOrder() {
            this.submitting = true;
            if (!this.orderAddress) { this.$message.error('请输入收货地址'); this.submitting = false; return; }
            if (!this.orderPhone) { this.$message.error('请输入联系电话'); this.submitting = false; return; }
            this.$axios.post('/orders/create', {
                productId: this.product.id, detail: this.orderRemark,
                address: this.orderAddress, phone: this.orderPhone, remark: this.orderRemark,
            }).then(res => {
                if (res.data.code === 200) {
                    this.orderVisible = false;
                    this.$message.success('下单成功');
                    setTimeout(() => this.$router.push('/orders'), 800);
                } else { this.$message.error(res.data.msg); }
            }).catch(() => { this.$message.error('下单失败'); })
            .finally(() => { this.submitting = false; });
        },
    }
};
</script>

<style scoped>
.detail-container { background: #fff; padding: 24px; border-radius: 14px; }
.breadcrumb { margin-bottom: 18px; font-size: 13px; color: #999; }
.crumb-link { cursor: pointer; color: #666; }
.crumb-link:hover { color: #409EFF; }
.crumb-sep { margin: 0 6px; }
.crumb-current { color: #333; }

.product-title { font-size: 22px; color: #1a1a2e; margin: 0 0 16px; font-weight: 600; }
.price-row { margin-bottom: 14px; display: flex; align-items: center; }
.price { font-size: 32px; color: #f56c6c; font-weight: 700; }
.meta-row { display: flex; gap: 24px; margin-bottom: 18px; font-size: 13px; color: #909399; }
.meta-row i { margin-right: 4px; }

.seller-card {
    display: flex; align-items: center; padding: 16px; margin: 18px 0;
    background: #f8f9fb; border-radius: 14px; cursor: pointer; transition: all .2s;
}
.seller-card:hover { background: #f0f2f5; transform: translateY(-1px); }
.seller-info { flex: 1; }
.seller-name { font-size: 15px; color: #303133; font-weight: 500; }
.seller-hint { font-size: 12px; color: #bbb; margin-top: 2px; }

.action-bar { display: flex; gap: 12px; margin-top: 18px; }
.action-btn { flex: 1; border-radius: 12px; height: 46px; font-size: 16px; }
.save-btn { border: 1px solid #e0e0e0; }
.buy-btn { background: linear-gradient(135deg, #ff6b6b, #ee5a24); border: none; }
.buy-btn:hover { background: linear-gradient(135deg, #ee5a24, #d63031); }
.login-tip { margin-top: 18px; font-size: 14px; color: #999; text-align: center; }
.login-tip a { color: #409EFF; cursor: pointer; }

.desc-content { padding: 16px 0; line-height: 1.9; font-size: 14px; color: #333; }

/* 下单弹窗 */
.order-dialog { padding: 4px 0 0; }
.od-product {
    display: flex; align-items: center; gap: 14px;
    background: #fafafa; border-radius: 12px; padding: 16px;
}
.od-cover { width: 80px; height: 80px; object-fit: cover; border-radius: 10px; }
.od-pinfo { flex: 1; }
.od-pname { font-size: 15px; color: #303133; margin-bottom: 8px; font-weight: 500; }
.od-pprice { font-size: 20px; color: #f56c6c; font-weight: 700; }
.od-section {
    margin-top: 16px; padding: 16px; background: #fafafa; border-radius: 12px;
}
.od-section-title { font-size: 14px; color: #303133; margin-bottom: 12px; font-weight: 600; }
.od-input { width: 100%; margin-bottom: 8px; }
.od-qty-row { display: flex; align-items: center; gap: 14px; }
.od-stock { font-size: 12px; color: #999; }
.od-summary {
    margin-top: 16px; padding: 16px; background: #fff7f7; border-radius: 12px;
    border: 1px solid #ffe0e0;
}
.od-sum-row { display: flex; justify-content: space-between; font-size: 14px; color: #666; margin-bottom: 8px; }
.od-sum-total {
    display: flex; justify-content: space-between; font-size: 15px; color: #303133; font-weight: 600;
    margin-top: 12px; padding-top: 12px; border-top: 1px dashed #ffcccc;
}
.total-num { font-size: 26px; color: #f56c6c; font-weight: 700; }

.dialog-title-wrap { font-size: 17px; font-weight: 600; color: #303133; }
.dialog-title-icon { margin-right: 8px; }
</style>

<style>
.order-dialog-custom { border-radius: 16px !important; overflow: hidden; }
.order-dialog-custom .el-dialog__header { padding: 24px 28px 8px; }
.order-dialog-custom .el-dialog__body { padding: 16px 28px; }
.order-dialog-custom .el-dialog__footer { padding: 16px 28px 24px; }
.order-dialog-custom .el-button { border-radius: 10px; padding: 10px 24px; }
</style>
