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

public class CustomFilenameMangler {
    
    private String archiveExt = "gz";
    private String stripFilenamePrefix = "logs";
    
    private final FileWriterConf conf;
    private final ServerDirConf dirConf;
    
    public CustomFilenameMangler(final FileWriterConf conf, final ServerDirConf dir) {
        this.conf = conf;
        this.dirConf = dir;
    }
    
    public String parseZippedFilenameMangle(final String filename) {                            
        if (filename.indexOf(this.archiveExt) > 0) {
            String res = filename.substring(0, filename.indexOf(this.archiveExt)-1);            
            res = res + "." + this.dirConf.server;
            String namePrefixCustom = "";
            if (this.dirConf.jvm.indexOf(this.stripFilenamePrefix) >= 0) {
                namePrefixCustom = this.dirConf.jvm.substring(this.dirConf.jvm.indexOf(this.stripFilenamePrefix)+this.stripFilenamePrefix.length()) ;
                res = res + "." + namePrefixCustom;
            }
            return res;
        }
        return filename;
    }
    
    public String parseJvmMangle() {               
        String namePrefixCustom = ""; 
        if (this.dirConf.jvm.indexOf(this.stripFilenamePrefix) >= 0) {
            namePrefixCustom = this.dirConf.jvm.substring(this.dirConf.jvm.indexOf(this.stripFilenamePrefix)+this.stripFilenamePrefix.length()) ;            
            return namePrefixCustom; 
        }
        return this.dirConf.jvm;
    }
    
} // End of Class //
