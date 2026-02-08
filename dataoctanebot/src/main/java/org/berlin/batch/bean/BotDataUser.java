package org.berlin.batch.bean;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "bot_data_user")
public class BotDataUser {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "assignedGen")
	private Long id;

	private Date createdAt;
	private Long followersCount;
	private BigInteger messageUserId;
	private String screenName;

	/**
	 * A user may have many messages.
	 */
	@OneToMany(mappedBy = "bot_data_user", targetEntity = BotDataUser.class)
	@JoinTable(name = "bot_data_messages", joinColumns = {@JoinColumn(name = "user_id")})
	private Set<BotDataMessages> messages = new HashSet<BotDataMessages>();

	public String toString() {
		return String.format("[DataUser : name=%s | userId=%s %s]", screenName, this.messageUserId,
				this.followersCount);
	} // End of the method //

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id
	 *            the id to set
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
	 * @param createdAt
	 *            the createdAt to set
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	/**
	 * @return the followersCount
	 */
	public Long getFollowersCount() {
		return followersCount;
	}
	/**
	 * @param followersCount
	 *            the followersCount to set
	 */
	public void setFollowersCount(final Long followersCount) {
		this.followersCount = followersCount;
	}
	/**
	 * @return the messageUserId
	 */
	public BigInteger getMessageUserId() {
		return messageUserId;
	}
	/**
	 * @param messageUserId
	 *            the messageUserId to set
	 */
	public void setMessageUserId(BigInteger messageUserId) {
		this.messageUserId = messageUserId;
	}
	/**
	 * @return the screenName
	 */
	public String getScreenName() {
		return screenName;
	}
	/**
	 * @param screenName
	 *            the screenName to set
	 */
	public void setScreenName(String screenName) {
		this.screenName = screenName;
		if (this.screenName != null) {
			this.screenName = this.screenName.replaceAll("'", "");
		}
	}
	/**
	 * @return the messages
	 */
	public Set<BotDataMessages> getMessages() {
		return messages;
	}
	/**
	 * @param messages
	 *            the messages to set
	 */
	public void setMessages(final Set<BotDataMessages> messages) {
		this.messages = messages;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + ((followersCount == null) ? 0 : followersCount.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((messageUserId == null) ? 0 : messageUserId.hashCode());
		result = prime * result + ((screenName == null) ? 0 : screenName.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BotDataUser other = (BotDataUser) obj;
		if (createdAt == null) {
			if (other.createdAt != null)
				return false;
		} else if (!createdAt.equals(other.createdAt))
			return false;
		if (followersCount == null) {
			if (other.followersCount != null)
				return false;
		} else if (!followersCount.equals(other.followersCount))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (messageUserId == null) {
			if (other.messageUserId != null)
				return false;
		} else if (!messageUserId.equals(other.messageUserId))
			return false;
		if (screenName == null) {
			if (other.screenName != null)
				return false;
		} else if (!screenName.equals(other.screenName))
			return false;
		return true;
	}

} // End of the class //
