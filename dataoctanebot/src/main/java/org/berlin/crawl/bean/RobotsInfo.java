package org.berlin.crawl.bean;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.berlin.logs.scan.NullRef;

public class RobotsInfo {

	public static final String CORE_ROBOT_KEY = "*.disallow";
	
	/**
	 * Allow and disallow path.
	 * Example mapping : ('crawler1.disallow' = ['/tmp/','/foo.html'])
	 */
	private Map<String, List<String>> userAgentDisallowPath = new HashMap<String, List<String>>();
	private List<String> sitemap = new ArrayList<String>();
	
	private String robotsText;
	private String host;
	private boolean valid = false;
	
	private String lastRuleFail = "";
	
	public boolean valid() {
		// If these conditions met, info is valid
		return robotsText != null && host != null && valid;		
	}
	
	public String toString() {
		final StringBuffer buf = new StringBuffer();
		buf.append(userAgentDisallowPath);
		buf.append("//");
		buf.append(sitemap);
		return buf.toString();
	}	
	public void add(final String key, final String path) {
		// There is data here to parse, set for valid //
		this.valid = true;
		if (this.userAgentDisallowPath.get(key) == null) {
			final List<String> l = new ArrayList<String>();
			l.add(path);
			this.userAgentDisallowPath.put(key, l);
		} else {
			this.userAgentDisallowPath.get(key).add(path);
		} // End of the if - else 
	} // End of the method //
	
	public void addSitemap(final String s) {
		sitemap.add(s);
	}
	
	/**
	 * Validate the link and check against the robots info.
	 * 
	 * @param link
	 */
	public boolean verifyLink(final BotLink link) {		
		final List<String> list = this.userAgentDisallowPath.get(CORE_ROBOT_KEY);		
		if (list == null) {
			// If no disallow data, assume 'allow'
			// OK to crawl
			return true;
		}
		// Assume starting with path
		final String path1 = link.getPath();
		if (!NullRef.hasValue(path1)) {
			// Strange case, return true TODO
			return true;
		}
		final String path = path1.trim().toLowerCase();
		for (final String chkx : list) {
			final String chk = chkx.trim().toLowerCase();
			if (path.equals(chk)) {
				// Invalid, disallow
				lastRuleFail = "Rule failed, DISALLOW " + path + " == " + chk;
				return false;
			}
			// Simple check for context path //
			if (path.length() > chk.length()) {
				// Compare with same number of characters
				final String p = path.substring(0, chk.length());
				if (p.equals(chk)) {
					lastRuleFail = "Rule failed, DISALLOW/StartsWith " + p + " == " + chk;
					return false;
				}
			}
			// Cheap hack, try to find rule for hiding various extensions
			final int chkSlash = chk.length() - chk.replaceAll("/", "").length();
			final int chkWildcard = chk.length() - chk.replaceAll("\\*", "").length();
			if (chkSlash==1 && chkWildcard==1) {
				final Matcher m = Pattern.compile("^/\\*\\.([a-z0-9]*)$").matcher(chk);
				String ext = null;
				while(m.find()) {
					if (m.groupCount() == 1) {
						ext = m.group(1);
					}
				}
				if (path.endsWith("."+ext)) {
					lastRuleFail = "Rule failed, extension '" + ext + "'";
					return false;
				}
			}			
		}		
		return true;
	} // End of method //
	
	/**
	 * @return the robotsText
	 */
	public String getRobotsText() {
		return robotsText;
	}

	/**
	 * @param robotsText the robotsText to set
	 */
	public void setRobotsText(String robotsText) {
		this.robotsText = robotsText;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result
				+ ((robotsText == null) ? 0 : robotsText.hashCode());
		result = prime * result + ((sitemap == null) ? 0 : sitemap.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		RobotsInfo other = (RobotsInfo) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (robotsText == null) {
			if (other.robotsText != null)
				return false;
		} else if (!robotsText.equals(other.robotsText))
			return false;
		if (sitemap == null) {
			if (other.sitemap != null)
				return false;
		} else if (!sitemap.equals(other.sitemap))
			return false;
		return true;
	}
	
	public static void main(final String [] args) {
		System.out.println("Running");
		final RobotsInfo r = new RobotsInfo();
		
		r.robotsText = "123";
		final List<String> xx = new ArrayList<String>();
		xx.add("/*.rssx");
		r.userAgentDisallowPath.put("*.disallow", xx);
		
		final BotLink l = new BotLink();
		l.setHost("berlin2.com");
		l.setPath("/abc/fjfj/kjsdfkjsdlf.rssx");		
		System.out.println("Done - " + r.verifyLink(l));
	}

	/**
	 * @return the lastRuleFail
	 */
	public String getLastRuleFail() {
		return lastRuleFail;
	}
	
} /// End of the class //
