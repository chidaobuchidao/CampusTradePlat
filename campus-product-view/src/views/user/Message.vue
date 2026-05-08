<template>
    <div class="message-page">
        <h3 style="margin-bottom:15px;">消息</h3>
        <div v-if="conversations.length" class="conv-list">
            <div class="conv-item" v-for="c in conversations" :key="c.uid + '_' + c.productId" @click="openChat(c)">
                <div class="conv-left">
                    <img :src="$imgUrl(c.userAvatar)" class="conv-avatar" @click.stop="$router.push('/user/' + c.uid)" />
                    <span v-if="c.unread > 0" class="unread-dot"></span>
                </div>
                <div class="conv-center">
                    <div class="conv-header">
                        <span class="conv-name" @click.stop="$router.push('/user/' + c.uid)">{{ c.userName }}</span>
                        <span class="conv-time">{{ timeAgo(c.lastTime) }}</span>
                    </div>
                    <div class="conv-product" v-if="c.productName">
                        <img :src="$imgUrl(getCover(c.productCover))" class="conv-pcover" />
                        <span class="conv-pname">{{ c.productName }}</span>
                        <span class="conv-pprice" v-if="c.productPrice">¥{{ c.productPrice }}</span>
                    </div>
                    <div class="conv-footer">
                        <span class="conv-msg">{{ formatLastMsg(c.lastMsg) }}</span>
                        <span v-if="c.unread > 0" class="unread-badge">{{ c.unread > 99 ? '99+' : c.unread }}</span>
                    </div>
                </div>
            </div>
        </div>
        <div v-else class="empty-state">
            <i class="el-icon-message" style="font-size:48px;color:#ddd;"></i>
            <p>暂无消息</p>
        </div>
    </div>
</template>

<script>
import { timeAgo } from "@/utils/data";
export default {
    name: 'Message',
    data() {
        return { conversations: [], timer: null };
    },
    created() {
        this.loadList();
        this.timer = setInterval(() => this.loadList(), 5000);
        document.addEventListener('visibilitychange', this.onVis);
    },
    activated() { this.loadList(); },
    beforeDestroy() {
        clearInterval(this.timer);
        document.removeEventListener('visibilitychange', this.onVis);
    },
    methods: {
        timeAgo,
        onVis() {
            if (document.hidden) { clearInterval(this.timer); }
            else { this.loadList(); this.timer = setInterval(() => this.loadList(), 5000); }
        },
        getCover(c) { return c ? c.split(',')[0] : null; },
        formatLastMsg(msg) {
            if (!msg) return '';
            // 如果是 JSON 订单卡片，显示摘要
            try { const j = JSON.parse(msg); if (j.isOrderCard) return '[订单] ' + (j.content || ''); } catch(e){}
            return msg;
        },
        loadList() {
            this.$axios.get('/message/conversations').then(res => {
                if (res.data.code === 200) this.conversations = res.data.data;
            }).catch(() => {});
        },
        openChat(c) {
            this.$router.push({ path: '/chat/' + c.uid, query: { pid: c.productId } });
        },
    }
};
</script>

<style scoped lang="scss">
.message-page { background: #fff; border-radius: 12px; padding: 20px; min-height: 400px; }
.conv-list { margin-top: 8px; }
.conv-item {
    display: flex; align-items: flex-start; padding: 14px 12px; border-bottom: 1px solid #f5f5f5;
    cursor: pointer; transition: background .15s; border-radius: 8px;
}
.conv-item:hover { background: #f8f9fb; }
.conv-left { position: relative; margin-right: 12px; flex-shrink: 0; }
.conv-avatar { width: 48px; height: 48px; border-radius: 50%; object-fit: cover; cursor: pointer; }
.conv-avatar:hover { opacity: 0.8; }
.unread-dot {
    position: absolute; top: 0; right: 0; width: 10px; height: 10px;
    background: #f56c6c; border-radius: 50%; border: 2px solid #fff;
}
.conv-center { flex: 1; min-width: 0; }
.conv-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px; }
.conv-name { font-size: 15px; color: #303133; font-weight: 500; cursor: pointer; }
.conv-name:hover { color: #409EFF; }
.conv-time { font-size: 11px; color: #bbb; flex-shrink: 0; margin-left: 8px; }
.conv-product {
    display: flex; align-items: center; gap: 6px; padding: 4px 8px;
    background: #f8f9fb; border-radius: 6px; margin-bottom: 6px;
}
.conv-pcover { width: 24px; height: 24px; border-radius: 4px; object-fit: cover; }
.conv-pname { font-size: 12px; color: #666; flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.conv-pprice { font-size: 12px; color: #f56c6c; font-weight: 600; flex-shrink: 0; }
.conv-footer { display: flex; justify-content: space-between; align-items: center; }
.conv-msg { font-size: 13px; color: #999; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 260px; }
.unread-badge {
    background: #f56c6c; color: #fff; font-size: 11px; padding: 2px 7px;
    border-radius: 10px; min-width: 18px; text-align: center; flex-shrink: 0;
}
.empty-state { text-align: center; padding: 80px 0; color: #999; }
.empty-state p { margin-top: 12px; font-size: 14px; }
</style>
