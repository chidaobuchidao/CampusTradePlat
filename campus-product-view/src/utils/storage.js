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
// 清除token及用户信息
export function clearToken() {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(USER_INFO);
    sessionStorage.removeItem("role");
}
// 搜索历史（最多6条）
const HISTORY_KEY = "searchHistory";
export function getSearchHistory() {
    try { return JSON.parse(sessionStorage.getItem(HISTORY_KEY)) || []; } catch(e) { return []; }
}
export function addSearchHistory(key) {
    if (!key || !key.trim()) return;
    let list = getSearchHistory();
    list = list.filter(k => k !== key);
    list.unshift(key);
    if (list.length > 6) list = list.slice(0, 6);
    sessionStorage.setItem(HISTORY_KEY, JSON.stringify(list));
}