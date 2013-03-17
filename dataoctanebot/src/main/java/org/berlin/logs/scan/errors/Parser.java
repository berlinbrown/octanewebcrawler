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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.berlin.logs.scan.GlobalConfiguration;

/**
 * Parse error data points.
 */
public class Parser implements ILogErrorParser {

    public static String NL = System.getProperty("line.separator");
    
    public static final String FILE_ERROR_LOG_STATS = "error_log_stats.txt";
    public static final String DATA_FILE_BY_WEEK = "LogScanErrorsParse_byWEEK.csv";
    public static final String DATA_FILE_BY_RECENTLY = "LogScanErrorsParse_byTODAY.csv";
    public static final String DATA_FILE_PLOT_WEEK = "LogScanErrorsParse_byWEEK";
    
    /*
     * On detecting user-agent strings:
     * 
     * Beginning with Internet Explorer 8, the version token reported in the user-agent string may not reflect the actual version of the browser. 
     * If Compatibility View is enabled for a webpage or the browser mode is set to an earlier version, the version token reports the earlier version.
     * 
     * For example, if you are using Internet Explorer 9 to view a webpage in Compatibility View, the version token is, by default, MSIE 7.0
     */
    public static final String MS_IE = "MSIE";
    public static final String BROWSER_NEW_IE8 = "Trident/4\\.0";
    public static final String BROWSER_NEW_IE9 = "Trident/5\\.0";
    public static final String BROWSER_NEW_IE7 = "MSIE 7\\.0";
    public static final String BROWSER_NEW_IE  = "MSIE 6";    
    public static final String BROWSER_ANDROID = "Android";  
    public static final String BROWSER_IE_MOBILE_1 = "IEMobile 8\\.12";
    public static final String BROWSER_IE_MOBILE_2 = "MSIEMobile";
    
    public static final String BROWSER_MAC = "Macintosh";
    public static final String BROWSER_IPH = "iPhone";    
    public static final String BROWSER_IPAD = "iPad";    
    
    public static final String FIELD_BROWSER_IE8 = "IE8";
    public static final String FIELD_BROWSER_IE9 = "IE9";
    public static final String FIELD_BROWSER_IE7 = "IE7";
    public static final String FIELD_BROWSER_IE  = "IE6";    
    public static final String FIELD_BROWSER_IE_NA  = "IE";
    public static final String FIELD_BROWSER_ANDROID = "Android";  
    public static final String FIELD_BROWSER_IE_MOBILE = "IEMobile";    
    public static final String FIELD_BROWSER_NA = "Unknown";
    public static final String FIELD_BROWSER_MAC = "Mac";
    
    public static final String FIELD_BROWSER_FIREFOX = "Firefox";
    public static final String FIELD_BROWSER_CHROME = "Chrome";
        
    public static final int MIN_HIGH_PROC_TIME = 56000;
    public static final int MIN_ERR_MSG_LEN = 180;
    public static final int MIN_ERR_MSG_LEN2 = 110;
    
    public static final int MIN_USR_AGENT_LEN = 24;
    public static final int MIN_LOG_LINE_FILE_PREFIX = 47;
    public static final int MIN_LOG_LINE_FILE_TIME_PREFIX = 82;
    
    public static final int MAX_LEN_URL = 124;
    
    private String lineMarker = "--";
    private int markersFound = 0;    
    private String mainErrorToken = "ERROR";
    
    private String tokenRegexUserAgent = "User-Agent";    
    private String tokenRegexSessionId = "(sessionId=(\\S+)\\|[a-zA-Z])";    
    private String tokenRegexProcTime = "(procTime=(\\d+)\\|[a-zA-Z])";                 
    private String tokenUrlCause = "(url\\.cause=(.*))$";
    private String tokenUrlCause2 = "(url\\.cause=(.*)\\|[a-zA-Z])";    
    private String tokenLastUrl = "lastUrl=.*$";
    private String tokenBeginRequest = " onBeginRequest .*$";
    private String tokenEndRequest = " onEndRequest .*$";    
    
