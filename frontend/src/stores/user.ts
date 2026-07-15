import { defineStore } from 'pinia'
import { ref, reactive, computed } from 'vue'
import Constants from '@/utils/constants'
import { getLocalStorage, saveLocalStorage, clearLocalStorage } from '@/utils/utils'


//  `defineStore()` 的返回值的命名是自由的
// 但最好含有 store 的名字，且以 `use` 开头，以 `Store` 结尾。
// (比如 `useUserStore`，`useCartStore`，`useProductStore`)
// 第一个参数是你的应用中 Store 的唯一 ID。
export const useUserStore = defineStore('user', () => {

  // 根据 Constants.USER_TOKEN 从 localStorage 中获取 token，若不存在则设为空字符串
  // 把token和user做持久化处理
  const token = ref(getLocalStorage(Constants.TOKEN) || "")
  const user = reactive(JSON.parse(getLocalStorage(Constants.USER_INFO) || '{}'))

  const getToken = computed(() => {
    if (token.value) {
        return token.value
    }
    return getLocalStorage(Constants.TOKEN)

  })

  function setUserInfo(data: any) {
    user.id = data.id
    user.username = data.username
    user.name = data.name
    user.telephone = data.telephone
    token.value = data.token
    user.headurl = data.headurl

    saveLocalStorage(Constants.TOKEN, data.token)
    saveLocalStorage(Constants.USER_INFO, JSON.stringify(data))
  }

function updateUserInfo(data: Partial<typeof user>) {
    // 只覆盖有值的字段
    if (data.name !== undefined && data.name !== null) {
        user.name = data.name
    }
    if (data.telephone !== undefined && data.telephone !== null) {
        user.telephone = data.telephone
    }
    if (data.headurl !== undefined && data.headurl !== null) {
        user.headurl = data.headurl
    }
    if (data.email !== undefined && data.email !== null) {
        user.email = data.email
    }
    if (data.sex !== undefined && data.sex !== null) {
        user.sex = data.sex
    }
    if (data.introduce !== undefined && data.introduce !== null) {
        user.introduce = data.introduce
    }

    // token 不覆盖，保持原来的值
    saveLocalStorage(Constants.USER_INFO, JSON.stringify({ ...user, token: token.value }))
}

  function logout() {
    user.id = ''
    user.username = ''
    user.name = ''
    user.telephone = ''
    token.value = ''
    user.headurl = ''

    clearLocalStorage()
  }

  //是返回定义这个 Store 的“公共 API”，决定了外部世界能对它做什么。
  return { token, setUserInfo, logout, user, getToken, updateUserInfo }

})

