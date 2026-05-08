<template>
    <div class="my-save">
        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
            <el-tab-pane label="我的收藏" name="save"></el-tab-pane>
            <el-tab-pane label="我想要" name="want"></el-tab-pane>
        </el-tabs>

        <el-row v-if="tableData.length" class="product-grid">
            <el-col :span="6" v-for="item in tableData" :key="item.id" style="padding: 8px;">
                <el-card shadow="hover" class="product-card">
                    <img :src="getCover(item.coverList)" class="product-cover" @click="goDetail(item.productId)" />
                    <div class="product-info">
                        <div class="product-name">{{ item.productName || item.name || item.productId }}</div>
                        <div v-if="item.productPrice || item.price" class="product-price">¥{{ item.productPrice || item.price }}</div>
                        <div class="product-time">{{ timeAgo(item.actionTime) }}</div>
                        <el-button size="mini" type="danger" @click="cancel(item)" style="margin-top:6px;">
                            {{ activeTab === 'save' ? '取消收藏' : '移除' }}
                        </el-button>
                    </div>
                </el-card>
            </el-col>
        </el-row>
        <el-row v-else style="text-align:center;padding:60px 0;color:#999;">
            {{ activeTab === 'save' ? '暂无收藏' : '暂无想要的商品' }}
        </el-row>
    </div>
</template>

<script>
import { timeAgo } from "@/utils/data";
export default {
    name: 'MySave',
    data() {
        return {
            activeTab: 'save',
            tableData: [],
        };
    },
    created() { this.fetchData(); },
    methods: {
        timeAgo,
        getCover(c) { return this.$imgUrl(c ? c.split(',')[0] : null); },
        goDetail(id) { if (id) this.$router.push('/product/detail/' + id); },
        handleTabClick() { this.fetchData(); },
        fetchData() {
            const actionType = this.activeTab === 'save' ? 1 : 2;
            const endpoint = actionType === 1 ? '/interaction/mySave' : '/interaction/myView';
            const params = { current: 1, size: 100 };
            if (actionType === 1) {
                this.$axios.get('/interaction/mySave', { params }).then(res => {
                    if (res.data.code === 200) { this.tableData = res.data.data; }
                });
            } else {
                this.$axios.get('/interaction/myView', { params }).then(res => {
                    if (res.data.code === 200) { this.tableData = res.data.data; }
                });
            }
        },
        cancel(item) {
            if (this.activeTab === 'save') {
                this.$axios.post('/interaction/toggleSave/' + item.productId).then(res => {
                    if (res.data.code === 200) { this.$message.success('已取消'); this.fetchData(); }
                });
            } else {
                this.$message.info('暂不支持移除浏览记录');
            }
        },
    }
};
</script>

<style scoped lang="scss">
.my-save { background: #fff; padding: 20px; border-radius: 8px; }
.product-grid { margin: 0 -8px; }
.product-card { cursor: pointer; }
.product-cover { width: 100%; height: 140px; object-fit: cover; border-radius: 4px; cursor: pointer; }
.product-info { padding: 8px 0 0 0; }
.product-name { font-size: 14px; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-price { font-size: 16px; color: #f56c6c; font-weight: bold; margin: 4px 0; }
.product-time { font-size: 11px; color: #bbb; }
</style>
