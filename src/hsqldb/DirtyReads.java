package hsqldb;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import utils.DB;
import utils.Debug;
import main.Main;

import org.junit.Assert;

public class DirtyReads extends Thread {

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

	
		Debug.log("conn1 tries to select");
		int before = select(conn1);
		Debug.log("conn2 tries to update");
		updateRecord(conn2);
		Debug.log("conn1 tries to select again");
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
//		String sql = "SELECT id, first, last, age FROM Registration WHERE id = 1";
		String sql = "SELECT id, first, last, age FROM Registration WHERE id = 100";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		rs.next();
		return rs.getInt("age");
	}

	void updateRecord(Connection conn) throws Exception {
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE Registration " +
				"SET age = 21 WHERE id = 100");
	}

	public DirtyReads(boolean mvcc, int lockMode) {
		this.lockMode = lockMode;
		this.mvcc = mvcc;
	}
	public void run() {

		synchronized (Main.obj){ 
			try {
				runTest();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}

