<!--菜单栏-->
<template>
  <div class="menu">
    <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" router @select="handleSelect">
      <el-menu-item index="/" class="shouye">>首页</el-menu-item>
      <el-tabs stretch=true class="demo-tabs">
        <el-tab-pane label="水文分析"></el-tab-pane>
        <el-tab-pane label="其他功能"></el-tab-pane>
      </el-tabs>
      <router-view />
    </el-menu>

  </div>
</template>
  
<script lang="ts">
import { defineComponent, ref } from 'vue';


export default defineComponent({
  name: 'Menu',
  components: {

  },
  setup() {
    const activeIndex = ref('1')
    const handleSelect = (key: string, keyPath: string[]) => {
      console.log(key, keyPath)
    }
    return {
      activeIndex,
      handleSelect,
    }
  }

});
</script>

<style scoped>
.shouye {
  margin-top: 1.5px;
  margin-right: 15px;
}

.menu {
  padding: 0px;
  margin: 0px;
}

.el-menu-demo {
  height: 40px;
}
</style>