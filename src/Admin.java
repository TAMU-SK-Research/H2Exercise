import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;



public class Admin extends Thread {
	
	static String USER = "sa";
	static String PW = "";
	
	Connection conn = null;
	Statement stmt = null;

	public void run()  {
		try {
			
			// initialize database
			synchronized(H2Exercise.obj) {
				conn = H2Exercise.connectDB(H2Exercise.H2_DRIVER, H2Exercise.DB_URL, USER, PW);
				dropTable();
				createTable();
				H2Exercise.grantRoleToUser(conn, "REGISTRATION", "SELECT", "danguria");
				stmt = conn.createStatement();
				stmt.executeUpdate("INSERT INTO Registration " +
						"VALUES (1, 'Zara', 'Ali', 18)");
				stmt.executeUpdate("INSERT INTO Registration " +
						"VALUES (2, 'Mahnaz', 'Fatma', 25)");
				H2Exercise.setAutoCommit(conn, "OFF");
			}
			
			// update age every second and commit every 10 updates
			for (int i = 1; i < Integer.MAX_VALUE; i++) {
				String sql = "UPDATE Registration " +
                "SET age =" + i + "WHERE id in (1, 2)";
				insertDumyRecords(sql);
				if (i % 10 == 0) {
					H2Exercise.commit(conn);
				}
				Thread.sleep(1000);
			}
			
			H2Exercise.disconnectDB(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void dropTable() throws Exception {
		H2Exercise.log("Deleting table in given REGISTRATION...");
		
		stmt = conn.createStatement();
		String sql = "DROP TABLE REGISTRATION IF EXISTS";
		stmt.executeUpdate(sql);
		
		H2Exercise.log("Table  deleted in given REGISTRATION...");
	}
	
	void createTable() throws Exception {
		H2Exercise.log("Creating table in given REGISTRATION...");
		
		stmt = conn.createStatement();
		String sql = "CREATE TABLE REGISTRATION " +
				"(id INTEGER not NULL, " +
				" first VARCHAR(255), " + 
				" last VARCHAR(255), " + 
				" age INTEGER, " + 
				" PRIMARY KEY ( id ))"; 
		stmt.executeUpdate(sql);
		
		H2Exercise.log("Created table in given REGISTRATION...");	
	}
	
	void insertDumyRecords(String sql) throws Exception {
		H2Exercise.log("Inserting dumy records into the REGISTRATION table...");
		stmt.executeUpdate(sql);
		H2Exercise.log("Inserted dumy records into the REGISTRATION table...");
	}
	
	void insertRecords() throws Exception {
		H2Exercise.log("Inserting records into the REGISTRATION table...");
		stmt = conn.createStatement();

		String sql = "INSERT INTO Registration " +
				"VALUES (100, 'Zara', 'Ali', 18)";
		stmt.executeUpdate(sql);
		
		sql = "INSERT INTO Registration " +
				"VALUES (101, 'Mahnaz', 'Fatma', 25)";
		stmt.executeUpdate(sql);
		sql = "INSERT INTO Registration " +
				"VALUES (102, 'Zaid', 'Khan', 30)";
		stmt.executeUpdate(sql);
		sql = "INSERT INTO Registration " +
				"VALUES(103, 'Sumit', 'Mittal', 28)";
		stmt.executeUpdate(sql);
		H2Exercise.log("Inserted records into the REGISTRATION table...");
	}

	void updateRecords() throws Exception {
		H2Exercise.log("Updating records ...");
		stmt = conn.createStatement();
		String sql = "UPDATE Registration " +
				"SET age = 30 WHERE id in (100, 101)";
		stmt.executeUpdate(sql);

		// Now you can extract all the records
		// to see the updated records
		sql = "SELECT id, first, last, age FROM Registration";
		ResultSet rs = stmt.executeQuery(sql);

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
		rs.close();
	}
	
	void deleteRecords() throws Exception {
		H2Exercise.log("Deleting records...");
		stmt = conn.createStatement();
		String sql = "DELETE FROM Registration " +
				"WHERE id = 101";
		stmt.executeUpdate(sql);

		// Now you can extract all the records
		// to see the remaining records
		sql = "SELECT id, first, last, age FROM Registration";
		ResultSet rs = stmt.executeQuery(sql);

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
		rs.close();
	}

}
