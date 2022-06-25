// 该文件专门用于创建整个应用的路由器
import VueRouter from 'vue-router'
//引入组件
import uploadManagerArticle from '../pages/upload-manager/article'
import uploadManagerAppeal from '../pages/upload-manager/appeal'


//创建并暴露一个路由器
export default new VueRouter({
	routes: [
		{
			path: '/',
			redirect: '/upload_manager/article'
		},
		{
			path: '/upload_manager/article',
			component: uploadManagerArticle
		}, {
			path: '/upload_manager/appeal',
			component: uploadManagerAppeal
		}

	]
})
