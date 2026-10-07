<template>
  <el-container class="layout-container">
    <el-aside width="180px">
      <div class="el-aside__logo"></div>
      <el-menu
        class="el-menu-vertical-demo"
        mode="vertical"
        text-color="#303133"
        :default-active="$route.path"
        router
      >
        <el-menu-item index="/create/plan">
          <el-icon><Tickets /></el-icon>
          <span>智能备课</span>
        </el-menu-item>
        <el-menu-item index="/create/knowledge-plan">
          <el-icon><Collection /></el-icon>
          <span>知识库备课</span>
        </el-menu-item>
        <el-menu-item index="/create/ppt">
          <el-icon><Crop /></el-icon>
          <span>智能助手</span>
        </el-menu-item>
        <el-menu-item index="/study/analysis">
          <el-icon><Promotion /></el-icon>
          <span>学情分析</span>
        </el-menu-item>
       
        <el-sub-menu index="/user">
            <template #title>
              <el-icon><UserFilled /></el-icon>
              <span>个人中心</span>
            </template>
            <el-menu-item index="/user/mydoc">
              <el-icon><Management /></el-icon>
              <span>我的文档</span>
            </el-menu-item>
            <el-menu-item index="/user/profile">
              <el-icon><User /></el-icon>
              <span>基本资料</span>
            </el-menu-item>
            <el-menu-item index="/user/password">
              <el-icon><EditPen /></el-icon>
              <span>修改密码</span>
            </el-menu-item>
            

        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div>你好：<strong>{{ userStore.user.nickName || userStore.user.username }}</strong></div>
        <el-dropdown placement="bottom-end" @command="handleCommand">
          <span class="el-dropdown__box">
            <el-avatar :src="avatar"></el-avatar>
            <el-icon><CaretBottom /></el-icon>
          </span>
          <template #dropdown>
           <el-dropdown-menu>
            <el-dropdown-item command="create" :icon="Edit">
              智能备课
            </el-dropdown-item>
            <el-dropdown-item command="profile" :icon="User">
              基本资料
            </el-dropdown-item>
            <el-dropdown-item command="password" :icon="EditPen">
              修改密码
            </el-dropdown-item>
            <el-dropdown-item command="logout" :icon="SwitchButton">
              退出登录
            </el-dropdown-item>
           </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main>
      <router-view v-slot="{ Component }">
      <keep-alive :include="cachedViews">
        <component 
          :is="Component" 
          :key="$route.fullPath"
          v-if="$route.meta.KeepAlive"
        />
      </keep-alive>
      <component 
        :is="Component" 
        :key="$route.fullPath"
        v-if="!$route.meta.KeepAlive"
      />
    </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import {
  Management,
  Promotion,
  UserFilled,
  User,
  Crop,
  EditPen,
  SwitchButton,
  CaretBottom,
  Edit,
  Tickets,
  Collection

} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores';
import { onMounted } from 'vue';
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router';
import  avatar from '@/assets/default.png'
import {  ElMessageBox } from 'element-plus';
const userStore = useUserStore();
const router = useRouter();
onMounted(()=>{
  userStore.getUser()
})
const handleCommand=(command)=>{
  switch(command){
    case 'create':
      router.push('/create/plan')
      break;
    case 'profile':
      router.push('/user/profile')
      break;
    case 'password':
      router.push('/user/password')
      break;
    case 'logout':
      ElMessageBox.confirm('确定退出登录？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning', 
      }).then(()=>{
        ElMessage.success('退出登录成功')
        userStore.setToken('')
        userStore.setUser({})
        router.push('/login')
      }).catch(()=>{
        ElMessage.info('已取消退出登录')
      })
      break;
  }
}
const cachedViews = ref([
  'create-plan',
  'chat',
  'user-profile',
  'user-password',
  'user-mydoc'
])

const route = useRoute()
watch(route, (newVal) => {
  if (newVal.meta.KeepAlive && !cachedViews.value.includes(newVal.name)) {
    cachedViews.value.push(newVal.name)
  }
})
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  .el-aside {
    background-color: #F9FBFF;
    &__logo {
      height: 120px;
      background: url('@/assets/logo.jpeg') no-repeat center / 120px auto;
    }
    .el-menu {
      border-right: none;
    }
  }
  .el-header {
    background-color: #F9FBFF;
    display: flex;
    align-items: center;
    justify-content: space-between;
    .el-dropdown__box {
      display: flex;
      align-items: center;
      .el-icon {
        color: #999;
        margin-left: 10px;
      }

      &:active,
      &:focus {
        outline: none;
      }
    }
  }
  .el-main{
    padding: 0px;
  }
  .el-footer {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    color: #666;
  }
}
</style>
