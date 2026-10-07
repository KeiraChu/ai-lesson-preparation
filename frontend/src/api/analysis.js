import request from '@/utils/request'
import { useUserStore } from '@/stores'
export function getAnalysis(formData){
    
    const res=request.post('/file/upload',
        formData
    ,{
        headers: {
            'Content-Type': 'multipart/form-data'
          },
   
    
    })
   
    
    return res
}

export const getAnalysislist=(data)=>{
    const userStore=useUserStore()
    const res=request.get('/file',{
        params:{
            id:userStore.user.id,
            pageNum:data.pageNum,
            pageSize:data.pageSize,
        }
    })
    return res;
}

export const delAnalysis=(id)=>{
    const res=request.delete('/file',{
        params:{
            id
        }
    })
    return res
}

