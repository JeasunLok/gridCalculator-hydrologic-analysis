package gridCalculator;
import java.util.ArrayList;
import supportFunction.supportFunction;
import supportFunction.supportIterForAcc_flow;
import java.util.Arrays;
import Jama.Matrix;

public class gridCalculator extends Thread{
    // 网格计算主类
    private int NODATA_VALUE;
    private int nrows;//行
    private int ncolumns;//列
    private double cellsize;//网格大小
    private int standardNumber;//降雨站数量
    private ArrayList<Double> cellcorner;//左下角起始坐标
    private ArrayList<ArrayList<Double>> DEM;//输入高程
    private ArrayList<Double> standardX;//降雨站X坐标
    private ArrayList<Double> standardY;//降雨站Y坐标
    private ArrayList<Double> standardRainfall;//降雨站数据
    private ArrayList<ArrayList<Double>> gridRainfall_IDW;//IDW插值降雨量矩阵
    private ArrayList<ArrayList<Double>> gridRainfall_Kriging;//克里金插值降雨量矩阵
    private ArrayList<ArrayList<Double>> Slope;//坡度矩阵
    private ArrayList<ArrayList<Double>> Aspect;//坡向矩阵
    private ArrayList<ArrayList<Double>> filling_M_V;//填洼高程矩阵
    private ArrayList<ArrayList<Integer>> flow;//流向矩阵
    private ArrayList<ArrayList<Integer>> acc_flow;//累积流矩阵
    private ArrayList<ArrayList<Integer>> water_zone;//河网矩阵
    private ArrayList<ArrayList<Integer>> ridge_line;//山脊线矩阵

    public void run(){
        System.out.println("girdCalculator线程:"+getName());
    }
    public gridCalculator(int n,int r,int c,double gS,ArrayList<Double> cC,ArrayList<ArrayList<Double>> h,int sN,ArrayList<Double> sX,
                          ArrayList<Double> sY,ArrayList<Double> sR){
        //构造函数
        NODATA_VALUE=n;
        nrows=r;
        ncolumns=c;
        cellsize=gS;
        cellcorner=cC;
        DEM=h;
        standardNumber=sN;
        standardX=sX;
        standardY=sY;
        standardRainfall=sR;
        gridRainfall_IDW =new ArrayList<ArrayList<Double>>();//IDW插值降雨量矩阵初始化
        gridRainfall_Kriging =new ArrayList<ArrayList<Double>>();//插值降雨量矩阵初始化
        for(int i=0;i<r;i++){//IDW插值降雨量矩阵置0
            ArrayList<Double> rainfalltemp=new ArrayList<Double>();
            for(int j=0;j<c;j++){
                rainfalltemp.add((double)0);
            }
            gridRainfall_IDW.add(rainfalltemp);
        }
        Slope= new ArrayList<ArrayList<Double>>();//坡度矩阵初始化
        Aspect= new ArrayList<ArrayList<Double>>();//坡向矩阵初始化
        filling_M_V= new ArrayList<ArrayList<Double>>();//填洼高程矩阵初始化
        flow=new ArrayList<ArrayList<Integer>>();//流向矩阵初始化
        acc_flow=new ArrayList<ArrayList<Integer>>();
        for(int i=0;i<r;i++){
            ArrayList<Integer> acc_flowtemp=new ArrayList<Integer>();
            for(int j=0;j<c;j++){
                acc_flowtemp.add(0);
            }
            acc_flow.add(acc_flowtemp);
        }
        water_zone=new ArrayList<ArrayList<Integer>>();//流向矩阵初始化
        ridge_line=new ArrayList<ArrayList<Integer>>();//流向矩阵初始化
    }

    public int getNODATA_VALUE(){return NODATA_VALUE;}
    public int getnrows(){return nrows;}
    public int getncolumns(){return ncolumns;}
    public double getcellsize(){return cellsize;}
    public ArrayList<Double> getcellcorner(){return cellcorner;}

