<!--页头-->
<template>
  <div class="header">
    <a href="#">
      <span class="header-title">水文分析网格计算器</span>
    </a>
    <el-dropdown @command="handleCommand">
      <span class="el-dropdown-link">
        登录/注册
        <i class="el-icon-arrow-down el-icon--right"></i>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <!--注册与登录跳转同一界面-->
          <el-dropdown-item icon="el-icon-edit">
            <router-link to="/user" class="turn">>注册</router-link>
          </el-dropdown-item>
          <el-dropdown-item icon="el-icon-s-operation" command="quit">
            <router-link to="/user" class="turn">>登录</router-link>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>
  
<script lang="ts">
import { defineComponent, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus'

export default defineComponent({
  name: 'Header',
  components: {
    //HelloWorld,
  },
  setup() {
    //测试 
    function handleCommand(command: MouseEvent) {
      alert(`点击了${command}`)
    }
    return {
      handleCommand,
    }
  }

});
</script>

<style scoped>
.header {
  position: absolute;
  line-height: 60px;
  top: 0px;
  left: 0px;
  right: 0px;
}

.header-title {
  position: absolute;
  text-align: center;
  /* 居中 */
  color: white;
  font-size: 24px;
  margin-top: 5px;
}

/* 下拉菜单 */
.el-dropdown {
  float: right;
  /* 浮动在右边 */
  margin-right: 40px;
  /* 靠右40px */
  margin-top: 20px;
}

/* 系统管理 */
.el-dropdown-link {
  color: white;
  cursor: pointer;
  /* 鼠标放上去是手的形状 */
  font-size: 20px;
  margin-top: 6px;
}

.turn {
  text-decoration: none;
}
</style>