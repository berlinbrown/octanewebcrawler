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
package org.berlin.logs.scan.io.app;

import org.berlin.logs.idsl.IExecutor;
import org.berlin.logs.scan.GlobalConfiguration;
import org.berlin.logs.scan.io.FileWriterConf;
import org.berlin.logs.scan.io.ServerLogFileBatchWriter;

public class Copy implements IExecutor {

	private final GlobalConfiguration globalConf;

	public Copy(final GlobalConfiguration globalConf) {
		this.globalConf = globalConf;
	}

	public void run() {

		final FileWriterConf conf = new FileWriterConf();
		conf.addServerForSearch(this.globalConf.getServer1());
		conf.addServerForSearch(this.globalConf.getServer2());
		conf.addJvmForSearch(this.globalConf.getSubserver1());
		conf.addJvmForSearch(this.globalConf.getSubserver2());
		conf.addJvmForSearch(this.globalConf.getSubserver3());
		conf.addJvmForSearch(this.globalConf.getSubserver4());
		conf.addJvmForSearch(this.globalConf.getSubserver5());
		conf.addJvmForSearch(this.globalConf.getSubserver6());

		conf.addJvmForSearch(this.globalConf.getSubserver7());
		conf.addJvmForSearch(this.globalConf.getSubserver8());
		conf.addJvmForSearch(this.globalConf.getSubserver9());
		conf.addJvmForSearch(this.globalConf.getSubserver10());
		conf.addJvmForSearch(this.globalConf.getSubserver11());
		conf.addJvmForSearch(this.globalConf.getSubserver12());

		conf.setLocalTargetDir(this.globalConf.getFileCopyLocalTargetDir());
		conf.setLogDirToSearch(this.globalConf.getFileCopyLogDirToSearch());
		conf.setRegexIncludeFile(this.globalConf.getFileCopyRegexIncludeFile());
		conf.setTargetLogDirToSearch(this.globalConf.getFileCopyTargetLogDirToSearch());
		conf.lock();
		final ServerLogFileBatchWriter processor = new ServerLogFileBatchWriter(conf);
		conf.setUnzipArchiveFiles(this.globalConf.isUnzipArchiveFiles());
		conf.setDeleteOldFiles(this.globalConf.isDeleteOldFiles());
		processor.processAndCopy();
	}

} // End of the Class //