    public ArrayList<ArrayList<Double>> getDEM(){return DEM;}
    public ArrayList<ArrayList<Double>> getgridRainfall_IDW(){return gridRainfall_IDW;}
    public ArrayList<ArrayList<Double>> getgridRainfall_Kriging(){return gridRainfall_Kriging;}
    public ArrayList<ArrayList<Double>> getFilling_M_V(){return filling_M_V;}
    public ArrayList<ArrayList<Double>> getSlope(){return Slope;}
    public ArrayList<ArrayList<Double>> getAspect(){return Aspect;}
    public ArrayList<ArrayList<Integer>> getFlow(){return flow;}
    public ArrayList<ArrayList<Integer>> getAcc_flow(){return acc_flow;}
    public ArrayList<ArrayList<Integer>> getWater_zone(){return water_zone;}
    public ArrayList<ArrayList<Integer>> getRidge_line(){return ridge_line;}

    public ArrayList<ArrayList<Double>> VoronoiDWithDistance_ForRainfall(){
        // 反距离权重插值法求降雨量
        for(int i=0;i<standardNumber;i++){
            gridRainfall_IDW.get((int)((standardY.get(i)-cellcorner.get(1))/cellsize))
                        .set((int)((standardX.get(i)-cellcorner.get(0))/cellsize),standardRainfall.get(i));
        }// 获得降雨站坐标及其数据

        for(int i=0;i<nrows;i++){
            for(int j=0;j<ncolumns;j++){
                int mark=1;// 标记：不根据反距离权重法改变降雨站的降雨量
                for(int t=0;t<standardNumber;t++){
                    if((i==(int)((standardY.get(t)-cellcorner.get(1))/cellsize))&&
                       (j==(int)((standardX.get(t)-cellcorner.get(0))/cellsize))){
                        mark=0;
                    }
                }
                if(mark==1){
                    gridRainfall_IDW.get(i).set(j, supportFunction.inverseDistanceWeightingForRainfall(gridRainfall_IDW,standardX,standardY,i,j,cellsize,standardNumber,cellcorner));
                }// 插值
            }
        }
        ArrayList<ArrayList<Double>> true_rainfall=new ArrayList<ArrayList<Double>>();
        for(int i=0;i<nrows;i++){
            true_rainfall.add(gridRainfall_IDW.get(nrows-i-1));
        }

        for(int i=0;i<nrows;i++){
            for(int j=0;j<ncolumns;j++){
                if(DEM.get(i).get(j)==NODATA_VALUE){
                    true_rainfall.get(i).set(j,(double)NODATA_VALUE);
                }
            }
        }

        gridRainfall_IDW=true_rainfall;
        return true_rainfall;// 返回插值后的降雨量矩阵
    }

