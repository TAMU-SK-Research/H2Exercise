import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

class H2Exercise {

	static String H2_DRIVER = "org.h2.Driver";
	static String DB_URL = "jdbc:h2:~/STUDENT";
	static String USER = "sa";
	static String PW = "";
	
	Connection conn = null;
	Statement stmt = null;

	void connectDB(String driver, String url, String user, String pw) throws Exception {
		// Register JDBC(H2) driver
		Class.forName(driver);

		// Open a connection
		System.out.println("connecting to h2 ...");
		conn = DriverManager.
				getConnection(url, user, pw);
		System.out.println("successfully connected to h2 ...");
	}
	void createDB() throws Exception {
		// Does H2 support feature for creating DB in code?
		System.out.println("Creating database...");
		stmt = conn.createStatement();

		String sql = "CREATE DATABASE STUDENTS";
		stmt.executeUpdate(sql);
		System.out.println("Database created successfully...");
	}
	
	void dropTable() throws Exception {
		System.out.println("Deleting table in given REGISTRATION...");
		stmt = conn.createStatement();

		String sql = "DROP TABLE REGISTRATION IF EXISTS";

		stmt.executeUpdate(sql);
		System.out.println("Table  deleted in given REGISTRATION...");
	}
	void createTable() throws Exception {
		System.out.println("Creating table in given REGISTRATION...");
		stmt = conn.createStatement();

		String sql = "CREATE TABLE REGISTRATION " +
				"(id INTEGER not NULL, " +
				" first VARCHAR(255), " + 
				" last VARCHAR(255), " + 
				" age INTEGER, " + 
				" PRIMARY KEY ( id ))"; 

		stmt.executeUpdate(sql);
		System.out.println("Created table in given REGISTRATION...");	
	}
	void insertRecords() throws Exception {
		System.out.println("Inserting records into the REGISTRATION table...");
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
		System.out.println("Inserted records into the REGISTRATION table...");
	}

	void selectRecords() throws Exception {
		System.out.println("Inserting records ...");
		stmt = conn.createStatement();

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
			System.out.print("ID: " + id);
			System.out.print(", Age: " + age);
			System.out.print(", First: " + first);
			System.out.println(", Last: " + last);
		}
		rs.close();	
	}
	
	void updateRecords() throws Exception {
		System.out.println("Updating records ...");
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
			System.out.print("ID: " + id);
			System.out.print(", Age: " + age);
			System.out.print(", First: " + first);
			System.out.println(", Last: " + last);
		}
		rs.close();
	}
	
	void deleteRecords() throws Exception {
		System.out.println("Deleting records...");
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
			System.out.print("ID: " + id);
			System.out.print(", Age: " + age);
			System.out.print(", First: " + first);
			System.out.println(", Last: " + last);
		}
		rs.close();
	}
	public H2Exercise() throws Exception {
		connectDB(H2_DRIVER, DB_URL, USER, PW);
		dropTable();
		createTable();
		insertRecords();
		selectRecords();
		updateRecords();
		deleteRecords();
	}
	public static void main(String ar[]) throws Exception {
		new H2Exercise();
	}
}