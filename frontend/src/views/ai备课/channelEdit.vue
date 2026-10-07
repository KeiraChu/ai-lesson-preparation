<template>
 <el-dialog
 v-model="centerDialogVisible"
    title="保存教案"
    width="500"
    align-center
 >
    <el-form ref="formref" :model="form" :rules="rules" label-width="100px" style="padding-right: 30px">
       
        <el-form-item label="教案名称" prop="title">
            <el-input v-model="form.title"></el-input>
        </el-form-item>
    </el-form>
    <template #footer>
        <div class="dialog-footer">
            <el-button @click="centerDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="onSubmit">
            确认
            </el-button>
        </div>
    </template>
 </el-dialog>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useDocStore } from '@/stores'
import { useRoute } from 'vue-router'
const route=useRoute()
const centerDialogVisible = ref(false)
const docStore=useDocStore()
const open=()=>{
    form.value.id=route.query.id||''
    centerDialogVisible.value=true 
}
defineExpose({
    open
})

const form = ref({
    // id: route.query.id||'',
    title: docStore.currentDoc.title||'',
})

const formref=ref(null)
const rules={
    // id:[{required:true,message:'请输入教案id',trigger:'blur'}],
    title:[{required:true,message:'请输入教案名称',trigger:'blur'}]
}
const onSubmit= async ()=>{
   
    await formref.value.validate()
    emit('submit',form.value)
    centerDialogVisible.value=false
}
const emit=defineEmits(['submit'])
</script>


<style>

</style>