    public ArrayList<ArrayList<Double>> Kriging_ForRainfall(double a,double c0,double c1){
        // 克里金插值法求降雨量
        double[][] Inipre=new double[nrows][ncolumns];//降雨量数据
        for(int i=0;i<standardNumber;i++){
            Inipre[(int)((standardY.get(i)-cellcorner.get(1))/cellsize)][(int)((standardX.get(i)-cellcorner.get(0))/cellsize)]=standardRainfall.get(i);
        }

        int [][]StationLocation=new int[standardNumber][2];//雨量站坐标
        for(int i=0;i<standardNumber;i++){
            StationLocation[i][0]=(int)((standardY.get(i)-cellcorner.get(1))/cellsize);
            StationLocation[i][1]=(int)((standardX.get(i)-cellcorner.get(0))/cellsize);
        }

        Matrix semi_sta=supportFunction.semi_station(standardNumber,StationLocation,cellsize,Inipre);//计算雨量站的半方差矩阵
        Matrix semi_des=supportFunction.semi_dest(standardNumber,nrows,ncolumns,cellsize,StationLocation,a,c0,c1);//计算每个站点的版方差函数
        Matrix weight=semi_sta.times(semi_des);//二者相乘得到权重
        double [][]z=new double[1][standardNumber+1];//将降雨量存入数组中
        double []sortIni=new double[standardNumber+1];
        double maxIni;
        double minIni;
        double maxKIni;
        double minKIni;

        for(int i=0;i<standardNumber+1;i++)
        {
            if(i==standardNumber)
            {
                z[0][i]=1;
                sortIni[i]=0;
            }
            else {
                z[0][i]=Inipre[StationLocation[i][0]][StationLocation[i][1]];
                sortIni[i]=Inipre[StationLocation[i][0]][StationLocation[i][1]];
            }
        }

        maxIni=sortIni[0];
        minIni=sortIni[0];
        for(int i=0;i<standardNumber+1;i++)
        {
            if(maxIni<=sortIni[i])
                maxIni=sortIni[i];
            if(minIni>=sortIni[i]&&sortIni[i]!=0)
                minIni=sortIni[i];
        }

        Matrix ini=new Matrix(z);//将降雨量存入矩阵
        Matrix krging=ini.times(weight);//加权求和
        double [][]krging_d=krging.getArray();

        //标准化
        maxKIni=krging_d[0][0];
        minKIni=krging_d[0][0];
        for(int i=0;i<nrows*ncolumns;i++)
        {
            if(maxKIni<=krging_d[0][i])
                maxKIni=krging_d[0][i];
            if(minKIni>=krging_d[0][i]&&krging_d[0][i]!=0)
                minKIni=krging_d[0][i];
        }
        double disK=maxKIni-minKIni;
        double dis=maxIni-minIni;
        double [][]kringIni=new double[nrows][ncolumns];

        for(int i=0;i<nrows;i++)
        {
            for(int j=0;j<ncolumns;j++)
            {
                kringIni[i][j]=krging_d[0][i*ncolumns+j];
                kringIni[i][j]=(kringIni[i][j]-minKIni)/disK;
                kringIni[i][j]=kringIni[i][j]*dis+minIni;
            }
        }

        //裁剪
        for(int i=0;i<nrows;i++)
        {
            for(int j=0;j<ncolumns;j++)
            {
                if(DEM.get(i).get(j)==NODATA_VALUE)
                {
                    kringIni[i][j]=0;
                }
            }
        }

        ArrayList<ArrayList<Double>> true_rainfall = new ArrayList<ArrayList<Double>>();// 转为ArrayList
        for (int i = 0; i < nrows; i++) {
            ArrayList<Double> kriging_rainfall_temp = new ArrayList<Double>();
            for (int j = 0; j < ncolumns; j++) {
                kriging_rainfall_temp.add(kringIni[i][j]);
            }
            true_rainfall.add(kriging_rainfall_temp);
        }

        gridRainfall_Kriging=true_rainfall;
        return gridRainfall_Kriging;
    }

