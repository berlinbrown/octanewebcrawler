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

import java.io.File;
import java.io.PrintWriter;

import org.apache.http.HttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.berlin.crawl.bean.BotCrawlerError;
import org.berlin.crawl.bean.BotLink;
import org.berlin.crawl.bean.RobotsInfo;
import org.berlin.crawl.dao.BotCrawlerDAO;
import org.berlin.crawl.error.CrawlerError;
import org.berlin.crawl.net.RobotsConnector;
import org.berlin.crawl.net.WebConnector;
import org.berlin.crawl.parse.RobotsParser;
import org.berlin.crawl.parse.WebParser;
import org.berlin.crawl.util.OctaneCrawlerConstants;
import org.berlin.crawl.util.text.IO;
import org.berlin.crawl.util.text.IO.Fx;
import org.berlin.crawl.util.text.TextHelpers;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Connect to robots, connect to page and produce a collection of links and add to the collective.
 * 
 * Non-threaded approach for connecting and crawling.
 * 
 * @author bbrown 
 */
public class BotCrawlerProducer {

	private static final Logger logger = LoggerFactory.getLogger(BotCrawlerProducer.class);
	
	private final LinkProcessQueueDatabase queue;
	private final ApplicationContext ctx;	
	
	public BotCrawlerProducer(final ApplicationContext ctx, final LinkProcessQueueDatabase queue) {
		this.queue = queue;
		this.ctx = ctx;
	}
	
	/**
	 * Crawl and produce a collection of links.
	 * 
	 * @param link
	 */
	public void connectAndCrawl(final BotLink link) {		
		if (this.queue.hasproc(link)) {
			// Already been processed, exit //
			logger.info("This link has been processed, exiting " + link);
			return;
		}
		if (link == null || link.getHost() == null) {
			logger.error("Invalid link at connect and crawl");
			return;
		}
		logger.info("Launching crawl against link : " + link + " // id=" + Thread.currentThread().getName());
		
		// First check, verify against the 'ignore' list
		final boolean okFromIgnore = this.queue.okFromIgnore(link.getHost());
		if (!okFromIgnore) {
			// Not OK, invalid URL e.g. google.com/search
			logger.info("Link is not valid based on ignore information, link=" + link);
			return;
		}
		
		final String host = link.getHost();
		if (queue.robots().get(host) == null) {
			this.connectRobots(link);
		} else {
			logger.info("Using existing robots data for host, host=" + host);
		}
		
		// Check the robots text against the URL
		// If robots data suggests not to crawl then don't crawl //
		boolean invalidRobotsRunConnect = false;
		if (queue.robots().get(host) != null) {					
			// With robots info, attempt to process
			final RobotsInfo robotsInfo = queue.robots().get(host);
			if (robotsInfo.valid()) {
				final boolean okToConnect = robotsInfo.verifyLink(link);
				if (okToConnect) {
					logger.info("Connecting and parsing with valid robots info");
					this.connectAndParse(link);
				} else {
					logger.info("Failed robot check : lastRule=" + robotsInfo.getLastRuleFail() + " link=" + link);
				}
			} else {
				invalidRobotsRunConnect = true;
			}
		} else {
			invalidRobotsRunConnect = true;
		} // End of the if - else //
		
		if (invalidRobotsRunConnect) {
			// Connect and parse without robot info //
			logger.info("Connecting and parsing without robots info [CRL76x0]");
			this.connectAndParse(link);
		} // End of the if else //
		
		logger.info("End of launching crawl against link : " + link);
	} // End of the method //
	
