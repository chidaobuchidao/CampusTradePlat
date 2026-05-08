<template>
    <div class="user-profile-page">
        <!-- 用户不存在 -->
        <div v-if="notFound" class="empty-state" style="padding:120px 0;">
            <i class="el-icon-user-solid" style="font-size:64px;color:#ddd;"></i>
            <p>{{ isLoggedIn ? '用户不存在' : '请先登录后查看用户主页' }}</p>
            <el-button v-if="!isLoggedIn" type="primary" size="small" @click="$router.push({ path: '/login', query: { redirect: $route.fullPath } })">去登录</el-button>
            <el-button v-else size="small" @click="$router.back()">返回</el-button>
        </div>

        <!-- 用户信息头部 -->
        <div class="profile-header-card" v-if="!notFound">
            <div class="profile-main">
                <img :src="$imgUrl(userInfo.userAvatar)" class="profile-avatar" @error="e=>e.target.style.display='none'" />
                <div class="profile-info">
                    <div class="profile-name">{{ userInfo.userName || '未知用户' }}</div>
                    <div class="profile-signature" v-if="userInfo.signature">
                        <i class="el-icon-chat-dot-round"></i> {{ userInfo.signature }}
                    </div>
                    <div class="profile-meta">
                        <span><i class="el-icon-time"></i> 注册于 {{ userInfo.createTime }}</span>
                    </div>
                </div>
                <div class="profile-stats">
                    <div class="stat-item">
                        <div class="stat-num">{{ productTotal }}</div>
                        <div class="stat-label">商品</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-num">{{ avgRating }}</div>
                        <div class="stat-label">评分</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-num">{{ reviewTotal }}</div>
                        <div class="stat-label">评价</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Tab 导航 -->
        <div class="profile-tabs" v-if="!notFound">
            <span :class="{ active: activeTab === 'products' }" @click="activeTab = 'products'">
                <i class="el-icon-goods"></i> TA的商品
            </span>
            <span :class="{ active: activeTab === 'reviews' }" @click="activeTab = 'reviews'">
                <i class="el-icon-chat-dot-round"></i> TA的评价
            </span>
        </div>

        <!-- 商品列表 -->
        <div v-if="activeTab === 'products'">
            <div v-if="products.length" class="product-grid">
                <div v-for="p in products" :key="p.id" class="product-card" @click="$router.push('/product/detail/' + p.id)">
                    <div class="product-cover">
                        <img :src="$imgUrl(p.coverList)" @error="e=>e.target.src=''" />
                        <div v-if="p.status === 1" class="sold-badge">已售出</div>
                    </div>
                    <div class="product-body">
                        <div class="product-name">{{ p.name }}</div>
                        <div class="product-price">¥{{ p.price }}</div>
                    </div>
                </div>
            </div>
            <div v-else class="empty-state">
                <i class="el-icon-goods" style="font-size:48px;color:#ddd;"></i>
                <p>暂无商品</p>
            </div>
        </div>

        <!-- 评价列表 -->
        <div v-if="activeTab === 'reviews'">
            <div v-if="reviews.length" class="review-list">
                <div v-for="r in reviews" :key="r.id" class="review-card">
                    <div class="review-header">
                        <img :src="$imgUrl(r.userAvatar)" class="review-avatar" @error="e=>e.target.style.display='none'"
                            style="cursor:pointer;" @click="$router.push('/user/' + r.userId)" />
                        <div class="review-user">
                            <span class="review-name" style="cursor:pointer;" @click="$router.push('/user/' + r.userId)">{{ r.userName }}</span>
                            <span class="review-time">{{ r.createTime }}</span>
                        </div>
                        <div class="review-rating" v-if="r.rating">
                            <i v-for="s in 5" :key="s" :class="s <= r.rating ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
                        </div>
                    </div>
                    <div class="review-content">{{ r.content }}</div>
                </div>
            </div>
            <div v-else class="empty-state">
                <i class="el-icon-chat-dot-round" style="font-size:48px;color:#ddd;"></i>
                <p>暂无评价</p>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'UserProfile',
    data() {
        return {
            userId: null,
            userInfo: {},
            products: [],
            reviews: [],
            productTotal: 0,
            reviewTotal: 0,
            avgRating: '0.0',
            activeTab: 'products',
            notFound: false,
            isLoggedIn: false,
        };
    },
    created() {
        this.userId = this.$route.params.id;
        this.isLoggedIn = !!sessionStorage.getItem('token');
        if (this.userId) this.loadAll();
    },
    watch: {
        '$route.params.id'(val) {
            if (val && val !== this.userId) {
                this.userId = val;
                this.loadAll();
            }
        }
    },
    methods: {
        loadAll() {
            this.loadUser();
            this.loadProducts();
            this.loadReviews();
            this.loadRating();
        },
        loadUser() {
            this.notFound = false;
            this.$axios.get('/user/profile/' + this.userId).then(res => {
                if (res.data.code === 200) this.userInfo = res.data.data;
                else this.notFound = true;
            }).catch(() => { this.notFound = true; });
        },
        loadProducts() {
            this.$axios.post('/product/query', { userId: parseInt(this.userId), current: 1, size: 100 }).then(res => {
                if (res.data.code === 200) {
                    this.products = res.data.data || [];
                    this.productTotal = res.data.total || 0;
                }
            });
        },
        loadReviews() {
            this.$axios.get('/evaluations/userReviews/' + this.userId).then(res => {
                if (res.data.code === 200) {
                    const vo = res.data.data;
                    this.reviews = vo.data || [];
                    this.reviewTotal = vo.count || 0;
                }
            });
        },
        loadRating() {
            this.$axios.get('/evaluations/avgRating/' + this.userId).then(res => {
                if (res.data.code === 200) {
                    this.avgRating = (res.data.data.avgRating || 0).toFixed(1);
                }
            });
        },
    }
};
</script>

