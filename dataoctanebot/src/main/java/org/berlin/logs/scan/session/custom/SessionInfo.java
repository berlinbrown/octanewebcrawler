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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Session info bean for extracting session information.
 */
public class SessionInfo implements Comparable<SessionInfo> {

	String sessionId = "";
	boolean logError = false;
	boolean criticalError = false;
	String logLevel = "";
	int id = -1;

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

	Date javaDate = null;
	long typedJavaDate = 0;

	Date minRecordTime = null;
	Date maxRecordTime = null;
	double diffTimeMinutes = 0;

	String timeLongAgo = "";
	String browserFullUserAgent = "";
	int linesOfSessionData = 0;
	int onEndRequest = 0;

	String errorMessageType = "none";

	public static final String R1 = "2011.06.11 00:01:00";
	public static final String R2 = "2011.06.22 00:01:00";
	public static final String FORMAT_DATE_TIME = "yyyy.MM.dd HH:mm:ss";

	public static final int MIN_USR_AGENT_LEN = 24;

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
		if (this.minRecordTime == null) {
			this.minRecordTime = this.javaDate;
		} else {
			final Calendar calchk2 = GregorianCalendar.getInstance();
			calchk2.setTime(this.minRecordTime);
			if (calchk2.after(cal)) {
				this.minRecordTime = this.javaDate;
			}
		}
		if (this.maxRecordTime == null) {
			this.maxRecordTime = this.javaDate;
		} else {
			final Calendar calchk2 = GregorianCalendar.getInstance();
			calchk2.setTime(this.maxRecordTime);
			if (calchk2.before(cal)) {
				this.maxRecordTime = this.javaDate;
			}
		}
		if (this.maxRecordTime != null && this.minRecordTime != null) {
			final long diff = this.maxRecordTime.getTime() - this.minRecordTime.getTime();
			this.diffTimeMinutes = (diff / 1000.0) / (60.0);
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
		final String tmpj = String.format("%s.%s.%s %s:%s:%s", this.year, this.month, this.day, this.hour, this.minute,
				this.seconds);
		try {
			final SimpleDateFormat f = new SimpleDateFormat(FORMAT_DATE_TIME);
			final Date d = f.parse(tmpj);
			linesOfSessionData++;
			this.javaDate = d;
			this.processMinMaxRecordTime();
			if (this.javaDate != null) {
				typedJavaDate = this.javaDate.getTime();
				final Date now = new Date();
				final double diffFromLongAgoSeconds = Math.floor((now.getTime() - this.javaDate.getTime()) / 1000.0);
				final double diffFromLongMinutes = Math.floor(diffFromLongAgoSeconds / 60.0);
				final double diffFromLongAgoHours = Math.floor(diffFromLongMinutes / 60.0);
				final double diffFromLongAgoDays = Math.floor(diffFromLongAgoHours / 24.0);
				final double remForHours = diffFromLongAgoHours % 24.0;
				final double remForMins = diffFromLongMinutes % 60.0;
				final double remForSeconds = diffFromLongAgoSeconds % 60.0;
				this.timeLongAgo = (int) diffFromLongAgoDays + " days and " + (int) remForHours + " hours and "
						+ (int) remForMins + " mins and " + (int) remForSeconds + " secs ago";
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

			System.err.println("ERROR: line=" + line);
			e.printStackTrace();
		}
	} // End of Method //

	public void loadSessionInfo(final String key, final String val, final String idx, final String prop) {

		if ("sessionId".equalsIgnoreCase(prop)) {
			this.sessionId = val.trim();

		} else if ("filename".equalsIgnoreCase(prop)) {
			this.filename = val.trim();

		} else if ("lastDateMs".equalsIgnoreCase(prop)) {
			this.typedJavaDate = Long.parseLong(val.trim());
			if (this.typedJavaDate > 0) {
				this.javaDate = new Date(this.typedJavaDate);
			}

		} else if ("timeAgo".equalsIgnoreCase(prop)) {
			this.timeLongAgo = val.trim();

		} else if ("userAgent".equalsIgnoreCase(prop)) {
			this.browserFullUserAgent = val.trim();

		} else if ("linesData".equalsIgnoreCase(prop)) {
			this.linesOfSessionData = Integer.parseInt(val.trim());

		} else if ("numberRequestsInSess".equalsIgnoreCase(prop)) {
			this.onEndRequest = Integer.parseInt(val.trim());

		} else if ("timeFrameMins".equalsIgnoreCase(prop)) {
			this.diffTimeMinutes = Double.parseDouble(val.trim());

		} else if ("minTime".equalsIgnoreCase(prop)) {

		} else if ("maxTime".equalsIgnoreCase(prop)) {

		} else if ("hasCRITICALERROR".equalsIgnoreCase(prop)) {
			this.criticalError = Boolean.parseBoolean(val.trim());

		} // End of the if - else //
	}

	/**
	 * Return an object value by property key.
	 * 
	 * @param parm
	 * @return
	 */
	public Double pullValue(final String parm) {

		if ("lastDateMs".equalsIgnoreCase(parm)) {
			return Double.valueOf(this.typedJavaDate);

		} else if ("linesData".equalsIgnoreCase(parm)) {
			return Double.valueOf(this.linesOfSessionData);

		} else if ("numberRequestsInSess".equalsIgnoreCase(parm)) {
			return Double.valueOf(this.onEndRequest);

		} else if ("timeFrameMins".equalsIgnoreCase(parm)) {
			return Double.valueOf(this.diffTimeMinutes);

		} else if ("hasCRITICALERROR".equalsIgnoreCase(parm)) {
			if (this.criticalError) {
				return 1.0;
			} else {
				return 0.0;
			}
		}
		return 0.0;
	}

	/**
	 * Return an object value by property key.
	 * 
	 * @param parm
	 * @return
	 */
	public String pullValue(final String parm, final String moreinfo) {

		if ("lastDateMs".equalsIgnoreCase(parm)) {
			return String.valueOf(this.typedJavaDate);

		} else if ("linesData".equalsIgnoreCase(parm)) {
			return String.valueOf(this.linesOfSessionData);

		} else if ("numberRequestsInSess".equalsIgnoreCase(parm)) {
			return String.valueOf(this.onEndRequest);

		} else if ("timeFrameMins".equalsIgnoreCase(parm)) {
			return String.valueOf(this.diffTimeMinutes);

		} else if ("hasCRITICALERROR".equalsIgnoreCase(parm)) {
			if (this.criticalError) {
				return "true";
			} else {
				return "false";
			}
		} else if ("sessionId".equalsIgnoreCase(parm)) {

			return this.sessionId;
		}

		return "";
	}

	public static final int FI_1[][] = {{0, 4}, {5, 7}, {8, 10}, {11, 13}, {14, 16}, {17, 19},};

	@Override
	public int compareTo(final SessionInfo o) {
		if (o == null) {
			return 1;
		}
		if (this.javaDate == null) {
			return 1;
		}
		if (o.javaDate == null) {
			return -1;
		}
		if (this.javaDate.getTime() >= o.javaDate.getTime()) {
			return -1;
		} else {
			return 1;
		}
	}

} // End of the class //
