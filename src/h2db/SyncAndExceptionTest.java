package h2db;

public class SyncAndExceptionTest {
	
	public SyncAndExceptionTest() {
		Object lock = new Object();
		new Thread1(lock).start();
		new Thread2(lock).start();
	}

}


class Thread1 extends Thread {

	Object lock = null;
	
	public Thread1(Object lock) {
		this.lock = lock;
	}
	public void run() {
		
		synchronized (lock) {
			while (true) {
				try {
					System.out.println("[" + Thread.currentThread().getId() + "]" + " sleep...");
					Thread.sleep(5000);
					System.out.println("[" + Thread.currentThread().getId() + "]" + " woke up and throw exception...");
					throw new Exception();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}


class Thread2 extends Thread {

	Object lock = null;
	
	public Thread2(Object lock) {
		this.lock = lock;
	}
	public void run() {
		
		synchronized (lock) {
			while (true) {
				try {
					System.out.println("[" + Thread.currentThread().getId() + "]" + " sleep...");
					Thread.sleep(5000);
					System.out.println("[" + Thread.currentThread().getId() + "]" + " woke up and throw exception...");
					throw new Exception();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}