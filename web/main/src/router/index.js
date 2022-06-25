// 该文件专门用于创建整个应用的路由器
import VueRouter from 'vue-router'
//引入组件
import Main from '../pages/Main'
import Search from '../pages/Search'
import Video from '../pages/Video'
import Login from '../pages/Login'
import Channel from '../pages/Channel'



//创建并暴露一个路由器
export default new VueRouter({
	mode: 'history',
	routes: [
		{
			path: '/',
			component: Main
		},
		{
			path: '/results',
			name: 'Search',
			component: Search,
			props: true
		},
		{
			path: '/video/:videoId',
			component: Video
		},{
			path:'/login',
			component: Login
		},
		{
			path:'/channel/:userId',
			component: Channel
		}
	]
})
