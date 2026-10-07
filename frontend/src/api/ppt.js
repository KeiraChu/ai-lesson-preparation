import request from '@/utils/request'
import axios from 'axios'
import { useUserStore } from '@/stores'

export const getmain=(data)=>{
    const res=request.post('/ppt/outline',{

        "query":data.pptTheme,
        "language":data.language||"cn",
    })
    return res
}

export const gettheme=({style,color,industry,pageNum,pageSize})=>{
    // console.log(data );
    
    
    
    const res=request.post('/ppt/template',{
       
       
        style:style,
        color:color,
        industry:industry,
        pageNum:1,
        pageSize:10
       
    })
    return res
}
export const getppt=(data)=>{
    
    const userStore=useUserStore()
    const res=request.post('/ppt',{
        "outline":data.outline,
        "templateId":data.templateId,
        "language":data.language||"cn",
        "query":data.query,
        "id":userStore.sessionId
    })
    return res
}
export const getppturl=(data)=>{

   const res=request.get('/ppt/progress',{
    params:{
            sid:data
    }
   }) 
   return res
}
