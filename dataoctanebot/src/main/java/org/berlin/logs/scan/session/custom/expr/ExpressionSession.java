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
package org.berlin.logs.scan.session.custom.expr;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.berlin.logs.scan.GlobalConfiguration;
import org.berlin.logs.scan.functional.Functions;
import org.berlin.logs.scan.functional.IHandler;
import org.berlin.logs.scan.functional.SortedMap;
import org.berlin.logs.scan.session.custom.SessionInfo;

/**
 * Expression session parser.
 */
public class ExpressionSession implements IExpressionParser {

	public static final String TOKEN_GREATER_THAN = ">";
	public static final String TOKEN_LESS_THAN = "<";
	public static final String TOKEN_EQUALS = "=";
	public static final String TOKEN_IDENTIFY = "id";

	public static final String TOKEN_STR_EQUALS = "equals";

	private final Map<Integer, SessionInfo> sessionDatabase = new LinkedHashMap<Integer, SessionInfo>();

	private Collection<SessionInfo> lastSessionDatabaseList = null;
	private String lastScript = "";

	private double lastValueTypeDouble = 0;
	private Object lastValueObject = "";
	private String lastFieldParameter = "";

	private final GlobalConfiguration globalConf;

	/**
	 * Constructor for expression session.
	 * 
	 * @param globalConf
	 */
	public ExpressionSession(final GlobalConfiguration globalConf) {
		this.globalConf = globalConf;
	}

	/**
	 * Parse the script.
	 */
	public Collection<SessionInfo> parseScript(final String expression) throws Exception {
		if (expression == null || expression.length() == 0) {
			throw new IllegalStateException("Parser: invalid input, empty");
		}
		final String[] scriptArgs = expression.trim().split("\\s+");
		if (scriptArgs.length != 3) {
			throw new IllegalStateException(
					"Parser: invalid number of arguments for script, usage: <parameter | linesData, numberRequestsInSess ... > <operation> <value>");
		}
		final String tokenParameter = scriptArgs[0].trim();
		final String tokenOperation = scriptArgs[1].trim();
		final String tokenValue = scriptArgs[2].trim();
		this.lastScript = expression.trim();
		return this.parseScript(this.lastScript, tokenParameter, tokenOperation, tokenValue);
	}

	/**
	 * Parse the script.
	 */
	public Collection<SessionInfo> parseScript(final String expression, final String tokenParameter,
			final String tokenOperation, final String tokenValue) throws Exception {
		final Collection<SessionInfo> listOfData = this.run();
		if (listOfData != null) {
			System.out.println("After session parseScript - size of session info on load : " + listOfData.size());
		}
		if (TOKEN_GREATER_THAN.equals(tokenOperation)) {
			final double val = Double.parseDouble(tokenValue);
			this.lastValueTypeDouble = val;
			this.lastFieldParameter = tokenParameter;
			return Functions.filter(listOfData, this.$newFuncGreaterThan());

		} else if (TOKEN_LESS_THAN.equals(tokenOperation)) {
			final double val = Double.parseDouble(tokenValue);
			this.lastValueTypeDouble = val;
			this.lastFieldParameter = tokenParameter;
			return Functions.filter(listOfData, this.$newFuncLessThan());

		} else if (TOKEN_EQUALS.equals(tokenOperation)) {
			final double val = Double.parseDouble(tokenValue);
			this.lastValueTypeDouble = val;
			this.lastFieldParameter = tokenParameter;
			return Functions.filter(listOfData, this.$newFuncEquals());

		} else if (TOKEN_STR_EQUALS.equalsIgnoreCase(tokenOperation)) {

			this.lastValueTypeDouble = 0;
			this.lastFieldParameter = tokenParameter;
			this.lastValueObject = tokenValue.trim();
			return Functions.filter(listOfData, this.$newFuncStringEquals());

		} else if (TOKEN_IDENTIFY.equalsIgnoreCase(tokenOperation) || "void".equalsIgnoreCase(tokenOperation)) {

			this.lastValueTypeDouble = 0;
			this.lastFieldParameter = tokenParameter;
			this.lastValueObject = tokenValue.trim();
			return listOfData;

		} else {
			throw new IllegalStateException("Invalid operation, the input operation was not found, expr=" + expression);
		} // End of if - token check //
	}

