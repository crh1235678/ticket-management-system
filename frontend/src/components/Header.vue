<template>
    <div class="front-header">
        <div class="left">
            <img src="@/assets/images/login/logo.png" alt="">
            <div class="title">
                伴行
            </div>
            <!-- 菜单栏 -->
            <div class="navigation">
                <el-menu ellipsis mode="horizontal" popper-offset="10" style="width: 100%;background-color: #E8F5F2;">
                    <el-menu-item index="1">首页</el-menu-item>
                    <el-sub-menu index="2">
                        <template #title>Workspace</template>
                        <el-menu-item index="2-1">item one</el-menu-item>
                        <el-menu-item index="2-2">item two</el-menu-item>
                        <el-menu-item index="2-3">item three</el-menu-item>
                        <el-sub-menu index="2-4">
                            <template #title>item four</template>
                            <el-menu-item index="2-4-1">item one</el-menu-item>
                            <el-menu-item index="2-4-2">item two</el-menu-item>
                            <el-menu-item index="2-4-3">item three</el-menu-item>
                        </el-sub-menu>
                    </el-sub-menu>
                    <el-sub-menu index="3">
                        <template #title>Override Popper Offset</template>
                        <el-menu-item index="3-1">item one</el-menu-item>
                        <el-menu-item index="3-2">item two</el-menu-item>
                        <el-menu-item index="3-3">item three</el-menu-item>
                        <el-sub-menu index="3-4">
                            <template #title>override child</template>
                            <el-menu-item index="3-4-1">item one</el-menu-item>
                            <el-menu-item index="3-4-2">item two</el-menu-item>
                            <el-menu-item index="3-4-3">item three</el-menu-item>
                        </el-sub-menu>
                    </el-sub-menu>
                    <el-menu-item index="4">个人中心</el-menu-item>
                    <el-menu-item index="5" disabled>Info</el-menu-item>
                </el-menu>
            </div>
        </div>
        <div class="center">
            <div class="search-box">
                <el-input placeholder="搜索车票 / 机票 / 城市" clearable size="large" class="search-input">
                    <template #append>
                        <el-button type="primary">
                            搜索
                        </el-button>
                    </template>
                </el-input>
            </div>
        </div>
        <div class="right">
            <el-button type="primary" size="medium">登录</el-button>
            <el-button type="primary" size="medium">注册</el-button>
        </div>


    </div>

</template>

<script setup lang="ts">

</script>

<style lang="less" scoped>
.front-header {
    height: 155px;
    width: 100%;
    display: flex;
    align-items: center;
    background: #E8F5F2;

    // 左侧
    .left {
        display: flex;
        align-items: center;
        width: 665px;
        margin-right: 30px;

        .title {
            color: #355476;
            cursor: pointer;
            margin-left: 10px;
            font-size: 24px;
            font-weight: bold;

        }

        .navigation {
            flex: 1; // 占据剩余空间
            display: flex;
            justify-content: flex-start; // 左对齐
            margin-left: 20px; // 与左侧区域保持间距
        }

        img {
            height: 35px;
            width: 35px;
            border-radius: 50%;
        }
    }

    .center {
        width: 273.8px;
        display: flex;
        justify-content: center;
        margin-right: 10px;
        margin-left: 10px;

        /* 搜索框容器 */
        .search-box {
            width: 100%;
        }

        /* 搜索输入框 */
        .search-input {
            width: 100%;
        }
    }

    .right {
        display: flex;
        align-items: center;
        width: 466px;
        margin-left: 10px;
    }


}
</style>
