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
package org.berlin.crawl.util;

/*
 * Octane crawler is a simple web crawler in Java.
 * Simplest, proof of concept web crawler.
 * Crawling a URL is simple, request against the URL and download the content 
 * then parse the data and add any valid URLs to the link processing queue.
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

import org.berlin.crawl.bean.BotSeed;
import org.berlin.crawl.bom.StartURLSeedReader;
import org.berlin.crawl.dao.BotCrawlerDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AddNewSeedsMain {

	private static final Logger logger = LoggerFactory.getLogger(AddNewSeedsMain.class);
	
	public static BotSeed seed() {
		final BotSeed seed = new BotSeed();
		seed.setScheme("http");
		//seed.setHost("berlin2research.com");
		//seed.setPath("/crawl1/cr1.html");
		seed.setHost("thehill.com");
		seed.setPath("/");
		seed.setPort(null);		
		return seed;
	}  // End of the method //
	
	public static void main(final String [] args) {		
		logger.info("Running");
		final ApplicationContext ctx = new ClassPathXmlApplicationContext("/org/berlin/batch/batch-databot-context.xml");
		final BotCrawlerDAO dao = new  BotCrawlerDAO();
		final SessionFactory sf = (SessionFactory) ctx.getBean("sessionFactory");
		Session session = sf.openSession();		
		dao.createSeed(session, seed());
		final List<BotSeed> seeds = dao.findSeedRequests(session);
		for (final BotSeed seed : seeds) {
			System.out.println(seed);
		}
		if (session != null) {
			// May not need to close the session
			session.close();
		} // End of the if //
		logger.info("Done");		
	} // End of the method //
	
} // End of the class //
