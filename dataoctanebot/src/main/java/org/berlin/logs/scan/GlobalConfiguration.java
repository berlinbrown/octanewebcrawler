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
package org.berlin.logs.scan;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global configuration store for DSL scripts.
 */
public class GlobalConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalConfiguration.class);

	public static final String PATH = "org/berlin/logs/scan/resource/log_scan_system.properties";
	public static final String PATH2 = "src/main/java/org/berlin/logs/scan/resource/log_scan_system.properties";

	Properties systemPropertiesFromClasspath = new Properties();
	Properties userDynamicScriptPropertiesFromString = new Properties();

	String systemApplicationName = "X-LogScan";
	String systemVersion = "X-1.0.0";
	String sessionOutputName = "";

	/**
	 * Full path to target local directory.
	 */
	String fileCopyLocalTargetDir = "dir";
	String fileCopyLocalTargetDirBase = "dir";

	/**
	 * Specific folder after target dir.
	 */
	String fileCopyTargetLogDirToSearch = "dir";
	String fileCopyLogDirToSearch = "logs";
	String fileCopyRegexIncludeFile = "file";
	String workingDirectory = "workDir";
	String userSearchTerm = "Exception";
	String userHasSearch = "false";
	String dynamicActionCommand = "";
	String userDefaultOutputName = "";
	boolean hasHadoop = false;

	String server1 = "";
	String server2 = "";
	String server3 = "";
	String server4 = "";
	String subserver1 = "";
	String subserver2 = "";
	String subserver3 = "";
	String subserver4 = "";
	String subserver5 = "";
	String subserver6 = "";
	String subserver7 = "";
	String subserver8 = "";
	String subserver9 = "";
	String subserver10 = "";
	String subserver11 = "";
	String subserver12 = "";

	boolean saveOldLines = false;
	boolean useXMLPropertyFormat = true;
	boolean unzipArchiveFiles = true;
	boolean hasHTMLFilterOutput = false;
	boolean useQuickIncubatorDir = false;
	boolean deleteOldFiles = false;

	int maxNumberSesssionsInDatabase = 3500;
	int maxNumberSesssionsForClip = 3400;

	private String scriptExpression = "";

	/**
	 * Load the system global configuration.
	 * 
	 * @param propFilename
	 * @return
	 */
	public GlobalConfiguration load(final String propFilename) {
		final Properties props = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(propFilename);
			if (inputStream == null) {
				LOGGER.info("! Global Conf Invalid at [1] : " + propFilename);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		if (inputStream == null) {
			try {
				inputStream = this.getClass().getClassLoader().getResourceAsStream(PATH2);
				if (inputStream == null) {
					LOGGER.info("! Global Conf Invalid at [2] : " + PATH2);
				}
			} catch (final Exception e) {
				e.printStackTrace();
			} // End of try catch //
		} // End of if //
		if (inputStream == null) {
			LOGGER.info("Invalid inputStream at Property File");
			throw new IllegalStateException(
					"GlobalConfiguration: property file '" + propFilename + "' not found in the classpath");
		}
		try {
			props.load(inputStream);
			this.systemPropertiesFromClasspath = props;
			this.load(props);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(
					"GlobalConfiguration: property file '" + propFilename + "' not found in the classpath");
		}
		return this;
	}

	/**
	 * Load the property data.
	 * 
	 * @param props
	 */
	public void load(final Properties props) {

		this.systemApplicationName = props.getProperty("system.applicationName", "None").trim();
		this.systemVersion = props.getProperty("system.version", "None").trim();

		this.fileCopyLocalTargetDir = props.getProperty("file.copy.localTargetDir", "dir").trim();
		this.fileCopyLocalTargetDirBase = props.getProperty("file.copy.localTargetDir.base", "dir").trim();
		this.fileCopyTargetLogDirToSearch = props.getProperty("file.copy.targetLogDirToSearch", "dir").trim();
		this.fileCopyLocalTargetDir = props.getProperty("file.copy.localTargetDir", "dir").trim();
		this.fileCopyLogDirToSearch = props.getProperty("file.copy.logDirToSearch", "logs").trim();
		this.fileCopyRegexIncludeFile = props.getProperty("file.copy.regexIncludeFile", "file").trim();

		this.userSearchTerm = props.getProperty("user.searchTerm", "Exception").trim();
		this.userHasSearch = props.getProperty("user.hasSearch", "false").trim();
		this.userDefaultOutputName = props.getProperty("user.defaultOutputName", "false").trim();
		this.sessionOutputName = props.getProperty("session.defaultOutputName", "").trim();
		this.saveOldLines = props.getProperty("search.savePreviousLines", "").trim().equalsIgnoreCase("true");
		this.scriptExpression = props.getProperty("script.session.expression", "").trim();

		// Also update working directory //
		this.workingDirectory = props.getProperty("user.workingDirectory", "workDir");
		this.useXMLPropertyFormat = "true".equalsIgnoreCase(props.getProperty("useXMLPropertyFormat", "").trim());
		this.unzipArchiveFiles = "true".equalsIgnoreCase(props.getProperty("unzipArchiveFiles", "").trim());
		this.hasHTMLFilterOutput = "true".equalsIgnoreCase(props.getProperty("hasHTMLFilterOutput", "").trim());
		this.useQuickIncubatorDir = "true".equalsIgnoreCase(props.getProperty("useQuickIncubatorDir", "").trim());
		this.hasHadoop = "true".equalsIgnoreCase(props.getProperty("has.hadoop", "").trim());
		this.deleteOldFiles = "true".equalsIgnoreCase(props.getProperty("user.deleteOldFiles", "").trim());

		this.server1 = props.getProperty("file.copy.server.1", "").trim();
		this.server2 = props.getProperty("file.copy.server.2", "").trim();
		this.server3 = props.getProperty("file.copy.server.3", "").trim();
		this.server4 = props.getProperty("file.copy.server.4", "").trim();
		this.subserver1 = props.getProperty("file.copy.sub.server.1", "").trim();
		this.subserver2 = props.getProperty("file.copy.sub.server.2", "").trim();
		this.subserver3 = props.getProperty("file.copy.sub.server.3", "").trim();
		this.subserver4 = props.getProperty("file.copy.sub.server.4", "").trim();
		this.subserver5 = props.getProperty("file.copy.sub.server.5", "").trim();
		this.subserver6 = props.getProperty("file.copy.sub.server.6", "").trim();
		this.subserver7 = props.getProperty("file.copy.sub.server.7", "").trim();
		this.subserver8 = props.getProperty("file.copy.sub.server.8", "").trim();

		this.subserver9 = props.getProperty("file.copy.sub.server.9", "").trim();
		this.subserver10 = props.getProperty("file.copy.sub.server.10", "").trim();
		this.subserver11 = props.getProperty("file.copy.sub.server.11", "").trim();
		this.subserver12 = props.getProperty("file.copy.sub.server.12", "").trim();

		final File fwork = new File(this.workingDirectory);
		final boolean res1 = fwork.mkdirs();
		if (res1) {
			LOGGER.info("Creating directory : " + fwork.getAbsolutePath());
		}
		final File fwork2 = new File(this.fileCopyLocalTargetDir);
		final boolean res2 = fwork2.mkdirs();
		if (res2) {
			LOGGER.info("Creating directory : " + fwork2.getAbsolutePath());
		} // End of the if //
	}

	/**
	 * Load property file from dynamic script/string data.
	 * 
	 * @param props
	 * @return
	 */
	public Properties loadFromDynamicScript(final Properties props) {

		if (props == null) {
			return new Properties();
		}
		this.userDynamicScriptPropertiesFromString = props;
		final String tmpSearch = props.getProperty("user.searchTerm");
		if (tmpSearch != null && tmpSearch.length() != 0) {
			this.userSearchTerm = tmpSearch;
		} // End of if //

		final String action = props.getProperty("action.command");
		if (action != null && action.trim().length() != 0) {
			this.dynamicActionCommand = action.trim();
		}
		final String saveLines = props.getProperty("search.savePreviousLines");
		if (saveLines != null) {
			this.saveOldLines = "true".equalsIgnoreCase(saveLines);
		}

		final String tmpfileCopyLocalTargetDir = props.getProperty("file.copy.localTargetDir");
		if (tmpfileCopyLocalTargetDir != null) {
			this.fileCopyLocalTargetDir = tmpfileCopyLocalTargetDir;
		}
		final String tmpfileCopyLocalTargetDirBase = props.getProperty("file.copy.localTargetDir.base");
		if (tmpfileCopyLocalTargetDirBase != null) {
			this.fileCopyLocalTargetDirBase = tmpfileCopyLocalTargetDirBase;
		}
		final String tmpfileCopyTargetLogDirToSearch = props.getProperty("file.copy.targetLogDirToSearch");
		if (tmpfileCopyTargetLogDirToSearch != null) {
			this.fileCopyTargetLogDirToSearch = tmpfileCopyTargetLogDirToSearch;
		}

		final String tmpfileCopyLogDirToSearch = props.getProperty("file.copy.logDirToSearch");
		if (tmpfileCopyLogDirToSearch != null) {
			this.fileCopyLogDirToSearch = tmpfileCopyLogDirToSearch;
		}

		final String tmpfileCopyRegexIncludeFile = props.getProperty("file.copy.regexIncludeFile");
		if (tmpfileCopyRegexIncludeFile != null) {
			this.fileCopyRegexIncludeFile = tmpfileCopyRegexIncludeFile;
		}

		final String tmpuserHasSearch = props.getProperty("user.hasSearch");
		if (tmpuserHasSearch != null) {
			this.userHasSearch = tmpuserHasSearch;
		}

		final String tmpScript = props.getProperty("script.session.expression");
		if (tmpScript != null) {
			this.scriptExpression = tmpScript.trim();
		}

		final String tmpuserDefaultOutputName = props.getProperty("user.defaultOutputName");
		if (tmpuserDefaultOutputName != null) {
			this.userDefaultOutputName = tmpuserDefaultOutputName;
		}

		final String tmpsessionOutputName = props.getProperty("session.defaultOutputName");
		if (tmpsessionOutputName != null) {
			this.sessionOutputName = tmpsessionOutputName;
		}

		if (props.getProperty("useXMLPropertyFormat") != null) {
			this.useXMLPropertyFormat = "true".equalsIgnoreCase(props.getProperty("useXMLPropertyFormat", "").trim());
		}
		if (props.getProperty("unzipArchiveFiles") != null) {
			this.unzipArchiveFiles = "true".equalsIgnoreCase(props.getProperty("unzipArchiveFiles", "").trim());
		}

		if (props.getProperty("hasHTMLFilterOutput") != null) {
			this.hasHTMLFilterOutput = "true".equalsIgnoreCase(props.getProperty("hasHTMLFilterOutput", "").trim());
		}
		if (props.getProperty("useQuickIncubatorDir") != null) {
			this.useQuickIncubatorDir = "true".equalsIgnoreCase(props.getProperty("useQuickIncubatorDir", "").trim());
		}
		if (props.getProperty("has.hadoop") != null) {
			this.hasHadoop = "true".equalsIgnoreCase(props.getProperty("has.hadoop", "").trim());
		}

		if (props.getProperty("user.deleteOldFiles") != null) {
			this.deleteOldFiles = "true".equalsIgnoreCase(props.getProperty("user.deleteOldFiles", "").trim());
		}

		final String tmpworkingDirectory = props.getProperty("user.workingDirectory");
		if (tmpworkingDirectory != null) {
			this.workingDirectory = tmpworkingDirectory;

			final File fwork = new File(this.workingDirectory);
			final boolean res1 = fwork.mkdirs();
			if (res1) {
				LOGGER.info("Creating directory : " + fwork.getAbsolutePath());
			}
			final File fwork2 = new File(this.fileCopyLocalTargetDir);
			final boolean res2 = fwork2.mkdirs();
			if (res2) {
				LOGGER.info("Creating directory : " + fwork2.getAbsolutePath());
			}

		}
		if (props.getProperty("file.copy.server.1") != null) {
			this.server1 = props.getProperty("file.copy.server.1").trim();
		}
		if (props.getProperty("file.copy.server.2") != null) {
			this.server2 = props.getProperty("file.copy.server.2").trim();
		}
		if (props.getProperty("file.copy.server.3") != null) {
			this.server3 = props.getProperty("file.copy.server.3").trim();
		}
		if (props.getProperty("file.copy.server.4") != null) {
			this.server4 = props.getProperty("file.copy.server.4").trim();
		}
		if (props.getProperty("file.copy.sub.server.1") != null) {
			this.subserver1 = props.getProperty("file.copy.sub.server.1").trim();
		}
		if (props.getProperty("file.copy.sub.server.2") != null) {
			this.subserver2 = props.getProperty("file.copy.sub.server.2").trim();
		}
		if (props.getProperty("file.copy.sub.server.3") != null) {
			this.subserver3 = props.getProperty("file.copy.sub.server.3").trim();
		}
		if (props.getProperty("file.copy.sub.server.4") != null) {
			this.subserver4 = props.getProperty("file.copy.sub.server.4").trim();
		}
		if (props.getProperty("file.copy.sub.server.5") != null) {
			this.subserver5 = props.getProperty("file.copy.sub.server.5").trim();
		}
		if (props.getProperty("file.copy.sub.server.6") != null) {
			this.subserver6 = props.getProperty("file.copy.sub.server.6").trim();
		}
		if (props.getProperty("file.copy.sub.server.7") != null) {
			this.subserver7 = props.getProperty("file.copy.sub.server.7").trim();
		}
		if (props.getProperty("file.copy.sub.server.8") != null) {
			this.subserver8 = props.getProperty("file.copy.sub.server.8").trim();
		}

		if (props.getProperty("file.copy.sub.server.9") != null) {
			this.subserver8 = props.getProperty("file.copy.sub.server.9").trim();
		}
		if (props.getProperty("file.copy.sub.server.10") != null) {
			this.subserver8 = props.getProperty("file.copy.sub.server.10").trim();
		}
		if (props.getProperty("file.copy.sub.server.11") != null) {
			this.subserver8 = props.getProperty("file.copy.sub.server.11").trim();
		}
		if (props.getProperty("file.copy.sub.server.12") != null) {
			this.subserver8 = props.getProperty("file.copy.sub.server.12").trim();
		}

		return props;
	}

	public String toString() {
		return "[GlobalConfiguration : appName=" + this.systemApplicationName + ", vers=" + systemVersion + " ]";
	}

	/**
	 * @return the systemPropertiesFromClasspath
	 */
	public Properties getSystemPropertiesFromClasspath() {
		return systemPropertiesFromClasspath;
	}

	/**
	 * @return the systemApplicationName
	 */
	public String getSystemApplicationName() {
		return systemApplicationName;
	}

	/**
	 * @return the systemVersion
	 */
	public String getSystemVersion() {
		return systemVersion;
	}

	/**
	 * @return the fileCopyLocalTargetDir
	 */
	public String getFileCopyLocalTargetDir() {
		return fileCopyLocalTargetDir;
	}

	/**
	 * @return the fileCopyTargetLogDirToSearch
	 */
	public String getFileCopyTargetLogDirToSearch() {
		return fileCopyTargetLogDirToSearch;
	}

	/**
	 * @return the fileCopyLogDirToSearch
	 */
	public String getFileCopyLogDirToSearch() {
		return fileCopyLogDirToSearch;
	}

	/**
	 * @return the fileCopyRegexIncludeFile
	 */
	public String getFileCopyRegexIncludeFile() {
		return fileCopyRegexIncludeFile;
	}

	/**
	 * @return the workingDirectory
	 */
	public String getWorkingDirectory() {
		return workingDirectory;
	}

	/**
	 * @return the userSearchTerm
	 */
	public String getUserSearchTerm() {
		return userSearchTerm;
	}

	/**
	 * @return the userHasSearch
	 */
	public String getUserHasSearch() {
		return userHasSearch;
	}

	/**
	 * @return the dynamicActionCommand
	 */
	public String getDynamicActionCommand() {
		return dynamicActionCommand;
	}

	/**
	 * @return the server1
	 */
	public String getServer1() {
		return server1;
	}

	/**
	 * @return the server2
	 */
	public String getServer2() {
		return server2;
	}

	/**
	 * @return the server3
	 */
	public String getServer3() {
		return server3;
	}

	/**
	 * @return the server4
	 */
	public String getServer4() {
		return server4;
	}

	/**
	 * @return the subserver1
	 */
	public String getSubserver1() {
		return subserver1;
	}

	/**
	 * @return the subserver2
	 */
	public String getSubserver2() {
		return subserver2;
	}

	/**
	 * @return the subserver3
	 */
	public String getSubserver3() {
		return subserver3;
	}

	/**
	 * @return the subserver4
	 */
	public String getSubserver4() {
		return subserver4;
	}

	/**
	 * @return the subserver5
	 */
	public String getSubserver5() {
		return subserver5;
	}

	/**
	 * @return the subserver6
	 */
	public String getSubserver6() {
		return subserver6;
	}

	/**
	 * @return the subserver7
	 */
	public String getSubserver7() {
		return subserver7;
	}

	/**
	 * @return the subserver8
	 */
	public String getSubserver8() {
		return subserver8;
	}

	/**
	 * @return the fileCopyLocalTargetDirBase
	 */
	public String getFileCopyLocalTargetDirBase() {
		return fileCopyLocalTargetDirBase;
	}

	/**
	 * @return the sessionOutputName
	 */
	public String getSessionOutputName() {
		return sessionOutputName;
	}

	/**
	 * @return the saveOldLines
	 */
	public boolean isSaveOldLines() {
		return saveOldLines;
	}

	/**
	 * @return the userDefaultOutputName
	 */
	public String getUserDefaultOutputName() {
		return userDefaultOutputName;
	}

	public boolean isUseXMLPropertyFormat() {
		return useXMLPropertyFormat;
	}

	public boolean isUnzipArchiveFiles() {
		return unzipArchiveFiles;
	}

	public boolean isHasHTMLFilterOutput() {
		return hasHTMLFilterOutput;
	}

	/**
	 * @return the useQuickIncubatorDir
	 */
	public boolean isUseQuickIncubatorDir() {
		return useQuickIncubatorDir;
	}

	/**
	 * @return the maxNumberSesssionsInDatabase
	 */
	public int getMaxNumberSesssionsInDatabase() {
		return maxNumberSesssionsInDatabase;
	}

	/**
	 * @return the maxNumberSesssionsForClip
	 */
	public int getMaxNumberSesssionsForClip() {
		return maxNumberSesssionsForClip;
	}

	/**
	 * @return the scriptExpression
	 */
	public String getScriptExpression() {
		return scriptExpression;
	}

	/**
	 * @return the deleteOldFiles
	 */
	public boolean isDeleteOldFiles() {
		return deleteOldFiles;
	}

	/**
	 * @return the hasHadoop
	 */
	public boolean isHasHadoop() {
		return hasHadoop;
	}

	/**
	 * @return the userDynamicScriptPropertiesFromString
	 */
	public Properties getUserDynamicScriptPropertiesFromString() {
		return userDynamicScriptPropertiesFromString;
	}

	/**
	 * @return the subserver9
	 */
	public String getSubserver9() {
		return subserver9;
	}

	/**
	 * @return the subserver10
	 */
	public String getSubserver10() {
		return subserver10;
	}

	/**
	 * @return the subserver11
	 */
	public String getSubserver11() {
		return subserver11;
	}

	/**
	 * @return the subserver12
	 */
	public String getSubserver12() {
		return subserver12;
	}

} // End of the Class //
