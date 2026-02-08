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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.berlin.logs.scan.errors.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Report Writer.
 */
public class TermReportWriter {

	private static final Logger LOGGER = LoggerFactory.getLogger(TermReportWriter.class);

	enum ReportType {
		basicdatabase, basictimeplot, timeplotbymin, timeplotbyserver
	}

	private final LogFileStatistics stats;
	private final Map<String, List<LineTermInfoHandler>> termInfoMapByFile;
	private final Map<Long, MinuteStatisticsHandler> statsMapByMinute;

	private final LogTermProcessor processor;

	private boolean verbose = false;

	private ReportType report = ReportType.timeplotbymin;
	private String searchTermFromScan = "";

	/**
	 * Constructor.
	 * 
	 * @param stats
	 * @param termInfoMapByFile
	 */
	public TermReportWriter(final LogFileStatistics stats, final LogTermProcessor processor) {
		this.stats = stats;
		this.termInfoMapByFile = processor.getTermInfoMapByFile();
		this.statsMapByMinute = processor.getStatsMapByMinute();
		this.processor = processor;
	}

	public void report(final String outputFilename) {
		this.reportQuickSummary();
		// this.reportDatabase(outputFilename);
		// this.reportBasicTimeplot(outputFilename);
		// this.reportTotalMinuteTimeplot(outputFilename);
		new ReportWriterByServer(stats, this.processor).report(outputFilename);
	}

	public void reportQuickSummary() {
		LOGGER.info("---------------------");
		LOGGER.info(">> Statistics Analysis of Log Files Scan<<");
		LOGGER.info("---------------------");
		LOGGER.info("Number of Files Processed : " + this.processor.getTotalFilesProcessed());
		LOGGER.info("  (Expected Number of Files to Process) : " + this.processor.getExpectedFilesToProcess());
		LOGGER.info("Lines Processed lines=" + this.stats.totalLines);
		LOGGER.info("BeginRequest Count : " + this.stats.totalBeginRequest);
		LOGGER.info("EndRequest Count : " + this.stats.totalEndRequest);
		LOGGER.info("Critical Errors : " + this.stats.totalCriticalError);
		LOGGER.info("Exception terms found : " + this.stats.totalException);
		LOGGER.info("NullPointerException terms found : " + this.stats.totalNullPointer);
		LOGGER.info("Error terms found : " + this.stats.totalError);
		LOGGER.info("Search terms found [" + this.searchTermFromScan + "] : " + this.stats.totalSearchTermFound);
		LOGGER.info("TimeFrame : MinTime = '" + this.processor.getMinRecordTime() + "',  MaxTime = '"
				+ this.processor.getMaxRecordTime()
				+ String.format("',  timeFrameInHours=%.2f", this.processor.getDiffTimeHours()) + " hours");
		LOGGER.info("----");
	}

