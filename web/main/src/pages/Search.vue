<template>
  <div id="search-page">
    <div id="filter-btn-scope"></div>
    <div id="user-space-scope">
      <a class="user-space-link" href="#">
        <div>
          <img
            src="https://yujing-youtube.oss-cn-guangzhou.aliyuncs.com/user/article/2022-03-20/8f848728-1ba0-494a-aaee-ac92549d50e0.png"
          />
        </div>
      </a>
      <div class="user-space-content">
        <span class="user-space-nickname ellipsis">作者名</span>
        <span class="user-space-details ellipsis">订阅数和视频总数</span>
        <span class="user-space-description double-ellipsis"
          >个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介个人空间简介</span
        >
      </div>
    </div>
    <div id="articles-scope" v-show="page.records && page.records.length > 0">
      <div
        class="article-item-scope"
        v-for="article in page.records"
        :key="article.videoId"
      >
        <a :href="article.videoId | videoAddressFilter"
          ><div>
            <img class="article-cover" :src="article.videoCover" /></div
        ></a>
        <div class="article-item-content">
          <a
            :href="article.videoId | videoAddressFilter"
            class="article-item-title double-ellipsis"
            v-text="article.videoTitle"
            >标题</a
          >
          <span
            class="article-item-details ellipsis"
            v-text="
              getVideoDetails(article.videoViews, article.releasingDate)
            "
            >观看次数和时间</span
          >
          <div class="article-item-author-details">
            <span style="height: 100%; display: inline-block">
              <img
                style="width: 24px; height: 24px;margin-right 5px;border-radius: 12px;"
                :src="article.authorAvatar"
            /></span>
            <span class="ellipsis" v-text="article.authorName">作者名</span>
          </div>
          <span
            class="article-item-description double-ellipsis"
            v-text="article.videoDescription"
            >作品简介</span
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Search",
  watch: {
    "$store.state.searchTrigger"() {
      this.initPage();
    },
  },
  filters: {
    videoAddressFilter(videoId) {
      return `http://yujing.icu/video/${videoId}`;
    },
  },
  data() {
    return {
      prevSearchKey: "",
      page: {
        current: 1,
        total: 0,
        totalPage: 0,
        records: [],
      },
    };
  },
  methods: {
    initPage() {
      let query = { query: this.$store.state.searchKey };

      if (this.page.current > 1) {
        query.page = this.page.current;
      }

      if (this.prevSearchKey == query.query) {
        return;
      }
      this.prevSearchKey = query.query;

      try {
        this.$router.replace({
          name: "Search",
          query: query,
        });
      } catch (error) {
        console.log("catch");
      }

      this.$request.product
        .get("api/results", {
          params: query,
        })
        .then(({ data }) => {
          if (data.code == 200) {
            console.log("data:", data.data);
            this.page = data.data;
          }
        });
    },
    getVideoDetails(views, releasingDate) {
      return `${views}次观看 · 发布日期: ${releasingDate}`;
    },
  },

  created() {
    console.log("created");
    this.$store.commit("changeInSearchPage", true);
    this.$store.commit("changeSearchKey", this.$route.query.query);
    this.initPage();
  },
  mounted() {
    console.log("mounted");
  },
  beforeDestroy() {
    this.$store.commit("changeSearchKey", "");
    this.$store.commit("changeInSearchPage", false);
  },
};
</script>

<style>
/* .ellipsis {
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
} */
#search-page {
  width: 1100px;
  height: auto;
  margin-left: auto;
  margin-right: auto;
  justify-content: center;
  background-color: #181818;
  color: #aaaaaa;
}
#filter-btn-scope {
  width: 100%;
  height: 36px;
  margin-top: 5px;
}

#user-space-scope {
  display: flex;
  padding-top: 10px;
  padding-bottom: 25px;
  border: 2px #303030 solid;
  border-left: none;
  border-right: none;
}

.user-space-link > div {
  text-align: center;
  width: 360px;
  height: 136px;
}

.user-space-link > div > img {
  width: 136px;
  height: 136px;
  border-radius: 68px;
  display: inline-block;
}

.user-space-content {
  margin-left: 15px;
  display: flex;
  flex-direction: column;
  width: 600px;
}

.user-space-nickname {
  margin-top: 10px;
  font-size: 18px;
  font-weight: 800;
  line-height: 26px;
  color: #ffffff;
}

.user-space-details {
  margin-top: 12px;
  font-size: 14px;
  line-height: 16px;
}

.user-space-description {
  margin-top: 10px;
  font-size: 14px;
  line-height: 16px;
}

#articles-scope {
  display: flex;
  flex-direction: column;
}

.article-item-scope {
  margin-top: 15px;
  display: flex;
}

.article-cover {
  width: 360px;
  height: 200px;
}

.article-item-content {
  margin-left: 15px;
  display: flex;
  flex-direction: column;
  width: 600px;
}

.article-item-title {
  text-decoration: none;
  max-height: 54px;
  font-size: 18px;
  font-weight: 800;
  color: #ffffff;
}

.article-item-details {
  margin-top: 15px;
  font-size: 14px;
}

.article-item-author-details {
  margin-top: 14px;
  font-size: 14px;
  height: 24px;
}

/* 作者名样式 */
.article-item-author-details > span {
  display: inline-block;
  vertical-align: middle;
  margin-left: 6px;
  /* height: 24px; */
}

.article-item-description {
  margin-top: 10px;
  font-size: 14px;
}
</style>