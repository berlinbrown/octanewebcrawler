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
 * Berlin Brown - Parser for critical errors in java web files.
 */
package org.berlin.logs.scan.errors;

import java.util.Date;

/**
 * Allow sort by min time or when error. Sort by server/jvm/time Sort by
 * procTime
 */
public class ParseErrorDataPoint {

	private String errorMessage = "";

	private boolean errorTypeNullPointerException = false;
	private boolean errorTypeSyncTimeoutException = false;

	private boolean externalHasTimeoutError = false;

	private String linePrefixFile = "";
	private String lineTimePrefixAppr = "";

	private String shortErrorMsgType = "UnknownErr";

	private long lineNumberInFile = 0;

	private boolean browserInternetExplorer = false;
	private boolean browserFirefox = false;
	private boolean browserIPad = false;
	private boolean browserIPhone = false;
	private boolean browserAndroid = false;
	private boolean browserMac = false;
	private boolean browserChrome = false;
	private boolean browserIEMobile = false;
	private boolean browserUnknown = false;

	private String browserVersion = "";
	private String browserSortableQuickUserAgent = "Unknown";
	private String browserFullUserAgent = "Unknown";

	private boolean ie7 = false;
	private boolean ie8 = false;
	private boolean ie9 = false;

	private long tBlockMin = 0;
	private long tBlockMax = 0;

	private Date tBlockStart = null;
	private Date tBlockEnd = null;

	private String dateField = "";

	private int numLinesBlock = 0;
	private int blockParseIndex = 0;

	private String server = "";
	private String jvm = "";
	private String logFilename = "";

	private final DataPointLogFilenameSortable logFilenameInfo = new DataPointLogFilenameSortable();

	final LogTimePrefix newLogTimePrefix = new LogTimePrefix();

	private int procTimeAtError = 0;

	private String remoteId = "";
	private String sessionId = "";
	private String id = "";

	private String urlAtError = "";
	private String urlLastUrl = "";
	private String urlLastBeginRequest = "";
	private String urlLastEndRequest = "";

	/**
	 * Return object as string.
	 */
	public String toString() {
		return String.format("[ErrorDataPoint (ie?=%s fieldUsrAgt=%s) %s]", this.browserInternetExplorer,
				this.browserSortableQuickUserAgent, newLogTimePrefix);
	}

	public void reseterr() {
		errorTypeNullPointerException = false;
		errorTypeSyncTimeoutException = false;
	}

