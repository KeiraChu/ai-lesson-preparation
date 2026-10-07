import { defineStore } from 'pinia'
import { getWordList, getWordListById } from '@/api/word.js'
import { ref } from 'vue'
import { delDoc } from '@/api/word.js'
export const useDocStore = defineStore('ai-doc', () => {
  let total = ref(0)
  let docList = ref([])
  let currentDoc = ref({})
  let editingMessage = ref({})
  const getDocList = async data => {

    const res = await getWordList(data)

    total.value = res.total
    docList.value = []
    docList.value = res.records

    return docList.value
  }
  const editDoclist = data => {}
  const delDoclist = async id => {
    const res = await delDoc(id)
  }
  const getDocListById = async id => {
    const res = await getWordListById(id)
    docList.value = [res]
    currentDoc.value = null
    currentDoc.value = res
    return docList.value
  }

  return {
    docList,
    getDocList,
    total,
    currentDoc,
    getDocListById,
    editDoclist,
    delDoclist,
    editingMessage
  }
})
