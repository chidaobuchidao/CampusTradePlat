import axios from "axios"
import { getToken, clearToken } from "@/utils/storage.js";
import ElementUI from 'element-ui';

const URL_API = process.env.VUE_APP_API_BASE_URL || 'http://localhost:21090/api/campus-product-sys/v1.0'
const request = axios.create({
  baseURL: URL_API,
  timeout: 8000
});

// 请求拦截器 — 注入 token
request.interceptors.request.use(config => {
  const token = getToken();
  if (token !== null) {
    config.headers["token"] = token;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

// 响应拦截器 — 统一错误处理
request.interceptors.response.use(response => {
  const data = response.data;
  // 业务层 401 统一跳转登录
  if (data && data.code === 401) {
    clearToken();
    window.location.href = '/login';
  }
  return response;
}, error => {
  if (error.response) {
    const { status } = error.response;
    if (status === 401) {
      clearToken();
      window.location.href = '/login';
    } else if (status === 403) {
      ElementUI.Message.error('无操作权限');
    } else if (status >= 500) {
      ElementUI.Message.error('服务器异常，请稍后重试');
    }
  } else {
    ElementUI.Message.error('网络连接异常');
  }
  return Promise.reject(error);
});

export default request;
