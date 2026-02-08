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

import java.util.Collections;
import java.util.List;

import org.berlin.crawl.dao.BotCrawlerDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ListSeedsMain {

	private static final Logger logger = LoggerFactory.getLogger(ListSeedsMain.class);

	private static final String PREFIX = "insert into bot_crawler_seeds(created_at, scheme, host, path, enabled) values('2013-03-14 03:22:36', 'http', '";
	private static final String POST1 = "', '";
	private static final String POST2 = "', 'Y');";

	public static void main(final String[] args) {
		logger.info("Running");
		final ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"/org/berlin/batch/batch-databot-context.xml");
		final BotCrawlerDAO dao = new BotCrawlerDAO();
		final SessionFactory sf = (SessionFactory) ctx.getBean("sessionFactory");
		Session session = sf.openSession();

		final StringBuffer buf = new StringBuffer();
		final List<String> seeds = dao.findHosts(session);
		final String nl = System.getProperty("line.separator");
		buf.append(nl);
		for (final String seed : seeds) {
			buf.append(PREFIX);
			buf.append(seed);
			buf.append(POST1);
			buf.append("/");
			buf.append(POST2);
			buf.append(nl);
		} // End of the for //
		logger.info(buf.toString());

		// Now print the number of links //
		final List<Long> ii = dao.countLinks(session);
		logger.warn("Count of Links : " + ii);

		// Also print top hosts //
		final List<Object[]> hosts = dao.findTopHosts(session);
		Collections.reverse(hosts);
		for (final Object[] oo : hosts) {
			System.out.println(oo[0] + " // " + oo[1].getClass());
		}

		if (session != null) {
			// May not need to close the session
			session.close();
		} // End of the if //
		logger.info("Done");
	} // End of the method //

} // End of the class //
