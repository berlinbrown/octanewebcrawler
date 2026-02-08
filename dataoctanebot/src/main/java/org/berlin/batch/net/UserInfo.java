package org.berlin.batch.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
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
import org.berlin.batch.bean.BotDataUser;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserInfo {

	public static final String USER_AGENT = "Mozilla/5.0 (compatible; octanebot/1.0; http://code.google.com/p/octane-crawler/)";
	private static final Logger logger = LoggerFactory.getLogger(UserInfo.class);

	public synchronized BotDataUser connect(final String queryUserId) {

		InputStream instream = null;
		JsonNode rootNode = null;
		try {
			final URIBuilder builder = new URIBuilder();

			// Example query string:
			// ?max_id=305342403513044991&q=obama&lang=en&count=24&include_entities=1&result_type=mixed
			builder.setScheme("https").setHost("api.twitter.com").setPath("/1/users/show.json");
			builder.setParameter("user_id", queryUserId);

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

					if (rootNode != null) {
						if (rootNode.get("id_str") == null) {
							return null;
						}
						final String idstr = rootNode.get("id_str").asText();
						final String screenName = rootNode.get("screen_name").asText();
						final long followers = rootNode.get("followers_count").asLong();
						final BotDataUser user = new BotDataUser();
						user.setFollowersCount(followers);
						user.setMessageUserId(new BigInteger(idstr));
						user.setScreenName(screenName);
						return user;
					} /// End of if //
				} // End of - instream ///
			} // End of the if /
			Thread.sleep(100);
		} catch (final Exception e) {
			logger.error("Error at query user info", e);
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

} // End of the class //
