<template>
<div class="app-container">

    
  <div class="header">
    <span class="icon" @click="goBack">
        <el-icon  ><ArrowLeft /></el-icon>     
    </span>
    <span class="back">
        返回
    </span>
  </div>

  <!-- 生成表单区域 -->
  <div class="main-content" >
      <el-card class="generate-card">
        <h2 class="card-title">一句话创建</h2>
        <p class="card-tip">输入主题后，AI将自动生成结构化大纲</p>

        <div class="config-section">
          <el-input
            v-model="formData.pptTheme"
            placeholder="输入PPT主题"
            class="theme-input"
            clearable
          >
            <template #suffix>
              <el-button 
                type="primary" 
                :loading="loading"
                @click="handleGenerate"
              >
                立即生成
              </el-button>
            </template>
          </el-input>

          <div class="config-options">
            <el-select
              v-model="formData.language"
              placeholder="选择语言"
              class="config-select"  
            >
              <el-option
                v-for="item in langOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 大纲展示区域 -->
    <div class="outline-wrapper" v-if="showOutline">
        <el-card class="result-card">
        <!-- 当前大纲标题 -->
        <div class="current-outline">
            <h3>当前大纲</h3>
            
        </div>

        <!-- 大纲层级结构 -->
        <div class="outline-tree">
            <div v-for="(chapter, index) in resultData.chapters" 
                :key="index"
                class="chapter-item">
            <!-- 章节标题 -->
            <div class="chapter-header">
                <el-icon class="chapter-icon"><Document /></el-icon>
                <div class="chapter-title">
                <span class="chapter-tag">章节{{ index +1 }}</span>
                {{ chapter.chapterTitle }}
                </div>
            </div>

            <!-- 子内容区域 -->
            <div class="sub-contents">
                <div v-for="(content, cIndex) in chapter.chapterContents"
                    :key="cIndex"
                    class="content-item">
                <div class="content-line">
                    <el-icon class="content-type-icon" :class="{ 'catalog-icon': cIndex%2 }">
                        <Collection v-if="cIndex%2" />  <!-- 奇数项显示目录图标 -->
                        <Reading v-else />              <!-- 偶数项显示正文图标 -->
                    </el-icon>
                    <span class="content-text">{{ content.chapterTitle }}</span>
                </div>
                <!-- 分割线 -->
                <div class="divider" v-if="cIndex < chapter.chapterContents.length -1"></div>
                </div>
            </div>
            </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
            <div class="left">
            <el-button type="primary" plain>我们生成的效果怎么样?</el-button>
            </div>
            <div class="right">
            <el-button @click="showOutline = false">重新生成</el-button>
            <el-button type="primary" @click="gotheme">下一步</el-button>
            </div>
        </div>
        </el-card>
    </div>
</div>   
</template>

<script setup>
import { getmain } from '@/api/ppt'
import { Collection, InfoFilled, Document, Reading } from '@element-plus/icons-vue'
import { ref, reactive } from 'vue'
import { defineOptions } from 'vue'
import { ArrowLeft } from '@element-plus/icons-vue'

import { useRouter } from 'vue-router'
import { usePPTStore } from '@/stores'
const router = useRouter()
defineOptions({
  name: 'ai-ppt'
})
const pptStore = usePPTStore()

// 表单数据
const formData = reactive({
  pptTheme: '',
  language: 'cn'
})

pptStore.pptquery.language=formData.language


const goBack=()=>{
  window.history.back() 
}

// 状态管理
const loading = ref(false)
const showOutline = ref(false)
const resultData = reactive({
  title: '',
  subTitle: '',
  chapters: []
})
const handleGenerate = async () => {
  if (!formData.pptTheme.trim()) {
    return ElMessage.warning('请输入PPT主题')
  } 
  loading.value = true
  pptStore.pptquery.query=formData.pptTheme
  const res=await getmain(formData)
  loading.value = false
  resultData.title=res.title
  resultData.subTitle=res.subTitle
  resultData.chapters=res.chapters

  pptStore.pptquery.outline.title=res.title
  pptStore.pptquery.outline.subTitle=res.subTitle
  pptStore.pptquery.outline.chapters=res.chapters
  showOutline.value = true


}
const langOptions=[
    {
        value:'cn',
        label:'中文'
    },{
        value:'en',
        label:'英语'
    },{
        value:'ja',
        label:'日语'
    },{
        value:'ko',
        label:'韩语'
    },{
        value:'ru',
        label:'俄语'
    },{
        value:'de',
        label:'德语'
    },{
        value:'fr',
        label:'法语'
    },{
        value:'es',
        label:'西班牙语'
    },{
        value:'it',
        label:'意大利语'
    },{
        value:'pt',
        label:'葡萄牙语'
    },{
        value:'th',
        label:'泰语'
    }
]
const gotheme=()=>{
  router.push({ path: '/theme' })
}
</script>

<style lang="scss" scoped>
.app-container {
  width: 100%;
  padding: 0;
  background: #f7faff; // 调整为更明亮的浅蓝
  min-height: 100vh;
 
}

