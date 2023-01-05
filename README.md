### 智慧城市建设原理与方法课程设计（大作业）
***
#### 本项目主要由两个部分组成
##### 1.使用Java作为主要编程语言的gridCalculator，包含桌面版GUI
##### 2.使用Vue3+TypeScript+Element Plus完成的gridCalculator_Vue3Web静态网页
##### 在整个项目的压缩文件中分别对应
1. gridCalculator
2. gridCalculator_Vue3Web 
##### 以下对两个部分做主要的介绍
***
##### 1.gridCalculator
该文件夹的主要结构是IDEA的目录组成

1. out <b>构建的jar工件</b>
2. result <b>所有算法的asc以及jpg输出结果</b>
3. src <b>源码文件</b>

以下对源码文件夹src做简单的介绍
1. gridCalculator 
    * gridCalculator.java <b>网格计算类类文件</b>
2. gridDatabase
    * gridDatabase.java <b>网格计算类的数据库操作接口类文件</b>
3. supportFunction
    * supportFunction.java <b>辅助网格计算类的支撑函数类文件</b>
    * readDEM.java <b>读取DEM类文件</b>
    * readRainFallStation.java <b>读取雨量站及其降雨量类文件</b>
    * supportIterForAcc_flow.java <b>辅助累积流计算类文件</b>
    * writeASC.java <b>写出asc类文件</b>
4. lib
    * Jama-1.0.3.jar <b>Jama矩阵操作依赖jar包</b>
    * mysql-connector-java-8.0.30.jar <b>MySQL数据库操作依赖jdbc的jar包</b>
5. META-INF
    * MANIFEST.MF <b>构建项目jar包文件</b>
6. gridCalculator_GUI.java <b>图形UI界面程序入口，可直接运行</b>
7. gridCalculator_test.java <b>控制台程序入口，可直接运行</b>

##### 需要注意的是，想要运行本程序必须在IDEA中的依赖引入lib文件夹的两个jar包！
***
##### 2.gridCalculator_Vue3Web
该文件夹的主要结构Vue3的脚手架Vue-cli的目录组成

1. node_modules <b>npm包</b>
2. public <b>网页入口</b>
3. src <b>源码及资源文件</b>
4. 其余设置文件

以下对源码文件夹src做简单的介绍
1. asset <b>资源目录</b>
2. components <b>基本组件</b>
    * Choice.vue <b>功能选择组件</b>
    * Header.vue <b>头部组件</b>
    * HomeHeader.vue <b>主页头部组件</b>
    * Map.vue <b>图层展示组件</b>
    * Menu.vue <b>菜单栏组件</b>
    * User.vue <b>用户栏组件</b>
3. routers <b>跳转路由</b>
    * index.ts <b>路由文件</b>
4. views <b>基本视图</b>
    * Home.vue <b>主页视图</b>
    * HomeView.vue <b>功能操作视图</b>
    * Register.vue <b>登录注册视图</b>
5. App.vue <b>界面主入口</b>
6. main.ts <b>引入资源文件</b>
7. shims-vue.d.ts <b>vue声明文件</b>

##### 需要注意的是，想要运行本页面必须在对应的文件夹目录下cmd后输入npm install且必须安装vue-cli脚手架以及选定使用TypeScript作为js代码，引入Element Plus依赖之后键入npm run serve并去往对应端口查看网页。
***