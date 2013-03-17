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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogFileWriter {
  
    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileWriter.class);
  
    private String src = "";
    private String dest = "";
    private File srcFile;
    private File destFile;
    
    private boolean verbose = false;
    
    private int bufferSizeInput = 4 * 8192;
    private int bufferSizeOutput = 4 * 8192;
    private int internalCopyWriteBufferSize = 8192;
    
    public LogFileWriter(final String src, final String dest) {
        this.src = src;
        this.dest = dest;
        this.srcFile = new File(src);
        this.destFile = new File(dest);        
    }    
    
    public LogFileWriter(final File src, final File dest) {
        if (this.src == null) {
            throw new IllegalArgumentException("Invalid File at src");
        }
        if (this.dest == null) {
            throw new IllegalArgumentException("Invalid File at dest");
        }
        this.srcFile = src;
        this.destFile = dest;       
    }    
    
    public boolean copy() {   
      
        LOGGER.info("LogWriter: copying : src=" + this.srcFile.getName() + " -> dest=" + this.destFile.getName());
        LOGGER.info("LogWriter: copying : src=" + this.srcFile.getAbsolutePath() + " -> dest=" + this.destFile.getAbsolutePath());
        
        // Reader
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        boolean success = false;
        try {
            final FileInputStream fis = new FileInputStream(this.srcFile);
            bis = new BufferedInputStream(fis, bufferSizeInput);            
            // Writer
            final FileOutputStream fout = new FileOutputStream(this.destFile);
            bos = new BufferedOutputStream(fout, bufferSizeOutput);            
            byte [] b = new byte[internalCopyWriteBufferSize];
            int noOfBytes = 0;
            while( (noOfBytes = bis.read(b)) != -1) {        
                bos.write(b, 0, noOfBytes);
            }
            success = true;
            Thread.sleep(80);
        } catch(final IOException ioe) {
            ioe.printStackTrace();
        } catch(final InterruptedException oo) {
            oo.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }   
            }            
        } // End of the try - catch finally //
        LOGGER.info("LogWriter: copying : >> Done <<");
        return success;
    }
    
} // End of the class // 
