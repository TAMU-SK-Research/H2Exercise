package h2db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.h2.jdbcx.JdbcDataSource;

import utils.Debug;

public class MiscTest {
	
	public MiscTest() throws Exception {
		JdbcDataSource ds = new JdbcDataSource();
		ds.setUrl("jdbc:h2:mem:testdb;");
		
		Connection conn = ds.getConnection();
		
		Statement stmt = conn.createStatement();
		String sql = "CREATE TABLE Contest (" 
				+ "id INTEGER NOT NULL PRIMARY KEY, "
				+ "contest_name VARCHAR(100) NOT NULL)";
		stmt.executeUpdate(sql);

		
		sql = "CREATE TABLE Division (" 
				+ "id INTEGER NOT NULL PRIMARY KEY, "
				+ "contest_id INTEGER NOT NULL, "
				+ "division_name VARCHAR(100) NOT NULL,"
				+ "FOREIGN KEY(contest_id) REFERENCES Contest(id))";
		stmt.executeUpdate(sql);

		sql = "INSERT INTO Contest (id, contest_name) VALUES (?, ?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, 1);
		ps.setString(2,  "contest1");
		ps.executeUpdate();

		sql = "INSERT INTO Contest (id, contest_name) VALUES (?, ?)";
		ps = conn.prepareStatement(sql);
		ps.setInt(1, 2);
		ps.setString(2,  "contest2");
		ps.executeUpdate();

		sql = "INSERT INTO Division (id, contest_id, division_name) VALUES (?, ?, ?)";
		ps = conn.prepareStatement(sql);
		ps.setInt(1, 1);
		ps.setInt(2, 1);
		ps.setString(3,  "division1");
		ps.executeUpdate();

		sql = "INSERT INTO Division (id, contest_id, division_name) VALUES (?, ?, ?)";
		ps = conn.prepareStatement(sql);
		ps.setInt(1, 2);
		ps.setInt(2, 2);
		ps.setString(3,  "division2");
		ps.executeUpdate();

		sql = "INSERT INTO Division (id, contest_id, division_name) VALUES (?, ?, ?)";
		ps = conn.prepareStatement(sql);
		ps.setInt(1, 3);
		ps.setInt(2, 2);
		ps.setString(3,  "division3");
		ps.executeUpdate();
		
	
		sql = "SELECT C.*, D.* FROM Contest AS C"
				+ " JOIN Division AS D"
				+ " ON C.id = D.contest_id";
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		while(rs.next()) {
			int id = rs.getInt("id");
			String contestName = rs.getString("contest_name");
			//int contest_id = rs.getInt("i
			String divisionName = rs.getString("division_name");
			
			Debug.log("ID: " + id + ", contestName: " + contestName + "divisionName: " + divisionName);
			//Debug.log("ID: " + id + ", contestName: " + contestName);
		}
		
		
		
		
	}
}