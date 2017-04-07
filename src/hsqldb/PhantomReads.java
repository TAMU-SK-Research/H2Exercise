package hsqldb;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import org.junit.Assert;

import utils.DB;
import utils.Debug;
import main.Main;

public class PhantomReads extends Thread {

	static String USER_ADMIN= "sa";
	static String PW = "";
	static String USER_READER= "reader";
	static String USER_WRITER = "writer";
	
	int lockMode = Connection.TRANSACTION_SERIALIZABLE;
	boolean mvcc = true;
	
	void runTest() throws Exception {
		Debug.logTitle("start dirty reads test(" + mvcc + ", " + lockMode +")");
		Admin.init();

		Connection conn1 = DB.connectDB(HSQL.DRIVER, HSQL.DB_URL, USER_ADMIN, "");
		Connection conn2 = DB.connectDB(HSQL.DRIVER, HSQL.DB_URL, USER_ADMIN, "");

		if (mvcc) {
			Debug.log("setting mvcc...");
			DB.executeSql(conn1, "SET DATABASE TRANSACTION CONTROL MVCC");
			DB.executeSql(conn2, "SET DATABASE TRANSACTION CONTROL MVCC");
		}
	
		conn1.setAutoCommit(false);
		conn2.setAutoCommit(false);

		conn1.setTransactionIsolation(lockMode);
		conn2.setTransactionIsolation(lockMode);
		
		
		Debug.log("before");
		Debug.log("table of conn1");
		printAll(conn1);
		Debug.log("table of conn2");
		printAll(conn2);
		
		
		//TODO:
		int before = select(conn1);
		insertRecord(conn2);
		conn2.commit();
		int after = select(conn1);
		
		Assert.assertEquals(before, after);
		
		Debug.log("after test");
		Debug.log("table of conn1");
		printAll(conn1);
		Debug.log("table of conn2");
		printAll(conn2);
		
		conn1.commit();
		conn2.commit();

		Debug.log("after commit");
		Debug.log("table of conn1");
		printAll(conn1);
		Debug.log("table of conn2");
		printAll(conn2);

		conn1.close();
		conn2.close();
		Debug.logTitle("test finished");
		
	}
	
	static void printAll(Connection conn) throws Exception {
		String sql = "SELECT * FROM Registration";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		while(rs.next()){
			// Retrieve by column name
			int id  = rs.getInt("id");
			int age = rs.getInt("age");
			String first = rs.getString("first");
			String last = rs.getString("last");

			// Display values
			Debug.log("ID: " + id + ", Age: " + age
					+ ", First: " + first + ", Last: " + last);
		}
	}
	
	int select(Connection conn) throws Exception {
		String sql = "SELECT id, first, last, age FROM Registration WHERE age BETWEEN 1 AND 30";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		int numOfRecords = 0;
		while (rs.next()) numOfRecords++;
		return numOfRecords;
	}
	
	void insertRecord(Connection conn) throws Exception {
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("INSERT INTO Registration " +
				"VALUES (3, 'Sungkeun', 'Kim', 2)");
	}

	public PhantomReads(boolean mvcc, int lockMode) {
		this.lockMode = lockMode;
		this.mvcc = mvcc;
	}
	
	public void run() {
		synchronized (Main.obj) {
			try {
				runTest();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

