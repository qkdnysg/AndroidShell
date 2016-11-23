package com.demo.testtest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class InserIntoTable {
	public static void InserIntoTablewithNrows(int n){
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					"jdbc:mysql://127.0.0.1:3306/keystore"
					, "root" , "qwer1234");
			ps = conn.prepareStatement("insert into keydb values(null, 1, ?)");
			String str = null;
			for(int i = 0; i < n; i++){
				str = UUID.randomUUID().toString();
				ps.setString(1, str);
				ps.executeUpdate();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			//关闭资源
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					rs = null;
				}
			}
			if(ps!=null){
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					ps = null;
				}
			}
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					conn = null;
				}
			}
			System.out.println("恭喜！成功向数据库中插入了" + n + "行记录！");
		}
	}

}
