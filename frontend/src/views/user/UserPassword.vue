<template>
  <PageContainer title="修改密码">
    <el-row>
      <el-col :span="12">
        <el-form
          :model="PwdForm"
          :rules="rules"
          ref="formRef"
          label-width="100px"
          size="large"
        >
          <el-form-item label="原密码" prop="old_pwd">
            <el-input type="password" v-model="PwdForm.oldPassword"></el-input>
          </el-form-item>
          <el-form-item label="新密码" prop="new_pwd">
            <el-input type="password" v-model="PwdForm.newPassword"></el-input>
          </el-form-item>
          <el-form-item label="确认新密码" prop="re_pwd">
            <el-input type="password" v-model="PwdForm.rePassword"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onSubmit">提交修改</el-button>
            <el-button @click="reset">重置</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
  </PageContainer>
</template>

<script setup>
import { defineOptions } from 'vue'
import PageContainer from '@/components/PageContainer.vue'
import { ref } from 'vue'
import { changepassword } from '@/api/login.js'
import { useUserStore } from '@/stores'

const userStore = useUserStore()
defineOptions({
  name: 'user-password'
})
const PwdForm = ref({
  oldPassword: '',
  newPassword: '',
  rePassword: ''
})
const rules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    {
      pattern: /^\S{6,15}$/,
      message: '密码长度在6到15个字符之间',
      trigger: 'blur'
    }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    {
      pattern: /^\S{6,15}$/,
      message: '密码长度在6到15个字符之间',
      trigger: 'blur'
    }
  ],
  rePassword: [
    { required: true, message: '请输入确认密码', trigger: 'blur' },
    {
      pattern: /^\S{6,15}$/,
      message: '密码长度在6到15个字符之间',
      trigger: 'blur'
    },
    {
      validator: (rule, value, callback) => {
        if (value !== PwdForm.value.newPassword) {
          return callback(new Error('两次密码不一致'))
        } else {
          return callback()
        }
      }
    }
  ]
}
const formRef = ref(null)
const onSubmit = async () => {
  await formRef.value.validate()
  const res = await changepassword({
    oldPassword: PwdForm.value.oldPassword,
    newPassword: PwdForm.value.newPassword
  })
  if (res != null) {
    ElMessage.success('修改成功')
  }
}
const reset = () => {
  formRef.value.resetFields()
}
</script>

<style></style>
