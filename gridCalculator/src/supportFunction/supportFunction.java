package supportFunction;
import gridCalculator.gridCalculator;
//辅助函数包
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import Jama.Matrix;

import javax.imageio.ImageIO;


public class supportFunction {
    public static double indexWithCellcornerForDistance(double cellSize,int i,int j,int di,int dj){
        //求解两点的欧氏距离
        double X=cellSize*(i-di);
        double Y=cellSize*(j-dj);
        return Math.sqrt(X*X+Y*Y);
    }
    public static double inverseDistanceWeightingForRainfall(ArrayList<ArrayList<Double>> Rainfall,ArrayList<Double> standardX
            ,ArrayList<Double> standardY,int i,int j,double cellSize,int standardNumber,ArrayList<Double> cellcorner){
        //反距离权重法
        double[] standardDistanceInverse=new double[standardNumber];
        for(int n=0;n<standardNumber;n++){
            double d=indexWithCellcornerForDistance(cellSize,i,j,(int)((standardY.get(n)-cellcorner.get(1))/cellSize),
                                                                 (int)((standardX.get(n)-cellcorner.get(0))/cellSize));
            standardDistanceInverse[n]=1/(d*d);
        }
        double totalDistanceInverse=0;
        for(int n=0;n<standardNumber;n++){
            totalDistanceInverse=totalDistanceInverse+standardDistanceInverse[n];
        }
        double[] weightForRainfall=new double[standardNumber];
        for(int n=0;n<standardNumber;n++){
            weightForRainfall[n]=standardDistanceInverse[n]/totalDistanceInverse;
        }
        double resultRainfall=0;
        for(int n=0;n<standardNumber;n++){
            resultRainfall=resultRainfall+weightForRainfall[n]*
                    Rainfall.get((int)((standardY.get(n)-cellcorner.get(1))/cellSize))
                            .get((int)((standardX.get(n)-cellcorner.get(0))/cellSize));
        }
        return resultRainfall;
    }
    public static double XYtoRainfall(ArrayList<ArrayList<Double>>  Rainfall,int cellSize,int gridNumber,int x,int y){
        //坐标与数组索引的转换算法
        double i=Math.floor((double)(y)/(double)cellSize);
        double j=Math.floor((double)(x)/(double)cellSize);
        return Rainfall.get((int)i).get((int)j);
    }//本程序用不到

    public static Matrix semi_station(int NumStation,int[][] StationLocation,double cellSize,double[][] Inipre)
    {
        double[][] Semi_variogram=new double[NumStation+1][NumStation+1];
        for(int i=0;i<NumStation+1;i++)
        {
            for (int j = 0; j < NumStation+1; j++) {
                if (j != NumStation&&i!=NumStation)
                    Semi_variogram[i][j] = (Inipre[StationLocation[i][0]][StationLocation[i][1]] - Inipre[StationLocation[j][0]][StationLocation[j][1]])*(Inipre[StationLocation[i][0]][StationLocation[i][1]] - Inipre[StationLocation[j][0]][StationLocation[j][1]])/ 2;
                else if(j == NumStation&&i==NumStation)
                    Semi_variogram[i][j]=0;
                else
                    Semi_variogram[i][j]=1;
            }
        }
        Matrix Semi_variogram_m=new Matrix(Semi_variogram);
        Semi_variogram_m=Semi_variogram_m.inverse();
        return Semi_variogram_m;
    }

    //变差函数,球状模型,要改***
    public static double r_cir(double dis,double a,double c0,double c1)
    {
        if(dis<=a)
        {
            return c0+c1*(1.5*dis/a-Math.pow((dis/a),3)/2);
        }
        else {
            return c0+c1;
        }
    }

    //计算每个点的半方差函数值
    public static Matrix semi_dest(int NumStation,int row,int colum,double CellSize,int[][] StationLocation,double a,double c0,double c1)
    {
        double [][]distance=new double[NumStation+1][row*colum];
        for(int i=0;i<row;i++)
            for(int j=0;j<colum;j++)
            {
                for(int m=0;m<NumStation+1;m++)
                {
                    if(m==NumStation)
                        distance[m][i*colum+j]=1;
                    else
                    {
                        distance[m][i*colum+j]= CellSize*Math.abs(Math.sqrt((i-StationLocation[m][0])*(i-StationLocation[m][0])+(j-StationLocation[m][1])*(j-StationLocation[m][1])));
                        distance[m][i*colum+j]=r_cir(distance[m][i*colum+j],a,c0,c1);
                    }
                }
            }
        //定义插值点的半方差矩阵
        Matrix r_des=new Matrix(distance);
        return r_des;
    }

