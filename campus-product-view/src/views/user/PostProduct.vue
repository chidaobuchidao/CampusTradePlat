<template>
    <div class="post-container">
        <h3 style="margin-bottom:20px;">发布商品</h3>
        <el-form label-width="100px">
            <el-form-item label="商品名称">
                <el-input v-model="form.name" placeholder="请输入商品名称" maxlength="50"></el-input>
            </el-form-item>
            <el-form-item label="商品类别">
                <el-select v-model="form.categoryId" placeholder="请选择类别" style="width:260px;">
                    <el-option-group v-for="group in categoryGroups" :key="group.id" :label="group.name">
                        <el-option v-for="sub in group.children" :key="sub.id" :label="sub.name" :value="sub.id"></el-option>
                    </el-option-group>
                </el-select>
            </el-form-item>
            <el-form-item label="新旧程度">
                <el-select v-model="form.oldLevel" placeholder="请选择">
                    <el-option label="全新" :value="1"></el-option>
                    <el-option label="几乎全新" :value="2"></el-option>
                    <el-option label="有使用痕迹" :value="3"></el-option>
                    <el-option label="较旧" :value="4"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="价格">
                <el-input-number v-model="form.price" :min="0.01" :precision="2" placeholder="元"></el-input-number>
            </el-form-item>
            <el-form-item label="库存数量">
                <el-input-number v-model="form.inventory" :min="1" :step="1"></el-input-number>
            </el-form-item>
            <el-form-item label="支持砍价">
                <el-switch v-model="form.isBargain"></el-switch>
            </el-form-item>
            <el-form-item label="封面图片">
                <el-upload action="http://localhost:21090/api/campus-product-sys/v1.0/file/upload"
                    list-type="picture-card" :on-success="onUploadSuccess" :on-remove="onUploadRemove"
                    :headers="uploadHeaders" accept="image/*">
                    <i class="el-icon-plus"></i>
                </el-upload>
            </el-form-item>
            <el-form-item label="商品描述">
                <Editor :receiveContent="form.detail" @on-receive="onEditorChange" height="300px" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="submit" :loading="submitting">发布商品</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
import Editor from "@/components/Editor.vue";
import { getToken } from "@/utils/storage";
export default {
    name: 'PostProduct',
    components: { Editor },
    data() {
        return {
            categoryGroups: [],
            coverImages: [],
            form: { name: '', categoryId: null, oldLevel: null, price: 0, inventory: 1, isBargain: false, coverList: '', detail: '' },
            submitting: false,
        };
    },
    computed: {
        uploadHeaders() { return { token: getToken() }; }
    },
    created() {
        this.$axios.post('/category/query', { current: 1, size: 100, isUse: true }).then(res => {
            if (res.data.code === 200) {
                const all = res.data.data;
                const parents = all.filter(c => c.parentId === 0 || !c.parentId);
                this.categoryGroups = parents.map(p => ({
                    id: p.id, name: p.name,
                    children: all.filter(c => c.parentId === p.id)
                }));
            }
        });
    },
    methods: {
        onUploadSuccess(res) { if (res.code === 200) this.coverImages.push(res.data || res.msg); },
        onUploadRemove(file) {
            const url = file.response ? file.response.data : file.url;
            this.coverImages = this.coverImages.filter(u => u !== url);
        },
        onEditorChange(html) { this.form.detail = html; },
        submit() {
            if (!this.form.name) { this.$message.error('请输入商品名称'); return; }
            if (!this.form.categoryId) { this.$message.error('请选择商品类别'); return; }
            if (!this.form.price || this.form.price <= 0) { this.$message.error('请输入有效价格'); return; }
            if (!this.coverImages.length) { this.$message.error('请上传封面图片'); return; }
            this.form.coverList = this.coverImages.join(',');
            this.submitting = true;
            this.$axios.post('/product/save', this.form).then(res => {
                if (res.data.code === 200) {
                    this.$message.success('发布成功');
                    this.$router.push('/myProduct');
                } else {
                    this.$message.error(res.data.msg);
                }
            }).catch(() => { this.$message.error('发布失败'); })
            .finally(() => { this.submitting = false; });
        },
    }
};
</script>

<style scoped>
.post-container { background: #fff; padding: 20px; border-radius: 8px; max-width: 800px; }
</style>
