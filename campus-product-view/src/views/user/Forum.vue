<template>
    <div class="forum-page">
        <div class="breadcrumb">
            <span @click="$router.push('/product')" class="crumb-link">首页</span>
            <span class="crumb-sep">/</span>
            <span class="crumb-current">论坛</span>
            <span v-if="loginStatus" class="edit-button" @click="$router.push('/post-forum')" style="float:right;">发帖</span>
        </div>

        <div class="filter-bar">
            <span v-for="cat in categories" :key="cat" :class="{ 'cat-tag': true, 'cat-active': query.category === cat }"
                @click="selectCategory(cat)">{{ cat }}</span>
            <span :class="{ 'cat-tag': true, 'cat-active': !query.category }" @click="selectCategory('')">全部</span>
        </div>

        <div v-if="tableData.length" class="forum-list">
            <div class="forum-card" v-for="item in tableData" :key="item.id" @click="goDetail(item.id)">
                <div class="forum-card-header">
                    <span class="forum-category">{{ item.category }}</span>
                    <span class="forum-time">{{ timeAgo(item.createTime) }}</span>
                </div>
                <div class="forum-title">{{ item.title }}</div>
                <div class="forum-meta">
                    <span><i class="el-icon-view"></i> {{ item.viewCount || 0 }}</span>
                </div>
            </div>
        </div>
        <div v-else class="empty-state">
            <i class="el-icon-chat-dot-square" style="font-size:48px;color:#ddd;"></i>
            <p>暂无帖子</p>
        </div>

        <div class="pagination-wrap" v-if="totalItems > 0">
            <el-pagination background @current-change="handleCurrentChange" :current-page="currentPage"
                :page-size="10" layout="prev, pager, next" :total="totalItems">
            </el-pagination>
        </div>
    </div>
</template>

<script>
import { getToken } from "@/utils/storage";
import { timeAgo } from "@/utils/data";
export default {
    name: 'Forum',
    data() {
        return {
            categories: ['交流', '求助', '交易', '其他'],
            query: { category: '' },
            tableData: [], currentPage: 1, totalItems: 0,
            loginStatus: !!getToken(),
        };
    },
    created() { this.fetchData(); },
    methods: {
        timeAgo,
        selectCategory(cat) { this.query.category = cat; this.currentPage = 1; this.fetchData(); },
        fetchData() {
            const params = { current: this.currentPage, size: 10 };
            if (this.query.category) params.category = this.query.category;
            this.$axios.post('/forum/query', params).then(res => {
                if (res.data.code === 200) { this.tableData = res.data.data; this.totalItems = res.data.total; }
            });
        },
        handleCurrentChange(v) { this.currentPage = v; this.fetchData(); },
        goDetail(id) { this.$router.push('/forum/detail/' + id); },
    }
};
</script>

<style scoped lang="scss">
.forum-page { background: #fff; border-radius: 8px; padding: 20px; min-height: 400px; }
.breadcrumb { margin-bottom: 12px; font-size: 13px; color: #999; }
.crumb-link { cursor: pointer; color: #666; }
.crumb-link:hover { color: #409EFF; }
.crumb-sep { margin: 0 6px; }
.crumb-current { color: #333; }
.filter-bar { margin-bottom: 20px; display: flex; gap: 10px; }
.cat-tag { padding: 4px 14px; border-radius: 20px; cursor: pointer; font-size: 13px; color: #666; background: #f5f5f5; }
.cat-tag:hover { background: #e0e0e0; }
.cat-active { background: #409EFF; color: #fff; }
.forum-list { display: flex; flex-direction: column; gap: 0; }
.forum-card { padding: 16px 0; border-bottom: 1px solid #f0f0f0; cursor: pointer; }
.forum-card:hover { background: #fafafa; }
.forum-card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.forum-category { font-size: 11px; color: #409EFF; background: #ecf5ff; padding: 2px 8px; border-radius: 3px; }
.forum-time { font-size: 11px; color: #bbb; }
.forum-title { font-size: 16px; color: #333; font-weight: 500; margin-bottom: 6px; }
.forum-meta { font-size: 12px; color: #999; }
.empty-state { text-align: center; padding: 80px 0; color: #999; }
.pagination-wrap { text-align: center; margin-top: 20px; }
</style>
