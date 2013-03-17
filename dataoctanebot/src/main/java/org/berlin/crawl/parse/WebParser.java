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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.utils.URIBuilder;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.Link;
import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.berlin.crawl.bean.BotCrawlerLink;
import org.berlin.crawl.bean.BotLink;
import org.berlin.crawl.bom.LinkProcessQueueDatabase;
import org.berlin.crawl.dao.BotCrawlerDAO;
import org.berlin.crawl.util.OctaneCrawlerConstants;
import org.berlin.crawl.util.text.IO;
import org.berlin.crawl.util.text.IO.Fx;
import org.berlin.crawl.util.text.TextHelpers;
import org.berlin.logs.scan.NullRef;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.xml.sax.ContentHandler;

public class WebParser {
		
	private static final Logger logger = LoggerFactory.getLogger(WebParser.class);
	
	public static final int DELAY_FOR_ADDING_TO_Q = 100;	
	public static final int MAX_LINKS_PAGE = 160;
	   
    private static final Pattern LINKS_PATTERN = Pattern.compile(OctaneCrawlerConstants.LINKS_REGEX, Pattern.CASE_INSENSITIVE);
    
    private static final String SIMPLE_LINK_REGEX = "^(https?)://([-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])";
    private static final Pattern SIMPLE_LINK = Pattern.compile(SIMPLE_LINK_REGEX, Pattern.CASE_INSENSITIVE);
    
    private static final String SIMPLE_LINK_REGEX2 = "^([-a-zA-Z0-9+&@#%~_|!:,.;]+)/?(.+)";
    private static final Pattern SIMPLE_LINK2 = Pattern.compile(SIMPLE_LINK_REGEX2, Pattern.CASE_INSENSITIVE);
    
    private LinkProcessQueueDatabase queue;
    private final ApplicationContext ctx;
    
    public WebParser(final ApplicationContext ctx, final LinkProcessQueueDatabase q) {
    	this.queue = q;
    	this.ctx = ctx;
    }
    
    public List<String> extractLinks(final String content) {
        if (content == null || content.length() == 0) {
            return Collections.emptyList();
        }
        final List<String> extractions = new ArrayList<String>();
        final Matcher matcher = LINKS_PATTERN.matcher(content);
        while (matcher.find()) {
            extractions.add(matcher.group());
        }
        return extractions;
    }
	
    protected String fullURL(final Link link, final URIBuilder lastBuilder, final Set<String> urls) {
    	final String uri = link.getUri().trim();
		final boolean basicExclude = (uri.length() != 0) && !uri.equals("/");
		if (basicExclude) {
			String fullURL = "";
			// We have some URL's
			if (extractLinks(uri).size() > 0) {
				// First add full URL to set //
				fullURL = uri;
			} else {
				if (uri.startsWith("/")) {
					fullURL = lastBuilder.getScheme() + "://" + lastBuilder.getHost() + uri;
				} else {
					final String path = parsePath(lastBuilder.getPath());
					fullURL = lastBuilder.getScheme() + "://" + lastBuilder.getHost() + path + uri;
				} // End of the if - else //
			} // End //
			if (urls.contains(fullURL)) {
				// Return null so we don't reprocess 
				return null;
			}
			// We should have a valid full URL.
			urls.add(fullURL);
			return fullURL;
		} // End of the if //
		return null;
    }
    