    private final List<ParseErrorDataPoint> listErrorDataPoints = new ArrayList<ParseErrorDataPoint>(20);          
    
    private Statistics stats = new Statistics();
        
    private int blockParseNumber = 1;
    
    private final GlobalConfiguration globalConf;
    
    /**
     * Constructor for parser.
     */
    public Parser(final GlobalConfiguration globalConf) {
        this.globalConf = globalConf;
    }
    
    /**
     * Run report.
     */
    public void report() {
        
        final ReportWriter rw = new ReportWriter(listErrorDataPoints, stats);
        
        rw.statisticsReport(this.globalConf.getWorkingDirectory() + "\\" + FILE_ERROR_LOG_STATS, false);
        rw.databaseReport(this.globalConf.getWorkingDirectory() + "\\" + DATA_FILE_BY_WEEK, false, false, true);       
        rw.databaseReport(this.globalConf.getWorkingDirectory() + "\\" + DATA_FILE_BY_RECENTLY, false, true, false);       
        rw.databaseReportGraphProcTime(this.globalConf.getWorkingDirectory() + "\\" + DATA_FILE_PLOT_WEEK, 1, true, false);
        rw.databaseReportGraphProcTime(this.globalConf.getWorkingDirectory() + "\\" + DATA_FILE_PLOT_WEEK, 2, true, false);
        rw.databaseReportGraphProcTime(this.globalConf.getWorkingDirectory() + "\\" + "LogScanCErrorsParse_byWEEK", 3, true, false);
        rw.databaseReportGraphProcTime(this.globalConf.getWorkingDirectory() + "\\" + "LogScanErrorsParse_byWEEK", 4, true, false);
    }
    
    protected void parseUserAgent(final ParseErrorDataPoint errorDataPoint, final String line) {
        if (errorDataPoint == null) {
            return;
        }        
        errorDataPoint.resetbrowser();
        if (line.matches(".*" + MS_IE + ".*")) {
            if (line.matches(".*(" + BROWSER_IE_MOBILE_1 + "|" + BROWSER_IE_MOBILE_2 + ").*")) {
                errorDataPoint.setBrowserIEMobile(true);
                errorDataPoint.setBrowserSortableQuickUserAgent(FIELD_BROWSER_IE_MOBILE);                
            } else {
                errorDataPoint.setBrowserInternetExplorer(true);
                errorDataPoint.setBrowserSortableQuickUserAgent(FIELD_BROWSER_IE_NA);
                
                if (line.matches(".*(" + BROWSER_NEW_IE8 + ").*")) {
                    errorDataPoint.setBrowserSortableQuickUserAgent(FIELD_BROWSER_IE8);
                    errorDataPoint.setIe8(true);
                    
                } else if (line.matches(".*(" + BROWSER_NEW_IE9 + ").*")) {
                    errorDataPoint.setBrowserSortableQuickUserAgent(FIELD_BROWSER_IE9);
                    errorDataPoint.setIe9(true);
                    
                } else if (line.matches(".*(" + BROWSER_NEW_IE + ").*")) {
                    errorDataPoint.setBrowserSortableQuickUserAgent(FIELD_BROWSER_IE_NA);                    
                    
                } else if (line.matches(".*(" + BROWSER_NEW_IE7 + ").*")) {
                    errorDataPoint.setBrowserSortableQuickUserAgent(FIELD_BROWSER_IE7);
                    errorDataPoint.setIe7(true);
                }
            } // End of IE check //
            
        } else {
            errorDataPoint.setBrowserInternetExplorer(false);
            // Else case not IE
            if (line.matches(".*" + BROWSER_ANDROID + ".*")) {
                errorDataPoint.setBrowserAndroid(true);
                errorDataPoint.setBrowserSortableQuickUserAgent(FIELD_BROWSER_ANDROID);
                
            } else if (line.matches(".*" + BROWSER_MAC + ".*")) {
                errorDataPoint.setBrowserSortableQuickUserAgent(FIELD_BROWSER_MAC);
                
            } else if (line.matches(".*" + BROWSER_IPH + ".*")) {
                errorDataPoint.setBrowserIPhone(true);
                errorDataPoint.setBrowserSortableQuickUserAgent(BROWSER_IPH);
                
            } else if (line.matches(".*" + BROWSER_IPAD + ".*")) {
                errorDataPoint.setBrowserIPad(true);
                errorDataPoint.setBrowserSortableQuickUserAgent(BROWSER_IPAD);
                
            } else if (line.matches(".*Chrome.*")) {
                errorDataPoint.setBrowserChrome(true);
                errorDataPoint.setBrowserSortableQuickUserAgent(FIELD_BROWSER_CHROME);
                
            } else if (line.matches(".*Firefox.*")) {
                errorDataPoint.setBrowserFirefox(true);
                errorDataPoint.setBrowserSortableQuickUserAgent(FIELD_BROWSER_FIREFOX);
                
            } else {             
                errorDataPoint.setBrowserUnknown(true);
                errorDataPoint.setBrowserSortableQuickUserAgent(FIELD_BROWSER_NA);               
            }           
        } // End if ie
                
    }
    
