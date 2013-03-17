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
package org.berlin.crawl.net;

/*
 * Octane crawler is a simple web crawler in Java.  All open with a liberal license.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParamBean;
import org.berlin.crawl.bean.BotLink;
import org.berlin.crawl.bom.LinkProcessQueueDatabase;
import org.berlin.crawl.error.CrawlerError;
import org.berlin.crawl.util.OctaneCrawlerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebConnector {
	
	public static final int LINK_PROCESS_DELAY = 200;
	public static final String USER_AGENT = OctaneCrawlerConstants.USER_AGENT;
	
	private static final String NL = System.getProperty("line.separator");
	
	private static final Logger logger = LoggerFactory.getLogger(WebConnector.class);			
	private final LinkProcessQueueDatabase db;
	
	private HttpResponse response; 
	
	public WebConnector(final LinkProcessQueueDatabase db) {
		this.db = db;
	}
	
	public synchronized String connect(final BotLink blink, final URIBuilder builder) throws Exception {	
		InputStream instream = null;
		try {						
			logger.info("!* Attempting download and connect request : " + builder.toString());
			final HttpParams params = new BasicHttpParams();
			final HttpProtocolParamBean paramsBean = new HttpProtocolParamBean(params);
			paramsBean.setUserAgent(USER_AGENT);
			// Set this to false, or else you'll get an
			// Expectation Failed: error
			paramsBean.setUseExpectContinue(false);

			final URI uri = builder.build();
			final HttpClient httpclient = new DefaultHttpClient();
			final HttpGet httpget = new HttpGet(uri);
			httpget.setParams(params);
			
			// Connect //
			final HttpResponse response = httpclient.execute(httpget);
			final HttpEntity entity = response.getEntity();
			
			this.response = response;
			if (response != null) {				
				if (response.getStatusLine() != null) {
					if (response.getStatusLine().getStatusCode() != 200) {
						// Log the error line
						logger.error("Invalid status code - "   + response.getStatusLine().getStatusCode());
						throw new CrawlerError("Invalid status code - " + response.getStatusLine().getStatusCode());
					}
				}
			}
			
			if (entity != null) {
				blink.setStatusline(String.valueOf(response.getStatusLine()));
				blink.setCode(response.getStatusLine().getStatusCode());
				instream = entity.getContent();
				if (instream != null) {
					final StringBuffer document = new StringBuffer();
					final BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
					String line = "";
					while ((line = reader.readLine()) != null) {
						document.append(line);
						document.append(NL);
					} // End of the while //
					
					db.proc(blink);
					Thread.sleep(LINK_PROCESS_DELAY);
					
					return document.toString();
				} // End of - instream ///
			} // End of the if /
						
		} catch (final Throwable e) {
			logger.error("Error at connect to LINK", e);
			throw new CrawlerError("Error at connect to LINK", e);
		} finally {
			try {
				if (instream != null) {
					instream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} // End of the try - catch block //
		return null;
	} // End of the method //

	/**
	 * @return the response
	 */
	public HttpResponse getResponse() {
		return response;
	}
	
} // End of the class //
