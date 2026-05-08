<template>
    <div class="search-page">
        <div class="breadcrumb">
            <span @click="$router.push('/product')" class="crumb-link">首页</span>
            <span class="crumb-sep">/</span>
            <span class="crumb-current">搜索结果</span>
        </div>

        <h3 v-if="searchKey" style="margin:15px 0;">搜索 "{{ searchKey }}" 的结果</h3>

        <!-- 精确匹配 -->
        <div v-if="exactList.length" class="product-grid">
            <div class="product-card" v-for="item in exactList" :key="item.id" @click="goDetail(item.id)">
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
        <div v-else style="text-align:center;padding:40px 0;color:#999;">
            未找到匹配的商品
        </div>

        <!-- 标签联想推荐 -->
        <div v-if="relatedList.length" class="related-section">
            <h4 class="section-title">相关推荐 · {{ tagName }}</h4>
            <div class="product-grid">
                <div class="product-card" v-for="item in relatedList" :key="'r'+item.id" @click="goDetail(item.id)">
                    <div class="card-img">
                        <img :src="getCover(item.coverList)" @error="onImgError" />
                        <el-tag size="mini" class="level-tag" :type="levelType(item.oldLevel)">{{ levelText(item.oldLevel) }}</el-tag>
                    </div>
                    <div class="card-body">
                        <div class="card-name">{{ item.name }}</div>
                        <div class="card-price">¥{{ item.price }}</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { getSearchKey } from "@/utils/storage";
import { timeAgo } from "@/utils/data";
export default {
    name: 'Search',
    data() {
        return { searchKey: '', exactList: [], relatedList: [], tagName: '' };
    },
    created() { this.doSearch(); },
    watch: { '$route'() { this.doSearch(); } },
    methods: {
        doSearch() {
            this.searchKey = getSearchKey();
            this.fetchData();
        },
        getCover(c) { return this.$imgUrl(c ? c.split(',')[0] : null); },
        onImgError(e) { e.target.src = 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 200 160%22%3E%3Crect fill=%22%23f5f5f5%22 width=%22200%22 height=%22160%22/%3E%3Ctext x=%22100%22 y=%2285%22 text-anchor=%22middle%22 fill=%22%23ccc%22 font-size=%2216%22%3E暂无图片%3C/text%3E%3C/svg%3E'; },
        levelText(l) { return {1:'全新',2:'几乎全新',3:'有使用痕迹',4:'较旧'}[l]||''; },
        levelType(l) { return {1:'success',2:'',3:'warning',4:'danger'}[l]||''; },
        timeAgo,
        goDetail(id) { this.$router.push('/product/detail/' + id); },
        fetchData() {
            if (!this.searchKey) return;
            this.$axios.post('/product/search', { keyword: this.searchKey, current: 1, size: 50 }).then(res => {
                if (res.data.code === 200) {
                    const d = res.data.data;
                    this.exactList = d.exact || [];
                    this.relatedList = d.related || [];
                    this.tagName = d.tag || '';
                }
            });
        },
    }
};
</script>

<style scoped lang="scss">
.search-page { background: #fff; border-radius: 8px; padding: 20px; min-height: 400px; }
.breadcrumb { margin-bottom: 12px; font-size: 13px; color: #999; }
.crumb-link { cursor: pointer; color: #666; }
.crumb-link:hover { color: #409EFF; }
.crumb-sep { margin: 0 6px; }
.crumb-current { color: #333; }

.product-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.product-card { border-radius: 12px; overflow: hidden; cursor: pointer; border: 1px solid #eef0f2; background: #fff; transition: all 0.3s cubic-bezier(0.4,0,0.2,1); box-shadow: 0 1px 3px rgba(0,0,0,.04); }
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

.related-section { margin-top: 30px; padding-top: 20px; border-top: 1px solid #f0f0f0; }
.section-title { font-size: 16px; color: #333; margin-bottom: 12px; padding: 2px 0 2px 10px; border-left: 3px solid #E6A23C; }
</style>