    public static void filling_Iterate(int line, int column, int NODATA_value, double[][] H, double[][][] grid) {
        for (int i = 1; i < line - 1; i++) {
            for (int j = 1; j < column - 1; j++) {
                //寻找非NODATA_value网格8邻域中，除NODATA_value值外的最小值
                if (H[i][j] != grid[i][j][0] && H[i][j] != NODATA_value) {
                    double[] neighbor = {H[i - 1][j + 1], H[i][j + 1], H[i + 1][j + 1],
                            H[i - 1][j], H[i + 1][j],
                            H[i - 1][j - 1], H[i][j - 1], H[i + 1][j - 1]};
                    Arrays.sort(neighbor);
                    double neighbor_min = 0;
                    for (int k = 0; k < neighbor.length; k++) {
                        if (neighbor[k] != NODATA_value) {
                            neighbor_min = neighbor[k];
                            break;
                        }
                    }
                    // 将网格 C 的当前值与邻域网格值作比较，若 C 的当前值大于邻域网格值加上极小变量的值，则将网格 C 重新赋值为邻域网格值加上极小变量
                    // 遍历8个领域的结果 == 寻找最小的领域网格加上极小变量的值，并将它与 C当前值 对比
                    if (H[i][j] > neighbor_min + 0.001) {
                        H[i][j] = neighbor_min + 0.001;
                    }
                    // 判断网格原始高程与邻域网格值加上极小变量的大小关系，若原始高程更大，则将网格新值重新赋值为原始高程
                    // 遍历8个邻域的结果 == 寻找最小的领域网格加上极小变量的值，并将它与 原始高程 对比
                    if (grid[i][j][0] > neighbor_min + 0.001) {
                        H[i][j] = grid[i][j][0];
                    }
                }
            }
        }
    }

    public static int findDirection(int x0,int y0,int x,int y){
        //方向输出 输入中心坐标(x0,y0)以及判断坐标(x,y)
        int direction=0;
        //使用多个条件判断流向
        if((x0==x)&&(y0==y)){
            direction=0;
        }
        else if((x0==x)&&(y0==y-1)){
            direction=1;
        }
        else if((x0==x-1)&&(y0==y-1)){
            direction=2;
        }
        else if((x0==x-1)&&(y0==y)){
            direction=4;
        }
        else if((x0==x-1)&&(y0==y+1)){
            direction=8;
        }
        else if((x0==x)&&(y0==y+1)){
            direction=16;
        }
        else if((x0==x+1)&&(y0==y+1)){
            direction=32;
        }
        else if((x0==x+1)&&(y0==y)){
            direction=64;
        }
        else if((x0==x+1)&&(y0==y-1)){
            direction=128;
        }
        else{
            System.out.println("Direction ERROR!");//异常输入
            System.exit(0);
        }
        return direction;
    }

    public static int findFlow(ArrayList<ArrayList<Double>> height,int x,int y){
        //流向输出 传入高程数组以及需要判断的中心点(x,y)
        int NoData=-9999;
        //无数据
        int result_i=x;
        int result_j=y;
        double maxHeightDifference=0;
        //八邻域搜索流向
        for(int i=x-1;i<=x+1;i++){
            for(int j=y-1;j<=y+1;j++){
                if(height.get(i).get(j)!=(double)NoData){
                    double tempHeightDifference=(height.get(x).get(y)-height.get(i).get(j))/Math.sqrt((x-i)*(x-i)+(y-j)*(y-j));
                    if(tempHeightDifference>maxHeightDifference){
                        maxHeightDifference=tempHeightDifference;
                        result_i=i;
                        result_j=j;
                    }
                }
            }
        }
        return findDirection(x,y,result_i,result_j);
        //调用方向找寻函数
    }
    public static int calculateAcc_flow_1(ArrayList<ArrayList<Integer>> flow,int x,int y){
        // 判断是否有流向经过
        int NODATA_VALUE=-9999;
        if(flow.get(x).get(y)==NODATA_VALUE){
            return NODATA_VALUE;
        }
        int flow_mark=0;
        for(int i=x-1;i<=x+1;i++){
            for(int j=y-1;j<=y+1;j++){
                if(isAcc_flow(flow,x,y,i,j)){
                    flow_mark=1;
                }
            }
        }
        return flow_mark;//返回标记
    }
    public static boolean isAcc_flow(ArrayList<ArrayList<Integer>> flow,int x0,int y0,int x,int y) {
        // 八邻域流向是否经过判断条件函数
        if ((x0 == x) && (y0 == y - 1) && (flow.get(x).get(y) == 16)) {
//            direction=1;
            return true;
        } else if ((x0 == x - 1) && (y0 == y - 1) && (flow.get(x).get(y) == 32)) {
//            direction=2;
            return true;
        } else if ((x0 == x - 1) && (y0 == y) && (flow.get(x).get(y) == 64)) {
//            direction=4;
            return true;
        } else if ((x0 == x - 1) && (y0 == y + 1) && (flow.get(x).get(y) == 128)) {
//            direction=8;
            return true;
        } else if ((x0 == x) && (y0 == y + 1) && (flow.get(x).get(y) == 1)) {
//            direction=16;
            return true;
        } else if ((x0 == x + 1) && (y0 == y + 1) && (flow.get(x).get(y) == 2)) {
//            direction=32;
            return true;
        } else if ((x0 == x + 1) && (y0 == y) && (flow.get(x).get(y) == 4)) {
//            direction=64;
            return true;
        } else if ((x0 == x + 1) && (y0 == y - 1) && (flow.get(x).get(y) == 8)) {
//            direction=128;
            return true;
        } else {
            return false;
        }
    }

