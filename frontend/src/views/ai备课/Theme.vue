<template>
  <div class="app-container">

    <div class="header">
    <div>
        <span class="icon" @click="goBack">
        <el-icon  ><ArrowLeft /></el-icon>     
    </span>
    <span class="back">
        返回
    </span>
    </div>
    <div>
        <el-button type="primary" @click="goppt" >开始生成</el-button>
    </div>
  </div>

  <!-- 选择区域 -->
  <div class="filter-wrapper">
    <div class="filter-container">
    <!-- 行业筛选 -->
    <div class="filter-group">
      <div class="filter-label"><strong>行业：</strong></div>
      <div class="filter-options">
        <el-radio-group v-model="filterParams.industry">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button 
            v-for="item in industries" 
            :key="item.value" 
            :label="item.value"
            @click="getlist"
          >
            {{ item.label }}
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 风格筛选 -->
    <div class="filter-group">
      <div class="filter-label"><strong>风格：</strong></div>
      <div class="filter-options">
        <el-radio-group v-model="filterParams.style">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button 
            v-for="item in styles" 
            :key="item.value" 
            :label="item.value"
            @click="getlist"
          >
            {{ item.label }}
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 颜色筛选 -->
    <div class="filter-group">
      <div class="filter-label"><strong>颜色：</strong></div>
      <div class="filter-options">
        <el-radio-group v-model="filterParams.color">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button 
            v-for="item in colors" 
            :key="item.value" 
            :label="item.value"
            @click="getlist"
          >
            <div class="color-option"
           
            >
              
              {{ item.label }}
            </div>
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>
  </div>
  </div>

  <!-- 选择模版区域 -->
  <div class="template-container">
      <!-- 左列模板列表 -->
      <div class="template-list">
        <el-row :gutter="20">
          <el-col 
            v-for="item in templateData.records" 
            :key="item.templateIndexId"
            :span="12"
            class="template-item"
          >
            <el-card 
              shadow="hover" 
              class="template-card"
              @click="handleSelectTemplate(item)"
            >
              <!-- 模板封面图 -->
              <el-image 
                :src="item.detailImage.titleCoverImage" 
                fit="cover" 
                class="cover-image"
              >
                <template #error>
                  <div class="image-error">封面加载失败</div>
                </template>
              </el-image>

              <!-- 模板信息 -->
              <div class="template-meta">
                <div class="meta-header">
                  <el-tag type="info" size="small">{{ item.pageCount }}页</el-tag>
                  
                </div>
                <h4 class="title">{{ item.industry }} - {{ item.style }}</h4>
                <div class="sub-title">{{ generateSubtitle(item) }}</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 右列模板预览 -->
      <div class="template-preview" v-if="activeTemplate">
        <el-card class="preview-card">
          <!-- 大图预览 -->
          <el-carousel 
            :interval="5000" 
            arrow="always"
            height="400px"
            v-if="activeTemplate.detailImage"
          >
            <el-carousel-item 
              v-for="(url, key) in activeTemplate.detailImage" 
              :key="key"
            >
              <el-image 
                :src="url" 
                fit="contain"
                class="preview-image"
                :preview-src-list="getImageList(activeTemplate.detailImage)"
              >
                <template #error>
                  <div class="image-error">{{ getImageLabel(key) }}加载失败</div>
                </template>
              </el-image>
            </el-carousel-item>
          </el-carousel>

          <!-- 详细信息 -->
          <el-descriptions 
            title="模板详情" 
            :column="1" 
            border
            class="detail-info"
          >
            <el-descriptions-item label="行业">{{ activeTemplate.industry }}</el-descriptions-item>
            <el-descriptions-item label="风格">{{ activeTemplate.style }}</el-descriptions-item>
            <el-descriptions-item label="页数">{{ activeTemplate.pageCount }}</el-descriptions-item>
            <el-descriptions-item label="类型">{{ activeTemplate.payType }}</el-descriptions-item>
          </el-descriptions>

          <!-- 操作按钮 -->
          
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ArrowLeft } from '@element-plus/icons-vue';
import { defineOptions } from 'vue'
import{onMounted} from 'vue'
import { reactive,ref,watch } from 'vue';
import { gettheme } from '@/api/ppt';
import { useRouter } from 'vue-router';
import { usePPTStore } from '@/stores';
const router=useRouter()
const pptStore=usePPTStore()
defineOptions({
  name: 'theme'
})
const goBack=()=>{
  window.history.back()
}
onMounted(()=>{
    getlist()
})

const industries=[
    {
        value:'科技互联网',
        label:'科技互联网'
    },{
        value:'教育培训',
        label:'教育培训'
    },{
        value:'政务',
        label:'政务'
    },{
        value:'学院',
        label:'学院'
    },{
        value:'电子商务',
        label:'电子商务'
    },{
        value:'金融战略',
        label:'金融战略'
    },{
        value:'法律',
        label:'法律'
    },{
        value:'医疗健康',
        label:'医疗健康'
    },{
        value:'文旅体育',
        label:'文旅体育'
    },{
        value:'艺术广告',
        label:'艺术广告'
    },{
        value:'人力资源',
        label:'人力资源'
    },{
        value:'游戏娱乐',
        label:'游戏娱乐'
    }
]

const styles=[
 {
    value:'简约',
    label:'简约'
 } ,{
    value:'卡通',
    label:'卡通'
 },{
    value:'商务',
    label:'商务'
 },{
    value:'创意',
    label:'创意'
 },{
    value:'国风',
    label:'国风'
 },{
    value:'清新',
    label:'清新'
 },{
    value:'扁平',
    label:'扁平'
 },{
    value:'插画',
    label:'插画'
 },{
    value:'节日',
    label:'节日'
 }
]

