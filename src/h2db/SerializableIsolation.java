package h2db;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import org.junit.Assert;

import utils.DB;
import main.Main;

public class SerializableIsolation extends Thread {

	static String USER = "sa";
	static String PW = "";

	void runTest() throws Exception {
		Connection conn1 = DB.connectDB(H2DB.DRIVER, H2DB.DB_URL, USER, PW);
		conn1.setAutoCommit(false);

		Connection conn2 = DB.connectDB(H2DB.DRIVER, H2DB.DB_URL, USER, PW);
		conn2.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		conn2.setAutoCommit(false);

		Assert.assertEquals(2, select(conn1));
		Assert.assertEquals(2, select(conn2));

		insertRecord(conn1);

		Assert.assertEquals(3, select(conn1));
		Assert.assertEquals(2, select(conn2));

		conn1.commit();
		Assert.assertEquals(3, select(conn1));
		Assert.assertEquals(2, select(conn2));
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
