<template>
  <div id="main-scope">
    <div id="channel-header-scope">
      <div id="channel-header">
        <div class="channel-header-user-avatar-scope">
          <img :src="channelOwner.avatar" />
        </div>
        <div id="channel-inner-header-scope">
          <div id="channel-name" v-text="channelOwner.nickname">鱼颈</div>
          <div
            id="cahnnel-fans-count"
            v-text="`${channelOwner.fansCount}名订阅者`"
          >
            100
          </div>
        </div>
      </div>
      <div id="channel-operation-scope">
        <!-- <div
          operation="0"
          :id="operation == 0 ? 'channel-operation-checked-item-style' : ''"
          class="channel-operation-item-scope"
        >
          首页
        </div> -->
        <div
          operation="1"
          :id="operation == 1 ? 'channel-operation-checked-item-style' : ''"
          class="channel-operation-item-scope"
        >
          视频
        </div>
        <div
          operation="2"
          :id="operation == 2 ? 'channel-operation-checked-item-style' : ''"
          class="channel-operation-item-scope"
        >
          播放列表
        </div>
        <div
          operation="3"
          :id="operation == 3 ? 'channel-operation-checked-item-style' : ''"
          class="channel-operation-item-scope"
        >
          简介
        </div>
      </div>
    </div>
    <div id="channel-content-scope">
      <div id="channel-content">
        <!-- <div id="inner-page-home" v-show="operation == 0"></div> -->
        <div id="inner-page-videos" v-show="operation == 1">
          <div v-if="videoPage.record.length != 0" id="sub-menu-scope">
            <div class="sub-menu-label-text-scope"><div>稿件</div></div>
            <div>
              <el-dropdown trigger="click" @command="optedVideoOrder">
                <div class="sub-menu-sort-icon-scope">
                  <svg class="sub-menu-sort-icon icon" aria-hidden="true">
                    <use xlink:href="#icon-paixu"></use>
                  </svg>
                  <div>排序方式</div>
                </div>

                <el-dropdown-menu
                  slot="dropdown"
                  class="sub-menu-sort-dropdown"
                >
                  <!-- <el-dropdown-item :command="{orderField:'views',orderType:0}">最多播放</el-dropdown-item> -->
                  <el-dropdown-item
                    :command="{ orderField: 'releasing_date', orderType: 0 }"
                    >日期降序</el-dropdown-item
                  >
                  <el-dropdown-item
                    :command="{ orderField: 'releasing_date', orderType: 1 }"
                    >日期升序</el-dropdown-item
                  >
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </div>
          <div v-if="videoPage.record.length != 0" id="video-page-content">
            <div
              class="video-page-content-item"
              v-for="video in this.videoPage.record"
              :key="video.videoId"
            >
              <router-link
                :to="`/video/${video.videoId}`"
                class="video-page-content-item-cover-scope"
              >
                <img
                  class="video-page-content-item-cover"
                  :src="video.videoCoverUrl"
                />
              </router-link>
              <div class="video-page-content-item-metadata-scope">
                <router-link
                  :to="`/video/${video.videoId}`"
                  class="video-page-content-item-title double-ellipsis"
                  v-text="video.videoTitle"
                ></router-link>
                <div
                  class="video-page-content-item-metadata"
                  v-text="
                    $viewsFilter(video.videoViews) +
                    ' · ' +
                    $dateFromNowFilter(video.videoReleasingDate)
                  "
                ></div>
              </div>
            </div>
          </div>
          <div v-else class="video-page-no-data-view">暂无视频</div>
        </div>
        <div id="inner-page-playlist" v-show="operation == 2"></div>
        <div id="inner-page-description" v-show="operation == 3"></div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      // operation: 0:首页 1:视频 2:播放列表 3:简介
      operation: 1,
      channelOwner: {},
      videoPage: {
        inited: false,
        isLoading: false,
        scrollInterval: false,
        isEnded: false,
        columnCount: 0,
        preOrderField: undefined,
        preOrderType: undefined,
        orderField: "releasing_date",
        orderType: 0,
        record: [],
      },
      windowResizeFunction: undefined,
    };
  },
  watch: {
    "$store.state.sidebarVisible"() {
      window.onscroll = undefined;
      if (this.operation == 1) {
        this.videoPageResize();
      }
    },
    operation: {
      immediate: true,
      handler(newValue) {
        if (newValue == 1) {
          this.$nextTick(() => {
            if (this.videoPage.inited) {
              return;
            }
            this.videoPageResize();
            this.initVideoPage();
          });
        }
      },
    },
  },
  methods: {
    // 选项框效果
    operationBarStyle() {
      let opertaionItems = document.getElementsByClassName(
        "channel-operation-item-scope"
      );
      for (let i = 0; i < opertaionItems.length; i++) {
        // 鼠标悬浮在操作选项上时,字体变白
        opertaionItems[i].onmouseover = ($event) => {
          $event.target.style.color = "#ffffff";
        };
        // 鼠标离开操作选项上时,字体变灰
        opertaionItems[i].onmouseout = ($event) => {
          $event.target.style.color = "#aaaaaa";
        };
        // 点击操作选项
        opertaionItems[i].onclick = ($event) => {
          this.operation = $event.target.getAttribute("operation");
        };
      }
    },
    // 初始化视频界面
    initVideoPage() {
      this.videoPage.inited = true;
      this.getVideoPageData();
      this.bindVideoPageScroll();
    },
    // 获取视频数据
    getVideoPageData() {
      if (this.videoPage.isEnded) {
        return;
      }
      this.$request.product
        .get("api/video/get_videos_of_by_user_id", {
          params: {
            index: this.videoPage.record.length,
            size: this.videoPage.columnCount * 4,
            orderField: this.videoPage.orderField,
            orderType: this.videoPage.orderType,
            userId: this.$route.params.userId,
          },
        })
        .then(({ data }) => {
          if (data.code == 200) {
            if (data.data == undefined || data.data.length == 0) {
              this.videoPage.isEnded = true;
              window.onscroll = undefined;
              return;
            }
            this.videoPage.record = this.videoPage.record.concat(data.data);
          } else {
          }
          this.videoPage.isLoading = false;
        })
        .catch((error) => {
          console.log(error);
          this.videoPage.isLoading = false;
        });
    },
    // 当窗口发生大小变化时,视频页面样式调整
    videoPageResize() {
      let videoPageDom = document.getElementById("channel-content-scope");
      let cw = videoPageDom.clientWidth - videoPageDom.clientWidth * 0.18 * 2;
      this.videoPage.columnCount = Math.floor(cw / 214);
      let contentWidth = 214 * this.videoPage.columnCount;
      document.getElementById("inner-page-videos").style.width =
        contentWidth + "px";
    },
    // 当窗口滚动到底部时,获取视频界面数据
    videoPageScrollToBottom() {
      console.log("scrollHeight", document.documentElement.scrollHeight);
      console.log("scrollTop", document.documentElement.scrollTop);
      console.log("clientHeight", document.documentElement.clientHeight);
      let bottomOfWindow =
        document.documentElement.scrollHeight -
          document.documentElement.scrollTop -
          document.documentElement.clientHeight <=
        50;

      if (bottomOfWindow && this.videoPage.isLoading == false) {
        this.videoPage.isLoading = true;
        this.getVideoPageData();
      }
    },
    bindVideoPageScroll() {
      window.onscroll = () => {
        console.log("scroll");
        if (!this.videoPage.scrollInterval) {
          this.videoPage.scrollInterval = true;
          this.videoPageScrollToBottom();
          console.log("Scroll");
          setTimeout(() => {
            this.videoPage.scrollInterval = false;
          }, 100);
        }
      };
    },
    // 选择视频界面数据的排序方式
    optedVideoOrder(order) {
      this.videoPage.preOrderField = this.videoPage.orderField;
      this.videoPage.preOrderType = this.videoPage.orderType;
      this.videoPage.orderField = order.orderField;
      this.videoPage.orderType = order.orderType;

      if (
        order.orderField == this.videoPage.preOrderField &&
        order.orderType == this.videoPage.preOrderType
      ) {
        return;
      }
      if (this.videoPage.isEnded) {
        this.videoPage.isEnded = false;
        this.bindVideoPageScroll();
      }
      this.videoPage.record = [];
      this.getVideoPageData();
    },
  },
  created() {
    this.$request.user
      .get(`api/user/${this.$route.params.userId}`)
      .then(({ data }) => {
        if (data.code == 200) {
          this.channelOwner = data.data;
        } else {
          console.log("code:", data.code, ", msg:", data.msg);
        }
      })
      .catch((error) => {
        console.log(error);
      });
  },
  mounted() {
    this.windowResizeFunction = () => {
      if (this.operation == 1) {
        this.videoPageResize();
      }
    };
    window.addEventListener("resize", this.windowResizeFunction);
    this.operationBarStyle();
  },
  beforeDestroy() {
    window.onscroll = undefined;
    window.removeEventListener("resize", this.windowResizeFunction);
  },
};
</script>

