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
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;

import utils.Debug;


public class Issue180 {

	private static final AtomicBoolean running = new AtomicBoolean(true);

	public Issue180() throws Exception {

		JdbcDataSource ds = new JdbcDataSource();
		ds.setUrl("jdbc:h2:mem:qed;DB_CLOSE_DELAY=-1;MVCC=TRUE;LOCK_TIMEOUT=120000;MULTI_THREADED=FALSE");


		List<String> expected = new ArrayList<String>();

		Connection c = ds.getConnection();
		c.setAutoCommit(false);
		update(c, "CREATE TABLE test ("
				+ "entity_id INTEGER NOT NULL PRIMARY KEY, "
				+ "lastUpdated TIMESTAMP NOT NULL)");


		PreparedStatement ps = c.prepareStatement("INSERT INTO test (entity_id, lastUpdated) VALUES (?, ?)");
		for (int i = 0; i < 10; i++) {
			ps.setInt(1,  i);
			expected.add(Integer.toString(i));
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.executeUpdate();
		}
		c.commit();

		new Updater(ds, expected).start();
		//for (int i = 0; i < 100; i++)
		new SelectForUpdate(ds, expected).start();
	}

	private static void update(Connection c, String sql) throws SQLException {
		Statement s = c.createStatement();
		s.executeUpdate(sql);
	}

	private static class Updater extends Thread {
		private final DataSource ds;
		private final List<String> expected;
		private final Random random = new Random();

		public Updater(DataSource ds, List<String> expected) {
			super();
			this.ds = ds;
			this.expected = expected;
		}

		@Override
		public void run() {
			try {
				while (running.get()) {
					Debug.logTitle("[updater] start loop ...");
					Connection c = ds.getConnection();
					c.setAutoCommit(false);

					int i = random.nextInt(expected.size());
					String s = expected.get(i);
					Debug.logTitle("[updater] prepareStatement ...");
					PreparedStatement ps = c.prepareStatement("UPDATE test SET lastUpdated = ?");
					ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
					Debug.logTitle("[updater] excuteUpdate ...");
					int cnt = ps.executeUpdate();
					Debug.logTitle("[updater] cnt: " + cnt);

					Debug.logTitle("[updater] commit ...");
					c.commit();

					try {
						Thread.sleep(10);
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
			Debug.logTitle("start new thread ...");
			while(running.get()) {
				try {
					Debug.logTitle("[selector] start loop ...");
					int i = random.nextInt(expected.size());
					String s = expected.get(i);
					Connection c = ds.getConnection();
					c.setAutoCommit(false);

					Debug.logTitle("[selector] prepare statement ...");
					PreparedStatement ps = c.prepareStatement("SELECT * FROM test WHERE entity_id = ? FOR UPDATE");
					ps.setString(1, s);
					Debug.logTitle("[selector] execute query ...");
					ResultSet set = ps.executeQuery();
					Debug.logTitle("set.next ...");
					if (!set.next()) {
						Debug.log("[selector] Found dropped record: " + s);

						running.set(false);
					} else {
						Debug.log("OK!: " + s);
					}

					c.commit();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
