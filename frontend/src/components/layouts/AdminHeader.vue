<template>
    <div class="header">
        <div class="header-menu">
            <div class="header-logo">
                <div class="header-title">
                    vaeee后台管理系统
                </div>
            </div>

            <div class="header-rinfo">
                当前用户:
                <span>{{ admin.username }}</span>
                <span class="header-exit">
                    <a @click="logOut()">退出</a>
                </span>
            </div>
            
        </div>
    </div>
</template>

<script lang="ts" setup> 

import { adminApi } from '@/api/admin-api'
import { useAdminStore } from '@/stores/admin'
import { storeToRefs } from 'pinia'

const adminStore = useAdminStore();
// admin 是响应式引用他的改变会让页面视图改变,所以需要storeToRefs
const { admin } = storeToRefs(adminStore)

async function logOut() {
    try {
        await adminApi.logout();
        window.location.href = "/admin/login";
    } catch (error) {
        console.log(error);
    }finally {
        // 登出成功后，清空本地存储,也就是清空登录信息状态
        adminStore.logout();
    }
}


</script>