	/**
	 * Report totals based on 10 minute intervals.
	 * 
	 * @param outputFilename
	 */
	public void reportTotalMinuteTimeplot(final String outputFilename) {
		final Random random = new Random();
		final File f = new File(outputFilename + ".minuteplot.dat");
		StringBuffer buf = new StringBuffer(1024);
		buf = new StringBuffer(1024);
		buf.append("# \"Data\"");
		buf.append(Parser.NL);
		FileWriter fw = null;
		int i = 1;
		try {
			fw = new FileWriter(f);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			fw.write(buf.toString());
			fw.flush();
			buf = new StringBuffer(1024);
		} catch (IOException e1) {
			e1.printStackTrace();
		} // End of the try catch //

		final StringBuffer bufXTicPlot = new StringBuffer();
		bufXTicPlot.append("set xtics (");
		int dataForXTics = 0;

		for (final Long timepoint : this.statsMapByMinute.keySet()) {

			final MinuteStatisticsHandler info = this.statsMapByMinute.get(timepoint);
			final LineTermInfoHandler line = info.dataAtTimeOfStat;
			String jdate = "" + (line.javaDate == null ? "" : line.javaDate);
			jdate = jdate.replaceAll(" ", "_");
			final String jdateTrim = jdate.length() > 17 ? jdate.substring(0, 16) : jdate;
			buf.append("" + i);
			buf.append("\t\t" + line.termType);
			buf.append("\t\t" + line.timeByMin);
			buf.append("\t\t" + timepoint);
			buf.append("\t\t'" + jdate + "'");
			buf.append("\t\t" + info.beginTotal);
			buf.append("\t\t" + info.criticalerrorTotal);
			buf.append("\t\t" + info.exceptionTotal);
			buf.append("\t\t" + info.errorTotal);
			buf.append("\t\t" + info.searchTermTotal);
			buf.append(Parser.NL);

			/****************************
			 * After row data, print xtic information
			 ****************************/
			if (i < 2) {
				bufXTicPlot.append("'");
				bufXTicPlot.append(jdateTrim);
				bufXTicPlot.append("' " + line.timeByMin + ", ");
				dataForXTics++;
			} else {
				if (random.nextDouble() < 0.0020 && dataForXTics < 26) {
					// Percent chance to print out the date for xlabel plot
					bufXTicPlot.append("'");
					bufXTicPlot.append(jdateTrim);
					bufXTicPlot.append("' " + line.timeByMin + ", ");
					dataForXTics++;
				}
			} // End if - check random

			i++;
			if ((i % 100) == 0) {
				try {
					LOGGER.info("reportMinTimePlot: Writing row..." + i);
					fw.write(buf.toString());
					fw.flush();
					buf = new StringBuffer(1024);
				} catch (IOException e1) {
					e1.printStackTrace();
				} // End of the try catch //
			} // End of the if //
		} // End of the For (one-file) //
		try {
			fw.write(buf.toString());
			fw.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} // End of try write

		bufXTicPlot.append(")");
		LOGGER.info("End with xlabel plot data");
		LOGGER.info(bufXTicPlot.toString());
	}

	public void reportBasicTimeplot(final String outputFilename) {
		final File f = new File(outputFilename + ".timeplot.dat");
		StringBuffer buf = new StringBuffer(1024);
		buf = new StringBuffer(1024);
		buf.append("# \"Data\"");
		buf.append(Parser.NL);
		FileWriter fw = null;
		int i = 1;
		try {
			fw = new FileWriter(f);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			fw.write(buf.toString());
			fw.flush();
			buf = new StringBuffer(1024);
		} catch (IOException e1) {
			e1.printStackTrace();
		} // End of the try catch //
		final Random random = new Random();
		final StringBuffer bufXTicPlot = new StringBuffer();
		bufXTicPlot.append("set xtics (");
		int dataForXTics = 0;
		for (final String filename : termInfoMapByFile.keySet()) {
			final int size = termInfoMapByFile.get(filename).size();
			if (size > 0) {
				LOGGER.info("Writing PLOT/Time database filename=" + filename);
				final List<LineTermInfoHandler> list = termInfoMapByFile.get(filename);
				for (final LineTermInfoHandler line : list) {
					String jdate = "" + (line.javaDate == null ? "" : line.javaDate);
					jdate = jdate.replaceAll(" ", "_");
					final String jdateTrim = jdate.length() > 17 ? jdate.substring(0, 16) : jdate;

					buf.append("" + i);
					buf.append("\t\t" + line.termType);
					buf.append("\t\t" + line.timeByMin);
					buf.append("\t\t'" + jdate + "'");
					buf.append("\t\t" + parseTermTypeForPlot(line));
					buf.append(Parser.NL);
					// End of row, write ///

					/****************************
					 * After row data, print xtic information
					 ****************************/
					if (i < 2) {
						bufXTicPlot.append("'");
						bufXTicPlot.append(jdateTrim);
						bufXTicPlot.append("' " + line.timeByMin + ", ");
						dataForXTics++;
					} else {
						if (random.nextDouble() < 0.0020 && dataForXTics < 26) {
							// Percent chance to print out the date for xlabel plot
							bufXTicPlot.append("'");
							bufXTicPlot.append(jdateTrim);
							bufXTicPlot.append("' " + line.timeByMin + ", ");
							dataForXTics++;
						}
					} // End if - check random
					i++;
					if ((i % 100) == 0) {
						try {
							LOGGER.info("reportBasicTimePlot/Impulse: Writing row..." + i);
							fw.write(buf.toString());
							fw.flush();
							buf = new StringBuffer(1024);
						} catch (IOException e1) {
							e1.printStackTrace();
						} // End of the try catch //
					} // End of the if //
				} // End of for ///////

			} // End of the If //
		} // End of the For (one-file) //

		try {
			fw.write(buf.toString());
			fw.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} // End of try write
		bufXTicPlot.append(")");
		LOGGER.info("End with xlabel plot data");
		LOGGER.info(bufXTicPlot.toString());
	}

