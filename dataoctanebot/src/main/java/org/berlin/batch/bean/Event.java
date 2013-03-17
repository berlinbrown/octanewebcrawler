package org.berlin.batch.bean;

import java.util.Date;

public class Event {
	
	private Long id;
	private String message;
	private Date date;

	public Event() {
		// this form used by Hibernate
		this.date = new Date();
	}

	public Event(final String msg, final Date date) {
		// for application use, to create new events
		this.message = msg;
		this.date = date;
	}

	public String toString() {
		// Return a formatted string
		return String.format("[id=%s message=%s %s]", id, message, date);
	}
	
	public Long getId() {
		return id;
	}

	private void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String msg) {
		this.message = msg;
	}
} // End of the class 