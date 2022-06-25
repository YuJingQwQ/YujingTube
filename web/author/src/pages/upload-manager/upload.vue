<template>
  <div id="upload-div">
    <el-upload
      v-if="!uploadedFile"
      class="video-uploader"
      drag
      action="https://jsonplaceholder.typicode.com/posts/"
      :show-file-list="false"
      :http-request="VideoUploader"
    >
      <div class="video-uploader-content">
        <img
          style="width: 62px; height: 62px"
          :src="$globalConfig.author + 'static/author/img/upload.png'"
          alt=""
        />
        <p>拖拽文件到此框内可上传</p>
        <span style="font-size: 14px">文件格式只支持[mp4,mkv,flv]</span>
        <span style="font-size: 14px">文件大小不能超过100MB</span>
      </div>
    </el-upload>

    <div v-if="uploadedFile" id="details">
      <div class="file-upload-progress-scope">
        <div>
          <img
            class="uploaded-file-img"
            :src="`${$globalConfig.product}static/common/img/file.png`"
          />
        </div>
        <div class="file-upload-progress">
          <el-progress
            style="width: 840px"
            :color="fileUploadingProgressColor"
            :percentage="fileUploadingProgress"
          ></el-progress>
        </div>
      </div>
      <span class="details-title">基本设置</span>
      <el-form
        :rules="videoValidationRules"
        :show-message="false"
        :model="video"
        ref="videoForm"
        label-width="150px"
        label-position="left"
      >
        <el-form-item
          error="视频封面不能为空"
          label="封面"
          prop="coverUrl"
          required
        >
          <el-upload
            class="video-cover-uploader"
            action="https://jsonplaceholder.typicode.com/posts/"
            :show-file-list="false"
            :http-request="videoCoverUploader"
          >
            <div id="video-cover-div">
              <img
                style="width: 100%; height: 100%"
                v-if="video.coverUrl"
                :src="video.coverUrl"
                class="avatar"
              />
              <i
                v-else
                class="
                  el-icon-plus
                  avatar-uploader-icon
                  video-cover-uploader-icon
                "
              ></i>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item
          error="标题长度为1-80个字符"
          label="标题"
          prop="title"
          required
        >
          <el-input
            show-word-limit
            :maxlength="80"
            :minlength="1"
            size="medium"
            class="video-title-input"
            v-model="video.title"
          ></el-input>
        </el-form-item>
        <el-form-item
          error="类型必须为自制或转载"
          label="类型"
          prop="type"
          required
        >
          <el-radio v-model="video.type" :label="0">自制</el-radio>
          <el-radio v-model="video.type" :label="1">转载</el-radio>
        </el-form-item>
        <el-form-item label="分区" prop="zoneId" required>
          <el-select v-model="video.zoneId" placeholder="请选择分区">
            <el-option
              v-for="zone in zones"
              :key="zone.id"
              :label="zone.label"
              :value="zone.id"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input
            class="video-description-input"
            size="medium"
            :maxlength="2000"
            resize="none"
            type="textarea"
            :rows="8"
            placeholder="对作品的描述"
            v-model="video.description"
            show-word-limit
          >
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="uploadDetails" type="primary">立即投稿</el-button>
          <!-- <el-button>保存模板</el-button> -->
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    var validateCoverUrl = (rule, value, callback) => {
      if (value.length == 0) {
        callback(new Error("视频封面不能为空"));
      } else {
        callback();
      }
    };
    var validateTitle = (rule, value, callback) => {
      if (!value || value.length < 1 || value.length > 80) {
        callback(new Error("标题必须在1-80个字符之间"));
      } else {
        callback();
      }
    };
    var validateType = (rule, value, callback) => {
      if (value == 0 || value == 1) {
        callback();
      } else {
        callback(new Error("视频类型必须为自制或转载"));
      }
    };
    var validateZoneId = (rule, value, callback) => {
      if (!Number.isInteger(value)) {
        callback(new Error("分区不能为空"));
      } else {
        callback();
      }
    };
    return {
      uploadedFile: false,
      fileUploadingProgress: 0,
      fileUploadingProgressColor: "#67C23A",
      video: {
        title: "",
        type: 0,
        zoneId: undefined,
        coverUrl: "",
        videoUrl: "",
        description: "",
        videoFile: "",
      },
      zones: [],

      videoValidationRules: {
        coverUrl: [{ validator: validateCoverUrl }],
        title: [{ validator: validateTitle }],
        type: [{ validator: validateType }],
        zoneId: [{ validator: validateZoneId }],
      },
    };
  },
  methods: {
    // 上传视频后初始化表单
    initDetails() {
      this.$request.product
        .get("api/zones")
        .then(({ data }) => {
          if (data.code == 200) {
            this.zones = data.data;
          }
        })
        .catch((error) => {});
    },
    async videoSlice(file) {
      let fileTotalSize = file.size;
      let uploadedFileSize = 0;
      // 每次请求发送5MB数据
      let sliceLength = 1024 * 1024 * 5;
      let start = 0;
      let end = 0;
      let errorCount = 0;
      let uploadingId = "";

      while (end <= fileTotalSize) {
        end = start + sliceLength;
        let fileBlob = undefined;
        if (end >= fileTotalSize) {
          fileBlob = file.slice(start, fileTotalSize);
        } else {
          fileBlob = file.slice(start, end);
        }

        let sliceFile = new File([fileBlob], file.name, {
          type: file.type,
        });
        let form = new FormData();
        form.append("file", sliceFile);
        form.append("uploadingId", uploadingId);
        let success = true;
        await this.$request.product
          .post("api/author/upload/article/slice", form)
          .then(({ data }) => {
            if (data.code == 200) {
              uploadingId = data.data;
              uploadedFileSize = uploadedFileSize + sliceFile.size;
              console.log("uploadedFileSize:", uploadedFileSize);
              this.fileUploadingProgress =
                (uploadedFileSize / fileTotalSize) * 100;
            } else {
              success = false;
              errorCount = errorCount + 1;
            }
          })
          .catch((err) => {
            success = false;
            errorCount = errorCount + 1;
          });

        if (success) {
          start = end;
        } else {
          if (errorCount > 200) {
            this.$message.error("上传失败次数过多,取消上传");
            return;
          }
        }
      }

      // 发送一个文件发送完毕请求
      this.$request.product
        .post(`api/author/upload/article/end?uploadingId=${uploadingId}`)
        .then(({ data }) => {
          if (data.code == 200) {
            console.log("上传成功");
            this.video.videoUrl = data.data;
            this.initDetails();
            this.uploadedFile = true;
          } else {
            this.$message.error(data.msg);
          }
        })
        .catch((err) => {
          console.log("axios发送文件结束失败");
        });
    },
    // 上传视频
    VideoUploader({ file }) {
      // 具体格式支持未测验,已测验的格式为 MP4 和 mkv
      let checkFileSuffix = this.$yujingUtils.checkFileSuffix(file.name, [
        "flv",
        "mp4",
        // "mov",
        "mkv",
      ]);

      if (!checkFileSuffix) {
        // this.$message.error("文件格式只支持[mp4,mkv,mov,flv]");
        this.$message.error("文件格式只支持[mp4,mkv,flv]");
        return;
      }

      let checkFileSize = this.$yujingUtils.checkFileSize(file.size, 100, "MB");
      if (!checkFileSize) {
        this.$message.error("文件大小不能超过100MB");
        return;
      }

      this.uploadedFile = true;

      this.videoSlice(file);
    },
    // 上传视频封面
    videoCoverUploader({ file }) {
      let checkFileSuffix = this.$yujingUtils.checkFileSuffix(file.name, [
        "jpg",
        "jpeg",
        "png",
      ]);

      if (!checkFileSuffix) {
        this.$message.error("文件格式只支持[jpg,jpeg,png]");
        return;
      }

      let checkFileSize = this.$yujingUtils.checkFileSize(file.size, 10, "MB");
      if (!checkFileSize) {
        this.$message.error("文件大小不能超过10MB");
        return;
      }

      let coverForm = new FormData();
      coverForm.append("cover", file);

      this.$request.product
        .post("api/author/upload/article/cover", coverForm, {
          headers: { ContentType: "multipart/form-data" },
        })
        .then(({ data }) => {
          console.log("data:", data);
          if (data.code == 200) {
            console.log("上传成功");
            this.video.coverUrl = data.data;
          } else {
            this.$message.error(data.msg);
          }
        })
        .catch((error) => {
          this.$message.error("请求失败");
        });
    },
    // 上传视频详细信息
    uploadDetails() {
      this.$refs.videoForm.validate((result, wrongFields) => {
        if (result) {
          // 成功
          this.$request.product
            .post("api/author/upload/article/details", this.video)
            .then(({ data }) => {
              if (data.code == 200) {
                this.$message.success("上传成功,正在等待审核");
                // 路由到稿件管理
                this.$router.push("/upload_manager/article");
              } else {
                this.$message.error(data.msg);
              }
            })
            .catch((error) => {});
        } else {
          // 失败
          for (let field in wrongFields) {
            this.$message.error(wrongFields[field][0].message);
            return;
          }
        }
      });
    },
  },
  mounted() {
    this.initDetails();
  },
};
</script>

