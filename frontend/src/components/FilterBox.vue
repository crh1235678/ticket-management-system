<!-- 筛选框组件 -->
<template>
  <el-card class="filter-card">
    <el-form inline label-width="70px" class="filter-form">

      <el-form-item
        v-for="item in configs"
        :key="item.prop"
        :label="item.label"
        class="filter-item"
      >

        <!-- 输入框 -->
        <el-input
          v-if="item.type === 'input'"
          v-model="model[item.prop]"
          :placeholder="item.placeholder || '请输入'"
          clearable
        />

        <!-- 日期 -->
        <el-date-picker
          v-else-if="item.type === 'date'"
          v-model="model[item.prop]"
          type="date"
          placeholder="请选择日期"
          value-format="YYYY-MM-DD"
        />

        <!-- 下拉 -->
        <el-select
          v-else-if="item.type === 'select'"
          v-model="model[item.prop]"
          placeholder="请选择"
          clearable
        >
          <el-option
            v-for="op in item.options"
            :key="op.value"
            :label="op.label"
            :value="op.value"
          />
        </el-select>

      </el-form-item>

      <!-- 按钮 -->
      <el-form-item class="filter-btns">
        <el-button type="primary" @click="$emit('search')">查询</el-button>
        <el-button @click="$emit('reset')">重置</el-button>
      </el-form-item>

    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import type { FilterConfig } from '@/types/filter'
defineProps<{
  model: Record<string, any>
  configs: FilterConfig[]
}>()
/* defineProps({
  model: {
    type: Object,
    required: true
  },
  configs: {
    type: Array,
    required: true
  }
}) */

defineEmits(['search', 'reset'])
</script>

<style scoped lang="less">
.filter-card {
  margin-bottom: 15px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 16px;

  .filter-item {
    width: 220px;

    :deep(.el-input),
    :deep(.el-select),
    :deep(.el-date-editor) {
      width: 100%;
    }
  }

  .filter-btns {
    display: flex;
    align-items: center;
    margin-left: 10px;
  }
}
</style>