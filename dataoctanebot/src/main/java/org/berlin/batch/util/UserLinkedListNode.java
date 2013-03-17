package org.berlin.batch.util;

import java.util.ArrayList;
import java.util.List;

import org.berlin.batch.bean.BotDataUser;

public class UserLinkedListNode {

	private UserLinkedListNode next;
	private BotDataUser userData;
	private GenericTree treeData;
	
	public UserLinkedListNode(final BotDataUser user, UserLinkedListNode n) {
		this.userData = user;
		this.setNext(n);	
	} // End of the method //
	
	public UserLinkedListNode() {
		super();
	} // End of the method //

	public GenericTree newtree() {
		// OK to create an empty linked list node //
		this.treeData = new GenericTree(new UserLinkedListNode());
		return treeData;
	}
	public GenericTree tree() {
		return treeData;
	}
	
	public void setNext(final UserLinkedListNode n) {
		this.next = n;				
	} // End of the method //

	/**
	 * @return the userData
	 */
	public BotDataUser getUserData() {
		return userData;
	}

	/**
	 * @param userData the userData to set
	 */
	public void setUserData(BotDataUser userData) {
		this.userData = userData;
	}
	
	public void print(final StringBuffer buf) {
		if (this.next != null) {			
			buf.append(String.format("[%s/%s] -> ", this.userData.getMessageUserId(), this.userData.getScreenName()));
			this.next.print(buf);
		} else {
			if (this.userData != null) {
				buf.append(String.format("[%s/%s]", this.userData.getMessageUserId(), this.userData.getScreenName()));
			}
		}
	}
	
	public List<UserLinkedListNode> list() {
		final List<UserLinkedListNode> list = new ArrayList<UserLinkedListNode>();
		return this.list(list);
	}
	
	public List<UserLinkedListNode> list(final List<UserLinkedListNode> list) {
		if (this.next != null) {
			list.add(this.next);
			this.next.list(list);
		} 
		return list;
	}

	/**
	 * @return the next
	 */
	public UserLinkedListNode getNext() {
		return next;
	}
		
} // End of the class //
