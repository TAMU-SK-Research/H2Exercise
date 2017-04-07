package h2db;
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
		Debug.logTitle("start serializable isolation test(" + mvcc + ", " + lockMode +")");
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
		insertRecord(conn2);
		conn2.commit();
		int after = select(conn1);

		Assert.assertEquals(before, after);
		
		Debug.logTitle("program finished");
	}

	int select(Connection conn) throws Exception {
		String sql = "SELECT id, first, last, age FROM Registration WHERE age BETWEEN 1 AND 30";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		int numOfRecords = 0;
		while (rs.next()) numOfRecords++;
		Debug.log("num: " + numOfRecords);
		return numOfRecords;
	}

	void insertRecord(Connection conn) throws Exception {
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("INSERT INTO Registration " +
				"VALUES (104, 'Sungkeun', 'Kim', 2)");
	}

	public PhantomReads(boolean mvcc, int lockMode) {
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
