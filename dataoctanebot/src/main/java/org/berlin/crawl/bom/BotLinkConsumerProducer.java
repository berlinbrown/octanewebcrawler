/* Copyright (c) 2013 Berlin Brown (berlin2research.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.berlin.crawl.bom;

/*
 * Octane crawler is a simple web crawler in Java.
 * http://code.google.com/p/octane-crawler/
 * http://berlin2research.com/
 * 
 * Author: Berlin Brown (berlin dot brown at gmail.com)
 * 
 * Libraries used:
 * ---------------- 
 * dom4j-1.6.1.jar, hibernate-core-4.0.1.Final.jar, hsqldb-1.8.0.10.jar, httpclient-4.2.3.jar, jackson-core-asl-1.9.12.jar, 
 * log4j-1.2.16.jar, mysql-connector-java-5.1.23.jar, opennlp-maxent-3.0.2-incubating.jar
 * opennlp-tools-1.5.2-incubating.jar, spring-core-3.1.1.RELEASE.jar, spring-web-3.1.1.RELEASE.jar, 
 * struts-core-1.3.10.jar, tagsoup-1.2.1.jar, tika-core-1.3.jar
 */

import java.util.Random;

import org.berlin.crawl.bean.BotLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class BotLinkConsumerProducer implements Runnable {

	// It is possible to launch multiple consumer producers against the same
	// link without issue.

	private final LinkProcessQueueDatabase queue;
	private static final Logger logger = LoggerFactory.getLogger(BotLinkConsumerProducer.class);

	private final ApplicationContext ctx;

	private boolean completeWithProcessing = false;

	public BotLinkConsumerProducer(final ApplicationContext ctx, final LinkProcessQueueDatabase queue) {
		this.queue = queue;
		this.ctx = ctx;
	}

	public void run() {
		try {
			final QueueMonitorThread monitor = new QueueMonitorThread();
			final Thread mn = new Thread(monitor);
			mn.setDaemon(false);
			mn.start();
			// Run until incomplete with processing //
			while (!completeWithProcessing) {
				// Loop until no other links are available //
				// We may need poison the queue so we can release
				consumeLink(queue.get().take());

				// Also add random delay between requests to avoid too much of an automated
				// request
				final Random rr = new Random(System.currentTimeMillis());
				final int rdelay = rr.nextInt(LinkProcessQueueDatabase.LINK_PROCESS_DELAY + 400);
				Thread.sleep(LinkProcessQueueDatabase.LINK_PROCESS_DELAY + rdelay);
			} // End of the while //

			// We have to remove the thread //
		} catch (final Throwable ex) {
			ex.printStackTrace();
		} // End of the try - catch //
	} // End of the method //

	public void consumeLink(final BotLink link) {
		if (link.getHost() == null) {
			// It is possible this is poisoned link, so exit
			logger.warn("Consuming link but this is invalid data, exiting");
			return;
		}
		logger.info("Consuming link : " + link + " consumedCount=" + queue.incConsumed());
		// This operation will robots, connect and produce links
		// and add to the queue. (non-threaded)
		// Recursive call //
		final BotCrawlerProducer crawl = new BotCrawlerProducer(ctx, queue);
		crawl.connectAndCrawl(link);
		// Size of queue after crawl
		logger.info("At consumer/producer crawler thread, size of queue after crawl : " + queue.get().size());
	} // End of the method //

	protected class QueueMonitorThread implements Runnable {
		// Monitor the link queue if no more links,
		// Wait after so many seconds and then exit
		private int numberOfChecks = 3;
		@Override
		public void run() {
			boolean operational = true;
			/// Run for 100 seconds
			try {
				// Delay for so many seconds //
				Thread.sleep(38 * 1000);
				while (operational) {
					// Monitor link queue
					// If queue size is zero run for a while
					// keep checking. If it is still zero then exit
					if (queue.get().size() == 0) {
						for (int i = 0; i < numberOfChecks; i++) {
							Thread.sleep(10 * 1000);
							logger.info(
									"At consumer/producer crawler monitor thread, checking status of queue ... size="
											+ queue.get().size());
						} // End of the if //

						// Perform one more check, if NOT zero, we are OK
						if (queue.get().size() == 0) {
							// Complete with processing
							completeWithProcessing = true;
							operational = false;
							queue.poison();
							logger.info(
									"At consumer/producer crawler monitor thread, complete with processing, sending message");
							// At this point, the thread should be dead //
						} // End of the if //
					} // End of if //
						// Main monitor delay
					Thread.sleep(20 * 1000);
				} // End of while
			} catch (final Exception e) {
				e.printStackTrace();
			}
		} // End of the method run
	} // End of the class //

} // End of the class //
