<template>
  <div class="app-container">
    <!-- 头部导航 -->
    <div class="header">
      <div class="nav-back" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回</span>
      </div>
      <el-button type="primary" @click="handleDownload">
        {{ isDownloading ? '生成中...' : '下载PPT' }}
      </el-button>
    </div>

    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading-state">
      <div class="gradient-sphere">
        <div class="loading-content">
          <div class="loading-title">内容生成中</div>
          <div class="loading-progress">{{ progress }}%</div>
        </div>
      </div>
    </div>

    <!-- 内容展示 -->
    <div v-else-if="pptList" class="content-container">
      <img :src="pptList.coverImgSrc" class="cover-image" />
      <div class="text-content">
        <h2>{{ pptList.title }}</h2>
        <p>{{ pptList.subTitle }}</p>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <el-empty description="数据加载失败，请重试">
        <el-button type="primary" @click="retryLoad">重新加载</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ArrowLeft } from '@element-plus/icons-vue'
import { usePPTStore } from '@/stores'
import { ref, onMounted, computed, watch } from 'vue'

const pptStore = usePPTStore()
const { pptList } = pptStore

// 状态管理
const isLoading = ref(true)
const isDownloading = ref(false)
const progress = ref(0)
const pptProgress = ref({
  aiImageStatus: '',
  cardNoteStatus: '',
  pptStatus: ''
}) // 用于存储状态
let progressTimer = null

// 获取PPT进度信息

const getdone = async () => {
  const res = await pptStore.pptprogress()
  pptProgress.value = res // 将获取的状态存入pptProgress中
}
getdone()
// 计算属性：检查是否所有状态都完成
const isAllDone = computed(() => {
  const { aiImageStatus, cardNoteStatus, pptStatus } = pptProgress.value

  return (
    aiImageStatus === 'done' &&
    cardNoteStatus === 'done' &&
    pptStatus === 'done'
  )
})

// 计算属性：下载按钮是否可用
const isDownloadReady = computed(() => {
  return !isLoading.value && pptList.value && isAllDone.value
})

onMounted(async () => {
  startProgressAnimation()
  try {
    await pptStore.getPPTList()
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    clearInterval(progressTimer)
    isLoading.value = false
    progress.value = 100
  }
})

// 进度条动画
const startProgressAnimation = () => {
  progressTimer = setInterval(() => {
    if (progress.value < 90) progress.value += 10
  }, 5000)
}

// 下载处理
// const handleDownload = async () => {
//   console.log(11111);

//   if (!isAllDone.value) {
//     ElMessage.warning('PPT生成尚未完成，请稍候')
//     return
//   }

//   isDownloading.value = true

//   if (isAllDone.value && pptProgress.value.pptUrl) {
//     window.open(pptProgress.value.pptUrl, '_blank')
//   } else {
//     ElMessage.warning('文件尚未准备好，请稍后再试')
//   }

//   isDownloading.value = false
// }

const handleDownload = async () => {
  if (
    pptProgress.value.aiImageStatus === 'done' &&
    pptProgress.value.cardNoteStatus === 'done' &&
    pptProgress.value.pptStatus === 'done'
  ) {

    window.open(pptProgress.value.pptUrl, '_blank')
  } else {
    ElMessage.warning('文件尚未准备好，请稍后再试')
  }
}

const goBack = () => window.history.back()
const retryLoad = () => window.location.reload()
</script>

<style lang="scss" scoped>
.app-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #f0f7ff 0%, #e6f3ff 100%);
}

/* 头部样式 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 56px;
  padding: 0 20px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(28, 41, 61, 0.08);

  .nav-back {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    color: #606266;
    transition: color 0.3s;

    &:hover {
      color: #3d8bff;
    }
  }
}

/* 加载状态 */
.loading-state {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);

  .gradient-sphere {
    width: 200px;
    height: 200px;
    border-radius: 50%;
    background: linear-gradient(135deg, #3d8bff 0%, #6cb7ff 100%);
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;
    overflow: hidden;
    box-shadow: 0 8px 24px rgba(61, 139, 255, 0.2);
    animation: float 3s ease-in-out infinite;

    &::after {
      content: '';
      position: absolute;
      width: 120%;
      height: 120%;
      background: radial-gradient(
        circle,
        rgba(255, 255, 255, 0.15) 0%,
        rgba(255, 255, 255, 0) 70%
      );
      animation: ripple 2s ease-out infinite;
    }
  }

  .loading-content {
    text-align: center;
    color: white;
    z-index: 2;

    .loading-title {
      font-size: 18px;
      margin-bottom: 8px;
      animation: text-fade 2s ease-in-out infinite;
    }

    .loading-progress {
      font-size: 24px;
      font-weight: 500;
    }
  }
}

/* 内容区域 */
.content-container {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;

  .cover-image {
    width: 100%;
    max-height: 400px;
    object-fit: contain;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    margin-bottom: 24px;
  }

  .text-content {
    h2 {
      font-size: 24px;
      color: #1a3353;
      margin-bottom: 8px;
    }

    p {
      font-size: 16px;
      color: #606266;
    }
  }
}

/* 空状态 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 60vh;
}

/* 动画定义 */
@keyframes float {
  0%,
  100% {
    transform: translate(-50%, -53%);
  }
  50% {
    transform: translate(-50%, -47%);
  }
}

@keyframes ripple {
  0% {
    opacity: 1;
    transform: scale(0);
  }
  100% {
    opacity: 0;
    transform: scale(1);
  }
}

@keyframes text-fade {
  0%,
  100% {
    opacity: 0.9;
    transform: translateY(0);
  }
  50% {
    opacity: 1;
    transform: translateY(-3px);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content-container {
    margin: 20px auto;

    .cover-image {
      max-height: 300px;
    }

    .text-content {
      h2 {
        font-size: 20px;
      }
      p {
        font-size: 14px;
      }
    }
  }

  .loading-state .gradient-sphere {
    width: 150px;
    height: 150px;
  }
}
</style>