    protected void parseErrorType(final ParseErrorDataPoint errorDataPoint, final String line) {
        if (errorDataPoint == null) {
            return;     
        }
    }
  
    protected void parseUrlCause(final ParseErrorDataPoint errorDataPoint, final String t1) {    
        final int maxUrl = MAX_LEN_URL;
        final Pattern p9 = Pattern.compile(tokenUrlCause, Pattern.DOTALL);
        final Matcher m9 = p9.matcher(t1);
        boolean hasEndLineCause = false;
        String line = "";
        if (m9.find()) {            
            final String data = t1.substring(m9.start(2), m9.end(2));
            line = data.length() > maxUrl ? data.substring(0, MAX_LEN_URL-2) : data;
            hasEndLineCause = true;
        }
        if (!hasEndLineCause) {         
            final Pattern p8 = Pattern.compile(tokenUrlCause2, Pattern.DOTALL);
            final Matcher m8 = p8.matcher(t1);            
            if (m8.find()) {                
                final String data = t1.substring(m8.start(2), m8.end(2));
                line = data.length() > maxUrl ? data.substring(0, MAX_LEN_URL-2) : data;
                line = line.replaceAll("\"", "");                
            }            
        }       
        line = line.replaceAll("\"", "");
        if (line.length() > 0) {
            errorDataPoint.setUrlAtError(line);
        }
    }
        
    protected void parseLastUrlError(final ParseErrorDataPoint errorDataPoint, final String t1) {
        final int maxUrl = MAX_LEN_URL;
        final Pattern p9 = Pattern.compile(tokenLastUrl, Pattern.DOTALL);
        final Matcher m9 = p9.matcher(t1);
        String line = "";
        if (m9.find()) {            
            final String data = t1.substring(m9.start(), m9.end());
            line = data.length() > maxUrl ? data.substring(0, 122) : data;
            line = line.replaceAll("\"", "");
        }    
        if (line.length() > 0) {
            errorDataPoint.setUrlLastUrl(line);
        }
    }
    
