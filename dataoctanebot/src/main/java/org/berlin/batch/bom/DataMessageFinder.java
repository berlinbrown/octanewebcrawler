package org.berlin.batch.bom;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.berlin.batch.bean.BotDataMessages;
import org.berlin.batch.bean.BotDataUser;
import org.berlin.batch.dao.BotMessagesDAO;
import org.berlin.batch.net.QueryRequest;
import org.berlin.logs.scan.NullRef;
import org.codehaus.jackson.JsonNode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class DataMessageFinder {

	private static final Logger logger = LoggerFactory.getLogger(DataMessageFinder.class);

	/**
	 * With a higher number of followers, expect higher quality content.
	 */
	private int minNumberFollowers = 1600;

	/**
	 * Delay between connect requests.
	 */
	private int delayConnectRequest = 1300;

	/**
	 * Force the type of seed terms to use. 1,2,3 If '-1' then ignore the rule.
	 */
	private int forceRequestRule = -1;

	private int totalMessageBatchProcessed = 0;
	private int totalMessageBatchValid = 0;

	public void execute(final ApplicationContext ctx) {
		final int maxAllDayRun = 360;
		for (int i = 1; i <= maxAllDayRun; i++) {
			try {
				synchronized (ctx) {
					logger.info("+ LAUNCHING another job / runner.execute()");
					connectAndParse(ctx);
					logger.info("+! END job - postAnalysis : " + totalProcessedMessage());
				} // End of the sync block //
				Thread.sleep(1000 * 60 * 2);
			} catch (final Exception e) {
				e.printStackTrace();
			} // End of the try - catch //
		} // End of the for //
	} // end of the method //

	public void connectAndParse(final ApplicationContext ctx) {
		final BotMessagesDAO dao = new BotMessagesDAO();
		final SessionFactory sf = (SessionFactory) ctx.getBean("sessionFactory");
		Session session = sf.openSession();
		final int maxRuns = 3;
		final int maxNextPages = 6;
		ContinueRequestData nextRequestData = null;

		final List<String> ruleData = this.queryConnectRules();
		final int termsLength = ruleData.size();
		final int numberOfRequests = termsLength * maxRuns * maxNextPages;
		int operationsComplete = 0;
		int z = 0;
		for (final String term : ruleData) {
			z++;
			nextRequestData = null;
			for (int i = 1; i < maxRuns; i++) {
				nextRequestData = null;
				for (int j = 1; j <= maxNextPages; j++) {
					operationsComplete++;
					final double percComplete = (operationsComplete / (double) numberOfRequests) * 100.0;
					logger.info("At Data Runner : Inititiating log run : run=" + i);
					logger.info(String.format("===== PERCENT COMPLETE === : perc=>%.2f %%", percComplete));
					final QueryRequest req = new QueryRequest();
					if (nextRequestData != null) {
						req.setNextRequestURI(nextRequestData.nextRequestURI);
						req.setRefreshURI(nextRequestData.refreshURI);
					} // End of the if //
					final JsonNode results = req.connect(term);
					final int groupNextId = ((j * maxNextPages) * (z * termsLength) * (i * maxRuns) * 3) + (j * 10);
					nextRequestData = null;
					nextRequestData = this.parseResults(sf, term, dao, session, results, groupNextId);
					logger.info("At Data Runner : End of log run : run=" + i + " term=" + term.toUpperCase());
					try {
						Thread.sleep(delayConnectRequest);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					} // End of the try - catch //
					try {
						session.flush();
					} catch (final Exception e) {
						/// Clear the request data on error
						nextRequestData = null;
						logger.error("Error at save/flush", e);
						// On error, attempt to close the session and reopen //
						try {
							session.clear();
							// session.close();
						} catch (final Exception e2) {
							logger.error("Error at create message record{attempt to close and open}", e2);
						}
						continue;
					} // End of the try - catch //
				} // End of for //
			} // End of the for //
		} // End of the for //
		if (session != null) {
			// May not need to close the session
			session.close();
		} // End of the if //
	} // End of the method //

	public List<String> queryConnectRules() {
		// First, a random selection to determine which rule to use //
		final Random rand = new Random(System.currentTimeMillis());
		// 1 = backwards
		// 2 = random values
		// 3(default) = forward
		int rule = 3;
		final double r = rand.nextDouble();
		if (r >= 0.8) {
			rule = 1;
		} else if (r <= 0.32) {
			rule = 2;
		} else {
			rule = 3;
		} // End of the if - else //

		if (this.forceRequestRule != -1) {
			rule = this.forceRequestRule;
		}
		final List<String> ruleData = new ArrayList<String>();
		if (2 == rule) {
			// Reversed list //
			for (final String term : POPULAR_TERMS) {
				ruleData.add(term);
			} // End of the for //
			Collections.reverse(ruleData);
		} else if (1 == rule) {
			// Random list //
			final Random rand2 = new Random();
			for (String s : POPULAR_TERMS) {
				final int ni = rand2.nextInt(POPULAR_TERMS.length - 1);
				ruleData.add(POPULAR_TERMS[ni]);
			} // End of the for //
		} else {
			// Forward list //
			for (final String term : POPULAR_TERMS) {
				ruleData.add(term);
			} // End of the for //
		} // End of the if - else //
		return ruleData;
	} // End of the method //

	protected ContinueRequestData parseResults(final SessionFactory sf, final String queryTerm,
			final BotMessagesDAO dao, Session session, final JsonNode resultsRoot, final int offsetId) {
		String nextRequestURI = "";
		String refreshURI = "";
		final JsonNode results = resultsRoot.get("statuses");
		long followers = 0;
		final ContinueRequestData nextRequestData = new ContinueRequestData();

		int localMessageBatchProcessed = 0;
		int localMessageBatchValid = 0;

		if (results != null) {
			logger.info("Attempting parse results with queryTerm=" + queryTerm + " offsetId=" + offsetId);
			final JsonNode searchMetaData = resultsRoot.get("search_metadata");
			if (searchMetaData != null) {
				final JsonNode nextResultsNode = searchMetaData.get("next_results");
				if (nextResultsNode != null) {
					nextRequestURI = nextResultsNode.asText();
				} // End of the if //
				if (NullRef.hasValue(nextRequestURI)) {
					nextRequestData.nextRequestURI = nextRequestURI;
				}
			} // End of the if //
			for (int i = 0; i < results.size(); i++) {
				final JsonNode res = results.get(i);
				if (res != null) {
					final String text = res.get("text").asText();
					final JsonNode user = res.get("user");
					final String userMessageId = user.get("id").asText();
					boolean showText = true;
					String screenName = "";
					if (user != null) {
						screenName = user.get("screen_name").asText();
						final JsonNode followerCount = user.get("followers_count");
						if (followerCount != null) {
							followers = followerCount.asLong();
							if (followers <= minNumberFollowers) {
								showText = false;
							}
						} else {
							showText = false;
						} // End of if - follower count //
					} // End of user check
					final String createdAt = res.get("created_at").asText();
					final String messageId = res.get("id").asText();
					Date twitterDate = null;
					try {
						twitterDate = toDate(createdAt);
					} catch (ParseException e) {
						e.printStackTrace();
					} // End of the try - catch //
					if (text.trim().length() <= 32) {
						showText = false;
					} // End of the if //
						// Increment the number of messages attempted
					localMessageBatchProcessed++;
					this.totalMessageBatchProcessed++;
					// If show text is false, continue //
					if (!showText) {
						continue;
					} // End of the if //
						// Build user information //
					final BotDataUser userObj = new BotDataUser();
					userObj.setFollowersCount(followers);
					userObj.setMessageUserId(new BigInteger(userMessageId));
					userObj.setScreenName(screenName);
					// Build the text request //
					try {
						final BotDataMessages msg = new BotDataMessages();
						final int offset = (offsetId * 40) + i;
						// msg.setId(Long.valueOf(offset));
						msg.setFollowersCount(followers);
						msg.setMessage(text.trim());
						msg.setMessageCreatedAt(twitterDate);
						msg.setMessageCreatedAtStr(createdAt);
						msg.setMessageId(new BigInteger(messageId));
						msg.setScreenName(screenName);
						msg.setQuery(queryTerm);
						logger.info("At saveOrUpdate : " + msg);
						dao.create(session, userObj, msg);
						localMessageBatchValid++;
						this.totalMessageBatchValid++;
					} catch (final Exception e) {
						logger.error("Error at create message record", e);
						// On error, attempt to close the session and reopen //
						try {
							session.clear();
							// session.close();
						} catch (final Exception e2) {
							logger.error("Error at create message record{attempt to close and open}", e2);
						}
					} // End of the try catch //
				} // End of the if //
			} // End of the for //
		} // End of the if //
		final double percLocalProcessed = (localMessageBatchValid / (double) localMessageBatchProcessed) * 100.0;
		logger.info("// We are processing a message batch, localMessageBatchProcessed=" + localMessageBatchProcessed
				+ String.format(" percent=%.2f %%", percLocalProcessed));
		// Attempt to make another request //
		return nextRequestData;
	} // End of the method //

	public String totalProcessedMessage() {
		final double percLocalProcessed = (totalMessageBatchValid / (double) totalMessageBatchProcessed) * 100.0;
		return "[TOTAL ATTEMPTS] processed a message batch, totalMessageBatchProcessed=" + totalMessageBatchProcessed
				+ String.format(" percent=%.2f %%", percLocalProcessed);
	}

	private class ContinueRequestData {
		private String nextRequestURI = "";
		private String refreshURI = "";
	}

	/**
	 * @param forceRequestRule
	 *            the forceRequestRule to set
	 */
	public void setForceRequestRule(int forceRequestRule) {
		this.forceRequestRule = forceRequestRule;
	}

	public static Date toDate(final String date) throws ParseException {
		final String TWITTER = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		final SimpleDateFormat sf = new SimpleDateFormat(TWITTER);
		sf.setLenient(true);
		return sf.parse(date);
	} // End of the method //

	/**
	 * Basic seed words.
	 */
	public static final String POPULAR_TERMS[] = {"government", "company", "woman", "problem", "family", "problem",
			"country", "important", "headquarters", "license", "grandfather", "justice", "entertainment", "tech",
			"crazy", "love", "hate", "breaking news", "headline news", "dates", "places", "rebel", "firefox", "games",
			"legalization", "Austrian", "capitalism", "destruction", "free market", "violence", "hoods", "chrome",
			"google glass", "playstation", "secure", "washington", "technology", "programming", "robots", "cyborg",
			"life", "obama", "news", "school", "science", "engineering", "data", "google", "math", "science", "english",
			"japan", "china", "android", "stocks", "bonds", "russia", "information", "planets", "whales", "diet",
			"health", "medicine", "cancer", "nationalized", "health care", "java", "microsoft", "bing", "xbox", "light",
			"new york", "PHYSICIAN", "placebo", "word", "music", "internet", "books", "learning", "violence", "war",
			"routine", "hiv", "Aspirin", "poetry", "church", "food", "water", "Jefferson", "constitution", "security",
			"century", "future", "north korea", "iran", "persian", "oscars", "argo", "nyse", "trends", "aliens", "war",
			"art", "drawing", "lessons", "greece", "london", "winter", "guns", "woman", "men", "korea", "international",
			"cnn", "mexico", "america", "Chesapeake", "think", "clinton", "important", "immigration", "economy",
			"liberals", "survivors", "boeing", "resources", "hamas", "wired", "airport", "conspiracy", "Nationalized",
			"health-care", "vote", "away", "reality", "instituted", "responsible", "parents", "providers", "corporate",
			"officers", "market", "computers", "country", "angry", "founders", "congress", "senator", "senate"};

} // End of the class //
