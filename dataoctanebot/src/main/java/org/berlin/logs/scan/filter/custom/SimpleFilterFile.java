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
package org.berlin.logs.scan.filter.custom;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.berlin.logs.scan.GlobalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Search for term using NIO.
 */
public class SimpleFilterFile {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFilterFile.class);

	public static final String PATTERN_NEWLINE = ".*\r?\n";
	public static final String DEFAULT_ENCODING = "ISO-8859-15";

	private final GlobalConfiguration globalConf;

	String encoding = DEFAULT_ENCODING;
	private Charset charset = Charset.forName(encoding);
	private CharsetDecoder decoder = charset.newDecoder();

	private Pattern linePattern = Pattern.compile(PATTERN_NEWLINE);

	long lines = 0;
	long totalLinesProcessed = 0;
	long totalLinesAccepted = 0;
	double percentAccepted = 0;

	boolean verbose = false;
	boolean printResults = false;

	boolean writeOutputFile = true;
	String outputFilename = "";

	int lastIdValue = 0;
	int id = 0;

	private File lastFile = null;
	private PrintWriter printWriter;

	int defaultReadBufferSize = 10 * 1024 * 1024;
	int smallerDefaultReadBufferSize = 10 * 1024 * 1024;
	boolean readMoreLargeFile = true;

	String regexSessionPattern = "^(.*) (\\[(WebContainer) : (\\d+)\\] \\[(\\S{23}) \\- (\\w{5})\\] (\\w*)) .*";
	int formatType = 1;

	final Map<String, Integer> sessionSet;
	final Map<String, Integer> agentSet;

	Map<String, Date> minSessionDateSet = new Hashtable<String, Date>();

	/**
	 * Constructor basic search.
	 * 
	 * @param globalConf
	 */
	public SimpleFilterFile(final GlobalConfiguration globalConf, final Map<String, Integer> sessionSet,
			final Map<String, Integer> agentSet) {
		this.globalConf = globalConf;
		this.sessionSet = sessionSet;
		this.agentSet = agentSet;
	}
	/**
	 * Use the linePattern to break the given CharBuffer into lines, applying the
	 * input pattern to each line to see if we have a match
	 */
	protected void search(final File f, final CharBuffer cb) {

		this.lastFile = f;
		final Matcher lm = linePattern.matcher(cb);
		this.lines = 0;
		final Pattern pPrefix = Pattern.compile(this.regexSessionPattern);
		String lastSessionId = "";
		int counterSwitchSessionId = 0;

		if (this.formatType == 2) {
			this.printWriter.println(
					" <table cellspacing='0' cellpadding='0' style='width: 1300px; border: 1px solid #CCC'>\n");
			this.printWriter.println("  <tr>\n");
		}
		long lineIndex = 1;
		while (lm.find()) {
			this.lines++;
			final CharSequence csLine = lm.group();
			if (csLine.length() < 24) {
				continue;
			}
			final String line = csLine.toString();
			totalLinesProcessed++;
			/**********************************************
			 * Filter out invalid items
			 **********************************************/

			if (line.indexOf("[AGT94] Save or update") >= 0) {
				continue;
			}

			if (line.indexOf("com.ibm.ws") >= 0) {
				continue;
			}

			FilterLineInfo info = null;
			final Matcher mPrefix = pPrefix.matcher(line);
			while (mPrefix.find()) {
				if (mPrefix.groupCount() >= 6) {
					info = new FilterLineInfo();
					info.minSessionDateSet = this.minSessionDateSet;
					info.lineIndex = lineIndex;
					lineIndex++;
					info.timePrefix = mPrefix.group(1).trim();
					info.threadId = mPrefix.group(4);
					info.sessionId = mPrefix.group(5);
					info.endSizePrefixForStrip = mPrefix.end(2);
					info.parse(line);
					if (info.sessionId != null) {
						if (this.sessionSet.get(info.sessionId) == null) {
							final int idx = this.sessionSet.size() + 1;
							this.sessionSet.put(info.sessionId, idx);
							info.sessionIndex = idx;
						} else {
							info.sessionIndex = this.sessionSet.get(info.sessionId);
						}
						if (!info.sessionId.equals(lastSessionId)) {
							lastSessionId = info.sessionId;
							counterSwitchSessionId++;
							if (this.formatType != 2) {
								this.printWriter.println("...");
							} else {
								this.printWriter.println(
										"\n<tr><td colspan='5'><div style='height: 14px'>&nbsp;</div></td></tr>\n");
							}

						} // End of if session id change

					} // End of if valid session

				} // End of if - find valid //
			} // End of While //

			String newline = line;
			newline = newline.replaceAll("com\\.project", "");
			newline = newline.replaceAll("protocol.WebRequestCycle.onBeginRequest", "onBegin");

			if (this.formatType == 2) {
				newline = new HTMLOutputLineFormatter(newline, info).format();
			} else {
				newline = new DefaultOutputLineFormatter(newline, info).format();
			}
			newline = newline.replaceAll(" (DEBUG|INFO) ", "");
			newline = newline.replaceAll("WebContainer ", "T");
			this.printWriter.println(newline);
			this.totalLinesAccepted++;
			if (lm.end() == cb.limit()) {
				break;
			}
		} // End of the While //
		if (this.formatType == 2) {
			this.printWriter.println("\n  </tr>\n");
			this.printWriter.println("\n </table>\n");
		}
		this.percentAccepted = (double) this.totalLinesAccepted / this.totalLinesProcessed;
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
				this.search(f, CharBuffer.wrap(data));
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

	public SimpleFilterFile search(final boolean enablePattr, final File f) {
		final long tstart = System.currentTimeMillis();
		try {
			LOGGER.info("Attempting search in '" + f.getName() + "' path=" + f.getAbsolutePath());
			this.search(f);
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

	public SimpleFilterFile search(final String pttrn, final String f) {
		return this.search(pttrn, f);
	}

	/**
	 * @param printWriter
	 *            the printWriter to set
	 */
	public void setPrintWriter(final PrintWriter printWriter) {
		this.printWriter = printWriter;
	}
	/**
	 * @param minSessionDateSet
	 *            the minSessionDateSet to set
	 */
	public void setMinSessionDateSet(Map<String, Date> minSessionDateSet) {
		this.minSessionDateSet = minSessionDateSet;
	}

} // End of the Class //
