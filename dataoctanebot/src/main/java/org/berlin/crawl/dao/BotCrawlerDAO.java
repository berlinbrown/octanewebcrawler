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
package org.berlin.crawl.dao;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.berlin.crawl.bean.BotCrawlerError;
import org.berlin.crawl.bean.BotCrawlerIgnore;
import org.berlin.crawl.bean.BotCrawlerLink;
import org.berlin.crawl.bean.BotSeed;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate data access layer for connecting to the bot crawler database. 
 * Store links and seed information.
 * 
 * @author bbrown
 *
 */
public class BotCrawlerDAO {

	private static final Logger logger = LoggerFactory.getLogger(BotCrawlerDAO.class);
	
	public List<BotCrawlerIgnore> findIgnores(final Session session) {
		// Use the hibernate template class		
		Transaction transaction = null;
		final List<BotCrawlerIgnore> ignores = new ArrayList<BotCrawlerIgnore>();
		try {						
			transaction = session.beginTransaction();
			logger.info("Attempting to find ignores");			
			final List list = session.createQuery("from BotCrawlerIgnore as ignore").list();			
			BotCrawlerIgnore ig = null;
			if (list != null) {
				for (final Object obj : list) {
					ig = (BotCrawlerIgnore) obj;
					ignores.add(ig);
				}
			} // End of the if //																
			transaction.commit();
			session.flush();
		} catch (final Exception e) {
			logger.error("Error at add", e);			
			if (transaction != null) {
				transaction.rollback();
			}			
		} finally {			
		}
		return ignores;
	} // End of the method //
	
	public List<BotSeed> findSeedRequests(final Session session) {
		// Use the hibernate template class		
		Transaction transaction = null;
		final List<BotSeed> seeds = new ArrayList<BotSeed>();
		try {						
			transaction = session.beginTransaction();			
			logger.info("Attempting to find top seed requests");			
			final List list = session.createQuery("from BotSeed as seed").list();			
			BotSeed seed = null;
			if (list != null) {
				for (final Object obj : list) {
					seed = (BotSeed) obj;
					seeds.add(seed);
				}
			} // End of the if //																
			transaction.commit();
			session.flush();
		} catch (final Exception e) {
			logger.error("Error at add", e);			
			if (transaction != null) {
				transaction.rollback();
			}			
		} finally {			
		}
		return seeds;
	} // End of the method //
	
	public List<String> findHosts(final Session session) {
		// Use the hibernate template class		
		Transaction transaction = null;
		final List<String> seeds = new ArrayList<String>();
		try {						
			transaction = session.beginTransaction();			
			logger.info("Attempting to find top seed requests");			
			final List list = session.createQuery("select distinct host from BotCrawlerLink o ").list();			
			String seed = null;
			if (list != null) {
				for (final Object obj : list) {
					seed = (String) obj;
					seeds.add(seed);
				}
			} // End of the if //																
			transaction.commit();
			session.flush();
		} catch (final Exception e) {
			logger.error("Error at add", e);			
			if (transaction != null) {
				transaction.rollback();
			}			
		} finally {			
		}
		return seeds;
	} // End of the method //

	public List<Long> countLinks(final Session session) {
		// Use the hibernate template class		
		Transaction transaction = null;
		final List<Long> seeds = new ArrayList<Long>();
		try {						
			transaction = session.beginTransaction();			
			logger.info("Attempting to find top seed requests");			
			final List list = session.createQuery("select count(*) from BotCrawlerLink o ").list();			
			Long i = null;
			if (list != null) {
				for (final Object obj : list) {
					i = (Long) obj;
					seeds.add(i);
				}
			} // End of the if //																
			transaction.commit();
			session.flush();
		} catch (final Exception e) {
			logger.error("Error at add", e);			
			if (transaction != null) {
				transaction.rollback();
			}			
		} finally {			
		}
		return seeds;
	} // End of the method //
	
	public void createSeed(final Session session, final BotSeed seed) {
		// Use the hibernate template class		
		Transaction transaction = null;
		int status = -1;
		try {		
			status = 1001;
			transaction = session.beginTransaction();																	
			seed.setCreatedAt(new Date());												
			session.saveOrUpdate(seed);
			status = 1004;
			// End of work //
			transaction.commit();
			session.flush();
		} catch (final Exception e) {
			logger.error("Error at add <status: " + status + ">>", e);			
			if (transaction != null) {
				transaction.rollback();
			}
			throw new RuntimeException("Error from create");
		} finally {			
		}
	} // End of the method //	
	
	public void createLink(final Session session, final BotCrawlerLink link) {
		// Use the hibernate template class		
		Transaction transaction = null;
		int status = -1;
		try {		
			status = 1001;
			transaction = session.beginTransaction();																	
			link.setCreatedAt(new Date());												
			session.saveOrUpdate(link);
			status = 1004;
			// End of work //
			transaction.commit();
			session.flush();
		} catch (final Exception e) {
			logger.error("Error at add <status: " + status + ">>", e);			
			if (transaction != null) {
				transaction.rollback();
			}
			throw new RuntimeException("Error from create");
		} finally {			
		}
	} // End of the method //
	
	public void createError(final Session session, final BotCrawlerError err) {
		// Use the hibernate template class		
		Transaction transaction = null;
		int status = -1;
		try {		
			status = 1001;
			transaction = session.beginTransaction();																	
			err.setCreatedAt(new Date());												
			session.saveOrUpdate(err);
			status = 1004;
			// End of work //
			transaction.commit();
			session.flush();
		} catch (final Exception e) {
			logger.error("Error at add <status: " + status + ">>", e);			
			if (transaction != null) {
				transaction.rollback();
			}
			throw new RuntimeException("Error from create");
		} finally {			
		}
	} // End of the method //
	
	public List<Object []> findTopHosts(final Session session) {
		// Use the hibernate template class		
		Transaction transaction = null;
		final List<Object []> seeds = new ArrayList<Object []>();
		try {						
			transaction = session.beginTransaction();			
			logger.info("Attempting to find top seed requests");			
			final List list = session.createQuery("select o.host, count(o.host) as theCount from BotCrawlerLink o group by o.host order by theCount").list();			
			Object [] seed = null;
			if (list != null) {
				for (final Object obj : list) {
					seed = (Object []) obj;
					seeds.add(seed);
				}
			} // End of the if //																
			transaction.commit();
			session.flush();
		} catch (final Exception e) {
			logger.error("Error at add", e);			
			if (transaction != null) {
				transaction.rollback();
			}			
		} finally {			
		}
		return seeds;
	} // End of the method //
	
} // End of the class //