	public void resetbrowser() {
		browserInternetExplorer = false;
		browserFirefox = false;
		browserIPad = false;
		browserIPhone = false;
		browserAndroid = false;
		browserMac = false;
		browserChrome = false;
		browserIEMobile = false;
		browserUnknown = false;
		ie7 = false;
		ie8 = false;
		ie9 = false;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * @param errorMessage
	 *            the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	/**
	 * @return the lineNumberInFile
	 */
	public long getLineNumberInFile() {
		return lineNumberInFile;
	}
	/**
	 * @param lineNumberInFile
	 *            the lineNumberInFile to set
	 */
	public void setLineNumberInFile(long lineNumberInFile) {
		this.lineNumberInFile = lineNumberInFile;
	}
	/**
	 * @return the browserInternetExplorer
	 */
	public boolean isBrowserInternetExplorer() {
		return browserInternetExplorer;
	}
	/**
	 * @param browserInternetExplorer
	 *            the browserInternetExplorer to set
	 */
	public void setBrowserInternetExplorer(boolean browserInternetExplorer) {
		this.browserInternetExplorer = browserInternetExplorer;
	}
	/**
	 * @return the browserIPad
	 */
	public boolean isBrowserIPad() {
		return browserIPad;
	}
	/**
	 * @param browserIPad
	 *            the browserIPad to set
	 */
	public void setBrowserIPad(boolean browserIPad) {
		this.browserIPad = browserIPad;
	}
	/**
	 * @return the browserIPhone
	 */
	public boolean isBrowserIPhone() {
		return browserIPhone;
	}
	/**
	 * @param browserIPhone
	 *            the browserIPhone to set
	 */
	public void setBrowserIPhone(boolean browserIPhone) {
		this.browserIPhone = browserIPhone;
	}
	/**
	 * @return the browserAndroid
	 */
	public boolean isBrowserAndroid() {
		return browserAndroid;
	}
	/**
	 * @param browserAndroid
	 *            the browserAndroid to set
	 */
	public void setBrowserAndroid(boolean browserAndroid) {
		this.browserAndroid = browserAndroid;
	}
	/**
	 * @return the browserChrome
	 */
	public boolean isBrowserChrome() {
		return browserChrome;
	}
	/**
	 * @param browserChrome
	 *            the browserChrome to set
	 */
	public void setBrowserChrome(boolean browserChrome) {
		this.browserChrome = browserChrome;
	}
	/**
	 * @return the browserUnknown
	 */
	public boolean isBrowserUnknown() {
		return browserUnknown;
	}
	/**
	 * @param browserUnknown
	 *            the browserUnknown to set
	 */
	public void setBrowserUnknown(boolean browserUnknown) {
		this.browserUnknown = browserUnknown;
	}
	/**
	 * @return the browserVersion
	 */
	public String getBrowserVersion() {
		return browserVersion;
	}
	/**
	 * @param browserVersion
	 *            the browserVersion to set
	 */
	public void setBrowserVersion(String browserVersion) {
		this.browserVersion = browserVersion;
	}
	/**
	 * @return the browserFullUserAgent
	 */
	public String getBrowserFullUserAgent() {
		return browserFullUserAgent;
	}
	/**
	 * @param browserFullUserAgent
	 *            the browserFullUserAgent to set
	 */
	public void setBrowserFullUserAgent(String browserFullUserAgent) {
		this.browserFullUserAgent = browserFullUserAgent;
	}
	/**
	 * @return the browserSortableQuickUserAgent
	 */
	public String getBrowserSortableQuickUserAgent() {
		return browserSortableQuickUserAgent;
	}
	/**
	 * @param browserSortableQuickUserAgent
	 *            the browserSortableQuickUserAgent to set
	 */
	public void setBrowserSortableQuickUserAgent(String browserSortableQuickUserAgent) {
		this.browserSortableQuickUserAgent = browserSortableQuickUserAgent;
	}
	/**
	 * @return the tBlockMin
	 */
	public long getTBlockMin() {
		return tBlockMin;
	}
	/**
	 * @param blockMin
	 *            the tBlockMin to set
	 */
	public void setTBlockMin(long blockMin) {
		tBlockMin = blockMin;
	}
	/**
	 * @return the tBlockMax
	 */
	public long getTBlockMax() {
		return tBlockMax;
	}
	/**
	 * @param blockMax
	 *            the tBlockMax to set
	 */
	public void setTBlockMax(long blockMax) {
		tBlockMax = blockMax;
	}
	/**
	 * @return the tBlockStart
	 */
	public Date getTBlockStart() {
		return tBlockStart;
	}
	/**
	 * @param blockStart
	 *            the tBlockStart to set
	 */
	public void setTBlockStart(Date blockStart) {
		tBlockStart = blockStart;
	}
	/**
	 * @return the tBlockEnd
	 */
	public Date getTBlockEnd() {
		return tBlockEnd;
	}
	/**
	 * @param blockEnd
	 *            the tBlockEnd to set
	 */
	public void setTBlockEnd(Date blockEnd) {
		tBlockEnd = blockEnd;
	}
	/**
	 * @return the dateField
	 */
	public String getDateField() {
		return dateField;
	}
	/**
	 * @param dateField
	 *            the dateField to set
	 */
	public void setDateField(String dateField) {
		this.dateField = dateField;
	}
	/**
	 * @return the numLinesBlock
	 */
	public int getNumLinesBlock() {
		return numLinesBlock;
	}
	/**
	 * @param numLinesBlock
	 *            the numLinesBlock to set
	 */
	public void setNumLinesBlock(int numLinesBlock) {
		this.numLinesBlock = numLinesBlock;
	}
	/**
	 * @return the blockParseIndex
	 */
	public int getBlockParseIndex() {
		return blockParseIndex;
	}
	/**
	 * @param blockParseIndex
	 *            the blockParseIndex to set
	 */
	public void setBlockParseIndex(int blockParseIndex) {
		this.blockParseIndex = blockParseIndex;
	}
	/**
	 * @return the server
	 */
	public String getServer() {
		return server;
	}
	/**
	 * @param server
	 *            the server to set
	 */
	public void setServer(String server) {
		this.server = server;
	}
	/**
	 * @return the jvm
	 */
	public String getJvm() {
		return jvm;
	}
	/**
	 * @param jvm
	 *            the jvm to set
	 */
	public void setJvm(String jvm) {
		this.jvm = jvm;
	}
	/**
	 * @return the logFilename
	 */
	public String getLogFilename() {
		return logFilename;
	}
	/**
	 * @param logFilename
	 *            the logFilename to set
	 */
	public void setLogFilename(String logFilename) {
		this.logFilename = logFilename;
	}

	/**
	 * @return the remoteId
	 */
	public String getRemoteId() {
		return remoteId;
	}
	/**
	 * @param remoteId
	 *            the remoteId to set
	 */
	public void setRemoteId(String remoteId) {
		this.remoteId = remoteId;
	}
	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}
	/**
	 * @param sessionId
	 *            the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	/**
	 * @return
	 */
	public String getId() {
		return id;
	}
	/**
	 * @return the logFilenameInfo
	 */
	public DataPointLogFilenameSortable getLogFilenameInfo() {
		return logFilenameInfo;
	}
	/**
	 * @return the browserFirefox
	 */
	public boolean isBrowserFirefox() {
		return browserFirefox;
	}
	/**
	 * @param browserFirefox
	 *            the browserFirefox to set
	 */
	public void setBrowserFirefox(boolean browserFirefox) {
		this.browserFirefox = browserFirefox;
	}
	/**
	 * @return the browserIEMobile
	 */
	public boolean isBrowserIEMobile() {
		return browserIEMobile;
	}
	/**
	 * @param browserIEMobile
	 *            the browserIEMobile to set
	 */
	public void setBrowserIEMobile(boolean browserIEMobile) {
		this.browserIEMobile = browserIEMobile;
	}

