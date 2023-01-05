<template>
  <div class="image">
    <el-container>
      <el-aside width="630px">
        <el-image class="result_image" :src="require(`@/assets/Aspect.jpg`)" />
      </el-aside>
      <el-main class="layers">
        <div>
          <el-container direction="horizontal" class="tuceng">图层</el-container>
          <el-divider class="divider" />
          <div class="buttonall">
            <el-switch v-model="value1" size="large" active-text="打开IDW降雨量插值图层" class="layer_button"/>
            <el-switch v-model="value2" size="large" active-text="打开克里金降雨量插值图层" class="layer_button" />
            <el-switch v-model="value3" size="large" active-text="打开高程填洼图层层" class="layer_button" />
            <el-switch v-model="value4" size="large" active-text="打开坡度图层" class="layer_button"/>
            <el-switch v-model="value5" size="large" active-text="打开坡向图层" class="layer_button"/>
            <el-switch v-model="value6" size="large" active-text="打开流向图层" class="layer_button"/>
            <el-switch v-model="value7" size="large" active-text="打开累积流图层" class="layer_button"/>
            <el-switch v-model="value8" size="large" active-text="打开河网图层" class="layer_button"/>
            <el-switch v-model="value9" size="large" active-text="打开山脊线图层" class="layer_button"/>
          </div>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
const value1 = ref(true)
const value2 = ref(true)
const value3 = ref(true)
const value4 = ref(true)
const value5 = ref(true)
const value6 = ref(true)
const value7 = ref(true)
const value8 = ref(true)
const value9 = ref(true)
</script>
<style scoped >
.image {
  width: auto;
  height: auto;
}

.result_image {
  margin-top: 30px;
  margin-left: 30px;
  margin-right: 30px;
  height: 600px;
}

.tuceng {
  margin-top: -10px;
  margin-left: 135px;
  margin-bottom: -10px;
  text-align: center;
  font-size: 24px;
  font-weight: bold;
}

.layers {
  border-left: 2px solid black;
}

.divider {
  margin-left: 20px
}
.layer_button{
  margin-top: 18px;
  margin-left:55px;
}
</style>