<style>
#main-scope {
  background-color: #0f0f0f;
  display: flex;
  flex-direction: column;
}

#channel-header-scope {
  height: 150px;
  background-color: #181818;
  padding-top: 12px;
  padding-left: 18%;
  padding-right: 18%;
}
#channel-header {
  height: 100px;
  display: flex;
  align-items: center;
}
.channel-header-user-avatar-scope {
  margin-right: 24px;
}
.channel-header-user-avatar-scope > img {
  height: 80px;
  width: 80px;
  border-radius: 40px;
}
#channel-inner-header-scope {
  display: flex;
  flex-direction: column;
}

#channel-name {
  height: 26px;
  font-size: 22px;
  color: #ffffff;
  margin-bottom: 4px;
}
#cahnnel-fans-count {
  height: 17px;
  color: #aaaaaa;
}

#channel-operation-scope {
  color: #aaaaaa;
  font-size: 16px;
  display: flex;
}
#channel-operation-checked-item-style {
  border-bottom: 3px solid #aaaaaa;
}
.channel-operation-item-scope {
  width: 90px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
#channel-content-scope {
}
#channel-content {
  padding-left: 18%;
  padding-right: 18%;
  display: flex;
  flex-direction: column;
}
#inner-page-videos {
  display: flex;
  flex-direction: column;
  flex: 1;
}

