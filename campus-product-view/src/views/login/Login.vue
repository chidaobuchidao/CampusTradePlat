<template>
    <div class="login-container">
        <!-- <div style="display: flex;justify-content: left;margin: 20px 0;">
            <img src="/logo.jpg" style="width: 220px;height: 100px;">
        </div> -->
        <div class="login-panel">
            <div class="login-illustration">
                <img src="/bag.png" />
            </div>
            <div class="right-login">
                <div>
                    <h2>校园二手交易平台</h2>
                </div>
                <div class="text">
                    <input v-model="act" class="act" placeholder="输入账号" />
                </div>
                <div class="text">
                    <input v-model="pwd" class="pwd" type="password" placeholder="输入密码" />
                </div>
                <div>
                    <span class="login-btn" @click="login">立即登录</span>
                </div>
                <div class="tip">
                    <p>没有账号？<span class="no-act" @click="toDoRegister">点此注册</span></p>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
const DELAY_TIME = 1300;
import request from "@/utils/request.js";
import { setToken } from "@/utils/storage.js";
import md5 from 'js-md5';
import Logo from '@/components/Logo.vue';
export default {
    name: "Login",
    components: { Logo },
    data() {
        return {
            act: '',
            pwd: '',
            colorLogo: 'rgb(38,38,38)',
        }
    },
    methods: {
        // 跳转注册页面
        toDoRegister() {
            this.$router.push('/register');
        },
        async login() {
            if (!this.act || !this.pwd) {
                this.$swal.fire({
                    title: '填写校验',
                    text: '账号或密码不能为空',
                    icon: 'error',
                    showConfirmButton: false,
                    timer: DELAY_TIME,
                });
                return;
            }
            const hashedPwd = md5(md5(this.pwd));
            const paramDTO = { userAccount: this.act, userPwd: hashedPwd };
            try {
                const { data } = await request.post(`user/login`, paramDTO);
                if (data.code !== 200) {
                    this.$swal.fire({
                        title: '登录失败',
                        text: data.msg,
                        icon: 'error',
                        showConfirmButton: false,
                        timer: DELAY_TIME,
                    });
                    return;
                }
                setToken(data.data.token);
                // 根据角色延迟跳转
                setTimeout(() => {
                    const { role } = data.data;
                    sessionStorage.setItem('role', role);
                    this.navigateToRole(role);
                }, DELAY_TIME);
            } catch (error) {
                console.error('登录请求错误:', error);
                this.$message.error('登录请求出错，请重试！');
            }
        },
        navigateToRole(role) {
            const redirect = this.$route.query.redirect;
            switch (role) {
                case 1:
                    this.$router.push(redirect || '/adminLayout');
                    break;
                case 2:
                    this.$router.push(redirect || '/product');
                    break;
                default:
                    this.$router.push('/product');
                    break;
            }
        },
    }
};
</script>

<style lang="scss" scoped>
* { user-select: none; }

.login-container {
    width: 100%; min-height: 100vh;
    background: linear-gradient(135deg, #1a1a2e 0%, #16213e 40%, #0f3460 70%, #1a5276 100%);
    display: flex; justify-content: center; align-items: center; flex-direction: column;
    position: relative; overflow: hidden;

    &::before {
        content: ''; position: absolute; width: 600px; height: 600px;
        border-radius: 50%; background: rgba(255,208,75,.04);
        top: -200px; right: -100px;
    }
    &::after {
        content: ''; position: absolute; width: 400px; height: 400px;
        border-radius: 50%; background: rgba(64,158,255,.05);
        bottom: -100px; left: -80px;
    }

    .login-panel {
        display: flex; padding: 0; justify-content: center; height: auto;
        border-radius: 16px; overflow: hidden; position: relative; z-index: 1;
        background: #fff;
        box-shadow: 0 20px 60px rgba(0,0,0,.3);

        .login-illustration {
            width: 320px; display: flex; align-items: center; justify-content: center;
            background: linear-gradient(135deg, #f8f9ff, #e8ecf4);
            img { width: 260px; }
        }
        .right-login {
            width: 340px; padding: 40px 36px;
            h2 { text-align: center; color: #1a1a2e; margin-bottom: 30px; }
        }

        .act, .pwd {
            margin: 6px 0; height: 48px; line-height: 48px; width: 100%;
            font-size: 15px; padding: 0 16px; background: #f5f7fa;
            box-sizing: border-box; border: 2px solid #f5f7fa; border-radius: 10px; margin-top: 14px;
            transition: all .3s;
        }
        .act:focus, .pwd:focus { outline: none; border-color: #409EFF; background: #fff; }
        .act:hover, .pwd:hover { background: #eef1f6; }
    }

    .login-btn {
        display: inline-block; text-align: center; border-radius: 10px;
        margin-top: 24px; height: 46px; line-height: 46px; width: 100%;
        background: linear-gradient(135deg, #409EFF, #337ecc); font-size: 15px;
        border: none; color: white; padding: 0; cursor: pointer;
        user-select: none; transition: all .3s; font-weight: 600; letter-spacing: 2px;
    }
    .login-btn:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(64,158,255,.4); }

    .tip {
        margin: 24px 0 0; text-align: center;
        p { padding: 3px 0; margin: 0; font-size: 14px; color: #909399; }
        .no-act { color: #409EFF; cursor: pointer; font-weight: 500; }
        .no-act:hover { color: #337ecc; }
    }
}
</style>
