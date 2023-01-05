import gridCalculator.gridCalculator;
import gridDatabase.*;
import supportFunction.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.sql.*;

public class gridCalculator_test {
    public static void main(String[] args) throws Exception {
        //首先创建数据库
        String URL = "jdbc:mysql://localhost:3306/";
        String Database = "grid";
        String user = "root";
        String password = "12345678";
        gridDatabase.createDatabase(URL, Database, user, password);
        //两个读取函数，分别都写了一个类，里面使用了数据库
        //多线程全部使用继承Thread类编码
        readRainFallStation rRFS=new readRainFallStation("src/StationProperty.txt","src/rain.txt");
        //rRFS线程
        rRFS.start();
        readDEM rDEM=new readDEM("src/dem.asc");
        //rDEM线程
        rDEM.start();

        int NODATA_VALUE=rDEM.NODATA_VALUE;
        int nrows=rDEM.nrows;
        int ncolumns=rDEM.ncols;
        double cellsize=rDEM.cellsize;
        int standardNumber= rRFS.standardNumber;
        ArrayList<Double> cellcorner=new ArrayList<Double>();
        cellcorner.add(rDEM.xllcorner);
        cellcorner.add(rDEM.yllcorner);
        ArrayList<ArrayList<Double>> DEM=rDEM.DEM;
        ArrayList<Double> standardX=rRFS.standardX;
        ArrayList<Double> standardY=rRFS.standardY;
        ArrayList<Double> standardRainfall=rRFS.standardRainfall;

        //数据库准备工作
        gridDatabase.createTable(URL, Database, user, password, ncolumns);

        // 网格计算类对象
        gridCalculator gC=new gridCalculator(NODATA_VALUE,nrows,ncolumns,cellsize,
                cellcorner,DEM,standardNumber,standardX,standardY,standardRainfall);

        //gC线程
        gC.start();
        try {
            gC.join();
        }catch(InterruptedException e) {
            System.out.println("gridCalculator Thread interrupted.");
        }

        // 根据降雨站降雨量的IDW插值结果
        System.out.println("the rainfall after IDW interpolation is below:");
        ArrayList<ArrayList<Double>> rainfall_IDW=gC.VoronoiDWithDistance_ForRainfall();
//        for(ArrayList<Double> i:rainfall_IDW){
//            System.out.println(i);
//        }
        System.out.println();

        // 根据降雨站降雨量的克里金插值结果
        System.out.println("the rainfall after Kriging interpolation is below:");
        double a=28788;double c0=0.137;double c1=2.91;
        ArrayList<ArrayList<Double>> rainfall_Kriging=gC.Kriging_ForRainfall(a,c0,c1);
//        for(ArrayList<Double> i:rainfall_Kriging){
//            System.out.println(i);
//        }
        System.out.println();
//
//        // 根据M_V算法计算的填洼结果
        System.out.println("the filling_DEM is below:");
        ArrayList<ArrayList<Double>> filling_DEM=gC.filling_M_V();
//        for(ArrayList<Integer> i:filling_DEM){
//            System.out.println(i);
//        }
        System.out.println();
//
//        // 根据DEM的坡度结果
        System.out.println("the slope is below:");
        ArrayList<ArrayList<Double>> slope=gC.calculate_Slope_Aspect("Slope");
//        for(ArrayList<Double> i:slope){
//            System.out.println(i);
//        }
        System.out.println();
//
//        // 根据DEM的坡向结果
        System.out.println("the aspect interpolation is below:");
        ArrayList<ArrayList<Double>> aspect=gC.calculate_Slope_Aspect("Aspect");
//        for(ArrayList<Double> i:aspect){
//            System.out.println(i);
//        }
        System.out.println();
//
        // 根据填洼后的DEM计算的流向结果
        System.out.println("the flow is below:");
        ArrayList<ArrayList<Integer>> flow=gC.flowCalculator();
        // 控制台输出结果
//        for(ArrayList<Integer> i:flow){
//            System.out.println(i);
//        }
        System.out.println();
//
//        // 根据流向结果计算的流量结果
        System.out.println("the flow_accumulate is below:");
        ArrayList<ArrayList<Integer>> acc_flow=gC.acc_flowCalculator();
//        for(ArrayList<Integer> i:acc_flow){
//            System.out.println(i);
//        }
//

        // 根据流量结果计算的河网结果
        System.out.println("the water_zone is below:");
        ArrayList<ArrayList<Integer>> water_zone=gC.waterzoneCalculator(30);
//        for(ArrayList<Integer> i:water_zone){
//            System.out.println(i);
//        }
//

        // 根据流量结果计算的山脊线结果
        System.out.println("the ridge_line is below:");
        ArrayList<ArrayList<Integer>> ridge_line=gC.ridgelineCalculator();
//        for(ArrayList<Integer> i:ridge_line){
//            System.out.println(i);
//        }
//

        // 将结果写入数据库中
        gridDatabase.insertData(URL, Database, user, password, nrows, ncolumns, rainfall_IDW, rainfall_Kriging, filling_DEM,
                slope, aspect, flow, acc_flow, water_zone, ridge_line);

        // 写出所有文件
        writeASC write_rfi_IDW=new writeASC("src/rainfall_IDWinterpolation.asc",nrows,ncolumns,rDEM.xllcorner,rDEM.yllcorner,
                                    cellsize,NODATA_VALUE,rainfall_IDW,0);

        writeASC write_rfi_Kriging=new writeASC("src/rainfall_Kriginginterpolation.asc",nrows,ncolumns,rDEM.xllcorner,rDEM.yllcorner,
                cellsize,NODATA_VALUE,rainfall_Kriging,0);

        writeASC write_filling_DEM=new writeASC("src/filling_DEM.asc",nrows,ncolumns,rDEM.xllcorner,rDEM.yllcorner,
                cellsize,NODATA_VALUE,filling_DEM,0);

        writeASC write_slope=new writeASC("src/slope.asc",nrows,ncolumns,rDEM.xllcorner,rDEM.yllcorner,
                cellsize,NODATA_VALUE,slope,0);

        writeASC write_aspect=new writeASC("src/aspect.asc",nrows,ncolumns,rDEM.xllcorner,rDEM.yllcorner,
                cellsize,NODATA_VALUE,aspect,0);

        writeASC write_flow=new writeASC("src/flow.asc",nrows,ncolumns,rDEM.xllcorner,rDEM.yllcorner,
                cellsize,NODATA_VALUE,flow,0);

        writeASC write_accflow=new writeASC("src/acc_flow.asc",nrows,ncolumns,rDEM.xllcorner,rDEM.yllcorner,
                cellsize,NODATA_VALUE,acc_flow,0);

        writeASC write_waterzone=new writeASC("src/water_zone.asc",nrows,ncolumns,rDEM.xllcorner,rDEM.yllcorner,
                cellsize,NODATA_VALUE,water_zone,0);

        writeASC write_ridgeline=new writeASC("src/ridge_line.asc",nrows,ncolumns,rDEM.xllcorner,rDEM.yllcorner,
                cellsize,NODATA_VALUE,ridge_line,0);

        // IDW插值结果转成jpg输出
        supportFunction.saveImg_Double(rainfall_IDW,"src/rainfall_IDWinterpolation.jpg",NODATA_VALUE);
        // Kriging插值结果转成jpg输出
        supportFunction.saveImg_Double(rainfall_Kriging,"src/rainfall_Kriginginterpolation.jpg",NODATA_VALUE);
        // 填洼结果转成jpg输出
        supportFunction.saveImg_Double(filling_DEM,"src/filling_DEM.jpg",NODATA_VALUE);
        // 坡度结果转成jpg输出
        supportFunction.saveImg_Double(slope,"src/slope.jpg",NODATA_VALUE);
        // 坡向结果转成jpg输出
        supportFunction.saveImg_Double(aspect,"src/aspect.jpg",NODATA_VALUE);
        // 流向结果转成jpg输出
        supportFunction.saveImg_Integer(flow,"src/flow.jpg",NODATA_VALUE);
        // 累积流结果转成jpg输出
        supportFunction.saveImg_Integer(acc_flow,"src/acc_flow.jpg",NODATA_VALUE);
        // 河网提取结果转成jpg输出
        supportFunction.saveImg_Integer(water_zone,"src/water_zone.jpg",NODATA_VALUE);
        // 山脊线提取结果转成jpg输出
        supportFunction.saveImg_Integer(ridge_line,"src/ridge_line.jpg",NODATA_VALUE);
    }
}
