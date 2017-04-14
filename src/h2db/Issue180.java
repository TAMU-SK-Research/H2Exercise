package h2db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;

import utils.Debug;


public class Issue180 {

	private static final AtomicBoolean running = new AtomicBoolean(true);

	public Issue180() throws Exception {

		JdbcDataSource ds = new JdbcDataSource();
		//ds.setUrl("jdbc:h2:mem:qed;DB_CLOSE_DELAY=-1;MVCC=TRUE;LOCK_TIMEOUT=120000;MULTI_THREADED=TRUE");
		ds.setUrl("jdbc:h2:mem:qed;DB_CLOSE_DELAY=-1;MVCC=TRUE;LOCK_TIMEOUT=120000;MULTI_THREADED=FALSE");
		//ds.setUrl("jdbc:h2:tcp://localhost/~/test");

		
		List<String> expected = new ArrayList<String>();
		
		Connection c = ds.getConnection();
		c.setAutoCommit(false);
		update(c, "CREATE TABLE test ("
				+ "entity_id VARCHAR(100) NOT NULL PRIMARY KEY, "
				+ "lastUpdated TIMESTAMP NOT NULL)");

			
		PreparedStatement ps = c.prepareStatement("INSERT INTO test (entity_id, lastUpdated) VALUES (?, ?)");
		for (int i = 0; i < 10; i++) {
			String id = UUID.randomUUID().toString();
			expected.add(id);
			ps.setString(1, id);
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.executeUpdate();
		}
		c.commit();

		new Updater(ds).start();
		new SelectForUpdate(ds, expected).start();
	}
	
	private static void update(Connection c, String sql) throws SQLException {
		Statement s = c.createStatement();
		s.executeUpdate(sql);
	}

	private static class Updater extends Thread {
		private final DataSource ds;
		
		public Updater(DataSource ds) {
			super();
			this.ds = ds;
		}

		@Override
		public void run() {
			try {
				while (running.get()) {
					Connection c = ds.getConnection();
					c.setAutoCommit(false);

					PreparedStatement ps = c.prepareStatement("UPDATE test SET lastUpdated = ?");
					ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
					ps.executeUpdate();

					c.commit();
				
					String sql = "SELECT entity_id, lastUpdated FROM test";
					Statement stmt = c.createStatement();
					ResultSet rs = stmt.executeQuery(sql);

					while(rs.next()){
						// Retrieve by column name
						String id  = rs.getString("entity_id");
						Timestamp time = rs.getTimestamp("lastUpdated");

						// Display values
						Debug.log("ID: " + id + ", time: " + time.toString());
					}

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static class SelectForUpdate extends Thread {
		private final DataSource ds;
		private final List<String> expected;
		private final Random random = new Random();
		

		public SelectForUpdate(DataSource ds, List<String> expected) {
			super();
			this.ds = ds;
			this.expected = expected;
		}

		@Override
		public void run() {
			while(running.get()) {
				try {
					int i = random.nextInt(expected.size());
					String s = expected.get(i);
					Connection c = ds.getConnection();
					c.setAutoCommit(false);

					PreparedStatement ps = c.prepareStatement("SELECT * FROM test WHERE entity_id = ? FOR UPDATE");
					ps.setString(1, s);
					if (!ps.executeQuery().next()) {
						System.out.println("Found dropped record: " + s);
						running.set(false);
					}

					c.commit();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