	protected void connectAndParse(final BotLink link)  {
		final WebConnector connector = new WebConnector(queue);
		try {
			// Based on allowed rules from robots.txt continue with parsing //			
			final URIBuilder builder = new URIBuilder();
			builder.setScheme(link.getScheme())
			.setHost(link.getHost())
			.setPath(link.getPath());
			final String data = connector.connect(link, builder);
			if (data == null) {
				logger.error("Could not collect data from request, link=" + link);
				return;
			} // End of the if //
			final WebParser parser = new WebParser(ctx, queue);
			parser.parse(link, builder, data);
		} catch(final CrawlerError ce) {
			logger.error("Crawler code Error at connect/parse", ce);
			final HttpResponse response = connector.getResponse();
			if (response != null) {
				if (response.getStatusLine() != null) {
					if (response.getStatusLine().getStatusCode() != 200) {
						// Log the error line
						try {
							final SessionFactory sf = (SessionFactory) ctx.getBean("sessionFactory");
							final Session session = sf.openSession();
							final BotCrawlerDAO dao = new BotCrawlerDAO();
							final BotCrawlerError berr = new BotCrawlerError();
							berr.setHost(link.getHost());
							berr.setStatus(response.getStatusLine().getStatusCode());
							berr.setStatusline(String.valueOf(response.getStatusLine()));
							berr.setUrl(String.valueOf(link.toString()));
							dao.createError(session, berr);
							if (session != null) {
								session.close();
							}
						} catch(final Throwable ee) {
							logger.error("Error at connect error save", ee);
						} 
					} // End if code //
				} // End of the if status line 
			} // End of the if - response //
		} catch(final Throwable e) {
			e.printStackTrace();
			logger.error("Error at connact and parse" , e);					
		} // End of the try - catch //
	} // End of the method //
	
	protected void connectRobots(final BotLink link)  {
		final RobotsConnector robotsConnector = new RobotsConnector();
		try {
			// Download robots.txt and parse //			
			final String robotsTxt = robotsConnector.connect(link.getScheme(), link.getHost());
			final RobotsParser robotParser = new RobotsParser();
			final RobotsInfo info = robotParser.parse(link, robotsTxt);		
			logger.info(String.valueOf(info));			
			
			// Write robots info to correct path //
			final TextHelpers text = new TextHelpers();
			final String dir = text.baseDirectory(link);					 					
			final File chkdir = new File(OctaneCrawlerConstants.CRAWLER_HOME + "/" + dir + "_robots_ignore");
			final boolean res = chkdir.mkdirs();			
			final String robotpath = OctaneCrawlerConstants.CRAWLER_HOME + "/" + dir + "_robots_ignore/robots.txt";
			new IO<Void>().w(robotpath, new Fx<PrintWriter>() {							
				 public void $(final PrintWriter o, final int idx) {
					 o.println(robotsTxt);
				 } 
			});						
			// Save that this robots data is available //
			queue.robots().put(link.getHost(), info);	
		} catch(final CrawlerError ce) {
			logger.error("Crawler code Error at connect robots", ce);
			final HttpResponse response = robotsConnector.getResponse();
			if (response != null) {
				if (response.getStatusLine() != null) {
					if (response.getStatusLine().getStatusCode() != 200) {
						// Log the error line
						try {
							final SessionFactory sf = (SessionFactory) ctx.getBean("sessionFactory");
							final Session session = sf.openSession();
							final BotCrawlerDAO dao = new BotCrawlerDAO();
							final BotCrawlerError berr = new BotCrawlerError();
							berr.setHost(link.getHost());
							berr.setStatus(response.getStatusLine().getStatusCode());
							berr.setStatusline(String.valueOf(response.getStatusLine()));
							berr.setUrl(String.valueOf(link.toString()));
							dao.createError(session, berr);
							if (session != null) {
								session.close();
							}
						} catch(final Throwable ee) {
							logger.error("Error at connect robots/error save", ee);
						} 
					} // End if code //
				} // End of the if status line 
			} // End of the if - response //
		} catch(final Throwable e) {
			logger.error("Error at connect robots", e);			
			// It is possible to log the error and persist			
		} // End of the try - catch //	
	} // End of the method //
	
} // End of the class //
