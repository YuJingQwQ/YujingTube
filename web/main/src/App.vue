<template>
  <div id="app">
    <el-container>
      <el-header class="top-scope">
        <!--  页面顶部  -->
        <div id="top">
          <!--  菜单按钮  -->
          <div style="display: flex">
            <div @click="clickSidebar" class="guide-button-style-scope">
              <img
                class="guide-button"
                :src="
                  $globalConfig.product + 'static/product/img/guide-button.png'
                "
              />
            </div>

            <!--  logo  -->
            <div class="logo-style-scope">
              <router-link to="/">
                <img
                  :src="$globalConfig.product + 'static/common/img/logo.png'"
                  class="logo-img"
              /></router-link>
            </div>
          </div>
          <!--  搜索模块  -->
          <div class="search-style-scope">
            <!--    搜索框    -->
            <div class="search-box">
              <!-- <div class="search-form"> -->
              <input
                @keydown.enter="search()"
                id="search-input"
                placeholder="搜索"
                v-model="searchKey"
                type="text"
              />
              <!-- </div> -->
            </div>
            <!--    搜索放大镜(搜索按钮)    -->
            <div @click="search()" class="search-image-style-scope">
              <img
                class="search-image"
                :src="$globalConfig.product + 'static/product/img/search.png'"
              />
            </div>
          </div>
          <!--  用户模块  -->
          <div
            class="user-style-scope"
            :loginStatus="$store.state.isLogined ? 1 : 0"
          >
            <div class="user-message-scope">
              <img
                class="user-message"
                :src="$globalConfig.product + 'static/product/img/message.png'"
              />
            </div>
            <div class="user-avatar-scope">
              <img
                class="user-avatar"
                style="cursor: pointer"
                @mouseover="mouseoverUserAvatar"
                @mouseout="mouseoutUserAvatar"
                :src="$store.state.user.avatar"
              />
            </div>
          </div>
          <!-- 用户点击头像后弹出的选择框 -->
          <div
            v-show="userMenuVisible"
            id="menu-container"
            @mouseover="mouseoverUserMenu"
            @mouseout="mouseoutUserMenu"
            :style="userMenuVisible == true ? 'opacity: 1;' : 'opacity: 0;'"
          >
            <!--            -->
            <img
              id="menu-user-avatar"
              @click="toUserChannel()"
              :src="$store.state.user.avatar"
            />
            <span
              id="menu-user-nickname"
              v-text="$store.state.user.nickname"
            ></span>
            <hr style="width: 80%" />
            <div id="menu-user-content">
              <div class="subscribing">
                <span>关注</span
                ><span v-text="$store.state.user.subscribingCount"></span>
              </div>
              <div class="fans">
                <span>粉丝</span
                ><span v-text="$store.state.user.fansCount"></span>
              </div>
              <div class="videos">
                <span>视频</span><span v-text="$store.state.user.videos"></span>
              </div>
            </div>
            <hr style="width: 80%" />
            <div id="menu-content">
              <div @click="toUserChannel()" class="user-center">
                <img
                  :src="
                    $globalConfig.product + 'static/product/img/user-center.png'
                  "
                />
                <span>个人中心</span>
              </div>
              <div @click="toAuthorPage" class="uploading-panel">
                <img
                  :src="
                    $globalConfig.product + 'static/product/img/user-center.png'
                  "
                />
                <span>投稿管理</span>
              </div>
              <div v-if="$store.state.isLogined" @click="logout" class="logout">
                <img
                  :src="
                    $globalConfig.product + 'static/product/img/user-center.png'
                  "
                />
                <span>退出</span>
              </div>
            </div>
          </div>
        </div></el-header
      >
      <el-container>
        <el-aside
          v-show="$store.state.sidebarVisible"
          class="sidebar-scope"
          width="70px"
          ><!--  侧边栏  -->
          <div id="sidebar">
            <router-link to="/">
              <img
                :src="$globalConfig.product + 'static/product/img/index.png'"
              />
              <span>首页</span></router-link
            >
            <router-link to="/">
              <img
                :src="
                  $globalConfig.product + 'static/product/img/subscribe.png'
                "
              />
              <span>订阅内容</span></router-link
            >

            <router-link to="/"
              ><img
                :src="$globalConfig.product + 'static/product/img/history.png'"
              />
              <span>媒体库</span></router-link
            >
          </div></el-aside
        >
        <el-main class="main-scope">
          <!-- 显示主要内容 -->
          <router-view></router-view
        ></el-main>
      </el-container>
    </el-container>
    <iframe
      id="userIframe"
      src="http://user.yujing.icu/share.html"
      style="display: none"
    ></iframe>
  </div>
