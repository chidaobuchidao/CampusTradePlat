<template>
    <div class="my-products">
        <el-row style="margin-bottom:15px; display:flex; justify-content:space-between;">
            <h3>我的商品</h3>
            <span class="edit-button" @click="$router.push('/post-product')" style="float:right;">发布新商品</span>
        </el-row>

        <el-row v-if="tableData.length" class="product-grid">
            <el-col :span="6" v-for="item in tableData" :key="item.id" style="padding: 8px;">
                <el-card shadow="hover" class="product-card">
                    <img :src="getCover(item.coverList)" class="product-cover" @click="goDetail(item.id)" />
                    <div class="product-info">
                        <div class="product-name">{{ item.name }}</div>
                        <div class="product-price">¥{{ item.price }}</div>
                        <div class="product-meta">
                            <el-tag size="mini">{{ levelText(item.oldLevel) }}</el-tag>
                            <span>{{ item.inventory || 1 }}件</span>
                        </div>
                        <div style="margin-top:8px;display:flex;gap:8px;">
                            <el-button size="mini" @click="handleEdit(item)">编辑</el-button>
                            <el-button size="mini" type="danger" @click="handleDelete(item)">删除</el-button>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>
        <el-row v-else style="text-align:center;padding:60px 0;color:#999;">
            暂无商品，<span style="color:#409EFF;cursor:pointer;" @click="$router.push('/post-product')">去发布</span>
        </el-row>

        <el-dialog title="编辑商品" :visible.sync="dialogVisible" width="600px">
            <el-form label-width="80px">
                <el-form-item label="名称"><el-input v-model="editForm.name"></el-input></el-form-item>
                <el-form-item label="价格"><el-input-number v-model="editForm.price" :min="0.01"></el-input-number></el-form-item>
                <el-form-item label="库存"><el-input-number v-model="editForm.inventory" :min="1"></el-input-number></el-form-item>
                <el-form-item label="新旧程度">
                    <el-select v-model="editForm.oldLevel">
                        <el-option label="全新" :value="1"></el-option>
                        <el-option label="几乎全新" :value="2"></el-option>
                        <el-option label="有使用痕迹" :value="3"></el-option>
                        <el-option label="较旧" :value="4"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="砍价"><el-switch v-model="editForm.isBargain"></el-switch></el-form-item>
            </el-form>
            <span slot="footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="saveEdit">保存</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: 'MyProduct',
    data() {
        return {
            tableData: [],
            dialogVisible: false,
            editForm: {},
        };
    },
    created() { this.fetchData(); },
    methods: {
        getCover(c) { return this.$imgUrl(c ? c.split(',')[0] : null); },
        levelText(l) { return {1:'全新',2:'几乎全新',3:'有使用痕迹',4:'较旧'}[l]||''; },
        goDetail(id) { this.$router.push('/product/detail/' + id); },
        fetchData() {
            this.$axios.post('/product/myProducts', { current: 1, size: 100 }).then(res => {
                if (res.data.code === 200) { this.tableData = res.data.data; }
            });
        },
        handleEdit(row) {
            this.editForm = { ...row };
            this.dialogVisible = true;
        },
        saveEdit() {
            this.$axios.put('/product/update', this.editForm).then(res => {
                if (res.data.code === 200) {
                    this.$message.success('修改成功');
                    this.dialogVisible = false;
                    this.fetchData();
                } else { this.$message.error(res.data.msg); }
            });
        },
        handleDelete(row) {
            this.$swalConfirm({ title: '删除商品', text: '删除后不可恢复？', icon: 'warning' }).then(ok => {
                if (ok) {
                    this.$axios.post('/product/batchDelete', [row.id]).then(res => {
                        if (res.data.code === 200) { this.$message.success('已删除'); this.fetchData(); }
                    });
                }
            });
        },
    }
};
</script>

<style scoped lang="scss">
.my-products { background: #fff; padding: 20px; border-radius: 8px; }
.product-grid { margin: 0 -8px; }
.product-card { cursor: pointer; }
.product-cover { width: 100%; height: 140px; object-fit: cover; border-radius: 4px; cursor: pointer; }
.product-info { padding: 8px 0 0 0; }
.product-name { font-size: 14px; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-price { font-size: 16px; color: #f56c6c; font-weight: bold; margin: 4px 0; }
.product-meta { display: flex; justify-content: space-between; align-items: center; font-size: 12px; color: #999; }
</style>
