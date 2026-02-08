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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.berlin.logs.scan.errors.Parser;

public class ReportWriterByServer {

	private final LogFileStatistics stats;
	private final LogTermProcessor processor;

	public ReportWriterByServer(final LogFileStatistics stats, final LogTermProcessor processor) {
		this.stats = stats;
		this.processor = processor;
	}

	/**
	 * Report by server name.
	 * 
	 * @param outputFilename
	 */
	public void report(final String outputFilename) {
		final Map<Long, Map<String, MinuteStatisticsHandler>> data = this.processor.getStatsByServer();
		if (data == null) {
			System.err.println("Invalid data for stats by server");
			return;
		}

		final Map<String, Map<Long, MinuteStatisticsHandler>> newMapByServer = new LinkedHashMap<String, Map<Long, MinuteStatisticsHandler>>();
		for (final Long byTime : data.keySet()) {
			final Map<String, MinuteStatisticsHandler> byServer = data.get(byTime);
			for (final String server : byServer.keySet()) {
				final MinuteStatisticsHandler currentMinStats = byServer.get(server);
				// Build new map data transform
				if (newMapByServer.get(server) == null) {
					final Map<Long, MinuteStatisticsHandler> subMapTime = new LinkedHashMap<Long, MinuteStatisticsHandler>();
					subMapTime.put(byTime, currentMinStats);
					newMapByServer.put(server, subMapTime);
				} else {
					newMapByServer.get(server).put(byTime, currentMinStats);
				}
			} // End of the For //
		} // End of For by Time Period //

		// Re-run and print //
		for (final String serverfilename : newMapByServer.keySet()) {
			final String writefilename = outputFilename + "." + serverfilename;
			reportTotalMinuteTimeplot(newMapByServer.get(serverfilename), writefilename);
		}
	}

	/**
	 * Report totals based on 10 minute intervals.
	 * 
	 * @param outputFilename
	 */
	protected void reportTotalMinuteTimeplot(final Map<Long, MinuteStatisticsHandler> statsMapByMinute,
			final String outputFilename) {
		if (statsMapByMinute == null) {
			System.out.println("reportTotalMinuteTimeplot: Invalid map data");
			return;
		}
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
		for (final Long timepoint : statsMapByMinute.keySet()) {
			final MinuteStatisticsHandler info = statsMapByMinute.get(timepoint);
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
					System.out.println("reportMinTimePlot[byServer]: Writing row..." + i);
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
		System.out.println("End with xlabel plot data");
		System.out.println(bufXTicPlot);
	}

} // End of the Class //
