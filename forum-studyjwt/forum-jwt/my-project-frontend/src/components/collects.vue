<script setup>
defineProps({
  show:Boolean
})
const emit=defineEmits(['close'])
</script>

<template>
<div>
  <el-drawer :model-value="show" direction="rtl" @close="emit('close')">
      <slot></slot>
  </el-drawer>
</div>
</template>

<style scoped>

</style>