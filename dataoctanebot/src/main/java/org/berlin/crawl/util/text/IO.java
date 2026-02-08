/* Copyright (c) 2013 Berlin Brown (berlin2research.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.berlin.crawl.util.text;

/*
 * Octane crawler is a simple web crawler in Java.
 * Simplest, proof of concept web crawler.
 * Crawling a URL is simple, request against the URL and download the content 
 * then parse the data and add any valid URLs to the link processing queue.
 * 
 * http://code.google.com/p/octane-crawler/
 * http://berlin2research.com/
 * 
 * Author: Berlin Brown (berlin dot brown at gmail.com)
 * 
 * Libraries used:
 * ---------------- 
 * dom4j-1.6.1.jar, hibernate-core-4.0.1.Final.jar, hsqldb-1.8.0.10.jar, httpclient-4.2.3.jar, jackson-core-asl-1.9.12.jar, 
 * log4j-1.2.16.jar, mysql-connector-java-5.1.23.jar, opennlp-maxent-3.0.2-incubating.jar
 * opennlp-tools-1.5.2-incubating.jar, spring-core-3.1.1.RELEASE.jar, spring-web-3.1.1.RELEASE.jar, 
 * struts-core-1.3.10.jar, tagsoup-1.2.1.jar, tika-core-1.3.jar
 */

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Simple functional programming algebras
 * 
 * @param <A>
 */
public final class IO<A> {

	public static interface Fx2<Bx> {
		Bx f();
	}
	public static interface Fx<Ax> {
		void $(final Ax o, final int idx);
	}
	public static interface Fx3<Ax, Bx> {
		void $(final Ax o, final Bx u, final int idx);
	}
	public final void foreach(final Iterable<A> lst, final Fx<A> f) {
		int i = 0;
		for (final A o : lst) {
			f.$(o, i++);

		}
	}

	public final void w(final String filename, final Fx<PrintWriter> f) {
		this.withOpenWriteFile(filename, f);
	}

	/**
	 * Open file macro.
	 */
	public final void withOpenFile(final String filename, final Fx<String> f) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(filename));
			final BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			String data = "";
			do {
				int i = 0;
				data = reader.readLine();
				if (data != null) {
					f.$(data, i++);
				}
			} while (data != null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		} // End of the try - catch finally //
	} // End of the function //

	/**
	 * Open file macro.
	 */
	public final void withOpenWriteFile(final String filename, final Fx<PrintWriter> f) {
		PrintWriter out = null;
		try {
			final String outputFilename = filename;
			final BufferedOutputStream bos1 = new BufferedOutputStream(new FileOutputStream(outputFilename));
			out = new PrintWriter(bos1);
			f.$(out, 0);
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		} // End of the try - catch //
	} // End of the function //

	/**
	 * Open file macro.
	 */
	public final void withFiles(final String filename1, final String filename2, final Fx3<String, PrintWriter> f) {
		PrintWriter out = null;
		FileInputStream fis = null;
		try {
			final String outputFilename = filename2;
			final BufferedOutputStream bos1 = new BufferedOutputStream(new FileOutputStream(outputFilename));
			out = new PrintWriter(bos1);

			fis = new FileInputStream(new File(filename1));
			final BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			String data = "";
			do {
				int i = 0;
				data = reader.readLine();
				if (data != null) {
					f.$(data, out, i++);
				}
			} while (data != null);
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} // End of the try - catch //
	} // End of the function //

	public final void programAspect(final String msg, final Fx<String> f) {
		final long start = System.currentTimeMillis();
		System.out.println("* {START} " + msg);
		f.$("", 0);
		final long end = System.currentTimeMillis();
		System.out.println("* {END} " + msg + " - procTime=" + (end - start));
	}

} // End of the algebra //