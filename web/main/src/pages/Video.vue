<template>
  <div id="content">
    <div id="main-content">
      <div id="video-content-scope">
        <div id="video-player"></div>
        <div id="video-content">
          <div class="video-title" v-text="video.videoTitle">
            ジブリジャズ 30曲 Studio Ghibli Winter Night Jazz Piano Collection
            Piano Covered by Relax Music BGM CHANNEL
          </div>
          <div class="video-details" v-text="spawnVideoDetails()">
            2,233,922次观看首播开始于 2021年10月26日
          </div>
          <div class="video-operations">赞 踩 举报</div>
        </div>
      </div>

      <!-- 作者信息 -->
      <div id="author-content-scope">
        <div class="author-content">
          <router-link
            class="author-content-author-avatar-link"
            :to="`/channel/${video.authorId}`"
            ><img
              class="author-content-author-avatar-img"
              :src="video.authorAvatar"
          /></router-link>
          <div class="author-content-details">
            <router-link class="link-stype" :to="`/channel/${video.authorId}`">
              <span class="author-content-author-name" v-text="video.authorName"
                >作者名</span
              ></router-link
            >
            <br />
            <span v-text="`${this.video.authorFansCount}位订阅者`">订阅数</span>
          </div>
          <div style="float: right">订阅</div>
        </div>
        <div
          id="video-description-scope"
          :class="videoDescriptionClass"
          v-text="video.videoDescription"
        >
          根据场景可给予用户操作建议或安全提示，但不能代替用户进行决策根据场景可给予用户操作建议或安全提示，但不能代替用
          代替用户进行决策
        </div>
        <el-link
          class="video-description-button"
          type="primary"
          @click="changeVideoDescriptionClass()"
          v-text="videoDescriptionClass.videoDescriptionPart ? '展开' : '收缩'"
        ></el-link>
      </div>

      <!-- 评论 -->
      <div id="comments-scope">
        <div id="my-comment-scope">
          <div>
            <a class="comment-item-user-avatar-link"
              ><img
                class="comment-item-user-avatar-img"
                :src="$store.state.user.avatar"
            /></a>
          </div>

          <div id="user-comment-input">
            <el-input
              type="textarea"
              :autosize="true"
              placeholder="评论一下"
              v-model="myComment"
            >
            </el-input>
            <div id="comment-button">
              <el-button @click="postComment()" type="primary" size="mini" plain
                >评论</el-button
              >
            </div>
          </div>
        </div>

        <div class="comments">
          <div
            class="comments-item-scope"
            v-for="(comment, inx) in commentPage.records"
            :key="comment.commentId"
          >
            <a class="comment-item-user-avatar-link" href="#"
              ><img
                class="comment-item-user-avatar-img"
                :src="comment.userAvatar"
            /></a>
            <div>
              <div>
                <span
                  class="comment-item-user-nickname"
                  v-text="comment.username"
                  >用户名</span
                ><span
                  class="comment-item-date"
                  v-text="commentDateFilter(comment.date)"
                  >时间</span
                >
              </div>
              <div
                class="comment-item-comment-content"
                v-text="comment.comment"
              >
                评论
              </div>
              <div class="comment-item-operations-scope">
                <div
                  @click="
                    commentOperation($event, 0, comment.commentId, comment, inx)
                  "
                  class="comment-item-operation-items"
                >
                  <img
                    class="comment-item-operation-opertaion-img"
                    :src="commentOperationImgFilter(comment.myOperation, true)"
                  />
                </div>
                <span v-text="comment.ups"></span>
                <div
                  @click="
                    commentOperation($event, 1, comment.commentId, comment, inx)
                  "
                  class="comment-item-operation-items"
                >
                  <img
                    class="comment-item-operation-opertaion-img"
                    :src="commentOperationImgFilter(comment.myOperation, false)"
                  />
                </div>
                <span v-text="comment.downs"></span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div id="recommend-content"></div>
  </div>
</template>

