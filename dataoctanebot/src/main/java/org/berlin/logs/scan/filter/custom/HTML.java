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
package org.berlin.logs.scan.filter.custom;

import java.util.Hashtable;

public class HTML {

	Hashtable<String, String> sessionBgColor = new Hashtable<String, String>();
	Hashtable<String, String> agentBgColor = new Hashtable<String, String>();

	public HTML() {

		sessionBgColor.put("1", "#ffbbee");
		sessionBgColor.put("2", "#bbffaa");
		sessionBgColor.put("3", "#ffaaee");
		sessionBgColor.put("4", "#ffaadd");
		sessionBgColor.put("5", "#fff");
		sessionBgColor.put("6", "#ffffaa");
		sessionBgColor.put("7", "#ffffbb");
		sessionBgColor.put("8", "#fff");
		sessionBgColor.put("9", "#ffee88");
		sessionBgColor.put("10", "#ffee88");
		sessionBgColor.put("11", "#fff");
		sessionBgColor.put("12", "#ffee88");
		sessionBgColor.put("13", "#bbddff");

		agentBgColor.put("1", "#ff88dd");
		agentBgColor.put("2", "#ffff88");
		agentBgColor.put("3", "#ff77dd");
		agentBgColor.put("4", "#ee77ff");
		agentBgColor.put("5", "#ffaaff");
		agentBgColor.put("6", "#ffaaff");
		agentBgColor.put("7", "#ffccff");
		agentBgColor.put("8", "#ff77dd");

	}

} // End of the class //
