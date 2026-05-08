<template>
    <div class="detail-page" v-if="post.id">
        <div class="breadcrumb">
            <span @click="$router.push('/forum')" class="crumb-link">论坛</span>
            <span class="crumb-sep">/</span>
            <span class="crumb-current">{{ post.title }}</span>
        </div>

        <div class="post-content">
            <h2>{{ post.title }}</h2>
            <div class="post-meta">
                <span><i class="el-icon-view"></i> {{ post.viewCount || 0 }}</span>
                <span>{{ post.createTime }}</span>
                <span class="text-button" v-if="canDelete" @click="handleDelete">删除</span>
            </div>
            <div class="post-body" v-html="post.content"></div>
        </div>

        <div style="margin-top:20px;">
            <Evaluations :contentId="post.id" contentType="FORUM" />
        </div>
    </div>
</template>

<script>
import Evaluations from "@/components/Evaluations.vue";
import { getToken } from "@/utils/storage";
export default {
    name: 'ForumDetail',
    components: { Evaluations },
    data() {
        return { post: {}, isLoggedIn: false, canDelete: false };
    },
    created() {
        this.isLoggedIn = !!getToken();
        this.loadDetail();
    },
    methods: {
        loadDetail() {
            const id = this.$route.params.id;
            this.$axios.get('/forum/detail/' + id).then(res => {
                if (res.data.code === 200) {
                    this.post = res.data.data;
                    const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || '{}');
                    this.canDelete = userInfo.id === this.post.userId || sessionStorage.getItem('role') === '1';
                }
            });
        },
        handleDelete() {
            this.$swalConfirm({ title: '删除帖子', text: '确定删除？', icon: 'warning' }).then(ok => {
                if (ok) {
                    this.$axios.delete('/forum/delete/' + this.post.id).then(res => {
                        if (res.data.code === 200) { this.$message.success('已删除'); this.$router.push('/forum'); }
                    });
                }
            });
        },
    }
};
</script>

<style scoped>
.detail-page { background: #fff; border-radius: 8px; padding: 20px; min-height: 400px; }
.breadcrumb { margin-bottom: 16px; font-size: 13px; color: #999; }
.crumb-link { cursor: pointer; color: #666; }
.crumb-link:hover { color: #409EFF; }
.crumb-sep { margin: 0 6px; }
.crumb-current { color: #333; }
.post-content { padding: 10px 0; border-bottom: 1px solid #f0f0f0; }
.post-content h2 { font-size: 22px; color: #333; margin-bottom: 10px; }
.post-meta { font-size: 12px; color: #999; margin-bottom: 20px; display: flex; gap: 20px; }
.post-body { line-height: 1.8; font-size: 15px; color: #333; padding: 10px 0; }
</style>
