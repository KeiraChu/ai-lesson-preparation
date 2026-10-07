<template>
  <div class="layout-container">
    <el-row>
      <!-- 左侧编辑区 -->
      <el-col :span="14">
        <div class="left-column">
          <!-- 顶部操作栏 -->
          <div class="toolbar">
            <div class="toolbar-left">
              <el-button
                type="primary"
                plain
                @click="goback"
                class="back-button"
              >
                <el-icon><ArrowLeft /></el-icon>
                <span>返回</span>
              </el-button>
              <span class="title">
                {{ title }}
              </span>
            </div>

            <div class="toolbar-right">
              <el-button type="primary" @click="open">打开本地 Word</el-button>
              <el-button type="primary" @click="onSubmit">保存</el-button>
              <el-button type="success" @click="download">导出 Word</el-button>
            </div>
          </div>

          <div class="editor">
            <quill-editor
              v-model:content="content"
              :options="editorOptions"
              theme="snow"
              content-type="html"
            />
          </div>
        </div>
      </el-col>

      <!-- 右侧智能生成区 -->
      <el-col :span="10">
        <div class="right-column">
          <el-container>
            <el-header>
              <div>智能推荐练习题</div>
            </el-header>
            <el-main>
              <!-- 加载状态 -->
              <div v-if="loading" class="loading-container">
                <div class="spinner"></div>
                <span>正在加载推荐内容...</span>
              </div>

              <!-- 推荐内容 -->
              <div v-else class="chat-content">
                <img class="avatar" src="../../assets/助手.png" alt="" />
                <div class="message-content">
                  <el-icon @click.stop="copymsg">
                    <DocumentCopy />
                  </el-icon>

                  <div
                    v-if="isMD(messages)"
                    class="markdown-content"
                    v-html="renderMarkdown(messages)"
                  ></div>
                  <div v-else class="plain-content">
                    {{ messages }}
                  </div>
                </div>
              </div>
            </el-main>
          </el-container>
        </div>
      </el-col>
    </el-row>

    <!-- 保存教案对话框 -->
    <el-dialog
      v-model="saveDialogVisible"
      title="保存教案"
      width="500"
      align-center
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <!-- <el-form-item label="教案ID" prop="id">
            <el-input v-model="form.id"></el-input>
          </el-form-item> -->
        <el-form-item label="教案名称" prop="title">
          <el-input v-model="form.title"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="saveDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">确认</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { defineAsyncComponent } from 'vue'
import { QuillEditor } from '@vueup/vue-quill'
import { Document, Paragraph, TextRun, Packer } from 'docx'
import { saveAs } from 'file-saver'
import hljs from '@/utils/highlight'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import 'highlight.js/styles/atom-one-dark.css'
import * as mammoth from 'mammoth' // DOCX解析库
import DOMPurify from 'dompurify'
import { marked } from 'marked'

import { DocumentCopy } from '@element-plus/icons-vue'
import { getData } from '@/api/practice'
import { useDocStore } from '@/stores'
//   import { gettext } from '@/api/chat.js'
//   import { saveLesson } from '@/api/lesson.js'
const router = useRouter()
const docStore = useDocStore()
let title = ref('')

title.value = docStore.currentDoc.title
const goback = () => {
  window.history.back()
}

const route = useRoute()

// 左侧编辑区数据
let content = ref('')

// 配置编辑器选项
const editorOptions = {
  modules: {
    syntax: {
      highlight: text => {
        return hljs.highlightAuto(text).value
      }
    },
    toolbar: [
      ['bold', 'italic', 'underline', 'strike'],
      [{ color: [] }, { background: [] }],
      [{ script: 'sub' }, { script: 'super' }],
      [{ header: 1 }, { header: 2 }, { header: 6 }],
      [{ list: 'ordered' }, { list: 'bullet' }],
      ['link', 'image', 'video'],
      ['code-block'],
      [{ align: [] }],
      [{ indent: '-1' }, { indent: '+1' }],
      ['clean']
    ]
  }
}

