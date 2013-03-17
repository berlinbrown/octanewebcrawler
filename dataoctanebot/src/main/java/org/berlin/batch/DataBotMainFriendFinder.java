package org.berlin.batch;

import org.berlin.batch.bom.FriendFindStats;
import org.berlin.batch.bom.FriendFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DataBotMainFriendFinder {

private static final Logger logger = LoggerFactory.getLogger(DataBotMainApp.class);
	
	public static void main(final String [] args) {
		logger.info("Launching databot application +");		
		final ApplicationContext ctx = new ClassPathXmlApplicationContext("/org/berlin/batch/batch-databot-context.xml");
		final DataBotMainFriendFinder app = new DataBotMainFriendFinder();
		app.launchConnectThreads(ctx);
	} // End of the method //
	
	protected void launchConnectThreads(final ApplicationContext ctx) {
		final FriendFindStats stats = new FriendFindStats();
		final int maxThreads = 1;
		final Thread ts [] = new Thread [maxThreads];
		for (int i = 0; i < maxThreads; i++) {
			final ConnectAndParseThread t = new ConnectAndParseThread(ctx, stats);			
			ts[i] = new Thread(t);
			ts[i].start();
			try {
				Thread.sleep(400);
			} catch(final Exception e) {
				e.printStackTrace();
			} // End of the try - catch //
		} // End of the for //
	} // End of the method //
	
	protected class ConnectAndParseThread implements Runnable {
		private final ApplicationContext ctx;
		private final FriendFindStats stats;
		public ConnectAndParseThread(final ApplicationContext ctx, final FriendFindStats stats) {
			this.ctx = ctx;
			this.stats = stats;
		}
		public void run() {
			final FriendFinder finder = new FriendFinder(this.stats);
			finder.find(ctx);
		} // End of the method //
	} // End of the class //
	
} // End of the class //
