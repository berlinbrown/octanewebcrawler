package org.berlin.batch.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.berlin.batch.bean.BotDataMessages;
import org.berlin.batch.bean.BotDataUser;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotMessagesDAO {

	private static final Logger logger = LoggerFactory.getLogger(BotMessagesDAO.class);
	
	private int minFollowersCountQuery = 50000;
	private int maxResultsTopUsers = 6;
	
	public class UserIdPair {
		private BigInteger id;
		private String screenName;
	} // End of the class //
	
	public List<BotDataUser> findTopUsers(final Session session) {
		// Use the hibernate template class		
		Transaction transaction = null;
		final List<BotDataUser> users = new ArrayList<BotDataUser>();
		try {						
			transaction = session.beginTransaction();			
			logger.info("Attempting to find top users");			
			final List list = session.createQuery("from BotDataUser as user where followers_count > " + minFollowersCountQuery + " order by followers_count desc")					
					.list();			
			BotDataUser user = null;
			if (list != null) {
				for (final Object obj : list) {
					user = (BotDataUser) obj;
					users.add(user);
				}
			} // End of the if //																
			transaction.commit();
			session.flush();
		} catch (final Exception e) {
			logger.error("Error at add", e);			
			if (transaction != null) {
				transaction.rollback();
			}
			throw new RuntimeException("Error from create");
		} finally {			
		}
		return users;
	} // End of the method //

	
	public void create(final Session session, final BotDataUser userObj, final BotDataMessages message) {
		// Use the hibernate template class		
		Transaction transaction = null;
		int status = -1;
		try {		
			status = 1001;
			transaction = session.beginTransaction();			
			logger.info("Attempting to query and verify for screename=" + userObj.getScreenName());						
			final List list = session.createQuery("from BotDataUser as user where screen_name = '"+userObj.getScreenName()+"'")
					//.setMaxResults(maxResultsTopUsers)
					.list();
			status = 1009;
			BotDataUser user = null;
			if (list != null) {
				for (final Object obj : list) {
					user = (BotDataUser) obj;
				}
			} // End of the if //
			
			status = 1002;
			if (user == null) {
				user = userObj;
			}
			user.setCreatedAt(new Date());
			session.saveOrUpdate(user);				
			message.setCreatedAt(new Date());			
			message.setUser(user);
			message.setUserId(user.getId());
			status = 1003;
			
			final Set<BotDataMessages> set = new HashSet<BotDataMessages>();
			set.add(message);			
			session.saveOrUpdate(message);
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
	
} // End of the class //