const open = () => {
  // 创建隐藏的文件输入元素
  const input = document.createElement('input')
  input.type = 'file'
  input.accept =
    '.doc,.docx,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document'

  input.onchange = async e => {
    const file = e.target.files[0]
    if (!file) return

    try {
      const filecontent = await readFileContent(file)
      content.value = filecontent // 更新富文本内容
      ElMessage.success('文档导入成功')
    } catch (error) {
      console.error('文件读取失败:', error)
      ElMessage.error('文档导入失败: ' + error.message)
    }
  }

  input.click() // 触发文件选择
}

// 读取文件内容
const readFileContent = file => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()

    reader.onload = async e => {
      try {
        let result = ''

        // 根据文件类型选择解析方式
        if (file.name.endsWith('.docx')) {
          // 解析docx
          const arrayBuffer = e.target.result
          const { value } = await mammoth.extractRawText({ arrayBuffer })
          result = value
        } else if (file.name.endsWith('.doc')) {
          // 旧版doc需要转换处理
          result = '旧版.doc文件建议转换为.docx格式'
        } else {
          // 纯文本文件直接读取
          result = e.target.result
        }

        resolve(result)
      } catch (error) {
        reject(error)
      }
    }

    reader.onerror = () => {
      reject(new Error('文件读取错误'))
    }

    // 开始读取
    if (file.name.endsWith('.docx')) {
      reader.readAsArrayBuffer(file)
    } else {
      reader.readAsText(file)
    }
  })
}

const getPlainText = html => {
  // 处理换行符：将</p>转换为换行
  const withNewLines = html
    .replace(/<p>/gi, '')
    .replace(/<\/p>/gi, '\n')
    .replace(/<br>/gi, '\n')

  const tempDiv = document.createElement('div')
  tempDiv.innerHTML = withNewLines
  return tempDiv.textContent || ''
}
// 导出逻辑（使用 docx）
const download = async () => {
  if (!content.value.trim()) {
    ElMessage.warning('内容为空，无法导出')
    return
  }

  try {
    // 提取纯文本并保留换行符
    const plainText = getPlainText(content.value)

    // 创建纯文本Word文档
    const doc = new Document({
      sections: [
        {
          children: [
            new Paragraph({
              children: [new TextRun(plainText)],
              style: 'NormalText' // 自定义纯文本样式
            })
          ]
        }
      ],
      styles: {
        paragraphStyles: [
          {
            id: 'NormalText',
            name: 'NormalText',
            basedOn: 'Normal',
            next: 'NormalText',
            quickFormat: true,
            run: {
              font: '宋体',
              size: 24, // 12磅字号
              color: '000000'
            }
          }
        ]
      }
    })

    // 生成并下载
    const blob = await Packer.toBlob(doc)
    saveAs(blob, `纯文本教案_${new Date().toISOString().slice(0, 10)}.docx`)
  } catch (error) {
    ElMessage.error(`导出失败: ${error.message}`)
  }
}

const form = ref({
  id: route.query.id || '',
  title: ''
})

const loading = ref(true)
const messages = ref('')

const getmsg = async () => {
  const res = await getData(form.value.id)
  // console.log('API响应:', res)
  messages.value = res
  loading.value = false // 加载完成
}

onMounted(() => {
  getmsg()
})

