<template>
  <div id="login-page-scope">
    <!-- <div class="title-line">
      <span class="title-text">登录</span>
    </div> -->
    <div class="login-div-scope animate__animated animate__zoomInDown">
      <div class="login-type">
        <span
          @click="changeLoginFormVisible(true)"
          class="login-type-button"
          :class="loginTypeActive ? 'login-type-active' : ''"
          >密码登录</span
        >
        <div class="login-type-line"></div>
        <span
          @click="changeLoginFormVisible(false)"
          class="login-type-button"
          :class="loginTypeActive ? '' : 'login-type-active'"
          >账号注册</span
        >
      </div>
      <el-form
        :show-message="false"
        v-show="loginTypeActive"
        :model="loginForm"
        :rules="loginFormRules"
        ref="loginForm"
        label-width="0"
        class="login-form animate__animated animate__zoomInLeft"
      >
        <el-form-item prop="phone">
          <el-input
            :maxlength="11"
            show-word-limit
            type="text"
            placeholder="您的手机号"
            v-model="loginForm.phone"
          ></el-input>
        </el-form-item>
        <el-form-item v-show="loginType == 0" prop="password">
          <el-input
            show-password
            type="password"
            placeholder="密码"
            v-model="loginForm.password"
          ></el-input>
        </el-form-item>
        <el-form-item v-show="loginType == 1" prop="verificationCode">
          <el-input
            type="text"
            placeholder="验证码"
            v-model="loginForm.verificationCode"
          >
            <div slot="suffix" class="send-verification-code-button-scope">
              <el-link
                type="primary"
                @click="sendVerificationCode()"
                :disabled="!verificationCodeButtonActive"
                :underline="false"
                >发送验证码</el-link
              >
            </div>
          </el-input>
        </el-form-item>
      </el-form>

      <!-- 用户注册表单 -->
      <el-form
        :show-message="false"
        v-show="!loginTypeActive"
        :model="registerForm"
        :rules="registerFormRules"
        ref="registerForm"
        label-width="0"
        class="login-form animate__animated animate__zoomInRight"
      >
        <el-form-item prop="avatar">
          <div style="register-form-avatar-scope">
            <input
              @change="uploadAvatar()"
              id="register-form-avatar-input"
              style="display: none"
              type="file"
              name="avatar"
            />
            <img
              @click="clickRegisterFormAvatar()"
              class="register-form-avatar"
              :src="registerForm.avatar"
            />
          </div>
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input
            :maxlength="16"
            type="text"
            placeholder="昵称"
            v-model="registerForm.nickname"
          ></el-input>
        </el-form-item>
        <el-form-item prop="phone">
          <el-input
            :maxlength="11"
            show-word-limit
            type="text"
            placeholder="仅支持中国内陆 +86手机号"
            v-model="loginForm.phone"
          ></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            show-password
            type="password"
            placeholder="密码"
            v-model="registerForm.password"
          ></el-input>
        </el-form-item>
        <el-form-item prop="repassword">
          <el-input
            show-password
            type="password"
            placeholder="确认密码"
            v-model="registerForm.repassword"
          ></el-input>
        </el-form-item>
        <el-form-item prop="verificationCode">
          <el-input
            type="text"
            placeholder="验证码"
            v-model="registerForm.verificationCode"
          >
            <div slot="suffix" class="send-verification-code-button-scope">
              <el-link
                type="primary"
                @click="sendVerificationCode()"
                :disabled="!verificationCodeButtonActive"
                :underline="false"
                >发送验证码</el-link
              >
            </div>
          </el-input>
        </el-form-item>
      </el-form>
      <div v-show="loginTypeActive" class="login-form-buttons-scope">
        <el-button
          @click="login(0)"
          :type="loginType == '0' ? 'primary' : ''"
          v-text="loginType == '0' ? '登录' : '密码登录'"
          size="medium"
          >密码登录</el-button
        >
        <el-button
          @click="login(1)"
          :type="loginType == '1' ? 'primary' : ''"
          v-text="loginType == '1' ? '登录' : '验证码登录'"
          size="medium"
          >验证码登录</el-button
        >
      </div>
      <div v-show="!loginTypeActive" class="login-form-buttons-scope">
        <el-button
          @click="register()"
          class="login-or-register-button"
          type="primary"
          round
          >注册</el-button
        >
      </div>
    </div>
    <canvas class="background"></canvas>
  </div>
