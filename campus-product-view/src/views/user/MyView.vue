<template>
    <div class="my-view">
        <h3 style="margin-bottom:15px;">浏览足迹</h3>
        <el-row v-if="tableData.length">
            <el-row v-for="item in tableData" :key="item.id" class="view-item" @click="goDetail(item.productId)">
                <img :src="getCover(item.coverList)" class="view-cover" />
                <div class="view-info">
                    <div class="view-name">{{ item.productName || '商品' + item.productId }}</div>
                    <div v-if="item.productPrice" class="view-price">¥{{ item.productPrice || item.price }}</div>
                    <div class="view-time">{{ timeAgo(item.actionTime) }}</div>
                </div>
            </el-row>
        </el-row>
        <el-row v-else style="text-align:center;padding:60px 0;color:#999;">
            暂无浏览记录
        </el-row>
    </div>
</template>

<script>
import { timeAgo } from "@/utils/data";
export default {
    name: 'MyView',
    data() {
        return { tableData: [] };
    },
    created() { this.fetchData(); },
    methods: {
        timeAgo,
        getCover(c) { return this.$imgUrl(c ? c.split(',')[0] : null); },
        goDetail(id) { if (id) this.$router.push('/product/detail/' + id); },
        fetchData() {
            this.$axios.get('/interaction/myView', { params: { current: 1, size: 100 } }).then(res => {
                if (res.data.code === 200) { this.tableData = res.data.data; }
            });
        },
    }
};
</script>

<style scoped lang="scss">
.my-view { background: #fff; padding: 20px; border-radius: 8px; }
.view-item { display: flex; align-items: center; padding: 12px 0; border-bottom: 1px solid #f4f4f4; cursor: pointer; }
.view-item:hover { background: #fafafa; }
.view-cover { width: 60px; height: 60px; object-fit: cover; border-radius: 4px; margin-right: 15px; }
.view-info { flex: 1; }
.view-name { font-size: 14px; color: #333; }
.view-price { font-size: 14px; color: #f56c6c; }
.view-time { font-size: 11px; color: #bbb; margin-top: 4px; }
</style>
