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
import org.berlin.crawl.error.CrawlerError;
import org.berlin.crawl.util.OctaneCrawlerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RobotsConnector {

	private static final Logger logger = LoggerFactory.getLogger(WebConnector.class);	
	private static final String NL = System.getProperty("line.separator");
	
	private URIBuilder lastURIBuilder = null;
	private HttpResponse response = null;
	
	public String connect(final String scheme, final String host) throws Exception {
		final URIBuilder builder = new URIBuilder();
		builder.setScheme(scheme);
		builder.setHost(host);
		builder.setPath("/robots.txt");		
		return this.connect(builder);
	} // End of the method //
	
	/**
	 * Connect to robots.txt file.
	 * 
	 * On error, close inputstream, return empty document.
	 * 
	 * @param builder
	 * @return
	 * @throws Exception
	 */
	protected synchronized String connect(final URIBuilder builder) throws Exception {
		this.lastURIBuilder = builder;
		InputStream instream = null;
		try {						
			logger.info("Attempting request : " + builder.toString());
			final HttpParams params = new BasicHttpParams();
			final HttpProtocolParamBean paramsBean = new HttpProtocolParamBean(params);
			paramsBean.setUserAgent(OctaneCrawlerConstants.USER_AGENT);
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
				instream = entity.getContent();
				if (instream != null) {
					final StringBuffer document = new StringBuffer();
					final BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
					String line = "";
					while ((line = reader.readLine()) != null) {
						document.append(line);
						document.append(NL);
					} // End of the while //
					return document.toString();
				} // End of - instream ///
			} // End of the if /
			Thread.sleep(100);
		} catch (final Exception e) {
			logger.error("Error at robots connect", e);
			throw new CrawlerError("Error at connect", e);
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
	 * @return the lastURIBuilder
	 */
	public URIBuilder getLastURIBuilder() {
		return lastURIBuilder;
	}

	/**
	 * @return the response
	 */
	public HttpResponse getResponse() {
		return response;
	}
	
} // End of the class //
