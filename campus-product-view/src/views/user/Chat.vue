<template>
    <div class="chat-page">
        <div class="breadcrumb">
            <span @click="$router.replace('/message')" class="crumb-link">消息</span>
            <span class="crumb-sep">/</span>
            <span class="crumb-current crumb-link" @click="$router.push('/user/' + $route.params.userId)">{{ targetName }}</span>
        </div>

        <div class="chat-body">
            <!-- 商品卡片 -->
            <div class="chat-product-card" v-if="product.id" @click="goProduct">
                <img :src="$imgUrl(getCover(product.coverList))" class="cpc-cover" />
                <div class="cpc-info">
                    <div class="cpc-name">{{ product.name }}</div>
                    <div class="cpc-price">¥{{ product.price }}
                        <span v-if="product.inventory <= 0" style="color:#999;font-size:12px;">已售罄</span>
                    </div>
                </div>
                <template v-if="isBuyer()">
                    <el-button size="small" type="danger" @click.stop="showOrderDialog" class="cpc-buy"
                        :disabled="!product.inventory || product.inventory <= 0">
                        {{ (product.inventory && product.inventory > 0) ? '立即购买' : '已售罄' }}
                    </el-button>
                </template>
                <template v-else>
                    <el-button size="small" type="primary" @click.stop="goProduct" class="cpc-buy">
                        编辑商品
                    </el-button>
                </template>
            </div>

            <div class="chat-messages" ref="chatBox">
                <template v-for="(item, i) in displayItems">
                    <!-- 时间戳分割线 -->
                    <div v-if="item.type === 'time'" :key="'time-' + i" class="time-divider">
                        <span class="time-label">{{ item.label }}</span>
                    </div>

                    <!-- 订单状态卡片 -->
                    <div v-else-if="item.type === 'order'" :key="'order-' + (item.msg.id || i)" class="order-card-msg">
                        <div class="order-card" :class="[(item.msg.senderId == myId ? 'oc-sent' : 'oc-received'), item.stale ? 'oc-stale' : '']">
                            <div class="oc-header">{{ item.msg.content }}</div>
                            <div class="oc-body">
                                <img :src="$imgUrl(getCover(product.coverList || item.msg.coverList))" class="oc-pic" />
                                <div class="oc-info">
                                    <div class="oc-pname">{{ item.msg.productName || product.name }}</div>
                                    <div class="oc-price">¥{{ item.msg.orderPrice }}</div>
                                    <div class="oc-addr" v-if="item.msg.address"><i class="el-icon-location"></i> {{ item.msg.address }}</div>
                                    <div class="oc-addr" v-if="item.msg.phone"><i class="el-icon-phone"></i> {{ item.msg.phone }}</div>
                                </div>
                            </div>
                            <div class="oc-status">
                                <el-tag v-if="item.stale" type="info" size="small">已过期</el-tag>
                                <el-tag v-else :type="getOrderStatusType(item.msg)" size="small">
                                    {{ getOrderStatusText(item.msg) }}
                                </el-tag>
                                <span class="oc-total">合计 ¥{{ item.msg.orderPrice }}</span>
                            </div>
                            <div class="oc-actions" v-if="!item.stale && canOperate(item.msg)">
                                <!-- status=0: 待支付 -->
                                <el-button size="mini" type="primary" v-if="isBuyer() && item.msg.orderStatus == 0"
                                    @click="payOrder(item.msg)">确认付款</el-button>
                                <el-button size="mini" type="danger" v-if="isBuyer() && item.msg.orderStatus == 0"
                                    @click="cancelOrder(item.msg)">取消订单</el-button>
                                <el-button size="mini" type="warning" v-if="isSeller() && item.msg.orderStatus == 0"
                                    @click="showModifyPrice(item.msg)">修改价格</el-button>
                                <el-button size="mini" type="danger" v-if="isSeller() && item.msg.orderStatus == 0"
                                    @click="cancelOrder(item.msg)">取消订单</el-button>
                                <!-- status=1: 已支付/待发货 -->
                                <el-button size="mini" type="success" v-if="isSeller() && item.msg.orderStatus == 1"
                                    @click="shipOrder(item.msg)">确认发货</el-button>
                                <span v-if="isBuyer() && item.msg.orderStatus == 1" class="oc-waiting-text">等待卖家发货</span>
                                <!-- status=2: 已发货/待收货 -->
                                <el-button size="mini" type="success" v-if="isBuyer() && item.msg.orderStatus == 2"
                                    @click="receiveOrder(item.msg)">确认收货</el-button>
                                <span v-if="isSeller() && item.msg.orderStatus == 2" class="oc-waiting-text">等待买家确认收货</span>
                                <!-- status=3: 已收货/待评价 -->
                                <span v-if="item.msg.orderStatus == 3" class="oc-waiting-text">等待评价</span>
                            </div>
                        </div>
                    </div>

                    <!-- 文字消息 -->
                    <div v-else :key="'text-' + (item.msg.id || i)" :class="['msg-row', item.msg.senderId === myId ? 'msg-mine' : 'msg-other']">
                        <img :src="$imgUrl(item.msg.senderId === myId ? myAvatar : targetAvatar)" class="msg-avatar" />
                        <div class="msg-content-wrap">
                            <div class="msg-bubble">{{ item.msg.content }}</div>
                            <div :class="['msg-time-label', item.msg.senderId === myId ? 'msg-time-right' : 'msg-time-left']">
                                {{ formatTime(item.msg.createTime) }}
                            </div>
                        </div>
                    </div>
                </template>
                <div v-if="!messages.length && !loadingMsg" class="empty-state">暂无消息，开始聊天吧</div>
            </div>
        </div>

        <div class="chat-input-bar">
            <el-input v-model="text" placeholder="输入消息..." @keyup.enter.native="sendMsg" size="small" class="ci-input">
                <el-button slot="append" @click="sendMsg" icon="el-icon-s-promotion"></el-button>
            </el-input>
        </div>

        <!-- 内联下单弹窗 -->
        <el-dialog :visible.sync="orderVisible" width="460px" :show-close="true" :close-on-click-modal="false"
            custom-class="order-dialog-custom">
            <div slot="title" class="dialog-title-wrap">确认订单</div>
            <div class="od-product">
                <img :src="$imgUrl(getCover(product.coverList))" class="od-cover" />
                <div class="od-pinfo">
                    <div class="od-pname">{{ product.name }}</div>
                    <div class="od-pprice">¥{{ product.price }}</div>
                </div>
            </div>
            <div class="od-section">
                <div class="od-section-title">收货信息</div>
                <el-select v-if="addresses.length" v-model="selectedAddrId" placeholder="选择已保存地址" size="small"
                    style="width:100%;margin-bottom:8px;" @change="onAddrSelect" clearable>
                    <el-option v-for="a in addresses" :key="a.id" :label="a.name + ' - ' + a.address" :value="a.id">
                        <span>{{ a.name }}</span><span style="float:right;color:#999;font-size:12px;">{{ a.phone }}</span>
                    </el-option>
                </el-select>
                <el-input v-model="orderAddress" placeholder="收货地址" size="small" class="od-input" maxlength="100"></el-input>
                <el-input v-model="orderPhone" placeholder="联系电话" size="small" class="od-input" maxlength="20" style="margin-top:6px;"></el-input>
            </div>
            <div class="od-section">
                <div class="od-section-title">备注</div>
                <el-input v-model="orderRemark" placeholder="给卖家留言（选填）" size="small" maxlength="100"></el-input>
            </div>
            <div class="od-summary">
                <div class="od-sum-total">应付 <span class="total-num">¥{{ product.price }}</span></div>
            </div>
            <span slot="footer">
                <el-button @click="orderVisible = false" size="small">取消</el-button>
                <el-button type="danger" @click="submitOrder" :loading="submitting" size="small">提交订单</el-button>
            </span>
        </el-dialog>

        <!-- 改价弹窗 -->
        <el-dialog :visible.sync="modifyVisible" width="400px" :show-close="true" :close-on-click-modal="false"
            custom-class="order-dialog-custom">
            <div slot="title" class="dialog-title-wrap">修改订单</div>
            <div style="padding:8px 0;">
                <div style="margin-bottom:14px;">商品：{{ product.name }}</div>
                <div style="margin-bottom:14px;">
                    <span style="font-size:13px;color:#666;">修改价格</span>
                    <el-input-number v-model="modifyPrice" :min="0.01" :precision="2" size="small" style="margin-left:10px;"></el-input-number>
                </div>
                <div style="margin-bottom:14px;">
                    <span style="font-size:13px;color:#666;">留言</span>
                    <el-input v-model="modifyNote" placeholder="给买家留言" size="small" maxlength="100" style="margin-top:6px;"></el-input>
                </div>
            </div>
            <span slot="footer">
                <el-button @click="modifyVisible = false" size="small">取消</el-button>
                <el-button type="warning" @click="submitModify" :loading="modifying" size="small">确认修改</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import { getUserInfo } from "@/utils/storage";
