import request from '@/utils/request'

export const uploadKnowledgeDocument = (file, knowledgeBaseId = 'default') => {
  const form = new FormData()
  form.append('file', file)
  form.append('knowledgeBaseId', knowledgeBaseId)
  return request.post('/knowledge/documents', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 120000
  })
}

export const generateStructuredLessonPlan = payload => request.post('/ai/lesson-plan', payload, { timeout: 120000 })