const colors=[
    {
        value:'蓝色',
        label:'蓝色',
    },{
        value:'红色',
        label:'红色',
    },{
        value:'黄色',
        label:'黄色',
    },{
        value:'绿色',
        label:'绿色',
    },{
        value:'紫色',
        label:'紫色',
    },{
        value:'黑色',
        label:'黑色',
    },{
        value:'灰色',
        label:'灰色',
    },{
        value:'橙色',
        label:'橙色',
    },{
        value:'粉色',
        label:'粉色'
    }
]
const filterParams = reactive({
     // 行业
    style: '',   // 风格
    color: '',
    industry: '',
    pageNum:1,
    pageSize:15    // 颜色
  }) 

const templateData=ref({
 total:15,
 records:[
  
 ] 
})
const activeTemplate=ref(null)
const generateSubtitle=(item)=>{
    const {industry,style,color}=item
    return `${industry} - ${style} - ${color}`
}
const handleSelectTemplate=(item)=>{
    activeTemplate.value=item
    pptStore.pptquery.templateId=item.templateIndexId
    
    
}
const getlist= async()=>{
    
    const res=await gettheme(filterParams)
    templateData.value.total=[...res.records]
    templateData.value.records=res.records

}
const getImageList = (detailImage) => {
  return Object.values(detailImage).filter(url => url.startsWith('http'))
}

const getImageLabel = (key) => {
  const labelMap = {
    titleCoverImageLarge: '封面大图',
    titleCoverImage: '封面缩略图',
    catalogueCoverImage: '目录页',
    chapterCoverImage: '章节页', 
    contentCoverImage: '内容页',
    endCoverImage: '结束页'
  }
  return labelMap[key] || '未命名图片'
}

const handleThumbnailClick = (key) => {
  const index = Object.keys(activeTemplate.value.detailImage).indexOf(key)
  if(index >= 0) {
    // 联动轮播图跳转到对应位置
    carouselRef.value.setActiveItem(index)
  }
}
const goppt=()=>{
    router.push({
        path:'/ppt',
        query:{
            templateId:activeTemplate.value.templateIndexId,
          
        }
    })
}

watch(()=>filterParams,async()=>{
    await getlist()
})
</script>

<style lang="scss" scoped>
.app-container {
  width: 100%;
  padding: 0;
  background: #fff; // 调整为更明亮的浅蓝
  min-height: 100vh;
}

.header {
  width: 100%;
 
//   padding: 0 20px;
  height: 56px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(28, 41, 61, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
    div{
       padding: 0 20px; 
    }
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
.filter-wrapper {
  max-width: 1200px;
  margin: 0px auto;
  padding: 0 20px;
}

.filter-container {
  background: #fff;
  padding: 16px;
  border-radius: 4px;
 
}

.filter-group {
  display: flex;
  align-items: center;
  margin-bottom: 16px;

  &:last-child {
    margin-bottom: 0;
  }
}

.filter-label {
  width: 60px;
  color: #606266;
  font-size: 14px;
}

.filter-options {
  flex: 1;
  
  :deep(.el-radio-group) {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  :deep(.el-radio-button) {
    // 默认状态无边框
    .el-radio-button__inner {
      padding: 8px 10px;
      border-radius: 0;
      border: 0;
      background: transparent;
      color: #606266;
    //   transition: all 0.3s;
      
      &:hover {
        color: #409EFF;
      }
    }

    // 选中状态显示边框
    &.is-active {
      .el-radio-button__inner {
        border: 1px solid #409EFF;
        border-radius: 20px;
        background: #ecf5ff;
        color: #409EFF;
      }
    }
  }
}

.template-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 24px;
}

.template-list {
  .template-item {
    margin-bottom: 20px;

    .template-card {
      cursor: pointer;
      transition: transform 0.2s;

      &:hover {
        transform: translateY(-3px);
      }
    }

    .cover-image {
      width: 100%;
      height: 180px;
      border-radius: 4px;
    }

    .template-meta {
      padding: 12px 0;

      .meta-header {
        display: flex;
        gap: 8px;
        margin-bottom: 8px;
      }

      .title {
        margin: 0;
        font-size: 14px;
        color: #303133;
        
      }

      .sub-title {
        font-size: 12px;
        color: #909399;
        margin-top: 4px;
        
      }
    }
  }
}

.template-preview {
  .preview-card {
    position: sticky;
    top: 80px;
    
    .preview-image {
      width: 100%;
      height: 360px;
      border-radius: 4px;
    }

    .detail-info {
      margin-top: 16px;

      :deep(.el-descriptions__title) {
        font-size: 16px;
        color: #303133;
      }
    }

    .action-buttons {
      margin-top: 16px;
      display: flex;
      gap: 12px;
      justify-content: flex-end;
    }
  }
}

.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 12px;
}
/* 轮播图样式 */
.preview-image {
  width: 100%;
  height: 400px;
  background: #f5f7fa;
}

/* 缩略图网格 */
.thumbnail-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-top: 16px;
}

.thumbnail-item {
  position: relative;
  cursor: pointer;
  border-radius: 4px;
  overflow: hidden;
  transition: transform 0.2s;
  
  &:hover {
    transform: translateY(-3px);
  }
}

.thumbnail-image {
  width: 100%;
  height: 80px;
}

.thumbnail-label {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0,0,0,0.5);
  color: white;
  font-size: 12px;
  padding: 4px;
  text-align: center;
}
</style>