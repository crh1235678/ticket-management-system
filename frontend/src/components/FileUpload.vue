<template>
  <div class="avatar-upload" :style="{
    '--upload-width': props.width + 'px',
    '--upload-height': props.height + 'px'
  }">
    <!-- 图片显示 -->
    <!--这里用 :class 正是因为它是一个“开关式切换”,class 维护更轻松可以在css里.round进行样式更改,用style会样式混在模板里-->
    <div v-if="imageUrl" class="avatar-wrapper" :class="{ 'round': props.round }">
      <!-- @error是img组件的事件，当图片加载失败时触发onImgError方法 -->
      <!-- :src动态绑定图片地址 -->
      <img :src="imageUrl" class="avatar" :style="{ objectFit: props.fit }" @error="onImgError" />

      <!-- hover 遮罩 -->
      <div class="mask">
        <!--@click.stop 阻止事件冒泡，避免点击遮罩层也触发上传按钮 -->
        <el-icon @click.stop="preview">
          <ZoomIn />
        </el-icon>
        <el-icon @click.stop="remove">
          <Delete />
        </el-icon>
      </div>
    </div>

    <!-- 上传按钮 -->
    <!-- :before-upload="beforeUpload" 上传前触发方法beforeUpload校验
         :on-change="handleChange" 文件选择后触发方法handleChange
         :accept="props.accept" 允许上传的文件类型
         :multiple="props.multiple"  是否支持多文件上传
    -->
    <el-upload v-else :show-file-list="false" :auto-upload="false" :before-upload="beforeUpload"
      :on-change="handleChange" :accept="props.accept" :multiple="props.multiple">
      <div class="upload-placeholder">
        <el-icon>
          <Plus />
        </el-icon>
      </div>
    </el-upload>

    <!-- 图片预览,Element Plus 内置图片预览组件 -->
    <el-image-viewer v-if="previewVisible && imageUrl" :url-list="[imageUrl]" @close="previewVisible = false" />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onUnmounted } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import { Plus, ZoomIn, Delete } from '@element-plus/icons-vue'
import { commonApi } from '@/api/common-api'
import CONSTANTS from '@/utils/constants'

// defineProps 的核心作用就是：让子组件能够接收父组件传入的数据，并在子组件内部使用这些数据实现交互。
const props = defineProps({
  // 是否支持多文件上传
  multiple: { type: Boolean, default: false },
  maxUploadSize: { type: Number, default: 1 }, // MB
  // 允许上传的文件类型
  accept: { type: String, default: 'image/jpeg,image/png,image/gif' },
  defaultFileList: { type: Array, default: () => [] },
  // 列表类型
  listtype: { type: String, default: 'picture-card' },
  // 图片宽度 
  width: { type: Number, default: 178 },
  // 图片高度
  height: { type: Number, default: 178 },
  // 是否圆角
  round: { type: Boolean, default: false },
  // 图片填充方式,{ objectFit: string } 在某些 ts-plugin 版本里 无法自动推断成 CSSProperties，于是就报错
  // 所以这里用 as () => 'contain' | 'cover' | 'fill' | 'none' | 'scale-down' 来指定类型
  fit: {
    type: String as () => 'contain' | 'cover' | 'fill' | 'none' | 'scale-down',
    default: 'contain'
  }
})

const emit = defineEmits(['change'])

const imageUrl = ref('')
const previewVisible = ref(false)
const fileList = ref([])
// 缓存允许类型数组
const allowedTypes = ref(props.accept.split(','))

/* ===========================
   编辑回显
=========================== */
//watch(监听规则, 变化后执行的逻辑)
watch(
  //监听defaultFileList变化,因为props.defaultFileList是响应式所以发生改变就会调用watch
  () => props.defaultFileList,
  //newVal为变化后的值
  (newVal) => {
    if (Array.isArray(newVal) && newVal.length > 0) {
      const firstItem = newVal[0]
      if (firstItem && typeof firstItem.url === 'string') {
        //图片回显
        imageUrl.value = firstItem.url
        fileList.value = [...newVal]
      } else {
        console.warn('Invalid defaultFileList item:', firstItem)
      }
    } else {
      imageUrl.value = ''
      fileList.value = []
    }
  },
  //组件一加载就执行一次
  { immediate: true }
)

// 组件卸载时清理 Blob URL
onUnmounted(() => {
  if (imageUrl.value.startsWith('blob:')) {
    URL.revokeObjectURL(imageUrl.value)
  }
})

/* ===========================
   封装通用方法
=========================== */
const showLoading = () => ElLoading.service({ lock: true, text: '上传中，请稍候...', background: 'rgba(0,0,0,0.7)' })
const hideLoading = (loadingInstance: ReturnType<typeof ElLoading.service>) => loadingInstance.close()
const showError = (message: string) => ElMessage.error(message)

/* ===========================
   上传前校验
=========================== */
const beforeUpload = (file: File) => {
  const isAllowedType = allowedTypes.value.includes(file.type)
  const isLtMax = file.size / 1024 / 1024 < props.maxUploadSize

  if (!isAllowedType) {
    showError(`文件类型必须是: ${props.accept}`)
    return false
  }
  if (!isLtMax) {
    showError(`图片不能超过 ${props.maxUploadSize}MB`)
    return false
  }
  return true
}

/* ===========================
   选择文件
=========================== */
const handleChange = async (file: any) => {
  // raw 是上传的文件的原生对象，而file只是一个json数据
  const rawFile = file.raw
  // createObjectURL生成一个临时 URL也就是blob url，让浏览器可以直接在页面上显示这张图片
  imageUrl.value = URL.createObjectURL(rawFile)
  //这涉及到网络请求所以需要await等待handleUpload执行完成后再结束handleChange
  await handleUpload(rawFile)
}

/* ===========================
   上传到后端
=========================== */
const handleUpload = async (file: File) => {
  const loading = showLoading()

  try {
    //创建formData对象配合后端网页请求MultipartFile
    const formData = new FormData()
    //"file"对应后端@RequestParam("file")
    formData.append('file', file)

    const res = await commonApi.uploadFile(formData)
    const uploadedFile = res.data

    fileList.value = [uploadedFile]
    //触发change事件,将上传后的文件列表传递给父组件
    emit('change', fileList.value)
  } catch (e: any) {

  } finally {
    hideLoading(loading)
  }
}

/* ===========================
   预览
=========================== */
const preview = () => {
  previewVisible.value = true
}

/* ===========================
   删除
=========================== */
const remove = () => {
  if (imageUrl.value.startsWith('blob:')) {
    URL.revokeObjectURL(imageUrl.value)
  }
  imageUrl.value = ''
  fileList.value = []
  emit('change', fileList.value)
}

/* ===========================
   图片加载失败
=========================== */
const onImgError = (e: Event) => {
  (e.target as HTMLImageElement).src = CONSTANTS.DEFAULT_AVATAR
}
</script>

<style scoped>
.avatar-upload {
  width: var(--upload-width, 178px);
  height: var(--upload-height, 178px);
}

.avatar-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
}

.round {
  border-radius: 50%;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: #f5f5f5;
}

.mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  opacity: 0;
  /* 默认隐藏 */
  transition: 0.2s;
  /* 淡入淡出动画 */
}

/* :hover鼠标悬停操作显示 */
.avatar-wrapper:hover .mask {
  opacity: 1;
}

.mask .el-icon {
  font-size: 22px;
  color: #fff;
  cursor: pointer;
}

.upload-placeholder {
  width: var(--upload-width, 178px);
  height: var(--upload-height, 178px);
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}
</style>