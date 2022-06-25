//该文件用于创建Vuex中最为核心的store
import Vue from 'vue'
//引入Vuex
import Vuex from 'vuex'
//应用Vuex插件
Vue.use(Vuex)
import globalConfig from '../config/globalConfig.js'
//创建并暴露store
const state = {
    loginJwt: window.localStorage.getItem("loginJwt"),
    currentPage: 'Main',
    user: {
        id: undefined,
        avatar: globalConfig.product + 'static/product/img/user.png',
        nickname: '未登录',
        subscribingCount: '0',
        fansCount: '0',
        videos: '0'
    },
    sidebarVisible: true,
    isLogined: '',
    searchKey: '',
    searchTrigger: true,
    inSearchPage: false,
}
const mutations = {
    setCurrentPage(state, value) {
        state.currentPage = value;
    },
    logined(state, user) {
        state.user = user;
        state.isLogined = true
    },
    unlogined(state) {
        state.isLogined = false
    },
    changeSearchKey(state, value) {
        state.searchKey = value;
    },
    changeInSearchPage(state, value) {
        state.inSearchPage = value;
    },
    runSearchTrigger(state) {
        state.searchTrigger = !state.searchTrigger;
    }, changeSidebarVisible(state, value) {
        state.sidebarVisible = value
    }
}
//创建并暴露store
export default new Vuex.Store({
    undefined,
    mutations,
    state,
})

