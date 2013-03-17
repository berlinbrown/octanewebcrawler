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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Delete old log files.
 */
public class FileCleaner {
  
    private static final Logger LOGGER = LoggerFactory.getLogger(FileCleaner.class);
  
    private final FileWriterConf conf;
    
    public FileCleaner(final FileWriterConf conf) {
        this.conf = conf;
    }
    
    /**
     * Delete old files.
     */
    public void clean() {
        
        if (this.conf == null) {
            LOGGER.info("At fileClean, invalid configuration");
            return;
        }
        LOGGER.info("At clean : LocalTargetDir - " + this.conf.getLocalTargetDir());
        if (this.conf.getLocalTargetDir() == null || this.conf.getLocalTargetDir().length() == 0) {
            LOGGER.warn("At fileClean, invalid configuration, TargetLogDirToSearch is empty");
            return;
        }         
        final File fdir = new File(this.conf.getLocalTargetDir());
        final File fdirSys = new File(this.conf.getLocalTargetDir() + "system");
        final long tstart = System.currentTimeMillis();
        final int i = this.clean(fdir);
        final int ix = this.clean(fdirSys) + i;
        final long tdiff = System.currentTimeMillis() - tstart;
        LOGGER.info("Complete deleted files in " + tdiff + " ms totalFilesDeleted=" + ix);
    }
    
    /**
     * Remove the file.
     * @param fdir
     * @return
     */
    protected int clean(final File fdir) {
        int i = 0;
        if (!fdir.exists()) {
            return i;
        }
        if (fdir.listFiles() == null) {
            LOGGER.info("No files for cleaning");
            return i;
        }
        for (final File f : fdir.listFiles()) {
            if (f.isFile()) {
                final boolean r = f.delete();
                if (r) {
                    i++;
                    LOGGER.info("Deleted file = " + f.getAbsolutePath());
                }
            } // End of the if //
        } // End of the For //
        return i;
    }
    
} // End of the Class //
