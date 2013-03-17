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
package org.berlin.crawl.bean;

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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="bot_crawler_links")
public class BotCrawlerError {

	@Id	
	@Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "assignedGen")
	private Long id;		
	private Date createdAt;

	private String url;
	private String host;
	private int status;	
	private String statusline;	
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
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	
	/**
	 * @return the statusline
	 */
	public String getStatusline() {
		return statusline;
	}
	/**
	 * @param statusline the statusline to set
	 */
	public void setStatusline(String statusline) {
		this.statusline = statusline;
	}
	
} // End of the class //