    public ArrayList<ArrayList<Double>> calculate_Slope_Aspect(String item) {
        filling_M_V = filling_M_V();
        double Slope_pre[][] = new double[nrows][ncolumns];   //存储坡度结果
        double Aspect_pre[][] = new double[nrows][ncolumns];  //存储坡向结果
        double[][] Dem = new double[nrows][ncolumns];//分别为原始DEM，填洼后DEM
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncolumns; j++) {
                Dem[i][j] = filling_M_V.get(i).get(j);//初始化高程
            }
        }
        double Slope_we = 0;
        double Slope_sn = 0;
        double radiansToDegrees = 180 / Math.PI;       //弧度转换为角度
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncolumns; j++) {
                int judgement = 0;
                //边界上的数据无法计算，直接赋值为-9999
                if (i == 0 || i == nrows - 1 || j == 0 || j == ncolumns - 1) {
                    Slope_pre[i][j] = NODATA_VALUE;
                    Aspect_pre[i][j] = NODATA_VALUE;
                } else {
                    if (Dem[i][j] == NODATA_VALUE) {  //nodata处直接赋值为-9999
                        Slope_pre[i][j] = NODATA_VALUE;
                        Aspect_pre[i][j] = NODATA_VALUE;
                    } else {
                        //存储计算点九宫格内数据
                        double Cal[] = {Dem[i - 1][j - 1], Dem[i - 1][j], Dem[i - 1][j + 1], Dem[i][j - 1], Dem[i][j + 1], Dem[i + 1][j - 1], Dem[i + 1][j], Dem[i + 1][j + 1], Dem[i][j]};
                        //判断九宫格中是否有nodata
                        for (int k = 0; k < 8; k++) {
                            if (Cal[k] == NODATA_VALUE)
                                judgement = 1;
                        }
                        //九宫格内全为高程值的情况
                        if (judgement == 0) {
                            //三阶反距离平方权差分法
                            Slope_we = ((Cal[0] + 2 * Cal[3] + Cal[5]) - (Cal[2] + 2 * Cal[4] + Cal[7])) / (8 * cellsize);
                            Slope_sn = ((Cal[5] + 2 * Cal[6] + Cal[7]) - (Cal[0] + 2 * Cal[1] + Cal[2])) / (8 * cellsize);
                        }
                        //九宫格内存在nodata的情况
                        else {
                            //上下左右都具有高程值的情况
                            if (Cal[1] != NODATA_VALUE && Cal[3] != NODATA_VALUE && Cal[4] != NODATA_VALUE && Cal[6] != NODATA_VALUE) {
                                //二阶差分法
                                Slope_we = (Cal[3] - Cal[4]) / (2 * cellsize);
                                Slope_sn = (Cal[6] - Cal[1]) / (2 * cellsize);
                            }
                            //左下具有高程值的情况
                            else if (Cal[3] != NODATA_VALUE && Cal[6] != NODATA_VALUE) {
                                //简单差分法
                                Slope_we = (Cal[3] - Cal[8]) / cellsize;
                                Slope_sn = (Cal[6] - Cal[8]) / cellsize;
                            }
                            //左上具有高程值的情况
                            else if (Cal[3] != NODATA_VALUE && Cal[1] != NODATA_VALUE) {
                                //简单差分法
                                Slope_we = (Cal[3] - Cal[8]) / cellsize;
                                Slope_sn = (Cal[8] - Cal[1]) / cellsize;
                            }
                            //右下具有高程值的情况
                            else if (Cal[4] != NODATA_VALUE && Cal[6] != NODATA_VALUE) {
                                //简单差分法
                                Slope_we = (Cal[8] - Cal[4]) / cellsize;
                                Slope_sn = (Cal[6] - Cal[8]) / cellsize;
                            }
                            //右上具有高程值的情况
                            else if (Cal[4] != NODATA_VALUE && Cal[1] != NODATA_VALUE) {
                                //简单差分法
                                Slope_we = (Cal[8] - Cal[4]) / cellsize;
                                Slope_sn = (Cal[8] - Cal[1]) / cellsize;
                            }
                            //左右具有高程值的情况
                            else if (Cal[3] != NODATA_VALUE && Cal[4] != NODATA_VALUE) {
                                //二阶差分法
                                Slope_we = (Cal[3] - Cal[4]) / (2 * cellsize);
                            }
                            //上下具有高程值的情况
                            else if (Cal[6] != NODATA_VALUE && Cal[1] != NODATA_VALUE) {
                                //二阶差分法
                                Slope_sn = (Cal[6] - Cal[1]) / (2 * cellsize);
                            }
                            //只有左边具有高程值的情况
                            else if (Cal[3] != NODATA_VALUE) {
                                //简单差分法
                                Slope_we = (Cal[3] - Cal[8]) / cellsize;
                            }
                            //只有下边具有高程值的情况
                            else if (Cal[6] != NODATA_VALUE) {
                                //简单差分法
                                Slope_sn = (Cal[6] - Cal[8]) / cellsize;
                            }
                            //只有右边具有高程值的情况
                            else if (Cal[4] != NODATA_VALUE) {
                                //简单差分法
                                Slope_we = (Cal[8] - Cal[4]) / cellsize;
                            }
                            //只有上边具有高程值的情况
                            else if (Cal[1] != NODATA_VALUE) {
                                //简单差分法
                                Slope_sn = (Cal[8] - Cal[1]) / cellsize;
                            }
                        }
                        //计算整体坡度
                        Slope_pre[i][j] = Math.atan(Math.sqrt(Math.pow(Slope_sn, 2) + Math.pow(Slope_we, 2))) * radiansToDegrees;
                        //计算整体坡向
                        if (Slope_sn == 0 && Slope_we == 0)
                            Aspect_pre[i][j] = -1;
                        else
                            Aspect_pre[i][j] = Math.atan2(Slope_sn, Slope_we) * radiansToDegrees;
                        if (Aspect_pre[i][j] > 90)
                            Aspect_pre[i][j] = 450 - Aspect_pre[i][j];
                        else
                            Aspect_pre[i][j] = 90 - Aspect_pre[i][j];
                    }
                }
            }
        }

        ArrayList<ArrayList<Double>> true_slope = new ArrayList<ArrayList<Double>>();//坡度矩阵初始化
        ArrayList<ArrayList<Double>> true_aspect = new ArrayList<ArrayList<Double>>();//坡向矩阵初始化

        for (int i = 0; i < nrows; i++) {
            ArrayList<Double> filling_M_V_temp = new ArrayList<Double>();
            for (int j = 0; j < ncolumns; j++) {
                filling_M_V_temp.add(Slope_pre[i][j]);
            }
            true_slope.add(filling_M_V_temp);
        }

        for (int i = 0; i < nrows; i++) {
            ArrayList<Double> filling_M_V_temp = new ArrayList<Double>();
            for (int j = 0; j < ncolumns; j++) {
                filling_M_V_temp.add(Aspect_pre[i][j]);
            }
            true_aspect.add(filling_M_V_temp);
        }

        Slope=true_slope;
        Aspect=true_aspect;

        if (item.equals("Slope")) {
            return Slope;
        }
        else if (item.equals("Aspect")){
            return Aspect;
        }
        else{
            System.out.println("Value Error!");
            System.exit(1);
        }
        return Slope;
    }

    public ArrayList<ArrayList<Double>> filling_M_V(){
        double[][][] grid = new double[nrows][ncolumns][2];//分别为原始DEM，填洼后DEM
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncolumns; j++) {
                grid[i][j][0] = DEM.get(i).get(j);//初始化高程
                grid[i][j][1] = NODATA_VALUE;//填洼后高程（初始设为NODATA_value）
            }
        }
        double[][] Z = new double[nrows][ncolumns];
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncolumns; j++) {
                Z[i][j] = grid[i][j][0];
            }
        }
        double[] row_max = new double[nrows];
        for (int i = 0; i < nrows; i++) {
            Arrays.sort(Z[i]);
            row_max[i] = Z[i][ncolumns - 1];
        }
        Arrays.sort(row_max);
        double max_dem = row_max[row_max.length - 1];

        // M_V 1.2 边界网格保持值不变，中间网格赋值为最大DEM+1
        double[][] H = new double[nrows][ncolumns];
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncolumns; j++) {
                //寻找边界网格第一步，DEM最外侧不变
                if (i == 0 || i == nrows - 1 || j == 0 || j == ncolumns - 1) {
                    H[i][j] = grid[i][j][0];
                }
                //寻找网格第二步，如果某个网格为NODATA_value，则值不变
                else if (grid[i][j][0] == NODATA_VALUE)
                {
                    H[i][j] = grid[i][j][0];
                }
                //寻找边界网格第三步（应对非横纵边界），如果某个网格非NODATA_value且 4 邻域中存在NODATA_value网格，则认为该网格是边界
//                else if (grid[i][j + 1][0] == NODATA_VALUE || grid[i - 1][j][0] == NODATA_VALUE
//                        || grid[i + 1][j][0] == NODATA_VALUE || grid[i][j - 1][0] == NODATA_VALUE && grid[i][j][0] != NODATA_VALUE) {
//                    H[i][j] = grid[i][j][0];
                //寻找边界网格第三步（应对非横纵边界），如果某个网格非NODATA_value且 8 邻域中存在NODATA_value网格，则认为该网格是边界
                else if (grid[i - 1][j + 1][0] == NODATA_VALUE || grid[i][j + 1][0] == NODATA_VALUE || grid[i + 1][j + 1][0] == NODATA_VALUE
                        || grid[i - 1][j][0] == NODATA_VALUE || grid[i + 1][j][0] == NODATA_VALUE || grid[i - 1][j - 1][0] == NODATA_VALUE
                        || grid[i][j - 1][0] == NODATA_VALUE || grid[i + 1][j - 1][0] == NODATA_VALUE && grid[i][j][0] != NODATA_VALUE) {
                    H[i][j] = grid[i][j][0];
                } else {
                    H[i][j] = max_dem + 1;
                }

            }
        }
        //第一次遍历
        // M_V 2 以 3×3 窗口逐行对所有网格（边界除外）进行遍历。以网格 C 为中间网格，遍历其 8 邻域 ：
        supportFunction.filling_Iterate(nrows, ncolumns, NODATA_VALUE, H, grid);

        double[][] H0 = new double[nrows][ncolumns];
        int count = -1;
        while (count != 0) {
            count = 0;
            // 复制遍历后数组
            for (int i = 0; i < nrows; i++) {
                for (int j = 0; j < ncolumns; j++) {
                    H0[i][j] = H[i][j];
                }
            }
            // 第二次遍历数组，比较两个数组，如果不完全相同则再次遍历、比较
            supportFunction.filling_Iterate(nrows, ncolumns, NODATA_VALUE, H, grid);
            for (int i = 0; i < nrows; i++) {
                for (int j = 0; j < ncolumns; j++) {
                    if (H[i][j] != H0[i][j]) {
                        count += 1;
                    }
                }
            }
        }

        ArrayList<ArrayList<Double>> true_filling = new ArrayList<ArrayList<Double>>();//补全高程矩阵初始化

        for(int i=0;i<nrows;i++){
            ArrayList<Double> filling_M_V_temp=new ArrayList<Double>();
            for(int j=0;j<ncolumns;j++){
                filling_M_V_temp.add(H[i][j]);
            }
            true_filling.add(filling_M_V_temp);
        }

        filling_M_V=true_filling;
        return filling_M_V;
    }

    public ArrayList<ArrayList<Integer>> flowCalculator(){
        filling_M_V = filling_M_V();
        // 流向计算函数
        ArrayList<ArrayList<Double>> heightForFlow=new ArrayList<ArrayList<Double>>();//补全高程矩阵初始化
        for(int i=0;i<nrows+2;i++){
            // 本步循环操作为在传入的高程数组外补NoData方便八邻域的流向搜索
            ArrayList<Double> heighttemp=new ArrayList<Double>();
            for(int j=0;j<ncolumns+2;j++){
                if((i==0)||i==(nrows+1)||(j==0)||(j==ncolumns+1)){//判断条件
                    heighttemp.add((double)NODATA_VALUE);
                }
                else{
//                    heighttemp.add(DEM.get(i-1).get(j-1));
                    heighttemp.add(filling_M_V.get(i-1).get(j-1));
                }
            }
            heightForFlow.add(heighttemp);
        }

        ArrayList<ArrayList<Integer>> true_flow=new ArrayList<ArrayList<Integer>>();

        for(int i=0;i<nrows;i++){
            //将补NoData后的高程数组计算流向
            ArrayList<Integer> flowtemp=new ArrayList<Integer>();
            for(int j=0;j<ncolumns;j++){
                if(heightForFlow.get(i+1).get(j+1)==NODATA_VALUE){
                    flowtemp.add(NODATA_VALUE);
                }else {
                    flowtemp.add(supportFunction.findFlow(heightForFlow, i + 1, j + 1));//计算流向
                }
            }
            true_flow.add(flowtemp);
        }
        flow = true_flow;
        return flow;// 返回流向矩阵
    }

    public ArrayList<ArrayList<Integer>> acc_flowCalculator(){
        flow=flowCalculator();
        ArrayList<ArrayList<Integer>> flowForAcc_flow=new ArrayList<ArrayList<Integer>>();
        for(int i=0;i<nrows+2;i++){
            // 本步循环操作为在传入的流向数组外补NoData方便累积流计算
            ArrayList<Integer> flowForAcc_flow_temp=new ArrayList<Integer>();
            for(int j=0;j<ncolumns+2;j++){
                if((i==0)||i==(nrows+1)||(j==0)||(j==ncolumns+1)){//判断条件
                    flowForAcc_flow_temp.add(NODATA_VALUE);
                }
                else{
                    flowForAcc_flow_temp.add(flow.get(i-1).get(j-1));
                }
            }
            flowForAcc_flow.add(flowForAcc_flow_temp);
        }

        // 累积流计算辅助类
        supportIterForAcc_flow sIFA=new supportIterForAcc_flow(acc_flow.size(),acc_flow.get(0).size());
        //sIFA线程
        sIFA.start();

        for(int i=0;i<nrows;i++){
            //将补NoData后的流向数组计算初始辅助数组
            for(int j=0;j<ncolumns;j++){
                sIFA.acc_flow_sup.get(i).set(j, supportFunction.calculateAcc_flow_1(flowForAcc_flow, i + 1, j + 1));//计算辅助数组
            }
        }

        // 累积流计算
        for(int i=0;i<nrows;i++){
            for(int j=0;j<ncolumns;j++){
                if(sIFA.acc_flow_sup.get(i).get(j)==0){
                    int n=0;
                    sIFA= supportFunction.calculateAcc_flow_2(i,j,n,sIFA,flow);
                }
            }
        }
        ArrayList<ArrayList<Integer>> true_accflow=new ArrayList<ArrayList<Integer>>();

        true_accflow=sIFA.acc_flow;

        //处理NODATA_VALUE
        for(int i=0;i<nrows;i++){
            for(int j=0;j<ncolumns;j++){
                if(flow.get(i).get(j)==NODATA_VALUE){
                    true_accflow.get(i).set(j,NODATA_VALUE);
                }
            }
        }
        acc_flow=true_accflow;
        return acc_flow;// 返回累积流矩阵
    }

    public ArrayList<ArrayList<Integer>> waterzoneCalculator(double value) {
        acc_flow=acc_flowCalculator();
        ArrayList<ArrayList<Integer>> true_waterzone=new ArrayList<ArrayList<Integer>>();
        for(int i=0;i<nrows;i++){
            ArrayList<Integer> waterzone_temp=new ArrayList<Integer>();
            for(int j=0;j<ncolumns;j++){
                if(acc_flow.get(i).get(j)==NODATA_VALUE){//判断条件
                    waterzone_temp.add(NODATA_VALUE);
                }
                else if(acc_flow.get(i).get(j)>=value){
                    waterzone_temp.add(1);
                }
                else if(acc_flow.get(i).get(j)<value){
                    waterzone_temp.add(0);
                }
            }
            true_waterzone.add(waterzone_temp);
        }
        water_zone=true_waterzone;
        return water_zone;
    }

    public ArrayList<ArrayList<Integer>> ridgelineCalculator() {
        acc_flow=acc_flowCalculator();
        ArrayList<ArrayList<Integer>> true_ridgeline=new ArrayList<ArrayList<Integer>>();
        for(int i=0;i<nrows;i++){
            ArrayList<Integer> ridgeline_temp=new ArrayList<Integer>();
            for(int j=0;j<ncolumns;j++){
                if(acc_flow.get(i).get(j)==NODATA_VALUE){//判断条件
                    ridgeline_temp.add(NODATA_VALUE);
                }
                else if(acc_flow.get(i).get(j)==0){
                    ridgeline_temp.add(1);
                }
                else{
                    ridgeline_temp.add(0);
                }
            }
            true_ridgeline.add(ridgeline_temp);
        }
        ridge_line=true_ridgeline;
        return ridge_line;
    }
}