// 顶部返回栏全宽样式
.header {
  width: 100%;
  padding: 0 20px;
  height: 56px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(28, 41, 61, 0.08);
  display: flex;
  align-items: center;

  .icon {
    padding: 0;
    :deep(.el-icon) {
      color: #606266;
      & + .back { // 文字与图标间距
        margin-left: 8px;
      }
    }
  }
}

// 主内容容器
.main-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px 20px;
}

// 配置区域弹性布局优化
.config-section {
  .config-options {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-top: 20px;

    // 小尺寸下拉框
    .config-select {
      flex: 0 0 180px; // 固定宽度
      :deep(.el-input__wrapper) {
        height: 36px;
        border-radius: 6px;
        padding: 0 12px;
      }
    }

    // 图片模型选择器
    .model-select {
      flex: 0 0 200px;
    }

    // 操作按钮组
    .action-buttons {
      display: flex;
      gap: 8px;
      margin-left: auto; // 右对齐
      
      .el-button {
        height: 36px;
        padding: 0 16px;
        border-radius: 6px;
        font-size: 13px;
        &.think-btn {
          background: linear-gradient(135deg, #85bef9 0%, #1a73e8 100%);
        }
        &.search-btn {
          background: linear-gradient(135deg, #a8e6cf 0%, #67c23a 100%);
        }
      }
    }
  }
}

// 输入框样式微调
.theme-input {
  :deep(.el-input__wrapper) {
    box-shadow: 0 2px 6px rgba(25, 118, 210, 0.12) !important;
    &:hover {
      box-shadow: 0 2px 10px rgba(25, 118, 210, 0.2) !important;
    }
  }
}



// 操作按钮区
.action-bar {
  margin-top: 48px;
  .el-button {
    min-width: 120px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
  }
}

.outline-wrapper {
  max-width: 800px;
  margin-top: 32px;
  padding: 20px 20px;

  .current-outline {
    display: flex;
    align-items: center;
    margin-bottom: 24px;
    
    h3 {
      font-size: 18px;
      color: #1a1a1a;
      margin-right: 24px;
    }

    .edit-tools {
      display: flex;
      align-items: center;
      gap: 16px;

      .drag-tip {
        color: #999;
        font-size: 13px;
        display: flex;
        align-items: center;

        .el-icon {
          margin-right: 4px;
          font-size: 14px;
        }
      }
    }
  }

  .outline-tree {
    border: 1px solid #ebeef5;
    border-radius: 8px;
    background: #fff;
    

    .chapter-item {
      padding: 16px 24px;
      border-bottom: 1px solid #f0f2f5;

      &:last-child {
        border-bottom: none;
      }

      .chapter-header {
        display: flex;
        align-items: center;
        margin-bottom: 12px;

        .chapter-icon {
          color: #1a73e8;
          font-size: 18px;
          margin-right: 8px;
        }

        .chapter-title {
          font-size: 15px;
          color: #303133;

          .chapter-tag {
            background: #f0f6ff;
            color: #1a73e8;
            padding: 2px 6px;
            border-radius: 4px;
            margin-right: 8px;
            font-size: 13px;
          }
        }
      }

      .sub-contents {
        margin-left: 32px;

        .content-item {
          .content-line {
            display: flex;
            align-items: center;
            padding: 8px 0;

            .content-type-icon {
              color: #67c23a;
              font-size: 16px;
              margin-right: 12px;

              &.catalog-icon {
                color: #e6a23c;
              }
            }

            .content-text {
              color: #606266;
              font-size: 14px;
            }
          }

          .divider {
            height: 1px;
            background: repeating-linear-gradient(
              90deg,
              transparent 0,
              transparent 4px,
              #ebeef5 4px,
              #ebeef5 8px
            );
            margin: 8px 0;
          }
        }
      }
    }
  }

  .action-buttons {
    margin-top: 32px;
    display: flex;
    justify-content: space-between;

    .right {
      display: flex;
      gap: 12px;

      .el-button {
        min-width: 100px;
        border-radius: 20px;
      }
    }
  }
}
.app-container {
  width: 100%;
  padding: 0;
  background: #f7faff;
  min-height: 100vh;
  background-image: url('../../assets/bg4.png'); // 替换为你的背景图片路径
  background-size: cover; // 图片拉伸到覆盖整个容器
  background-position: center; // 图片居中
  background-repeat: no-repeat; // 防止图片重复
}

.common-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0px 20px;
  box-sizing: border-box;

  @media (max-width: 768px) {
    padding: 16px;
    max-width: 100%;
  }
}

.main-content {
  @extend .common-container;
  padding-top: 0;

  .generate-card {
    width: 100%;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(25, 118, 210, 0.08);
  }
}

.outline-wrapper {
  @extend .common-container;
  margin-top: 32px;

  .result-card {
    width: 100%;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(25, 118, 210, 0.08);
  }
  }
</style>