<style scoped lang="scss">
.user-profile-page { max-width: 900px; margin: 0 auto; padding: 20px; }

.profile-header-card {
    background: #fff; border-radius: 14px; padding: 32px;
    box-shadow: 0 2px 16px rgba(0,0,0,.05); margin-bottom: 20px;
}
.profile-main { display: flex; align-items: center; gap: 24px; }
.profile-avatar {
    width: 90px; height: 90px; border-radius: 50%; object-fit: cover;
    border: 3px solid #f0f0f0; flex-shrink: 0;
}
.profile-info { flex: 1; }
.profile-name { font-size: 24px; font-weight: 700; color: #303133; }
.profile-signature { font-size: 13px; color: #909399; margin-top: 6px;
    i { margin-right: 4px; }
}
.profile-meta { font-size: 12px; color: #bbb; margin-top: 8px; }

.profile-stats { display: flex; gap: 28px; flex-shrink: 0;
    .stat-item { text-align: center;
        .stat-num { font-size: 24px; font-weight: 700; color: #303133; }
        .stat-label { font-size: 12px; color: #909399; margin-top: 2px; }
    }
}

.profile-tabs {
    display: flex; gap: 0; margin-bottom: 16px;
    span {
        padding: 10px 24px; cursor: pointer; font-size: 14px; color: #909399;
        border-bottom: 2px solid transparent; transition: all .2s;
        &:hover { color: #409EFF; }
        &.active { color: #409EFF; font-weight: 600; border-bottom-color: #409EFF; }
    }
}

.product-grid {
    display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px;
}
.product-card {
    background: #fff; border-radius: 10px; overflow: hidden; cursor: pointer;
    box-shadow: 0 2px 8px rgba(0,0,0,.04); transition: all .2s;
    &:hover { transform: translateY(-2px); box-shadow: 0 4px 16px rgba(0,0,0,.08); }
}
.product-cover {
    position: relative; width: 100%; height: 160px; overflow: hidden; background: #f5f5f5;
    img { width: 100%; height: 100%; object-fit: cover; }
}
.sold-badge {
    position: absolute; top: 8px; right: 8px; background: rgba(0,0,0,.6); color: #fff;
    font-size: 11px; padding: 2px 8px; border-radius: 4px;
}
.product-body { padding: 12px; }
.product-name { font-size: 14px; color: #303133; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-price { font-size: 16px; font-weight: 700; color: #F56C6C; margin-top: 6px; }

.review-list { display: flex; flex-direction: column; gap: 12px; }
.review-card {
    background: #fff; border-radius: 10px; padding: 16px;
    box-shadow: 0 2px 8px rgba(0,0,0,.04);
}
.review-header { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.review-avatar { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; }
.review-user { flex: 1; }
.review-name { font-size: 14px; font-weight: 600; color: #303133; }
.review-name:hover { color: #409EFF; }
.review-time { font-size: 11px; color: #bbb; margin-left: 10px; }
.review-rating {
    i { color: #E6A23C; font-size: 16px; margin-left: 2px; }
    .el-icon-star-off { color: #ddd; }
}
.review-content { font-size: 13px; color: #606266; line-height: 1.6; }

.empty-state { text-align: center; padding: 60px 0; color: #999; }
.empty-state p { margin-top: 12px; font-size: 14px; }
</style>
