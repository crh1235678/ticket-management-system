<template>
    <div class="list-container">
        <!-- inline="true"让表单内联，横着排列-->
        <el-form :inline="true">
            <el-form-item label="姓名">
                <el-input v-model="queryForm.name" placeholder="请输入姓名" />
            </el-form-item>
            <el-form-item v-model="queryForm.telephone" label="电话">
                <el-input v-model="queryForm.telephone" placeholder="请输入电话" />
            </el-form-item>

            <el-form-item>
                <el-button @click="onSearch()" type="success" plain>查询</el-button>

                <el-button @click="add()" type="primary" plain>新增</el-button>

                <el-button @click="confirmDel()" type="danger" plain>批量删除</el-button>
            </el-form-item>
        </el-form>

        <!-- 分割线 -->
        <el-divider />
        <!-- v-loading是显示加载状态的,data是数据源,所有行数据都来自这个数组」-->
        <!-- @selection-change="handleSelectionChange"是选中或者取消行数据触发方法并且获取选中数据（:data="datalist"）的行内容传给handleSelectionChange方法」 -->
        <el-table border v-loading="listloading" :data="datalist" style="width: 100%"
            :header-cell-style="{ background: '#f5f5f5' }" @selection-change="handleSelectionChange">
            <!-- 数据行数选择  -->
            <el-table-column type="selection" width="55" />
            <!-- prop属性绑定后端数据库数据源的属性名,我这是管理员信息管理所以绑定admin数据表-->
            <el-table-column header-align="center" align="center" prop="id" label="ID" width="80" />
            <el-table-column header-align="center" align="center" prop="headurl" label="头像" width="100">
                <template #default="scope">
                    <el-avatar :size="50" :src="getFileUrl(scope.row.headurl)"></el-avatar>
                </template>
            </el-table-column>
            <el-table-column header-align="center" align="center" prop="username" label="用户名" min-width="120" />
            <el-table-column header-align="center" align="center" prop="userpassword" label="用户密码" min-width="150" />
            <el-table-column header-align="center" align="center" prop="name" label="姓名" width="100" />
            <el-table-column header-align="center" align="center" prop="telephone" label="电话" width="140" />
            <el-table-column header-align="center" align="center" prop="sex" label="性别" width="100" />
            <el-table-column header-align="center" align="center" prop="email" label="邮箱" min-width="180" />
            <el-table-column header-align="center" align="center" prop="role" label="权限" width="100" />
            <el-table-column header-align="center" align="center" prop="account" label="余额" width="120" />
            <!--   -->
            <el-table-column header-align="center" align="center" label="操作" width="150">
                <!-- v-slot是用来接收子组件数据到scope对象，
         scope = {
             row: 当前行的数据对象,
             column: 当前列对象,
             $index: 当前行的索引
             }里进行操作 
             这里就是v-slot:default="scope"，default只是个名字 -->
                <template #default="scope">
                    <el-button type="text" plain @click="update(scope.row)">修改</el-button>
                    <el-button type="text" plain @click="confirmDel(scope.row.id)">删除</el-button>
                </template>
            </el-table-column>

        </el-table>

        <!-- 分页组件,total是总页数得根据后台数据源获取所以也得是响应式 -->
        <!-- 分页点击触发事件handleCurrentChange-->
        <el-pagination @current-change="handleCurrentChange" background layout="prev, pager, next" :total="total"
            style="float: right; margin:10px 20px 20px 0px" ; :page-size="queryForm.pagesize" />

        <addOrUpdate 
        ref="operateRef" 
        title="管理员管理" 
        :fields="userFields" 
        :rules="userRules" 
        :submitApi="submitApi" 
        @refresh="getList">
        </addOrUpdate>
    </div>

</template>


<script lang="ts" setup>

import { ref, reactive, h } from 'vue'
import { userApi } from '@/api/user-api'
import { onMounted } from 'vue'
import Constants from '@/utils/constants'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFileUrl } from '@/utils/utils'
import addOrUpdate from '@/components/AddorUpdate.vue'
import type { Field } from '@/types/form'


// 定义表单字段
const userFields: Field[] = [
  {
    label: '用户名',
    prop: 'username',
    type: 'input',
    readonly: true
  },
  {
    label: '用户密码',
    prop: 'userpassword',
    type: 'password'
  },
  {
    label: '姓名',
    prop: 'name',
    type: 'input'
  },
  {
    label: '性别',
    prop: 'sex',
    type: 'input' 
  },
  {
    label: '电话',
    prop: 'telephone',
    type: 'input'
  },
  {
    label: '邮箱',
    prop: 'email',
    type: 'input'
  },
  {
    label: '角色',
    prop: 'role',
    type: 'select',
    options: [
      { label: '管理员', value: '管理员' },
      { label: '用户', value: '用户' }
    ]
  },
  {
    label: '描述',
    prop: 'introduce',
    type: 'input' 
  },
  {
    label: '账户余额',
    prop: 'account',
    type: 'input' 
  },
  {
    label: '头像',
    prop: 'headurl',
    type: 'upload'
  }
]

