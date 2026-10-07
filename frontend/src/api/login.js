import request from '@/utils/request'
import { useUserStore } from '@/stores/user'
import axios from 'axios'
export function userregister ({ username, password, code, sessionId }) {
  const res = request.post('/user/register', {
    username,
    password,
    code,
    sessionId
  })
  return res
}
export function userlogin ({ username, password, code, sessionId }) {
  const res = request.post('/user/login', {
    username,
    password,
    code,
    sessionId
  })

  return res
}
export function usergetcode () {
  const res = request.get('/user/captcha')
  return res
}

export function changepassword (data) {
  const userStore = useUserStore()

  const res = request.put('/user/updatepwd', {
    id: userStore.user.id,
    oldPassword: data.oldPassword,
    newPassword: data.newPassword
  })
  return res
}

// export function changepassword(data){
//     const userStore=useUserStore()
//     console.log(data);
//     console.log(userStore.user.id);
//     const res=axios.put('/api/user/updatepwd',{
//         id:'1909186124295966720',
//         oldPassword:'example-old-password',
//         newPassword:'example-new-password'
//     })
//     return res

// }

export function changemsg (data) {
  const userStore = useUserStore()
  const res = request.put('/user/update', {
    id: userStore.user.id,
    phone: data.phone,
    realName: data.realName,
    nickName: data.nickName,
    email: data.email,
    sex: parseInt(data.sex)
  })
  return res
}
