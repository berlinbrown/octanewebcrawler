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

package org.berlin.logs.scan.functional;

import java.io.Serializable;

/**
 * Basic pair, tuple type for storing multiple values. This class has an
 * advantage over arrays because you can hold different types of objects without
 * losing type safety.
 * 
 * Pair Usage:
 * 
 * <pre>
 * Pair<String, String> p = new Pair<String, String>("john", "A");
 * Pair<String, Integer> z = new Pair<String, Integer>("adam", 100);
 * </pre>
 * 
 * @param <T>
 * @param <S>
 */
public class Pair<T, S> implements Serializable {

	/**
	 * serial version id.
	 */
	private static final long serialVersionUID = -2202117371650541073L;

	private final T first;
	private final S second;

	/**
	 * Constructor for Pair.
	 * 
	 * @param f
	 *            T
	 * @param s
	 *            S
	 */
	public Pair(T f, S s) {
		first = f;
		second = s;
	}

	/**
	 * Method getFirst.
	 * 
	 * @return T
	 */
	public T getFirst() {
		return first;
	}

	/**
	 * Method getSecond.
	 * 
	 * @return S
	 */
	public S getSecond() {
		return second;
	}

	/**
	 * Method toString.
	 * 
	 * @return String
	 */
	public String toString() {
		return "(" + ((first == null) ? "" : first.toString()) + ", " + ((second == null) ? "" : second.toString())
				+ ")";
	}

	/**
	 * usage: --------
	 * 
	 * <pre>
	 * Pair&lt;String, String&gt; Pair = dup(&quot;Testing&quot;);
	 * </pre>
	 * 
	 * @param value
	 *            T
	 * @return Pair<T,T>
	 */
	public static <T> Pair<T, T> dup(T value) {
		return new Pair<T, T>(value, value);
	}

} // End of Class //
