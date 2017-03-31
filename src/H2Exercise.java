import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

class H2Exercise {

	static String H2_DRIVER = "org.h2.Driver";
	//static String DB_URL = "jdbc:h2:tcp://localhost/~/student;MVCC=FALSE";
	static String DB_URL = "jdbc:h2:tcp://localhost/~/test;MVCC=TRUE";
	static Object obj = new Object();
	
	public static Connection connectDB(String driver, String url, String user, String pw) throws Exception {
		// Register JDBC(H2) driver
		Class.forName(driver);

		// Open a connection
		log("connecting to h2 ...");
		Connection conn = DriverManager.
				getConnection(url, user, pw);
		log("successfully connected to h2 ...");
		
		return conn;
	}

	static void disconnectDB(Connection conn) throws Exception {
		log("Disconnecting db...");
		conn.close();
		log("Disconnected db...");
	}
	
	static void grantRoleToUser(Connection conn, String table, String role, String user) throws Exception {
		H2Exercise.log("Granting role to user ...");

		Statement stmt = conn.createStatement();
		// TODO: I want to execute these two commented query only if there isn't exist on db.
		//stmt.execute("CREATE USER danguria PASSWORD '123'");
		//stmt.execute("CREATE ROLE testing");
		stmt.execute("GRANT " + role + " ON " + table + " TO testing");
		stmt.execute("GRANT testing to " + user);
		
		H2Exercise.log("Granted role to user ...");
	}
	
	static void setAutoCommit(Connection conn, String set) throws Exception {
		H2Exercise.log("Setting auto commit " + set + "...");
		
		Statement stmt = conn.createStatement();
		stmt.execute("SET AUTOCOMMIT " + set);
		
		H2Exercise.log("Setted auto commit " + set + "...");
	}
	
	static void selectRecords(Connection conn) throws Exception {
		H2Exercise.log("Selecting records ...");
		
		Statement stmt = conn.createStatement();
		String sql = "SELECT id, first, last, age FROM Registration";
		ResultSet rs = stmt.executeQuery(sql);
		// Extract data from result set
		while(rs.next()){
			// Retrieve by column name
			int id  = rs.getInt("id");
			int age = rs.getInt("age");
			String first = rs.getString("first");
			String last = rs.getString("last");

			// Display values
			H2Exercise.log("ID: " + id + ", Age: " + age
					+ ", First: " + first + ", Last: " + last);
		}
		H2Exercise.log("Selected records ...");
		rs.close();	
	}
	
	
	static void commit(Connection conn) throws Exception {
		H2Exercise.log("committing ...");
		
		Statement stmt = conn.createStatement();
		stmt.execute("COMMIT");
	
		H2Exercise.log("committed ...");
	}
	
	static void executeSql(Connection conn, String sql) throws Exception {
		H2Exercise.log("Updating dummy records into the REGISTRATION table...");
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
		H2Exercise.log("Updated dummy records into the REGISTRATION table...");
	}
	
	static void log(String msg) {
		System.out.println("[" + Thread.currentThread().getId() + "] " + msg);
	}
	
	public H2Exercise() throws Exception {
		(new Admin()).start();
		Thread.sleep(2000);
		synchronized(obj) {
			(new User()).start();
		}
	}
	public static void main(String ar[]) throws Exception {
		new H2Exercise();
	}
}