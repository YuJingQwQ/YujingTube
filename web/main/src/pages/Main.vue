<template>
  <div id="main-scope">
    <!--  分区  -->
    <div id="partition">
      <div id="zone-scope-scope">
        <!--TODO      如果被选中,背景色变成白色,字体变成黑色      -->
        <div
          :zoneId="undefined"
          class="zone-scope"
          :id="currentZoneId == undefined ? 'checked-zone-scope' : ''"
        >
          <span>全部</span>
        </div>
        <div
          :zoneId="1"
          :id="currentZoneId == 1 ? 'checked-zone-scope' : ''"
          class="zone-scope"
        >
          <span>番剧</span>
        </div>
        <div
          :zoneId="2"
          :id="currentZoneId == 2 ? 'checked-zone-scope' : ''"
          class="zone-scope"
        >
          <span>音乐</span>
        </div>
        <div
          :zoneId="3"
          :id="currentZoneId == 3 ? 'checked-zone-scope' : ''"
          class="zone-scope"
        >
          <span>生活</span>
        </div>
        <div
          :zoneId="4"
          :id="currentZoneId == 4 ? 'checked-zone-scope' : ''"
          class="zone-scope"
        >
          <span>科技</span>
        </div>
      </div>
    </div>
    <!--  视频内容  -->
    <div @resize="contentResize()" id="content">
      <div class="content-column" v-for="(video, index) in videos" :key="index">
        <router-link class="link-style" :to="`/video/${video.videoId}`">
          <img class="media-img" :src="video.videoCoverUrl"
        /></router-link>

        <div class="media-details">
          <router-link
            :to="`/channel/${video.authorId}`"
            class="media-user-avatar link-style"
            ><img class="media-user-avatar" :src="video.authorAvatar"
          /></router-link>
          <div class="media-info">
            <div style="height: 44px">
              <router-link class="link-style" :to="`/video/${video.videoId}`">
                <span class="media-title" v-text="video.videoTitle">标题</span>
              </router-link>
            </div>
            <router-link class="link-style" :to="`/channel/${video.authorId}`">
              <span class="media-author" v-text="video.authorName">作者</span>
            </router-link>
            <span
              class="media-views-and-uploading-time"
              v-text="
                $viewsFilter(video.videoViews) +
                ' · ' +
                $dateFromNowFilter(video.videoReleasingDate)
              "
            ></span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  data: function () {
    return {
      previousZoneId: undefined,
      currentZoneId: undefined,
      videos: [],
      scrollInterval: false,
      isLoading: false,
      columnCount: 0,
      rowCount: 0,
      windowResizeFunc: undefined,
    };
  },
  watch: {
    "$store.state.sidebarVisible"() {
      this.contentResize();
    },
    currentZoneId() {
      this.initVideos();
    },
  },
  methods: {
    initVideos() {
      this.$request.product
        .get("api/videos", {
          params: {
            zoneId: this.currentZoneId,
            count: this.columnCount * this.rowCount,
          },
        })
        .then(({ data }) => {
          if (data.code == 200) {
            if (this.previousZoneId == this.currentZoneId) {
              this.videos = this.videos.concat(data.data);
            } else {
              this.videos = data.data;
              this.previousZoneId = this.currentZoneId;
            }
          }
          this.isLoading = false;
        })
        .catch((error) => {
          console.log(error);
          this.isLoading = false;
        });
    },
    toVideoPage(videoId) {
      this.$router.push({
        path: `/video/${videoId}`,
      });
    },
    contentResize() {
      let contentScopeDom = document.getElementById("main-scope");
      let contentDom = document.getElementById("content");
      let cw = contentScopeDom.clientWidth;
      this.columnCount = Math.floor(cw / 356);
      let contentWidth = 356 * this.columnCount;
      let padding = Math.abs(cw - contentWidth) / 2;
      contentDom.style.width = contentWidth + "px";
      contentDom.style.paddingLeft = padding + "px";
      contentDom.style.paddingRight = padding + "px";
    },
    // 计算当前宽度一行能存放多少个视频介绍框
    contentcolumnCount() {
      let contentScopeDom = document.getElementById("main-scope");
      let cw = contentScopeDom.clientWidth;
      this.columnCount = Math.floor(cw / 356);
    },
    // 计算当前高度能存放多少行视频介绍框
    contentRowCount() {
      this.rowCount = Math.floor(screen.availHeight / 319);
    },
    scrollToBottomThenSpawnVideos() {
      window.onscroll = () => {
        if (this.scrollInterval) {
          return;
        }
        this.scrollInterval = true;
        setTimeout(() => {
          this.scrollInterval = false;
        }, 100);
        // 距离底部100px时加载一次
        let bottomOfWindow =
          document.documentElement.scrollHeight -
            document.documentElement.scrollTop -
            document.documentElement.clientHeight <=
          200;

        if (bottomOfWindow && this.isLoading == false) {
          this.isLoading = true;
          this.initVideos();
        }
      };
    },
    bindZoneClickEvent() {
      let that = this;
      let zones = document.getElementsByClassName("zone-scope");
      for (let index = 0; index < zones.length; index++) {
        zones[index].onclick = function () {
          that.previousZoneId = that.currentZoneId;
          that.currentZoneId = this.getAttribute("zoneId");
        };
      }
    },
  },
  mounted() {
    this.windowResizeFunc = () => {
      this.contentResize();
    };
    window.addEventListener("resize", this.windowResizeFunc);
    this.contentcolumnCount();
    this.contentRowCount();
    this.initVideos();
    this.scrollToBottomThenSpawnVideos();
    this.bindZoneClickEvent();
  },
  beforeDestroy() {
    console.log("MainBeforeDestroy");
    window.onscroll = undefined;
    window.removeEventListener("resize", this.windowResizeFunc);
  },
};
</script>

