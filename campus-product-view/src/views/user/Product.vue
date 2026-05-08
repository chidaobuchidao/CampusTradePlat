<template>
    <div class="product-page">
        <!-- 面包屑导航 -->
        <div class="breadcrumb">
            <span @click="$router.push('/product')" class="crumb-link">首页</span>
            <span class="crumb-sep">/</span>
            <span class="crumb-current">商品列表</span>
        </div>

        <!-- 分类筛选：一级 → 二级 -->
        <div class="tag-filter">
            <span v-for="cat in parentCategories" :key="cat.id"
                :class="{ 'filter-tag': true, 'filter-tag-active': selectedParent === cat.id }"
                @click="selectParent(cat.id)">{{ cat.name }}</span>
        </div>
        <div class="tag-filter" style="margin-bottom:14px;" v-if="subCategories.length">
            <span style="font-size:12px;color:#999;line-height:30px;margin-right:6px;">▸</span>
            <span v-for="sub in subCategories" :key="sub.id"
                :class="{ 'filter-tag': true, 'filter-tag-sm': true, 'filter-tag-active': query.categoryId === sub.id }"
                @click="toggleSubCategory(sub.id)">{{ sub.name }}</span>
        </div>

        <!-- 筛选栏 -->
        <div class="filter-bar">
            <el-select size="small" v-model="query.oldLevel" placeholder="新旧程度" clearable @change="handleFilter" style="width: 140px;">
                <el-option label="全新" :value="1"></el-option>
                <el-option label="几乎全新" :value="2"></el-option>
                <el-option label="有使用痕迹" :value="3"></el-option>
                <el-option label="较旧" :value="4"></el-option>
            </el-select>
            <el-input size="small" v-model="priceMin" placeholder="最低价" clearable @input="debounceFilter" style="width: 110px;margin-left: 10px;"></el-input>
            <span style="margin: 0 5px;color: #999;">—</span>
            <el-input size="small" v-model="priceMax" placeholder="最高价" clearable @input="debounceFilter" style="width: 110px;"></el-input>
            <el-input size="small" v-model="query.name" placeholder="搜索商品名" clearable @input="debounceFilter" style="width: 200px;margin-left: 10px;">
                <i slot="prefix" class="el-icon-search" style="line-height:32px;margin-left:6px;color:#999;"></i>
            </el-input>
            <span style="margin-left:auto;font-size:12px;color:#999;">共 {{ totalItems }} 件商品</span>
        </div>

        <!-- 猜你喜欢 -->
        <div v-if="recommendList.length" class="recommend-section">
            <h4 class="section-title">猜你喜欢</h4>
            <div class="product-grid">
                <div class="product-card recommend-card" v-for="item in recommendList" :key="'r'+item.id" @click="goDetail(item.id)">
                    <span class="recommend-badge">推荐</span>
                    <div class="card-img">
                        <img :src="getCover(item.coverList)" @error="onImgError" />
                    </div>
                    <div class="card-body">
                        <div class="card-name">{{ item.name }}</div>
                        <div class="card-price">¥{{ item.price }}</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 商品卡片网格 -->
        <div v-if="tableData.length" class="product-grid">
            <div class="product-card" v-for="item in tableData" :key="item.id" @click="goDetail(item.id)">
                <div class="card-img">
                    <img :src="getCover(item.coverList)" @error="onImgError" />
                    <el-tag size="mini" class="level-tag" :type="levelType(item.oldLevel)">{{ levelText(item.oldLevel) }}</el-tag>
                </div>
                <div class="card-body">
                    <div class="card-name">{{ item.name }}</div>
                    <div class="card-price">¥{{ item.price }}</div>
                    <div class="card-footer">
                        <span class="card-time">{{ timeAgo(item.createTime) }}</span>
                    </div>
                </div>
            </div>
        </div>
        <div v-else class="empty-state">
            <i class="el-icon-goods" style="font-size:48px;color:#ddd;"></i>
            <p>暂无商品</p>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrap" v-if="totalItems > 0">
            <el-pagination background @current-change="handleCurrentChange" :current-page="currentPage"
                :page-size="pageSize" layout="total, prev, pager, next" :total="totalItems">
            </el-pagination>
        </div>
    </div>
</template>

