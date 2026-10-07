import { defineStore } from 'pinia'
import { reactive, ref } from 'vue'
import { getppt, getppturl } from '@/api/ppt'
export const usePPTStore = defineStore(
  'ai-ppt',
  () => {
    let pptquery = reactive({
      outline: {
        title: '',
        subtitle: '',
        chapters: []
      },
      templateId: '',
      language: '',
      query: ''
    })
    let pptList = ref({})
    let pptstatus = reactive({
      aiImageStatus: '',
      pptUrl: '',
      pptStatus: '',
      cardNoteStatus: ''
    })
    const setQuery = data => {
      pptquery = data
    }
    const getQuery = () => {

      return pptquery
    }
    const getPPTList = async () => {
      const res = await getppt(pptquery)
      pptList.value = res
      return pptList.value
    }
    const pptprogress = async () => {
      const res = await getppturl(pptList.value.sid)
      return res
    }

    return {
      pptquery,
      pptstatus,
      setQuery,
      getQuery,
      pptList,
      getPPTList,
      pptprogress
    }
  },
  {
    persist: true
  }
)
