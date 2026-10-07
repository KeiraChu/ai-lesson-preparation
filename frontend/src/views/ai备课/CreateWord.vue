<template>
  <PageContainer title="教案制作">
    <template #extra>
      <el-button type="primary" @click="goppt">创建PPT</el-button>
      <el-button type="primary" @click="onSubmit">保存</el-button>
      <el-button type="success" @click="download">导出 Word</el-button>
    </template>

    <div class="editor">
      <quill-editor
        v-model:content="content"
        :options="editorOptions"
        theme="snow"
        content-type="html"
      />
    </div>
    <channelEdit ref="dialog" @submit="onSave" />
  </PageContainer>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { QuillEditor } from '@vueup/vue-quill'
import { Document, Paragraph, TextRun, Packer } from 'docx'
import { saveAs } from 'file-saver'
import { useDocStore } from '@/stores'
import channelEdit from './channelEdit.vue'
import PageContainer from '@/components/PageContainer.vue'
import { saveWord } from '@/api/word.js'
import hljs from '@/utils/highlight'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import 'highlight.js/styles/atom-one-dark.css'
import { nextTick } from 'vue'

const router = useRouter()
const route = useRoute()
// 使用 ref 来定义响应式数据
const docStore = useDocStore()
// console.log(docStore.currentDoc);
// console.log(docStore.editingMessage.content);

const content = ref('')
if (route.query.id) {
  content.value = docStore.currentDoc.content
} else if (docStore.editingMessage) {
  content.value = docStore.editingMessage.content
} else {
  content.value = ''
}


defineOptions({
  name: 'create-plan'
})
watch(
  content,
  () => {
    if (route.query.id) {
      docStore.currentDoc.content = content.value
    } else if (docStore.editingMessage) {
      docStore.editingMessage.content = content.value
    }
  },
  { deep: true, immediate: true }
)
watch(
  docStore.editingMessage,
  async newVal => {
    await nextTick()
    content.value = newVal.content
  },
  { deep: true, immediate: true }
)

// 文件上传逻辑
const dialog = ref(null)
const onSubmit = () => {
  dialog.value.open()
}
const onSave = async data => {
  const res = await saveWord({ title: data.title, text: content.value })
  ElMessage.success('保存成功')
}
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
const goppt = () => {
  router.push('/aiPPT')
}
</script>

<style lang="scss" scoped>
.editor {
  width: 100%;
  background-color: #fff;
  border-radius: 8px;

  :deep(.ql-editor) {
    min-height: 300px;
    font-size: 16px;
    line-height: 1.6;
    color: #333;
    // 添加列表样式

    ol,
    ul {
      padding-left: 1.5em !important;
      margin: 16px 0 !important;

      li {
        display: list-item !important;
        margin: 8px 0 !important;
        &::before {
          display: none !important;
        }
      }
    }

    // 修复Quill 2.x的列表样式
    .ql-list.ql-bullet,
    .ql-list.ql-ordered {
      padding-left: 1.5em !important;
    }
  }
}
</style>
