package hsqldb;

public class HSQL {
	static String DRIVER = "org.hsqldb.jdbcDriver";
	static String DB_URL = "jdbc:hsqldb:file:testdb";

	// Below code doesn't work... Am I wrong?
	// static String DB_URL = "jdbc:hsqldb:file:testdb;hsqldb.tx=locks";
}
