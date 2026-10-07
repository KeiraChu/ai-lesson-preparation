<template>
  <PageContainer title="学情分析">
    <template #extra>
      <div class="upload-actions">
        <el-upload
          ref="uploadRef"
          class="upload-demo"
          :limit="1"
          :auto-upload="false"
          :on-change="handleFileChange"
        >
          <template #trigger>
            <el-button type="primary" :icon="Plus">选择文件</el-button>
          </template>
        </el-upload>

        <el-button type="success" @click="submitUpload">
          <el-icon><Upload /></el-icon>
          上传
        </el-button>
      </div>
    </template>

    <div class="main">
      <el-table :data="analysisStore.analysisData" style="width: 100%">
        <el-table-column label="文件Id" prop="id"></el-table-column>
        <el-table-column label="文件名称" prop="title" />
        <el-table-column label="上传时间" prop="createTime" />
        <el-table-column label="查看文件" prop="url">
          <template #default="{ row }">
            <el-button type="primary" @click="handleView(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="footer">
      <el-button type="success" @click="goanalysis">学情分析</el-button>
    </div>
  </PageContainer>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { Upload, Plus } from '@element-plus/icons-vue'

import PageContainer from '@/components/PageContainer.vue'
import { useAnalysisStore } from '@/stores'

const analysisStore = useAnalysisStore()
const uploadRef = ref()
const selectedFile = ref(null)

const submitUpload = async () => {
  await nextTick() // 确保DOM更新

  try {
    if (!uploadRef.value) {
      throw new Error('上传组件未初始化')
    }

    // 调试信息

    // 两种检查方式确保文件存在
    if (
      (!uploadRef.value.uploadFiles ||
        uploadRef.value.uploadFiles.length === 0) &&
      !selectedFile.value
    ) {
      throw new Error('请先选择文件')
    }

    // 优先使用组件实例中的文件，其次使用selectedFile
    const file = uploadRef.value.uploadFiles?.[0]?.raw || selectedFile.value
    if (!file) {
      throw new Error('无效的文件')
    }

    //   const loading = ElMessage.loading('文件上传中...', 0)
    try {
      await analysisStore.getAnalysisData(file)
      ElMessage.success('上传成功')
    } finally {
      // loading.close()
    }

    // 清空选择
    uploadRef.value?.clearFiles()
    selectedFile.value = null
  } catch (error) {
    ElMessage.error(error.message)
    console.error('上传错误:', error)
  }
}

const handleFileChange = (file, fileList) => {
  if (fileList.length > 0) {
    selectedFile.value = file.raw // 保存原始文件对象
    ElMessage.success(`已选择文件: ${file.name}`)
  } else {
    selectedFile.value = null
  }
}

const handleView = row => {
  if (row.url) {
    window.open(row.url) // 打开新窗口
  } else {
    ElMessage.warning('文件URL不存在')
  }
}
const goanalysis = () => {
  if (!analysisStore.analysisData.length) {
    ElMessage.warning('请先上传文件')
  } else {
    window.open(
      'https://b.datav.run/share/page/6fc42507ab0bfebb684b3e2f3206c319'
    )
  }
}
</script>

<style scoped lang="scss">
.upload-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.upload-demo {
  display: inline-block;
  margin-right: 10px;
}

.footer {
  margin-top: 20px;
  text-align: center;
  padding: 16px 0;
  border-top: 1px solid var(--el-border-color-light);
}
</style>
