package com.amaker.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
	
	//打开数据库连接
	public Connection operConnection(){
		String driver=null;
		String url=null;
		String username=null;
		String password=null;
		try {
			Properties prop=new Properties();
			//从属性配置文件DBConfig.properties中获取连接数据库的信息
			prop.load(this.getClass().getClassLoader().getResourceAsStream("DBConfig.properties"));
			driver=prop.getProperty("driver");
			url=prop.getProperty("url");
			username=prop.getProperty("username");
			password=prop.getProperty("password");
			Class.forName(driver);//加载驱动
			return DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//连接MySQL数据库
	public Connection getConnection(String driver,String url,String username,String password){
		try {
			Class.forName(driver);//加载驱动
			return DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//关闭MySQL数据库连接
	public void closeConnection(Connection conn){
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