	/**
	 * @return the browserMac
	 */
	public boolean isBrowserMac() {
		return browserMac;
	}

	/**
	 * @param browserMac
	 *            the browserMac to set
	 */
	public void setBrowserMac(boolean browserMac) {
		this.browserMac = browserMac;
	}

	/**
	 * @return the shortErrorMsgType
	 */
	public String getShortErrorMsgType() {
		return shortErrorMsgType;
	}

	/**
	 * @param shortErrorMsgType
	 *            the shortErrorMsgType to set
	 */
	public void setShortErrorMsgType(String shortErrorMsgType) {
		this.shortErrorMsgType = shortErrorMsgType;
	}

	/**
	 * @return the errorTypeNullPointerException
	 */
	public boolean isErrorTypeNullPointerException() {
		return errorTypeNullPointerException;
	}

	/**
	 * @param errorTypeNullPointerException
	 *            the errorTypeNullPointerException to set
	 */
	public void setErrorTypeNullPointerException(boolean errorTypeNullPointerException) {
		this.errorTypeNullPointerException = errorTypeNullPointerException;
	}

	/**
	 * @return the errorTypeSyncTimeoutException
	 */
	public boolean isErrorTypeSyncTimeoutException() {
		return errorTypeSyncTimeoutException;
	}

	/**
	 * @param errorTypeSyncTimeoutException
	 *            the errorTypeSyncTimeoutException to set
	 */
	public void setErrorTypeSyncTimeoutException(boolean errorTypeSyncTimeoutException) {
		this.errorTypeSyncTimeoutException = errorTypeSyncTimeoutException;
	}

