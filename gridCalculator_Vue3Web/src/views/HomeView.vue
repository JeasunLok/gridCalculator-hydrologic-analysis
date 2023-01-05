<!--主页-->
<template>
  <div class="homeview">
    <el-container class="box">
      <el-header class="header_contain">
        <Header />
      </el-header>
      <el-header class="menu_contain">
        <Menu />
      </el-header>
      <el-container class="low">
        <el-aside class="choice">
          <Choice></Choice>
        </el-aside>
        <el-main class="map_fill"><Map></Map></el-main>
      </el-container>
    </el-container>
  </div>
</template>


<script lang="ts">
import { defineComponent, reactive, provide } from 'vue';
// import axios from "axios";
import Menu from '@/components/Menu.vue';
import Header from '@/components/Header.vue';
import Choice from '@/components/Choice.vue';
import Map from '@/components/Map.vue';


export default defineComponent({
  name: 'HomeView',
  components: {
    Menu,
    Header,
    Choice,
    Map
  },

});
</script>

<style>
.homeview {
  border: 0.5px solid black;
}

.box {
  padding: 0px;
  height: 100%;
  margin: 0px;
}

.header_contain {
  height: 60px;
  border: 1px solid black;
  text-align: center;
  background-color: black;
}

.menu_contain {
  height: 41px !important;
  text-align: center;
}

.low {
  height: calc(100vh - 98px);
  border: 0.5px solid black;
}


.map_fill {
  width: 60%;
  height: auto;
  border: 0.5px solid black;
}

.choice {
  width: 40%;
  height:auto;
  border: 0.5px solid black;
}
</style>

