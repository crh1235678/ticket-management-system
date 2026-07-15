import { defineStore } from 'pinia'
import { ref, reactive, computed } from 'vue'
import Constants from '@/utils/constants'
import { getLocalStorage, saveLocalStorage, clearLocalStorage } from '@/utils/utils'


//  `defineStore()` 的返回值的命名是自由的
// 但最好含有 store 的名字，且以 `use` 开头，以 `Store` 结尾。
// (比如 `useUserStore`，`useCartStore`，`useProductStore`)
// 第一个参数是你的应用中 Store 的唯一 ID。
export const useAdminStore = defineStore('admin', () => {

  const token = ref(getLocalStorage(Constants.TOKEN) || "")
  const admin = reactive(JSON.parse(getLocalStorage(Constants.ADMIN_INFO) || '{}'))

  const getToken = computed(() => {
    if (token.value) {
        return token.value
    }
    return getLocalStorage(Constants.TOKEN)

  })

  function setAdminInfo(data: any) {
    admin.id = data.id
    admin.username = data.username
    admin.name = data.name
    admin.telephone = data.telephone
    token.value = data.token

    saveLocalStorage(Constants.TOKEN, data.token)
    saveLocalStorage(Constants.ADMIN_INFO, JSON.stringify(data))
  }
  function logout() {
    admin.id = ''
    admin.username = ''
    admin.name = ''
    admin.telephone = ''
    token.value = ''

    clearLocalStorage()
  }

  //是返回定义这个 Store 的“公共 API”，决定了外部世界能对它做什么。还存在token获取问题后续调整
  return { token, setAdminInfo, logout, admin, getToken }

})

