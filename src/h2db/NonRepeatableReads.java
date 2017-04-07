package h2db;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import utils.DB;
import utils.Debug;
import main.Main;

import org.junit.Assert;

public class NonRepeatableReads extends Thread {

	static String USER_ADMIN= "sa";
	static String PW = "";
	static String USER_READER= "reader";
	static String USER_WRITER = "writer";

	int lockMode = Connection.TRANSACTION_SERIALIZABLE;
	boolean mvcc = true;

	void runTest() throws Exception {
		Debug.logTitle("start dirty reads test(" + mvcc + ", " + lockMode +")");
		Admin.init(mvcc);
		String url = H2DB.DB_URL;
		if (mvcc) {
			url += ";MVCC=TRUE";
		} else {
			url += ";MVCC=FALSE";
		}

		Connection conn1 = DB.connectDB(H2DB.DRIVER, url, USER_ADMIN, "");
		conn1.setTransactionIsolation(lockMode);
		conn1.setAutoCommit(false);

		Connection conn2 = DB.connectDB(H2DB.DRIVER, url, USER_ADMIN, "");
		conn2.setTransactionIsolation(lockMode);
		conn2.setAutoCommit(false);


		int before = select(conn1);
		updateRecord(conn2);
		conn2.commit();
		int after = select(conn1);
		conn1.commit();
	
		Assert.assertEquals(before, after);
		conn1.close();
		conn2.close();
		Debug.logTitle("program finished");
	}

	int select(Connection conn) throws Exception {
		String sql = "SELECT id, first, last, age FROM Registration WHERE id = 1";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		rs.next();
		return rs.getInt("age");
		/*
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
		*/
	}

	void updateRecord(Connection conn) throws Exception {
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE Registration " +
				"SET age = 21 WHERE id = 1");
	}

	public NonRepeatableReads(boolean mvcc, int lockMode) {
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
