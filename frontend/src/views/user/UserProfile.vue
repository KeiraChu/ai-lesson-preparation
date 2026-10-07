<template>
  <PageContainer title="用户信息">
    <el-row>
        <el-col :span="12">
            <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">

                <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="form.realName" ></el-input>
                </el-form-item>
                <el-form-item label="用户昵称" prop="nickName">
                <el-input v-model="form.nickName"></el-input>
                </el-form-item>

                <el-form-item label="用户邮箱" prop="email">
                <el-input v-model="form.email"></el-input>
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                    <el-input v-model="form.phone"></el-input>
                </el-form-item>
                <el-form-item label="性别" prop="sex">
                    <el-radio-group v-model="form.sex">
                    <el-radio value="1" size="large">男</el-radio>
                    <el-radio value="2" size="large">女</el-radio>
                    </el-radio-group>
                </el-form-item>

                <el-form-item>
                <el-button type="primary" @click="onSubmit">提交修改</el-button>
                <el-button @click="resetForm">重置</el-button>
                </el-form-item>
            </el-form>
        </el-col>
    </el-row>
  </PageContainer>
    
</template>

<script setup>
import {defineOptions} from 'vue';
import PageContainer from '@/components/PageContainer.vue';
import {ref} from 'vue'
import { useUserStore } from '@/stores';
import {changemsg} from '@/api/login'
defineOptions({
    name:'user-profile' 
})
const {user:{email,id,nickName,phone,realName,sex}}=useUserStore()
const form=ref({email,nickName,phone,realName,sex})
const rules={
    nickName:[{required:true,message:'请输入用户昵称',trigger:'blur'},{
        min:2,max:10,message:'长度在2到10个字符',trigger:'blur'
    }],
    email:[{required:true,message:'请输入用户邮箱',trigger:'blur'},{
        type:'email',message:'请输入正确的邮箱',trigger:'blur' 
    }],
    phone:[{required:true,message:'请输入用户手机号',trigger:'blur'},{
        pattern:/^1[3-9]\d{9}$/,message:'请输入正确的手机号',trigger:'blur' 
    }],
    realName:[{required:true,message:'请输入真实姓名',trigger:'blur'},{
        min:2,max:10,message:'长度在2到10个字符',trigger:'blur' 
    }],
    sex:[{required:true,message:'请选择性别',trigger:'blur'}],

}
const formRef=ref(null)
const onSubmit=async()=>{
    
    await formRef.value.validate();
    
    const res=await changemsg(form.value)
    

}
const resetForm=()=>{
    formRef.value.resetFields() 
}
</script>

<style>

</style>