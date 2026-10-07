<template>
  <div class="container">
    <el-row class="login-page">
      <el-col :span="8" class="bg"></el-col>
      <el-col :span="6" class="form">
        <!-- <img src="../../assets/logo.jpeg" /> -->

        <!-- 注册表单 -->
        <el-form
          :model="formModel"
          :rules="rules"
          ref="form"
          size="large"
          autocomplete="off"
          v-if="isRegister"
        >
          <el-form-item>
            <h1>注册</h1>
          </el-form-item>
          <el-form-item prop="username">
            <el-input
              :prefix-icon="User"
              placeholder="请输入用户名"
              v-model="formModel.username"
            ></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              :prefix-icon="Lock"
              type="password"
              placeholder="请输入密码"
              v-model="formModel.password"
            ></el-input>
          </el-form-item>
          <el-form-item prop="code" class="code">
            <div>
              <el-input
                v-model="formModel.code"
                :prefix-icon="Lock"
                type=""
                placeholder="请输入验证码"
              ></el-input>
              <img @click="getCode" :src="imageData" alt="" />
            </div>
          </el-form-item>
          <el-form-item>
            <el-button
              class="button"
              type="primary"
              auto-insert-space
              @click="register"
            >
              注册
            </el-button>
          </el-form-item>
          <el-form-item class="flex">
            <el-link type="info" :underline="false" @click="isRegister = false">
              ← 返回
            </el-link>
          </el-form-item>
        </el-form>

        <!-- 注册表单 -->
        <el-form
          :model="formModel"
          :rules="rules"
          ref="form"
          size="large"
          autocomplete="off"
          v-else
        >
          <el-form-item>
            <h1>登录</h1>
          </el-form-item>
          <el-form-item prop="username">
            <el-input
              :prefix-icon="User"
              placeholder="请输入用户名"
              v-model="formModel.username"
            ></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="formModel.password"
              name="password"
              :prefix-icon="Lock"
              type="password"
              placeholder="请输入密码"
            ></el-input>
          </el-form-item>
          <el-form-item class="code" prop="code">
            <div>
              <el-input
                v-model="formModel.code"
                :prefix-icon="Lock"
                type=""
                placeholder="请输入验证码"
              ></el-input>
              <div>
                <img @click="getCode" :src="imageData" alt="" />
              </div>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button
              class="button"
              type="primary"
              auto-insert-space
              @click="login"
              >登录</el-button
            >
          </el-form-item>
          <el-form-item class="flex">
            <el-link type="info" :underline="false" @click="isRegister = true">
              注册 →
            </el-link>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { User, Lock } from '@element-plus/icons-vue'
import { ref, onMounted, watch } from 'vue'
import { usergetcode, userregister, userlogin } from '@/api/login'
import { useUserStore } from '@/stores/user'
// import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
const router = useRouter()
const formModel = ref({
  username: '',
  password: '',
  code: ''
})
const userStore = useUserStore()
const isRegister = ref(false)
const imageData = ref('')
const sessionId = ref('')
onMounted(() => {
  //获取验证码
  getCode()
})
const getCode = async () => {
  const res = await usergetcode()
  // console.log(res);
  imageData.value = `data:image/png;base64,${res.imageData}`
  sessionId.value = res.sessionId

  userStore.setSessionId(sessionId.value)
}
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 10, message: '长度在3到10个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 12, message: '长度在6到12个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 4, max: 4, message: '验证码为4位', trigger: 'blur' }
  ]
}
const form = ref(null)
const register = async () => {
  await form.value.validate()
  await userregister({ ...formModel.value, sessionId: sessionId.value })
  ElMessage.success('注册成功')
  isRegister.value = false
}

const login = async () => {
  await form.value.validate()

  const res = await userlogin({
    ...formModel.value,
    sessionId: sessionId.value
  })

  userStore.token = res.token
  userStore.setUser(res)
  ElMessage.success('登录成功')
  router.push('/create/plan')
}
watch(isRegister, () => {
  ;(formModel.value = {
    username: '',
    password: '',
    code: ''
  }),
    getCode()
})
</script>

<style lang="scss" scoped>
.container {
  height: 100vh;

  /* 背景部分的样式 */
}
.login-page {
  height: 100vh;
  background-color: #fff;
  background-image: url('../../assets/bg5.jpg');
  background-size: cover;
  background-position: center;
  // background-repeat: no-repeat;
  // 如果仍然存在对齐问题，可以添加以下代码
  .el-form-item__content {
    line-height: 1; // 消除默认行高对布局的影响
  }
  .form {
    display: flex;
    flex-direction: column;
    justify-content: center;
    user-select: none;
    .title {
      margin: 0 auto;
    }
    .button {
      width: 100%;
    }
    .flex {
      width: 100%;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    // 关键修改：使用深度选择器穿透组件样式
    :deep(.code) {
      .el-form-item__content {
        // 需要定位到 Element 生成的内容容器
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 10px; // 添加间距控制

        > div {
          // 包裹输入框和图片的容器
          flex: 1;
          display: flex;
          align-items: center;
          gap: 10px;
        }
      }

      .el-input {
        // 输入框样式调整
        flex: 1;
        min-width: 160px; // 防止内容过小时挤压
      }

      img {
        // 验证码图片
        flex-shrink: 0; // 禁止缩小
        width: 100px;
        height: 38px;
      }
    }
  }
}
</style>