	/**
	 * Write the request and error information to the database.
	 */
	public void reportDatabase(final String outputFilename) {

		final File f = new File(outputFilename);
		FileWriter fw = null;
		int i = 1;
		try {
			fw = new FileWriter(f);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		StringBuffer buf = new StringBuffer(1024);
		buf = new StringBuffer(1024);
		buf.append("\"ID\"");
		buf.append(",\"RecID\"");
		buf.append(",\"TokenType\"");
		buf.append(",\"DateTime\"");
		buf.append(",\"JAVADate\"");
		buf.append(",\"SysTimeMillis\"");

		buf.append(",\"ApprDay\"");
		buf.append(",\"ApprHour\"");
		buf.append(",\"ApprMin\"");

		buf.append(",\"Filename\"");
		buf.append(Parser.NL);
		try {
			fw.write(buf.toString());
			fw.flush();
			buf = new StringBuffer(1024);
		} catch (IOException e1) {
			e1.printStackTrace();
		} // End of the try catch //

		for (final String filename : termInfoMapByFile.keySet()) {
			final int size = termInfoMapByFile.get(filename).size();
			if (size > 0) {
				LOGGER.info("Writing database filename=" + filename);
				final List<LineTermInfoHandler> list = termInfoMapByFile.get(filename);
				for (final LineTermInfoHandler line : list) {

					buf.append("\"" + i + "\"");
					buf.append(",\"" + i + "\"");
					buf.append(",\"" + line.termType + "\"");
					buf.append(",\"" + line.dateTimeField + "\"");
					buf.append(",\"" + (line.javaDate == null ? "" : line.javaDate) + "\"");
					buf.append(",\"x" + (line.javaDate == null ? "0" : line.javaDate.getTime()) + "\"");
					buf.append(",\"" + line.timeByDay + "\"");
					buf.append(",\"" + line.timeByHour + "\"");
					buf.append(",\"" + line.timeByMin + "\"");

					buf.append(",\"" + filename + "\"");
					buf.append(Parser.NL);

					// End of row, write ///
					i++;
					if ((i % 100) == 0) {
						try {
							LOGGER.info("reportDB: Writing row..." + i);
							fw.write(buf.toString());
							fw.flush();
							buf = new StringBuffer(1024);
						} catch (IOException e1) {
							e1.printStackTrace();
						} // End of the try catch //
					} // End of the if //

				} // End of for ///////

				try {
					fw.write(buf.toString());
					fw.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					try {
						fw.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} // End of try write

			} // End of the If //
		} // End of the For //
	}

	protected String parseTermTypeForPlot(final LineTermInfoHandler line) {
		if (LineTermInfoHandler.TermType.begin.equals(line.termType)) {
			return "1\t\t0\t\t0\t\t0";
		} else if (LineTermInfoHandler.TermType.criticalerror.equals(line.termType)) {
			return "0\t\t4\t\t0\t\t0";
		} else if (LineTermInfoHandler.TermType.nullptr.equals(line.termType)) {
			return "0\t\t0\t\t3\t\t0";
		} else if (LineTermInfoHandler.TermType.exception.equals(line.termType)) {
			return "0\t\t0\t\t0\t\t2";
		}
		return "0\t\t0\t\t0\t\t0";
	}

	/**
	 * @return the searchTermFromScan
	 */
	public String getSearchTermFromScan() {
		return searchTermFromScan;
	}

	/**
	 * @param searchTermFromScan
	 *            the searchTermFromScan to set
	 */
	public void setSearchTermFromScan(String searchTermFromScan) {
		this.searchTermFromScan = searchTermFromScan;
	}

} // End of the Class //
