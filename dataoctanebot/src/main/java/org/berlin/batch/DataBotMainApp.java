package org.berlin.batch;

import org.berlin.batch.bom.DataMessageFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DataBotMainApp {

	private static final Logger logger = LoggerFactory.getLogger(DataBotMainApp.class);

	public static void main(final String[] args) {
		logger.info("Launching databot application +");
		final ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"/org/berlin/batch/batch-databot-context.xml");
		final DataBotMainApp app = new DataBotMainApp();
		app.launchConnectThreads(ctx);
	} // End of the method //

	protected void launchConnectThreads(final ApplicationContext ctx) {
		final int maxThreads = 4;
		final Thread ts[] = new Thread[4];
		for (int i = 0; i < maxThreads; i++) {
			int forceRule = (i == 0) ? 3 : 1;
			final ConnectAndParseThread t = new ConnectAndParseThread(ctx, forceRule);
			ts[i] = new Thread(t);
			ts[i].start();
			try {
				Thread.sleep(400);
			} catch (final Exception e) {
				e.printStackTrace();
			} // End of the try - catch //
		} // End of the for //

	} // End of the method //

	protected class ConnectAndParseThread implements Runnable {
		private final ApplicationContext ctx;
		private final int rule;
		public ConnectAndParseThread(final ApplicationContext ctx, final int rule) {
			this.ctx = ctx;
			this.rule = rule;
		}
		public void run() {
			final DataMessageFinder runner = new DataMessageFinder();
			runner.setForceRequestRule(rule);
			runner.execute(ctx);
		} // End of the method //
	} // End of the class //

} // End of the class //
