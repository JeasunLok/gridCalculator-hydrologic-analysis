package supportFunction;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.sql.*;

public class readDEM extends Thread {
    //读取DEM类
    public int ncols;
    public int nrows;
    public int cellsize;
    public int NODATA_VALUE;
    public double xllcorner;
    public double yllcorner;
    public ArrayList<ArrayList<Double>> DEM;
    public String z;

    public void run(){
        System.out.println("readDEM线程:"+getName());
    }
    public readDEM(String DEMpath) throws Exception{
        // 创建基础信息表
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grid", "root", "12345678");
        Statement stat=conn.createStatement();
        stat.executeUpdate("drop table if exists field");
        stat.executeUpdate("create table if not exists field(property varchar(20) not null,property_value double not null)");
        stat.close();
        String sql="insert into field (property,property_value) values(?,?)";
        PreparedStatement prestat=conn.prepareStatement(sql);
        // 读取文件
        File demFile = new File(DEMpath);
        try {
            Scanner inDEM = new Scanner(demFile);
            z = inDEM.next();
            ncols = inDEM.nextInt();
            prestat.setString(1,"ncols");
            prestat.setDouble(2,ncols);
            prestat.executeUpdate();
            z = inDEM.next();
            nrows = inDEM.nextInt();
            prestat.setString(1,"nrows");
            prestat.setDouble(2,nrows);
            prestat.executeUpdate();
            z = inDEM.next();
            xllcorner = inDEM.nextDouble();
            prestat.setString(1,"xllcorner");
            prestat.setDouble(2,xllcorner);
            prestat.executeUpdate();
            z = inDEM.next();
            yllcorner = inDEM.nextDouble();
            prestat.setString(1,"yllcorner");
            prestat.setDouble(2,yllcorner);
            prestat.executeUpdate();
            z = inDEM.next();
            cellsize = inDEM.nextInt();
            prestat.setString(1,"cellsize");
            prestat.setDouble(2,cellsize);
            prestat.executeUpdate();
            z = inDEM.next();
            NODATA_VALUE = inDEM.nextInt();
            prestat.setString(1,"NODATA_VALUE");
            prestat.setDouble(2,NODATA_VALUE);
            prestat.executeUpdate();

            DEM = new ArrayList<ArrayList<Double>>();
            for (int i = 0; i < nrows; i++) {
                ArrayList<Double> tempDEM = new ArrayList<Double>();
                for (int j = 0; j < ncols; j++) {
                    tempDEM.add(inDEM.nextDouble());
                }
                DEM.add(tempDEM);
            }
            inDEM.close();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }
}
