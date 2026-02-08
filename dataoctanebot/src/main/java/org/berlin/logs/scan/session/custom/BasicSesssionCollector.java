/**
 * Copyright (c) 2006-2011 Berlin Brown.  All Rights Reserved
 *
 * http://www.opensource.org/licenses/bsd-license.php
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * * Neither the name of the Botnode.com (Berlin Brown) nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Date: 8/15/2011
 *  
 * Description: LogFile Searcher.  Search log file and build statistics from the scan.
 * 
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 */
package org.berlin.logs.scan.session.custom;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.berlin.logs.scan.GlobalConfiguration;
import org.berlin.logs.scan.functional.Functions;
import org.berlin.logs.scan.functional.SortedMap;
import org.berlin.logs.scan.search.ISearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Search for term using NIO.
 */
public class BasicSesssionCollector implements ISearcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicSesssionCollector.class);

	public static final String PATTERN_NEWLINE = ".*\r?\n";
	public static final String DEFAULT_ENCODING = "ISO-8859-15";

	private final GlobalConfiguration globalConf;

	String encoding = DEFAULT_ENCODING;
	private Charset charset = Charset.forName(encoding);
	private CharsetDecoder decoder = charset.newDecoder();

	private Pattern linePattern = Pattern.compile(PATTERN_NEWLINE);
	private Pattern pattern;

	private int lines = 0;
	private int termFoundTotal = 0;

	boolean verbose = false;
	boolean printResults = false;

	boolean writeOutputFile = true;
	String outputFilename = "";
	private PrintWriter printWriter;

	private final Map<String, SessionInfo> sessionDatabase;

	int lastIdValue = 0;
	int id = 0;

	private File lastFile = null;

	int defaultReadBufferSize = 10 * 1024 * 1024;
	int smallerDefaultReadBufferSize = 10 * 1024 * 1024;
	boolean readMoreLargeFile = true;

	/**
	 * Constructor basic search.
	 * 
	 * @param globalConf
	 */
	public BasicSesssionCollector(final GlobalConfiguration globalConf,
			final Map<String, SessionInfo> sessionDatabase) {
		this.globalConf = globalConf;
		this.sessionDatabase = sessionDatabase;
	}

	/**
	 * Compile pattern.
	 * 
	 * @param pat
	 */
	protected void compile(String pat) {
		try {
			pattern = Pattern.compile(pat);
		} catch (final PatternSyntaxException x) {
			System.err.println(x.getMessage());
		}
	}

	public void onSession(final String line, final String session, final String agent, final String logLevel) {
		if (line == null) {
			return;
		}
		final SessionInfo inf = this.sessionDatabase.get(session);
		if (inf == null) {
			final SessionInfo data = new SessionInfo();
			data.sessionId = session;
			if (this.lastFile != null) {
				data.filename = this.lastFile.getName();
			}
			if (this.sessionDatabase.size() < this.globalConf.getMaxNumberSesssionsInDatabase()) {
				this.sessionDatabase.put(session, data);
			}
			data.parse(line);
		} else {
			inf.logLevel = logLevel;
			inf.parse(line);
		} // End of the if - else //
	}

	public void writeSessionDatabase() {
		LOGGER.info("Number of sessions in this file : " + this.sessionDatabase.size());
		// Write in property file format //
		id = lastIdValue + 1;
		final Map<String, SessionInfo> map = Functions.take(this.globalConf.getMaxNumberSesssionsForClip(),
				new SortedSessionMapByTime(this.sessionDatabase));
		((SortedSessionMapByTime) map).sortByValue();
		for (final String session : map.keySet()) {
			final SessionInfo inf = map.get(session);
			if (globalConf.isUseXMLPropertyFormat()) {
				this.propertyFormatXml(inf);
			} else {
				this.propertyFormat(inf);
			}
			id++;
		} // End of the For //
	}

	public void writeSessionDatabaseList(final Collection<SessionInfo> list) {
		LOGGER.info("Number of sessions in this file : " + list.size());
		id = 1;
		for (final SessionInfo inf : list) {
			this.propertyFormatXml(inf);
			id++;
		} // End of the For //
	}

	protected void propertyFormat(final SessionInfo inf) {
		if (inf == null) {
			return;
		}
		this.printWriter.println("sess." + id + ".sessionId=" + inf.sessionId);
		this.printWriter.println("sess." + id + ".filename=" + inf.filename);
		this.printWriter.println("sess." + id + ".lastDate=" + inf.javaDate);
		this.printWriter.println("sess." + id + ".lastDateMs=" + inf.javaDate.getTime());
		this.printWriter.println("sess." + id + ".timeAgo=" + inf.timeLongAgo);
		this.printWriter.println("sess." + id + ".userAgent=" + inf.browserFullUserAgent);
		this.printWriter.println("sess." + id + ".linesData=" + inf.linesOfSessionData);
		this.printWriter.println("sess." + id + ".numberRequestsInSess=" + inf.onEndRequest);
		this.printWriter.println("sess." + id + ".timeFrameMins=" + String.format("%.2f", inf.diffTimeMinutes));
		this.printWriter.println("sess." + id + ".minTime=" + inf.minRecordTime);
		this.printWriter.println("sess." + id + ".maxTime=" + inf.maxRecordTime);
		this.printWriter.println("sess." + id + ".hasCRITICALERROR=" + inf.criticalError);
		this.printWriter.println("sess." + id + ".hasLogError=" + inf.logError);
		this.printWriter.println();
	}

	protected void propertyFormatXml(final SessionInfo inf) {
		if (inf == null) {
			return;
		}
		this.printWriter
				.println("<entry key=\"" + "sess." + id + ".sessionId\">" + escapeHtml(inf.sessionId) + "</entry>");
		this.printWriter
				.println("<entry key=\"" + "sess." + id + ".filename\">" + escapeHtml(inf.filename) + "</entry>");
		this.printWriter
				.println("<entry key=\"" + "sess." + id + ".lastDate\">" + escapeHtml(inf.javaDate) + "</entry>");
		this.printWriter.println("<entry key=\"" + "sess." + id + ".lastDateMs\">"
				+ (inf.javaDate == null ? "" : inf.javaDate.getTime()) + "</entry>");
		this.printWriter.println("<entry key=\"" + "sess." + id + ".timeAgo\">" + inf.timeLongAgo + "</entry>");
		this.printWriter.println(
				"<entry key=\"" + "sess." + id + ".userAgent\">" + escapeHtml(inf.browserFullUserAgent) + "</entry>");
		this.printWriter
				.println("<entry key=\"" + "sess." + id + ".linesData\">" + inf.linesOfSessionData + "</entry>");
		this.printWriter
				.println("<entry key=\"" + "sess." + id + ".numberRequestsInSess\">" + inf.onEndRequest + "</entry>");
		this.printWriter.println("<entry key=\"" + "sess." + id + ".timeFrameMins\">"
				+ String.format("%.2f", inf.diffTimeMinutes) + "</entry>");
		this.printWriter.println("<entry key=\"" + "sess." + id + ".minTime\">" + inf.minRecordTime + "</entry>");
		this.printWriter.println("<entry key=\"" + "sess." + id + ".maxTime\">" + inf.maxRecordTime + "</entry>");
		this.printWriter
				.println("<entry key=\"" + "sess." + id + ".hasCRITICALERROR\">" + inf.criticalError + "</entry>");
		this.printWriter.println("<entry key=\"" + "sess." + id + ".hasLogError\">" + inf.logError + "</entry>");
		this.printWriter.println();
	}

	/**
	 * Use the linePattern to break the given CharBuffer into lines, applying the
	 * input pattern to each line to see if we have a match
	 */
	protected void search(final File f, final CharBuffer cb) {
		this.lastFile = f;
		final Matcher lm = linePattern.matcher(cb);
		Matcher pm = null;
		this.lines = 0;
		final int groupIdSession = 4;
		final int groupIdAgent = 5;
		final int groupIdErrorType = 6;
		while (lm.find()) {
			this.lines++;
			final CharSequence csLine = lm.group();
			if (csLine.length() < 100) {
				continue;
			}
			if (pm == null) {
				pm = pattern.matcher(csLine);
			} else {
				pm.reset(csLine);
			} // End of if - else //
			if (pm.find()) {
				termFoundTotal++;
				if (pm.groupCount() >= 6) {
					this.onSession(csLine.toString(), pm.group(groupIdSession), pm.group(groupIdAgent),
							pm.group(groupIdErrorType));
				}
			} // End of if - find //
			if (lm.end() == cb.limit()) {
				break;
			}
		} // End of the While //
	}

	/**
	 * Search for occurrences of the input pattern in the given file.
	 * 
	 * @param f
	 * @throws IOException
	 */
	protected void search(final File f) throws IOException {
		final FileInputStream fis = new FileInputStream(f);
		try {
			final long size = f.length() < this.defaultReadBufferSize ? f.length() : this.defaultReadBufferSize;
			final BufferedInputStream bis = new BufferedInputStream(fis, (int) size);
			final byte[] b = new byte[(int) size];
			int noOfBytes = 0;
			while ((noOfBytes = bis.read(b)) != -1) {
				final String data = new String(b);
				search(f, CharBuffer.wrap(data));
				LOGGER.info("Searching ... number of bytes = " + noOfBytes);
			}
		} finally {
			fis.close();
		}
		final double mb = 1024.0 * 1024;
		final double free = Runtime.getRuntime().freeMemory() / mb;
		final double total = Runtime.getRuntime().totalMemory() / mb;
		final double max = Runtime.getRuntime().maxMemory() / mb;
		final String fmt = String.format("Memory after operation [ freeMemory=%.2fM total=%.2fM maxMemory=%.2fM ]",
				free, total, max);
		LOGGER.info(fmt);
	}

	public BasicSesssionCollector search(final String pttrn, final File f) {
		final long tstart = System.currentTimeMillis();
		this.compile(pttrn);
		try {
			this.termFoundTotal = 0;
			LOGGER.info("Attempting search in '" + f.getName() + "' path=" + f.getAbsolutePath());
			this.search(f);
			if (this.termFoundTotal > 0) {
				LOGGER.info("  Found : " + this.termFoundTotal + " times");
			} else {
				LOGGER.info("  Found : 0 times.  [ '" + pttrn + "' ] Not found in file.");
			}
			final long tdiff = System.currentTimeMillis() - tstart;
			LOGGER.info("   Searched file in " + tdiff + " ms");
		} catch (final IOException x) {
			x.printStackTrace();
			System.err.println("ERROR " + f + ": " + x);
		} // End of the try - catch //
		return this;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html
	 *            the html string to escape
	 * @return the escaped string
	 */
	public static String escapeHtml(final Object html) {
		if (html == null) {
			return "";
		}
		return html.toString().replaceAll("&", "&amp;").toString().replaceAll("<", "&lt;").toString().replaceAll(">",
				"&gt;");
	}

	public static class SortedSessionMapByTime extends SortedMap<String, SessionInfo> {

		private static final long serialVersionUID = 1L;

		public SortedSessionMapByTime(final Map<String, SessionInfo> map) {
			super(map);
		}

		public SortedSessionMapByTime() {
			super();
		}
	}

	/**
	 * Sort session database by time.
	 */
	public static class SessionComparatorByTime implements Comparator<SessionInfo> {
		@Override
		public int compare(final SessionInfo o1, final SessionInfo o2) {
			if (o1 == null) {
				return 1;
			}
			if (o2 == null) {
				return -1;
			}
			if (o1.javaDate == null) {
				return 1;
			}
			if (o2.javaDate == null) {
				return -1;
			}
			if (o1.javaDate.getTime() >= o2.javaDate.getTime()) {
				return -1;
			} else {
				return 1;
			}
		}

	} // End of the comparator class /

	public BasicSesssionCollector search(final String pttrn, final String f) {
		return this.search(pttrn, f);
	}

	/**
	 * @return the termFoundTotal
	 */
	public int getTermFoundTotal() {
		return termFoundTotal;
	}

	/**
	 * @param printWriter
	 *            the printWriter to set
	 */
	public void setPrintWriter(final PrintWriter printWriter) {
		this.printWriter = printWriter;
	}

} // End of the Class //
