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
package org.berlin.logs.scan.search;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.berlin.logs.scan.GlobalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Search for term.
 */
public class BasicSearch implements ISearcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicSearch.class);

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

	boolean saveOldLines = false;
	int numberLinesPrevious = 64;
	int numberLinesAfterTerm = 2;
	private int counterLinesAfterToken = 0;
	private LinkedList<String> previousLinesCache = new LinkedList<String>();

	int defaultReadBufferSize = 10 * 1024 * 1024;
	int smallerDefaultReadBufferSize = 10 * 1024 * 1024;
	boolean readMoreLargeFile = true;

	/**
	 * Constructor basic search.
	 * 
	 * @param globalConf
	 */
	public BasicSearch(final GlobalConfiguration globalConf) {
		this.globalConf = globalConf;
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

	protected String join(final List<String> lines, final String prefix) {
		if (lines == null) {
			return "";
		}
		final StringBuffer buf = new StringBuffer();
		for (final String line : lines) {
			buf.append(prefix + line);
		}
		return buf.toString();
	}

	/**
	 * Use the linePattern to break the given CharBuffer into lines, applying the
	 * input pattern to each line to see if we have a match
	 */
	protected void search(final File f, final CharBuffer cb) {
		final Matcher lm = linePattern.matcher(cb);
		Matcher pm = null;
		this.lines = 0;
		this.counterLinesAfterToken = 0;
		while (lm.find()) {
			this.lines++;
			final CharSequence csLine = lm.group();
			if (pm == null) {
				pm = pattern.matcher(csLine);
			} else {
				pm.reset(csLine);
			} // End of if - else //
				// Save last 70 lines
			if (this.saveOldLines) {
				if (this.writeOutputFile && this.printWriter != null) {
					if (counterLinesAfterToken > 0) {
						this.printWriter.print(f.getName() + "-" + csLine);
						this.printWriter.println("--");
						counterLinesAfterToken--;
					}
				} // End of if - write output to file //

				if (previousLinesCache.size() >= this.numberLinesPrevious) {
					((Queue<String>) previousLinesCache).remove();
				}
				previousLinesCache.add(csLine.toString());
			} // End of if - save last 70 lines //

			if (pm.find()) {
				termFoundTotal++;
				if (this.printResults) {
					System.out.print(f.getName() + "-" + csLine);
				}
				counterLinesAfterToken = 1;
				if (this.writeOutputFile && this.printWriter != null) {
					if (this.saveOldLines) {
						this.printWriter.print(this.join(previousLinesCache, f.getName() + "-"));
					} else {
						this.printWriter.print(f.getName() + "-" + csLine);
					}
				} // End of if - write output to file //
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
			if (size <= 0) {
				return;
			}
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
	} // End of search deprecated //

	/**
	 * Search for occurrences of the input pattern in the given file.
	 * 
	 * @param f
	 * @throws IOException
	 */
	@Deprecated
	protected void searchDEPRECATED(final File f) throws IOException {
		final FileInputStream fis = new FileInputStream(f);
		try {
			final FileChannel fc = fis.getChannel();
			final long size = fc.size() < this.defaultReadBufferSize ? fc.size() : this.defaultReadBufferSize;
			if (fc.size() > this.defaultReadBufferSize) {
				LOGGER.info("Using capped buffer during search : " + f);
				long begin = 0;
				final MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, size);
				final CharBuffer cb = decoder.decode(bb);
				search(f, cb);
				if (readMoreLargeFile) {
					begin = size;
					long newSize = smallerDefaultReadBufferSize;
					final MappedByteBuffer bb2 = fc.map(FileChannel.MapMode.READ_ONLY, begin, newSize);
					final CharBuffer cb2 = decoder.decode(bb2);
					search(f, cb2);
				}
			} else {
				final MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, size);
				final CharBuffer cb = decoder.decode(bb);
				search(f, cb);
			}
			fc.close();
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
	} // End of search deprecated //

	public BasicSearch search(final String pttrn, final File f) {
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

	public BasicSearch search(final String pttrn, final String f) {
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
