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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.berlin.crawl.bean.BotSeed;
import org.berlin.crawl.dao.BotCrawlerDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartURLSeedReader {

	private Queue<Thread> activeThreads = new LinkedList<Thread>();
	
	/**
	 * Max active threads for processing links.
	 * Default (60)
	 */
	private int maxActiveThreads = 400;	
	
	private static final Logger logger = LoggerFactory.getLogger(StartURLSeedReader.class);
	
	public void launch() {
		final ApplicationContext ctx = new ClassPathXmlApplicationContext("/org/berlin/batch/batch-databot-context.xml");				
		final BotCrawlerDAO dao = new BotCrawlerDAO();
		final SessionFactory sf = (SessionFactory) ctx.getBean("sessionFactory");
		final Session session = sf.openSession();		
		final List<BotSeed> seeds = dao.findSeedRequests(session);
		int numberOfSeeds = 0;
		
		// Randomize the seed list //		
		Collections.shuffle(seeds);
		for (final BotSeed seed : seeds) {
			if ("Y".equalsIgnoreCase(seed.getEnabled())) {
				numberOfSeeds++;				
				if (this.activeThreads.size() < maxActiveThreads) {
					logger.info("At Start URL Seeder, seeding / num=" + numberOfSeeds + " / " + seed.getId() + " sz=" + this.activeThreads.size());
					// Only launch so many threads					
					final BotTrueCrawler crawl = new BotTrueCrawler(this, ctx, seed.toLink());
					crawl.launch();
					// Slight delay after producing the links for processing //
					try {
						Thread.sleep(BotTrueCrawler.DELAY_FROM_PRODUCER+(4*1000));
					} catch (final InterruptedException e) {			
						e.printStackTrace();
					} /// End of try catch //
				} else {					
					// Wait for the threads to finish, using join
					for (final Thread wt : this.activeThreads) {
						try {
							logger.info("At Start URL Seeder, waiting on seeds to complete");
							wt.join();							
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
					} // End of the for //
					// If we reached this point, remove all the threads , they finished processing
					if (this.activeThreads.size() > 0) {
						this.activeThreads.clear(); 
					}
				} // End of the if - else //				
				logger.info("!/=! At END OF seed launching, seeding / num=" + numberOfSeeds + " / " + seed.getId());
			} // End of the if //
		} // End of the for //
		
		// Wait for the threads to finish, using join
		for (final Thread wt : this.activeThreads) {
			try {
				logger.info("At Start URL Seeder, waiting on seeds to complete [f66x0");
				wt.join();							
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		} // End of the for //
		
		logger.info("!/=! At END OF seed seeding, launch complete");
		if (session != null) {
			// May not need to close the session
			session.close();
		} // End of the if //
	} // End of the method //
	
	public synchronized void addThread(final Thread t) {
		if (this.activeThreads.size() < maxActiveThreads) {
			// Only allow so many threads			
			this.activeThreads.add(t);
		}
	} // End of the method //
	
} // End of the class //
