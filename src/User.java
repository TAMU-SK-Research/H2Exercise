import java.sql.Connection;
import java.sql.Statement;


public class User extends Thread {
	static String USER = "danguria";
	static String PW = "123";
	
	Connection conn = null;
	Statement stmt = null;

	public void run() {
		try {
			conn = H2Exercise.connectDB(H2Exercise.H2_DRIVER, H2Exercise.DB_URL, USER, PW);
			while (true) {
				H2Exercise.selectRecords(conn);
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
