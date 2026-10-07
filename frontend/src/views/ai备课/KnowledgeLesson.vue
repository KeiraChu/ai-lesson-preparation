<template>
  <PageContainer title="知识库备课">
    <div class="workspace">
      <el-card shadow="never">
        <template #header><strong>1. 添加可信教学资料</strong></template>
        <el-upload accept=".pdf,.docx,.pptx,.xlsx,.txt,.md" :auto-upload="false" :limit="1" :on-change="onFileChange" :show-file-list="true">
          <el-button>选择 PDF、Word、PPT 或 Markdown</el-button>
        </el-upload>
        <el-button type="primary" :loading="uploading" :disabled="!selectedFile" @click="upload" class="action">
          解析并加入知识库
        </el-button>
        <el-alert v-if="ingestResult" type="success" :closable="false" :title="`已解析 ${ingestResult.chunk_count} 个知识片段`" />
      </el-card>

      <el-card shadow="never">
        <template #header><strong>2. 配置教案</strong></template>
        <el-form :model="form" label-width="90px">
          <el-row :gutter="16">
            <el-col :span="8"><el-form-item label="学科"><el-input v-model="form.subject" /></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="年级"><el-input v-model="form.grade" /></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="课时"><el-input-number v-model="form.duration_minutes" :min="10" :max="240" /></el-form-item></el-col>
          </el-row>
          <el-form-item label="课题"><el-input v-model="form.topic" /></el-form-item>
          <el-form-item label="教学目标"><el-input v-model="goals" type="textarea" placeholder="每行一个目标" /></el-form-item>
          <el-form-item label="额外约束"><el-input v-model="constraints" type="textarea" placeholder="例如：包含课堂讨论和分层练习" /></el-form-item>
          <el-button type="primary" :loading="generating" @click="generate">生成可审核教案</el-button>
        </el-form>
      </el-card>

      <el-card v-if="result" shadow="never">
        <template #header>
          <div class="result-title">
            <strong>3. 人工审核</strong>
            <div class="review-actions">
              <el-tag type="warning">{{ result.status }}</el-tag>
              <el-button size="small" @click="editing = !editing">{{ editing ? '完成编辑' : '编辑教案' }}</el-button>
              <el-button size="small" @click="downloadWord">导出 Word</el-button>
              <el-button size="small" @click="printPdf">打印 / PDF</el-button>
            </div>
          </div>
        </template>
        <el-alert v-for="warning in result.lesson_plan?.quality_warnings" :key="warning" type="warning" :title="warning" show-icon />
        <el-input v-if="editing" v-model="result.lesson_plan.title" class="title-editor" />
        <h2 v-else>{{ result.lesson_plan?.title }}</h2>
        <template v-if="editing">
          <strong>教学目标</strong>
          <el-input v-model="objectivesText" type="textarea" :rows="3" @change="syncObjectives" />
        </template>
        <p v-else><strong>教学目标：</strong>{{ result.lesson_plan?.objectives?.join('；') }}</p>
        <section v-for="section in result.lesson_plan?.sections" :key="section.title">
          <template v-if="editing">
            <el-input v-model="section.title" class="section-title-editor" />
            <el-input v-model="section.content" type="textarea" :rows="4" />
          </template>
          <template v-else><h3>{{ section.title }}</h3><p>{{ section.content }}</p></template>
          <div class="section-citations">
            <el-button v-for="citationId in section.citations" :key="citationId" link type="primary" @click="scrollCitation(citationId)">
              查看依据 {{ citationId }}
            </el-button>
          </div>
        </section>
        <el-divider content-position="left">引用资料</el-divider>
        <el-descriptions v-for="citation in result.lesson_plan?.citations" :id="`citation-${safeId(citation.chunk_id)}`" :key="citation.chunk_id" :column="1" border class="citation">
          <el-descriptions-item :label="citation.document_name">{{ citation.excerpt }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
      <el-alert v-else-if="generationError" type="error" :closable="false" :title="generationError" show-icon />
    </div>
  </PageContainer>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageContainer from '@/components/PageContainer.vue'
import { generateStructuredLessonPlan, uploadKnowledgeDocument } from '@/api/knowledge'

const selectedFile = ref(null)
const uploading = ref(false)
const generating = ref(false)
const ingestResult = ref(null)
const result = ref(null)
const generationError = ref('')
const goals = ref('理解核心概念\n能够应用所学知识解决问题')
const constraints = ref('内容必须基于上传资料并标注引用')
const editing = ref(false)
const objectivesText = ref('')
const form = reactive({ subject: '', grade: '', topic: '', duration_minutes: 45 })
const onFileChange = uploadFile => { selectedFile.value = uploadFile.raw }
const upload = async () => {
  uploading.value = true
  try { ingestResult.value = await uploadKnowledgeDocument(selectedFile.value); ElMessage.success('资料已加入知识库') }
  finally { uploading.value = false }
}
const generate = async () => {
  if (!form.subject || !form.grade || !form.topic) return ElMessage.warning('请填写学科、年级和课题')
  generating.value = true
  generationError.value = ''
  try {
    result.value = await generateStructuredLessonPlan({
      ...form,
      teaching_goals: goals.value.split('\n').filter(Boolean),
      constraints: constraints.value.split('\n').filter(Boolean),
      knowledge_base_ids: ['default']
    })
    if (result.value?.status === 'FAILED') {
      generationError.value = result.value.error || '教案生成失败，请检查知识库资料和模型配置'
      result.value = null
    } else {
      objectivesText.value = result.value?.lesson_plan?.objectives?.join('\n') || ''
    }
  } catch (error) {
    generationError.value = error?.message || '教案生成失败'
  } finally { generating.value = false }
}
const syncObjectives = () => {
  if (result.value?.lesson_plan) result.value.lesson_plan.objectives = objectivesText.value.split('\n').map(item => item.trim()).filter(Boolean)
}
const safeId = value => String(value).replace(/[^a-zA-Z0-9_-]/g, '-')
const scrollCitation = citationId => document.getElementById(`citation-${safeId(citationId)}`)?.scrollIntoView({ behavior: 'smooth', block: 'center' })
const escapeHtml = value => String(value || '').replace(/[&<>"']/g, char => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[char]))
const lessonHtml = () => {
  syncObjectives()
  const plan = result.value?.lesson_plan
  if (!plan) return ''
  const sections = plan.sections.map(section => `<h2>${escapeHtml(section.title)}</h2><p>${escapeHtml(section.content)}</p>`).join('')
  const citations = plan.citations.map(citation => `<li><strong>${escapeHtml(citation.document_name)}</strong>：${escapeHtml(citation.excerpt)}</li>`).join('')
  return `<html><meta charset="utf-8"><body><h1>${escapeHtml(plan.title)}</h1><h2>教学目标</h2><ul>${plan.objectives.map(item => `<li>${escapeHtml(item)}</li>`).join('')}</ul>${sections}<h2>引用资料</h2><ol>${citations}</ol></body></html>`
}
const downloadWord = () => {
  const blob = new Blob([lessonHtml()], { type: 'application/msword;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `${result.value?.lesson_plan?.title || '教案'}.doc`
  link.click()
  URL.revokeObjectURL(url)
}
const printPdf = () => {
  const popup = window.open('', '_blank')
  if (!popup) return ElMessage.warning('请允许浏览器打开打印窗口')
  popup.document.write(lessonHtml())
  popup.document.close()
  popup.print()
}
</script>

<style scoped>
.workspace { display: grid; gap: 16px; padding: 20px; max-width: 1100px; margin: auto; }
.action { margin: 12px 0; }
.result-title { display: flex; justify-content: space-between; align-items: center; }
.review-actions { display: flex; gap: 8px; align-items: center; }
.title-editor, .section-title-editor { margin-bottom: 10px; }
.section-citations { display: flex; flex-wrap: wrap; gap: 8px; }
.citation { margin-bottom: 10px; scroll-margin-top: 20px; }
section { padding: 8px 0; }
@media print { .review-actions { display: none; } }
</style>
