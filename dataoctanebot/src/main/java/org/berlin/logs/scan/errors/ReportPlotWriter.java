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
package org.berlin.logs.scan.errors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ReportPlotWriter implements IPlotWriter {
    
  private final Statistics stats;
    
    /**
     * Default constructor for writer.
     * 
     * @param listErrorDataPoints
     * @param stats
     */
    public ReportPlotWriter(final Statistics stats) {    
        this.stats = stats;
    }       
    
    /**
     * Return gnu plot stats.
     * 
     * @param outFilename
     * @param in
     * @param out
     * @param plotline
     */
    public void statisticsReportGnuPlotHistoScript1(final String outFilename, final String in, final String out, final String plotline) {
        final String o = outFilename + "_script.plot.sh";
        final File f = new File(o);          
        FileWriter fw = null;
                
        try {
            fw = new FileWriter(f);
            
            fw.write("#!/bin/sh");
            fw.write("\n");            
            fw.write("INPUT_PLOT_FILE="+ in);
            fw.write("\n");            
            fw.write("OUTPUT_IMG_FILE=" + out);
            fw.write("\n");            
            fw.write("echo \"set terminal pngcairo size 600,450");
            fw.write("\n");            
            fw.write("set output '${OUTPUT_IMG_FILE}'");
            fw.write("\n");            
            fw.write("set title 'Critical Errors By Type (Recently and Week)'");
            fw.write("\n");            
            fw.write("set size 1,1");
            fw.write("\n");            
            fw.write("set key left top");
            fw.write("\n");            
            fw.write("set autoscale");
            fw.write("\n");            
            fw.write("set xlabel 'Type Of Error'");
            fw.write("\n");            
            fw.write("set ylabel 'Number of Errors'");
            fw.write("\n");           
            fw.write("set style fill pattern");
            fw.write("\n");           
            fw.write("set style histogram clustered");
            fw.write("\n");            
            fw.write("set xtic rotate by -45 scale 0.8");
            fw.write("\n");
            
            // Example: fw.write("plot '${INPUT_PLOT_FILE}' u 2:xtic(1) t 'Recently' w histograms, '' u 3 t 'Week' w histograms");
            fw.write(plotline);
            fw.write("\n");                       
            fw.write("\n");            
            fw.write("\" > gnuplot_tmp_cmd.tmp");
            fw.write("\n");            
            fw.write("gnuplot gnuplot_tmp_cmd.tmp > /dev/null");
            fw.write("\n");
                    
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {        
            try {
                fw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } // End of try write
    }
            
    public void statisticsReportGnuPlotHisto(final String outFilename) {
        final String o = outFilename + ".dat";
        final File f = new File(o);          
        FileWriter fw = null;
        try {
            fw = new FileWriter(f);
            fw.write("# Type\t\tRecently\t\tWeek");
            fw.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }                        
        try {                    
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
        
    }    

    /**
     * Return gnu plot stats 2.
     * 
     * @param outFilename
     */
    protected void statisticsReportGnuPlotHisto2(final String outFilename) {
        final String o = outFilename + "2.dat";
        final File f = new File(o);          
        FileWriter fw = null;
        try {
            fw = new FileWriter(f);
            fw.write("# Type\t\tAllTime");
            fw.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }                
        try {
            fw.write(Parser.NL);
            fw.write("IE7\t\t" + stats.ie7);            
            fw.write(Parser.NL);
            
            fw.write("IE8\t\t" + stats.ie8);
            fw.write(Parser.NL);
            
            fw.write("IE9\t\t" + stats.ie9);            
            fw.write(Parser.NL);
            
            fw.write("iPad\t\t" + stats.ipad);
            fw.write(Parser.NL);
            
            fw.write("Android\t\t" + stats.android);
            fw.write(Parser.NL);
            
            fw.write("iPhone\t\t" + stats.iphone);
            fw.write(Parser.NL);
            
            fw.write("WinMobile\t\t" + stats.winmobile);
            fw.write(Parser.NL);                       
            
        } catch (IOException e1) {
            e1.printStackTrace();
        }                
        try {                    
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
    }
    
} // End of the class //