    protected String parseLastBeginUrl(final String regex, final String t1) {
        final int maxUrl = 120;
        final Pattern p9 = Pattern.compile(regex, Pattern.DOTALL);
        final Matcher m9 = p9.matcher(t1);
        String line = "";
        if (m9.find()) {            
            final String data = t1.substring(m9.start(), m9.end());
            if (data.length() > 28) {
                line = data.length() > maxUrl ? data.substring(26, 110) : data;
                line = line.replaceAll("\"", "");                
            }
        }        
        return line;
    }

    
    /**    
     * Parse when token found.
     * 
     * @param errorDataPoint
     * @param line
     */
    protected void parseAtTokenTerm(final ParseErrorDataPoint errorDataPoint, final String line) {
        if (errorDataPoint == null) {
            return;     
        }
        if (line.length() > MIN_LOG_LINE_FILE_PREFIX) {
            errorDataPoint.setLinePrefixFile(line.substring(0,MIN_LOG_LINE_FILE_PREFIX-1));
        }                   
        if (line.length() > MIN_LOG_LINE_FILE_TIME_PREFIX) {
            errorDataPoint.setLineTimePrefixAppr(line.substring(50,69));
        }
        errorDataPoint.newLogTimePrefix.parse(line);
        errorDataPoint.setBlockParseIndex(blockParseNumber);
        {   
            final Pattern p1 = Pattern.compile(tokenRegexSessionId );
            final Matcher m1 = p1.matcher(line);
            if (m1.find()) {
                final int errorLen = m1.start(2)+25;
                if (line.length() > errorLen) {
                    String s = line.substring(m1.start(2), m1.start(2)+23).replaceAll("\"", "").replaceAll("=", "");
                    s = s.replaceAll("=", "");
                    s = s.replaceAll("\\=", "");
                    errorDataPoint.setSessionId(s);
                }                
            } // End find session id
        }
        final Pattern p2 = Pattern.compile(tokenRegexProcTime);
        final Matcher m2 = p2.matcher(line);
        if (m2.find()) {
            try {
                errorDataPoint.setProcTimeAtError(Integer.parseInt(line.substring(m2.start(2), m2.end(2))));
            } catch(final Exception e) {
                e.printStackTrace();
            }
        } // End find session id        
        
    }
    
