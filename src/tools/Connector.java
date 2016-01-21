package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {

	String ip;
	String port;
	String dbName;
	String user;
	String pwd;

	public Connector(String dbName) {
		this.dbName = dbName;
		this.ip = "localhost";
		this.port = "3306";
		this.user = "root";
		this.pwd = "123456";
	}

	public Connector(String dbName, String user, String pwd) {
		this.dbName = dbName;
		this.user = user;
		this.pwd = pwd;
		this.ip = "localhost";
		this.port = "3306";
	}

	public Connector(String ip, String port, String dbName, String user,
			String pwd) {
		this.ip = ip;
		this.port = port;
		this.dbName = dbName;
		this.user = user;
		this.pwd = pwd;
	}

	public Connection getConnection() {
		Connection conn = null;
		try {
			String url = String.format(
					"jdbc:mysql://%s:%s/%s?user=%s&password=%s", ip, port,
					dbName, user, pwd);

			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public String getDBName() {
		return dbName;
	}

}