    public static supportIterForAcc_flow calculateAcc_flow_2(int x0,int y0,int n,supportIterForAcc_flow sIFA,
                                                         ArrayList<ArrayList<Integer>> flow){
        // 迭代计算累积流 算法比较复杂 总的来说是追踪每一条流向的算法
        int NODATA_VALUE=-9999;
        if((flow.get(x0).get(y0)==NODATA_VALUE)||(flow.get(x0).get(y0)==0)){
            return sIFA;
        }
        else{
            int direction=flow.get(x0).get(y0);
            int x=x0;
            int y=y0;
            if(direction==1){
                x=x0;
                y=y0+1;
            }
            else if(direction==2){
                x=x0+1;
                y=y0+1;
            }
            else if(direction==4){
                x=x0+1;
                y=y0;
            }
            else if(direction==8){
                x=x0+1;
                y=y0-1;
            }
            else if(direction==16){
                x=x0;
                y=y0-1;
            }
            else if(direction==32){
                x=x0-1;
                y=y0-1;
            }
            else if(direction==64){
                x=x0-1;
                y=y0;
            }
            else if(direction==128){
                x=x0-1;
                y=y0+1;
            }
            if(sIFA.acc_flow_mark.get(x0).get(y0)==0){
                n++;
            }
            sIFA=calculateAcc_flow_2(x,y,n,sIFA,flow);//迭代
            sIFA.acc_flow.get(x).set(y,sIFA.acc_flow.get(x).get(y)+n);
            sIFA.acc_flow_mark.get(x0).set(y0,1);
        }
        return sIFA;//返回辅助累积流计算对象 包含累积流 标记 以及流向是否经过矩阵
    }

    //线性拉伸函数
//    public static  ArrayList<ArrayList<Integer>> Norm_img(ArrayList<ArrayList<Double>> ini, int NODATA_VALUE)
//    {
//        double max;
//        double min;
//        int row=ini.size();
//        int colum=ini.get(0).size();
//        ArrayList<Double> maxArr=new ArrayList<Double>();
//        ArrayList<Double> minArr=new ArrayList<Double>();
//        for(int i=0;i<row;i++)
//        {
//            maxArr.add(Collections.max(ini.get(i)));
//            minArr.add(Collections.min(ini.get(i)));
//        }
//        max= Collections.max(maxArr);
//        min=Collections.min(minArr);//获取最大最小值
//        double dis=max-min;
//        ArrayList<ArrayList<Double>>nor_img_D = new ArrayList<ArrayList<Double>>();
//        ArrayList<ArrayList<Integer>>nor_img_I = new ArrayList<ArrayList<Integer>>();
//        for(int i=0;i<row;i++)
//        {
//            ArrayList<Double>imi_tem=new ArrayList<Double>();
//            for(int j=0;j<colum;j++)
//            {
//                if(ini.get(i).get(j)==-NODATA_VALUE)
//                {
//                    imi_tem.add(0.0);
//                }
//                else
//                {
//                    double m=ini.get(i).get(j)-min;
//                    double por=255*m/dis-128;//拉伸
//                    imi_tem.add(por);
//
//                }
//            }
//            nor_img_D.add(imi_tem);
//        }
//        for(int i=0;i<row;i++)
//        {
//            ArrayList<Integer>nor_img_I_tmp=new ArrayList<Integer>();
//            for(int j=0;j<colum;j++)
//            {
//                nor_img_I_tmp.add( (nor_img_D.get(i).get(j).intValue()));
//            }
//            nor_img_I.add(nor_img_I_tmp);
//        }
//        return nor_img_I;
//    }

