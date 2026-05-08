<template>
    <div>
        <div class="container">
            <div class="top">
                <div class="nav">
                    <div>
                        <Logo name="校园交易" />
                    </div>
                    <div class="route">
                        <span :class="{ active: isActive('/product') }" @click="handleRouteSelect('/product')">商品</span>
                        <span :class="{ active: isActive('/forum') }" @click="handleRouteSelect('/forum')">论坛</span>
                        <span v-if="loginStatus" :class="{ active: isActive('/myProduct') }" @click="handleRouteSelect('/myProduct')">我的商品</span>
                        <span v-if="loginStatus" :class="{ active: isActive('/mySave') }" @click="handleRouteSelect('/mySave')">我的收藏</span>
                        <span v-if="loginStatus" :class="{ active: isActive('/myView') }" @click="handleRouteSelect('/myView')">足迹</span>
                    </div>
                </div>
                <div class="right-nav">
                    <div class="word-search" style="position:relative;">
                        <div class="item">
                            <input type="text" placeholder="搜索商品" v-model="key"
                                @keyup.enter="fetch" @focus="showHistory = true"
                                @blur="hideHistory">
                            <i class="el-icon-search" @click="fetch"></i>
                        </div>
                        <div class="search-history" v-if="showHistory && historyList.length">
                            <div class="history-item" v-for="(h, i) in historyList" :key="i"
                                @mousedown.prevent="searchHistoryItem(h)">
                                <i class="el-icon-time"></i> {{ h }}
                            </div>
                            <div class="history-clear" @mousedown.prevent="clearHistory">
                                清除历史
                            </div>
                        </div>
                    </div>
                    <div class="icon-btn" v-if="loginStatus" @click="handleRouteSelect('/orders')">
                        <i class="el-icon-document"></i>
                        <span>订单</span>
                    </div>
                    <div class="icon-btn" v-if="loginStatus" @click="handleRouteSelect('/message')" style="position:relative;">
                        <i class="el-icon-bell"></i>
                        <span v-if="unreadCount > 0" class="unread-dot">{{ unreadCount > 99 ? '99+' : unreadCount }}</span>
                        <span>通知</span>
                    </div>
                    <div class="icon-btn" v-if="loginStatus" @click="handleRouteSelect('/post-product')">
                        <i class="el-icon-plus"></i>
                        <span>发布</span>
                    </div>
                    <div class="icon-btn" v-if="isAdmin" @click="$router.push('/adminLayout')">
                        <i class="el-icon-s-tools"></i>
                        <span>管理</span>
                    </div>
                    <div v-if="!loginStatus" class="login-link" @click="loginOperation">
                        登录&nbsp;|&nbsp;注册
                    </div>
                    <div v-else>
                        <img @click="handleRouteSelect('/myself')" class="avatar" :src="$imgUrl(userInfo.userAvatar)">
                    </div>
                </div>
            </div>
            <div class="info" v-if="loginStatus">
                <div>
                    <img :src="$imgUrl(userInfo.userAvatar)" style="width:90px;height:90px;border-radius:50%;">
                </div>
                <div style="padding: 0 40px;">
                    <div class="title1">
                        <span class="title">{{ userInfo.userName }}</span>
                    </div>
                    <div class="date">注册于： {{ userInfo.createTime }}</div>
                </div>
            </div>
        </div>
        <div class="content-area">
            <router-view></router-view>
        </div>
    </div>
