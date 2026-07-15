<template>
  <!-- dialog对话框 -->
  <el-dialog v-model="visible" :title="title" width="40%">
    <!-- :model="form"。这是el-form特定的，告诉 el-form：“后面你管理的表单数据都在这个对象里”el-form 会用它做 初始化、校验、重置、提交。每个 el-form 组件都需要 :model= -->
    <!-- :rules="rules"。这是el-form特定的，告诉 el-form：“后面你管理的校验规则都在这个对象里”el-form 会用它做 校验。-->
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <template v-for="item in fields" :key="item.prop">
        <!-- 遍历判断传入的prop类型选择使用不同的功能实现 -->
        <el-form-item :label="item.label" :prop="item.prop">

          <!-- 输入框类型 -->
          <el-input v-if="item.type === 'input'" v-model="form[item.prop]" :readonly="item.readonly && isEdit"
            style="width:50%" />

          <!-- 密码类型 -->
          <el-input v-else-if="item.type === 'password'" type="password" v-model="form[item.prop]" style="width:50%" />

          <!-- 下拉类型 -->
          <el-select v-else-if="item.type === 'select'" v-model="form[item.prop]" style="width:50%">
            <el-option v-for="opt in item.options" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>

          <!-- 上传类型 -->
          <FileUpload v-else-if="item.type === 'upload'" :defaultFileList="getDefaultFileList(item.prop)"
            @change="(fileList) => uploadChange(item.prop, fileList)" />
          <!-- 这一段是为了泛化，能接收不同名字的图像名称 -->
          <!-- 优化思路
          fields 里每个上传字段都有自己的 prop（比如：headurl、logourl）
          上传组件只需要知道自己绑定的是哪个字段，然后把上传结果写到 form[prop]
          show 和 uploadChange 都动态根据 item.prop 来处理，而不是硬编码 headurl 或 logourl 
          <FileUpload v-else-if="item.type === 'upload'" :defaultFileList="defaultFileList[item.prop] || []"
            @change="(fileList) => uploadChange(item.prop, fileList)" /> -->

          <!-- 时间选择器类型 -->
          <!-- 因为我后端的时间类型不是简单的输入，是LocalDateTime，所以要使用时间选择器 -->
          <el-date-picker v-else-if="item.type === 'datetime'" v-model="form[item.prop]" type="datetime"
            placeholder="请选择日期时间" style="width:50%" />

          <!-- 多行文本 -->
          <el-input v-else-if="item.type === 'textarea'" type="textarea" v-model="form[item.prop]" :rows="10"
            resize="none" placeholder="请输入文章内容（支持换行）" style="width:100%" />


        </el-form-item>

      </template>

    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submit" :loading="loading">
        确定
      </el-button>
    </template>

  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import FileUpload from '@/components/FileUpload.vue'
import { urls2FileUrl } from '@/utils/utils'
// 引入表单字段类型
import type { Field } from '@/types/form'

// props
const props = defineProps<{
  title: string
  // 这是自定义的表单字段类型
  fields: Field[]
  rules: any
  // 说明接收一个 form 参数，并且返回必须是 Promise 对象.
  submitApi: (form: any) => Promise<any>
}>()
//defineEmits 用于声明子组件可能会触发的事件
const emit = defineEmits(['refresh'])

const visible = ref(false)
const loading = ref(false)
const formRef = ref()
//声明创建的表单对象key是字符串，value是any类型
const form = reactive<Record<string, any>>({})
//const defaultFileList = ref([])
const defaultFileList = reactive<{ [key: string]: any[] }>({
  headurl: [],
  logourl: [],
  coverImg: [],
})
const isEdit = ref(false)

//让外面（父组件）能够调用这个方法，
defineExpose({ show })
// 获取默认文件列表
function getDefaultFileList(prop: string) {
  return defaultFileList[prop] || []
}
function show(row: any) {
  Object.keys(form).forEach(k => delete form[k])

  if (row) {
    Object.assign(form, row)
    isEdit.value = true

    if (form.headurl) defaultFileList.headurl = urls2FileUrl(form.headurl)
    if (form.logourl) defaultFileList.logourl = urls2FileUrl(form.logourl)
    if (form.coverImg) defaultFileList.coverImg = urls2FileUrl(form.coverImg)
  } else {
    isEdit.value = false
    defaultFileList.headurl = []
    defaultFileList.logourl = []
    defaultFileList.coverImg = []
  }

  visible.value = true
}

function uploadChange(prop: string, fileList: any[]) {
  form[prop] = fileList.map((f: any) => f.name).toString()
  defaultFileList[prop] = fileList
}

function submit() {
  formRef.value.validate().then(async () => {
    loading.value = true
    try {
      // ⭐ 统一调用父组件传进来的函数,并且返回promise给await
      // console.log(form)
      await props.submitApi(form)
      ElMessage.success('操作成功')
      visible.value = false
      emit('refresh')

    } finally {
      loading.value = false
    }
  })
}
</script>