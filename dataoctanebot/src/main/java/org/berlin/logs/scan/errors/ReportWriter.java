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
/**
 * Berlin Brown - Parser for errors in log files.
 */
package org.berlin.logs.scan.errors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Report Writer.
 */
public class ReportWriter implements ILogReportWriter {

	private final List<ParseErrorDataPoint> listErrorDataPoints;
	private final Statistics stats;

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportWriter.class);

	/**
	 * Default constructor for writer.
	 * 
	 * @param listErrorDataPoints
	 * @param stats
	 */
	public ReportWriter(final List<ParseErrorDataPoint> listErrorDataPoints, final Statistics stats) {
		this.listErrorDataPoints = listErrorDataPoints;
		this.stats = stats;
	}

	public void databaseReportGraphProcTime(final String outFilename, final int typi, final boolean today,
			final boolean week) {
		final String o = outFilename + ".proc" + typi + ".dat";
		final File f = new File(o);
		StringBuffer buf = new StringBuffer(1024);
		buf = new StringBuffer(1024);
		buf.append("#");
		buf.append("Time");
		buf.append("\t\tProcTime");
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
		String h[] = null;
		final List<String[]> list = new ArrayList<String[]>();
		int di = 0;
		for (final ParseErrorDataPoint e : this.listErrorDataPoints) {
			if (week && !e.newLogTimePrefix.errorAtWeek) {
				continue;
			}
			if (today && !e.newLogTimePrefix.todayOrYesterday) {
				continue;
			}
			final String tt = e.newLogTimePrefix.javaDate == null
					? ""
					: String.valueOf(e.newLogTimePrefix.javaDate.getTime());
			boolean valid = false;

		}
		for (final String[] zz : list) {
			buf.append(zz[0]);
			buf.append("\t\t");
			buf.append(zz[1]);
			buf.append(Parser.NL);
			i++;
			if ((i % 100) == 0) {
				try {
					LOGGER.info("ProcTime-Writing row..." + i);
					fw.write(buf.toString());
					fw.flush();
					buf = new StringBuffer(1024);
				} catch (IOException e1) {
					e1.printStackTrace();
				} // End of the try catch //
			}
		}
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
	}

	public void databaseReport(final String outFilename, final boolean postfix, final boolean today,
			final boolean week) {
		final long t = System.currentTimeMillis();
		final String o = outFilename + (postfix ? String.valueOf(t) : "");
		final File f = new File(o);

		StringBuffer buf = new StringBuffer(1024);
		buf = new StringBuffer(1024);
		buf.append("\"DBID\"");
		buf.append(",\"recID\"");
		buf.append(",\"ShortErrMsgType\"");
		buf.append(",\"isIE_at_Error\"");
		buf.append(",\"ShortBrowserSys\"");

		buf.append(",\"Server\"");
		buf.append(",\"JVM\"");
		buf.append(",\"JVMStr\"");

		buf.append(",\"LOGFileQuickServerLogPath\"");

		buf.append(",\"MONTH\"");
		buf.append(",\"DAY\"");
		buf.append(",\"DATEFIELD\"");
		buf.append(",\"JAVDATE\"");
		buf.append(",\"MILLITIME\"");

		buf.append(",\"RecentToday\"");
		buf.append(",\"ErrWithinWeek\"");

		buf.append(",\"SessionId\"");
		buf.append(",\"ExecutionTimeAtError\"");

		buf.append(",\"isSocketTimeOut\"");
		buf.append(",\"UserAgent\"");

		buf.append(",\"URLAtErr\"");
		buf.append(",\"LastURLBegin\"");

		buf.append(",\"ErrMsg\"");
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
		for (final ParseErrorDataPoint e : this.listErrorDataPoints) {
			if (week && !e.newLogTimePrefix.errorAtWeek) {
				continue;
			}
			if (today && !e.newLogTimePrefix.todayOrYesterday) {
				continue;
			}
			buf.append("\"" + i + "\"");
			buf.append(",\"" + e.getBlockParseIndex() + "\"");
			buf.append(",\"" + e.getShortErrorMsgType() + "\"");
			buf.append(",\"" + y(e.isBrowserInternetExplorer()) + "\"");
			buf.append(",\"" + e.getBrowserSortableQuickUserAgent() + "\"");
			buf.append(",\"" + e.newLogTimePrefix.server + "\"");
			buf.append(",\"" + e.newLogTimePrefix.jvm + "\"");
			buf.append(",\"" + e.newLogTimePrefix.server + "javaServer" + e.newLogTimePrefix.jvm + "\"");
			buf.append(",\"" + e.getLinePrefixFile() + "\"");
			buf.append(",\"" + e.newLogTimePrefix.month + "\"");
			buf.append(",\"" + e.newLogTimePrefix.day + "\"");
			buf.append(",\"" + e.newLogTimePrefix.dayField + "\"");
			buf.append(",\"" + (e.newLogTimePrefix.javaDate == null ? "" : String.valueOf(e.newLogTimePrefix.javaDate))
					+ "\"");
			buf.append(",\"x"
					+ (e.newLogTimePrefix.javaDate == null ? "" : String.valueOf(e.newLogTimePrefix.javaDate.getTime()))
					+ "\"");
			buf.append(",\"" + (e.newLogTimePrefix.todayOrYesterday ? "RecentToday" : "no") + "\"");
			buf.append(",\"" + (e.newLogTimePrefix.errorAtWeek ? "ErrWithinWeek" : "no") + "\"");
			buf.append(",\"" + e.getSessionId() + "\"");
			buf.append(",\"" + e.getProcTimeAtError() + "\"");
			buf.append(",\"" + y(e.isExternalHasTimeoutError()) + "\"");
			buf.append(",\"" + e.getBrowserFullUserAgent() + "\"");
			buf.append(",\"" + e.getUrlAtError() + "\"");
			buf.append(",\"" + e.getUrlLastBeginRequest() + "\"");
			buf.append(",\"" + e.getErrorMessage() + "\"");
			buf.append(Parser.NL);
			i++;
			if ((i % 100) == 0) {
				try {
					LOGGER.info("Writing row..." + i);
					fw.write(buf.toString());
					fw.flush();
					buf = new StringBuffer(1024);
				} catch (IOException e1) {
					e1.printStackTrace();
				} // End of the try catch //
			}
		} // End of the while //
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

	}

	/**
	 * Write stats to XML.
	 * 
	 * @param out
	 */
	public void statsReportPrintlnXml(final PrintStream out) {

		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		out.println("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">");
		out.println("<properties>");

		out.println("<entry key=\"StatisticsRunOn\">" + new Date() + "</entry>");
		out.println("<entry key=\"DataPoints.TODAY\">" + this.stats.errorsToday + "</entry>");
		out.println("<entry key=\"Data_points.errors_WEEK\">" + this.stats.errorsWeek + "</entry>");
		out.println("<entry key=\"Data_points.errors_all_errors_since\">" + this.stats.minDate + "</entry>");
		out.println("<entry key=\"Data_points.rrors_all_errors_since_time\">" + this.listErrorDataPoints.size()
				+ "</entry>");
		out.println();

		out.println(
				"<entry key=\"Errors_reported_recently_Appr_last_24_hours\">" + this.stats.errorsToday + "</entry>");
		out.println();

		final int totalNotSupported = stats.android + stats.iphone + stats.winmobile;
		out.println("<entry key=\"Errors_IE\">" + fnm(stats.ie) + "</entry>");
		out.println("<entry key=\"Errors_IE_7\">" + fnm(stats.ie7) + "</entry>");
		out.println("<entry key=\"Errors_IE_8\">" + fnm(stats.ie8) + "</entry>");
		out.println("<entry key=\"Errors_IE_9\">" + fnm(stats.ie9) + "</entry>");
		out.println();

		out.println("<entry key=\"Errors_Not_IE\">" + fnm(stats.nonIe) + "</entry>");
		out.println("<entry key=\"Errors_iPad\">" + fnm(stats.ipad) + "</entry>");
		out.println("<entry key=\"Errors_Android\">" + fnm(stats.android) + "</entry>");
		out.println("<entry key=\"Errors_iPhone\">" + fnm(stats.iphone) + "</entry>");
		out.println("<entry key=\"Errors_WinMobile\">" + fnm(stats.winmobile) + "</entry>");
		out.println("<entry key=\"Errors_NotSupportedMobile\">" + fnm(totalNotSupported) + "</entry>");

		out.println();
		out.println("</properties>");
	}

	/**
	 * Write stats to stream.
	 * 
	 * @param out
	 */
	public void statsReportPrintln(final PrintStream out) {

		out.println("--------" + Parser.NL);
		out.println("Statistics Error Logs" + Parser.NL);
		out.println("Statistics on " + new Date() + Parser.NL);
		out.println("--------" + Parser.NL);

		out.println("Writing statistics reports for CRITICAL ERRORS (a user may see an error page): ");
		out.println("-----");
		out.println("  Data points, critical errors TODAY OR RECENTLY(Appr Last 24 hours) : " + this.stats.errorsToday);
		out.println("  Data points, critical errors in past WEEK : " + this.stats.errorsWeek);
		out.println("  Data points, critical errors (all errors since " + this.stats.minDate + ")  : "
				+ this.listErrorDataPoints.size());
		out.println();

		out.println("-----");
		out.println("Errors reported recently (Appr last 24 hours) : " + this.stats.errorsToday);
		out.println();
		out.println("Note: 'or*' percent number is without create home page total");
		out.println();
		out.println("-----");
		out.println("Errors reported in last Week : " + +this.stats.errorsWeek);
		out.println();

		out.println("-----");
		out.println("Errors All Time : " + +this.listErrorDataPoints.size());
		out.println();

		final int totalNotSupported = stats.android + stats.iphone + stats.winmobile;
		out.println("  IE : " + fnm(stats.ie));
		out.println("  IE_7 : " + fnm(stats.ie7));
		out.println("  IE_8 : " + fnm(stats.ie8));
		out.println("  IE_9 : " + fnm(stats.ie9));
		out.println();

		out.println("  Not IE : " + fnm(stats.nonIe));
		out.println("  iPad : " + fnm(stats.ipad));
		out.println("  Android : " + fnm(stats.android));
		out.println("  iPhone : " + fnm(stats.iphone));
		out.println("  WinMobile : " + fnm(stats.winmobile));
		out.println("  NotSupportedMobile : " + fnm(totalNotSupported));

		out.println();
		out.println("-----");
	}

	public void statisticsReport(final String outFilename, final boolean postfix) {

		final String o = outFilename;
		final String xmlstats = outFilename + ".xml";
		final File f = new File(o);
		int i = 1;
		for (final ParseErrorDataPoint e : this.listErrorDataPoints) {
			LOGGER.info(i + ":Example-Data:" + e);
			i++;
			if (i == 2) {
				break;
			}
		}
		FileOutputStream fos = null;
		FileOutputStream fosXML = null;
		try {
			fos = new FileOutputStream(f);
			fosXML = new FileOutputStream(new File(xmlstats));
			this.statsReportPrintln(System.out);
			this.statsReportPrintln(new PrintStream(fos));
			this.statsReportPrintlnXml(new PrintStream(fosXML));
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				if (fosXML != null) {
					fosXML.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} // End of try write
		final ReportPlotWriter plotWriter = new ReportPlotWriter(stats);
		plotWriter.statisticsReportGnuPlotHisto(outFilename);
		plotWriter.statisticsReportGnuPlotHisto2(outFilename);
		plotWriter.statisticsReportGnuPlotHistoScript1(outFilename, "error_log_stats.txt.dat", "error_log_stats1.png",
				"plot '${INPUT_PLOT_FILE}' u 2:xtic(1) t 'Recently' w histograms, '' u 3 t 'Week' w histograms");
		plotWriter.statisticsReportGnuPlotHistoScript1(outFilename + "2", "error_log_stats2.txt.dat",
				"error_log_stats2.png", "plot '${INPUT_PLOT_FILE}' u 2:xtic(1) t 'All Time' w histograms");
	}

	protected String f(final String fmt, final Object... args) {
		return String.format(fmt, args);
	}

	protected String fnm(final int val) {
		final double f = 100.0 * ((double) val / this.stats.totalErrors);
		return String.format("occurrences = ( %s )  percent = ( %.1f%% ) ", val, f);
	}

	protected String fnm4(final int val) {
		final int t = this.stats.ipad;
		final double f = 100.0 * ((double) val / t);
		return String.format("occurrences = ( %s )  percOfAppleDevice = ( %.1f%% ) ", val, f);
	}

	protected String fnm2(final int val) {
		final double f = 100.0 * ((double) val / this.stats.errorsToday);
		return "";
	}
	protected String fnm3(final int val) {
		final double f = 100.0 * ((double) val / this.stats.errorsWeek);
		return String.format("wk.occurrences(rct) = ( %s )  percent = ( %.1f%% ) ", val, f);
	}

	protected String y(final boolean b) {
		return b ? "yes" : "no";
	}

} // End of the class
