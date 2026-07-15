<template>
    <div class="version">
        <!-- 超链接还未加入修改，后更改链接 -->
        <a target="_blank" class="copyright" href="https://www.vaeee.com">@2026-{{ currentYear }} vaeee后台管理员平台</a> |
        <a target="_blank" class="version" href="https://www.vaeee.com">vaeee公众号</a> |
    </div>
</template>

<script setup lang="ts">
//引入dayjs时间库
import dayjs from 'dayjs'
//用dayjs中的year方法定义当前年份给currentYear
const currentYear = dayjs().year();
</script>

<!-- scoped 表示css样式只对当前组件有效 -->
<style lang="less" scoped>
.version,
.copyright {
    font-size: 14px;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.45);

    a:hover {
        color: #0a0e14;
    }
}
</style>