<script>
import DPlayer from "dplayer";
export default {
  name: "Video",
  data() {
    return {
      video: {},
      videoPlayer: {},
      myComment: "",
      commentPage: {
        current: 1,
        total: 0,
        pages: 0,
        records: [],
      },
      videoDescriptionClass: {
        videoDescriptionFull: true,
        videoDescriptionPart: false,
      },
    };
  },
  methods: {
    initVideoPlayer() {
      let suffix = this.video.videoUrl.substr(
        this.video.videoUrl.lastIndexOf(".") + 1
      );
      let videoType = "auto";
      if (suffix == "flv") {
        videoType = suffix;
      }

      const dp = new DPlayer({
        container: document.getElementById("video-player"),
        theme: "#409EFF",
        lang: "zh-cn",
        video: {
          url: this.video.videoUrl,
          type: videoType,
        },
        preload: "metadata",
      });

      // 点击视频播放时,触发增加播放量
      dp.on("play", () => {
        console.log("play");
        this.$request.product
          .get(`api/video/play/${this.video.videoId}`)
          .then(() => {})
          .catch((err) => {});
      });
      console.log("dp:", dp);
      this.videoPlayer = dp;
    },
    changeVideoDescriptionClass() {
      this.videoDescriptionClass.videoDescriptionFull =
        !this.videoDescriptionClass.videoDescriptionFull;
      this.videoDescriptionClass.videoDescriptionPart =
        !this.videoDescriptionClass.videoDescriptionPart;
    },
    getVideoIdFromAddress() {
      let href = location.href;
      let start = href.indexOf("/video/") + "/video/".length;
      let end1 = href.indexOf("/", start);
      let end2 = href.indexOf("?", start);

      let videoId = "";
      if (end1 == -1 && end2 == -1) {
        videoId = href.substr(start);
      } else {
        if (end2 == -1) {
          videoId = href.substr(start, end1 - start);
        } else if (end1 == -1) {
          videoId = href.substr(start, end2 - start);
        } else {
          if (end1 > end2) {
            videoId = href.substr(start, end2 - start);
          } else {
            videoId = href.substr(start, end1 - start);
          }
        }
      }
      return videoId;
    },
    spawnVideoDetails() {
      return (
        this.video.videoViews +
        "次观看 · 视频发布日期: " +
        this.$moment(this.video.videoReleasingDate).format("yyyy-MM-DD HH:mm")
      );
    },
    postComment() {
      // 检查登录状态
      if (!this.$store.state.isLogined) {
        this.$message.warning("请先登录");
        return;
      }
      
      if (this.myComment.length == 0) {
        this.$message.warning("填点东西吧~");
        return;
      }

      console.log("postComment..");
      this.$request.product
        .post("api/comment", {
          videoId: this.video.videoId,
          comment: this.myComment,
        })
        .then(({ data }) => {
          if (data.code == 200) {
            this.initComments();
            this.myComment = "";
          } else {
            this.$message.error(data.msg);
          }
        })
        .catch((err) => {
          this.$message.error("评论失败,请稍后再试");
        });
    },
    initComments() {
      // 评论区信息
      this.$request.product
        .get("api/comments", {
          params: {
            page: this.commentPage.current,
            videoId: this.video.videoId,
          },
        })
        .then(({ data }) => {
          if (data.code == 200) {
            this.commentPage = data.data;
          } else {
            this.$message.error(data.msg);
          }
        })
        .catch((err) => {
          this.$message.error("获取评论区数据失败");
        });
    },
    commentOperation($event, operation, commentId, comment) {
      // 检查登录状态
      if (!this.$store.state.isLogined) {
        this.$message.warning("请先登录");
        return;
      }

      this.$request.product
        .get(
          `api/comment/operation/${operation}/${this.video.videoId}/${commentId}`
        )
        .then(({ data }) => {
          if (data.code == 200) {
            this.$message.success(data.msg);
          } else {
            this.$message.error(data.msg);
          }
        })
        .catch((err) => {
          console.log(err);
        });

      let upsNumber = comment.ups;
      let downsNumber = comment.downs;
      // 静态操作
      if (comment.myOperation == undefined) {
        // 如果次条评论用户非操作过
        if (operation == 0) {
          upsNumber = upsNumber + 1;
        } else {
          downsNumber = downsNumber + 1;
        }
      } else {
        // 双向操作
        if (comment.myOperation == operation) {
          // 用户想取消点赞或点踩
          if (operation == 0) {
            upsNumber = upsNumber - 1;
          } else {
            downsNumber = downsNumber - 1;
          }
          operation = undefined;
        } else {
          // 用户想从点赞到点踩(或相反)
          if (operation == 0) {
            upsNumber = upsNumber + 1;
            downsNumber = downsNumber - 1;
          } else {
            upsNumber = upsNumber - 1;
            downsNumber = downsNumber + 1;
          }
        }
      }
      comment.myOperation = operation;
      comment.ups = upsNumber;
      comment.downs = downsNumber;
    },
    commentOperationImgFilter(operation, isDianZan) {
      if (operation == undefined) {
        if (isDianZan) {
          return `${this.$globalConfig.product}static/common/img/dian_zan.png`;
        } else {
          return `${this.$globalConfig.product}static/common/img/dian_cai.png`;
        }
      } else if (operation == 0) {
        if (isDianZan) {
          return `${this.$globalConfig.product}static/common/img/yi_zan.png`;
        } else {
          return `${this.$globalConfig.product}static/common/img/dian_cai.png`;
        }
      } else if (operation == 1) {
        if (isDianZan) {
          return `${this.$globalConfig.product}static/common/img/dian_zan.png`;
        } else {
          return `${this.$globalConfig.product}static/common/img/yi_cai.png`;
        }
      }
    },
    commentDateFilter(date) {
      let nowMill = new Date().getTime();
      let dateMill = this.$moment(date, "yyyy-MM-DD HH:mm:sss")
        .toDate()
        .getTime();
      if (nowMill - dateMill >= 259200000) {
        return this.$moment(date).format("yyyy-MM-DD HH:mm");
      }
      return this.$moment(date, "yyyy-MM-DD HH:mm:ss").fromNow();
    },
  },
  created() {
    let videoId = this.getVideoIdFromAddress();

    this.$request.product
      .get(`api/video/${videoId}`)
      .then(({ data }) => {
        if (data.code == 200) {
          console.log("data:", data.data);
          this.video = data.data;
          this.initVideoPlayer();
          this.initComments();
        } else {
          this.$message.error(data.msg);
        }
      })
      .catch((err) => {
        this.$message.error("请求失败,请稍后刷新页面再试");
      });
  },
  mounted() {
    let that = this
    setTimeout(() => {
      // 获取到视频简介的高度 如果高度 <= 40 则不用显示展开按钮
      let videoDescription = document.getElementById("video-description-scope");
      console.log("offsetHeight", videoDescription.offsetHeight);
      if (videoDescription.offsetHeight <= 40) {
      } else {
        let btn = document.getElementsByClassName(
          "video-description-button"
        )[0];
        btn.style.display = "unset";
        that.videoDescriptionClass.videoDescriptionPart = true;
        videoDescription.setAttribute("class", "videoDescriptionPart");
      }
    }, 500);
  },
};
</script>