#sub-menu-scope {
  display: flex;
  align-items: center;
  height: 56px;
  color: #ffffff;
  padding: 4px 0 4px 0;
  justify-content: space-between;
}
.sub-menu-label-text-scope {
  height: 22px;
  font-size: 16px;
}
.sub-menu-sort-icon-scope {
  height: 22px;
  font-size: 16px;
  display: flex;
  cursor: pointer;
}
.sub-menu-sort-icon {
  height: 22px;
  width: 30px;
  fill: #ffffff;
}
.sub-menu-sort-dropdown {
  background-color: #202020;
  border-color: #202020;
}

#video-page-content {
  display: flex;
  flex: 1;
  flex-wrap: wrap;
  align-content: flex-start;
  min-height: calc(216px * 4);
}
.video-page-content-item {
  width: 210px;
  height: 192px;
  margin: 0 4px 24px 0;
}
.video-page-content-item-cover-scope {
  width: inherit;
  height: 118px;
  cursor: pointer;
}
.video-page-content-item-cover {
  width: inherit;
  height: inherit;
}
.video-page-content-item-metadata-scope {
  height: 74px;
}
.video-page-content-item-title {
  width: inherit;
  color: #ffffff;
  font-weight: 520;
  font-size: 16px;
  margin-top: 8px;
  margin-bottom: 8px;
  cursor: pointer;
  text-decoration: none;
}
.video-page-content-item-metadata {
  color: #aaaaaa;
  font-size: 12px;
}
.video-page-no-data-view {
  display: flex;
  justify-content: center;
  color: #ffffff;
  font-size: 24px;
  padding-top: 20px;
}
</style>