import gridCalculator.gridCalculator;
import gridDatabase.*;
import supportFunction.readDEM;
import supportFunction.readRainFallStation;
import supportFunction.writeASC;
import supportFunction.supportFunction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class gridCalculator_GUI {
    private JPanel mainJPanel;
    public static void main(String[] args) throws Exception {
        final JFrame jf = new JFrame("网格水文分析计算器");
        jf.setSize(800, 700);
        jf.setResizable(false);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JMenuBar menuBar=showMenu(jf);
        menuBar.setPreferredSize(new Dimension(800, 30));
        // 菜单栏设置
        jf.setJMenuBar(menuBar);

        final String[] inputPath = new String[3];
        final String[] outputPath = new String[9];
        final String[] DB_Info = new String[4];
        final boolean[] Selection = new boolean[2];
        final String[] show_output_Path= new String[1];
        final gridCalculator[] gC = new gridCalculator[1];

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        final JTextArea msgTextArea_DEM = new JTextArea("请选择高程asc文件路径...",1,20);
        msgTextArea_DEM.setBounds(10,10,300,25);
        msgTextArea_DEM.setLineWrap(false);
        msgTextArea_DEM.setEnabled(false);
        mainPanel.add(msgTextArea_DEM);

        JButton openBtn_DEM = new JButton("打开高程asc文件...");
        openBtn_DEM.setBounds(320,10,145,25);
        openBtn_DEM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputPath[0]=showFileOpenDialog(jf, msgTextArea_DEM);
            }
        });
        mainPanel.add(openBtn_DEM);

        final JTextArea msgTextArea_RainFall = new JTextArea("请选择雨量站txt文件路径...",1,20);
        msgTextArea_RainFall.setBounds(10,53,300,25);
        msgTextArea_RainFall.setLineWrap(false);
        msgTextArea_RainFall.setEnabled(false);
        mainPanel.add(msgTextArea_RainFall);

        JButton openBtn_RainFall= new JButton("打开雨量站txt文件...");
        openBtn_RainFall.setBounds(320,53,145,25);
        openBtn_RainFall.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputPath[1]=showFileOpenDialog(jf, msgTextArea_RainFall);
            }
        });
        mainPanel.add(openBtn_RainFall);

        final JTextArea msgTextArea_RainStation = new JTextArea("请选择降雨量txt文件路径...",1,20);
        msgTextArea_RainStation.setBounds(10,95,300,25);
        msgTextArea_RainStation.setLineWrap(false);
        msgTextArea_RainStation.setEnabled(false);
        mainPanel.add(msgTextArea_RainStation);

        JButton openBtn_RainStation = new JButton("打开降雨量txt文件...");
        openBtn_RainStation.setBounds(320,95,145,25);
        openBtn_RainStation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputPath[2]=showFileOpenDialog(jf, msgTextArea_RainStation);
            }
        });
        mainPanel.add(openBtn_RainStation);

        JLabel label_DBURL = new JLabel("MySQL URL:");
        label_DBURL.setBounds(480,9,100,20);
        mainPanel.add(label_DBURL);

        JLabel label_DBname = new JLabel("Database name:");
        label_DBname.setBounds(480,39,100,20);
        mainPanel.add(label_DBname);

        JLabel label_DBuser = new JLabel("Database user:");
        label_DBuser.setBounds(480,69,100,20);
        mainPanel.add(label_DBuser);

        JLabel label_DBpassword = new JLabel("Database user password:");
        label_DBpassword.setBounds(480,99,155,20);
        mainPanel.add(label_DBpassword);

        final JTextField textField_DBURL = new JTextField(30);
        textField_DBURL.setBounds(560,11,215,20);
        mainPanel.add(textField_DBURL);

        final JTextField textField_DBname = new JTextField(30);
        textField_DBname.setBounds(580,41,120,20);
        mainPanel.add(textField_DBname);

        final JTextField textField_DBuser = new JTextField(30);
        textField_DBuser.setBounds(575,71,125,20);
        mainPanel.add(textField_DBuser);

        final JPasswordField DBpasswordField = new JPasswordField(30);
        DBpasswordField.setBounds(635,101,140,20);
        mainPanel.add(DBpasswordField);

        JButton summitBtn_DB = new JButton("运行");
        summitBtn_DB.setBounds(705,43,69,45);
        mainPanel.add(summitBtn_DB);
        summitBtn_DB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DB_Info[0]=textField_DBURL.getText();
                DB_Info[1]=textField_DBname.getText();
                DB_Info[2]=textField_DBuser.getText();
                DB_Info[3]=String.valueOf(DBpasswordField.getPassword());
                try {
                    gridDatabase.createDatabase(DB_Info[0], DB_Info[1], DB_Info[2], DB_Info[3]);
                    readRainFallStation rRFS = new readRainFallStation(inputPath[1],inputPath[2]);
                    readDEM rDEM =new readDEM(inputPath[0]);
                    int NODATA_VALUE=rDEM.NODATA_VALUE;
                    int nrows=rDEM.nrows;
                    int ncolumns=rDEM.ncols;
                    int cellsize=rDEM.cellsize;
                    int standardNumber= rRFS.standardNumber;
                    ArrayList<Double> cellcorner=new ArrayList<Double>();
                    cellcorner.add(rDEM.xllcorner);
                    cellcorner.add(rDEM.yllcorner);
                    ArrayList<ArrayList<Double>> DEM=rDEM.DEM;
                    ArrayList<Double> standardX=rRFS.standardX;
                    ArrayList<Double> standardY=rRFS.standardY;
                    ArrayList<Double> standardRainfall=rRFS.standardRainfall;
                    gC[0] = new gridCalculator(NODATA_VALUE,nrows,ncolumns,cellsize,
                            cellcorner,DEM,standardNumber,standardX,standardY,standardRainfall);
                    if(Selection[0]){
                        gridDatabase.createTable(DB_Info[0], DB_Info[1], DB_Info[2], DB_Info[3], ncolumns);
                    }
                    showDBSuccessDialog(jf, jf);
                } catch (Exception ex) {
                    showDBErrorDialog(jf, jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton funcBtn_IDW = new JButton("计算IDW降雨插值");
        funcBtn_IDW.setBounds(10,135,145,30);
        mainPanel.add(funcBtn_IDW);
        funcBtn_IDW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    showCalculateINGSuccessDialog(jf,jf);
                    ArrayList<ArrayList<Double>> temp=gC[0].VoronoiDWithDistance_ForRainfall();
                    showCalculateFinishedSuccessDialog(jf,jf);
                }catch (Exception ex){
                    showCalculateFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton funcBtn_Kriging = new JButton("计算克里金降雨插值");
        funcBtn_Kriging.setBounds(10,185,145,30);
        mainPanel.add(funcBtn_Kriging);
        funcBtn_Kriging .addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    double[] KrigingParams=inputParamsKrigingDialog(jf,jf);
                    showCalculateINGSuccessDialog(jf,jf);
                    ArrayList<ArrayList<Double>> temp=gC[0].Kriging_ForRainfall(KrigingParams[0],KrigingParams[1],KrigingParams[2]);
                    showCalculateFinishedSuccessDialog(jf,jf);
                }catch (Exception ex){
                    showCalculateFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton funcBtn_Filling = new JButton("计算DEM填洼");
        funcBtn_Filling.setBounds(10,235,145,30);
        mainPanel.add(funcBtn_Filling);
        funcBtn_Filling.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    showCalculateINGSuccessDialog(jf,jf);
                    ArrayList<ArrayList<Double>> temp=gC[0].filling_M_V();
                    showCalculateFinishedSuccessDialog(jf,jf);
                }catch (Exception ex){
                    showCalculateFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton funcBtn_Slope = new JButton("计算DEM坡度");
        funcBtn_Slope.setBounds(10,285,145,30);
        mainPanel.add(funcBtn_Slope);
        funcBtn_Slope.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    showCalculateINGSuccessDialog(jf,jf);
                    ArrayList<ArrayList<Double>> temp=gC[0].calculate_Slope_Aspect("Slope");
                    showCalculateFinishedSuccessDialog(jf,jf);
                }catch (Exception ex){
                    showCalculateFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton funcBtn_Aspect = new JButton("计算DEM坡向");
        funcBtn_Aspect.setBounds(10,335,145,30);
        mainPanel.add(funcBtn_Aspect);
        funcBtn_Aspect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    showCalculateINGSuccessDialog(jf,jf);
                    ArrayList<ArrayList<Double>> temp=gC[0].calculate_Slope_Aspect("Aspect");
                    showCalculateFinishedSuccessDialog(jf,jf);
                }catch (Exception ex){
                    showCalculateFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton funcBtn_Flow = new JButton("计算DEM流向");
        funcBtn_Flow.setBounds(10,385,145,30);
        mainPanel.add(funcBtn_Flow);
        funcBtn_Flow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    showCalculateINGSuccessDialog(jf,jf);
                    ArrayList<ArrayList<Integer>> temp=gC[0].flowCalculator();
                    showCalculateFinishedSuccessDialog(jf,jf);
                }catch (Exception ex){
                    showCalculateFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton funcBtn_AccFlow = new JButton("计算DEM累积流");
        funcBtn_AccFlow.setBounds(10,435,145,30);
        mainPanel.add(funcBtn_AccFlow);
        funcBtn_AccFlow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    showCalculateINGSuccessDialog(jf,jf);
                    ArrayList<ArrayList<Integer>> temp=gC[0].acc_flowCalculator();
                    showCalculateFinishedSuccessDialog(jf,jf);
                }catch (Exception ex){
                    showCalculateFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton funcBtn_WaterZone = new JButton("计算DEM河网");
        funcBtn_WaterZone.setBounds(10,485,145,30);
        mainPanel.add(funcBtn_WaterZone);
        funcBtn_WaterZone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    double WaterZoneParams=inputParamsWaterZoneDialog(jf,jf);
                    showCalculateINGSuccessDialog(jf,jf);
                    ArrayList<ArrayList<Integer>> temp=gC[0].waterzoneCalculator(WaterZoneParams);
                    showCalculateFinishedSuccessDialog(jf,jf);
                }catch (Exception ex){
                    showCalculateFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton funcBtn_RidgeLine = new JButton("计算DEM山脊线");
        funcBtn_RidgeLine.setBounds(10,535,145,30);
        mainPanel.add(funcBtn_RidgeLine);
        funcBtn_RidgeLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    showCalculateINGSuccessDialog(jf,jf);
                    ArrayList<ArrayList<Integer>> temp=gC[0].ridgelineCalculator();
                    showCalculateFinishedSuccessDialog(jf,jf);
                }catch (Exception ex){
                    showCalculateFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton funcBtn_All = new JButton("计算全部");
        funcBtn_All.setBounds(10,585,100,30);
        mainPanel.add(funcBtn_All);
        funcBtn_All.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    double[] KrigingParams=inputParamsKrigingDialog(jf,jf);
                    double WaterZoneParams=inputParamsWaterZoneDialog(jf,jf);
                    showCalculateINGSuccessDialog(jf,jf);
                    ArrayList<ArrayList<Double>> temp1=gC[0].VoronoiDWithDistance_ForRainfall();
                    ArrayList<ArrayList<Double>> temp2=gC[0].Kriging_ForRainfall(KrigingParams[0],KrigingParams[1],KrigingParams[2]);
                    ArrayList<ArrayList<Double>> temp3=gC[0].filling_M_V();
                    ArrayList<ArrayList<Double>> temp4=gC[0].calculate_Slope_Aspect("Slope");
                    ArrayList<ArrayList<Double>> temp5=gC[0].calculate_Slope_Aspect("Aspect");
                    ArrayList<ArrayList<Integer>> temp6=gC[0].flowCalculator();
                    ArrayList<ArrayList<Integer>> temp7=gC[0].acc_flowCalculator();
                    ArrayList<ArrayList<Integer>> temp8=gC[0].waterzoneCalculator(WaterZoneParams);
                    ArrayList<ArrayList<Integer>> temp9=gC[0].ridgelineCalculator();
                    showCalculateFinishedSuccessDialog(jf,jf);
                }catch (Exception ex){
                    showCalculateFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton saveBtn_IDW = new JButton("保存IDW降雨插值结果");
        saveBtn_IDW.setBounds(165,135,170,30);
        mainPanel.add(saveBtn_IDW);
        saveBtn_IDW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    outputPath[0]=showFileSaveDialog(jf,"Single");
                    if(!outputPath[0].equals("No file")){
                        writeASC write_rfi_IDW=new writeASC(outputPath[0],gC[0].getnrows(),gC[0].getncolumns(),gC[0].getcellcorner().get(0),
                                gC[0].getcellcorner().get(1),gC[0].getcellsize(),gC[0].getNODATA_VALUE(),gC[0].getgridRainfall_IDW(),0);
                        showSaveSuccessDialog(jf,jf);
                    }
                }catch (Exception ex){
                    showSaveFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton saveBtn_Kriging = new JButton("保存克里金降雨插值结果");
        saveBtn_Kriging.setBounds(165,185,170,30);
        mainPanel.add(saveBtn_Kriging);
        saveBtn_Kriging.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    outputPath[1]=showFileSaveDialog(jf,"Single");
                    if(!outputPath[1].equals("No file")) {
                        writeASC write_rfi_Kriging = new writeASC(outputPath[1], gC[0].getnrows(), gC[0].getncolumns(), gC[0].getcellcorner().get(0),
                                gC[0].getcellcorner().get(1), gC[0].getcellsize(), gC[0].getNODATA_VALUE(), gC[0].getgridRainfall_Kriging(), 0);
                        showSaveSuccessDialog(jf, jf);
                    }
                }catch (Exception ex){
                    showSaveFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton saveBtn_Filling = new JButton("保存DEM填洼结果");
        saveBtn_Filling.setBounds(165,235,170,30);
        mainPanel.add(saveBtn_Filling);
        saveBtn_Filling.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    outputPath[2] = showFileSaveDialog(jf, "Single");
                    if(!outputPath[2].equals("No file")){
                        writeASC write_filling_DEM = new writeASC(outputPath[2], gC[0].getnrows(), gC[0].getncolumns(), gC[0].getcellcorner().get(0),
                                gC[0].getcellcorner().get(1), gC[0].getcellsize(), gC[0].getNODATA_VALUE(), gC[0].getFilling_M_V(), 0);
                    }
                    showSaveSuccessDialog(jf,jf);
                }catch (Exception ex){
                    showSaveFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton saveBtn_Slope = new JButton("保存DEM坡度结果");
        saveBtn_Slope.setBounds(165,285,170,30);
        mainPanel.add(saveBtn_Slope);
        saveBtn_Slope.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    outputPath[3]=showFileSaveDialog(jf,"Single");
                    if(!outputPath[3].equals("No file")) {
                        writeASC write_slope = new writeASC(outputPath[3], gC[0].getnrows(), gC[0].getncolumns(), gC[0].getcellcorner().get(0),
                                gC[0].getcellcorner().get(1), gC[0].getcellsize(), gC[0].getNODATA_VALUE(), gC[0].getSlope(), 0);
                        showSaveSuccessDialog(jf, jf);
                    }
                }catch (Exception ex){
                    showSaveFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton saveBtn_Aspect = new JButton("保存DEM坡向结果");
        saveBtn_Aspect.setBounds(165,335,170,30);
        mainPanel.add(saveBtn_Aspect);
        saveBtn_Aspect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    outputPath[4]=showFileSaveDialog(jf,"Single");
                    if(!outputPath[4].equals("No file")) {
                        writeASC write_aspect = new writeASC(outputPath[4], gC[0].getnrows(), gC[0].getncolumns(), gC[0].getcellcorner().get(0),
                                gC[0].getcellcorner().get(1), gC[0].getcellsize(), gC[0].getNODATA_VALUE(), gC[0].getAspect(), 0);
                        showSaveSuccessDialog(jf, jf);
                    }
                }catch (Exception ex){
                    showSaveFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton saveBtn_Flow = new JButton("保存DEM流向结果");
        saveBtn_Flow.setBounds(165,385,170,30);
        mainPanel.add(saveBtn_Flow);
        saveBtn_Flow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    outputPath[5]=showFileSaveDialog(jf,"Single");
                    if(!outputPath[5].equals("No file")) {
                        writeASC write_aspect = new writeASC(outputPath[5], gC[0].getnrows(), gC[0].getncolumns(), gC[0].getcellcorner().get(0),
                                gC[0].getcellcorner().get(1), gC[0].getcellsize(), gC[0].getNODATA_VALUE(), gC[0].getFlow(), 0);
                        showSaveSuccessDialog(jf, jf);
                    }
                }catch (Exception ex){
                    showSaveFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton saveBtn_AccFlow = new JButton("保存DEM累积流结果");
        saveBtn_AccFlow.setBounds(165,435,170,30);
        mainPanel.add(saveBtn_AccFlow);
        saveBtn_AccFlow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    outputPath[6]=showFileSaveDialog(jf,"Single");
                    if(!outputPath[6].equals("No file")) {
                        writeASC write_aspect = new writeASC(outputPath[6], gC[0].getnrows(), gC[0].getncolumns(), gC[0].getcellcorner().get(0),
                                gC[0].getcellcorner().get(1), gC[0].getcellsize(), gC[0].getNODATA_VALUE(), gC[0].getAcc_flow(), 0);
                        showSaveSuccessDialog(jf, jf);
                    }
                }catch (Exception ex){
                    showSaveFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton saveBtn_WaterZone = new JButton("保存DEM河网结果");
        saveBtn_WaterZone.setBounds(165,485,170,30);
        mainPanel.add(saveBtn_WaterZone);
        saveBtn_WaterZone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    outputPath[7]=showFileSaveDialog(jf,"Single");
                    if(!outputPath[7].equals("No file")) {
                        writeASC write_aspect = new writeASC(outputPath[7], gC[0].getnrows(), gC[0].getncolumns(), gC[0].getcellcorner().get(0),
                                gC[0].getcellcorner().get(1), gC[0].getcellsize(), gC[0].getNODATA_VALUE(), gC[0].getWater_zone(), 0);
                        showSaveSuccessDialog(jf, jf);
                    }
                }catch (Exception ex){
                    showSaveFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton saveBtn_RidgeLine = new JButton("保存DEM山脊线结果");
        saveBtn_RidgeLine.setBounds(165,535,170,30);
        mainPanel.add(saveBtn_RidgeLine);
        saveBtn_RidgeLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    outputPath[8]=showFileSaveDialog(jf,"Single");
                    if(!outputPath[8].equals("No file")) {
                        writeASC write_aspect = new writeASC(outputPath[8], gC[0].getnrows(), gC[0].getncolumns(), gC[0].getcellcorner().get(0),
                                gC[0].getcellcorner().get(1), gC[0].getcellsize(), gC[0].getNODATA_VALUE(), gC[0].getRidge_line(), 0);
                        showSaveSuccessDialog(jf, jf);
                    }
                }catch (Exception ex){
                    showSaveFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton saveBtn_All = new JButton("保存全部");
        saveBtn_All.setBounds(115,585,100,30);
        mainPanel.add(saveBtn_All);
        saveBtn_All.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String outputPath=showFileSaveDialog(jf,"All");
                    if(!outputPath.equals("No file")) {
                        writeASC write_rfi_IDW = new writeASC(outputPath + "\\IDW.asc", gC[0].getnrows(), gC[0].getncolumns(), gC[0].getcellcorner().get(0),
                                gC[0].getcellcorner().get(1), gC[0].getcellsize(), gC[0].getNODATA_VALUE(), gC[0].getgridRainfall_IDW(), 0);
                        writeASC write_rfi_Kriging = new writeASC(outputPath + "\\Kriging.asc", gC[0].getnrows(), gC[0].getncolumns(), gC[0].getcellcorner().get(0),
                                gC[0].getcellcorner().get(1), gC[0].getcellsize(), gC[0].getNODATA_VALUE(), gC[0].getgridRainfall_Kriging(), 0);
                        writeASC write_filling_DEM = new writeASC(outputPath + "\\Filling_DEM.asc", gC[0].getnrows(), gC[0].getncolumns(), gC[0].getcellcorner().get(0),
                                gC[0].getcellcorner().get(1), gC[0].getcellsize(), gC[0].getNODATA_VALUE(), gC[0].filling_M_V(), 0);
                        writeASC write_slope = new writeASC(outputPath + "\\Slope.asc", gC[0].getnrows(), gC[0].getncolumns(), gC[0].getcellcorner().get(0),
                                gC[0].getcellcorner().get(1), gC[0].getcellsize(), gC[0].getNODATA_VALUE(), gC[0].getSlope(), 0);
                        writeASC write_aspect = new writeASC(outputPath + "\\Aspect.asc", gC[0].getnrows(), gC[0].getncolumns(), gC[0].getcellcorner().get(0),
                                gC[0].getcellcorner().get(1), gC[0].getcellsize(), gC[0].getNODATA_VALUE(), gC[0].getAspect(), 0);
                        writeASC write_flow = new writeASC(outputPath + "\\Flow.asc", gC[0].getnrows(), gC[0].getncolumns(), gC[0].getcellcorner().get(0),
                                gC[0].getcellcorner().get(1), gC[0].getcellsize(), gC[0].getNODATA_VALUE(), gC[0].getFlow(), 0);
                        writeASC write_acc_flow = new writeASC(outputPath + "\\Acc_Flow.asc", gC[0].getnrows(), gC[0].getncolumns(), gC[0].getcellcorner().get(0),
                                gC[0].getcellcorner().get(1), gC[0].getcellsize(), gC[0].getNODATA_VALUE(), gC[0].getAcc_flow(), 0);
                        writeASC write_water_zone = new writeASC(outputPath + "\\Water_Zone.asc", gC[0].getnrows(), gC[0].getncolumns(), gC[0].getcellcorner().get(0),
                                gC[0].getcellcorner().get(1), gC[0].getcellsize(), gC[0].getNODATA_VALUE(), gC[0].getWater_zone(), 0);
                        writeASC write_ridge_line = new writeASC(outputPath + "\\Ridge_Line.asc", gC[0].getnrows(), gC[0].getncolumns(), gC[0].getcellcorner().get(0),
                                gC[0].getcellcorner().get(1), gC[0].getcellsize(), gC[0].getNODATA_VALUE(), gC[0].getRidge_line(), 0);
                        showSaveSuccessDialog(jf, jf);
                        if (Selection[0]) {
                            gridDatabase.insertData(DB_Info[0], DB_Info[1], DB_Info[2], DB_Info[3], gC[0].getnrows(), gC[0].getncolumns(),
                                    gC[0].getgridRainfall_IDW(), gC[0].getgridRainfall_Kriging(), gC[0].getFilling_M_V(), gC[0].getSlope(),
                                    gC[0].getAspect(), gC[0].getFlow(), gC[0].getAcc_flow(), gC[0].getWater_zone(), gC[0].getRidge_line());
                            showSaveDBSuccessDialog(jf, jf);
                        }
                    }
                }catch (Exception ex){
                    showSaveFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }
            }
        });

        // 创建复选框
        JCheckBox checkBox_DBWriteGrid = new JCheckBox("结果写入MySQL");
        checkBox_DBWriteGrid.setBounds(218,585,120,30);
        checkBox_DBWriteGrid.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // 获取事件源（即复选框本身）
                JCheckBox checkBox = (JCheckBox) e.getSource();
                Selection[0] =checkBox.isSelected();
                System.out.println(checkBox.getText() + " 是否选中: " + checkBox.isSelected());
            }
        });
        // 设置默认第一个复选框选中
        checkBox_DBWriteGrid.setSelected(false);
        mainPanel.add(checkBox_DBWriteGrid);

        // 创建开关按钮
        JToggleButton ShowtoggleBtn = new JToggleButton("---启动图形显示---");
        ShowtoggleBtn.setBounds(350,135,150,30);
        // 添加 toggleBtn 的状态被改变的监听
        ShowtoggleBtn.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // 获取事件源（即开关按钮本身）
                JToggleButton toggleBtn = (JToggleButton) e.getSource();
                Selection[1]=toggleBtn.isSelected();
                System.out.println(toggleBtn.getText() + " 是否选中: " + toggleBtn.isSelected());
            }
        });
        ShowtoggleBtn.setSelected(true);
        mainPanel.add(ShowtoggleBtn);

        // 需要选择的条目
        String[] listData = new String[]{"原始DEM","IDW降雨量插值结果","克里金降雨量插值结果","填洼DEM","坡度","坡向","流向","累积流","河网","山脊线"};
        // 创建一个下拉列表框
        final JComboBox<String> ShowcomboBox = new JComboBox<String>(listData);
        ShowcomboBox.setBounds(512,135,151,30);
        final int[] selected_index = new int[1];
        // 添加条目选中状态改变的监听器
        ShowcomboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                // 只处理选中的状态
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    System.out.println("选中: " + ShowcomboBox.getSelectedIndex() + " = " + ShowcomboBox.getSelectedItem());
                    selected_index[0] =ShowcomboBox.getSelectedIndex();
                }
            }
        });
        // 设置默认选中的条目
        ShowcomboBox.setSelectedIndex(0);
        mainPanel.add(ShowcomboBox);

        JButton show_save_imageBtn = new JButton("保存并显示");
        show_save_imageBtn.setBounds(675,135,100,30);
        mainPanel.add(show_save_imageBtn);

        final JLabel[] show_Image = new JLabel[1];
        show_Image[0]=new JLabel();
        mainPanel.add(show_Image[0]);

        show_save_imageBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    show_output_Path[0]=showFileSaveDialog(jf,"SingleImage");
                    if(!show_output_Path[0].equals("No file")) {
                        System.out.println(show_output_Path[0]);
                        System.out.println((selected_index[0]));
                        // ====================================================================================================
                        // 这里加一个把所选择的的selected_index对应的数据输出到show_output_Path[0]路径变成一个jpg的操作 =>函数saveResultImage
                        saveResultImage(gC[0], selected_index[0], show_output_Path[0]);
                        //=====================================================================================================
                        show_Image[0].setBounds(350, 185, 425, 430);
                        if (Selection[1]) {
                            mainPanel.repaint();
                            ImageIcon image_icon = new ImageIcon(show_output_Path[0]);//背景图
                            image_icon.setImage(image_icon.getImage().getScaledInstance(show_Image[0].getWidth(), show_Image[0].getHeight(), Image.SCALE_DEFAULT));
                            show_Image[0].setIcon(image_icon);
                            mainPanel.repaint();
                        }
                    }
                }catch (Exception ex){
                    showSaveFailedDialog(jf,jf);
                    throw new RuntimeException(ex);
                }

            }
        });

        jf.setContentPane(mainPanel);
        jf.setVisible(true);
    }

    public static void saveResultImage(gridCalculator g,int selected_index,String show_output_Path) throws IOException {
        if(selected_index==0){
            ArrayList<ArrayList<Double>> writeArray=g.getDEM();
            // 二维ArrayList转成jpg输出到show_output_Path
            supportFunction.saveImg_Double(writeArray,show_output_Path,g.getNODATA_VALUE());
        }else if(selected_index==1){
            ArrayList<ArrayList<Double>> writeArray=g.getgridRainfall_IDW();
            // 二维ArrayList转成jpg输出到show_output_Path
            supportFunction.saveImg_Double(writeArray,show_output_Path,g.getNODATA_VALUE());
        }
        else if(selected_index==2){
            ArrayList<ArrayList<Double>> writeArray=g.getgridRainfall_Kriging();
            // 二维ArrayList转成jpg输出到show_output_Path
            supportFunction.saveImg_Double(writeArray,show_output_Path,g.getNODATA_VALUE());
        }
        else if(selected_index==3){
            ArrayList<ArrayList<Double>> writeArray=g.getFilling_M_V();
            // 二维ArrayList转成jpg输出到show_output_Path
            supportFunction.saveImg_Double(writeArray,show_output_Path,g.getNODATA_VALUE());
        }
        else if(selected_index==4){
            ArrayList<ArrayList<Double>> writeArray=g.getSlope();
            // 二维ArrayList转成jpg输出到show_output_Path
            supportFunction.saveImg_Double(writeArray,show_output_Path,g.getNODATA_VALUE());
        }
        else if(selected_index==5){
            ArrayList<ArrayList<Double>> writeArray=g.getAspect();
            // 二维ArrayList转成jpg输出到show_output_Path
            supportFunction.saveImg_Double(writeArray,show_output_Path,g.getNODATA_VALUE());
        }
        else if(selected_index==6){
            ArrayList<ArrayList<Integer>> writeArray=g.getFlow();
            // 二维ArrayList转成jpg输出到show_output_Path
            supportFunction.saveImg_Integer(writeArray,show_output_Path,g.getNODATA_VALUE());
        }
        else if(selected_index==7){
            ArrayList<ArrayList<Integer>> writeArray=g.getAcc_flow();
            // 二维ArrayList转成jpg输出到show_output_Path
            supportFunction.saveImg_Integer(writeArray,show_output_Path,g.getNODATA_VALUE());
        }
        else if(selected_index==8){
            ArrayList<ArrayList<Integer>> writeArray=g.getWater_zone();
            // 二维ArrayList转成jpg输出到show_output_Path
            supportFunction.saveImg_Integer(writeArray,show_output_Path,g.getNODATA_VALUE());
        }
        else{
            ArrayList<ArrayList<Integer>> writeArray=g.getRidge_line();
            // 二维ArrayList转成jpg输出到show_output_Path
            supportFunction.saveImg_Integer(writeArray,show_output_Path,g.getNODATA_VALUE());
        }
    }
    public static JMenuBar showMenu(JFrame jf){
        //创建一个菜单栏
        JMenuBar menuBar = new JMenuBar();

        //创建一级菜单
        JMenu optionMenu = new JMenu(" 一般选项 ");
        JMenu descriptionMenu = new JMenu(" 软件说明 ");
        JMenu helpMenu = new JMenu(" 帮助文档 ");
        JMenu aboutMenu = new JMenu(" 关于我们 ");
        // 一级菜单添加到菜单栏
        menuBar.add(optionMenu);
        menuBar.add(descriptionMenu);
        menuBar.add(helpMenu);
        menuBar.add(aboutMenu);

        // 创建 "一般选项" 一级菜单的子菜单
        JMenuItem exitMenuItem = new JMenuItem("退出...");
        // 子菜单添加到一级菜单
        optionMenu.add(exitMenuItem);

        // 创建 "软件说明" 一级菜单的子菜单
        JMenuItem introductionMenuItem = new JMenuItem("软件介绍");
        JMenuItem easyUseMenuItem = new JMenuItem("简要使用说明");
        JMenuItem DBMenuItem = new JMenuItem("MySQL数据库使用说明");
        // 子菜单添加到一级菜单
        descriptionMenu.add(introductionMenuItem);
        descriptionMenu.addSeparator();       // 添加一条分割线
        descriptionMenu.add(easyUseMenuItem);
        descriptionMenu.add(DBMenuItem);

        // 创建 "帮助文档" 一级菜单的子菜单
        JMenuItem helpdocMenuItem = new JMenuItem("帮助文档地址");
        JMenuItem reposMenuItem = new JMenuItem("gitHub仓库地址");
        // 子菜单添加到一级菜单
        helpMenu.add(helpdocMenuItem);
        helpMenu.addSeparator();                // 添加一个分割线
        helpMenu.add(reposMenuItem);

        // 创建 "关于我们" 一级菜单的子菜单
        JMenuItem aboutMenuItem = new JMenuItem("简要介绍");
        JMenuItem contactMenuItem = new JMenuItem("联系我们");
        // 子菜单添加到一级菜单
        aboutMenu.add(aboutMenuItem);
        aboutMenu.addSeparator();                // 添加一个分割线
        aboutMenu.add(contactMenuItem);

        // 菜单项的点击/状态改变事件监听，事件监听可以直接设置在具体的子菜单上（这里只设置其中几个）
        // 菜单栏监听
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });
        introductionMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuDialog(jf,jf,1);
            }
        });
        easyUseMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuDialog(jf,jf,2);
            }
        });
        DBMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuDialog(jf,jf,3);
            }
        });
        helpdocMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuDialog(jf,jf,4);
            }
        });
        reposMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuDialog(jf,jf,5);
            }
        });
        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuDialog(jf,jf,6);
            }
        });
        contactMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuDialog(jf,jf,7);
            }
        });

        // 一些可能会添加的代码
        //        // 创建 "帮助文档" 一级菜单的子菜单
