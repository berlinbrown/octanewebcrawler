package org.berlin.batch.bom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.berlin.batch.bean.BotDataUser;
import org.berlin.batch.util.GenericTree;
import org.berlin.batch.util.UserLinkedListNode;

public class UserConnector {

	/**
	 * This data structure is a link between a user and other users.
	 */
	private Map<BotDataUser, Set<BotDataUser>> connections = new HashMap<BotDataUser, Set<BotDataUser>>();
	
	int validSize = 0;
	
	/**
	 * Loop through list of trees, connect a screenName/id pair to
	 * a list of other users.
	 *  
	 * @param listOfTrees
	 */
	public void build(final List<GenericTree> listOfTrees) {
		// Loop through the trees		
		for (final GenericTree tree : listOfTrees) {			
			build(tree);
		} // End of the for //					
		System.out.println("Size: " + connections.size() + " // " + validSize);
		
		for (final BotDataUser k : connections.keySet()) {
			final Set<BotDataUser> s = connections.get(k);
			for (final BotDataUser b : s) {
				System.out.println("key=" + k + "::: " + b);
			}
		}
		
	} // End of the method //
	
	public void build(final GenericTree tree) {
		if (tree.root() != null) {
			// Extract the elements
			final List<UserLinkedListNode> nodes = tree.root().list();				
			if (nodes != null) {										
				if (nodes.size() > 1) {
					validSize++;
					final UserLinkedListNode rootKey = nodes.get(0);
					Set<BotDataUser> set = (Set<BotDataUser>) connections.get(rootKey.getUserData());
					if (set == null) {
						set = new HashSet<BotDataUser>();
						set.add(rootKey.getUserData());
						connections.put(rootKey.getUserData(), set);
					} else {
						for (int i = 1; i < nodes.size(); i++) {
							set.add(nodes.get(i).getUserData());
							if (rootKey.tree() != null) {
								build(rootKey.tree());
							}
						} // Iterate through all of the nodes in the list //
					} // End of the if //					
					
					// Also build a data structure, key links to nodes.
					// Now build the other links to the nodes
					for (int i = 1; i < nodes.size(); i++) {
						final BotDataUser linkedUser = nodes.get(i).getUserData();
						if (linkedUser != null) {
							set = (Set<BotDataUser>) connections.get(linkedUser);
							if (set == null) {
								set = new HashSet<BotDataUser>();
								set.add(linkedUser);
								connections.put(linkedUser, set);
							} else {
								// Otherwise
								set.add(linkedUser);
							} // End of the if //													
						} // End of if - check linked user //
					} // End of the for //
					
				} // End of the if //													
			} /// End of the if - nodes //
		} // End of the if //			
	} // End of the method //
	
} // End of the class //
