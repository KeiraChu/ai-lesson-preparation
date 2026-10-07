import request from '@/utils/request'
import { useUserStore } from '@/stores'
export function getData(id){
    const userStore=useUserStore()
    
    
    const res=request.post('/practice',{
        lessonId:id,
        userId:userStore.user.id
    })
    return res
}