//        final JCheckBoxMenuItem checkBoxMenuItem = new JCheckBoxMenuItem("复选框子菜单");
//        final JRadioButtonMenuItem radioButtonMenuItem01 = new JRadioButtonMenuItem("单选按钮子菜单01");
//        final JRadioButtonMenuItem radioButtonMenuItem02 = new JRadioButtonMenuItem("单选按钮子菜单02");
//        // 子菜单添加到一级菜单
//        helpMenu.add(checkBoxMenuItem);
//        helpMenu.addSeparator();                // 添加一个分割线
//        helpMenu.add(radioButtonMenuItem01);
//        helpMenu.add(radioButtonMenuItem02);
//
//        // 其中两个 单选按钮子菜单，要实现单选按钮的效果，需要将它们放到一个按钮组中
//        ButtonGroup btnGroup = new ButtonGroup();
//        btnGroup.add(radioButtonMenuItem01);
//        btnGroup.add(radioButtonMenuItem02);
//
//        // 默认第一个单选按钮子菜单选中
//        radioButtonMenuItem01.setSelected(true);
//        // 设置 复选框子菜单 状态改变 监听器
//        aboutMenuItem.addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                System.out.println("复选框是否被选中: " + aboutMenuItem.isSelected());
//            }
//        });
//        // 设置 单选按钮子菜单 状态改变 监听器
//        contactMenuItem.addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                System.out.println("单选按钮01 是否被选中: " + contactMenuItem.isSelected());
//            }
//        });

        return menuBar;
    }

    private static String showFileOpenDialog(Component parent, JTextArea msgTextArea) {
        // 创建一个默认的文件选取器
        JFileChooser fileChooser = new JFileChooser();

        // 设置默认显示的文件夹为当前文件夹
        fileChooser.setCurrentDirectory(new File("."));

        // 设置文件选择的模式（只选文件、只选文件夹、文件和文件均可选）
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // 设置是否允许多选
//        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setMultiSelectionEnabled(false);

        // 添加可用的文件过滤器（FileNameExtensionFilter 的第一个参数是描述, 后面是需要过滤的文件扩展名 可变参数）
//        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("txt(*.txt)", "txt"));
        // 设置默认使用的文件过滤器
        fileChooser.setFileFilter(new FileNameExtensionFilter("file(*.asc,*txt)", "asc","txt"));

        // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
        int result = fileChooser.showOpenDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION) {
            // 如果点击了"确定", 则获取选择的文件路径
            File file = fileChooser.getSelectedFile();
            msgTextArea.setText("");
            msgTextArea.append(file.getAbsolutePath());
            return file.getAbsolutePath();
        }
        return "No file";
    }

    private static String showFileSaveDialog(Component parent,String saveMode) {
        // 创建一个默认的文件选取器
        JFileChooser fileChooser = new JFileChooser();

        // 设置默认显示的文件夹为当前文件夹
        fileChooser.setCurrentDirectory(new File("."));

        if(saveMode.equals("All")) {
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setSelectedFile(new File(""));
        }
        else if(saveMode.equals("SingleImage")){
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setSelectedFile(new File("untitled.jpg"));
            fileChooser.setFileFilter(new FileNameExtensionFilter("image(*.jpg,*.jpeg,*.png)", "jpg","jpeg","png"));
        }
        else{
            // 设置打开文件选择框后默认输入的文件名
            fileChooser.setSelectedFile(new File("untitled.asc"));
            // 文件过滤器
            fileChooser.setFileFilter(new FileNameExtensionFilter("file(*.asc)", "asc"));
        }
        // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
        int result = fileChooser.showSaveDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION) {
            // 如果点击了"保存", 则获取选择的保存路径
            File file = fileChooser.getSelectedFile();
            return file.getAbsolutePath();
        }
        return "No file";
    }

    public static void showDBErrorDialog(Frame owner, Component parentComponent) {
        // 创建一个模态对话框
        final JDialog dialog = new JDialog(owner, "提示", true);
        // 设置对话框的宽高
        dialog.setSize(240, 160);
        // 设置对话框大小不可改变
        dialog.setResizable(false);
        // 设置对话框相对显示的位置
        dialog.setLocationRelativeTo(parentComponent);

        // 创建一个标签显示消息内容
        JLabel messageLabel = new JLabel("输入错误，请检查文件与数据库信息");
        messageLabel.setBounds(14, 20, 220, 30);
        // 创建一个按钮用于关闭对话框
        JButton okBtn = new JButton("确定");
        okBtn.setBounds(85, 73, 60, 30);
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 关闭对话框
                dialog.dispose();
            }
        });
        // 创建对话框的内容面板, 在面板内可以根据自己的需要添加任何组件并做任意是布局
        JPanel panel = new JPanel();
        panel.setLayout(null);
        // 添加组件到面板
        panel.add(messageLabel);
        panel.add(okBtn);
        // 设置对话框的内容面板
        dialog.setContentPane(panel);
        // 显示对话框
        dialog.setVisible(true);
    }
    public static void showDBSuccessDialog(Frame owner, Component parentComponent) {
        // 创建一个模态对话框
        final JDialog dialog = new JDialog(owner, "提示", true);
        // 设置对话框的宽高
        dialog.setSize(240, 160);
        // 设置对话框大小不可改变
        dialog.setResizable(false);
        // 设置对话框相对显示的位置
        dialog.setLocationRelativeTo(parentComponent);

        // 创建一个标签显示消息内容
        JLabel messageLabel = new JLabel("数据库登录存储成功");
        messageLabel.setBounds(54, 20, 220, 30);
        // 创建一个按钮用于关闭对话框
        JButton okBtn = new JButton("确定");
        okBtn.setBounds(85, 73, 60, 30);
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 关闭对话框
                dialog.dispose();
            }
        });
        // 创建对话框的内容面板, 在面板内可以根据自己的需要添加任何组件并做任意是布局
        JPanel panel = new JPanel();
        panel.setLayout(null);
        // 添加组件到面板
        panel.add(messageLabel);
        panel.add(okBtn);
        // 设置对话框的内容面板
        dialog.setContentPane(panel);
        // 显示对话框
        dialog.setVisible(true);
    }
    public static void showCalculateINGSuccessDialog(Frame owner, Component parentComponent) {
        // 创建一个模态对话框
        final JDialog dialog = new JDialog(owner, "提示", true);
        // 设置对话框的宽高
        dialog.setSize(240, 160);
        // 设置对话框大小不可改变
        dialog.setResizable(false);
        // 设置对话框相对显示的位置
        dialog.setLocationRelativeTo(parentComponent);

        // 创建一个标签显示消息内容
        JLabel messageLabel = new JLabel("计算正在进行中...");
        messageLabel.setBounds(54, 20, 220, 30);
        // 创建一个按钮用于关闭对话框
        JButton okBtn = new JButton("确定");
        okBtn.setBounds(85, 73, 60, 30);
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 关闭对话框
                dialog.dispose();
            }
        });

        // 创建对话框的内容面板, 在面板内可以根据自己的需要添加任何组件并做任意是布局
        JPanel panel = new JPanel();
        panel.setLayout(null);
        // 添加组件到面板
        panel.add(messageLabel);
        panel.add(okBtn);
        // 设置对话框的内容面板
        dialog.setContentPane(panel);
        // 显示对话框
        dialog.setVisible(true);
    }

    public static void showCalculateFinishedSuccessDialog(Frame owner, Component parentComponent) {
        // 创建一个模态对话框
        final JDialog dialog = new JDialog(owner, "提示", true);
        // 设置对话框的宽高
        dialog.setSize(240, 160);
        // 设置对话框大小不可改变
        dialog.setResizable(false);
        // 设置对话框相对显示的位置
        dialog.setLocationRelativeTo(parentComponent);

        // 创建一个标签显示消息内容
        JLabel messageLabel = new JLabel("计算完成可进行保存");
        messageLabel.setBounds(54, 20, 220, 30);
        // 创建一个按钮用于关闭对话框
        JButton okBtn = new JButton("确定");
        okBtn.setBounds(85, 73, 60, 30);
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 关闭对话框
                dialog.dispose();
            }
        });
        // 创建对话框的内容面板, 在面板内可以根据自己的需要添加任何组件并做任意是布局
        JPanel panel = new JPanel();
        panel.setLayout(null);
        // 添加组件到面板
        panel.add(messageLabel);
        panel.add(okBtn);
        // 设置对话框的内容面板
        dialog.setContentPane(panel);
        // 显示对话框
        dialog.setVisible(true);
    }
    public static void showCalculateFailedDialog(Frame owner, Component parentComponent) {
        // 创建一个模态对话框
        final JDialog dialog = new JDialog(owner, "提示", true);
        // 设置对话框的宽高
        dialog.setSize(240, 160);
        // 设置对话框大小不可改变
        dialog.setResizable(false);
        // 设置对话框相对显示的位置
        dialog.setLocationRelativeTo(parentComponent);

        // 创建一个标签显示消息内容
        JLabel messageLabel = new JLabel("计算失败，请检查文件与数据库信息");
        messageLabel.setBounds(14, 20, 220, 30);
        // 创建一个按钮用于关闭对话框
        JButton okBtn = new JButton("确定");
        okBtn.setBounds(85, 73, 60, 30);
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 关闭对话框
                dialog.dispose();
            }
        });
        // 创建对话框的内容面板, 在面板内可以根据自己的需要添加任何组件并做任意是布局
        JPanel panel = new JPanel();
        panel.setLayout(null);
        // 添加组件到面板
        panel.add(messageLabel);
        panel.add(okBtn);
        // 设置对话框的内容面板
        dialog.setContentPane(panel);
        // 显示对话框
        dialog.setVisible(true);
    }
    public static void showSaveSuccessDialog(Frame owner, Component parentComponent) {
        // 创建一个模态对话框
        final JDialog dialog = new JDialog(owner, "提示", true);
        // 设置对话框的宽高
        dialog.setSize(240, 160);
        // 设置对话框大小不可改变
        dialog.setResizable(false);
        // 设置对话框相对显示的位置
        dialog.setLocationRelativeTo(parentComponent);

        // 创建一个标签显示消息内容
        JLabel messageLabel = new JLabel("保存成功，请在保存目录下进行查看");
        messageLabel.setBounds(14, 20, 220, 30);
        // 创建一个按钮用于关闭对话框
        JButton okBtn = new JButton("确定");
        okBtn.setBounds(85, 73, 60, 30);
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 关闭对话框
                dialog.dispose();
            }
        });
        // 创建对话框的内容面板, 在面板内可以根据自己的需要添加任何组件并做任意是布局
        JPanel panel = new JPanel();
        panel.setLayout(null);
        // 添加组件到面板
        panel.add(messageLabel);
        panel.add(okBtn);
        // 设置对话框的内容面板
        dialog.setContentPane(panel);
        // 显示对话框
        dialog.setVisible(true);
    }
    public static void showSaveFailedDialog(Frame owner, Component parentComponent) {
        // 创建一个模态对话框
        final JDialog dialog = new JDialog(owner, "提示", true);
        // 设置对话框的宽高
        dialog.setSize(240, 160);
        // 设置对话框大小不可改变
        dialog.setResizable(false);
        // 设置对话框相对显示的位置
        dialog.setLocationRelativeTo(parentComponent);

        // 创建一个标签显示消息内容
        JLabel messageLabel = new JLabel("保存失败，请检查是否已经进行计算");
        messageLabel.setBounds(14, 20, 220, 30);
        // 创建一个按钮用于关闭对话框
        JButton okBtn = new JButton("确定");
        okBtn.setBounds(85, 73, 60, 30);
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 关闭对话框
                dialog.dispose();
            }
        });
        // 创建对话框的内容面板, 在面板内可以根据自己的需要添加任何组件并做任意是布局
        JPanel panel = new JPanel();
        panel.setLayout(null);
        // 添加组件到面板
        panel.add(messageLabel);
        panel.add(okBtn);
        // 设置对话框的内容面板
        dialog.setContentPane(panel);
        // 显示对话框
        dialog.setVisible(true);
    }
    public static void showSaveDBSuccessDialog(Frame owner, Component parentComponent) {
        // 创建一个模态对话框
        final JDialog dialog = new JDialog(owner, "提示", true);
        // 设置对话框的宽高
        dialog.setSize(240, 160);
        // 设置对话框大小不可改变
        dialog.setResizable(false);
        // 设置对话框相对显示的位置
        dialog.setLocationRelativeTo(parentComponent);

        // 创建一个标签显示消息内容
        JLabel messageLabel = new JLabel("保存到数据库成功，请于数据库检查");
        messageLabel.setBounds(14, 20, 220, 30);
        // 创建一个按钮用于关闭对话框
        JButton okBtn = new JButton("确定");
        okBtn.setBounds(85, 73, 60, 30);
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 关闭对话框
                dialog.dispose();
            }
        });
        // 创建对话框的内容面板, 在面板内可以根据自己的需要添加任何组件并做任意是布局
        JPanel panel = new JPanel();
        panel.setLayout(null);
        // 添加组件到面板
        panel.add(messageLabel);
        panel.add(okBtn);
        // 设置对话框的内容面板
        dialog.setContentPane(panel);
        // 显示对话框
        dialog.setVisible(true);
    }

    public static double[] inputParamsKrigingDialog(Frame owner, Component parentComponent) {
        // 创建一个模态对话框
        double Params[] = new double[3];
        final JDialog dialog = new JDialog(owner, "输入克里金插值模型参数", true);
        // 设置对话框的宽高
        dialog.setSize(400, 260);
        // 设置对话框大小不可改变
        dialog.setResizable(false);
        // 设置对话框相对显示的位置
        dialog.setLocationRelativeTo(parentComponent);

        // 创建一个标签显示消息内容
        JLabel ParamLabel1 = new JLabel("请输入相关距离系数：");
        ParamLabel1 .setBounds(80,15, 150, 30);

        JLabel  ParamLabel2 = new JLabel("请输入块金基台差值：");
        ParamLabel2.setBounds(80, 65, 150, 30);

        JLabel  ParamLabel3 = new JLabel("请输入块金效应误差：");
        ParamLabel3.setBounds(80, 115, 150, 30);

        final JTextField textField_THZS = new JTextField("28788",30);
        textField_THZS.setBounds(230,15,90,30);

        final JTextField textField_CSX = new JTextField("0.137",30);
        textField_CSX.setBounds(230,65,90,30);

        final JTextField textField_YCX = new JTextField("2.91",30);
        textField_YCX.setBounds(230,115,90,30);

        // 创建一个按钮用于关闭对话框
        JButton okBtn = new JButton("确定");
        okBtn.setBounds(160, 175, 60, 30);
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 关闭对话框
                Params[0]= Double.parseDouble(textField_THZS.getText());
                Params[1]= Double.parseDouble(textField_CSX.getText());
                Params[2]= Double.parseDouble(textField_YCX.getText());
                dialog.dispose();
            }
        });
        // 创建对话框的内容面板, 在面板内可以根据自己的需要添加任何组件并做任意是布局
        JPanel panel = new JPanel();
        panel.setLayout(null);
        // 添加组件到面板
        panel.add(ParamLabel1);
        panel.add(ParamLabel2);
        panel.add(ParamLabel3);
        panel.add(textField_THZS);
        panel.add(textField_CSX);
        panel.add(textField_YCX);
        panel.add(okBtn);
        // 设置对话框的内容面板
        dialog.setContentPane(panel);
        // 显示对话框
        dialog.setVisible(true);
        return Params;
    }

    public static double inputParamsWaterZoneDialog(Frame owner, Component parentComponent) {
        // 创建一个模态对话框
        double[] Params = new double[1];
        final JDialog dialog = new JDialog(owner, "输入河网提取阈值", true);
        // 设置对话框的宽高
        dialog.setSize(400, 150);
        // 设置对话框大小不可改变
        dialog.setResizable(false);
        // 设置对话框相对显示的位置
        dialog.setLocationRelativeTo(parentComponent);

        // 创建一个标签显示消息内容
        JLabel ParamLabel1 = new JLabel("请输入河网提取阈值：");
        ParamLabel1 .setBounds(80,15, 150, 30);

        final JTextField textField_THZS = new JTextField("30",30);
        textField_THZS.setBounds(210,15,90,30);

        // 创建一个按钮用于关闭对话框
        JButton okBtn = new JButton("确定");
        okBtn.setBounds(160, 60, 60, 30);
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 关闭对话框
                Params[0]= Double.parseDouble(textField_THZS.getText());
                dialog.dispose();
            }
        });
        // 创建对话框的内容面板, 在面板内可以根据自己的需要添加任何组件并做任意是布局
        JPanel panel = new JPanel();
        panel.setLayout(null);
        // 添加组件到面板
        panel.add(ParamLabel1);
        panel.add(textField_THZS);
        panel.add(okBtn);
        // 设置对话框的内容面板
        dialog.setContentPane(panel);
        // 显示对话框
        dialog.setVisible(true);
        return Params[0];
    }

    public static void menuDialog(Frame owner, Component parentComponent, int item) {

        String title;
        String attention;
        if(item==1){
            title="软件介绍";
            attention="本程序是一款基于网格计算类的水文分析工具";
        }
        else if(item==2){
            title="简要使用说明";
            attention="本程序使用必须使用MySQL本机数据库，具体数据库操作请查看MySQL数据库使用说明。操作顺序为导入三个对应的输入文件，并输入"
            + "对应的MySQL数据库信息，选择左下角是否写入MySQL数据库，点击运行。运行完成后即可进行计算（注意：填洼计算功能之后的功能均需先进行"
            + "填洼计算，这里已内置），这里推荐使用全部计算功能。计算完成后可选择对应的结果进行保存，这里推荐选择全部保存功能，保存结果全部为asc格式文件。"
            + "可在软件中查看计算结果，点选启动图形显示，选择需要查看的结果，并选择保存并显示功能在本机上保存为jpg文件即可在软件内简要查看";
        }
        else if(item==3){
            title="MySQL数据库使用说明";
            attention="MySQL数据库的使用需要四个参数，其中数据库名、用户名与密码按照MySQL的要求填写即可，但URL必须是Java提供的jdbc URL。"
            + "如果是本机的MySQL数据库，一般为jdbc:mysql://localhost:3306/。使用本程序必须进行MySQL的连接，否则无法使用功能。点选结果"
            + "写入MySQL数据库后，点击保存全部将同时把所有结果写入MySQL对应的表中；不点选则只将原关系数据（雨量站、雨量以及DEM基本信息）写入。";
        }
        else if(item==4){
            title="帮助文档地址";
            attention="https://github.com/JeasunLok/gridCalculator-hydrologic-analysis/blob/main/Help.md";
        }
        else if(item==5){
            title="gitHub仓库地址";
            attention="https://github.com/JeasunLok/gridCalculator-hydrologic-analysis";
        }
        else if(item==6){
            title="简要介绍";
            attention="中山大学地理科学与规划学院地理信息科学专业2020级本科生骆俊燊、林浩媚、辛书豪、陈鹏显、彭森林（排名不分先后）于2022年12月";
        }
        else if(item==7){
            title="联系我们";
            attention="邮件：luojsh7@mail2.sysu.edu.cn     地址：广东省广州市番禺区小谷围街道广州大学城外环东路132号中山大学广州校区东校园地理科学与规划学院";
        }
        else{
            title="错误";
            attention="错误";
        }

        // 创建一个模态对话框
        final JDialog dialog = new JDialog(owner, title, true);

        int Dialog_row = (attention.length()/20)+1;
        int Dialog_height = Dialog_row*18+30;
        // 设置对话框的宽高
        dialog.setSize(240, Dialog_height);
        // 设置对话框大小不可改变
        dialog.setResizable(false);
        // 设置对话框相对显示的位置
        dialog.setLocationRelativeTo(parentComponent);


        // 创建一个标签显示消息内容
        JTextArea JTextArea = new JTextArea(attention,Dialog_row,20);
        JTextArea.setFont(new Font("宋体",Font.PLAIN,12));
        JTextArea.setLineWrap(true);
        JTextArea.setWrapStyleWord(true);
        JTextArea.setEditable(false);
        JTextArea.setSize(200, Dialog_height);

        // 创建对话框的内容面板, 在面板内可以根据自己的需要添加任何组件并做任意是布局
        Box vBox = Box.createVerticalBox();
        vBox.add(JTextArea);
        // 添加组件到面板
        vBox.add(JTextArea);
        // 设置对话框的内容面板
        dialog.setContentPane(vBox);
        // 显示对话框
        dialog.setVisible(true);
    }
}
