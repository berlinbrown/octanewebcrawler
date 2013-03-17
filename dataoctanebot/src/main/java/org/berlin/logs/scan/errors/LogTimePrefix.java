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
 * Berlin Brown - Parser for critical errors files.
 */
package org.berlin.logs.scan.errors;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Log time prefix.
 */
public class LogTimePrefix {
    
    public static final int OFFSET_PREV_DAY = 1;
    public static final int OFFSET_PREV_HOURS = 9;
    public static final int OFFSET_PREV_WEEK = 9;
    public static final int MIN_TIME_LOG_LEN = 62;
    public static final int INDEX_FORMAT_CHECK_1 = 24;
    public static final int INDEX_FORMAT_CHECK_2 = 26;
    
    public static final int F1 = 0;
    public static final int F2 = 1;
    public static final int F3 = 2;
    
    private static final int off2 = 2;
    private static final int off3 = 11;
    
    public static final int FI_1 [][] = {
        { 0,  23 },        
        { 24, 32 }, 
        { 33, 35 },        
        { 36, 40 },
        { 41, 43 },
        { 44, 46 },        
        { 47, 49 },
        { 50, 52 }, 
        { 53, 55 }, 
    };
    
    public static final int FI_2 [][] = {
        { 0+off2,  23+off2 },        
        { 24+off2, 32+off2 }, 
        { 33+off2, 35+off2 },        
        { 36+off2, 40+off2 },
        { 41+off2, 43+off2 },
        { 44+off2, 46+off2 },        
        { 47+off2, 49+off2 },
        { 50+off2, 52+off2 }, 
        { 53+off2, 55+off2 }, 
    };
    
    public static final int FI_3 [][] = {
        { 0+off3,  23+off3 },        
        { 24+off3, 32+off3 }, 
        { 33+off3, 35+off3 },        
        { 36+off3, 40+off3 },
        { 41+off3, 43+off3 },
        { 44+off3, 46+off3 },        
        { 47+off3, 49+off3 },
        { 50+off3, 52+off3 }, 
        { 53+off3, 55+off3 }, 
    };    
    
    String prefix = "";
    String index = "";
    String server = "";
    String jvm = "";
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
        
    boolean todayOrYesterday    = false;
    boolean errorAtWeek = false;
    
    public static final String R1 = "2011.06.11 00:01:00";
    public static final String R2 = "2011.06.22 00:01:00";    
    
    public static final String FORMAT_DATE_TIME = "yyyy.MM.dd HH:mm:ss";
        
    /**  
     * 0 format1,
     * 1 format2,
     * 2 format3
     */
    int format = 0;
    
    /**
     * Determine filename server format.
     * 
     * @param line
     * @return
     */
    protected int format(final String line) {
        if (line == null) {
            return 0;
        }
        if (line.length() < MIN_TIME_LOG_LEN) {
            return 0;
        }
        final int index1chk = INDEX_FORMAT_CHECK_1;
        final int index2chk = INDEX_FORMAT_CHECK_2;
        final Character c1 = line.charAt(index1chk);
        if (Character.isLetter(c1)) {
            return F1;
        } else {
            final Character c2 = line.charAt(index2chk);
            if (Character.isLetter(c2)) {
                return F2;
            } else {
                return F3;
            }            
        } // End of the if - else //
    }
    
    /**
     * Parse.
     * 
     * @param line
     * @return
     */
    public LogTimePrefix parse(final String line) {
        if (line == null) {
            return this;
        }
        if (line.length() < MIN_TIME_LOG_LEN) {
            return this;
        }
        this.format = this.format(line);
        // Format one.
        int [][] idx = FI_1;
        if (format == F1) {
            idx = FI_1;
        } else if (format == F2) {
            idx = FI_2;
        } else if (format == F3) {
            idx = FI_3;
        }
        this.prefix = line.substring(idx[0][0], idx[0][1]);       
        this.server = line.substring(idx[1][0], idx[1][1]);
        this.jvm = line.substring(idx[2][0], idx[2][1]);        
        this.year = line.substring(idx[3][0], idx[3][1]);
        this.month = line.substring(idx[4][0], idx[4][1]);
        this.day = line.substring(idx[5][0], idx[5][1]);        
        this.hour = line.substring(idx[6][0], idx[6][1]);
        this.minute = line.substring(idx[7][0], idx[7][1]);
        this.seconds = line.substring(idx[8][0], idx[8][1]);
        this.dayField = String.format("x%s%s%s", this.month, this.day, this.year);                
        final String tmpj = String.format("%s.%s.%s %s:%s:%s", this.year, this.month, this.day,
                this.hour, this.minute, this.seconds);
        // 2001.07.04 AD 12:08:56
        try {                       
            final SimpleDateFormat f = new SimpleDateFormat(FORMAT_DATE_TIME);
            final Date d = f.parse(tmpj);
            javaDate = d;
        } catch(final Exception e) {
            System.err.println("ERROR: format=" + this.format + " line="+ line);
            e.printStackTrace();
        }                                 
        try {                       
            final Calendar c = GregorianCalendar.getInstance();
            c.add(Calendar.DATE, -OFFSET_PREV_DAY);
            c.add(Calendar.HOUR, -OFFSET_PREV_HOURS);   
            if (this.javaDate != null) {
                if (this.javaDate.after(c.getTime())) {
                    this.todayOrYesterday = true;                    
                }
            }            
        } catch(final Exception e) {
            e.printStackTrace();
        }        
        try {                       
            final Calendar c = GregorianCalendar.getInstance();
            c.add(Calendar.DATE, -OFFSET_PREV_WEEK);
            c.set(Calendar.HOUR, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            if (this.javaDate != null) {
                if (this.javaDate.after(c.getTime())) {
                    this.errorAtWeek = true;                    
                }
            }            
        } catch(final Exception e) {
            e.printStackTrace();
        }        
        return this;
    }

    @Override
    public String toString() {
        return String.format("[%s %s, f=%s, %s-%s-%s (%s:%s)]", server, jvm, this.format, this.year, this.month, this.day, this.hour, this.minute);
    }
    
} // End of the Class //
