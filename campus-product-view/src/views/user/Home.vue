<template>
    <div>
        <div class="container">
            <div class="top">
                <div class="nav">
                    <div>
                        <Logo name="校园交易" />
                    </div>
                    <div class="route">
                        <span @click="handleRouteSelect('/product')">商品</span>
                        <span v-if="loginStatus" @click="handleRouteSelect('/myProduct')">我的商品</span>
                        <span v-if="loginStatus" @click="handleRouteSelect('/mySave')">我的收藏</span>
                        <span v-if="loginStatus" @click="handleRouteSelect('/myView')">足迹</span>
                    </div>
                </div>
                <div
                    style="cursor: pointer;font-size: 14px;display: flex;justify-content: left;align-items: center;gap: 20px;color: rgb(143, 143, 143);">
                    <div class="word-search">
                        <div class="item">
                            <input type="text" placeholder="搜索商品" v-model="key">
                            <i class="el-icon-search" @click="fetch"></i>
                        </div>
                    </div>
                    <div style="text-align: center;" v-if="loginStatus" @click="handleRouteSelect('/orders')">
                        <div>
                            <i class="el-icon-document"></i>
                        </div>
                        <div>
                            订单
                        </div>
                    </div>
                    <div style="text-align: center;" v-if="loginStatus" @click="handleRouteSelect('/message')">
                        <div>
                            <i class="el-icon-bell"></i>
                        </div>
                        <div>
                            通知
                        </div>
                    </div>
                    <div style="text-align: center;" v-if="loginStatus" @click="handleRouteSelect('/post-product')">
                        <div>
                            <i class="el-icon-plus"></i>
                        </div>
                        <div>
                            发布商品
                        </div>
                    </div>
                    <div v-if="!loginStatus" @click="loginOperation">
                        登录&nbsp;|&nbsp;注册
                    </div>
                    <div v-else>
                        <img @click="handleRouteSelect('/myself')" class="avatar" :src="userInfo.userAvatar">
                    </div>
                </div>
            </div>
            <div class="info" v-if="loginStatus">
                <div>
                    <img :src="userInfo.userAvatar" style="width: 90px;width: 90px;border-radius: 50%;">
                </div>
                <div style="padding: 0 40px;">
                    <div class="title1">
                        <span class="title">{{ userInfo.userName }}</span>
                        <!-- <span class="poin">商品被浏览138</span>
                        <span class="poin">商品被收藏290</span> -->
                    </div>
                    <!-- <div class="save">
                        收藏商品： 1
                    </div> -->
                    <div class="date">
                        注册于： {{ userInfo.createTime }}
                    </div>
                </div>
            </div>
        </div>
        <div style="padding: 10px 300px;">
            <router-view></router-view>
        </div>
    </div>
</template>
<script>
import Logo from "@/components/Logo";
import { getToken, setUserInfo, setSearchKey } from "@/utils/storage";
export default {
    name: "Home",
    components: {
        Logo,
    },
    data() {
        return {
            selfPath: { name: '个人中心', path: '/mySelf' },
            userRoutes: [],
            key: null,
            dialogOperaion: false,
            loginStatus: false, // 默认未登录
            userInfo: {},
            searchPath: '/search',
        };
    },
    created() {
        this.loadLoginStatus();
        this.handleRouteSelect('/product')
    },
    methods: {
        // 跳转登录页
        loginOperation() {
            this.$router.push('/login');
        },
        // 搜索
        fetch() {
            setSearchKey(this.key);
            this.handleRouteSelect(this.searchPath);
        },
        // 路由切换
        handleRouteSelect(path) {
            if (this.$router.currentRoute.fullPath !== path) {
                this.$router.push(path);
            }
        },
        //判断用户是否已经登录
        loadLoginStatus() {
            const token = getToken();
            // 没登录
            if (token === null) {
                this.loginStatus = false;
                return;
            }
            this.auth();
        },
        // token检验
        auth() {
            this.$axios.get(`/user/auth`).then(res => {
                const { data } = res;
                if (data.code === 200) {
                    // 存储用户信息
                    setUserInfo(data.data);
                    this.userInfo = data.data;
                }
                this.loginStatus = data.code === 200;
            }).catch(error => {
                console.log("token检验异常：", error);
            });
        },
    }
};
</script>
<style scoped lang="scss">
.avatar {
    border: 3px solid rgb(51, 51, 51);
    width: 30px;
    width: 30px;
    border-radius: 50%;
}

.avatar:hover {
    border: 3px solid rgb(216, 206, 25);
}

.word-search {
    display: flex;
    justify-content: center;

    .item {
        max-width: 210px;
        background-color: rgb(244, 244, 244);
        border-radius: 20px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        box-sizing: border-box;
        border: 1px solid rgb(245, 245, 245);

        input:focus {
            width: 110px;
        }

        input {
            border: none;
            transition: all 0.6s;
            width: 80px;
            margin: 0 10px;
            background-color: rgb(244, 244, 244);
            outline: none;
            font-size: 14px;
        }

        i {
            padding: 6px 12px;
            border-radius: 5px;
            cursor: pointer;
            border-top-right-radius: 20px;
            border-bottom-right-radius: 20px;
            background-color: rgb(244, 244, 244);
        }

        i:hover {
            background-color: rgb(241, 241, 241);
        }
    }

}

.container {
    background-color: rgb(51, 51, 51);
}

.info {
    padding: 10px 200px;
    display: flex;

    img {
        padding: 6px;
        box-sizing: border-box;
        border: 3px solid rgb(255, 255, 255);
    }

    .title1 {
        font-size: 26px;
        color: rgb(255, 255, 255);
        gap: 12px;

        .poin {
            display: inline-block;
            padding: 2px 6px;
            margin-inline: 4px;
            box-sizing: border-box;
            border-radius: 20px;
            cursor: pointer;
            font-size: 10px;
            color: rgb(255, 255, 255);
            background-color: rgb(197, 153, 57);
        }
    }

    .save,
    .date {
        margin: 10px 0;
        font-size: 12px;
        color: rgb(153, 150, 141);
    }

}

.top {

    height: 80px;
    padding: 0 200px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .nav {
        padding: 20px 0;
        display: flex;
        justify-content: left;
        align-items: center;
        gap: 20px;

        .route {
            display: flex;
            justify-content: left;
            align-items: center;
            gap: 30px;


            span {
                font-size: 14px;
                color: rgb(143, 143, 143);
                cursor: pointer;
            }

            span:hover {
                color: rgb(255, 255, 255);
            }
        }

    }
}
</style>