export default {
    name: 'Chat',
    data() {
        return {
            messages: [], text: '', myId: null, loadingMsg: false,
            targetName: '', timer: null, product: {},
            myAvatar: '', targetAvatar: '',
            orderVisible: false, orderAddress: '', orderPhone: '', orderRemark: '', submitting: false,
            addresses: [], selectedAddrId: null,
            modifyVisible: false, modifyPrice: 0, modifyNote: '', modifying: false,
            modifyingMsg: null, cachedOrder: null,
        };
    },
    computed: {
        displayItems() {
            const items = [];
            let lastTime = null;
            const TIME_GAP = 5 * 60 * 1000;
            // 收集每个 orderId 最后一次出现的索引
            const lastOrderIndex = {};
            this.messages.forEach((msg, i) => {
                if (msg.isOrderCard && msg.orderId) lastOrderIndex[msg.orderId] = i;
            });
            for (let i = 0; i < this.messages.length; i++) {
                const msg = this.messages[i];
                const msgTime = this.parseTime(msg.createTime);
                if (!lastTime || (msgTime - lastTime > TIME_GAP)) {
                    items.push({ type: 'time', label: this.formatDividerTime(msg.createTime) });
                }
                lastTime = msgTime;
                if (msg.isOrderCard) {
                    const isStale = msg.orderId && lastOrderIndex[msg.orderId] !== i;
                    items.push({ type: 'order', msg, stale: isStale });
                } else {
                    items.push({ type: 'text', msg });
                }
            }
            return items;
        }
    },
    created() {
        const ui = getUserInfo();
        this.myId = ui ? ui.id : null;
        this.myAvatar = ui ? (ui.userAvatar || ui.avatar || '') : '';
        this.targetName = '聊天';
        this.loadProduct();
        this.loadMessages();
        this.startPolling();
        document.addEventListener('visibilitychange', this.onVisibility);
    },
    beforeDestroy() { clearInterval(this.timer); document.removeEventListener('visibilitychange', this.onVisibility); },
    watch: {
        '$route.query.pid'() { this.cachedOrder = null; this.loadProduct(); this.loadMessages(); },
        '$route.params.userId'() { this.cachedOrder = null; this.loadProduct(); this.loadMessages(); }
    },
    methods: {
        startPolling() { clearInterval(this.timer); this.timer = setInterval(() => this.loadMessages(), 3000); },
        onVisibility() {
            if (document.hidden) { clearInterval(this.timer); }
            else { this.loadMessages(); this.startPolling(); }
        },
        getCover(c) { return c ? c.split(',')[0] : null; },
        parseTime(t) {
            if (!t) return 0;
            if (typeof t === 'number') return t;
            return new Date(String(t).replace(/-/g, '/')).getTime();
        },
        formatTime(t) {
            if (!t) return '';
            const d = new Date(String(t).replace(/-/g, '/'));
            return String(d.getHours()).padStart(2, '0') + ':' + String(d.getMinutes()).padStart(2, '0');
        },
        formatDividerTime(t) {
            if (!t) return '';
            const d = new Date(String(t).replace(/-/g, '/'));
            const now = new Date();
            const hh = String(d.getHours()).padStart(2, '0');
            const mm = String(d.getMinutes()).padStart(2, '0');
            if (d.toDateString() === now.toDateString()) return '今天 ' + hh + ':' + mm;
            const yesterday = new Date(now); yesterday.setDate(now.getDate() - 1);
            if (d.toDateString() === yesterday.toDateString()) return '昨天 ' + hh + ':' + mm;
            return (d.getMonth() + 1) + '月' + d.getDate() + '日 ' + hh + ':' + mm;
        },

        loadProduct() {
            const pid = this.$route.query.pid;
            if (!pid) return;
            this.$axios.get('/product/getById/' + pid).then(res => {
                if (res.data.code === 200) {
                    this.product = res.data.data;
                    const otherId = this.product.userId == this.myId ? this.$route.params.userId : this.product.userId;
                    this.$axios.get('/user/getById/' + otherId).then(r2 => {
                        if (r2.data.code === 200) {
                            this.targetName = r2.data.data.userName;
                            this.targetAvatar = r2.data.data.userAvatar || r2.data.data.avatar || '';
                        }
                    });
                }
            }).catch(() => {});
        },
        goProduct() { this.$router.push('/product/detail/' + this.product.id); },

        getOrderStatusType(msg) {
            const map = { 0: 'warning', 1: 'primary', 2: '', 3: '', 4: 'success', 5: 'info' };
            return map[msg.orderStatus] || 'info';
        },
        getOrderStatusText(msg) {
            const map = { 0: '待支付', 1: '已支付', 2: '已发货', 3: '已收货', 4: '交易完成', 5: '已取消' };
            return map[msg.orderStatus] || '待支付';
        },
        isBuyer() { return this.product.userId != this.myId; },
        isSeller() { return this.product.userId == this.myId; },
        buildCardMsg(orderStatus, msg, content, extra) {
            return JSON.stringify({
                isOrderCard: true, orderStatus, orderId: msg.orderId,
                address: msg.address, phone: msg.phone,
                productName: this.product.name, orderPrice: msg.orderPrice,
                coverList: this.product.coverList, content, ...extra
            });
        },
        sendCardMsg(cardMsg, cb) {
            const targetId = parseInt(this.$route.params.userId);
            const pid = parseInt(this.$route.query.pid) || 0;
            this.$axios.post('/message/send', { receiverId: targetId, productId: pid, content: cardMsg }).then(() => {
                if (cb) cb();
            });
        },
        canOperate(msg) {
            if ([4, 5].includes(msg.orderStatus)) return false;
            return this.isBuyer() || this.isSeller();
        },

        findRealOrder() {
            const pid = parseInt(this.$route.query.pid) || 0;
            return this.$axios.get('/orders/findByProduct/' + pid).then(r => {
                if (r.data.code === 200) { this.cachedOrder = r.data.data; return r.data.data; }
                this.cachedOrder = null; return null;
            }).catch(() => { this.cachedOrder = null; return null; });
        },

        showOrderDialog() {
            this.orderAddress = ''; this.orderPhone = ''; this.orderRemark = ''; this.selectedAddrId = null; this.orderVisible = true;
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
            if (!this.orderAddress) { this.$message.error('请输入收货地址'); return; }
            if (!this.orderPhone) { this.$message.error('请输入联系电话'); return; }
            this.submitting = true;
            this.$axios.post('/orders/create', {
                productId: this.product.id, detail: this.orderRemark,
                address: this.orderAddress, phone: this.orderPhone, remark: this.orderRemark,
            }).then(res => {
                if (res.data.code === 200) {
                    this.orderVisible = false;
                    this.findRealOrder().then(order => {
                        const fakeMsg = { orderId: order ? order.id : null, address: this.orderAddress, phone: this.orderPhone, orderPrice: this.product.price };
                        this.sendCardMsg(this.buildCardMsg(0, fakeMsg, '新订单已创建，等待买家付款'), () => this.loadMessages());
                    });
                    this.$message.success('下单成功');
                    this.loadProduct();
                } else { this.$message.error(res.data.msg); }
            }).catch(() => { this.$message.error('下单失败'); })
            .finally(() => { this.submitting = false; });
        },

        payOrder(msg) {
            if (!msg.orderId) { this.$message.warning('订单信息不完整'); return; }
            this.$confirm('确认支付 ¥' + msg.orderPrice + ' 吗？付款后将通知卖家发货。', '安全确认', {
                confirmButtonText: '确认付款', cancelButtonText: '再想想', type: 'warning',
            }).then(() => {
                this.$axios.put('/orders/pay', { id: msg.orderId }).then(res => {
                    if (res.data.code === 200) {
                        this.$message.success('付款成功');
                        this.sendCardMsg(this.buildCardMsg(1, msg, '买家已付款，请尽快发货'), () => { this.loadMessages(); this.loadProduct(); });
                    } else { this.$message.error(res.data.msg); }
                });
            }).catch(() => {});
        },

        shipOrder(msg) {
            if (!msg.orderId) { this.$message.warning('订单信息不完整'); return; }
            this.$confirm('确认已发货？', '确认发货', { type: 'info' }).then(() => {
                this.$axios.put('/orders/ship', { id: msg.orderId }).then(res => {
                    if (res.data.code === 200) {
                        this.$message.success('已确认发货');
                        this.sendCardMsg(this.buildCardMsg(2, msg, '卖家已发货，请注意查收'), () => this.loadMessages());
                    } else { this.$message.error(res.data.msg); }
                });
            }).catch(() => {});
        },

        receiveOrder(msg) {
            if (!msg.orderId) { this.$message.warning('订单信息不完整'); return; }
            this.$confirm('确认已收到商品？', '确认收货', { type: 'success' }).then(() => {
                this.$axios.put('/orders/receive', { id: msg.orderId }).then(res => {
                    if (res.data.code === 200) {
                        this.$message.success('已确认收货');
                        this.sendCardMsg(this.buildCardMsg(3, msg, '买家已确认收货，等待评价'), () => this.loadMessages());
                    } else { this.$message.error(res.data.msg); }
                });
            }).catch(() => {});
        },

        showModifyPrice(msg) {
            this.modifyingMsg = msg;
            this.modifyPrice = parseFloat(msg.orderPrice) || this.product.price || 0;
            this.modifyNote = ''; this.modifyVisible = true;
        },
        submitModify() {
            if (!this.modifyingMsg || !this.modifyingMsg.orderId) { this.$message.warning('订单信息不完整'); return; }
            this.modifying = true;
            const msg = this.modifyingMsg;
            this.$axios.put('/orders/updatePrice', { id: msg.orderId, price: this.modifyPrice }).then(res => {
                if (res.data.code === 200) {
                    this.modifyVisible = false; this.$message.success('订单已修改');
                    const content = '卖家已修改订单，新价格 ¥' + this.modifyPrice + (this.modifyNote ? '，留言：' + this.modifyNote : '');
                    this.sendCardMsg(this.buildCardMsg(0, { ...msg, orderPrice: this.modifyPrice }, content), () => { this.loadMessages(); this.loadProduct(); });
                } else { this.$message.error(res.data.msg); }
            }).finally(() => { this.modifying = false; });
        },

        cancelOrder(msg) {
            if (!msg.orderId) { this.$message.warning('订单信息不完整'); return; }
            this.$confirm('确定要取消此订单吗？', '提示', { type: 'warning' }).then(() => {
                this.$axios.put('/orders/cancel', { id: msg.orderId }).then(res => {
                    if (res.data.code === 200) {
                        this.$message.success('订单已取消');
                        const who = this.isBuyer() ? '买家' : '卖家';
                        this.sendCardMsg(this.buildCardMsg(5, msg, who + '已取消订单'), () => { this.loadMessages(); this.loadProduct(); });
                    } else { this.$message.error(res.data.msg); }
                });
            }).catch(() => {});
        },

        loadMessages() {
            const targetId = this.$route.params.userId;
            const pid = this.$route.query.pid || 0;
            this.$axios.get('/message/chat/' + targetId + '?productId=' + pid).then(res => {
                if (res.data.code === 200) {
                    const newMsgs = res.data.data.map(m => {
                        try { const c = JSON.parse(m.content); if (c.isOrderCard) return { ...m, ...c }; } catch(e){}
                        return m;
                    });
                    if (newMsgs.length !== this.messages.length ||
                        (newMsgs.length > 0 && this.messages.length > 0 &&
                         newMsgs[newMsgs.length - 1].id !== this.messages[this.messages.length - 1].id)) {
                        this.messages = newMsgs;
                        this.$nextTick(() => { const box = this.$refs.chatBox; if (box) box.scrollTop = box.scrollHeight; });
                    }
                    this.syncOrderStatus();
                    this.$axios.put('/message/read/' + targetId + '?productId=' + pid);
                }
            }).catch(() => {});
        },

        syncOrderStatus() {
            this.$axios.post('/orders/myOrders', { current: 1, size: 100 }).then(r => {
                const realOrders = Array.isArray(r.data.data) ? r.data.data : [];
                let changed = false;
                this.messages.forEach(m => {
                    if (!m.isOrderCard || !m.orderId) return;
                    const real = realOrders.find(o => o.id == m.orderId);
                    if (real) {
                        const ns = real.status != null ? real.status : ((real.tradeStatus === true || real.tradeStatus === 1) ? 1 : 0);
                        if (m.orderStatus !== ns) { m.orderStatus = ns; changed = true; }
                        if (real.price && m.orderPrice != real.price) { m.orderPrice = real.price; changed = true; }
                    }
                });
                if (changed) this.$forceUpdate();
            }).catch(() => {});
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
        sendMsg() {
            if (!this.text.trim()) return;
            if (!this.myId) { this.promptLogin('登录后才能发送消息'); return; }
            const targetId = parseInt(this.$route.params.userId);
            const pid = parseInt(this.$route.query.pid) || 0;
            const content = this.text;
            this.text = '';
            this.messages.push({
                id: Date.now(), senderId: this.myId, receiverId: targetId,
                content, senderAvatar: this.myAvatar, isRead: false, createTime: this.formatNow()
            });
            this.$nextTick(() => { const box = this.$refs.chatBox; if (box) box.scrollTop = box.scrollHeight; });
            this.$axios.post('/message/send', { receiverId: targetId, productId: pid, content }).then(res => {
                if (res.data.code === 200) { setTimeout(() => this.loadMessages(), 500); }
            });
        },
        formatNow() {
            const d = new Date();
            return d.getFullYear() + '-' + String(d.getMonth()+1).padStart(2,'0') + '-' + String(d.getDate()).padStart(2,'0') + ' ' +
                   String(d.getHours()).padStart(2,'0') + ':' + String(d.getMinutes()).padStart(2,'0') + ':' + String(d.getSeconds()).padStart(2,'0');
        }
    }
};
</script>

<style scoped lang="scss">
.chat-page { background: #fff; border-radius: 12px; display: flex; flex-direction: column; height: calc(100vh - 140px); position: relative; }
.breadcrumb { padding: 15px 20px 10px; font-size: 13px; color: #999; border-bottom: 1px solid #f0f0f0; flex-shrink: 0; }
.crumb-link { cursor: pointer; color: #666; }
.crumb-link:hover { color: #409EFF; }
.crumb-sep { margin: 0 6px; }
.crumb-current { color: #333; }

.chat-body { flex: 1; display: flex; flex-direction: column; overflow: hidden; min-height: 0; }

.chat-product-card {
    display: flex; align-items: center; padding: 12px 20px; gap: 12px; flex-shrink: 0;
    background: #fffbe6; border-bottom: 1px solid #f5e6b8; cursor: pointer;
}
.chat-product-card:hover { background: #fff8cc; }
.cpc-cover { width: 56px; height: 56px; object-fit: cover; border-radius: 8px; }
.cpc-info { flex: 1; }
.cpc-name { font-size: 14px; color: #333; margin-bottom: 4px; }
.cpc-price { font-size: 18px; color: #f56c6c; font-weight: bold; }
.cpc-buy { flex-shrink: 0; }

.chat-messages { flex: 1; overflow-y: auto; padding: 15px 20px; }

.time-divider { text-align: center; margin: 16px 0 12px; }
.time-label {
    display: inline-block; padding: 3px 12px; background: #e8e8e8; border-radius: 4px;
    font-size: 11px; color: #999; line-height: 1.4;
}

.msg-row { display: flex; align-items: flex-start; margin-bottom: 6px; }
.msg-mine { flex-direction: row-reverse; }
.msg-avatar { width: 36px; height: 36px; border-radius: 50%; margin: 0 8px; flex-shrink: 0; object-fit: cover; }
.msg-content-wrap { max-width: 65%; }
.msg-bubble { padding: 10px 14px; border-radius: 16px; font-size: 14px; line-height: 1.6; word-break: break-word; }
.msg-mine .msg-bubble { background: #409EFF; color: #fff; border-bottom-right-radius: 4px; }
.msg-other .msg-bubble { background: #f0f0f0; color: #333; border-bottom-left-radius: 4px; }
.msg-time-label { font-size: 10px; color: #ccc; margin-top: 2px; }
.msg-time-right { text-align: right; margin-right: 4px; }
.msg-time-left { text-align: left; margin-left: 4px; }
.empty-state { text-align: center; padding: 60px 0; color: #ccc; font-size: 14px; }

.order-card-msg { display: flex; justify-content: center; margin: 12px 0; }
.order-card {
    width: 340px; background: #fff; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,.08);
    overflow: hidden; text-align: left;
}
.oc-header { padding: 10px 14px; font-size: 13px; font-weight: 600; color: #303133; background: #f8f9fb; }
.oc-body { display: flex; gap: 12px; padding: 12px 14px; }
.oc-pic { width: 64px; height: 64px; object-fit: cover; border-radius: 8px; }
.oc-info { flex: 1; }
.oc-pname { font-size: 14px; color: #333; margin-bottom: 4px; }
.oc-price { font-size: 16px; color: #f56c6c; font-weight: bold; }
.oc-addr { font-size: 11px; color: #999; margin-top: 3px; }
.oc-status { display: flex; justify-content: space-between; align-items: center; padding: 10px 14px; border-top: 1px solid #f5f5f5; }
.oc-total { font-size: 16px; color: #f56c6c; font-weight: 700; }
.oc-actions { padding: 8px 14px 12px; text-align: right; border-top: 1px solid #f5f5f5; display: flex; gap: 6px; justify-content: flex-end; }
.oc-sent { border: 1px solid #d0e4ff; }
.oc-received { border: 1px solid #ffe0c0; }
.oc-stale { opacity: 0.5; filter: grayscale(60%); pointer-events: none; }
.oc-waiting-text { font-size: 12px; color: #E6A23C; }

.chat-input-bar { padding: 10px 20px; border-top: 1px solid #f0f0f0; background: #fff; flex-shrink: 0; }
.ci-input { width: 100%; }

.od-product { display: flex; align-items: center; gap: 14px; background: #fafafa; border-radius: 12px; padding: 16px; margin-bottom: 14px; }
.od-cover { width: 72px; height: 72px; object-fit: cover; border-radius: 10px; }
.od-pinfo { flex: 1; }
.od-pname { font-size: 15px; color: #303133; margin-bottom: 6px; font-weight: 500; }
.od-pprice { font-size: 20px; color: #f56c6c; font-weight: 700; }
.od-section { padding: 14px; background: #fafafa; border-radius: 10px; margin-bottom: 12px; }
.od-section-title { font-size: 13px; color: #666; margin-bottom: 10px; font-weight: 600; }
.od-input { width: 100%; }
.od-summary { text-align: right; padding: 14px; background: #fff7f7; border-radius: 10px; border: 1px solid #ffe0e0; }
.od-sum-total { font-size: 14px; color: #666; }
.total-num { font-size: 24px; color: #f56c6c; font-weight: 700; margin-left: 8px; }
.dialog-title-wrap { font-size: 16px; font-weight: 600; }
</style>
