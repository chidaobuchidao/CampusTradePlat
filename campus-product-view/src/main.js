import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import 'element-ui/lib/theme-chalk/index.css';  
import { provinceAndCityData, regionData } from 'element-china-area-data';  
import VueSweetalert2 from 'vue-sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';
import './assets/css/editor.scss'
import './assets/css/button.scss'
import './assets/css/elementui-cover.scss'
import './assets/css/basic.scss'
import './assets/css/dialog.scss'
import './assets/css/input.scss'
import request from '@/utils/request'
import md5 from 'js-md5';

Vue.config.productionTip = false;
Vue.use(VueSweetalert2);
Vue.prototype.$md5 = md5;
Vue.prototype.$axios = request;
// 图片URL解析：自动补全后端地址
Vue.prototype.$imgUrl = function(url) {
    if (!url) return 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 200 200%22%3E%3Crect fill=%22%23f5f5f5%22 width=%22200%22 height=%22200%22/%3E%3Ctext x=%22100%22 y=%22105%22 text-anchor=%22middle%22 fill=%22%23ccc%22 font-size=%2218%22%3E暂无图片%3C/text%3E%3C/svg%3E';
    if (url.startsWith('http')) return url;
    if (url.startsWith('/')) return 'http://localhost:21090' + url;
    return url;
};
import swalPlugin from '@/utils/swalPlugin';
Vue.use(swalPlugin);

new Vue({
  router,
  regionData,
  provinceAndCityData,
  VueSweetalert2,
  render: h => h(App)
}).$mount("#app");
