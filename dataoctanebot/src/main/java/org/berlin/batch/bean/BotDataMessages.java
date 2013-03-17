package org.berlin.batch.bean;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

@Entity
@Table(name = "bot_data_messages")
public class BotDataMessages {
	
	@Id	
	private Long id;
	
	private Date createdAt;
	private String messageCreatedAtStr;
	private Date messageCreatedAt;
	private String message;
	private String screenName;
	private Long followersCount;
	private Long userId;
	private BigInteger messageId;	
	private String query;
	
	@ManyToOne
	@JoinColumn(name="user_id", insertable=true, updatable=true)	
	@Cascade(value = { org.hibernate.annotations.CascadeType.ALL })	
	private BotDataUser user;
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;		
	}
	/**
	 * @param id the id to set
	 */
	private void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the createdAt
	 */
	public Date getCreatedAt() {
		return createdAt;
	}
	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	/**
	 * @return the messageCreatedAtStr
	 */
	public String getMessageCreatedAtStr() {
		return messageCreatedAtStr;
	}
	/**
	 * @param messageCreatedAtStr the messageCreatedAtStr to set
	 */
	public void setMessageCreatedAtStr(String messageCreatedAtStr) {
		this.messageCreatedAtStr = messageCreatedAtStr;
	}
	/**
	 * @return the messageCreatedAt
	 */
	public Date getMessageCreatedAt() {
		return messageCreatedAt;
	}
	/**
	 * @param messageCreatedAt the messageCreatedAt to set
	 */
	public void setMessageCreatedAt(Date messageCreatedAt) {
		this.messageCreatedAt = messageCreatedAt;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
		if (this.message != null) {
			this.message = this.message.replaceAll("'", "");
		}
	}
	/**
	 * @return the followersCount
	 */
	public Long getFollowersCount() {
		return followersCount;
	}
	/**
	 * @param followersCount the followersCount to set
	 */
	public void setFollowersCount(final Long followersCount) {
		this.followersCount = followersCount;
	}
	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * @return the messageId
	 */
	public BigInteger getMessageId() {
		return messageId;
	}
	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(final BigInteger messageId) {
		this.messageId = messageId;
	}
	/**
	 * @return the user
	 */
	public BotDataUser getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(final BotDataUser user) {
		this.user = user;
	}
	/**
	 * @return the screenName
	 */
	public String getScreenName() {
		return screenName;
	}
	/**
	 * @param screenName the screenName to set
	 */
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}
	/**
	 * @param query the query to set
	 */
	public void setQuery(final String query) {
		this.query = query;
	}
	
	public String toString() {
		final String txt = this.message == null ? "" : (this.message.length()>=14?this.message.substring(0,12):this.message);
		return String.format("[query=%s messageId=%s]", this.query, txt);
	}

} // End of the class //
