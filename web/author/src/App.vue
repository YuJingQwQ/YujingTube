<template>
  <div id="app" v-if="logined">
    <el-container style="width: 100%; height: 100%">
      <!-- 顶部导航 -->
      <el-header id="header">
        <div id="left-block">
          <img
            style="height: 39px; width: 150px"
            :src="$globalConfig.author + 'static/author/img/author_logo.jpg'"
          />
          <a class="main-page-link" :href="$globalConfig.product"
            ><img
              :src="$globalConfig.author + 'static/author/img/main_site.png'"
            /><span>主站</span></a
          >
        </div>
        <div id="right-block">个人信息~~~~~</div>
      </el-header>
      <el-container id="main-container">
        <!-- 侧边栏 -->
        <el-aside width="200px" id="sider">
          <!-- 侧边栏投稿按钮 -->
          <div id="upload-video-btn">
            <router-link to="/upload_manager/upload">
              <img
                style="width: 24px; height: 24px"
                :src="$globalConfig.author + 'static/author/img/upload.png'"
                alt=""
              /><span>投稿</span></router-link
            >
          </div>
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
  data() {
    return {
      logined: false,
    };
  },
  beforeCreate() {
    // 在访问页面前判断用户是否登录
    this.$request.user.get("api/user/from/session").then(({ data }) => {
      if (data.code == 20006) {
        window.localStorage.removeItem("loginJwt");
        window.location.reload();
      }
      if (data.data == undefined) {
        location.href = this.$globalConfig.product + "login";
      } else {
        this.logined = true;
      }
    });
  },
};
</script>
<style>
html {
  height: 100%;
  width: 100%;
}

body {
  margin: 0px;
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

/* #left-block > a {
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
} */
.main-page-link {
  display: flex;
  float: right;
  position: relative;
  top: 50%;
  transform: translateY(-50%);
  color: #ffffff;
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

#main-container {
}
/* 侧边 */
#sider {
  background-color: #202020;
}
#upload-video-btn {
  width: 136px;
  height: 41px;
  margin: 24px auto 17px auto;
  background-color: #1aabda;
}

#upload-video-btn > a {
  padding-left: 37px;
  display: flex;
  position: relative;
  top: 50%;
  transform: translateY(-50%);
  color: black;
  text-align: justify;
  line-height: 24px;
  height: 24px;
  text-decoration: none;
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