<style scoped>
/* 主体 */
#content {
  display: flex;
  justify-content: center;
  width: 100%;
  padding: 20px 0 20px 0;
  color: #ffffff;
}

/* 视频与评论区 */
#main-content {
  max-width: 1280px;
  margin-right: 20px;
}

/* 视频 */
#video-player {
  /* max-width: 1280px; */
  /* max-height: 720px; */
  width: 1280px;
  height: 720px;
}

#video-content {
}

.video-title {
  max-width: 1000px;
  font-size: 18px;
  margin: 10px 0 10px 0;
}

.video-details {
  display: inline-block;
  font-size: 14px;
  color: #aaaaaa;
}

.video-operations {
  float: right;
  display: inline-block;
}

/* 作者信息 */
#author-content-scope {
  margin: 15px 0 15px 0;
  padding: 15px 5px 15px 5px;
  font-size: 14px;
  border: 2px #202020 solid;
  border-left: hidden;
  border-right: hidden;
}

.author-content {
  display: flex;
}

/* 作者头像 */
.author-content-author-avatar-link {
  width: 54px;
  height: 54px;
  border-radius: 27px;
  margin-right: 12px;
}
.author-content-author-avatar-img {
  width: 54px;
  height: 54px;
  border-radius: 27px;
}

/* 作者名和关注量 */
.author-content-details {
  width: 1000px;
}

.author-content-author-name {
  max-width: 500px;
  display: inline-block;
  font-size: 16px;
  color: #ffffff;
  line-height: 22px;
  margin-bottom: 5px;
}

#video-description-scope {
  line-height: 20px;
  margin-left: 66px;
  max-width: 700px;
}

/* 视频简介 */
.videoDescriptionPart {
  max-height: 40px;
  overflow: hidden;
}
.videoDescriptionFull {
}
.video-description-button {
  margin: 10px 0 0 66px;
  display: none;
}

/* 评论区 */
#my-comment-scope {
  display: flex;
  max-width: 1280px;
  margin-bottom: 30px;
}

#user-comment-input {
  display: inline-block;
  max-width: 1220px;
  width: 1300px;
  /* padding-top: 20px; */
}

#comment-button {
  margin-top: 8px;
}

.comments-item-scope {
  display: flex;
  font-size: 14px;
  font-weight: 400;
  letter-spacing: 1px;
  margin-bottom: 20px;
}

.comment-item-user-avatar-link {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  margin-right: 12px;
}
.comment-item-user-avatar-img {
  width: 40px;
  height: 40px;
  border-radius: 20px;
}

.comment-item-date {
  display: inline-block;
  margin-left: 14px;
}

.comment-item-user-nickname {
}

.comment-item-comment-content {
  margin-top: 5px;
  margin-bottom: 15px;
}

.comment-item-operations-scope {
  display: flex;
  align-items: center;
}

.comment-item-operation-items {
  cursor: pointer;
  display: inline-flex;
  width: 32px;
  height: 32px;
  justify-content: center;
  align-items: center;
}

.comment-item-operation-opertaion-img {
  width: 16px;
  height: 16px;
}

/* 右边推荐栏 */
#recommend-content {
  display: flex;
  flex-direction: column;
  background-color: azure;
  width: 400px;
  height: 100%;
}
</style>