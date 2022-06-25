// 该文件专门用于创建整个应用的路由器
import VueRouter from 'vue-router'
//引入组件
import Article from '../pages/upload-manager/article'
import Appeal from '../pages/upload-manager/appeal'
import Upload from '../pages/upload-manager/upload'


//创建并暴露一个路由器
export default new VueRouter({
	routes: [
		{
			path:'/',redirect :'/upload_manager/article'
		},
		{
			path: '/upload_manager/article',
			component: Article
		},
		{
			path: '/upload_manager/appeal',
			component: Appeal
		},
		{
			path: '/upload_manager/upload',
			component: Upload
		},
	]
})
