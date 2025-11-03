const TOKEN_KEY = "token"
const USER_INFO = "userInfo";
const SEARCH_KEY = "searchKey";
//获取搜索关键词
export function getSearchKey() {
    return sessionStorage.getItem(SEARCH_KEY);
}
//存储搜索关键词
export function setSearchKey(key) {
    sessionStorage.setItem(SEARCH_KEY, key);
}
//获取用户信息
export function getUserInfo() {
    const jsonUserInfo = sessionStorage.getItem(USER_INFO);
    return JSON.parse(jsonUserInfo);
}
//存储用户信息
export function setUserInfo(userInfo) {
    sessionStorage.setItem(USER_INFO, JSON.stringify(userInfo));
}
//获取token
export function getToken() {
    return sessionStorage.getItem(TOKEN_KEY);
}
// 设置token
export function setToken(token) {
    sessionStorage.setItem(TOKEN_KEY, token);
}
// 清除token
export function clearToken() {
    sessionStorage.setItem(TOKEN_KEY, null);
}