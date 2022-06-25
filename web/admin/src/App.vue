<template>
  <div id="app">
    <el-container style="width: 100%; height: 100%">
      <!-- 顶部导航 -->
      <el-header id="header">
        <div id="left-block">
          <img
            style="height: 39px; width: 150px"
            :src="$globalConfig.author + 'static/author/img/author_logo.jpg'"
          />
          <!-- <a :href="$globalConfig.product"
            ><img
              :src="$globalConfig.author + 'static/author/img/main_site.png'"
            /><span>主站</span></a
          > -->
        </div>
        <div id="right-block">管理员信息~~~</div>
      </el-header>

      <el-container>
        <!-- 侧边栏 -->
        <el-aside width="200px" id="sidebar">
          <!--路由跳转菜单 -->
          <div id="router-menu">
            <el-menu
              default-active="/upload_manager/article"
              :router="true"
              class="el-menu-vertical-demo"
              background-color="#202020"
              text-color="#FFFFFF"
            >
              <el-submenu index="upload_manager">
                <template slot="title">
                  <i class="el-icon-location"></i>
                  <span>内容管理</span>
                </template>
                <el-menu-item index="/upload_manager/article">
                  <i class="el-icon-menu"></i>
                  <span slot="title">稿件管理</span>
                </el-menu-item>
                <el-menu-item index="/upload_manager/appeal">
                  <i class="el-icon-menu"></i>
                  <span slot="title">申诉管理</span>
                </el-menu-item>
              </el-submenu>
            </el-menu>
          </div>
        </el-aside>
        <el-main id="main">
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  name: "App",
  components: {},

  mounted() {
    console.log(this.$globalConfig);
  },
};
</script>
<style>
html {
  margin: 0;
  padding: 0;
  height: 100%;
}

body {
  margin: 0px;
  padding: 0px;
  width: 100%;
  height: 100%;
}

#app {
  width: 100%;
  height: 100%;
}
#header {
  vertical-align: middle;
  height: 60px;
  background-color: #202020;
}
#header > #left-block {
  position: relative;
  top: 50%;
  transform: translateY(-50%);
  display: inline-block;
  height: 39px;
  width: 235px;
}

#left-block > a {
  display: flex;
  float: right;
  position: relative;
  top: 50%;
  transform: translateY(-50%);
  color: black;
  text-align: justify;
  line-height: 24px;
  height: 24px;
  text-decoration: none;
}

#header > #right-block {
  background-color: cornflowerblue;
  position: relative;
  top: 50%;
  transform: translateY(-50%);
  float: right;
  display: inline-block;
  align-self: center;
  height: 30px;
  width: 380px;
}

/* 侧边 */
#sidebar {
  background-color: #202020;
}

#router-menu {
  background-color: darkcyan;
  display: flex;
  flex-direction: column;
}

#router-menu > div {
  height: 46px;
}

/* 侧边 */

/* 主界面 */
#main {
  background-color: #181818;
}
</style>
