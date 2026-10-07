import { defineStore } from 'pinia'
import { getAnalysis,getAnalysislist,delAnalysis } from '@/api/analysis.js'
import { ref } from 'vue'
import { useUserStore } from './user'


export const useAnalysisStore = defineStore('analysis', () => {
  const analysisData = ref([])
  const userStore = useUserStore()
  let total=0;
  let  analysisList=ref([])
  const getAnalysisData = async (file) => {
   
      // 1. 创建新的 FormData 实例
      const formData = new FormData()
      
      // 2. 确保文件存在
      if (!file) {
        throw new Error('文件对象不存在')
      }
      
      // 3. 正确添加字段到 FormData
      formData.append('id', userStore.user.id)
      formData.append('file', file) // 添加文件名作为第三个参数
      
      // 4. 发送请求
      const response = await getAnalysis(formData)
      let data=ref({})
      data.value = response;
      analysisData.value.push(data.value)
        
        return response.data
  }
  const getAnalysisList=async(data)=>{
    
    
    const res=await getAnalysislist(data)
    
    total=res.total;
    analysisList.value=res.records;
  }
  const delanalysis=async(id)=>{
        await delAnalysis(id)
        getAnalysisList()
        
    }

  return {
    analysisData,
    getAnalysisData,
    getAnalysisList,
    analysisList,
    total,
    delanalysis
  }
})