    /**
     * Parse block data point.
     * 
     * @param block
     * @throws IOException
     */
    protected ParseErrorDataPoint parseBlock(final String block) throws IOException {        
        if (block == null) {
            return null;
        }
        String line = "";
        final StringReader reader = new StringReader(block);
        final BufferedReader br = new BufferedReader(reader);
        int lineNumber = 0;
        final ParseErrorDataPoint errorDataPoint = new ParseErrorDataPoint();
        boolean hasParserErrMsg = false;
        final StringBuffer bufError = new StringBuffer();        
        while((line = br.readLine()) != null) {            
            lineNumber++;
            if (line.length() > 0) {
                if (hasParserErrMsg) {
                  bufError.append(line);                                    
                } // End if                                                                 
                {
                    final Pattern p = Pattern.compile(this.mainErrorToken);
                    final Matcher m = p.matcher(line);
                    if (m.find()) {
                        blockParseNumber++;
                        this.parseAtTokenTerm(errorDataPoint, line);
                        // Token found //
                        hasParserErrMsg = true;                    
                    } // End of If //
                }                
                {
                    final Pattern pUsr = Pattern.compile(this.tokenRegexUserAgent);
                    final Matcher mUsr = pUsr.matcher(line);
                    if (mUsr.find()) {
                        this.parseUserAgent(errorDataPoint, line);
                        final String s9 = line.substring(mUsr.start());
                        if (s9.length() > MIN_USR_AGENT_LEN) {
                            errorDataPoint.setBrowserFullUserAgent(line.substring(mUsr.start() + (MIN_USR_AGENT_LEN-3)));
                        }
                    } // End of the find
                }                
                this.parseUrlCause(errorDataPoint, line);                
                this.parseLastUrlError(errorDataPoint, line);
                final String a = this.parseLastBeginUrl(tokenBeginRequest, line);
                if (a.length() > 0) {
                    errorDataPoint.setUrlLastBeginRequest(a.length() > 0 ? a : "");
                }
                final String b = this.parseLastBeginUrl(tokenEndRequest, line);
                if (b.length() > 0) {
                    errorDataPoint.setUrlLastEndRequest(b.length() > 0 ? b : "");
                }
                /************
                 * At end of block parse
                 *************/                
                this.parseErrorType(errorDataPoint, line);                                               
            } // End of if - valid line                                              
        } // End of the while //        
        String errmsg = bufError.toString().replaceAll("[\n\r\t]", "|");        
        errmsg = errmsg.replaceAll("'", "");
        errmsg = errmsg.replaceAll("\"", "");
        errmsg = errmsg.replaceAll(",", ""); 
        
        if (errmsg.length() > MIN_ERR_MSG_LEN2) {
            errmsg = errmsg.substring(40, MIN_ERR_MSG_LEN2-8);
        } else if (errmsg.length() > MIN_ERR_MSG_LEN) {
            errmsg = errmsg.substring(30, MIN_ERR_MSG_LEN-8);
        }         
        /*******************************
         * Data collection at end of block processing.
         *******************************/     
        if (stats.minDate == null) {
            if (errorDataPoint.newLogTimePrefix.javaDate != null) {
                stats.minDate = errorDataPoint.newLogTimePrefix.javaDate;
            }
        } else {            
            if (errorDataPoint.newLogTimePrefix.javaDate != null) {
                final Calendar c = GregorianCalendar.getInstance();
                c.setTime(errorDataPoint.newLogTimePrefix.javaDate);
                if (c.before(stats.minDate)) {
                    stats.minDate = errorDataPoint.newLogTimePrefix.javaDate;
                }
            }
       }                                                                       
        if (errorDataPoint.isBrowserIPhone()) {
            stats.iphone++;
        }
        if (errorDataPoint.isBrowserAndroid()) {
            stats.android++;
        }
        if (errorDataPoint.isBrowserIEMobile()) {
            stats.winmobile++;
        }        
        if (errorDataPoint.isIe7()) {
            stats.ie7++;
        }
        if (errorDataPoint.isIe8()) {
            stats.ie8++;
        }
        if (errorDataPoint.isIe9()) {
            stats.ie9++;
        }
        
        if (errorDataPoint.isBrowserInternetExplorer()) {
            stats.ie++;            
        } else {
            stats.nonIe++;
        } // End of Data Collection //        
        
        if (errorDataPoint.newLogTimePrefix.todayOrYesterday) {
            stats.errorsToday++;
        }
        if (errorDataPoint.newLogTimePrefix.errorAtWeek) {
            stats.errorsWeek++;
        }
                  
        /********************
         * End of stats
         ********************/       
        errorDataPoint.setErrorMessage("(... " + errmsg + " ...)");
        return errorDataPoint;
    }
    
    /**
     * Parse.
     * 
     * @param reader
     * @return
     * @throws IOException
     */
    public Parser parse(final BufferedReader reader) throws IOException {
        this.stats = new Statistics();
        String line = null;
        StringBuffer buf = new StringBuffer(500);
        String lastblock = "";
        while((line = reader.readLine()) != null) {
            if (line.equals(this.lineMarker)) {                                
                markersFound++;               
                lastblock = buf.toString();
                Object o = null;
                try { 
                    o = this.parseBlock(lastblock);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                if (o != null) {
                    listErrorDataPoints.add((ParseErrorDataPoint) o);
                }
                buf = new StringBuffer(500);
            } else {
                buf.append(line);
                buf.append(NL);
            }
        } // End of the while //
        if (buf.length() > 4) {
            markersFound++;               
            lastblock = buf.toString();
            this.parseBlock(lastblock);
        }
        stats.totalErrors = listErrorDataPoints.size(); 
        return this;
    }

    /**
     * @param lineMarker the lineMarker to set
     */
    public void setLineMarker(String lineMarker) {
        this.lineMarker = lineMarker;
    }

    /**
     * @return the markerFound
     */
    public int getMarkersFound() {
        return markersFound;
    }
    
} // End of the class //