<style>
#main-scope {
  width: 100%;
  height: 100%;
}
.canClick {
  cursor: pointer;
}
/*---  分区  ---*/
#partition {
  background-color: #202020;
  display: flex;
  align-items: center;
  width: 100%;
  height: 58px;
  border-color: #373737;
  border-style: solid;
  border-width: 1px;
  border-left: hidden;
  border-right: hidden;
}

#checked-zone-scope {
  background-color: #ffffff;
  color: #181818;
}

#zone-scope-scope {
  display: flex;
  margin-left: auto;
  margin-right: auto;
  width: 450px;
  height: 32px;
}

.zone-scope {
  display: flex;
  flex-direction: column;
  align-items: center;
  border-radius: 20px;
  margin-left: 20px;
  width: 55px;
  height: 32px;
  color: #ffffff;
  background-color: #4d4d4d;
  cursor: pointer;
}

#partition > div > div > span {
  line-height: 32px;
}

/*---  分区  end*/

/*---  内容  ---*/
#content {
  display: flex;
  flex-wrap: wrap;
  padding-top: 20px;
  /* justify-content: center; */

  padding-left: calc((100vw - (5 * 356px) - 100px) / 2);
  padding-right: calc((100vw - (5 * 356px) - 100px) / 2);
}
.content-column {
  width: 340px;
  padding-left: 8px;
  padding-right: 8px;
  margin-bottom: 15px;
}

.media-img {
  margin-left: auto;
  margin-right: auto;
  width: 340px;
  height: 190px;
}

.media-user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 36px;
}

.media-details {
  display: flex;
  width: 340px;
  height: 100px;
  background-color: #181818;
  padding-top: 10px;
}

.media-info {
  margin-left: 10px;
  margin-right: 30px;
}

.media-title {
  color: #fff;
  display: -webkit-box;
  line-height: 22px;
  font-size: 16px;
  font-weight: 500;
  word-break: break-all;
  overflow: hidden;
  -webkit-line-clamp: 2;
  text-overflow: ellipsis;
  white-space: normal;
  -webkit-box-orient: vertical;
}

.media-author {
  padding-top: 5px;
  display: inline-block;
  height: 25px;
  color: #aaaaaa;
  font-size: 14px;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.media-views-and-uploading-time {
  display: block;
  color: #aaaaaa;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/*---  内容  end*/
</style>