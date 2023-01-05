<!--侧边栏:相关设置-->
<template>
  <div>
    <el-container direction="horizontal" class="xuanzeqi">文件选择器
    </el-container>
    <el-divider />
    <el-container direction="horizontal" class="origin_layout_0">
      <el-upload v-model:file-list="fileList" class="upload-demo" action="http://localhost:8080/homeview"
        :on-change="handleChange">
        <el-button type="primary">上传高程asc文件</el-button>
        <template #tip>
        </template>
      </el-upload>
    </el-container>

    <el-container direction="horizontal" class="origin_layout">
      <el-upload v-model:file-list="fileList1" class="upload-demo" action="http://localhost:8080/homeview"
        :on-change="handleChange">
        <el-button type="primary">上传雨量站txt文件</el-button>
        <template #tip>
        </template>
      </el-upload>
    </el-container>

    <el-container direction="horizontal" class="origin_layout">
      <el-upload v-model:file-list="fileList2" class="upload-demo" action="http://localhost:8080/homeview"
        :on-change="handleChange">
        <el-button type="primary">上传降雨量txt文件</el-button>
        <template #tip>
        </template>
      </el-upload>
    </el-container>
    <el-divider />
    <el-container direction="horizontal" class="xuanzeqi">功能选择器</el-container>
    <el-container direction="horizontal" class="sql">
      <el-switch v-model="value1" class="mb-2" active-text="启用MySQL数据库" inactive-text="不启用MySQL数据库" />
    </el-container>
    <el-container direction="horizontal" class="zujian">
      <el-button type="primary" class="function1">IDW降雨量插值</el-button>
      <el-button type="primary" class="function2">克里金降雨量插值</el-button>
    </el-container>
    <el-container direction="horizontal" class="zujian">
      <el-button type="primary" class="function3">高程填洼计算</el-button>
      <el-button type="primary" class="function4">坡度坡向计算</el-button>
    </el-container>
    <el-container direction="horizontal" class="zujian">
      <el-button type="primary" class="function5">流向计算</el-button>
      <el-button type="primary" class="function6">累积流计算</el-button>
    </el-container>
    <el-container direction="horizontal" class="zujian">
      <el-button type="primary" class="function7">河网提取</el-button>
      <el-button type="primary" class="function8">山脊线提取</el-button>
    </el-container>
  </div>

</template>
  
<script lang="ts" setup>
import { ref } from 'vue'

import type { UploadProps, UploadUserFile } from 'element-plus'

const fileList = ref<UploadUserFile[]>([
  {
    name: 'dem.asc',
    url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100',
  }
])

const fileList1 = ref<UploadUserFile[]>([
  {
    name: 'StationProperty.txt',
    url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100',
  }
])


const fileList2 = ref<UploadUserFile[]>([
  {
    name: 'rain.txt',
    url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100',
  }
])

const value1 = ref(true)

const handleChange: UploadProps['onChange'] = (uploadFile, uploadFiles) => {
  fileList.value = fileList.value.slice(-3)
}
</script>

<style scoped>
.xuanzeqi {
  margin-top: 15px;
  margin-left: 250px;
  text-align: center;
  font-size: 24px;
  font-weight: bold;
}

.sql {
  margin-top: 15px;
  margin-left: 150px;
}

.upload-demo {
  margin-top: 8px;
  margin-left: 240px;
}

.origin_layout {
  margin-top: 5px;
}
.origin_layout_0 {
  margin-top: 5px;
}
.zujian {
  margin-top: 20px;
}
.function1{
  margin-left:150px;
}

.function2{
  margin-left:60px;
}


.function3{
  margin-left:157px;
}

.function4{
  margin-left:80px;
}

.function5{
  margin-left:170px;
}
.function6{
  margin-left:101px;
}
.function7{
  margin-left:170px;
}
.function8{
  margin-left:101px;
}
</style>