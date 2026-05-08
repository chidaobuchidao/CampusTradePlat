<template>
    <div class="post-container">
        <h3 style="margin-bottom:20px;">发帖</h3>
        <el-form label-width="80px">
            <el-form-item label="标题">
                <el-input v-model="form.title" placeholder="请输入标题" maxlength="100"></el-input>
            </el-form-item>
            <el-form-item label="分类">
                <el-select v-model="form.category" placeholder="选择分类">
                    <el-option v-for="c in categories" :key="c" :label="c" :value="c"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="内容">
                <Editor :receiveContent="form.content" @on-receive="onEditorChange" height="350px" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="submit" :loading="submitting">发布帖子</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
import Editor from "@/components/Editor.vue";
export default {
    name: 'PostForum',
    components: { Editor },
    data() {
        return {
            categories: ['交流', '求助', '交易', '其他'],
            form: { title: '', category: '', content: '' },
            submitting: false,
        };
    },
    methods: {
        onEditorChange(html) { this.form.content = html; },
        submit() {
            if (!this.form.title) { this.$message.error('请输入标题'); return; }
            if (!this.form.content || this.form.content === '<p>创作内容</p>') { this.$message.error('请输入内容'); return; }
            if (!this.form.category) this.form.category = '交流';
            this.submitting = true;
            this.$axios.post('/forum/save', this.form).then(res => {
                if (res.data.code === 200) { this.$message.success('发帖成功'); this.$router.push('/forum'); }
                else { this.$message.error(res.data.msg); }
            }).finally(() => { this.submitting = false; });
        },
    }
};
</script>

<style scoped>
.post-container { background: #fff; padding: 20px; border-radius: 8px; max-width: 800px; }
</style>
