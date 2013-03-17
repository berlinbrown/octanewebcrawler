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

import java.util.LinkedHashMap;
import java.util.Map;

public class MapByServerParser {

    public void parse(final Map<Long, Map<String, MinuteStatisticsHandler>> statsByServerByJVM, final MinuteStatisticsHandler minStats) {
        if (minStats == null) {
            return;
        }
        if (minStats.dataAtTimeOfStat == null) {
            return;
        }
        final long timeKey = minStats.dataAtTimeOfStat.timeByTenMin;
        
        if (statsByServerByJVM.get(timeKey) == null) {
            final Map<String, MinuteStatisticsHandler> newMap = new LinkedHashMap<String, MinuteStatisticsHandler>();
            if (minStats.dataAtTimeOfStat.serverInfo.valid()) {
                final String fullServerKey = (minStats.dataAtTimeOfStat.serverInfo.serverName+"_"+minStats.dataAtTimeOfStat.serverInfo.serverNumberVal);
                newMap.put(fullServerKey, minStats);
                statsByServerByJVM.put(timeKey, newMap);
            }            
        } else {            
            final Map<String, MinuteStatisticsHandler> curMap = statsByServerByJVM.get(timeKey);
            final String fullServerKey = (minStats.dataAtTimeOfStat.serverInfo.serverName+"_"+minStats.dataAtTimeOfStat.serverInfo.serverNumberVal);
            if (curMap.get(fullServerKey) == null) {                
                curMap.put(fullServerKey, minStats);                
            }            
        } // End of if - time by key //        
    }
    
} // End of the Class //
