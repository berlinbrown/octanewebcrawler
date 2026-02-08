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
package org.berlin.logs.scan.terms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log file processor for terms.
 */
public class LogTermProcessor implements ILogProcessor {

	private final FileScanner fileScanner;

	private static final Logger LOGGER = LoggerFactory.getLogger(LogTermProcessor.class);

	/**
	 * Scan search term.
	 */
	private String searchTerm = "Exception";
	private boolean hasSerchTerm = false;

	/**
	 * Verbosity.
	 */
	private boolean verbose = true;

	/**
	 * Used with random number generation, probability when to extract data from
	 * file. As opposed to collect millions of hits, only analyze a random sample.
	 * Average random sample = 0.18.
	 */
	private double randomSampleLogLine = 0.11;
	private boolean hasRandomSampling = true;

	private final Map<String, List<LineTermInfoHandler>> termInfoMapByFile = new LinkedHashMap<String, List<LineTermInfoHandler>>();
	private final Map<Long, MinuteStatisticsHandler> statsMapByMinute = new LinkedHashMap<Long, MinuteStatisticsHandler>();
	private final LogFileStatistics stats = new LogFileStatistics();
	private final Map<Long, Map<String, MinuteStatisticsHandler>> statsByServer = new LinkedHashMap<Long, Map<String, MinuteStatisticsHandler>>();

	private Date minRecordTime;
	private Date maxRecordTime;

	private double diffTimeHours = 0;
	private double diffTimeDays = 0;

	private int totalFilesProcessed = 0;
	private int expectedFilesToProcess = 0;

	/**
	 * Report term type.
	 */
	private TermReportWriter.ReportType reportType = TermReportWriter.ReportType.timeplotbymin;

	/**
	 * Constructor.
	 * 
	 * @param scanner
	 */
	public LogTermProcessor(final FileScanner scanner) {
		this.fileScanner = scanner;
	}

	protected void buildTermInfoMap(final List<File> files) {
		for (final File file : files) {
			final List<LineTermInfoHandler> termInfoList = new ArrayList<LineTermInfoHandler>();
			termInfoMapByFile.put(file.getName(), termInfoList);
		} // End of the for //
	}

	public void report() {
		final TermReportWriter report = new TermReportWriter(this.stats, this);
		report.setSearchTermFromScan(this.searchTerm);
		report.report("LogScanTerms_All.csv");
	}

	/**
	 * Process all the files.
	 */
	public void execute() {

		final Random random = new Random(System.currentTimeMillis());
		final List<File> files = this.fileScanner.execute();
		long tstart = 0;
		long tdiff = 0;
		long tstartTot = 0;
		long tdiffTot = 0;

		tstartTot = System.currentTimeMillis();
		final int size = files.size();
		int i = 0;
		String line = "";
		LineTermInfoHandler termInfo = null;
		this.buildTermInfoMap(files);

		final MapByServerParser mapByServerParser = new MapByServerParser();

		final String procMsg = "Processing file=%s , path=%s";
		final String procMemoryMsg = "  -> Processed file [%s] in %s ms, [ freeMemory=%.2fM total=%.2fM maxMemory=%.2fM ]";
		final String dryRunMsg1 = "  -> (DryRunAtFileZero) Estimated time to complete processing <<< %.2f mins";
		final String dryRunMsg2 = "  -> (DryRunAtFileZero) Lines extracted at first file : %s AllLinesProcessed=%s";
		final int bufReaderSize = 84 * 1024;
		final double mb = 1024.0 * 1024;

		this.expectedFilesToProcess = files.size();

		/******************************
		 * Process all files
		 ******************************/
		for (final File file : files) {
			totalFilesProcessed++;
			tstart = System.currentTimeMillis();
			if (this.verbose) {
				LOGGER.info(String.format(procMsg, file, file.getAbsolutePath()));
			}
			BufferedReader bufferedReader = null;
			try {
				bufferedReader = new BufferedReader(new FileReader(file), bufReaderSize);
				final ServerInfo serverInfo = ServerInfo.parseAndLoad(file.getName());
				LOGGER.info("  Filename, serverInfParse : " + serverInfo);
				while ((line = bufferedReader.readLine()) != null) {
					/******************
					 * Process line, begin
					 ******************/
					termInfo = new LineTermInfoHandler(this.stats);
					termInfo.serverInfo = serverInfo;
					final boolean resAllProcessLine = termInfo.processAtAllLines(line,
							(this.hasSerchTerm ? searchTerm : ""), true);
					stats.processAllStatistics(line);
					stats.totalLines++;
					final MinuteStatisticsHandler minStats = MinuteStatisticsHandler
							.buildStatisticsMapHelper(statsMapByMinute, termInfo);
					if (minStats != null) {
						mapByServerParser.parse(statsByServer, minStats);
					}
					this.processMinMaxRecordTime(termInfo);
					if (hasRandomSampling) {
						if (random.nextDouble() >= this.randomSampleLogLine) {
							if (termInfoMapByFile.get(file.getName()) != null) {
								if (this.collectTermInfoNeeded() && resAllProcessLine
										&& !LineTermInfoHandler.TermType.none.equals(termInfo.termType)) {
									termInfoMapByFile.get(file.getName()).add(termInfo);
								}
							} // End of if - add //
							continue;
						}
					} // End of if after random sampling //
					final boolean resSampling = termInfo.processAtSampling(line);
					if (termInfoMapByFile.get(file.getName()) != null) {
						if (this.collectTermInfoNeeded() && resSampling
								&& !LineTermInfoHandler.TermType.none.equals(termInfo.termType)) {
							termInfoMapByFile.get(file.getName()).add(termInfo);
						}
					} // End of if - add //

				} // / End of While on Line //
			} catch (final Exception e) {
				e.printStackTrace();
			} finally {
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
			} // End of try finally
			/************************************
			 * End of File Process
			 ************************************/
			tdiff = System.currentTimeMillis() - tstart;
			final double free = Runtime.getRuntime().freeMemory() / mb;
			final double total = Runtime.getRuntime().totalMemory() / mb;
			final double max = Runtime.getRuntime().maxMemory() / mb;
			final String fmt = String.format(procMemoryMsg, file.getName(), tdiff, free, total, max);
			LOGGER.info(fmt);
			final double estimatedMin = ((size * tdiff) / 1000.0) / 60.0;
			if (i == 0) {
				LOGGER.info(String.format(dryRunMsg1, estimatedMin));
				if (this.hasRandomSampling) {
					final int sizeAtEndFile = termInfoMapByFile.get(file.getName()).size();
					LOGGER.info(String.format(dryRunMsg2, sizeAtEndFile, stats.totalLines));
				}
			} // End of if, first file //
			int totalDataPoints = 0;
			for (final String filenameKey : this.termInfoMapByFile.keySet()) {
				totalDataPoints += this.termInfoMapByFile.get(filenameKey).size();
			}
			LOGGER.info("  DataPoints Collected Thus Far = " + totalDataPoints);
			LOGGER.info("  MinTime = '" + this.minRecordTime + "',  MaxTime = '" + this.maxRecordTime
					+ String.format("' timeFrameInHours=%.2f", this.diffTimeHours));
			stats.totalFiles++;
			i++;
			// TODO: Uncomment for more data
			if (i >= 8) {
				break;
			}
			System.gc();
		} // End of the For //

		final double free = Runtime.getRuntime().freeMemory() / mb;
		final double total = Runtime.getRuntime().totalMemory() / mb;
		final double max = Runtime.getRuntime().maxMemory() / mb;
		tdiffTot = System.currentTimeMillis() - tstartTot;
		final double tdiffMinutes = (tdiffTot / 1000.0) / 60.0;
		final String fmt = String.format(
				">> End << : Processed all files in %.2f min, [ freeMemory=%.2fM total=%.2fM maxMemory=%.2fM ]",
				tdiffMinutes, free, total, max);
		LOGGER.info(fmt);
	}

