<script setup>

</script>

<template>
  <div class="light">
    <slot></slot>
  </div>
</template>

<style scoped>
  .light{
    background-color: var(--el-bg-color);
    border-radius: 8px;
    box-sizing: border-box;
    min-height: 20px;
    padding: 10px;
  }
</style>