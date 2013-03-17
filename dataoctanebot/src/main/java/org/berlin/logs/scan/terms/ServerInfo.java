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

public class ServerInfo {

    public static final int OFFSET_PREV_DAY = 1;
    public static final int OFFSET_PREV_HOURS = 9;
    public static final int OFFSET_PREV_WEEK = 9;
    public static final int MIN_TIME_LOG_LEN = 35;
    
    public static final int INDEX_FORMAT_CHECK_1 = 24;
    public static final int INDEX_FORMAT_CHECK_2 = 26;
    
    public static final int F1 = 0;
    public static final int F2 = 1;
    public static final int F3 = 2;
    
    String serverName = "";
    String serverNumber = "";
    int serverNumberVal = -1;
    int format = 0;
    
    public static ServerInfo parseAndLoad(final String filename) {
        // Example line: 
        final ServerInfo inf = new ServerInfo();
        if (filename.length() < MIN_TIME_LOG_LEN) {
            return inf;
        }
        inf.format = inf.format(filename);
        if (inf.format == F1) {
            inf.serverName = filename.substring(24, 32);  
            inf.serverNumber = filename.substring(33, 35);
            inf.serverNumberVal = Integer.parseInt(inf.serverNumber);
        } else if (inf.format == F2) {
           
        } else if (inf.format == F3) {
            if (filename.length() >= 46) {
                inf.serverName = filename.substring(35, 43);
                inf.serverNumber = filename.substring(44, 46);
                inf.serverNumberVal = Integer.parseInt(inf.serverNumber);
            }
        }
        return inf;
    }
    
    /**
     * Determine filename format.
     * 
     * @param line
     * @return
     */
    protected int format(final String line) {
        if (line == null) {
            return F1;
        }
        if (line.length() < MIN_TIME_LOG_LEN) {
            return F1;
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
    
    public String toString() {
        return "[ServerInf : filenameFormat=" + this.format+ " serv=" + this.serverName + "." + this.serverNumberVal + "]";
    }
    
    public boolean valid() {
        return !(this.serverName == null || this.serverName.length() == 0); 
    }
    
} // End of the class //
