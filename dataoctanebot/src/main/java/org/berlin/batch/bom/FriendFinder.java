package org.berlin.batch.bom;

import java.util.ArrayList;
import java.util.List;

import org.berlin.batch.bean.BotDataUser;
import org.berlin.batch.dao.BotMessagesDAO;
import org.berlin.batch.net.FriendsRequest;
import org.berlin.batch.net.UserInfo;
import org.berlin.batch.util.GenericTree;
import org.berlin.batch.util.UserLinkedListNode;
import org.codehaus.jackson.JsonNode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Build a graph, find friends of friends.
 * 
 * @author bbrown 
 */
public class FriendFinder {
	
	private static final Logger logger = LoggerFactory.getLogger(FriendFinder.class);
	
	private final FriendFindStats stats;
	
	/**
	 * Levels deep in the friend find.
	 * Default value 3. 
	 */
	private int maxLevelsDeep = 3;

	/**
	 * Max number of new user node instances.
	 * Default 20-80. 
	 */
	private final int maxNodeInstances = 140;	
	private final int quickBreakTopUsers = 10;
	
	private int nodeInstances = 0;
	
	public FriendFinder(final FriendFindStats stats) {
		this.stats = stats; 
	}
	
	public void find(final ApplicationContext ctx) {
		logger.info("Launching find friend of a friend");
		final BotMessagesDAO dao = new BotMessagesDAO();		
		final SessionFactory sf = (SessionFactory) ctx.getBean("sessionFactory");
		final Session session = sf.openSession();
		final List<BotDataUser> listTopFromDB = dao.findTopUsers(session);
		if (session != null) {
			// May not need to close the session
			session.close();
		} // End of the if //		
		int i = 0;
		
		final List<GenericTree> listOfTrees = new ArrayList<GenericTree>();
		// Session closed, we have the list of contacts we need //				
		for (final BotDataUser userFromDB : listTopFromDB) {
			// Each user node at this level is completely distinct from the next
			// node in the iteration
			logger.info("Starting new data tree ... : " + userFromDB);
			final GenericTree tree = new GenericTree(new UserLinkedListNode());
			connect(userFromDB, tree, 0);
			// Print the tree //
			logger.info("End of data tree :>" + tree.report());			
			listOfTrees.add(tree);
			i++;
			if (i >= quickBreakTopUsers) {
				break;
			}
		} // End of the for //					
		logger.info("After DB analysis of users, we have trees: " + listOfTrees.size());
		 final UserConnector connector = new UserConnector();
		 connector.build(listOfTrees);
	} // End of the method //
	
	/**
	 * Iterate through list of queried ids.
	 */
	public void connect(final BotDataUser user, final GenericTree tree, final int levelDeep) {		
		if (tree == null) {			
			return;
		}
		final UserLinkedListNode activeTreeListNode = tree.root();
		if (activeTreeListNode == null) {			
			return;
		}
		if (levelDeep >= maxLevelsDeep) {
			// Exit at max levels 			
			return;
		}				
		if (nodeInstances >= maxNodeInstances) {			
			return;
		}		
		// We need a data structure to hold the list of ids
		// Then iterate through the list and build another list
		// Each node will have a 'tree'
		// We will only visit so many levels deep			
		activeTreeListNode.setUserData(user);
		UserLinkedListNode prev = activeTreeListNode;
		final FriendsRequest newRequest = new FriendsRequest();
		this.stats.net().incrementAndGet();
		logger.info("<<" + levelDeep + ">> Initiating network request, current total requests : " + this.stats.net().get() + " nodeInstances="+nodeInstances);
		logger.info("<<" + levelDeep + ">> Current user for investigation : " + user); 
		final JsonNode results = newRequest.connect(String.valueOf(user.getMessageUserId()));
		if (results != null) {
			final JsonNode ids = results.get("ids");
			if (ids != null && ids.size() > 0) {				
				for (int i = 0; i < ids.size(); i++) {
					final JsonNode id = ids.get(i);												
					final BotDataUser perUserInfo = queryUser(id);
					if (nodeInstances >= maxNodeInstances) {
						// Don't allow any more node creates;
						break;
					}					
					if (perUserInfo == null) {
						continue;
					}					
					final UserLinkedListNode next = new UserLinkedListNode(perUserInfo, null);
					prev.setNext(next);
					prev = next;
					nodeInstances++;
				} // End of the for //
			} // End of the if //
		} // End of the if //		
		// Iterate through all of the items in the list 
		// and build a list of lists //		
		// Print the message //		
		final StringBuffer buf = new StringBuffer();
		buf.append("(");
		activeTreeListNode.print(buf);
		buf.append(")");
		logger.info("We found a root list of items, descending into each item : " + buf.toString());									
		try {
			Thread.sleep(800);
		} catch(final Exception e) {
			logger.error("Error At Connect Find Friends", e);
		} // End of the try - catch //
		
		final List<UserLinkedListNode> curLevelList = activeTreeListNode.list();
		logger.info("Size of current level, number of nodes in set : " + curLevelList.size());
		for (final UserLinkedListNode n : curLevelList) {			
			// Recurse so many levels deep //
			// Create the tree //	
			logger.info("Connecting to new level for user : " + n.getUserData());
			this.connect(n.getUserData(), n.newtree(), levelDeep+1);
		} // End of the for //
	} // End of the method //
	
	public BotDataUser queryUser(final JsonNode id) {
		 if (id == null) {
			 return null;
		 }
		 final UserInfo inf = new UserInfo();
		 final String strid = id.asText();
		 return inf.connect(strid);		 
	} // End of the method //
} // End of the class //
