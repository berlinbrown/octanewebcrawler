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
 * Octane crawler is a simple web crawler in Java.  All open with a liberal license.
 * 
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

import java.util.List;

import org.berlin.crawl.bean.BotCrawlerIgnore;
import org.berlin.crawl.bean.BotLink;
import org.berlin.crawl.dao.BotCrawlerDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class BotTrueCrawler {

	private static final Logger logger = LoggerFactory.getLogger(BotTrueCrawler.class);

	public static final int DELAY_FROM_PRODUCER = 600;

	private final ApplicationContext ctx;
	private BotLink firstLink;
	private LinkProcessQueueDatabase queue = new LinkProcessQueueDatabase();
	private final StartURLSeedReader seeder;

	public BotTrueCrawler(final StartURLSeedReader seeder, final ApplicationContext ctx, final BotLink l) {
		this.firstLink = l;
		this.ctx = ctx;
		this.seeder = seeder;
	}

	public void launch() {
		// Each bot crawler will launch in a separate thread //
		// Each true crawler will have a blocking queue for added
		// bot links to process
		loadIgnores();
		final BotTrueCrawlerThread thread = new BotTrueCrawlerThread();
		final Thread t = new Thread(thread);
		final String mngl = (this.firstLink.getHost().length() >= 12)
				? this.firstLink.getHost().substring(0, 10)
				: this.firstLink.getHost();
		t.setName(t.getName() + "-TrueCrawl-" + mngl);
		t.start();
		queue.launchReportSystem();
	} // End of the method //

	protected void loadIgnores() {
		final BotCrawlerDAO dao = new BotCrawlerDAO();
		final SessionFactory sf = (SessionFactory) ctx.getBean("sessionFactory");
		Session session = sf.openSession();
		final List<BotCrawlerIgnore> igs = dao.findIgnores(session);
		for (final BotCrawlerIgnore ig : igs) {
			this.queue.addIgnore(ig);
		}
		if (session != null) {
			// May not need to close the session
			session.close();
		} // End of the if //
	}

	public class BotTrueCrawlerThread implements Runnable {

		// Launch thread from true crawler.
		public void run() {
			// This is a thread because we want independent 'true crawler'
			// per each 'seed' item

			// Threaded approach for launching the producers
			// This operation will robots, connect and produce links
			// and add to the queue. (not a thread)
			final BotCrawlerProducer crawl = new BotCrawlerProducer(ctx, queue);
			crawl.connectAndCrawl(firstLink);
			// Size of queue after crawl
			logger.info("At true crawler thread, size of queue after crawl : " + queue.get().size());

			// Slight delay after producing the links for processing //
			try {
				Thread.sleep(DELAY_FROM_PRODUCER);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			} /// End of try catch //

			// At this point, we will have an initial set of links in the queue
			// It is possible the consumer could run for a while, adding and consuming links
			launchConsumerProducer();
			/// End of launching threads //

		} // End of method //

		protected void launchConsumerProducer() {
			final int consumersPerLink = 3;
			for (int i = 0; i < consumersPerLink; i++) {
				// At this point, we will have an initial set of links in the queue
				// It is possible the consumer could run for a while, adding and consuming links
				final BotLinkConsumerProducer consumerProducer = new BotLinkConsumerProducer(ctx, queue);
				final Thread cpThread = new Thread(consumerProducer);
				final String name = cpThread.getName();
				cpThread.setName(name + "-ConsumerProducer_" + i);
				seeder.addThread(cpThread);
				cpThread.start();
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				/// End of launching threads //
			} // End of the for //
		}
	} // End of the class //

} // End of the class //