<style scoped>
#upload-div {
  width: 1100px;
  height: 500px;
  margin: 0 auto;
}

/* 上传视频框 */
.video-uploader {
  width: 600px;
  height: 400px;
  margin: 0 auto;
}
.video-uploader-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 240px;
  margin: 120px auto;
  color: #ffffff;
}

.file-upload-progress-scope {
  height: 78px;
  display: flex;
  align-items: center;
}
.uploaded-file-img {
  width: 38px;
  height: 38px;
}

.file-upload-progress {
  margin-left: 15px;
  max-width: 850px;
}

/* 上传视频详细信息 */
#details {
  padding-left: 50px;
  padding-top: 20px;
  background-color: #202020;
  color: #ffffff;
  width: 1100px;
  height: 800px;
}
/* 详细信息标题 */
.details-title {
  display: block;
  color: aliceblue;
  font-size: 20;
}

.video-cover-uploader {
  background-color: #181818;
  width: 190px;
  height: 120px;
}
#video-cover-div {
  width: 190px;
  height: 120px;
}
.video-cover-uploader-icon {
  margin-left: auto;
  margin-right: auto;
  position: relative;
  top: 50%;
  transform: translateY(-50%);
  font-size: 30px;
}
.video-title-input {
  width: 600px;
  height: 36px;
}
.video-description-input {
  width: 745px;
  /* height: 160px; */
}
</style>

<style >
.el-upload-dragger {
  background-color: #202020;
}

.video-uploader .el-upload-dragger {
  width: 600px;
  height: 400px;
}
</style>