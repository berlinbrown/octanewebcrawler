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
package org.berlin.logs.scan.session.custom.app;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Collection;

import org.berlin.logs.idsl.IExecutor;
import org.berlin.logs.scan.GlobalConfiguration;
import org.berlin.logs.scan.session.custom.BasicSesssionCollector;
import org.berlin.logs.scan.session.custom.SessionInfo;
import org.berlin.logs.scan.session.custom.expr.ExpressionSession;

/**
 * Expression runner, run session expression.
 */
public class ExpressionRunner implements IExecutor {

	String outputFilename = "xxx-output.log";
	private PrintWriter printWriter;
	GlobalConfiguration globalConf;

	/**
	 * Run the expression parser.
	 */
	public void run() {
		final ExpressionSession parser = new ExpressionSession(globalConf);
		try {
			final Collection<SessionInfo> res = parser.parseScript(globalConf.getScriptExpression());
			// // Save to property file ///
			{
				// Write the full session database to file //
				this.outputFilename = this.globalConf.getWorkingDirectory() + "\\expression_for_session.xml";
				final BasicSesssionCollector s = new BasicSesssionCollector(globalConf, null);
				this.openOutputFile();
				s.setPrintWriter(this.printWriter);
				s.writeSessionDatabaseList(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			this.close();
		}
	}

	/**
	 * Open output file.
	 */
	public void openOutputFile() {

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
	public void close() {
		if (this.outputFilename == null || this.outputFilename.length() == 0) {
			return;
		}
		if (this.printWriter != null) {
			this.printWriter.close();
			final File f = new File(this.outputFilename);
			System.out.println("SessionSearch : Closing output file, see file for results : " + f.getAbsolutePath()
					+ " parentDir=" + f.getParent());
		}
	}

} // End of the Class //
