<template>
  <div id="article-div">
    <div id="article-header">
      <div id="article-status-nav">
        <a
          :class="status == undefined ? 'active-status' : 'inactive-status'"
          @click="status = undefined"
          href="#"
          >全部稿件</a
        >丨
        <a
          :class="status == 0 ? 'active-status' : 'inactive-status'"
          @click="status = 0"
          href="#"
          >审核中</a
        >丨
        <a
          :class="status == 2 ? 'active-status' : 'inactive-status'"
          @click="status = 2"
          href="#"
          >已通过</a
        >丨
        <a
          :class="status == 3 ? 'active-status' : 'inactive-status'"
          @click="status = 3"
          href="#"
          >未通过</a
        >
      </div>
      <div id="operation-nav">
        <el-select
          style="width: 110px; margin-right: 15px"
          size="small"
          v-model="zoneId"
        >
          <el-option
            v-for="zone in zoneList"
            :key="zone.id"
            :label="zone.label"
            :value="zone.id"
          >
          </el-option>
        </el-select>
        <el-select style="width: 150px" size="small" v-model="orderField">
          <el-option
            v-for="sort in sortList"
            :key="sort.field"
            :label="sort.label"
            :value="sort.field"
          >
          </el-option>
        </el-select>
      </div>
    </div>
    <div id="article-body">
      <div v-for="article in page.records" :key="article.id">
        <a style="width: 154px; height: 96px; display: inline-block"
          ><img
            style="
              width: 154px;
              height: 96px;
              border-radius: 5px;
              display: inline-block;
            "
            :src="article.coverUrl"
        /></a>
        <div class="article-meta-wrap">
          <!--TODO 直接跳转难道视频地址 -->
          <!-- <el-link
            :underline="false"
            :href="'#'"
            v-text="article.title"
          ></el-link> -->
          <div class="article-meta-title ellipsis">
            <a href="#" v-text="article.title"></a>
          </div>
          <div class="article-meta-date ellipsis">
            <span v-text="article.uploadingDate"></span>
            <span v-if="article.status == 0" style="color: #409eff">
              | 审核中</span
            >
            <span v-else-if="article.status == 2" style="color: #59e184">
              | 已通过</span
            >
            <span v-if="article.status == 3" style="color: #f41744">
              | 未通过</span
            >
          </div>
          <div class="article-meta-footer">
            <img src="static/author/img/play.png" /><span
              v-text="article.views"
            ></span>
            <img src="static/author/img/comments.png" /><span
              v-text="article.comments"
            ></span>
            <img src="static/author/img/likes.png" /><span
              v-text="article.likes"
            ></span>
          </div>
        </div>

        <div class="article-operation">
          <el-button
            style="background-color: #181818"
            type="primary"
            icon="el-icon-edit"
          ></el-button>
          <el-button
            v-if="article.status == 3"
            @click="reupload(article.id)"
            style="background-color: #181818"
            type="primary"
            >申请审核</el-button
          >
        </div>
      </div>
    </div>
    <div id="article-footer" v-if="page.pages > 1">
      <el-pagination
        @current-change="changePage"
        :pager-count="5"
        :current-page="page.current"
        background
        layout="prev, pager, next"
        :total="page.total"
      >
      </el-pagination>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      status: undefined,
      zoneId: undefined,
      zoneList: [],
      orderField: "uploading_date",
      orderType: 0,
      sortList: [
        { field: "uploading_date", label: "投稿时间排序" },
        { field: "views", label: "播放数量排序" },
      ],
      page: {
        current: 1,
        records: [],
        total: 0,
        pages: 0,
      },
    };
  },
  watch: {
    status() {
      this.page.current = 1;
      this.initArticlePage();
    },
    zoneId() {
      this.page.current = 1;
      this.initArticlePage();
    },
    orderField() {
      this.page.current = 1;
      this.initArticlePage();
    },
    "page.current"() {
      this.initArticlePage();
    },
  },
  methods: {
    initArticlePage() {
      // 请求投稿信息
      this.$request.product
        .get("api/author/articles", {
          params: {
            page: this.page.current,
            status: this.status,
            zoneId: this.zoneId,
            orderField: this.orderField,
            orderType: this.orderType,
          },
        })
        .then(({ data }) => {
          if (data.code == 200) {
            let page = data.data;
            this.page.records = page.records;
            this.page.total = page.total;
            this.page.pages = page.pages;
          } else {
            this.$message.error(data.msg);
          }
        })
        .catch((error) => {
          this.$message.error("请求错误,请稍后再试");
        });
    },
    reupload(articleId) {
      this.$request.product
        .get(`api/author/reupload/article/${articleId}`)
        .then(({ data }) => {
          if (data.code == 200) {
            this.$message.success("请求成功");
            this.initArticlePage();
          } else {
            this.$message.error("请求失败");
          }
        })
        .catch((error) => {
          this.$message.error("请求失败");
        });
    },
    changePage(value) {
      this.page.current = value;
    },
  },
  created() {
    // 请求分区信息
    this.$request.product.get("api/zones").then(({ data }) => {
      if (data.code == 200) {
        let temp = [{ id: undefined, label: "全部分区" }];
        this.zoneList = temp.concat(data.data);
      }
    });

    this.initArticlePage();
  },
  mounted() {},
};
</script>

<style scoped>
.ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
#article-div {
  padding-left: 20px;
  padding-right: 20px;
  margin-left: auto;
  margin-right: auto;
  width: 1280px;
  height: 100%;
  background-color: #202020;
}

/* header */
#article-header {
  padding-top: 20px;
}

#article-status-nav {
  display: inline-block;
  font-size: 16px;
}
#article-status-nav > a {
  text-decoration: none;
}

.active-status {
  color: #52c3f1;
}
.inactive-status {
  color: #fafafa;
}
#operation-nav {
  display: inline-block;
  float: right;
}
/* header */

/* body */
#article-body {
  margin-top: 60px;
}

#article-body > div {
  height: 96px;
  padding: 24px 0;
  /* background-color: yellow; */
  display: flex;
}

.article-meta-wrap {
  color: azure;
  height: 100%;
  width: 400px;
  display: inline-block;
  margin-left: 25px;
}

.article-meta-title {
  vertical-align: middle;
  font-size: 18px;
  line-height: 20px;
}

.article-meta-title > a {
  color: azure;
  text-decoration: none;
}

.article-meta-date {
  margin-top: 18px;
  font-size: 14px;
  line-height: 16px;
}
.article-meta-footer {
  margin-top: 22px;
  line-height: 20px;
}

.article-meta-footer > span {
  margin-left: 5px;
  margin-right: 20px;
}
.article-operation {
  display: flex;
  align-items: center;
  margin-left: 500px;
}

#article-footer {
  float: right;
}
/* body */
</style>