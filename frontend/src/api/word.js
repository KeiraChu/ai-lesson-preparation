import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

export function saveWord(data){
    const userStore=useUserStore()
    const res=request.post('/text/lesson',{
        id:userStore.sessionId,
        title:data.title,
        text:data.text
    },{
        headers:{
            'Content-Type':'application/json',
            
        }
    })
  
    
    return res
}

export function getWordList(data){
    const userStore=useUserStore()
    
    
    const res=request.post('/text/lessonlist',{
       
          
            id:userStore.sessionId,
            pageNum:data.pageNum,
            pageSize:data.pageSize,
            
            
          
          
        
    })
    
    
    return res
}

export function getWordListById(id){
    const res=request.get('/text/getById',{
        params:{
            id:id,
        }
    })
   
    
    return res
}
export function delDoc(id){
  
    const res=request.delete('/text/lesson',{
        "id":id,
    })
    return res
}