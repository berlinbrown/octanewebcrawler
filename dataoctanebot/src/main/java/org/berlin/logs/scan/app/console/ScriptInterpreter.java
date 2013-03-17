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
package org.berlin.logs.scan.app.console;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.berlin.logs.idsl.IInterpreter;
import org.berlin.logs.scan.GlobalConfiguration;
import org.berlin.logs.scan.Version;
import org.berlin.logs.scan.errors.ILogErrorParser;
import org.berlin.logs.scan.errors.Parser;
import org.berlin.logs.scan.filter.custom.app.FilterCustom1;
import org.berlin.logs.scan.io.app.CopyCustom1;
import org.berlin.logs.scan.search.LogSearch;
import org.berlin.logs.scan.search.custom.err1.LogSearchErr1;
import org.berlin.logs.scan.session.custom.BatchSessionSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interpret input commands.
 */
public class ScriptInterpreter implements IInterpreter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptInterpreter.class);
  
    public static final String DSL_ACTION_SEARCH  = "search"; 
    public static final String DSL_ACTION_HELP    = "help";    
    public static final String DSL_ACTION_PROPERTIES = "properties";
    
    public static final String DSL_ACTION_COPY    = "copy1";
    public static final String DSL_ACTION_DOWNLOAD = "download1";    
    public static final String DSL_ACTION_COPY2    = "copy2";
    public static final String DSL_ACTION_DOWNLOAD2 = "download2";    
    public static final String DSL_ACTION_STATS1   = "stats1";
    public static final String DSL_ACTION_ERRORS1  = "errors1";
    public static final String DSL_ACTION_SESSION1 = "session1";
    public static final String DSL_ACTION_CLEAR_COMMAND = "clearcommand";
    public static final String DSL_ACTION_FILTER1 = "filter1";
        
    private final StringBuffer bufOut = new StringBuffer();
          
    public static final String NL = System.getProperty("line.separator"); 
    
    private final Mutable<Boolean> inProcessRunningCommand = new Mutable<Boolean>(false);
    
    /**
     * Constructor for script interpreter.
     * 
     * @param window
     * @param bufOut
     */
    public ScriptInterpreter() {
        super();
    }
    
    /**
     * Interpret the last command.
     */
    public void interpret() {
      this.interpret("none");
    }
    
    /**
     * Interpret the last command.
     */
    public void interpret(final String inputArgCommand) {
                                                                    
        /****************************************
         * Load from the classpath, then the filesystem and then from the command console.
         ****************************************/
        final File fConfLocal = new File("log_scan_system.properties");
        final Properties propsLocalFile = new Properties();
        if (fConfLocal.exists()) {
            LOGGER.info("Conf Exists, attempting to load - " + fConfLocal.getAbsolutePath());            
            try {
                propsLocalFile.load(new FileInputStream(fConfLocal));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } // End of the try - catch
                       
        } // End of the if //
        final GlobalConfiguration globalConf = new GlobalConfiguration().load(GlobalConfiguration.PATH);
        globalConf.loadFromDynamicScript(propsLocalFile);        
        LOGGER.info(globalConf.toString());        
        LOGGER.info(Version.num + " -- " + globalConf.getSystemApplicationName() + "-" + globalConf.getSystemVersion());
                
        String cmd = globalConf.getDynamicActionCommand();
        if (inputArgCommand != null && inputArgCommand.length() != 0) {
          // If given an argument as a command, use that instead //
          cmd = inputArgCommand;
        }
        if (cmd != null && cmd.length() != 0) {            
            LOGGER.info("Dynamic Script Command :  " + cmd);
            bufOut.append("Dynamic Script Command :  " + cmd);
            bufOut.append(NL);
            LOGGER.info(bufOut.toString());                                      
            if (inProcessRunningCommand.get()) {
                bufOut.append(NL);
                bufOut.append("Another command is running, please standby...");
                bufOut.append(NL);
                LOGGER.info(bufOut.toString());
                LOGGER.info("Another command is running, please standby...");
                return;
            }
            
            if (DSL_ACTION_SEARCH.equalsIgnoreCase(cmd)) {
                
                inProcessRunningCommand.set(true);
                LOGGER.info(">> Running Search <<");                                                     
                final LogSearch s = new LogSearch(globalConf);
                s.setOutputFilename(globalConf.getWorkingDirectory() + "\\" + globalConf.getUserDefaultOutputName());
                s.search();        
                LOGGER.info(">> Done <<");                
                bufOut.append("Total found : " + s.getStatsTotalSearchFound());
                bufOut.append(NL);
                bufOut.append("Done with search");
                bufOut.append(NL);
                LOGGER.info(bufOut.toString());
                inProcessRunningCommand.set(false);
                
            } else if (DSL_ACTION_COPY.equalsIgnoreCase(cmd) || DSL_ACTION_DOWNLOAD.equalsIgnoreCase(cmd)) {
                
                inProcessRunningCommand.set(true);
                LOGGER.info(">> Begin Log File Download <<");
                final CopyCustom1 custom1 = new CopyCustom1(globalConf);                
                custom1.run();
                LOGGER.info(">> Done <<");                
                bufOut.append(NL);
                bufOut.append("Done with copy");
                bufOut.append(NL);
                                                               
                bufOut.append("Working Directory : " + globalConf.getWorkingDirectory());
                bufOut.append(NL);
                bufOut.append("Target Local Output Search Directory : " + globalConf.getFileCopyLocalTargetDir());                
                bufOut.append(NL);
                
                LOGGER.info(bufOut.toString());
                inProcessRunningCommand.set(false);
                
            } else if (DSL_ACTION_COPY2.equalsIgnoreCase(cmd) || DSL_ACTION_DOWNLOAD2.equalsIgnoreCase(cmd)) {
                                
            } else if (DSL_ACTION_STATS1.equalsIgnoreCase(cmd)) {
                inProcessRunningCommand.set(true);
                this.stats1(globalConf);
                inProcessRunningCommand.set(false);
                
            } else if (DSL_ACTION_FILTER1.equalsIgnoreCase(cmd)) {
              new FilterCustom1(globalConf).run();
            } else if (DSL_ACTION_ERRORS1.equalsIgnoreCase(cmd)) {
                
                inProcessRunningCommand.set(true);
                LOGGER.info(">> Running Search <<");                                                     
                final LogSearchErr1 s = new LogSearchErr1(globalConf).run();
                LOGGER.info(">> Done <<");                              
                bufOut.append(NL);
                bufOut.append("Done with search");
                bufOut.append(NL);
                LOGGER.info(bufOut.toString());
                inProcessRunningCommand.set(false);
                
            } else if (DSL_ACTION_SESSION1.equalsIgnoreCase(cmd)) {
                
                inProcessRunningCommand.set(true);
                LOGGER.info(">> Running Search <<");                                                     
                final BatchSessionSearcher s = new BatchSessionSearcher(globalConf);
                s.setUseXMLFormat(globalConf.isUseXMLPropertyFormat());
                s.setOutputFilename(globalConf.getWorkingDirectory() + "\\" + globalConf.getSessionOutputName());
                s.search();
                LOGGER.info(">> Done <<");
                                
                bufOut.append(NL);
                bufOut.append("Done with search");
                bufOut.append(NL);
                LOGGER.info(bufOut.toString());
                inProcessRunningCommand.set(false);
                
            } else if (DSL_ACTION_CLEAR_COMMAND.equalsIgnoreCase(cmd)) {
                inProcessRunningCommand.set(false);
                
            } else if (DSL_ACTION_HELP.equalsIgnoreCase(cmd)) {
                                
                inProcessRunningCommand.set(false);
                LOGGER.info("Command Listing:");
                LOGGER.info(" search - search log directory");
                LOGGER.info(" copy - download log files");
                                
                bufOut.append("Command Listing:\n");
                bufOut.append(" \nAlso visit browser at http://localhost:7181\n\n");
                bufOut.append(" search - search log directory with terms from configuration\n");
                bufOut.append(" copy1/download1 - Download and archive log files for custom-log1\n");
                bufOut.append(" copy2/download2 - Download and archive system log files\n");                
                bufOut.append(" errors1 - Write critical error log search data for copied logs.  Search for critical error terms.\n");
                bufOut.append(" stats1 - Read critical error log search data and build stats (you must run errors1 first)\n");
                bufOut.append(" session1 - Collect session data from custom-log1\n");
                bufOut.append(" properties - Print current configuration\n");
                
                bufOut.append(NL);
                bufOut.append("Current Properties and Configuration :\n");                
                bufOut.append("Working Directory : " + globalConf.getWorkingDirectory());
                bufOut.append(NL);
                bufOut.append("Local Output Search Directory : " + globalConf.getFileCopyLocalTargetDir());                
                bufOut.append(NL);
                
                LOGGER.info(bufOut.toString());                
    
            } else if (DSL_ACTION_PROPERTIES.equalsIgnoreCase(cmd)) {
                
                inProcessRunningCommand.set(false);
                bufOut.append(NL);
                bufOut.append("Current Properties and Configuration :\n");                
                bufOut.append("Working Directory : " + globalConf.getWorkingDirectory());
                bufOut.append(NL);
                bufOut.append("Local Output Search Directory : " + globalConf.getFileCopyLocalTargetDir());                
                bufOut.append(NL);
                
            } else {
                LOGGER.info("Invalid Script Command, not a valid command");
                bufOut.append("Invalid Script Command, not a valid command");
                bufOut.append(NL);
                LOGGER.info(bufOut.toString());
                inProcessRunningCommand.set(false);
            } // End of if - else on command //
            
        } else {
            LOGGER.info("Invalid Script Command, empty");
            bufOut.append("Invalid Script Command, empty");
            bufOut.append(NL);
            LOGGER.info(bufOut.toString());
            
        } // End of if - check command //
    }
    
    protected void stats1(final GlobalConfiguration globalConf) {
        final long start = System.currentTimeMillis();
        LOGGER.info("Running MainParseCritical Logs ...");                
        BufferedReader br = null;
        try {                                
            final File f = new File(globalConf.getWorkingDirectory() + "\\all_err.txt");
            if (f.exists()) {
                final FileInputStream fis = new FileInputStream(f);
                final BufferedInputStream bis = new BufferedInputStream(fis);
                br = new BufferedReader(new InputStreamReader(bis));
                final ILogErrorParser p = new Parser(globalConf).parse(br);
                p.report();
                final long diff = System.currentTimeMillis() - start;
                LOGGER.info("Markers found = " + p.getMarkersFound());
                LOGGER.info("Done - diff=" + diff + " ms");
            } else {
                LOGGER.info("Invalid, File does not exist - " + f.getAbsolutePath());                        
            } // End of if //
            
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
    
    /**
     * Simple class to modify one element.
     * This can be used to modify primitives to methods have final arguments.          
     *
     * @param <T>
     */
    public static final class Mutable<T> {
        private T mutable;
        /**
         * Constructor.
         * @param m
         */
        public Mutable(final T m) {
            this.mutable = m;
        }
        /**
         * Set the mutable.
         * @param val
         * @return
         */
        public synchronized T set(final T val) {
            this.mutable = val;
            return mutable;
        }        
        public synchronized T get() {
            return mutable;
        }
        @Override
        public String toString() {
            return String.valueOf(mutable);
        }
    }
    
} // End of the Class //
