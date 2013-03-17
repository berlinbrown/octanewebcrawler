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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UnzipWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnzipWriter.class);
  
    private final String filename;
    private final FileWriterConf conf;
        
    private final ServerDirConf dirConf;
    
    private int bufferSize = 2048;
        
    public UnzipWriter(final String filename, final FileWriterConf conf, final ServerDirConf dir) {
        this.filename = filename;
        this.conf = conf;
        this.dirConf = dir;
    }
    
    public boolean unzipWrite() { 
        final long tstart = System.currentTimeMillis();
        GZIPInputStream zipfile = null;
        OutputStream out = null;
        boolean success = false;
        try {            
            final File f = new File(this.filename);
            final String outputfilename = new CustomFilenameMangler(this.conf, this.dirConf).parseZippedFilenameMangle(this.filename);
            final File fout = new File(outputfilename);
            if (!f.exists()) {
                return false;
            }
            zipfile = new GZIPInputStream(new FileInputStream(f));
            out = new FileOutputStream(fout);
            byte[] buf = new byte[bufferSize];
            int len;
            while ((len = zipfile.read(buf)) > 0) {
              out.write(buf, 0, len);
            } // End of the While //
            success = true;
            Thread.sleep(100);
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } // End of the try - catch //
                
        final long tdiff = System.currentTimeMillis() - tstart;
        LOGGER.info("Unzip file="+this.filename + " procTime=" + tdiff);
        return success;
    }  
    
    public boolean deleteArchiveFile() {
        final File f = new File(this.filename);
        return f.delete();
    }        
    
} // End of the Class //
