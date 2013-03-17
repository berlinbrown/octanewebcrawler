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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.berlin.logs.scan.GlobalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main.
 * 
 * Enter the :  ant_search/search_result/ directory
 * Using linux command:  $ grep -H -A2 -B60 ERROR * > ../all_err.txt
 * Run with;  
 * java -Xms40m -Xmx200m -server -cp "bin" MainParseCritical < all_err.txt
 * 
 * ErrorsTodayByTime
 * ErrorsTodayByErrorType
 * ErrorsTodayByJvm
 * ErrorsAllByTime
 * ErrorsAllByBrowser
 *
 */
public class MainParseCritical {
  
    private static final Logger LOGGER = LoggerFactory.getLogger(MainParseCritical.class);
  
    /**
     * Main.
     * 
     * @param args
     */
    public static void main(final String [] args) {
        
        final long start = System.currentTimeMillis();
        LOGGER.info("Running MainParseCritical Logs ...");                
        BufferedReader br = null;
        try {
            
            final GlobalConfiguration globalConf = new GlobalConfiguration().load(GlobalConfiguration.PATH);
            LOGGER.info(globalConf.toString());
            
            final FileInputStream fis = new FileInputStream(globalConf.getWorkingDirectory() + "\\all_err.txt");
            final BufferedInputStream bis = new BufferedInputStream(fis);
            br = new BufferedReader(new InputStreamReader(bis));
            final ILogErrorParser p = new Parser(globalConf).parse(br);
            p.report();
            final long diff = System.currentTimeMillis() - start;
            LOGGER.info("Markers found = " + p.getMarkersFound());
            LOGGER.info("Done - diff=" + diff + " ms");
            
        } catch(final Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } // End of the try - catch finally //
    }   
} // End of the class //