</template>

<script>
export default {
  name: "App",
  data() {
    return {
      searchKey: "",
      user: undefined,
      initedUserMenu: false,
      userMenuVisible: false,
      userMenuTimer: 0,
    };
  },
  methods: {
    clickSidebar() {
      this.$store.commit(
        "changeSidebarVisible",
        !this.$store.state.sidebarVisible
      );
    },
    getUserFromSession() {
      this.$request.user
        .get("api/user/from/session")
        .then(({ data }) => {
          if (data.code == 200) {
            if (data.data != undefined) {
              this.$store.commit("logined", data.data);
            } else {
              this.$store.commit("unlogined");
            }
          } else if (data.code == 20006) {
            window.localStorage.removeItem("loginJwt");
            window.location.reload();
          }
        })
        .catch((error) => {});
    },
    search() {
      if (this.searchKey == undefined || this.searchKey == "") {
        console.log("finish");
        return;
      }
      this.$store.state.searchKey = this.searchKey;
      if (this.$store.state.inSearchPage) {
        this.$store.commit("runSearchTrigger");
        return;
      }
      this.$router.push({
        name: "Search",
        query: { query: this.searchKey },
      });
    },
    mouseoverUserAvatar() {
      clearTimeout(this.userMenuTimer);
      if (!this.initedUserMenu) {
        if (this.$store.state.user.id) {
          this.$request.user
            .get(`api/user/${this.$store.state.user.id}`)
            .then(({ data }) => {
              if (data.code == 200) {
                this.$store.state.user = data.data;
                this.initedUserMenu = true;
              } else {
              }
            })
            .catch((error) => {
              console.log(error);
            });
        }
      }
      this.userMenuVisible = true;
    },
    mouseoutUserAvatar() {
      clearTimeout(this.userMenuTimer);
      this.userMenuTimer = setTimeout(() => {
        this.userMenuVisible = false;
      }, 500);
    },
    mouseoverUserMenu() {
      clearTimeout(this.userMenuTimer);
    },
    mouseoutUserMenu() {
      if (this.userMenuVisible) {
        clearTimeout(this.userMenuTimer);
        this.userMenuTimer = setTimeout(() => {
          this.userMenuVisible = false;
        }, 500);
      }
    },
    toUserChannel() {
      if (this.$store.state.isLogined) {
        // 跳转个人频道
        this.$router.push(`/channel/${this.$store.state.user.id}`);
      } else {
        // 未登录则跳转登录页面
        this.$router.push("/login");
      }
    },
    toAuthorPage() {
      window.open(this.$globalConfig.user);
    },
    logout() {
      this.$request.user
        .get("api/user/logout")
        .then(({ data }) => {
          if (data.code == 200) {
            window.localStorage.setItem("loginJwt", "");
            document
              .getElementById("userIframe")
              .contentWindow.postMessage(
                { loginJwt: "" },
                "http://user.yujing.icu"
              );
            location.href = this.$globalConfig.product;
          }
        })
        .catch((error) => {
          console.log("error:", error);
        });
    },
  },
  created() {
    this.searchKey = this.$route.query.query;
  },
  mounted() {
    this.getUserFromSession();
  },
};
</script>

<style>
* {
  box-sizing: border-box ;
  margin: 0 ;
  padding: 0 ;
}
/* 设置滚动条的样式 */
::-webkit-scrollbar {
  width: 8px;
}
/* 滚动槽 */
::-webkit-scrollbar-track {
  background-color: #202020;
  border-radius: 10px;
}
/* 滚动条滑块 */
::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background-color: #aaaaaa;
}
.ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.double-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  word-break: break-all;
}
.link-style {
  text-decoration: none;
}
html {
  width: 100%;
  height: 100%;
}
body {
  background-color: #181818;
  width: 100%;
  height: 100%;
}

#app {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

@media only screen and (max-width: 479px) {
  .top-scope{
    height: 48px !important;
  }
}

