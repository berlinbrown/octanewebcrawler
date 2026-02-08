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
package org.berlin.crawl.parse;

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
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.berlin.crawl.bean.BotLink;
import org.berlin.crawl.bean.RobotsInfo;

public class RobotsParser {

	public RobotsInfo parse(final BotLink link, final String robotsTxtArg) {
		final RobotsInfo robotsData = new RobotsInfo();
		if (robotsTxtArg == null) {
			// Return with invalid info //
			robotsData.setHost(link.getHost());
			robotsData.setRobotsText(null);
			return robotsData;
		}
		final String robotsTxt = robotsTxtArg.trim();
		final ByteArrayInputStream is = new ByteArrayInputStream(robotsTxt.getBytes());
		final BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String currentAgent = "";
		try {
			robotsData.setRobotsText(robotsTxt);
			robotsData.setHost(link.getHost());
			String line = null;
			while ((line = in.readLine()) != null) {
				final String l = line.trim();
				if (l.length() > 2) {
					// Mixed case, tag, white-space and then any other characters
					final Pattern p = Pattern.compile("(?i)(disallow|sitemap|user-agent):\\s*(.*)");
					final Matcher m = p.matcher(l);
					while (m.find()) {
						if (m.groupCount() == 2) {
							final String key = m.group(1).trim().toLowerCase();
							final String val = m.group(2).trim();
							// Set a new current user agent //
							if (key.equalsIgnoreCase("user-agent")) {
								// E.g. '*'
								currentAgent = val;
								currentAgent = currentAgent + ".disallow";
							} else if (key.equalsIgnoreCase("sitemap")) {
								robotsData.addSitemap(val);
							} else if (key.equalsIgnoreCase("disallow")) {
								// We can add 'disallow' to the current agent set
								robotsData.add(currentAgent, val);
							} // End of the if //
						} // End of the if //
					} // End of the while //
				} // End of the if //
			} // End of the while //
		} catch (final Exception e) {
			e.printStackTrace();
		} // End of the try - catch //
		return robotsData;
	} // End of the method //

} // End of the class //
