package org.berlin.batch.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParamBean;
import org.berlin.logs.scan.NullRef;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FriendsRequest {

	public static final String USER_AGENT = "Mozilla/5.0 (compatible; octanebot/1.0; http://code.google.com/p/octane-crawler/)";
	
	private String nextRequestURI = "";
	private String refreshURI = "";	
	
	/**
	 * Max number from friends.
	 * Default 4-8
	 */
	private int maxNumberResults = 4;
	
	private static final Logger logger = LoggerFactory.getLogger(FriendsRequest.class);	
	
	public synchronized JsonNode connect(final String queryUserId) {		
		// We are setting this to synchornized to avoid
		// multiple requests to twitter
		
		// See info on the twitter api from
		// https://dev.twitter.com/
		// Twitter Rules:
		// If your application will eventually need more than 1 million user tokens, or you expect your embedded Tweets 
		// and embedded timelines to exceed 10 million daily impressions, you will need to talk to us directly about 
		// your access to the Twitter API as you may be subject to additional terms. Furthermore, applications that 
		// attempt to replicate Twitter's core user experience (as described in Section I.5 below) will need our 
		// permission to have more than 100,000 user tokens and are subject to additional terms.
		
		InputStream instream = null;
		JsonNode rootNode = null;	
		try {			
			final OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
	                // the consumer key of this app
	                "2werersdfatsdwfsidfVsdlfsdxYmj6OhUDdt5xoA",
	                // the consumer secret of tfhis app
	                "EzzfDiA9SGaD6CIZkvyzl68y87tfKDZUAidYmmwewsefsdfsdfs0g0");
			/*
	        final OAuthProvider provider = new DefaultOAuthProvider(
	                "http://twitter.com/oauth/request_token",
	                "http://twitter.com/oauth/access_token",
	                "http://twitter.com/oauth/authorize");
	                */			
			final OAuthProvider provider = new DefaultOAuthProvider(
					"https://api.twitter.com/oauth/request_token",
					"https://api.twitter.com/oauth/access_token",
					"https://api.twitter.com/oauth/authorize");
			
	        /****************************************************
	         * The following steps should only be performed ONCE
	         ***************************************************/
						
	        /****************************************************
	         * The following steps are performed everytime you
	         * send a request accessing a resource on Twitter
	         ***************************************************/

	        // If not yet done, load the token and token secret for
	        // the current user and set them
	        /*
	        final String accessToken = consumer.getToken();
	        final String tokenSecret = consumer.getTokenSecret();
	        */we
	        final String accessToken = "2asdasd28wrwer246769-6jEUFqJo4ikKvWfEBX9ImsdfsdfsdfsdflsdfjlksjdiujewoiruweKYdckLykBuQARyzYIIX";
	        final String tokenSecret = "0U1CBsdfsdfsdfVr31d2e14389uy9sdfsdf889jhoijosdfsdfsf";
	        
	        // Set token with secret [ ready for request ]
	        consumer.setTokenWithSecret(accessToken, tokenSecret);
			
			final URIBuilder builder = new URIBuilder();
			
			// Example query string: ?max_id=305342403513044991&q=obama&lang=en&count=24&include_entities=1&result_type=mixed			
			builder.setScheme("https").setHost("api.twitter.com").setPath("/1/friends/ids.json");
			if (NullRef.hasValue(this.nextRequestURI)) {
				builder.setQuery(this.nextRequestURI.replaceAll("\\?", ""));
				logger.info("Making nextRequestURI ...moving to next : " + this.nextRequestURI);
			} else if (NullRef.hasValue(this.refreshURI)) {
				builder.setQuery(this.refreshURI);
			} else {
				builder.setParameter("user_id", queryUserId)
			    	.setParameter("cursor", "-1")			    	
			    	.setParameter("stringify_ids", "true")
			    	.setParameter("count", String.valueOf(maxNumberResults));
			} // End of the if - else //			
			logger.info("Attempting request : " + builder.toString());
			
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
			
			// Now use oauth for request //
			consumer.sign(httpget);
			
			// Connect //
			final HttpResponse response = httpclient.execute(httpget);
			final HttpEntity entity = response.getEntity();
						
			if (entity != null) {
				instream = entity.getContent();
				if (instream != null) {
					final StringBuffer document = new StringBuffer();														
					final BufferedReader reader = new BufferedReader(new InputStreamReader(instream));              
			        String line = "";	        
			        while ((line = reader.readLine()) != null) {
			        	document.append(line);
			        } // End of the while //			        			        
			        final ObjectMapper mapper = new ObjectMapper();	        
			        rootNode = mapper.readValue(document.toString(), JsonNode.class);			        			        
				} // End of - instream ///
			} // End of the if //
			logger.info("Status line from request : " + response.getStatusLine());
			Thread.sleep(150);
		} catch(final Exception e) {
			logger.error("Error at friends request : ", e);			
		} finally {			
			try {
				if (instream != null) {
					instream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}			
		} // End of the try - catch block //		
		return rootNode;
	} // End of the method //

	/**
	 * @param nextRequestURI the nextRequestURI to set
	 */
	public void setNextRequestURI(String nextRequestURI) {
		this.nextRequestURI = nextRequestURI;
	}

	/**
	 * @param refreshURI the refreshURI to set
	 */
	public void setRefreshURI(String refreshURI) {
		this.refreshURI = refreshURI;
	}
	
} // End of the class //