.top-scope {
  z-index: 100;
  margin: 0;
  padding: 0;
}
.sidebar-scope {
  margin: 0;
  padding: 0;
  width: 70px;
  background-color: #202020;
  z-index: 100;
}

.main-scope {
  margin: 0;
  padding: 0;
}
/*--- 顶部导航 ---*/
#top {
  display: flex;
  padding-left: 16px;
  background-color: #202020;
  justify-content: space-between;
}

/*菜单栏样式*/
.guide-button-style-scope {
  cursor: pointer;
  margin-right: 14px;
  align-self: center;
  position: relative;
  width: 40px;
  height: 40px;
}

/*菜单按钮*/
.guide-button {
  display: block;
  margin-left: auto;
  margin-right: auto;
  position: relative;
  top: 50%;
  transform: translateY(-50%);
}

/*logo样式*/
.logo-style-scope {
  max-width: 120px;
  padding: 18px 14px 18px 16px;
}

/*logo*/
.logo-img {
  width: 90px;
  height: 20px;
}

/*搜索模块*/
.search-style-scope {
  align-self: center;
  display: flex;
  height: 40px;
  min-width: 0;
}

/*搜索框*/
.search-box {
  display: flex;
  min-width: 0;
  width: 644px;
  height: 40px;
  border-style: solid;
  /*使用JS当search-input聚焦时更换边框颜色 #1C62B9*/
  border-color: #303030;
  border-width: 1px;
  background-color: #000000;
}

/*抠唆框内表单的输入框*/
#search-input {
  outline: none;
  background-color: #000000;
  border-style: hidden;
  padding: 0;
  font-size: 18px;
  color: white;
  min-width: 100px;
  width: 100%;
  height: 100%;
  margin-left: 15px;
}

/*点击搜索按钮样式*/
.search-image-style-scope {
  cursor: pointer;
  display: flex;
  min-width: 64px;
  width: 64px;
  background-color: #313131;
}

/*点击搜索按钮图片*/
.search-image {
  align-self: center;
  width: 24px;
  height: 24px;
  margin-left: auto;
  margin-right: auto;
}

/*用户模块样式*/
.user-style-scope {
  display: flex;
  margin-top: 8px;
  margin-right: 30px;
  width: 100px;
  height: 40px;
}

/*用户信息(铃铛)*/
.user-message-scope {
  display: flex;
  align-items: center;
  width: 40px;
}

.user-message {
  width: 24px;
  height: 24px;
  margin-left: auto;
}

/*用户头像*/
.user-avatar-scope {
  display: flex;
  align-items: center;
  width: 60px;
}

.user-avatar {
  margin-left: auto;
  width: 32px;
  height: 32px;
  border-radius: 32px;
}

/*--- 顶部导航 end*/

/*---  侧边栏  ---*/
#sidebar {
  display: flex;
  flex-direction: column;
}

#sidebar > a {
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 74px;
  background-color: #202020;
  text-decoration: none;
  color: #ffffff;
  padding-top: 10px;
}

#sidebar > div > img {
  margin-top: 10px;
  margin-left: auto;
  margin-right: auto;
  width: 28px;
  height: 28px;
}

#sidebar > div > span {
  display: block;
  color: white;
  font-weight: 400;
  font-size: 14px;
  height: 22px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
/*---  侧边栏  end*/

/* 个人用户悬浮交互菜单 */
#menu-container {
  color: #eaeaea;
  align-items: center;
  position: absolute;
  right: 80px;
  top: 56px;
  z-index: 1024;
  background-color: #212121;
  width: 300px;
  display: flex;
  flex-direction: column;
}

#menu-user-avatar {
  cursor: pointer;
  margin-top: -40px;
  width: 80px;
  height: 80px;
  border-radius: 40px;
}

#menu-user-nickname {
  color: #eeeeee;
  margin-top: 5px;
  display: block;
  font-size: 16px;
  font-weight: 600;
}

#menu-user-content {
  display: flex;
  align-items: center;
}

#menu-user-content span {
  margin: 0 10px 0 10px;
  display: block;
  max-width: 60px;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

#menu-content {
  /*background-color: #0086b3;*/
  width: 100%;
  height: 100%;
  align-self: start;
}

#menu-content div {
  margin: 20px 0 10px 20px;
  cursor: pointer;
}

#menu-content img {
  margin-right: 5px;
  width: 24px;
  height: 24px;
}
</style>
