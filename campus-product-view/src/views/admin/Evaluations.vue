<template>
    <el-row style="background-color: #FFFFFF;padding: 10px 0;border-radius: 5px;">
        <el-row style="padding: 10px;margin: 0 10px;">
            <el-row>
                <el-date-picker @change="handleFilter" size="small" style="width: 220px;" v-model="searchTime"
                    type="daterange" range-separator="至" start-placeholder="评论开始" end-placeholder="评论结束">
                </el-date-picker>
                <el-input size="small" style="width: 188px;margin-left: 5px;margin-right: 6px;"
                    v-model="evalustionsQueryDto.content" placeholder="评论内容" clearable @clear="handleFilter">
                    <el-button slot="append" @click="handleFilter" icon="el-icon-search"></el-button>
                </el-input>
            </el-row>
        </el-row>
        <el-row style="margin: 0 20px;border-top: 1px solid rgb(245,245,245);">
            <el-table :data="tableData" style="width: 100%">
                <el-table-column prop="content" label="评论内容" min-width="200">
                    <template slot-scope="scope">
                        <div class="cell-name">{{ scope.row.content }}</div>
                    </template>
                </el-table-column>
                <el-table-column label="点赞数" width="70">
                    <template slot-scope="scope">
                        <span style="font-size:16px;font-weight:bold;">{{
                            scope.row.upvoteList ? scope.row.upvoteList.split(',').length : 0 }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="contentType" width="80" label="挂载源"></el-table-column>
                <el-table-column prop="userName" width="100" label="评论者"></el-table-column>
                <el-table-column prop="replierName" width="100" label="被回复">
                    <template slot-scope="scope">
                        <span v-if="scope.row.replierName">{{ scope.row.replierName }}</span>
                        <span v-else style="color:#ccc;">—</span>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" width="160" label="评论时间"></el-table-column>
                <el-table-column prop="parentId" width="70" label="层级">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.parentId === null" size="mini">父级</el-tag>
                        <el-tag v-else type="warning" size="mini">子级</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="80" fixed="right">
                    <template slot-scope="scope">
                        <span class="text-button" @click="handleDelete(scope.row)">删除</span>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination style="margin:10px 0;float:right;" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[20, 50]"
                :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="totalItems">
            </el-pagination>
        </el-row>
    </el-row>
</template>

<script>
export default {
    data() {
        return {
            tableData: [],
            currentPage: 1,
            pageSize: 20,
            totalItems: 0,
            searchTime: [],
            delectedRows: [],
            evalustionsQueryDto: {},
        };
    },
    created() {
        this.fetchFreshData();
    },
    methods: {
        async batchDelete() {
            if (!this.delectedRows.length) {
                this.$message('未选中任何数据');
                return;
            }
            const confirmed = await this.$swalConfirm({
                title: '删除评论数据',
                text: '删除后不可恢复，是否继续？',
                icon: 'warning',
            });
            if (confirmed) {
                try {
                    let ids = this.delectedRows.map(e => e.id);
                    const response = await this.$axios.post('/evaluations/batchDelete', ids);
                    if (response.data.code === 200) {
                        this.$message.success('删除成功');
                        this.fetchFreshData();
                    }
                } catch (e) {
                    this.$message.error('删除异常');
                }
            }
        },
        async fetchFreshData() {
            let startTime = '';
            let endTime = '';
            if (this.searchTime && this.searchTime.length === 2) {
                const [s, e] = this.searchTime.map(d => d.toISOString());
                startTime = s.split('T')[0] + 'T00:00:00';
                endTime = e.split('T')[0] + 'T23:59:59';
            }
            const params = {
                current: this.currentPage,
                size: this.pageSize,
                startTime,
                endTime,
                ...this.evalustionsQueryDto,
            };
            try {
                const res = await this.$axios.post('/evaluations/query', params);
                if (res.data.code === 200) {
                    this.tableData = res.data.data;
                    this.totalItems = res.data.total;
                }
            } catch (e) {
                console.error('查询异常:', e);
            }
        },
        handleFilter() { this.currentPage = 1; this.fetchFreshData(); },
        handleSizeChange(v) { this.pageSize = v; this.currentPage = 1; this.fetchFreshData(); },
        handleCurrentChange(v) { this.currentPage = v; this.fetchFreshData(); },
        handleDelete(row) { this.delectedRows = [row]; this.batchDelete(); },
    },
};
</script>
<style scoped lang="scss"></style>
