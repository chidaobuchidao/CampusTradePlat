import Vue from "vue";
import VueRouter from "vue-router";
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import { getToken, clearToken } from "@/utils/storage.js";
import echarts from 'echarts';
Vue.prototype.$echarts = echarts;
Vue.use(ElementUI);
Vue.use(VueRouter);

const routes = [
  // 登录/注册 — 独立页面，无导航栏
  { path: "/login", component: () => import(`@/views/login/Login.vue`) },
  { path: "/register", component: () => import(`@/views/register/Register.vue`) },

  // 管理端 — 独立布局
  {
    path: "/admin",
    component: () => import(`@/views/admin/Home.vue`),
    meta: { requireAuth: true, role: 1 },
    children: [
      { path: "/adminLayout", name: '仪表盘', icon: 'el-icon-pie-chart', component: () => import(`@/views/admin/Main.vue`), meta: { requireAuth: true } },
      { path: "/userManage", name: '用户管理', icon: 'el-icon-user-solid', component: () => import(`@/views/admin/User.vue`), meta: { requireAuth: true } },
      { path: "/productManage", name: '商品管理', icon: 'el-icon-goods', component: () => import(`@/views/admin/Product.vue`), meta: { requireAuth: true } },
      { path: "/categoryManage", name: '商品类别管理', icon: 'el-icon-files', component: () => import(`@/views/admin/Category.vue`), meta: { requireAuth: true } },
      { path: "/evaluations", name: '评论管理', icon: 'el-icon-chat-dot-round', component: () => import(`@/views/admin/Evaluations.vue`), meta: { requireAuth: true } },
    ]
  },

  // 用户端/公开 — 统一使用 Home.vue 导航栏布局
  {
    path: "/",
    component: () => import(`@/views/user/Home.vue`),
    children: [
      // 公开页面
      { path: "", redirect: "/product" },
      { path: "product", name: '商品', component: () => import(`@/views/user/Product.vue`) },
      { path: "product/detail/:id", component: () => import(`@/views/user/ProductDetail.vue`) },
      { path: "user/:id", component: () => import(`@/views/user/UserProfile.vue`) },
      { path: "search", component: () => import(`@/views/user/Search.vue`) },
      // 公开
      { path: "forum", name: '论坛', component: () => import(`@/views/user/Forum.vue`) },
      { path: "forum/detail/:id", component: () => import(`@/views/user/ForumDetail.vue`) },
      // 需登录的页面
      { path: "myProduct", name: '我的商品', component: () => import(`@/views/user/MyProduct.vue`), meta: { requireAuth: true } },
      { path: "mySave", name: '我的收藏', component: () => import(`@/views/user/MySave.vue`), meta: { requireAuth: true } },
      { path: "myView", name: '足迹', component: () => import(`@/views/user/MyView.vue`), meta: { requireAuth: true } },
      { path: "orders", name: '订单', component: () => import(`@/views/user/Orders.vue`), meta: { requireAuth: true } },
      { path: "message", name: '消息', component: () => import(`@/views/user/Message.vue`), meta: { requireAuth: true } },
      { path: "chat/:userId", component: () => import(`@/views/user/Chat.vue`), meta: { requireAuth: true } },
      { path: "myself", name: '个人中心', component: () => import(`@/views/user/Myself.vue`), meta: { requireAuth: true } },
      { path: "post-product", name: '发布商品', component: () => import(`@/views/user/PostProduct.vue`), meta: { requireAuth: true } },
      { path: "post-forum", name: '发帖', component: () => import(`@/views/user/PostForum.vue`), meta: { requireAuth: true } },
    ]
  },
];

const router = new VueRouter({
  routes,
  mode: 'history'
});

router.beforeEach((to, from, next) => {
  if (to.path === '/login' || to.path === '/register') {
    return next();
  }

  if (to.matched.some(record => record.meta.requireAuth)) {
    const token = getToken();
    if (!token) {
      return next({ path: '/login', query: { redirect: to.fullPath } });
    }
    try {
      const role = parseInt(sessionStorage.getItem('role'));
      // 管理端路由仅限 role=1
      if (to.matched.some(r => r.path === '/admin') && role !== 1) {
        clearToken();
        return next("/login");
      }
      return next();
    } catch (error) {
      console.error('权限检查失败:', error);
      return next('/login');
    }
  }

  next();
});
export default router;
