package com.demo.shelltools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DButils {

	public static KeyEntity getKeyEntityFromDB(){
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					"jdbc:mysql://127.0.0.1:3306/keystore"
					, "root" , "qwer1234");
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select * from keydb");
			ps = conn.prepareStatement("update keydb set usable = ? where id = ?");
			while(rs.next()){
				if(1 == rs.getInt(2)){
					
					KeyEntity ket = new KeyEntity(rs.getInt(1), rs.getInt(2), rs.getString(3));
					System.out.println("获得的记录：" + rs.getInt(1) + "\t"
										+ rs.getInt(2) + "\t"
											+ rs.getString(3));
					
					ps.setInt(1, 0);
					ps.setInt(2, rs.getInt(1));
					ps.executeUpdate();
					//stmt.executeUpdate("update keydb set usable = 0 where id = " + rs.getInt(1));
					
					return ket;
				}
			
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
			if(stmt!=null){
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					stmt = null;
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
		}
		
		return null;
	
	}
	
}
