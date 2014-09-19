package com.sinaapp.expr3d;

import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.PreparedStatement;  
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {

	public static final String data3d = "select * from data3d";
    public static final String URL="jdbc:mysql://localhost:3306/expr3d";
    public static final String username="ethan";
    public static final String password="never-say-goodbye";
    public static final String driver="com.mysql.jdbc.Driver";
    
    public static List<List<Integer>> Load3DData() throws Exception{
        Class.forName(driver).newInstance();
        Connection conn=DriverManager.getConnection(URL,username,password);    
        PreparedStatement pst = conn.prepareStatement(data3d);//准备执行语句  

    	List<List<Integer>> arr = new ArrayList<List<Integer>>();
    	
        String sql = "select * from data3d";//SQL语句  
        ResultSet ret = pst.executeQuery();//执行语句，得到结果集  
        while (ret.next()) {  
            int qh = ret.getInt("qh");
            int rq = ret.getInt("rq");
            int b = ret.getInt("b");
            int s = ret.getInt("s");
            int g = ret.getInt("g");
            int sb = ret.getInt("sb");
            int ss = ret.getInt("ss");
            int sg = ret.getInt("sg");
            List<Integer> record = new ArrayList<Integer>();
            record.add(qh);
            record.add(rq);
            record.add(b);
            record.add(s);
            record.add(g);
            record.add(sb);
            record.add(ss);
            record.add(sg);
            arr.add(record);               
        }//显示数据  
        ret.close();  
        conn.close();  
        pst.close();  
        
    	return arr; 	
    }
    
}