const isMD = text => {
  if (!text) return false
  const mdPatterns = [
    /^#{1,6}\s/, // 标题
    /(\*\*|__)(.*?)\1/, // 加粗/斜体
    /^[-*+]\s/, // 列表
    /^>\s/, // 引用
    /!\[.*?\]\(.*?\)|\[.*?\]\(.*?\)/, // 图片/链接
    /```[\s\S]*?```/g, // 代码块
    /`[^`]+`/ // 行内代码
  ]
  return mdPatterns.some(pattern => pattern.test(text))
}

const renderMarkdown = text => {
  if (!text) return ''

  // 使用DOMPurify过滤XSS
  const clean = DOMPurify.sanitize(text)
  // 使用marked转换
  return marked.parse(clean)
}

const copymsg = async () => {
  try {
    let content = messages.value
    if (isMD(content)) {
      // 使用 DOMPurify 过滤 XSS 内容
      const sanitized = DOMPurify.sanitize(content)
      // 生成带样式的 HTML
      const html = `
        <!DOCTYPE html>
        <html>
          <head>
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/github-markdown-css/5.1.0/github-markdown.min.css">
          </head>
          <body class="markdown-body">${marked.parse(sanitized)}</body>
        </html>
      `
      // 写入富文本格式
      await navigator.clipboard.write([
        new ClipboardItem({
          'text/html': new Blob([html], { type: 'text/html' }),
          'text/plain': new Blob([sanitized], { type: 'text/plain' })
        })
      ])
    } else {
      // 纯文本复制
      await navigator.clipboard.writeText(content)
    }
    ElMessage.success('复制成功')
  } catch (err) {
    console.error('复制失败:', err)
    ElMessage.error(`复制失败: ${err.message}`)
  }
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
  padding: 0;
  background-color: #f0f5ff;
}

.el-row {
  height: 100%;
  margin: 0;
}

.left-column {
  height: 100%;
  padding: 15px;
  background-color: white;
  border-radius: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;

  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
    padding: 10px 0;
    border-bottom: 1px solid #f0f0f0;
    flex-shrink: 0;
  }

  .editor {
    height: calc(100vh - 120px); /* 根据实际需要调整这个值 */
    border-radius: 8px;
    border: 1px solid #e6e6e6;
    overflow: hidden;
    flex-grow: 1;
    min-height: 0;

    /* 确保Quill编辑器内部也填满空间 */
    :deep(.ql-container) {
      height: calc(100% - 42px); /* 减去工具栏高度 */
    }
  }
}
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;

  .toolbar-left {
    display: flex;
    align-items: center;
    gap: 16px;

    .back-button {
      display: flex;
      align-items: center;
      padding: 8px 12px;

      .el-icon {
        margin-right: 4px;
      }
    }

    .title {
      font-size: 18px;
      font-weight: 600;
      color: #333;
      max-width: 300px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }

  .toolbar-right {
    display: flex;
    gap: 10px;

    .el-button {
      margin-left: 0;
    }
  }
}

.right-column {
  height: 100%;
  padding: 15px;
  background-color: rgb(225, 238, 249);
  border-radius: 20px;

  :deep(el-header) {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 50px;
    font-size: 20px;
  }
}

.avatar {
  width: 60px; /* 头像大小 */
  height: 60px; /* 头像大小 */
  border-radius: 50%; /* 圆形头像 */
}
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #888;
}

.chat-content {
  //   display: flex;
  margin-bottom: 15px;
  align-items: flex-start;
}

.message-content {
  margin-left: 15px;
  padding: 10px 15px;
  background: #f5f7fb;
  border-radius: 8px;
  position: relative;
}

/* 添加这些样式确保内容可见 */
.message-content {
  min-width: 100px;
  padding: 10px;
  background: #fff;
  color: #333;
  border-radius: 4px;
  margin-left: 10px;
  white-space: pre-wrap; /* 保留换行和空格 */
}

/* 添加Markdown渲染样式 */
.markdown-content {
  h1,
  h2,
  h3,
  h4,
  h5,
  h6 {
    margin: 1em 0 0.5em;
    font-weight: bold;
  }

  h1 {
    font-size: 1.8em;
    border-bottom: 1px solid #eee;
  }
  h2 {
    font-size: 1.5em;
  }
  h3 {
    font-size: 1.3em;
  }

  p {
    margin: 0.8em 0;
    line-height: 1.6;
  }

  ul,
  ol {
    padding-left: 2em;
    margin: 0.8em 0;
  }

  blockquote {
    border-left: 4px solid #ddd;
    padding-left: 1em;
    color: #666;
    margin: 1em 0;
  }

  pre {
    background: #f5f7fa;
    padding: 1em;
    border-radius: 4px;
    overflow-x: auto;
  }

  code {
    background: #f5f7fa;
    padding: 0.2em 0.4em;
    border-radius: 3px;
    font-family: monospace;
  }

  a {
    color: #409eff;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

.plain-content {
  white-space: pre-wrap;
  line-height: 1.6;
}
.spinner {
  width: 30px;
  height: 30px;
  border: 3px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: #409eff;
  animation: spin 1s linear infinite;
  margin-right: 10px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
