package main;

public class Main {
	public static Object obj = new Object();
	public static void main(String ar[]) throws Exception {
		/* WhatIsMVCC
		h2db.Admin.init();
		new h2db.WhatIsMVCC();
		 */

		/* SerializableIsolation for H2DB
		h2db.Admin.init();
		(new h2db.SerializableIsolation()).start();
		*/

		/* SerializableIsolation for HSQLDB
		*/
		new hsqldb.Admin();
		(new hsqldb.SerializableIsolation()).start();
	}
}
