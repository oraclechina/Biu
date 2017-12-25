package com.oracle.cloud.biu.modules.biubiu.shell.patch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OracleJDBCTools {

	public static boolean checkConnection(String ip, String port, String sid, String username, String p_password) {
		Connection con = null;
		PreparedStatement pre = null;
		ResultSet result = null;
		boolean pass = false;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String url = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + sid;
			String user = username;
			String password = p_password;
			con = DriverManager.getConnection(url, user, password);// 获取连接
			if (null != con)
				pass = true;
		} catch (Exception e) {
			pass = false;
		} finally {
			try {
				// 逐一将上面的几个对象关闭，因为不关闭的话会影响性能、并且占用资源
				// 注意关闭的顺序，最后使用的最先关闭
				if (result != null)
					result.close();
				if (pre != null)
					pre.close();
				if (con != null)
					con.close();
				return pass;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
}
