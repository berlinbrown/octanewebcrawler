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
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerLogFileBatchWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerLogFileBatchWriter.class);
    private final FileWriterConf conf;    
    
    public ServerLogFileBatchWriter(final FileWriterConf conf) {
        this.conf = conf;
    }

    public List<ServerDirConf> listDirsPerServer() {        
        // Search by server and jvm
        final List<ServerDirConf> files = new ArrayList<ServerDirConf>();
        for (final String server : conf.getServersForSearch()) {              
            if (server == null || server.trim().length() == 0) {
                continue;
            }            
            final String s = "\\\\" + server + "\\" + conf.getBaseLogDirToSearch();
            for (final String jvm : conf.getJvmsToSearch()) {
                if (jvm == null || jvm.trim().length() == 0) {
                    continue;
                }
                final String dir = s + "\\" + jvm + "\\" + conf.getTargetLogDirToSearch();
                final ServerDirConf c = new ServerDirConf();
                c.dir = dir;
                c.jvm = jvm;
                c.server = server;
                files.add(c);
            }                                    
        } // End of For 
        return files;
    }    
    
    /**
     * Process the server files and copy.
     */
    public void processAndCopy() {
              
        int i = 1;
        int j = 1;
        final File newLocalDir = new File(conf.getLocalTargetDir());
        final long tstart = System.currentTimeMillis();
        newLocalDir.mkdirs();        
        // Delete old files
        if (conf.isDeleteOldFiles()) {
            new FileCleaner(conf).clean();
        }        
        final List<ServerDirConf> dirs = this.listDirsPerServer();        
        for (final ServerDirConf dir : dirs) {
            final long tstart2 = System.currentTimeMillis();
            LOGGER.info("(" + i + ") Attempt : " + dir);
            final File curDirScan = new File(dir.dir);
            if (curDirScan.isDirectory() && curDirScan.exists()) {
                final File [] files = curDirScan.listFiles(newFilenameFilter());
                for (final File fileProcess : files) {
                    try {
                        LOGGER.info("  -> Attempt File ( " + j + " ) : processAndCopy : " + fileProcess);
                        final String jvmMangleName = new CustomFilenameMangler(conf, dir).parseJvmMangle();
                        final String localFileName = fileProcess.getName() + "." + dir.server + "." + jvmMangleName;                                               
                        final File localTargetFile = new File(conf.getLocalTargetDir() + "\\" + localFileName);
                        final LogFileWriter writer = new LogFileWriter(fileProcess, localTargetFile);
                        boolean res = writer.copy();
                        // If file type is archived, also do a unzip
                        if (conf.isUnzipArchiveFiles() && res && fileProcess.getName().matches(conf.getRegexZippedExt())) {
                            final UnzipWriter unzip = new UnzipWriter(localTargetFile.getAbsolutePath(), conf, dir);
                            unzip.unzipWrite();
                            unzip.deleteArchiveFile();                            
                        } else {                          
                          if (!conf.isUnzipArchiveFiles() && res && fileProcess.getName().matches(conf.getRegexZippedExt())) {
                            LOGGER.info("WARN - attempt to delete archive file while unzip is false (may not want to delete)");
                            final File fx = new File(localTargetFile.getAbsolutePath());                            
                            fx.delete();                            
                          }                          
                        } // End of the if - else //
                        final long tdiff = System.currentTimeMillis() - tstart2;
                        LOGGER.info("  -> Done in " + tdiff + " ms");
                    } catch(Exception nfe) {
                        nfe.printStackTrace();
                    }
                    j++;
                } // End of the for //
            } else {
                LOGGER.info("  (" + i + ") INVALID DIR : " + dir + " <<<");
            } // End of if directory/exists //
            
            i++;
        } // End of the for //
        final long tdiff = System.currentTimeMillis() - tstart;
        final double tMin = (tdiff / 1000.0) / 60.0;
        LOGGER.info(String.format("Processed all files in %.2f min", tMin));
    }       
    
    protected FilenameFilter newFilenameFilter() {
        final FilenameFilter ff = new FilenameFilter() {
            public boolean accept(final File dir, final String name) {
                if (name == null || name.length() == 0) {
                    return false;
                }
                final boolean hasExt = conf.getRegexIncludeExt() != null && conf.getRegexIncludeExt().length() > 0;
                if (hasExt && name.matches(conf.getRegexIncludeExt())) {
                    if (name.matches(conf.getRegexIncludeFile())) {
                        return true;
                    }
                } else {
                    if (name.matches(conf.getRegexIncludeFile())) {
                        return true;
                    }   
                } // End of the if - else //
                return false;
            }
        };
        return ff;
    }
    
} // End of the Class //
