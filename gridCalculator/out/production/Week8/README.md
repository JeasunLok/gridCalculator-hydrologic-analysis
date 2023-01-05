## Week7:本周智慧城市作业 
### 在Week6的基础上进行多线程编写
#### 由于采用项目且分文件编写 因而项目打包发送 源代码在src目录下
## Version_2 2022.10.20
### 新增：多线程编写
1. 所有辅助类均继承Thread类 便于多线程编写
2. 主程序中对readRainFallStation类以及readDEM类的数据读取部分进行多线程处理
3. 网格计算类的初始化也使用多线程并增添join()部分
4. 计算降雨插值矩阵以及流量、流向中由于包含辅助类，辅助类也使用多线程处理
## Version_1 2022.10.13
***
#### 程序主文件
###### gridCalculator_test.java 
程序测试主文件 包含文件输出
***
#### 辅助类文件：存在于两个包中
##### 1.gridCalculator包
###### girdCaclculator.java 网格计算类
包含网格计算所需的数据定义、构造函数、降雨量插值函数、流向计算函数、累积流计算函数，在主函数中通过初始化该类对象进行操作
##### 2.supportFunction包
###### (1)readDEM 读取DEM类
读取DEM.asc文件 可用于读取asc类型的文件
###### (2)readRainFallStation 读取降雨站及其降雨量类
读取StationProperty.txt文件以及rain.txt文件
###### (3)supportFunction 辅助函数类
用于计算降雨量插值、流向、累积流的复杂算法所用函数集成的类
###### (4)supportIterForAcc_flow 辅助累积流迭代计算类
用于辅助累积流计算的类 算法比较复杂
***
#### 使用IDEA将Week6项目作为源目录后直接运行gridCalculator_test.java程序主文件即可得到输出结果
##### 数据dem.asc StationProperty.txt rain.txt已在src目录下
##### 数据rainfall_interpolation.asc为降雨量插值结果asc文件
##### 数据flow.asc为流向计算结果asc文件
##### 数据acc_flow.asc为累积流计算结果asc文件
##### 注意:height.asc为课程PPT上给出的5x5高程矩阵的asc文件