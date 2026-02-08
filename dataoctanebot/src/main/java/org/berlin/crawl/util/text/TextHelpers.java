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
package org.berlin.crawl.util.text;

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

import org.berlin.crawl.bean.BotLink;

public class TextHelpers {

	public String baseDirectory(final BotLink link) {
		if (link == null) {
			// If link is null do not create //
			return null;
		}
		// Host + path
		final StringBuffer buf = new StringBuffer();
		// Replace all nonalphanumeric characters with a space
		final String h = link.getHost().trim().toLowerCase().replaceAll("[^_a-z0-9]", "");
		if (h.length() == 0) {
			return null;
		}
		return "pX" + h + "Xy";
	}

	public String mangleFile(final BotLink link) {
		if (link == null) {
			// If link is null do not create //
			return null;
		}
		if (link.getPath() == null) {
			return null;
		}
		// Host + path
		final StringBuffer buf = new StringBuffer();

		// Treat path as _
		String p = link.getPath().trim().toLowerCase();
		p = p.replaceAll("/", "_");
		p = p.replaceAll("\\.", "_");
		// Replace all nonalphanumeric characters with a space
		p = p.replaceAll("[^_a-z0-9]", "");
		if (p.length() == 0) {
			return null;
		}

		String q = "";
		if (link.getQuery() != null) {
			q = link.getQuery().trim().toLowerCase();
			q = q.replaceAll("[^a-z0-9]", "");
		}
		return "wX" + p + "_" + q + "Xz";
	}

	public String manglePath(final BotLink link) {
		final String b = baseDirectory(link);
		final String f = mangleFile(link);
		String f2 = "f" + f.hashCode() + "_" + f;
		if (f2.length() > 142) {
			throw new RuntimeException("Invalid filename length, link=" + link);
		}
		return (b + "/" + f2).replaceAll("\\-", "");
	}

	public String mangleText(final BotLink link) {
		return manglePath(link) + ".oct_txt";
	}
	public String mangleFull(final BotLink link) {
		return manglePath(link) + ".oct_fll";
	}

} // End of the class //