// 定义表单验证规则
const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],

  userpassword: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],

  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],

  sex: [
    { required: true, message: '请输入性别', trigger: 'blur' }
  ],

  telephone: [
    { required: true, message: '请输入电话', trigger: 'blur' },
    {
      pattern: /^1\d{10}$/,
      message: '手机号格式不正确',
      trigger: 'blur'
    }
  ],

  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    {
      type: 'email',
      message: '邮箱格式不正确',
      trigger: 'blur'
    }
  ],

  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],

  introduce: [
    { required: true, message: '请输入描述', trigger: 'blur' }
  ],

  account: [
    { required: true, message: '请输入账户余额', trigger: 'blur' }
  ],

  headurl: [
    { required: true, message: '请上传头像', trigger: 'change' }
  ]
}

//表单的初始值
const queryFormState = {
    name: '',
    telephone: '',
    pagenum: Constants.PAGE_NUM,
    pagesize: Constants.PAGE_SIZE,
};

//定义响应式变量,列表数据
const datalist = ref([]);

//定义响应式变量,加载状态
const listloading = ref(false);

//定义响应式变量,数据总条数
const total = ref(0);

//定义响应式对象,表单查询条件对象
//reactive()：用于创建对象或数组的响应式数据
//{...queryFormState} - 展开运算符，复制对象属性并将复制的对象转换为响应式
//避免直接引用初始值，初始值数据被修改
const queryForm = reactive({ ...queryFormState });

//定义引用新增或修改组件的响应式变量
const operateRef = ref();

//定义多个选中的行数据
const multipleSelection = ref([]);

//定义提交Api，这里定义的Api是添加或修改的Api
const submitApi = (form: any) => {
  if (form.id) {
    return userApi.update(form)
  } else {
    return userApi.add(form)
  }
}



//async使函数为异步函数，让函数支持awit。让函数一定返回一个Promise对象
//这里为什么要用异步函数，因为adminApi.queryPageList({}, 1, 10)是一个网络请求，他返回的是Promise对象不是立刻有结果的数据
//你要等后端数据回来才能执行接下来，所以得用异步
async function getList() {
    try {
        //读取数据时开始加载
        listloading.value = true;
        //responseModel获得的是响应体数据，也就是json数据
        //awit会终止当前函数执行，直到 Promise 被 resolved 或 rejected。
        //promise.reject()会抛出一个错误，
        //catch会捕获到这个错误，跳出try进入catch
        console.log(queryForm);
        let responseModel = await userApi.queryPageList(queryForm, queryForm.pagenum, queryForm.pagesize);
        //MP分页插件返回的数据是在响应式的records数组里，total是总条数
        datalist.value = responseModel.data.records;
        total.value = responseModel.data.total;

    }
    catch (error) {

    }
    finally {
        //读取数据完成后,关闭加载状态
        listloading.value = false;
    }
}

// 查询方法
function onSearch() {
    //查询时重置页码
    queryForm.pagenum = 1;
    //重新获取数据
    getList();
}

// 分页查询
function handleCurrentChange(val: number) {
    queryForm.pagenum = val;
    getList();
}

// 新增管理员
function add() {
    //获得addOrUpdate的子组件实例，并调用showModel方法
    //这里注意一定要operateRef.value表示引用的子组件实例
    operateRef.value.show();

}

// 修改管理员信息
function update(row) {
    operateRef.value.show(row);
}

function confirmDel(id) {
    ElMessageBox.confirm(
        '是否确认删除？',
        '确认删除',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(() => {
            del(id);
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '用户取消删除',
            })
        })
}

// 删除管理员
async function del(id) {
    try {
        listloading.value = true;
        //根据是否传入id来确定是单删除还是批量删除
        //multipleSelection.value.map(item => item.id);map遍历原数组把数组里的对象放到item里，然后提取每个item的id构成新的数组
        let ids = id ? [id] : multipleSelection.value.map(item => item.id);
        if (!ids.length) {
            ElMessage.warning('请选择要删除的行');
            return;
        }
        await userApi.delete(ids);
        ElMessage.success('删除成功');
        //删除成功后刷新列表
        getList();
    } catch (error) {

    } finally {
        listloading.value = false;
    }
}

function handleSelectionChange(val) {
    multipleSelection.value = val;
}


//onMounted 钩子可以用来在组件完成初始渲染并创建 DOM 节点后运行代码
//在组件挂载完成后调用getList函数
onMounted(getList)




</script>

<style scoped>
.button-example {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.button-row {
    display: flex;
    flex-wrap: wrap;
    gap: 1rem;
    align-items: center;
}

.button-row>* {
    margin: 0;
}
</style>
