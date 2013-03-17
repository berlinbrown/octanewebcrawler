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
package org.berlin.logs.scan.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.berlin.logs.scan.GlobalConfiguration;


/**
 * Writer configuration.
 * 
 */
public class FileWriterConf {

    private List<String> serversForSearch = new ArrayList<String>();       
    private List<String> jvmsToSearch = new ArrayList<String>();
    
    /**
     * Log directory to append to source.
     */
    private String logDirToSearch = "logs";
    
    /**
     * Full local target directory
     */
    private String localTargetDir = "E:\\berlin_work\\search_logs\\ant_search\\search_result\\logs";
    
    /**
     * End target directory for source.
     */
    private String targetLogDirToSearch = "logs";
  
    // EXAMPLE : private String regexIncludeExt = ".*\\.(?i)(gz|log|1|2|3|4|5)$";
    private String regexIncludeFile = ".*(?i)(logs).*";
    private String regexIncludeExt = "";
    private String regexExcludeExt = "";
    
    private String regexZippedExt = ".*\\.(?i)(gz)$";;
    
    private boolean committed = false;    
    private boolean verbose = false;    
    private boolean deleteOldFiles = true;   
    private boolean unzipArchiveFiles = true;
        
    /**
     * Add server for search.
     * @param server
     */
    public void addServerForSearch(final String server) {
        if (committed) {
            throw new IllegalStateException("Data is committed and cannot receive further modifications");
        }
        serversForSearch.add(server);
    }
    
    /**
     * Add server for search.
     * @param server
     */
    public void addJvmForSearch(final String server) {
        if (committed) {
            throw new IllegalStateException("Data is committed and cannot receive further modifications");
        }
        this.jvmsToSearch.add(server);
    }
        
    /**
     * Lock servers.
     */
    public void lockServers() {
        if (committed) {
            throw new IllegalStateException("Data is committed and cannot receive further modifications");
        }        
        this.serversForSearch = Collections.unmodifiableList(this.serversForSearch);
        this.jvmsToSearch = Collections.unmodifiableList(this.jvmsToSearch);
        
        for (final String server : this.serversForSearch) {
            if (server == null || server.trim().length() == 0) {
                continue;
            }
            final File f = new File("\\\\" + server);
            if (!f.exists()) {                
                System.out.println("At Lock Servers : cannot process, file=" + f.getAbsolutePath());
            } 
        } // End of the for //
        committed = true;
    }    
    
    /**
     * Transfer configuration from global configuration .
     */
    public void transferGlobalConf(final GlobalConfiguration mainConf) {
        
    }
    
    public void lock() {
        this.lockServers();
    }

    /**
     * @return the serversForSearch
     */
    public List<String> getServersForSearch() {
        return serversForSearch;
    }

    /**
     * @return the jvmsToSearch
     */
    public List<String> getJvmsToSearch() {
        return jvmsToSearch;
    }

    /**
     * @return the logDirToSearch
     */
    public String getBaseLogDirToSearch() {
        return logDirToSearch;
    }
    
    /**
     * End target directory for source.
     * @return the targetLogDirToSearch
     */
    public String getTargetLogDirToSearch() {
        return targetLogDirToSearch;
    }

    /**
     * @return the regexIncludeFile
     */
    public String getRegexIncludeFile() {
        return regexIncludeFile;
    }

    /**
     * @return the regexIncludeExt
     */
    public String getRegexIncludeExt() {
        return regexIncludeExt;
    }

    /**
     * @return the regexExcludeExt
     */
    public String getRegexExcludeExt() {
        return regexExcludeExt;
    }

    /**
     * @return the localTargetDir
     */
    public String getLocalTargetDir() {
        return localTargetDir;
    }

    /**
     * @return the regexZippedExt
     */
    public String getRegexZippedExt() {
        return regexZippedExt;
    }

    /**
     * @return the deleteOldFiles
     */
    public boolean isDeleteOldFiles() {
        return deleteOldFiles;
    }

    /**
     * @return the unzipArchiveFiles
     */
    public boolean isUnzipArchiveFiles() {
        return unzipArchiveFiles;
    }

    /**
     * @param regexIncludeFile the regexIncludeFile to set
     */
    public void setRegexIncludeFile(String regexIncludeFile) {
        this.regexIncludeFile = regexIncludeFile;
    }

    /**
     * End target directory for source.
     * @param targetLogDirToSearch the targetLogDirToSearch to set
     */
    public void setTargetLogDirToSearch(String targetLogDirToSearch) {
        this.targetLogDirToSearch = targetLogDirToSearch;
    }

    /**
     * @param localTargetDir the localTargetDir to set
     */
    public void setLocalTargetDir(String localTargetDir) {
        this.localTargetDir = localTargetDir;
    }

    /**
     * @param logDirToSearch the logDirToSearch to set
     */
    public void setLogDirToSearch(String logDirToSearch) {
        this.logDirToSearch = logDirToSearch;
    }
    
    public void setUnzipArchiveFiles(boolean unzipArchiveFiles) {
      this.unzipArchiveFiles = unzipArchiveFiles;
    }

    /**
     * @param deleteOldFiles the deleteOldFiles to set
     */
    public void setDeleteOldFiles(boolean deleteOldFiles) {
      this.deleteOldFiles = deleteOldFiles;
    }
       
} // End of the Class //
