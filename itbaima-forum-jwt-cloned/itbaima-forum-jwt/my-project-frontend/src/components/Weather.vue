<script setup>
 defineProps({
  data:Object,
})
</script>

<template>
  <div v-loading="!data.success" element-loading-text="正在加载天气信息..." style="height: 230px;">
    <div v-if="data.success"  style="display: flex; justify-content: space-between; margin: auto 20px;">
      <div style="font-size: 45px; color: gray; margin: auto;">
        <i :class="`qi-${data.now.icon}-fill`"></i>
        <div style="font-size: 15px;font-weight: bold">{{data.now.text}}</div>
      </div>
      <div style="font-size: 20px;font-weight: bold;margin: auto; ">
        <div>{{ data.now.temp  }}℃</div>
      </div>
      <div style="font-size: 15px;font-weight: bold;margin: auto;">
        <div>{{ data.location.country }} {{ data.location.adm1 }}</div>
        <div style="color: gray;font-size: 12px;">{{ data.location.adm2 }}市 {{data.location.name}}区</div>
      </div>
    </div>
    <el-divider style="margin: 10px 0;"></el-divider>
    <div v-if="data.success" style="display: grid; grid-template-columns: repeat(5, 1fr); text-align: center;">
        <div v-for="item in data.hourly">
          <div style="font-size: 15px;">{{new Date(item.fxTime).getHours()}}时</div>
          <div style="font-size: 30px;color: grey;"> <i :class="`qi-${item.icon}-fill`"></i></div>
          <div style="font-size: 12px;color: grey;font-weight: bold;">{{data.now.text}}</div>
          <div style="font-size: 15px;">{{item.temp}}℃</div>
        </div>
    </div>
  </div>

</template>

<style scoped>

</style>