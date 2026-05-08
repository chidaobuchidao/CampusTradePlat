<template>
    <div class="register-containel">
        <div class="register-panel">
            <h1>注册新身份</h1>
            <div class="text">
                <input v-model="act" class="act" placeholder="新账号" />
            </div>
            <div class="text">
                <input v-model="name" class="act" placeholder="新用户名" />
            </div>
            <div class="text">
                <input v-model="pwd" class="pwd" type="password" placeholder="输入密码" />
            </div>
            <div class="text">
                <input v-model="pwdConfirm" class="pwd" type="password" placeholder="确认密码" />
            </div>
            <div>
                <span class="register-btn" @click="registerFunc">立即注册</span>
            </div>
            <div class="tip">
                <p>已有账户？<span class="no-act" @click="toDoLogin">返回登录</span></p>
            </div>
        </div>
    </div>
</template>

<script>
const DELAY_TIME = 1300;
import request from "@/utils/request.js";
import md5 from 'js-md5';
import Logo from '@/components/Logo.vue';
export default {
    name: "Register",
    components: { Logo },
    data() {
        return {
            act: '', // 账号
            pwd: '', // 密码
            pwdConfirm: '', // 确认密码
            name: '' // 用户名
        }
    },
    methods: {
        // 返回登录页面
        toDoLogin() {
            this.$router.push('/login');
        },
        async registerFunc() {
            if (!this.act || !this.pwd || !this.pwdConfirm || !this.name ) {
                this.$swal.fire({
                    title: '填写校验',
                    text: '账号或密码或用户名不能为空',
                    icon: 'error',
                    showConfirmButton: false,
                    timer: DELAY_TIME,
                });
                return;
            }
            if (this.pwd !== this.pwdConfirm) {
                this.$swal.fire({
                    title: '填写校验',
                    text: '前后密码输入不一致',
                    icon: 'error',
                    showConfirmButton: false,
                    timer: DELAY_TIME,
                });
                return;
            }
            const hashedPwd = md5(md5(this.pwd));
            const paramDTO = { userAccount: this.act, userPwd: hashedPwd, userName: this.name };
            try {
                const { data } = await request.post(`user/register`, paramDTO);
                if (data.code !== 200) {
                    this.$swal.fire({
                        title: '注册失败',
                        text: data.msg,
                        icon: 'error',
                        showConfirmButton: false,
                        timer: DELAY_TIME,
                    });
                    return;
                }
                // 使用Swal通知注册成功，延迟后跳转
                this.$swal.fire({
                    title: '注册成功',
                    text: '即将返回登录页...',
                    icon: 'success',
                    showConfirmButton: false,
                    timer: DELAY_TIME,
                });
                // 根据角色延迟跳转
                setTimeout(() => {
                    this.$router.push('/login');
                }, DELAY_TIME);
            } catch (error) {
                console.error('注册请求错误:', error);
            }
        }
    }
};
</script>

<style lang="scss" scoped>
* { user-select: none; }

.register-containel {
    width: 100%; min-height: 100vh;
    background: linear-gradient(135deg, #1a1a2e 0%, #16213e 40%, #0f3460 70%, #1a5276 100%);
    display: flex; justify-content: center; align-items: center; flex-direction: column;
    position: relative; overflow: hidden;

    .register-panel {
        margin: 0 auto; width: 380px; height: auto; padding: 40px 36px 24px;
        border-radius: 16px; background: #fff; position: relative; z-index: 1;
        box-shadow: 0 20px 60px rgba(0,0,0,.3);
    }

    h1 { text-align: center; color: #1a1a2e; margin-bottom: 28px; font-size: 22px; }

    .act, .pwd {
        margin: 6px 0; height: 46px; line-height: 46px; width: 100%;
        padding: 0 16px; background: #f5f7fa; box-sizing: border-box;
        border: 2px solid #f5f7fa; border-radius: 10px; font-size: 15px;
        margin-top: 14px; transition: all .3s;
    }
    .act:focus, .pwd:focus { outline: none; border-color: #409EFF; background: #fff; }

    .register-btn {
        display: inline-block; text-align: center; border-radius: 10px;
        margin-top: 24px; height: 46px; line-height: 46px; width: 100%;
        background: linear-gradient(135deg, #409EFF, #337ecc);
        font-size: 15px; border: none; color: #fff; padding: 0;
        cursor: pointer; user-select: none; transition: all .3s; font-weight: 600; letter-spacing: 2px;
    }
    .register-btn:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(64,158,255,.4); }

    .tip { margin: 20px 0 0; text-align: center;
        p { padding: 3px 0; font-size: 14px; margin: 0; color: #909399; }
        .no-act { color: #409EFF; cursor: pointer; font-weight: 500; }
        .no-act:hover { color: #337ecc; }
    }
}
</style>