</template>
<script>
import Logo from "@/components/Logo";
import { getToken, setUserInfo, setSearchKey, getSearchHistory, addSearchHistory } from "@/utils/storage";
export default {
    name: "Home",
    components: { Logo },
    data() {
        return {
            key: null,
            loginStatus: false,
            isAdmin: false,
            userInfo: {},
            searchPath: '/search',
            showHistory: false,
            historyList: [],
            unreadCount: 0,
        };
    },
    created() {
        this.loadLoginStatus();
        this.historyList = getSearchHistory();
    },
    watch: {
        '$route'(to, from) {
            // 离开消息页面时刷新未读数
            if (from && from.path === '/message') this.loadUnread();
            // 进入消息页面时清零
            if (to && to.path === '/message') this.unreadCount = 0;
        }
    },
    methods: {
        isActive(path) {
            return this.$router.currentRoute.fullPath === path;
        },
        loginOperation() { this.$router.push('/login'); },
        fetch() {
            if (!this.key || !this.key.trim()) return;
            addSearchHistory(this.key.trim());
            this.historyList = getSearchHistory();
            this.showHistory = false;
            setSearchKey(this.key.trim());
            if (this.$router.currentRoute.fullPath === '/search') {
                this.$router.replace({ path: '/search', query: { t: Date.now() } });
            } else {
                this.$router.push('/search');
            }
        },
        hideHistory() { setTimeout(() => { this.showHistory = false; }, 200); },
        searchHistoryItem(key) { this.key = key; this.fetch(); },
        clearHistory() {
            sessionStorage.removeItem('searchHistory');
            this.historyList = [];
            this.showHistory = false;
        },
        handleRouteSelect(path) {
            if (this.$router.currentRoute.fullPath !== path) {
                this.$router.push(path);
            }
        },
        loadLoginStatus() {
            const token = getToken();
            if (!token) { this.loginStatus = false; return; }
            this.auth();
        },
        auth() {
            this.$axios.get('/user/auth').then(res => {
                const { data } = res;
                if (data.code === 200) {
                    setUserInfo(data.data);
                    this.userInfo = data.data;
                    this.isAdmin = data.data.userRole === 1;
                }
                this.loginStatus = data.code === 200;
                if (data.code === 200) { this.loadUnread(); setInterval(() => this.loadUnread(), 15000); }
            }).catch(() => { this.loginStatus = false; });
        },
        loadUnread() {
            this.$axios.get('/message/unread').then(res => {
                if (res.data.code === 200) this.unreadCount = res.data.data || 0;
            }).catch(()=>{});
        },
    }
};
</script>
<style scoped lang="scss">
.avatar { border: 2px solid rgba(255,255,255,.3); width: 32px; height: 32px; border-radius: 50%; cursor: pointer; transition: all .3s; }
.avatar:hover { border-color: #ffd04b; transform: scale(1.1); }
.login-link { color: rgba(255,255,255,.7); cursor: pointer; font-size: 14px; transition: color .3s; }
.login-link:hover { color: #ffd04b; }

.icon-btn { text-align: center; cursor: pointer; color: rgba(255,255,255,.6); font-size: 12px; transition: color .3s;
    span { display: block; margin-top: 2px; }
    i { font-size: 18px; }
}
.icon-btn { position: relative; }
.icon-btn:hover { color: #ffd04b; }
.unread-dot { position: absolute; top: -6px; right: -8px; background: #f56c6c; color: #fff; font-size: 10px; min-width: 16px; height: 16px; line-height: 16px; text-align: center; border-radius: 8px; padding: 0 4px; }

.right-nav { display: flex; align-items: center; gap: 22px; }

.word-search {
    .item {
        max-width: 220px; background: rgba(255,255,255,.12); border-radius: 22px;
        display: flex; justify-content: space-between; align-items: center;
        border: 1px solid transparent; transition: all .3s;
        input:focus { width: 140px; }
        input { border: none; transition: all .4s; width: 80px; margin: 0 12px; background: transparent; outline: none; font-size: 13px; color: #fff; }
        input::placeholder { color: rgba(255,255,255,.4); }
        i { padding: 8px 14px; border-radius: 22px; cursor: pointer; color: rgba(255,255,255,.6); }
        i:hover { color: #ffd04b; background: rgba(255,255,255,.1); }
    }
    .item:focus-within { border-color: rgba(255,255,255,.3); background: rgba(255,255,255,.18); }
    .search-history {
        position: absolute; top: 42px; left: 0; right: 0;
        background: #fff; border-radius: 8px; box-shadow: 0 8px 24px rgba(0,0,0,.15);
        z-index: 100; padding: 6px 0; overflow: hidden;
        .history-item { padding: 10px 16px; cursor: pointer; font-size: 13px; color: #333; i { margin-right: 8px; color: #bbb; } }
        .history-item:hover { background: #f5f7fa; }
        .history-clear { text-align: center; padding: 8px; font-size: 11px; color: #999; border-top: 1px solid #f0f0f0; cursor: pointer; }
        .history-clear:hover { color: #f56c6c; }
    }
}

.container { background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%); box-shadow: 0 2px 20px rgba(0,0,0,.2); }

.info {
    padding: 22px 200px; display: flex; align-items: center; background: rgba(255,255,255,.03);
    img { width: 80px; height: 80px; border-radius: 50%; border: 3px solid rgba(255,255,255,.2); padding: 0; }
    .title1 { font-size: 22px; color: #fff; font-weight: 600; }
    .date { margin: 6px 0; font-size: 13px; color: rgba(255,255,255,.45); }
}

.top {
    height: 64px; padding: 0 200px;
    display: flex; justify-content: space-between; align-items: center;
    .nav {
        display: flex; align-items: center; gap: 24px;
        .route {
            display: flex; align-items: center; gap: 28px;
            span { font-size: 14px; color: rgba(255,255,255,.6); cursor: pointer; padding-bottom: 4px; border-bottom: 2px solid transparent; transition: all .3s; }
            span:hover { color: #fff; }
            span.active { color: #fff; font-weight: 600; border-bottom-color: #ffd04b; }
        }
    }
}

.content-area { padding: 20px 200px; min-height: 70vh; background: #f5f7fa; }
</style>
