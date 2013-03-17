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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.berlin.logs.scan.GlobalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterFileProcessor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterFileProcessor.class);  
  
    private final GlobalConfiguration globalConf;    
    private long statsTotalSearchFound = 0;
        
    boolean writeOutputFile = true;
    String outputFilename = "xxx-output.log";    
    private PrintWriter printWriter;           
    int smallerDefaultReadBufferSize = 12 * 1024 * 1024;    
    boolean readMoreLargeFile = true;
    int formatType = 1;
    
    Map<String, Integer> sessionSet = new Hashtable<String, Integer>();
    Map<String, Integer> agentSet = new Hashtable<String, Integer>();
    Map<String, Date> minSessionDateSet = new Hashtable<String, Date>();
    
    /**
     * Constructor for LogSearch.
     * 
     * @param globalConf
     */
    public FilterFileProcessor(final GlobalConfiguration globalConf, final int format) {
        this.globalConf = globalConf;
        this.formatType = format;
    }
    
   /**
     * Search for term in log files.
     * 
     * @return
     */
    public FilterFileProcessor search() {
      
        final long tstart = System.currentTimeMillis();
                        
        final String ifHTMLError = this.formatType == 2 ? "\\errors" : "";
        String dir = this.globalConf.getFileCopyLocalTargetDir() + ifHTMLError;
        
        if (this.globalConf.isUseQuickIncubatorDir()) {
          dir = this.globalConf.getWorkingDirectory() + "\\quickIncubatorTests";
          final File cinc = new File(dir);
          cinc.mkdirs();
          LOGGER.info("Using quick incubator directory : " + dir);
        } else {
          dir = this.globalConf.getFileCopyLocalTargetDir() + ifHTMLError;
          if (this.formatType == 2) {              
              LOGGER.info(">>>>>>> NOTE: USING HTML OUTPUT FORMAT, USE ERROR LOG");
              LOGGER.info(">>>>>>> NOTE: USING HTML OUTPUT FORMAT, USE ERROR LOG");
              LOGGER.info(">>>>>>> NOTE: CREATE ERRORS DIRECTORY COPY To: " + dir);
              LOGGER.info(">>>>>>> NOTE: Try  : grep -A300 -B300 -h ERROR * > errors/all_errs.txt");
              LOGGER.info(">>>>>>> NOTE: Also try by session : grep -h Dhhxff7bNjiHxWM7FXuiXOn * > errors/all_errs.txt");
          }          
        }
        
        final String searchTerm = this.globalConf.getUserSearchTerm();
        if (searchTerm == null || searchTerm.length() == 0) {
            LOGGER.info("Invalid search term");
            return this;
        }
        LOGGER.info("Searching directory : " + dir);
        LOGGER.info(">> Searching for term '" + searchTerm + "'");
        final File fd = new File(dir);
        if (!fd.exists() || !fd.isDirectory()) {
            LOGGER.info("Target path is not a directory, exiting");
            return this;
        }
        long totalAllFiles = 0;        
        final File newDir = new File(this.globalConf.getWorkingDirectory() + "\\filter\\");
        newDir.mkdirs();
        for (final File f : fd.listFiles()) {
            if (f.isDirectory()) {
              continue;
            }                   
            this.outputFilename = this.globalConf.getWorkingDirectory() + "\\filter\\" + f.getName() + ".FILTERED";
            if (this.formatType == 2) {
              this.outputFilename += ".html";  
            }
            this.openOutputFileSingle();            
            if (this.printWriter != null) {
              if (this.formatType == 2) {
                this.printWriter.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
                this.printWriter.println(" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
                this.printWriter.println("<html>");
                this.printWriter.println("<head>");
                this.printWriter.println("  <title>Logs</title>");
                this.printWriter.println("  <style type='text/css'>");
                this.printWriter.println("    body {");
                this.printWriter.println("     background-color: white;"); 
                this.printWriter.println("     color: black;");
                this.printWriter.println("     font-family: Courier;");    
                this.printWriter.println("     font-size: 11px;");
                this.printWriter.println("     padding: 0px;");
                this.printWriter.println("     margin: 0px;");                                        
                this.printWriter.println("    }");
                
                this.printWriter.println("    table td {");
                this.printWriter.println("     font-family: Courier;");    
                this.printWriter.println("     font-size: 11px;");
                this.printWriter.println("     padding: 0px;");
                this.printWriter.println("     margin: 0px;");
                this.printWriter.println("    }");
                                                
                this.printWriter.println("  </style>");
                this.printWriter.println("</head>");
                this.printWriter.println("<body>");                
              }
            } // End if null //
            
            final SimpleFilterFile s = new SimpleFilterFile(globalConf, this.sessionSet, this.agentSet);
            s.formatType = this.formatType;
            s.writeOutputFile = this.writeOutputFile;
            s.outputFilename = this.outputFilename;
            s.setPrintWriter(printWriter);            
            s.search(true, f);            
            totalAllFiles += s.totalLinesProcessed;
            LOGGER.info("Total lines procesed in file : " + s.totalLinesProcessed);
            LOGGER.info("Percent lines accepted in file : " + s.percentAccepted);
            
            if (this.printWriter != null) {
              if (this.formatType == 2) {                
                this.printWriter.println("</body>");                
                this.printWriter.println("</html>");
              }
            } // End if null //
            
            this.closeSingle();
        } // End of the For //                
        
        this.statsTotalSearchFound = totalAllFiles;        
        final long tdiff = System.currentTimeMillis() - tstart;
        LOGGER.info(">> Found term '" + searchTerm + "' total of = " + this.statsTotalSearchFound + " times in files"); 
        LOGGER.info(">> Found term in " + tdiff + " ms , " + (tdiff / 1000.0) + " seconds");
        
        final double mb = 1024.0 * 1024;
        final double free = Runtime.getRuntime().freeMemory() / mb;            
        final double total = Runtime.getRuntime().totalMemory() / mb;
        final double max = Runtime.getRuntime().maxMemory() / mb;        
        final String fmt = String.format("Memory after operation [ freeMemory=%.2fM total=%.2fM maxMemory=%.2fM ]", free, total, max);
        LOGGER.info(fmt);
        return this;
    }

    /**
     * Open output file.
     */
    public void openOutputFileSingle() {                
        if (this.outputFilename == null || this.outputFilename.length() == 0) {
            return;
        }
        BufferedOutputStream bos;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(this.outputFilename));
            this.printWriter = new PrintWriter(bos);            
        } catch (final FileNotFoundException e) { 
            e.printStackTrace();
            throw new IllegalStateException("Could not open output file");
        }         
    }
    
    /**
     * Close the open output file.
     */
    public void closeSingle() {
        if (!this.writeOutputFile) {
            return;
        }
        if (this.outputFilename == null || this.outputFilename.length() == 0) {
            return;
        }
        if (this.printWriter != null) {
            this.printWriter.close();            
            final File f = new File(this.outputFilename);            
            LOGGER.info(">>> SessionSearch : Closing output file, see file for results : " + f.getAbsolutePath() + " parentDir=" + f.getParent());            
        }
    }
    
} // End of Class //