    protected void processFullURL(final List<BotLink> linksForProcessing, final Link tkLink, final String u) {    	
		String scheme = "";
		String host = "";
		String path = "";
		String query = "";				
		 final Matcher m = SIMPLE_LINK.matcher(u);
		 while(m.find()) {
			 if (m.groupCount() >= 2) {
				 scheme = m.group(1).trim();
				 final String tmp = m.group(2).trim();
				 final Matcher m2 = SIMPLE_LINK2.matcher(tmp);
				 while (m2.find()) {
					 if (m2.groupCount() >=2) {
						 host = m2.group(1).trim();
						 // At this point we should have a path
						 // Remove the 'query' section if available
						 final String tmp2 = m2.group(2).trim();
						 if (tmp2.indexOf('?') > 0) {
							  final String wQuery = tmp2.substring(tmp2.indexOf('?')+1);
							  path = tmp2.substring(0, tmp2.indexOf('?'));
							  query = wQuery;
						 } else {
							 path = tmp2;
						 }
					 } // End of the if //
				 }
			 }
		 } // End of the while				 
		 if (scheme.length() > 0 && host.length() > 0) {
			 // Create a link for for further processing //
			 final BotLink link = new BotLink();			 
			 link.setHost(host);
			 if (path.length() > 0) {
				 link.setPath("/" + path);
			 } // End of the if //
			 link.setScheme(scheme);
			 link.setQuery(query);
			 link.setLink(tkLink);			 
			 logger.info("Attempt to process and add to queue / link , link=" + link);			 
			 linksForProcessing.add(link);					 			 					 			 				 
		 } // End of the if //				 
		 			 
    } // End of method //
    
	public List<BotLink> parse(final BotLink origLink, final URIBuilder lastBuilder, final String document) {
		final SessionFactory sf = (SessionFactory) ctx.getBean("sessionFactory");
		final Session session = sf.openSession();		
		try {									
			final InputStream input = new ByteArrayInputStream(document.getBytes());
			final LinkContentHandler linkHandler = new LinkContentHandler();
			final ContentHandler textHandler = new BodyContentHandler();
			final ToHTMLContentHandler toHTMLHandler = new ToHTMLContentHandler();
			final TeeContentHandler teeHandler = new TeeContentHandler(linkHandler, textHandler, toHTMLHandler);
			final Metadata metadata = new Metadata();
			final ParseContext parseContext = new ParseContext();
			final HtmlParser parser = new HtmlParser();
			parser.parse(input, teeHandler, metadata, parseContext);
			
			final String titleOfPage = metadata.get("title");
			// For analytical data, ignore pages that don't have titles
			if (!NullRef.hasValue(titleOfPage)) {
				logger.warn("Warning, invalid title for page, EXITING logic, link=" + origLink);
				return null;
			}
			
			// Continue with parsing //
			final List<BotLink> linksForProcessing = new ArrayList<BotLink>();
			final Set<String> urls = new HashSet<String>();
			
			int fullLinkCount = 0;
			for (final Link link : linkHandler.getLinks()) {
				fullLinkCount++;
			}			
			int linkcount = 0;
			// Loop through the links on the page
			// And add a set number to the queue.
			final Random rchk = new Random(System.currentTimeMillis());
			final List<Link> linksFromPage = linkHandler.getLinks();
			Collections.shuffle(linksFromPage);					
			for (final Link link : linksFromPage) {
				// Add a 30% chance of adding this link
				final double rval = rchk.nextDouble();
				final boolean okToAdd = rval > 0.65;
				if (okToAdd && link.getUri() != null) {
					linkcount++;
					if (linkcount > MAX_LINKS_PAGE) {
						// Only process a given number of links on a page //
						break;
					} // End of if max reached
					final String fullURL = this.fullURL(link, lastBuilder, urls);
					if (fullURL != null) {
						try {
							this.processFullURL(linksForProcessing, link, fullURL);
						} catch(final Throwable te) {
							te.printStackTrace();
						}
					}							
				} // End of the if //				 
			} // End of the for through the links //
			
			// Parse the available URLS //
			logger.info("In Web Parser for " + lastBuilder + " # availableNumberOfLinks=" + urls.size() + " fullLinkCount=" + fullLinkCount);
									
			// Persist the current link // 
			origLink.setNumberLinks(fullLinkCount);
			this.writeFileAndSave(origLink, session, metadata, document, textHandler.toString());
			
			processLinksForQueue(linksForProcessing);			
			return linksForProcessing;
			
		} catch(final Throwable e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		} // End of the try - catch //
		return null;
	} // End of the method //
	
