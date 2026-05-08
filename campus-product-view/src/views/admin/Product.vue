<template>
    <el-row style="background-color: #FFFFFF;padding: 5px 0;border-radius: 5px;">
        <el-row style="padding: 10px;margin-left: 5px;">
            <el-row>
                <el-input size="small" style="width: 200px;" v-model="query.name" placeholder="搜索商品名" clearable @clear="handleFilter">
                    <el-button slot="append" @click="handleFilter" icon="el-icon-search"></el-button>
                </el-input>
                <el-select size="small" v-model="query.categoryId" placeholder="商品类别" clearable @change="handleFilter" style="width: 140px;margin-left: 10px;">
                    <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id"></el-option>
                </el-select>
            </el-row>
        </el-row>

        <el-row style="margin: 0 22px;border-top: 1px solid rgb(245,245,245);">
            <el-table :stripe="true" :data="tableData" style="width: 100%" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="48"></el-table-column>
                <el-table-column prop="id" width="60" label="ID"></el-table-column>
                <el-table-column label="封面" width="80">
                    <template slot-scope="scope">
                        <img :src="getCover(scope.row.coverList)" style="width:50px;height:50px;object-fit:cover;border-radius:4px;" />
                    </template>
                </el-table-column>
                <el-table-column prop="name" label="商品名" min-width="140"></el-table-column>
                <el-table-column prop="price" label="价格" width="100">
                    <template slot-scope="scope">¥{{ scope.row.price }}</template>
                </el-table-column>
                <el-table-column prop="oldLevel" label="新旧" width="80">
                    <template slot-scope="scope">{{ levelText(scope.row.oldLevel) }}</template>
                </el-table-column>
                <el-table-column prop="inventory" label="库存" width="70"></el-table-column>
                <el-table-column prop="createTime" label="发布时间" width="160"></el-table-column>
                <el-table-column label="操作" width="120">
                    <template slot-scope="scope">
                        <span class="text-button" @click="handleEdit(scope.row)">编辑</span>
                        <span class="text-button" @click="handleDelete(scope.row)">删除</span>
                    </template>
                </el-table-column>
            </el-table>

            <el-row style="margin:10px 0;display:flex;justify-content:space-between;">
                <span class="edit-button" @click="batchDelete" style="float:left;">批量删除</span>
                <el-pagination style="float: right;" @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    :current-page="currentPage" :page-sizes="[10, 20]" :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper" :total="totalItems">
                </el-pagination>
            </el-row>
        </el-row>

        <el-dialog :show-close="false" :visible.sync="dialogVisible" width="30%">
            <div style="padding:16px 20px;">
                <el-row><p>*商品名</p><input class="dialog-input" v-model="data.name" placeholder="商品名" /></el-row>
                <el-row style="margin-top:12px;"><p>*价格</p><el-input-number v-model="data.price" :min="0.01" size="small"></el-input-number></el-row>
                <el-row style="margin-top:12px;"><p>*库存</p><el-input-number v-model="data.inventory" :min="1" size="small"></el-input-number></el-row>
                <el-row style="margin-top:12px;">
                    <p>*新旧程度</p>
                    <el-select v-model="data.oldLevel" size="small">
                        <el-option label="全新" :value="1"></el-option>
                        <el-option label="几乎全新" :value="2"></el-option>
                        <el-option label="有使用痕迹" :value="3"></el-option>
                        <el-option label="较旧" :value="4"></el-option>
                    </el-select>
                </el-row>
            </div>
            <span slot="footer" class="dialog-footer" style="margin-top: 10px;">
                <span class="channel-button" @click="cancel">取消操作</span>
                <span class="edit-button" @click="saveOperation">确定保存</span>
            </span>
        </el-dialog>
    </el-row>
</template>

<script>
export default {
    data() {
        return {
            categories: [],
            data: {},
            tableData: [],
            selectedRows: [],
            query: { name: '', categoryId: null },
            currentPage: 1,
            pageSize: 10,
            totalItems: 0,
            dialogVisible: false,
        };
    },
    created() {
        this.loadCategories();
        this.fetchData();
    },
    methods: {
        getCover(c) { return this.$imgUrl(c ? c.split(',')[0] : null); },
        levelText(l) { return {1:'全新',2:'几乎全新',3:'有使用痕迹',4:'较旧'}[l]||''; },
        loadCategories() {
            this.$axios.post('/category/query', { current: 1, size: 100 }).then(res => {
                if (res.data.code === 200) this.categories = res.data.data;
            });
        },
        fetchData() {
            const params = { current: this.currentPage, size: this.pageSize, includeSoldOut: true };
            if (this.query.name) params.name = this.query.name;
            if (this.query.categoryId) params.categoryId = this.query.categoryId;
            this.$axios.post('/product/query', params).then(res => {
                if (res.data.code === 200) {
                    this.tableData = res.data.data;
                    this.totalItems = res.data.total;
                }
            });
        },
        handleSelectionChange(rows) { this.selectedRows = rows; },
        handleFilter() { this.currentPage = 1; this.fetchData(); },
        handleSizeChange(v) { this.pageSize = v; this.currentPage = 1; this.fetchData(); },
        handleCurrentChange(v) { this.currentPage = v; this.fetchData(); },
        handleEdit(row) { this.data = { ...row }; this.dialogVisible = true; },
        handleDelete(row) { this.selectedRows = [row]; this.batchDelete(); },
        cancel() { this.data = {}; this.dialogVisible = false; },
        saveOperation() {
            const api = this.data.id ? this.$axios.put('/product/update', this.data) : this.$axios.post('/product/save', this.data);
            api.then(res => {
                if (res.data.code === 200) {
                    this.$message.success('保存成功');
                    this.cancel();
                    this.fetchData();
                } else { this.$message.error(res.data.msg); }
            });
        },
        async batchDelete() {
            if (!this.selectedRows.length) { this.$message('未选中任何数据'); return; }
            const ok = await this.$swalConfirm({ title: '删除商品', text: '删除后不可恢复？', icon: 'warning' });
            if (ok) {
                const ids = this.selectedRows.map(r => r.id);
                this.$axios.post('/product/batchDelete', ids).then(res => {
                    if (res.data.code === 200) { this.$message.success('已删除'); this.fetchData(); }
                });
            }
        },
    }
};
</script>
