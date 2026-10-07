import { defineStore } from 'pinia'
import { ref } from 'vue'
export const useUserStore=defineStore(
    'ai-user',
    ()=>{
        const token=ref('')
        const sessionId=ref('')
        const user=ref({})
        const setUser=(data)=>{
            user.value=data
        }
        const getUser=()=>{
            return user.value 
        }
        const clearUser=()=>{
            user.value={} 
        }
        const getSessionId=()=>{
            return sessionId.value 
        }
        const setSessionId=(data)=>{
            sessionId.value=data 
        }
        const getToken=()=>{
            return token.value 
        }
        const setToken=(data)=>{
            token.value=data 
        }
        return {
            token,
            user,
            setUser,
            getUser,
            clearUser,
            sessionId,
            setSessionId,
            getSessionId,
            setToken,
            getToken
        }

    },{
        persist:true
    }
)
