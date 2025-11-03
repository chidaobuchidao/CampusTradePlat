import Vue from "vue";
import VueRouter from "vue-router";
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import { getToken } from "@/utils/storage.js";
import echarts from 'echarts';
Vue.prototype.$echarts = echarts;
Vue.use(ElementUI);
Vue.use(VueRouter);

const routes = [
  { path: "/", component: () => import(`@/views/user/Home.vue`) },
  { path: "/login", component: () => import(`@/views/login/Login.vue`) },
  { path: "/register", component: () => import(`@/views/register/Register.vue`) },
  {
    path: "/admin",
    component: () => import(`@/views/admin/Home.vue`),
    meta: { requireAuth: true },
    children: [
      { path: "/adminLayout", name: '仪表盘', icon: 'el-icon-pie-chart', component: () => import(`@/views/admin/Main.vue`), meta: { requireAuth: true } },
      { path: "/userManage", name: '用户管理', icon: 'el-icon-user-solid', component: () => import(`@/views/admin/User.vue`), meta: { requireAuth: true } },
      { path: "/categoryManage", name: '商品类别管理', icon: 'el-icon-files', component: () => import(`@/views/admin/Category.vue`), meta: { requireAuth: true } },
      { path: "/evaluations", name: '评论管理', icon: 'el-icon-chat-dot-round', component: () => import(`@/views/admin/Evaluations.vue`), meta: { requireAuth: true } },
    ]
  },
  {
    path: "/user",
    component: () => import(`@/views/user/Home.vue`),
    meta: { requireAuth: true },
    children: [
      { name: '商品', path: "/product", component: () => import(`@/views/user/Product.vue`), meta: { requireAuth: true } },
      { name: '我的商品', path: "/myProduct", component: () => import(`@/views/user/MyProduct.vue`), meta: { requireAuth: true } },
      { name: '我的收藏', path: "/mySave", component: () => import(`@/views/user/MySave.vue`), meta: { requireAuth: true } },
      { name: '足迹', path: "/myView", component: () => import(`@/views/user/MyView.vue`), meta: { requireAuth: true } },
      { name: '订单', path: "/orders", component: () => import(`@/views/user/Orders.vue`), meta: { requireAuth: true } },
      { name: '消息', path: "/message", component: () => import(`@/views/user/Message.vue`), meta: { requireAuth: true } },
      { name: '个人中心', path: "/myself", component: () => import(`@/views/user/Myself.vue`), meta: { requireAuth: true } },
      { name: '搜索页', path: "/search", show: false, auth: false, component: () => import(`@/views/user/Search.vue`), meta: { requireAuth: true } },
      { name: '发布商品', path: "/post-product", component: () => import(`@/views/user/PostProduct.vue`), meta: { requireAuth: true } },
    ]
  }
];

const router = new VueRouter({
  routes,
  mode: 'history'
});
router.beforeEach((to, from, next) => {
  // 放行登录页和注册页
  if (to.path === '/login' || to.path === '/register') {
    return next();
  }

  // 检查需要认证的路由
  if (to.matched.some(record => record.meta.requireAuth)) {
    const token = getToken();

    // 未登录情况处理
    if (!token) {
      return next({
        path: '/login',
        query: { redirect: to.fullPath } // 记录目标路由
      });
    }

    // 已登录时的权限检查
    try {
      const role = parseInt(sessionStorage.getItem('role'));
      
      // 管理员路径检查
      if (to.matched[0].path === '/admin' && role !== 1) {
        clearToken();
        return next("/login"); //返回登录页
      }

      // 用户路径检查
      if (to.matched[0].path === '/user' && role !== 2) {
        clearToken();
        return next("/login"); //返回登录页
      }

      return next();
    } catch (error) {
      console.error('权限检查失败:', error);
      return next('/login');
    }
  }

  // 普通页面直接放行
  next();
});
export default router;
