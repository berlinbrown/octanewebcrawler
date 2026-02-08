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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 * Session info bean for extracting session information.
 */
public class FilterLineInfo {

	int endSizePrefixForStrip = 0;
	String threadId = "";
	String sessionId = "";
	boolean logError = false;
	boolean criticalError = false;
	String logLevel = "";
	int id = -1;

	int sessionIndex = 0;
	int agentIndex = 0;

	String server = "";
	String jvm = "";
	String filename = "";
	String fileDate = "";
	String year = "";
	String month = "";
	String day = "";
	String hour = "";
	String minute = "";
	String seconds = "";
	String timeField = "";
	String dayField = "";
	String ms = "";

	Date javaDate = null;
	Date minRecordTime = null;
	Date maxRecordTime = null;
	double diffTimeMinutes = 0;

	String timePrefix = "";
	String timeLongAgo = "";
	String browserFullUserAgent = "";
	int linesOfSessionData = 0;
	int onEndRequest = 0;
	long lineIndex = 1;

	String javaDateFormatted = "0";
	String errorMessageType = "none";

	String timeOfSession = "";

	Map<String, Date> minSessionDateSet = null;

	public static final String R1 = "2011.06.11 00:01:00";
	public static final String R2 = "2011.06.22 00:01:00";
	public static final String FORMAT_DATE_TIME = "yyyy.MM.dd HH:mm:ss:S";
	public static final int MIN_USR_AGENT_LEN = 28;

	/**
	 * Detect the min max time period.
	 * 
	 * @param termInfo
	 */
	protected void processMinMaxRecordTime() {
		if (this.javaDate == null) {
			return;
		}
		final Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(this.javaDate);
		this.minRecordTime = this.javaDate;
		this.maxRecordTime = this.javaDate;
		if (this.minSessionDateSet != null && this.sessionId != null) {
			if (this.minSessionDateSet.get(this.sessionId) == null) {
				this.minSessionDateSet.put(this.sessionId, this.minRecordTime);
			} else {
				this.minRecordTime = this.minSessionDateSet.get(this.sessionId);
			}
		}
		if (this.maxRecordTime != null && this.minRecordTime != null) {
			final long diff = this.maxRecordTime.getTime() - this.minRecordTime.getTime();
			this.diffTimeMinutes = (diff / 1000.0) / (60.0);
			final double diffFromLongAgoSeconds = Math.floor(diff / 1000.0);
			final double diffFromLongMinutes = Math.floor(diffFromLongAgoSeconds / 60.0);
			final double remForMins = diffFromLongMinutes % 60.0;
			final double remForSeconds = diffFromLongAgoSeconds % 60.0;
			this.timeOfSession = (int) remForMins + "mins_and_" + (int) remForSeconds + "secs";
		}

	}

	/**
	 * Parse.
	 * 
	 * @param line
	 * @return
	 */
	public void parse(final String line) {
		if (line == null) {
			return;
		}
		if (line.length() < 50) {
			return;
		}
		int[][] idx = FI_1;
		idx = FI_1;

		this.year = line.substring(idx[0][0], idx[0][1]);
		this.month = line.substring(idx[1][0], idx[1][1]);
		this.day = line.substring(idx[2][0], idx[2][1]);
		this.hour = line.substring(idx[3][0], idx[3][1]);
		this.minute = line.substring(idx[4][0], idx[4][1]);
		this.seconds = line.substring(idx[5][0], idx[5][1]);
		this.dayField = String.format("x%s%s%s", this.month, this.day, this.year);
		this.ms = "0";
		if (this.timePrefix != null) {
			if (this.timePrefix.indexOf(',') > 0) {
				this.ms = this.timePrefix.substring(this.timePrefix.indexOf(',') + 1).trim();
			}
		}
		final String tmpj = String.format("%s.%s.%s %s:%s:%s:%s", this.year, this.month, this.day, this.hour,
				this.minute, this.seconds, this.ms);
		try {
			final SimpleDateFormat f = new SimpleDateFormat(FORMAT_DATE_TIME);
			final Date d = f.parse(tmpj);
			linesOfSessionData++;
			this.javaDate = d;
			this.processMinMaxRecordTime();
			if (this.javaDate != null) {
				final Date now = new Date();
				final double diffFromLongAgoSeconds = Math.floor((now.getTime() - this.javaDate.getTime()) / 1000.0);
				final double diffFromLongMinutes = Math.floor(diffFromLongAgoSeconds / 60.0);
				final double diffFromLongAgoHours = Math.floor(diffFromLongMinutes / 60.0);
				final double diffFromLongAgoDays = Math.floor(diffFromLongAgoHours / 24.0);
				final double remForHours = diffFromLongAgoHours % 24.0;
				final double remForMins = diffFromLongMinutes % 60.0;
				final double remForSeconds = diffFromLongAgoSeconds % 60.0;
				this.timeLongAgo = (int) diffFromLongAgoDays + " days and " + (int) remForHours + " hours and "
						+ (int) remForMins + " mins ago";

				final SimpleDateFormat formatterT = new SimpleDateFormat(
						"EEE_MMMMM_dd_yyyy hh:mm:ss a '(' ss'secs'_SSSS'ms' ')'");
				this.javaDateFormatted = formatterT.format(this.javaDate);
			} // End of if - java date //
			/**************************
			 * Misc searching
			 **************************/
			if (this.logLevel == null) {
				this.logLevel = "";
			}
			if ("ERROR".equalsIgnoreCase(this.logLevel.trim())) {
				this.logError = true;
			}
			if (line.indexOf("ERROR") >= 0) {
				this.criticalError = true;
			}
			{
				final int usrPos = line.indexOf("User-Agent");
				if (usrPos >= 0) {
					final String s9 = line.substring(usrPos);
					if (s9.length() > MIN_USR_AGENT_LEN) {
						if (browserFullUserAgent == null || browserFullUserAgent.length() == 0) {
							browserFullUserAgent = line.substring(usrPos + (MIN_USR_AGENT_LEN - 3));
							browserFullUserAgent = browserFullUserAgent.trim();
						}
					}
				} // End of the find
			}
			{
				final int onEndPos = line.indexOf("onEndRequest");
				if (onEndPos >= 0) {
					this.onEndRequest++;
				}
			}
		} catch (final Exception e) {
			System.err.println("Parse ERROR: " + e.getMessage() + " line=" + line);
			// e.printStackTrace();
		}
	} // End of Method //

	public static final int FI_1[][] = {{0, 4}, {5, 7}, {8, 10}, {11, 13}, {14, 16}, {17, 19},};

} // End of the class //