<script>
import { getSearchKey, setSearchKey } from "@/utils/storage";
import { timeAgo } from "@/utils/data";
export default {
    name: 'Product',
    data() {
        return {
            categoryList: [], parentCategories: [], subCategories: [],
            selectedParent: null,
            query: { name: '', categoryId: null, oldLevel: null },
            priceMin: '', priceMax: '',
            tableData: [], currentPage: 1, pageSize: 12, totalItems: 0,
            recommendList: [],
            filterTimer: null,
        };
    },
    created() {
        const key = getSearchKey();
        if (key) { this.query.name = key; setSearchKey(''); }
        this.loadCategories();
        this.fetchProducts();
        this.loadRecommend();
    },
    methods: {
        getCover(c) { return this.$imgUrl(c ? c.split(',')[0] : null); },
        levelText(l) { return {1:'全新',2:'几乎全新',3:'有使用痕迹',4:'较旧'}[l]||''; },
        levelType(l) { return {1:'success',2:'',3:'warning',4:'danger'}[l]||''; },
        timeAgo,
        toggleCategory(id) {
            this.query.categoryId = this.query.categoryId === id ? null : id;
            this.subCategories = [];
            this.selectedParent = null;
            this.handleFilter();
        },
        selectParent(parentId) {
            if (this.selectedParent === parentId) {
                this.selectedParent = null;
                this.subCategories = [];
                this.query.categoryId = null;
            } else {
                this.selectedParent = parentId;
                this.subCategories = this.categoryList.filter(c => c.parentId === parentId);
                this.query.categoryId = null;
            }
            this.handleFilter();
        },
        toggleSubCategory(subId) {
            this.query.categoryId = this.query.categoryId === subId ? null : subId;
            this.handleFilter();
        },
        debounceFilter() {
            clearTimeout(this.filterTimer);
            this.filterTimer = setTimeout(() => this.handleFilter(), 400);
        },
        loadCategories() {
            this.$axios.post('/category/query', { current: 1, size: 100, isUse: true }).then(res => {
                if (res.data.code === 200) {
                    this.categoryList = res.data.data;
                    this.parentCategories = res.data.data.filter(c => c.parentId === 0 || !c.parentId);
                }
            });
        },
        fetchProducts() {
            const params = { current: this.currentPage, size: this.pageSize };
            if (this.query.name) params.key = this.query.name;
            if (this.query.categoryId) params.categoryId = this.query.categoryId;
            if (this.query.oldLevel) params.oldLevel = this.query.oldLevel;
            if (this.priceMin) params.minPrice = parseFloat(this.priceMin);
            if (this.priceMax) params.maxPrice = parseFloat(this.priceMax);
            this.$axios.post('/product/query', params).then(res => {
                if (res.data.code === 200) {
                    this.tableData = res.data.data;
                    this.totalItems = res.data.total;
                }
            }).catch(() => {});
        },
        handleFilter() { this.currentPage = 1; this.fetchProducts(); },
        handleCurrentChange(v) { this.currentPage = v; this.fetchProducts(); },
        onImgError(e) { e.target.src = 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 200 160%22%3E%3Crect fill=%22%23f5f5f5%22 width=%22200%22 height=%22160%22/%3E%3Ctext x=%22100%22 y=%2285%22 text-anchor=%22middle%22 fill=%22%23ccc%22 font-size=%2216%22%3E暂无图片%3C/text%3E%3C/svg%3E'; },
        loadRecommend() {
            this.$axios.get('/product/recommend?limit=4').then(res => {
                if (res.data.code === 200) this.recommendList = res.data.data;
            }).catch(() => {});
        },
        goDetail(id) { this.$router.push('/product/detail/' + id); },
    }
};
</script>

<style scoped lang="scss">
.product-page { background: #fff; border-radius: 8px; padding: 20px; min-height: 400px; }

.breadcrumb { margin-bottom: 12px; font-size: 13px; color: #999; }
.crumb-link { cursor: pointer; color: #666; }
.crumb-link:hover { color: #409EFF; }
.crumb-sep { margin: 0 6px; }
.crumb-current { color: #333; }

.tag-filter { display: flex; gap: 10px; margin-bottom: 14px; flex-wrap: wrap; }
.filter-tag { padding: 6px 18px; border-radius: 20px; cursor: pointer; font-size: 13px; color: #666; background: #f5f5f5; transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1); user-select: none; }
.filter-tag:hover { background: #e8e8e8; transform: translateY(-1px); }
.filter-tag:active { transform: scale(0.96); }
.filter-tag-active { background: #409EFF; color: #fff; box-shadow: 0 2px 8px rgba(64,158,255,.35); }
.filter-tag-active:hover { background: #337ecc; }
.filter-tag-sm { padding: 4px 14px; font-size: 12px; }
.filter-bar { display: flex; align-items: center; margin-bottom: 20px; padding-bottom: 15px; border-bottom: 1px solid #f0f0f0; }
.recommend-section { margin-bottom: 24px; }
.section-title { font-size: 16px; color: #333; margin-bottom: 12px; padding: 2px 0 2px 10px; border-left: 3px solid #409EFF; }
.recommend-card { position: relative; border: 1.5px dashed #409EFF; background: linear-gradient(135deg, #f8fbff, #f0f6ff); }
.recommend-badge { position: absolute; top: 0; left: 0; z-index: 5; background: linear-gradient(135deg, #409EFF, #66b1ff); color: #fff; font-size: 11px; padding: 3px 12px; border-radius: 10px 0 10px 0; font-weight: 600; letter-spacing: 1px; }

.product-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 18px; }
.product-card {
    border-radius: 12px; overflow: hidden; cursor: pointer;
    border: 1px solid #eef0f2; background: #fff;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 1px 3px rgba(0,0,0,.04);
}
.product-card:hover { transform: translateY(-4px); box-shadow: 0 12px 28px rgba(0,0,0,.1); }
.product-card:active { transform: scale(0.98); }
.card-img { position: relative; height: 180px; overflow: hidden; background: #f5f5f5; }
.card-img img { width: 100%; height: 100%; object-fit: cover; }
.level-tag { position: absolute; top: 8px; right: 8px; }
.card-body { padding: 12px; }
.card-name { font-size: 14px; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-bottom: 6px; }
.card-price { font-size: 20px; color: #f56c6c; font-weight: bold; margin-bottom: 6px; }
.card-footer { display: flex; justify-content: space-between; align-items: center; }
.card-time { font-size: 11px; color: #bbb; }

.empty-state { text-align: center; padding: 80px 0; color: #999; }

.pagination-wrap { text-align: center; margin-top: 30px; }
</style>