	protected void writeFileAndSave(final BotLink link, final Session session, final Metadata metadata, final String document, final String txtOnlyDoc) {
		// Link added to processing, write the information to file
		 final TextHelpers text = new TextHelpers();
		 final String dir = text.baseDirectory(link);					 
		 final String fpathText = text.mangleText(link);
		 final String fpathFull = text.mangleFull(link);
		 final File chkdir = new File(OctaneCrawlerConstants.CRAWLER_HOME + "/" + dir);
		 final boolean res = chkdir.mkdirs();
		 final long wstart = System.currentTimeMillis();
		 final File txtfile = new File(OctaneCrawlerConstants.CRAWLER_HOME + "/" + fpathText);
		 final File fullfile = new File(OctaneCrawlerConstants.CRAWLER_HOME + "/" + fpathFull);						 
			 try { 
				 // Write text version and full file //
				 new IO<Void>().w(txtfile.getAbsolutePath(), new Fx<PrintWriter>() {							
					public void $(final PrintWriter o, final int idx) {
						o.println(txtOnlyDoc);
					} 
				 });
				 new IO<Void>().w(fullfile.getAbsolutePath(), new Fx<PrintWriter>() {							
					 public void $(final PrintWriter o, final int idx) {
						 o.println(document);
					 } 
				 });
				 final long diff = System.currentTimeMillis() - wstart;
				 logger.info("Result after directory and writes / mk:" + chkdir + " res=" + res + " procTime=" + diff);
				 
				 final BotCrawlerDAO dao = new  BotCrawlerDAO();
				 final BotCrawlerLink persistLink = new BotCrawlerLink();
				 persistLink.setHost(link.getHost());				 
				 persistLink.setUrl(String.valueOf(link.toBuilder()));
				 persistLink.setTitle(metadata.get("title"));
				 persistLink.setDescr(metadata.get("description"));
				 persistLink.setPath(fpathText);
				 persistLink.setLinkcount(link.getNumberLinks());
				 persistLink.setSource(Thread.currentThread().getName());
				 if (NullRef.hasValue(link.getStatusline())) {
					 persistLink.setStatusline(link.getStatusline());
					 persistLink.setStatus(link.getCode());
				 } // Check the status line //
				 if (link.getLink() != null) {
					 if (link.getLink().getText() != null) {
						 persistLink.setLinktext(link.getLink().getText());
					 }
				 }
				 dao.createLink(session, persistLink);
				 
			 } catch(final Throwable e) {
				 e.printStackTrace();
			 } // End of try - catch //			 
	} // End of method write file and persist //
	
	/**
	 * After processing, add more links to the queue.
	 * 
	 * @param listLink
	 */
	protected void processLinksForQueue(final List<BotLink> listLink) {	
		for (final BotLink link : listLink) {
			try {
				this.queue.get().put(link);
				Thread.sleep(DELAY_FOR_ADDING_TO_Q);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} // End of the for //
	}
	
	protected String parsePath(final String path) {
		if (path == null) {
			return "";
		}
		if (path.equals("/") || path.equals("//")) {
			return "";
		}
		final String sp [] = path.split("/");
		if (sp.length <= 1) {
			// One means we only have the root and the page
			return "";
		}
		final int n = sp.length;
		final StringBuffer buf = new StringBuffer();	
		for (int i = 0; i < n; i++) {
			// Check if standard mime
			if (i == (n-1)) {
				// Hack way to detect downloadable page content
				final String chk = sp[i].trim();
				if (!chk.matches("(?i)(.*\\.[a-z0-9]+)")) {
					buf.append(sp[i].trim());
				}
			} else {
				buf.append(sp[i].trim());
				buf.append('/');
			}
		} // End of the for //		
		return buf.toString();
	} // End of the method //
	
} // End of the class //