	/**
	 * @return the linePrefixFile
	 */
	public String getLinePrefixFile() {
		return linePrefixFile;
	}

	/**
	 * @param linePrefixFile
	 *            the linePrefixFile to set
	 */
	public void setLinePrefixFile(String linePrefixFile) {
		this.linePrefixFile = linePrefixFile;
	}

	/**
	 * @return the lineTimePrefixAppr
	 */
	public String getLineTimePrefixAppr() {
		return lineTimePrefixAppr;
	}

	/**
	 * @param lineTimePrefixAppr
	 *            the lineTimePrefixAppr to set
	 */
	public void setLineTimePrefixAppr(String lineTimePrefixAppr) {
		this.lineTimePrefixAppr = lineTimePrefixAppr;
	}

	/**
	 * @return the externalHasTimeoutError
	 */
	public boolean isExternalHasTimeoutError() {
		return externalHasTimeoutError;
	}

	/**
	 * @param externalHasTimeoutError
	 *            the externalHasTimeoutError to set
	 */
	public void setExternalHasTimeoutError(boolean externalHasTimeoutError) {
		this.externalHasTimeoutError = externalHasTimeoutError;
	}

	/**
	 * @return the procTimeAtError
	 */
	public int getProcTimeAtError() {
		return procTimeAtError;
	}

	/**
	 * @param procTimeAtError
	 *            the procTimeAtError to set
	 */
	public void setProcTimeAtError(int procTimeAtError) {
		this.procTimeAtError = procTimeAtError;
	}

	/**
	 * @return the ie7
	 */
	public boolean isIe7() {
		return ie7;
	}

	/**
	 * @param ie7
	 *            the ie7 to set
	 */
	public void setIe7(boolean ie7) {
		this.ie7 = ie7;
	}

	/**
	 * @return the ie8
	 */
	public boolean isIe8() {
		return ie8;
	}

	/**
	 * @param ie8
	 *            the ie8 to set
	 */
	public void setIe8(boolean ie8) {
		this.ie8 = ie8;
	}

	/**
	 * @return the ie9
	 */
	public boolean isIe9() {
		return ie9;
	}

	/**
	 * @param ie9
	 *            the ie9 to set
	 */
	public void setIe9(boolean ie9) {
		this.ie9 = ie9;
	}

	/**
	 * @return the urlAtError
	 */
	public String getUrlAtError() {
		return urlAtError;
	}

	/**
	 * @param urlAtError
	 *            the urlAtError to set
	 */
	public void setUrlAtError(String urlAtError) {
		this.urlAtError = urlAtError;
	}

	/**
	 * @return the urlLastUrl
	 */
	public String getUrlLastUrl() {
		return urlLastUrl;
	}

	/**
	 * @param urlLastUrl
	 *            the urlLastUrl to set
	 */
	public void setUrlLastUrl(String urlLastUrl) {
		this.urlLastUrl = urlLastUrl;
	}

	/**
	 * @return the urlLastBeginRequest
	 */
	public String getUrlLastBeginRequest() {
		return urlLastBeginRequest;
	}

	/**
	 * @param urlLastBeginRequest
	 *            the urlLastBeginRequest to set
	 */
	public void setUrlLastBeginRequest(String urlLastBeginRequest) {
		this.urlLastBeginRequest = urlLastBeginRequest;
	}

	/**
	 * @return the urlLastEndRequest
	 */
	public String getUrlLastEndRequest() {
		return urlLastEndRequest;
	}

	/**
	 * @param urlLastEndRequest
	 *            the urlLastEndRequest to set
	 */
	public void setUrlLastEndRequest(String urlLastEndRequest) {
		this.urlLastEndRequest = urlLastEndRequest;
	}

} // End of class //