</template>
<script>
import particlesjs from "particlesjs";
export default {
  data() {
    var validatePhone = (rule, value, callback) => {
      value = this.loginForm.phone;
      if (value.length != 11) {
        callback(new Error("手机号码必须为11位数字"));
      }
      if (/^[0-9]*$/.test(value)) {
        callback();
      } else {
        callback(new Error("手机号码非法"));
      }
    };
    var validatePassword = (rule, value, callback) => {
      if (value.length < 6 || value.length > 18) {
        callback(new Error("密码长度必须在6-18个字符之间"));
      }

      if (/^[0-9a-zA-Z!._]*$/.test(value)) {
        callback();
      } else {
        callback(new Error("密码只能为数字/字母以及中括号内的符号[!._]"));
      }
    };
    var validateRepassword = (rule, value, callback) => {
      if (this.registerForm.password == this.registerForm.repassword) {
        callback();
      } else {
        callback(new Error("两次密码不一致"));
      }
    };
    var validateVerificationCode = (rule, value, callback) => {
      if (value.length > 0) {
        callback();
      } else {
        callback(new Error("请填写验证码"));
      }
    };
    var validateAvatar = (rule, value, callback) => {
      if (this.registerForm.avatar.length == 0) {
        callback(new Error("头像不能为空"));
      }

      if (/[a-zA-z]+:\/\/[^\s]*/.test(this.registerForm.avatar)) {
        callback();
      } else {
        callback(new Error("非法的头像地址"));
      }
    };
    var validateNickname = (rule, value, callback) => {
      if (
        this.registerForm.nickname.length >= 2 &&
        this.registerForm.nickname.length <= 16
      ) {
        callback();
      } else {
        callback(new Error("昵称的长度必须在2-16个字符之间"));
      }
    };
    return {
      // true:登录, false:注册
      loginTypeActive: true,
      // 0:密码登录 1:验证码登录 2:注册
      loginType: 0,
      loginForm: {
        phone: "",
        password: "",
        verificationCode: "",
      },
      loginFormRules: {
        phone: [{ validator: validatePhone, trigger: "none" }],
        password: [{ validator: validatePassword, trigger: "none" }],
        verificationCode: [
          { validator: validateVerificationCode, trigger: "none" },
        ],
      },
      registerForm: {
        avatar:
          "https://yujing-youtube.oss-cn-guangzhou.aliyuncs.com/user/avatar/avatar.png",
        nickname: "",
        phone: "",
        password: "",
        repassword: "",
        verificationCode: "",
      },
      registerFormRules: {
        avatar: [{ validator: validateAvatar, trigger: "none" }],
        nickname: [{ validator: validateNickname, trigger: "none" }],
        phone: [{ validator: validatePhone, trigger: "none" }],
        password: [{ validator: validatePassword, trigger: "none" }],
        repassword: [{ validator: validateRepassword, trigger: "none" }],
        verificationCode: [
          { validator: validateVerificationCode, trigger: "none" },
        ],
      },
      verificationCodeButtonActive: true,
    };
  },
  watch: {
    loginTypeActive() {
      this.loginForm.password = "";
      this.loginForm.repassword = "";
      this.loginForm.verificationCode = "";
    },
    "$store.state.currentPage"(newValue, oldValue) {
      if (newValue != "Login") {
        this.destroy();
      }
    },
  },
  methods: {
    changeLoginFormVisible(value) {
      if (this.loginTypeActive != value) {
        this.loginTypeActive = value;
      }
    },
    changeLoginType(value) {
      this.loginType = value;
    },
    sendVerificationCode() {
      let success = true;
      let show = true;
      this.$refs.loginForm.validateField(["phone"], (error) => {
        if (error != "") {
          if (show) {
            this.$message.error(error);
            show = false;
            success = false;
          }
        }
      });

      if (!success) {
        return;
      }

      if (!this.verificationCodeButtonActive) {
        return;
      }
      this.verificationCodeButtonActive = false;

      let formData = new FormData();
      formData.append("phone", this.loginForm.phone);
      this.$request.user
        .post("api/user/verification/code", formData)
        .then(({ data }) => {
          if (data.code == 200) {
            this.$message.success("发送成功");
            setTimeout(() => {
              this.verificationCodeButtonActive = true;
            }, 60000);
          } else {
            this.verificationCodeButtonActive = true;
            console.log("发送失败:", data.msg);
            this.$message.error("发送失败");
          }
        })
        .catch((err) => {
          console.log("err: " + err);
          this.$message.error("发送失败");
          this.verificationCodeButtonActive = true;
        });
    },
    login(loginType) {
      if (this.loginType != loginType) {
        this.loginType = loginType;
        return;
      }
      if (this.loginType == 0) {
        this.loginByPassword();
      } else {
        this.loginByCode();
      }
    },
    adjustUserLogin(jwt) {
      window.localStorage.setItem("loginJwt", jwt);
      document
        .getElementById("userIframe")
        .contentWindow.postMessage({ loginJwt: jwt }, "http://user.yujing.icu");
      location.href = this.$globalConfig.product;
    },
    loginByPassword() {
      let show = true;
      this.$refs.loginForm.validateField(["phone", "password"], (error) => {
        if (error != "") {
          if (show) {
            this.$message.error(error);
            show = false;
          }
        }
      });
      // 发送登录请求
      this.$request.user
        .post("api/user/login/by/password", {
          phone: this.loginForm.phone,
          password: this.loginForm.password,
        })
        .then(({ data }) => {
          console.log("data:", data);
          if (data.code == 200) {
            this.adjustUserLogin(data.data);
          } else if (data.code == 89999) {
          } else {
            this.$message.error(data.msg);
          }
        })
        .catch((error) => {
          this.$message.error(error);
        });
    },
    loginByCode() {
      let show = true;
      this.$refs.loginForm.validateField(
        ["phone", "verificationCode"],
        (error) => {
          if (error != "") {
            if (show) {
              this.$message.error(error);
              show = false;
            }
          }
        }
      );
      // 发送登录请求
      this.$request.user
        .post("api/user/login/by/code", {
          phone: this.loginForm.phone,
          verificationCode: this.loginForm.verificationCode,
        })
        .then(({ data }) => {
          console.log("data:", data);
          if (data.code == 200) {
            this.adjustUserLogin(data.data);
          } else if (data.code == 89999) {
          } else {
            this.$message.error(data.msg);
          }
        })
        .catch((error) => {
          this.$message.error(error);
        });
    },
    register() {
      let success = true;
      let show = true;
      this.$refs.registerForm.validateField(
        [
          "phone",
          "avatar",
          "nickname",
          "password",
          "repassword",
          "verificationCode",
        ],
        (error) => {
          if (error != "") {
            if (show) {
              success = false;
              show = false;
              this.$message.error(error);
            }
          }
        }
      );

      if (!success) {
        return;
      }
      // 发送注册请求
      let userLoginVo = this.registerForm;
      userLoginVo.phone = this.loginForm.phone;
      this.$request.user
        .post("api/user/register", userLoginVo)
        .then(({ data }) => {
          if (data.code == 200) {
            location.href = `${this.$globalConfig.product}login`;
          } else {
            this.$message.error(data.msg);
          }
        })
        .catch((error) => {
          console.log("请求失败:", error);
          this.$message.error("请求失败");
        });
    },
    clickRegisterFormAvatar() {
      document.getElementById("register-form-avatar-input").click();
    },
    uploadAvatar() {
      let avatar = document.getElementById("register-form-avatar-input")
        .files[0];

      let formData = new FormData();
      formData.append("avatar", avatar);
      this.$request.user
        .post("api/user/upload/avatar", formData)
        .then(({ data }) => {
          console.log(data);
          if (data.code == 200) {
            this.registerForm.avatar = data.data;
          } else {
            console.log("头像上传失败:", data.msg);
            this.$message.error("头像上传失败");
          }
        })
        .catch((err) => {
          console.log();
          this.$message.error("头像上传失败:", err);
        });
    },
  },
  created() {
    // 关闭侧边栏
    this.$store.commit("changeSidebarVisible", false);
  },
  mounted() {
    particlesjs.init({
      selector: ".background",
      color: "#35e08b",
      maxParticles: 100,
      sizeVariations: 5,
      connectParticles: true,
    });
  },
};
</script>

