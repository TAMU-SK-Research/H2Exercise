package main;

import h2db.Issue180;
import h2db.MiscTest;
import h2db.SyncAndExceptionTest;

import java.sql.Connection;

public class Main {
	public static Object obj = new Object();
	static void whatIsMVCC() {
		//h2db.Admin.init();
		//new h2db.WhatIsMVCC();
	}
	
	static void testTransactionIsolation() {
		/* DirtyReads for H2DB
		 */
		//new h2db.DirtyReads(true, Connection.TRANSACTION_SERIALIZABLE).run();
		//new h2db.DirtyReads(true, Connection.TRANSACTION_READ_COMMITTED).run();
		//new h2db.DirtyReads(true, Connection.TRANSACTION_READ_UNCOMMITTED).run();

		//new h2db.DirtyReads(false, Connection.TRANSACTION_SERIALIZABLE).run();
		//new h2db.DirtyReads(false, Connection.TRANSACTION_READ_COMMITTED).run();
		//new h2db.DirtyReads(false, Connection.TRANSACTION_READ_UNCOMMITTED).run();
		
		
		/* DirtyReads for HSQl
		 */
		//new hsqldb.DirtyReads(true, Connection.TRANSACTION_SERIALIZABLE).run();
		//new hsqldb.DirtyReads(true, Connection.TRANSACTION_READ_COMMITTED).run();
		//new hsqldb.DirtyReads(true, Connection.TRANSACTION_READ_UNCOMMITTED).run();

		//new hsqldb.DirtyReads(false, Connection.TRANSACTION_SERIALIZABLE).run();
		//new hsqldb.DirtyReads(false, Connection.TRANSACTION_READ_COMMITTED).run();
		//new hsqldb.DirtyReads(false, Connection.TRANSACTION_READ_UNCOMMITTED).run();

		/* NonRepeatableReads for H2DB
		 */
		//new h2db.NonRepeatableReads(true, Connection.TRANSACTION_SERIALIZABLE).run();
		//new h2db.NonRepeatableReads(true, Connection.TRANSACTION_READ_COMMITTED).run();
		//new h2db.NonRepeatableReads(true, Connection.TRANSACTION_READ_UNCOMMITTED).run();

		//new h2db.NonRepeatableReads(false, Connection.TRANSACTION_SERIALIZABLE).run();
		//new h2db.NonRepeatableReads(false, Connection.TRANSACTION_READ_COMMITTED).run();
		//new h2db.NonRepeatableReads(false, Connection.TRANSACTION_READ_UNCOMMITTED).run();


		/* NonRepeatableReads for HSQL
		 */
		//new hsqldb.NonRepeatableReads(true, Connection.TRANSACTION_SERIALIZABLE).run();
		//new hsqldb.NonRepeatableReads(true, Connection.TRANSACTION_READ_COMMITTED).run();
		//new hsqldb.NonRepeatableReads(true, Connection.TRANSACTION_READ_UNCOMMITTED).run();

		//new hsqldb.NonRepeatableReads(false, Connection.TRANSACTION_SERIALIZABLE).run();
		//new hsqldb.NonRepeatableReads(false, Connection.TRANSACTION_READ_COMMITTED).run();
		//new hsqldb.NonRepeatableReads(false, Connection.TRANSACTION_READ_UNCOMMITTED).run();

		/* PhantomReads for H2DB
		*/
		(new h2db.PhantomReads(true, Connection.TRANSACTION_SERIALIZABLE)).start();
		//(new h2db.PhantomReads(true, Connection.TRANSACTION_READ_COMMITTED)).start();
		//(new h2db.PhantomReads(true, Connection.TRANSACTION_READ_UNCOMMITTED)).start();
		
		//(new h2db.PhantomReads(false, Connection.TRANSACTION_SERIALIZABLE)).start();
		//(new h2db.PhantomReads(false, Connection.TRANSACTION_READ_COMMITTED)).start();
		//(new h2db.PhantomReads(false, Connection.TRANSACTION_READ_UNCOMMITTED)).start();

		/* PhantomReads for HSQL
		*/
		//(new hsqldb.PhantomReads(true, Connection.TRANSACTION_SERIALIZABLE)).start();
		//(new hsqldb.PhantomReads(true, Connection.TRANSACTION_READ_COMMITTED)).start();
		//(new hsqldb.PhantomReads(true, Connection.TRANSACTION_READ_UNCOMMITTED)).start();
		
		//(new hsqldb.PhantomReads(false, Connection.TRANSACTION_SERIALIZABLE)).start();
		//(new hsqldb.PhantomReads(false, Connection.TRANSACTION_READ_COMMITTED)).start();
		//(new hsqldb.PhantomReads(false, Connection.TRANSACTION_READ_UNCOMMITTED)).start();
		
	}
	
	public static void main(String ar[]) throws Exception {
		//whatIsMVCC();
		//testTransactionIsolation();
		new Issue180();
		//new MiscTest();
		//new SyncAndExceptionTest();
	}
}