    public static ArrayList<ArrayList<Integer>> Norm_img(ArrayList<ArrayList<Double>> ini, int NODATA_VALUE)
    {
        double max;
        double min;
        int row=ini.size();
        int colum=ini.get(0).size();
        ArrayList<Double> maxArr=new ArrayList<Double>();
        ArrayList<Double> minArr=new ArrayList<Double>();
        //获取列中的最大值
        for(int i=0;i<row;i++)
        {
            maxArr.add(Collections.max(ini.get(i)));
        }
        //获取列中的最小值
        ArrayList<ArrayList<Double>> ini1=new ArrayList<ArrayList<Double>>();
        for(int i=0;i<row;i++)
        {
            ArrayList<Double> ini1_temp=new ArrayList<Double>();
            for(int j=0;j<colum;j++)
            {
                if(ini.get(i).get(j)==NODATA_VALUE){
                    ini1_temp.add((double)-NODATA_VALUE);
                }
                else{
                    ini1_temp.add(ini.get(i).get(j));
                }
            }
            ini1.add(ini1_temp);
        }

        for(int i=0;i<row;i++)
        {
            minArr.add(Collections.min(ini1.get(i)));
        }

        max=Collections.max(maxArr);
        min=Collections.min(minArr);//获取最大最小值
        double dis=max-min;
        ArrayList<ArrayList<Double>>nor_img_D = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Integer>>nor_img_I = new ArrayList<ArrayList<Integer>>();
        for(int i=0;i<row;i++)
        {
            ArrayList<Double>imi_tem=new ArrayList<Double>();
            for(int j=0;j<colum;j++)
            {
                if(ini.get(i).get(j)==NODATA_VALUE)
                {
                    imi_tem.add(0.0);
                }
                else
                {
                    double m=ini.get(i).get(j)-min;
                    double por=255*m/dis;//拉伸
                    imi_tem.add(por);

                }
            }
            nor_img_D.add(imi_tem);
        }
        for(int i=0;i<row;i++)
        {
            ArrayList<Integer>nor_img_I_tmp=new ArrayList<Integer>();
            for(int j=0;j<colum;j++)
            {
                nor_img_I_tmp.add( (nor_img_D.get(i).get(j).intValue()));
            }
            nor_img_I.add(nor_img_I_tmp);
        }
        return nor_img_I;
    }

    //转成图片
    public static void saveImg_Double(ArrayList<ArrayList<Double>> data ,String path,int NODATA_VALUE) throws IOException
    {
        ArrayList<ArrayList<Integer>> arr_im=Norm_img(data,NODATA_VALUE);//归一化,将数据转为0-255
        int width=data.get(0).size();
        int height=data.size();
        BufferedImage ims=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
        byte[]in=new byte[width*height];
        int k=0;
        for(int i=0;i<height;i++)
        {
            for(int j=0;j<width;j++)
            {
                in[k] =arr_im.get(i).get(j).byteValue();
                k++;
            }
        }
        ims.getRaster().setDataElements(0,0,width,height,in);
        File outputfile = new File(path);
        ImageIO.write(ims, "jpg", outputfile);
    }

    public static void saveImg_Integer(ArrayList<ArrayList<Integer>> data ,String path,int NODATA_VALUE) throws IOException
    {
        ArrayList<ArrayList<Double>> data_double = new ArrayList<ArrayList<Double>>();
        for(int i=0;i<data.size();i++){
            ArrayList<Double> temp=new ArrayList<Double>();
            for(int j=0;j<data.get(0).size();j++){
                temp.add((double)data.get(i).get(j));
            }
            data_double.add(temp);
        }
        ArrayList<ArrayList<Integer>> arr_im=Norm_img(data_double,NODATA_VALUE);//归一化,将数据转为0-255
        int width=data.get(0).size();
        int height=data.size();
        BufferedImage ims=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
        byte[]in=new byte[width*height];
        int k=0;
        for(int i=0;i<height;i++)
        {
            for(int j=0;j<width;j++)
            {
                in[k] =arr_im.get(i).get(j).byteValue();
                k++;
            }
        }
        ims.getRaster().setDataElements(0,0,width,height,in);
        File outputfile = new File(path);
        ImageIO.write(ims, "jpg", outputfile);
    }


}
