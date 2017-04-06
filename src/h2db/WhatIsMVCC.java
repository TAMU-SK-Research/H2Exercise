package h2db;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import utils.DB;
import utils.Debug;

public class WhatIsMVCC {
	static String USER_READER = "reader";
	static String USER_WRITER = "writer";
	static String PW = "123";

	static void select(Connection conn) throws Exception {
		Debug.log("Selecting records ...");
		
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
			Debug.log("ID: " + id + ", Age: " + age
					+ ", First: " + first + ", Last: " + last);
		}
		Debug.log("Selected records ...");
		rs.close();	
	}

	void runReader() {
		new Thread() {
			public void run() {
				Connection conn;
				try {
					conn = DB.connectDB(H2DB.DRIVER, H2DB.DB_URL, USER_READER, PW);
					while (true) {
						select(conn);
						Thread.sleep(1000);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	void runWriter() {
		new Thread() {
			public void run() {
				Connection conn = null;
				try {
					conn = DB.connectDB(H2DB.DRIVER, H2DB.DB_URL, USER_WRITER, PW);
					conn.setAutoCommit(false);
					// update age every second and commit every 10 updates
					for (int i = 1; i < Integer.MAX_VALUE; i++) {
						String sql = "UPDATE Registration " +
								"SET age =" + i + "WHERE id in (1, 2)";
						DB.executeSql(conn, sql);
						if (i % 10 == 0) {
							conn.commit();
						}
						Thread.sleep(1000);
					}	
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	public WhatIsMVCC() {
		runWriter();
		runReader();
	}
}
