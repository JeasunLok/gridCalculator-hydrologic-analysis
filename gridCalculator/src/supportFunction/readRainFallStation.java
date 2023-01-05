package supportFunction;
import java.io.*;
import java.util.ArrayList;
import java.sql.*;

public class readRainFallStation extends Thread{
    //读取降雨站以及雨量数据类
    public int standardNumber;
    public ArrayList<Integer> standardID;
    public ArrayList<String> NAME;
    public ArrayList<String> NAME_rain;
    public ArrayList<String> SID;
    public ArrayList<Double> standardX;
    public ArrayList<Double> standardY;
    public ArrayList<String> enname;
    public ArrayList<Double> standardRainfall;

    public void run(){
        System.out.println("readRainFallStation线程:"+getName());
    }
    public readRainFallStation(String fileName_StationProperty, String fileName_rain) throws Exception {
        // 主要内容是逐行读取
        // String fileName_StationProperty ="src/StationProperty.txt";
        // String fileName_rain ="src/rain.txt";
        FileReader fileReader_StationProperty = new FileReader(fileName_StationProperty);
        BufferedReader bufferedReader_StationProperty = new BufferedReader(fileReader_StationProperty);
        String line_StationProperty = bufferedReader_StationProperty.readLine();

        ArrayList<ArrayList<String>> file = new ArrayList<ArrayList<String>>();
        int rows_StationProperty = 0;
        standardID=new ArrayList<Integer>();
        NAME=new ArrayList<String>();
        SID=new ArrayList<String>();
        standardX=new ArrayList<Double>();
        standardY=new ArrayList<Double>();
        enname=new ArrayList<String>();

        // 数据库建表
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grid", "root", "12345678");
        Statement stat=conn.createStatement();
        stat.executeUpdate("drop table if exists stationProperty");
        stat.executeUpdate("create table if not exists stationProperty(ID int(3) not null," +
                "NAME varchar(10),SID varchar(10),X double,Y double,enname varchar(20))");

        stat.executeUpdate("drop table if exists rain");
        stat.executeUpdate("create table if not exists rain(NAME varchar(10) not null,rainfall double)");
        stat.close();

        String sql_station="insert into stationProperty (ID,NAME,SID,X,Y,enname) values(?,?,?,?,?,?)";
        String sql_rain="insert into rain (NAME,rainfall) values(?,?)";

        PreparedStatement prestat_station=conn.prepareStatement(sql_station);
        PreparedStatement prestat_rain=conn.prepareStatement(sql_rain);

        while (line_StationProperty != null) {
            ArrayList<String> tempfile = new ArrayList<String>();
            String[] s = line_StationProperty.split("\\t");
            for (int i = 0; i < s.length; i++) {
                tempfile.add(s[i]);
            }
            file.add(tempfile);
            line_StationProperty = bufferedReader_StationProperty.readLine();
            if (rows_StationProperty == 0) {
                standardNumber = Integer.parseInt(file.get(rows_StationProperty).get(0));
            }
            else if(rows_StationProperty>1) {
                standardID.add(Integer.parseInt(file.get(rows_StationProperty).get(0)));
                NAME.add(file.get(rows_StationProperty).get(1));
                SID.add(file.get(rows_StationProperty).get(2));
                standardX.add(Double.parseDouble(file.get(rows_StationProperty).get(3)));
                standardY.add(Double.parseDouble(file.get(rows_StationProperty).get(4)));
                enname.add(file.get(rows_StationProperty).get(5));

                prestat_station.setInt(1,Integer.parseInt(file.get(rows_StationProperty).get(0)));
                prestat_station.setString(2,file.get(rows_StationProperty).get(1));
                prestat_station.setString(3,file.get(rows_StationProperty).get(2));
                prestat_station.setDouble(4,Double.parseDouble(file.get(rows_StationProperty).get(3)));
                prestat_station.setDouble(5,Double.parseDouble(file.get(rows_StationProperty).get(4)));
                prestat_station.setString(6,file.get(rows_StationProperty).get(5));
                prestat_station.executeUpdate();
            }
            rows_StationProperty++;
        }
//        bufferedReader_StationProperty.close();
//        fileReader_StationProperty.close();

        FileReader fileReader_rain = new FileReader(fileName_rain);
        BufferedReader bufferedReader_rain = new BufferedReader(fileReader_rain);
        String line_rain = bufferedReader_rain.readLine();

        ArrayList<ArrayList<String>> file_rain = new ArrayList<ArrayList<String>>();
        int rows_rain = 0;
        NAME_rain=new ArrayList<String>();
        // 下面的读取那么复杂是因为想要处理降雨站不按顺序或没有该降雨站的雨量的异常情况
        while (line_rain != null) {
            ArrayList<String> tempfile = new ArrayList<String>();
            String[] s = line_rain.split("\\t");
            for (int i = 0; i < s.length; i++) {
                tempfile.add(s[i]);
            }
            file_rain.add(tempfile);
            line_rain= bufferedReader_rain.readLine();
            if (rows_rain == 1) {
                for (int i = 2; i < s.length; i++) {
                    NAME_rain.add(file_rain.get(rows_rain).get(i));
                }
            }
            rows_rain++;
        }

        ArrayList<Integer> standardIndex = new ArrayList<Integer>(standardNumber);
        for (int i = 0; i < standardNumber; i++) {
            for (int j = 0; j < NAME_rain.size(); j++) {
                if (NAME.get(i).equals(NAME_rain.get(j))) {
                    standardIndex.add(j+2);
                    break;
                }
                if(j==NAME_rain.size()-1){
                    standardIndex.add(-1);
                }
            }
        }

        standardRainfall = new ArrayList<Double>();
        for (int i = 0; i < standardNumber; i++) {
            standardRainfall.add((double) 0);
        }
        for (int i = 0; i < standardNumber; i++) {
            if (standardIndex.get(i) != -1) {
                for (int j = 2; j < rows_rain; j++) {
                    standardRainfall.set(i, standardRainfall.get(i) + Double.parseDouble(file_rain.get(j).get(standardIndex.get(i))));
                }
            }
            prestat_rain.setString(1,NAME.get(i));
            prestat_rain.setDouble(2,standardRainfall.get(i));
            prestat_rain.executeUpdate();
        }

        bufferedReader_StationProperty.close();
        fileReader_StationProperty.close();
    }
}