	/**
	 * Run against all properties.
	 */
	public Collection<SessionInfo> run() throws Exception {

		final File f = new File(this.globalConf.getWorkingDirectory() + "\\session_database.properties");
		final Properties props = new Properties();
		props.loadFromXML(new FileInputStream(f));
		final String patternSessionKey = "^sess\\.(\\d+)\\.(\\w+)$";
		final Pattern pKey = Pattern.compile(patternSessionKey);
		final List res = SortedMap.sort(props.keySet(), new Comparator<Object>() {
			@Override
			public int compare(final Object o1, final Object o2) {
				if (o1 == null) {
					return 1;
				}
				if (o2 == null) {
					return -1;
				}
				final String a = (String) o1;
				final String b = (String) o2;
				final Matcher m = pKey.matcher(a);
				final Matcher m2 = pKey.matcher(b);
				String c = a;
				String d = b;
				Integer x = null;
				Integer y = null;
				try {
					while (m.find()) {
						c = m.group(1);
						x = Integer.parseInt(c);
					} // End of the //
					while (m2.find()) {
						d = m2.group(1);
						y = Integer.parseInt(d);
					} // End of the //
				} catch (NumberFormatException ne) {

				}
				if (x == null || y == null) {
					return c.compareTo(d);
				} else {
					return x.compareTo(y);
				}
			}
		});
		int i = 0;
		for (final Object o : res) {
			final String key = (String) o;
			final Matcher m = pKey.matcher(key);
			while (m.find()) {
				try {
					final SessionInfo inf = this.onProperty(key, String.valueOf(props.get(o)), m.group(1), m.group(2));
				} catch (final Exception e) {
					e.printStackTrace();
				}
			} // End of the //
			i++;
		} // End of the For //
		this.lastSessionDatabaseList = this.sessionDatabase.values();
		return this.lastSessionDatabaseList;
	}

	protected SessionInfo onProperty(final String key, final String val, final String idx, final String prop) {
		final Integer id = Integer.parseInt(idx);
		final SessionInfo info = sessionDatabase.get(id);
		if (info == null) {
			final SessionInfo newinfo = new SessionInfo();
			newinfo.loadSessionInfo(key, val, idx, prop);
			sessionDatabase.put(id, newinfo);
		} else {
			info.loadSessionInfo(key, val, idx, prop);
		} // End of the if - else //
		return info;
	}

	/**
	 * Function for greater than.
	 */
	public IHandler<SessionInfo, Boolean> $newFuncGreaterThan() {
		return new IHandler<SessionInfo, Boolean>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Boolean execute(final SessionInfo input) {
				if (input == null) {
					return false;
				}
				if (input.pullValue(lastFieldParameter) > lastValueTypeDouble) {
					return true;
				}
				return false;
			}
		};
	}

	/**
	 * Function for less than.
	 */
	public IHandler<SessionInfo, Boolean> $newFuncLessThan() {
		return new IHandler<SessionInfo, Boolean>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Boolean execute(final SessionInfo input) {
				if (input == null) {
					return false;
				}
				if (input.pullValue(lastFieldParameter) < lastValueTypeDouble) {
					return true;
				}
				return false;
			}
		};
	}

	/**
	 * Function for equals.
	 */
	public IHandler<SessionInfo, Boolean> $newFuncEquals() {
		return new IHandler<SessionInfo, Boolean>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Boolean execute(final SessionInfo input) {
				if (input == null) {
					return false;
				}
				if (input.pullValue(lastFieldParameter).equals(lastValueTypeDouble)) {
					return true;
				}
				return false;
			}
		};
	}

	/**
	 * Function for equals.
	 */
	public IHandler<SessionInfo, Boolean> $newFuncStringEquals() {
		return new IHandler<SessionInfo, Boolean>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Boolean execute(final SessionInfo input) {
				if (input == null) {
					return false;
				}
				if (input.pullValue(lastFieldParameter, "none").equalsIgnoreCase(String.valueOf(lastValueObject))) {
					return true;
				}
				return false;
			}
		};
	}

} // End of the Class //
