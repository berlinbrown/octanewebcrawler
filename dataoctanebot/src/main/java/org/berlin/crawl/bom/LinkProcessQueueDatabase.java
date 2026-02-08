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

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.berlin.crawl.bean.BotCrawlerIgnore;
import org.berlin.crawl.bean.BotLink;
import org.berlin.crawl.bean.RobotsInfo;
import org.berlin.logs.scan.NullRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkProcessQueueDatabase {

	private static final Logger logger = LoggerFactory.getLogger(LinkProcessQueueDatabase.class);

	/**
	 * Main delay between requests for a host.
	 * 
	 * Default value is 4 seconds.
	 */
	public static final int LINK_PROCESS_DELAY = 3 * 900;

	private BlockingQueue<BotLink> queue = new LinkedBlockingQueue<BotLink>();
	private Map<BotLink, LinkProcessStatus> processed = new Hashtable<BotLink, LinkProcessStatus>();
	private AtomicInteger linksConsumed = new AtomicInteger();

	private Map<String, RobotsInfo> robotsData = new Hashtable<String, RobotsInfo>();

	/**
	 * For friendly browsing, ignore these URLs or hosts.
	 */
	private List<BotCrawlerIgnore> ignoreDatabase = new Vector<BotCrawlerIgnore>();

	public synchronized void addIgnore(final BotCrawlerIgnore ig) {
		this.ignoreDatabase.add(ig);
	}

	/**
	 * Verify if link is OK to connect based on ignore info.
	 * 
	 * @return
	 */
	public synchronized boolean okFromIgnore(final String host) {
		if (host == null) {
			// not OK, invalid url
			return false;
		}
		if (ignoreDatabase.size() == 0) {
			// If list is empty can't verify
			return true;
		}
		for (final BotCrawlerIgnore ig : ignoreDatabase) {
			if (NullRef.hasValue(ig.getHost())) {
				final String h = ig.getHost().trim().toLowerCase();
				final String hst = host.trim().toLowerCase();
				if (hst.contains(h)) {
					// ignore because in list
					return false;
				}
			}
		}
		return true;
	}

	public synchronized BlockingQueue<BotLink> get() {
		return queue;
	}

	/**
	 * Map robots data by hostname.
	 * 
	 * @return
	 */
	public synchronized Map<String, RobotsInfo> robots() {
		return robotsData;
	}

	/**
	 * Poison the queue with a bad link so processing can continue
	 * 
	 * @return
	 */
	public synchronized void poison() {
		queue.add(new BotLink());
	}

	public synchronized Map<BotLink, LinkProcessStatus> processed() {
		return processed;
	}

	public synchronized boolean hasproc(final BotLink link) {
		return (this.processed.get(link) != null);
	}

	public synchronized boolean proc(final BotLink link, final LinkProcessStatus status) {
		if (this.processed.get(link) == null) {
			this.processed.put(link, status);
			return true;
		} else {
			return false;
		}
	}

	public synchronized boolean proc(final BotLink link) {
		return proc(link, new LinkProcessStatus());
	}

	public int incConsumed() {
		return this.linksConsumed.incrementAndGet();
	}

	public void launchReportSystem() {
		// Run until end of program
		final DatabaseReport report = new DatabaseReport();
		final Thread t = new Thread(report);
		t.setName(t.getName() + "-ReportThread");
		t.setDaemon(false);
		t.start();
	}

	public class DatabaseReport implements Runnable {
		@Override
		public void run() {
			final String NL = System.getProperty("line.separator");
			while (true) {
				final StringBuffer buf = new StringBuffer();
				buf.append(NL);
				buf.append(" + ====// Database Processing Report === +").append(NL);
				buf.append(" + Active Thread Count : " + Thread.activeCount()).append(NL);
				buf.append(" + Size of link processing queue (waiting to process) : " + queue.size()).append(NL);
				buf.append(" + Have already processed : " + processed.size()).append(NL);
				buf.append(" + Links Consumed : " + linksConsumed).append(NL);
				buf.append(" + Links processed : ");
				int i = 0;
				// Loop through the bot links
				for (final BotLink l : processed.keySet()) {
					final Object o = processed.get(l);
					if (o != null) {
						buf.append(l);
						buf.append(",");
						i++;
						if (i >= 20) {
							break;
						}
					} // End of the if //
				} // End of the for //
				buf.append(NL);

				final double megabytes = 1024.0 * 1024.0;
				// Get current size of heap in bytes
				final double heapSize = Runtime.getRuntime().totalMemory() / megabytes;

				// Get maximum size of heap in bytes. The heap cannot grow beyond this size.
				// Any attempt will result in an OutOfMemoryException.
				final double heapMaxSize = Runtime.getRuntime().maxMemory() / megabytes;

				// Get amount of free memory within the heap in bytes. This size will increase
				// after garbage collection and decrease as new objects are created.
				final double heapFreeSize = Runtime.getRuntime().freeMemory() / megabytes;

				final String heap = String.format(
						" * Heap Memory curSize=%.2f MB / totalMax=s%.2f MB / curFree=%.2f MB", heapSize, heapMaxSize,
						heapFreeSize);
				buf.append(heap).append(NL);
				buf.append(" * ====// END OF = Database Processing Report === +").append(NL);
				logger.info(buf.toString());
				// Delay for a minute before next report
				try {
					Thread.sleep(2 * 40 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} // End of the while //
		} // End of the method //
	} // End of the class //

} // End of the class //
