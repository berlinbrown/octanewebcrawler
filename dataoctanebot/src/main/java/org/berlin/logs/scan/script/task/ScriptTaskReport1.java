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
package org.berlin.logs.scan.script.task;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.berlin.logs.idsl.IExecutor;
import org.berlin.logs.scan.GlobalConfiguration;
import org.berlin.logs.scan.errors.ILogErrorParser;
import org.berlin.logs.scan.errors.Parser;
import org.berlin.logs.scan.io.app.CopyCustom1;
import org.berlin.logs.scan.search.custom.err1.LogSearchErr1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Run script.
 */
public class ScriptTaskReport1 implements IExecutor {

	private final GlobalConfiguration globalConf;

	private static final Logger LOGGER = LoggerFactory.getLogger(ScriptTaskReport1.class);

	public ScriptTaskReport1(final GlobalConfiguration globalConf) {
		this.globalConf = globalConf;
	}

	public void run() {
		LOGGER.info(">> Begin Log File Download <<");
		final CopyCustom1 custom1 = new CopyCustom1(globalConf);
		custom1.run();
		LOGGER.info("Done with copy");
		LOGGER.info("Working Directory : " + globalConf.getWorkingDirectory());
		LOGGER.info("Target Local Output Search Directory : " + globalConf.getFileCopyLocalTargetDir());
		for (int i = 0; i < 3; i++) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			LOGGER.info("sleeping...");
		}

		LOGGER.info(">> Running Search <<");
		final LogSearchErr1 s = new LogSearchErr1(globalConf).run();
		LOGGER.info(">> Done <<");

		for (int i = 0; i < 2; i++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			LOGGER.info("sleeping [2]...");
		}
		this.stats1(globalConf);
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

		} catch (final Exception e) {
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

} // End of the Class //
