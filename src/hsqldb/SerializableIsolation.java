package hsqldb;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import org.junit.Assert;

import utils.DB;
import utils.Debug;
import main.Main;

public class SerializableIsolation extends Thread {

	static String USER = "sa";
	static String PW = "";
	
	void runTest() throws Exception {
		
		Debug.logTitle("start serializable isolation test");
		
		Connection conn1 = DB.connectDB(HSQL.DRIVER, HSQL.DB_URL, USER, PW);
		Connection conn2 = DB.connectDB(HSQL.DRIVER, HSQL.DB_URL, USER, PW);
		
		DB.executeSql(conn1, "SET DATABASE TRANSACTION CONTROL MVCC");
		DB.executeSql(conn2, "SET DATABASE TRANSACTION CONTROL MVCC");
		conn2.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		
		conn1.setAutoCommit(false);
		conn2.setAutoCommit(false);


		Debug.log("conn1 tries to select ...");
		Assert.assertEquals(2, select(conn1));
		Debug.log("conn2 tries to select ...");
		Assert.assertEquals(2, select(conn2));

		Debug.log("conn2 tries to insert ...");
		insertRecord(conn1);

		Debug.log("conn1 tries to select ...");
		Assert.assertEquals(3, select(conn1));
		Debug.log("conn2 tries to select ...");
		Assert.assertEquals(2, select(conn2));

		Debug.log("conn1 tries to commit");
		conn1.commit();
		Debug.log("conn1 tries to select ...");
		Assert.assertEquals(3, select(conn1));
		Debug.log("conn2 tries to select ...");
		Assert.assertEquals(2, select(conn2));
		
		Debug.logTitle("program finished");

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

