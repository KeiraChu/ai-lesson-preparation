<template>
  <PageContainer title="我的文档">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-form inline>
        <el-form-item>
          <el-input v-model="searchForm.id" placeholder="请输入Id"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handlesearch(searchForm)"
            >搜索</el-button
          >
          <el-button @click="handlereset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-tab-pane label="教案" name="first">
        <el-table :data="docStore.docList" style="width: 100%">
          <el-table-column
            label="教案Id"
            width="200"
            prop="id"
          ></el-table-column>
          <el-table-column label="教案名称" prop="title"></el-table-column>
          <el-table-column label="创建时间" prop="createTime"></el-table-column>
          <el-table-column label="更新时间" prop="updateTime"></el-table-column>
          <el-table-column label="生成习题">
            <template #default="{ row, $index }">
              <el-button type="primary" size="mini" @click="gotest(row, $index)"
                >生成</el-button
              >
            </template>
          </el-table-column>

          <el-table-column label="操作" width="150px">
            <template #default="{ row, $index }">
              <el-button
                :icon="Edit"
                circle
                plain
                @click="handleedit(row, $index)"
                type="primary"
              ></el-button>
              <el-button
                :icon="Delete"
                circle
                plain
                @click="handledelt(row, $index)"
                type="danger"
              ></el-button>
              <el-button
                :icon="Download"
                circle
                plain
                @click="updown(row, $index)"
                type="success"
              ></el-button>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty description="没有数据"></el-empty>
          </template>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="练习" name="two">
        <el-table style="width: 100%">
          <el-table-column label="练习Id"></el-table-column>
          <el-table-column label="创建时间"></el-table-column>
          <el-table-column label="更新时间"></el-table-column>
          <el-table-column label="操作" width="150px">
            <template #default="{ row, $index }">
              <el-button
                :icon="Edit"
                circle
                plain
                @click="edittest(row, $index)"
                type="primary"
              ></el-button>
              <el-button
                :icon="Delete"
                circle
                plain
                @click="deltest(row, $index)"
                type="danger"
              ></el-button>
              <el-button
                :icon="Download"
                circle
                plain
                @click="updown(row, $index)"
                tyoe="sucess"
              ></el-button>
            </template>
          </el-table-column>

          <template #empty>
            <el-empty description="没有数据"></el-empty>
          </template>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="学情分析" name="three">
        <el-table :data="analysisStore.analysisList" style="width: 100%">
          <el-table-column
            label="学情Id"
            width="200"
            prop="id"
          ></el-table-column>
          <el-table-column label="学情名称" prop="title"></el-table-column>
          <el-table-column label="创建时间" prop="createTime"></el-table-column>
          <el-table-column label="操作" width="150px">
            <template #default="{ row, $index }">
              <el-button
                :icon="Delete"
                circle
                plain
                @click="del(row, $index)"
                type="danger"
              ></el-button>
              <el-button
                :icon="Download"
                circle
                plain
                @click="updown2(row, $index)"
                type="success"
              ></el-button>
            </template>
          </el-table-column>

          <template #empty>
            <el-empty description="没有数据"></el-empty>
          </template>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-pagination
      background
      layout="prev, pager, next"
      :current-page="pageNum"
      :page-size="pageSize"
      :total="docStore.total"
      @current-change="handlePageChange"
    />
  </PageContainer>
</template>

<script setup>
import { computed, defineOptions } from 'vue'
import PageContainer from '@/components/PageContainer.vue'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Delete, Edit, Download } from '@element-plus/icons-vue'
import { useDocStore } from '@/stores'
import { useAnalysisStore } from '@/stores'
import { Document, Paragraph, TextRun, Packer } from 'docx'
import { saveAs } from 'file-saver'
defineOptions({
  name: 'user-mydoc'
})
const docStore = useDocStore()
const analysisStore = useAnalysisStore()
const router = useRouter()
onMounted(() => {
  getList()
})
const searchForm = ref({ id: '' })
const pageNum = ref(1)
const pageSize = ref(10)
const getList = async () => {
  await docStore.getDocList({
    pageNum: pageNum.value,
    pageSize: pageSize.value
  })
}

const handlePageChange = val => {
  pageNum.value = val
  getList()
}

const handleedit = (row, index) => {
  router.push({ path: '/create/plan', query: { id: row.id } })
}
const handledelt = async (row, index) => {
  await ElMessageBox.confirm('确定删除吗？', '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })
  await docStore.delDoclist(row.id)
  ElMessage.success('删除成功')
  await docStore.getDocList({
    pageNum: pageNum.value,
    pageSize: pageSize.value
  })
}
const updown = row => {

  if (!row.content) {
    ElMessage.warning('文档内容为空')
    return
  }

  try {
    // 创建文档结构
    const doc = new Document({
      sections: [
        {
          children: [
            new Paragraph({
              children: [
                new TextRun({
                  text: row.title,
                  bold: true,
                  size: 28 // 对应14号字
                })
              ]
            }),
            new Paragraph({
              children: [
                new TextRun({
                  text: '\n' + row.content.replace(/<[^>]+>/g, ''), // 去除HTML标签
                  size: 24 // 对应12号字
                })
              ]
            })
          ]
        }
      ]
    })

    // 生成并下载文件
    Packer.toBlob(doc).then(blob => {
      const safeFileName = row.title.replace(/[\\/:*?"<>|]/g, '_') // 过滤非法字符
      saveAs(blob, `${safeFileName}.docx`)
    })
  } catch (error) {
    ElMessage.error(`导出失败: ${error.message}`)
  }
}

const updown2 = row => {
  window.open(row.url)
}
const del = async (row, index) => {
  await ElMessageBox.confirm('确定删除吗？', '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })
  await analysisStore.delanalysis(row.id)
  await analysisStore.getAnalysisList({
    pageNum: pageNum.value,
    pageSize: pageSize.value
  })
}

const activeName = ref('first')
const handleClick = (tab, event) => {
  if (tab.paneName === 'three') {
    // 学情分析对应的name值
    getAlist() // 触发数据加载
  }
  if (tab.paneName === 'first') {
    getList()
  }
}
const getAlist = async () => {
  await analysisStore.getAnalysisList({
    pageNum: pageNum.value,
    pageSize: pageSize.value
  })
}
const handlesearch = form => {
  docStore.getDocListById(form.id)
}
const handlereset = () => {
  searchForm.value = { id: '' }
  getList()
}

const gotest = (row, $index) => {
  router.push({
    path: '/create/test',
    query: {
      id: row.id
    }
  })
}
</script>

<style></style>
