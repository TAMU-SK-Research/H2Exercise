package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DB {
	public static Connection connectDB(String driver, String url, String user, String pw) throws Exception {
		// Register JDBC driver
		Class.forName(driver);

		// Open a connection
		Debug.log("connecting to db ...");
		Connection conn = DriverManager.
				getConnection(url, user, pw);
		Debug.log("successfully connected to db ...");
		
		return conn;
	}

	public static Connection connectDB(String url, String user, String pw) throws Exception {

		// Open a connection
		Debug.log("connecting to db ...");
		Connection conn = DriverManager.
				getConnection(url, user, pw);
		Debug.log("successfully connected to db ...");
		
		return conn;
	}

	public static void disconnectDB(Connection conn) throws Exception {
		Debug.log("Disconnecting db...");
		conn.close();
		Debug.log("Disconnected db...");
	}

	public static void createUser(Connection conn, String name, String pw) throws Exception {
		Statement stmt = conn.createStatement();
		String sql = "CREATE USER IF NOT EXISTS " + name + " PASSWORD " + pw;
		stmt.execute(sql);
	}
	public static void grantAllOnTableToPublic(Connection conn, String table) throws Exception {
		Statement stmt = conn.createStatement();
		stmt.execute("GRANT ALL ON " + table + " TO PUBLIC");
	}

	public static void executeSql(Connection conn, String sql) throws Exception {
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
	}
	
}
