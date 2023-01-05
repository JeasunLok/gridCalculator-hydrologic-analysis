<!--主页-->
<template>
  <el-row>
    <el-col :span="24">
        <div class="grid-content ep-bg-purple-dark" >
            <HomeHeader/>
        </div>
    </el-col>
  </el-row>
  <el-row class="main-content">
    <el-col :span="24">
        <div class="grid-content ep-bg-purple-dark">
            <p class="title">网格计算类计算器</p>
            <p class="explain">基于水文分析</p>
            <div class="buttonContain">
              <el-button type="primary" class="button" round>
                <router-link to="/homeview" class="turn">开始体验</router-link> 

              </el-button>
            </div>
        </div>
    </el-col>
  </el-row>
  <el-row>
    <el-col :span="6" :offset="4">
      <div class="grid-content ep-bg-purple" >
        <!--简介左边的图片-->
        <img class="imgL" src="../assets/R-C.jpg"/>
      </div>
    </el-col>
    <el-col :span="8" :offset="3">
      <!--简介-->
      <div class="introduce grid-content ep-bg-purple-light" >
        <span></span>
        <div class="ex">
          <p>在这里，你可以通过输入对应</p>
          <p>区域的降雨站、降雨量和高程</p>
          <p>数据，通过网格计算类进行对</p>
          <p>应的水文分析，完成对应任务</p>
        </div>
      </div>
    </el-col>
  </el-row>
  <!--功能介绍-->
  <el-row>
    <el-col :span="5" :offset="2">
      <div class="function grid-content1 ep-bg-purple" id="box">
        <!--功能1-->
        降雨插值计算功能<br/>
        
      </div>
    </el-col>
    <el-col :span="5" :offset="2">
      <div class="function grid-content2 ep-bg-purple" id="box">
        <!--功能2-->
        坡度坡向流向流量<br/>
        
      </div>
    </el-col>
    <el-col :span="5" :offset="2">
      <div class="function grid-content3 ep-bg-purple" id="box">
        <!--功能3-->
        提取河网与山脊线<br/>
      </div>
    </el-col>
  </el-row>
 </template>
  
  
<script lang="ts">
  import { defineComponent } from 'vue';
  import HomeHeader from '@/components/HomeHeader.vue';
  
 
  
  export default defineComponent({
    name: 'HomeView',
    components: {
      //HelloWorld,
      HomeHeader

    },
  });
</script>
  
<style>

.main-content{
    background-image: url(../assets/OIP-C.jpg);
    height:600px;
    margin-top: 0;
    background-size:100%;

}
.main-content .grid-content .title{
    font-size:50px;
    font-family:  Arial, “Microsoft YaHei”;
    color: aliceblue;
    font-weight: bolder;
    text-align: center;
    line-height: 200px;
}
.main-content .grid-content .explain{
    font-size:20px;
    font-family:  Arial, “Microsoft YaHei”;
    color: aliceblue;
    font-weight: bolder;
    text-align: center;
    line-height: 10px;
}
.buttonContain{
  text-align: center;
}
.turn{
  text-decoration: none;
}

 .el-row {
  margin-bottom: 40px;
}
.el-row:first-child {
  margin-bottom: 0;
}
.el-row:last-child {
  margin-bottom: 0;
  background: black;

  height:400px;
}
.el-col {
  border-radius: 4px;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
} 

.imgL{
  width: 400px;
  height:300px;
}

.ex{
  text-align: center;
  line-height: 20px;
  padding-top: 34px;
  font-size: 25px;
  color: black;
  font-family: Arial, “Microsoft YaHei”;
}


#box {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 400px;
  height: 300px;
  color: white;
  font-family: 'Raleway';
  font-size: 2.5rem;
}

.function {
  --borderWidth: 3px;
  background: #1D1F20;
  position: relative;
  border-radius: var(--borderWidth);
}
.function:after {
  content: '';
  position: absolute;
  top: calc(-1 * var(--borderWidth));
  left: calc(-1 * var(--borderWidth));
  height: calc(100% + var(--borderWidth) * 2);
  width: calc(100% + var(--borderWidth) * 2);
  background: linear-gradient(60deg, #f79533, #f37055, #ef4e7b, #a166ab, #5073b8, #1098ad, #07b39b, #6fba82);
  border-radius: calc(2 * var(--borderWidth));
  z-index: -1;
  animation: animatedgradient 3s ease alternate infinite;
  background-size: 300% 300%;
}

</style>