	/**
	 * Detect the min max time period.
	 * 
	 * @param termInfo
	 */
	protected void processMinMaxRecordTime(final LineTermInfoHandler termInfo) {
		if (termInfo == null) {
			return;
		}
		if (termInfo.javaDate == null) {
			return;
		}
		final Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(termInfo.javaDate);
		if (this.minRecordTime == null) {
			this.minRecordTime = termInfo.javaDate;
		} else {
			final Calendar calchk2 = GregorianCalendar.getInstance();
			calchk2.setTime(this.minRecordTime);
			if (calchk2.after(cal)) {
				this.minRecordTime = termInfo.javaDate;
			}
		}
		if (this.maxRecordTime == null) {
			this.maxRecordTime = termInfo.javaDate;
		} else {
			final Calendar calchk2 = GregorianCalendar.getInstance();
			calchk2.setTime(this.maxRecordTime);
			if (calchk2.before(cal)) {
				this.maxRecordTime = termInfo.javaDate;
			}
		}
		if (this.maxRecordTime != null && this.minRecordTime != null) {
			final long diff = this.maxRecordTime.getTime() - this.minRecordTime.getTime();
			this.diffTimeHours = (diff / 1000.0) / (60.0 * 60.0);
		}
	}

	protected boolean collectTermInfoNeeded() {
		if (TermReportWriter.ReportType.timeplotbymin.equals(this.reportType)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the verbose
	 */
	public boolean isVerbose() {
		return verbose;
	}

	/**
	 * @param verbose
	 *            the verbose to set
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * @param reportType
	 *            the reportType to set
	 */
	public void setReportType(TermReportWriter.ReportType reportType) {
		this.reportType = reportType;
	}

	/**
	 * @param hasSerchTerm
	 *            the hasSerchTerm to set
	 */
	public void setHasSerchTerm(boolean hasSerchTerm) {
		this.hasSerchTerm = hasSerchTerm;
	}

	/**
	 * @return the termInfoMapByFile
	 */
	public Map<String, List<LineTermInfoHandler>> getTermInfoMapByFile() {
		return termInfoMapByFile;
	}

	/**
	 * @return the statsMapByMinute
	 */
	public Map<Long, MinuteStatisticsHandler> getStatsMapByMinute() {
		return statsMapByMinute;
	}

	/**
	 * @return the minRecordTime
	 */
	public Date getMinRecordTime() {
		return minRecordTime;
	}

	/**
	 * @return the maxRecordTime
	 */
	public Date getMaxRecordTime() {
		return maxRecordTime;
	}

	/**
	 * @return the diffTimeHours
	 */
	public double getDiffTimeHours() {
		return diffTimeHours;
	}

	/**
	 * @return the diffTimeDays
	 */
	public double getDiffTimeDays() {
		return diffTimeDays;
	}

	/**
	 * @return the totalFilesProcessed
	 */
	public int getTotalFilesProcessed() {
		return totalFilesProcessed;
	}

	/**
	 * @return the expectedFilesToProcess
	 */
	public int getExpectedFilesToProcess() {
		return expectedFilesToProcess;
	}

	/**
	 * @return the statsByServerByJVM
	 */
	public Map<Long, Map<String, MinuteStatisticsHandler>> getStatsByServer() {
		return statsByServer;
	}

} // End of the Class //