<style>
.background {
  position: absolute;
  display: block;
  top: 0;
  left: 0;
  z-index: 0;
}

.is-error {
  border-color: none;
}

#login-page-scope {
  width: 100%;
  height: 100%;
  background-color: #181818;
  display: flex;
  justify-content: center;
  align-items: center;
}
.title-line {
  max-width: 980px;
  height: 28px;
  margin: 0 auto;
  border-bottom: 1px solid #ddd;
  margin-bottom: 28px;
  text-align: center;
}

.title-text {
  height: 56px;
  line-height: 56px;
  margin: 0 auto;
  padding: 0 20px;
  font-size: 38px;
  background: #181818;
  text-align: center;
}

.login-div-scope {
  max-width: 500px;
  max-height: 800px;
  width: 500px;
  background-color: #202020;
  margin: 0 auto;
  padding-bottom: 30px;
  position: relative;
  top: -50px;
  z-index: 1;
}

.login-type {
  display: flex;
  justify-content: center;
  font-size: 18px;
  max-height: 20px;
  padding-top: 15px;
  color: #ffffff;
}

.login-type-button {
  cursor: pointer;
}

.login-type-active {
  color: #02a7de;
}

.login-type-line {
  margin: 0 10px 0 10px;
  width: 1px;
  max-height: 20px;
  display: inline-block;
  border-left: 1px white solid;
}

.login-form {
  max-width: 300px;
  margin: 10px auto;
}

.send-verification-code-button-scope {
  width: 100px;
  height: 100%;
}

.login-form-buttons-scope {
  max-width: 300px;
  margin: 0 auto;
  display: flex;
  justify-content: space-around;
}
.register-form-avatar-scope {
  background-color: #02a7de;
  width: inherit;
  height: 100%;
}
.register-form-avatar {
  display: block;
  cursor: pointer;
  width: 64px;
  height: 64px;
  border-radius: 32px;
  margin: 5px auto;
}
</style>