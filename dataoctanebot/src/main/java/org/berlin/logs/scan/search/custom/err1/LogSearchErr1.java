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
package org.berlin.logs.scan.search.custom.err1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

import org.berlin.logs.scan.GlobalConfiguration;
import org.berlin.logs.scan.search.LogSearch;

public class LogSearchErr1 {

	private final GlobalConfiguration globalConf;

	public LogSearchErr1(final GlobalConfiguration globalConf) {
		this.globalConf = globalConf;
	}

	public LogSearchErr1 run() {
		String dslScriptInput = "user.searchTerm=ERROR\n";
		dslScriptInput += "search.savePreviousLines=true\n";
		dslScriptInput += "user.defaultOutputName=all_err.txt\n";
		final ByteArrayInputStream is = new ByteArrayInputStream(dslScriptInput.getBytes());
		final Properties propertyScriptFromString = new Properties();
		try {
			propertyScriptFromString.load(is);
			globalConf.loadFromDynamicScript(propertyScriptFromString);
		} catch (final IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
		final LogSearch s = new LogSearch(globalConf);
		s.setOutputFilename(globalConf.getWorkingDirectory() + "\\" + globalConf.getUserDefaultOutputName());
		s.search();
		return this;
	}

} // End of the Class //
