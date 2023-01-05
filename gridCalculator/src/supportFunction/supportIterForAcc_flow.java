package supportFunction;

import java.util.ArrayList;

public class supportIterForAcc_flow extends Thread{
    // 辅助累积流计算类 算法比较复杂
    public ArrayList<ArrayList<Integer>> acc_flow=new ArrayList<ArrayList<Integer>>();
    public ArrayList<ArrayList<Integer>> acc_flow_sup=new ArrayList<ArrayList<Integer>>();
    public ArrayList<ArrayList<Integer>> acc_flow_mark=new ArrayList<ArrayList<Integer>>();
    public void run(){
        System.out.println("supportIterForAcc_flow线程:"+getName());
    }
    public supportIterForAcc_flow(int nrows,int ncols){
        for(int i=0;i<nrows;i++){
            ArrayList<Integer> temp=new ArrayList<Integer>();
            for(int j=0;j<ncols;j++){
                temp.add(0);
            }
            acc_flow.add(temp);
        }
        for(int i=0;i<nrows;i++){
            ArrayList<Integer> temp=new ArrayList<Integer>();
            for(int j=0;j<ncols;j++){
                temp.add(0);
            }
            acc_flow_sup.add(temp);
        }
        for(int i=0;i<nrows;i++){
            ArrayList<Integer> temp=new ArrayList<Integer>();
            for(int j=0;j<ncols;j++){
                temp.add(0);
            }
            acc_flow_mark.add(temp);
        }
    }
}
