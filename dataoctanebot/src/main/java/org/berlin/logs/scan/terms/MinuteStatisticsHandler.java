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

import java.util.Map;

/**
 * Minute statistics handler.
 */
public class MinuteStatisticsHandler {
         
    int beginTotal = 0;
    int endTotal = 0;
    int errorTotal = 0;
    int exceptionTotal = 0;
    int nullptrTotal = 0;
    int criticalerrorTotal = 0;  
    int searchTermTotal = 0;
    
    LineTermInfoHandler dataAtTimeOfStat = new LineTermInfoHandler(null);
    
    public static MinuteStatisticsHandler buildStatisticsMapHelper(final Map<Long, MinuteStatisticsHandler> statsMapByMinute, final LineTermInfoHandler dataAtTimeOfStat) {
        if (dataAtTimeOfStat == null) {
            return null;
        }
        if (dataAtTimeOfStat.timeByTenMin <= 1) {
            return null;
        }
        // Place on map if available
        final MinuteStatisticsHandler chkInfo = statsMapByMinute.get(dataAtTimeOfStat.timeByTenMin);
        if (chkInfo == null) {
            final MinuteStatisticsHandler info = new MinuteStatisticsHandler();
            info.dataAtTimeOfStat = dataAtTimeOfStat;
            statsMapByMinute.put(dataAtTimeOfStat.timeByTenMin, info);
            info.parse(dataAtTimeOfStat);
            return info;
        } else {
            statsMapByMinute.get(dataAtTimeOfStat.timeByTenMin).parse(dataAtTimeOfStat);
            // process existing handler
            return statsMapByMinute.get(dataAtTimeOfStat.timeByTenMin);
        }
    }    
    public void parse(final LineTermInfoHandler dataAtTimeOfStat) {
        if (dataAtTimeOfStat == null) {
            return;
        }               
        this.dataAtTimeOfStat = dataAtTimeOfStat;
        if (this.dataAtTimeOfStat.javaDate == null) {
            return;
        }
        if (this.dataAtTimeOfStat.timeByTenMin == 0) {
            return;
        }
        switch(this.dataAtTimeOfStat.termType) {
        case begin:
            this.beginTotal++;
            break;
        case end:
            this.endTotal++;
            break;            
        case error:
            this.errorTotal++;
            break;
        case exception:
            this.exceptionTotal++;
            break;
        case nullptr:
            this.nullptrTotal++;
            break;
        case criticalerror:            
            this.criticalerrorTotal++;
            break;
        case searchterm:
            this.searchTermTotal++;
            break;            
        default: break;
        };                        
    }    
} // End of Class //
